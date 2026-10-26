package com.gemcrab;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Gem Crab Helper",
	description = "Highlights the Gemstone Crab, shows its HP, and notifies you when it moves or you go idle",
	tags = {"gem", "crab", "gemstone", "afk", "combat", "notifier"}
)
public class GemCrabPlugin extends Plugin
{
	private static final Skill[] COMBAT_SKILLS = {
		Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC, Skill.HITPOINTS
	};

	@Inject
	private Client client;

	@Inject
	private GemCrabConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GemCrabNpcOverlay npcOverlay;

	@Inject
	private GemCrabSessionOverlay sessionOverlay;

	private NPC crab;
	// true once you've attacked the current crab, so walking past one doesn't trigger notifications
	private boolean fighting;
	private int idleTicks;
	private boolean idleNotified;

	private int crabsDone;
	private Instant sessionStart;
	private final Map<Skill, Integer> startXp = new EnumMap<>(Skill.class);

	@Provides
	GemCrabConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GemCrabConfig.class);
	}

	@Override
	protected void startUp()
	{
		crab = null;
		fighting = false;
		crabsDone = 0;
		sessionStart = Instant.now();
		startXp.clear();
		overlayManager.add(npcOverlay);
		overlayManager.add(sessionOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(npcOverlay);
		overlayManager.remove(sessionOverlay);
		crab = null;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (!isCrab(npc))
		{
			return;
		}
		crab = npc;
		fighting = false;
		idleTicks = 0;
		idleNotified = false;
		if (config.notifySpawn())
		{
			notifier.notify("Gemstone Crab has appeared");
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc() != crab)
		{
			return;
		}
		if (fighting)
		{
			crabsDone++;
			if (config.notifyGone())
			{
				notifier.notify("Gemstone Crab is gone - head to the next spot");
			}
		}
		crab = null;
		fighting = false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (crab == null || player == null)
		{
			return;
		}

		if (player.getInteracting() == crab)
		{
			fighting = true;
			idleTicks = 0;
			idleNotified = false;
			return;
		}

		if (!fighting || idleNotified || !config.notifyIdle())
		{
			return;
		}

		idleTicks++;
		// a game tick is 0.6 seconds
		if (idleTicks * 600 >= config.idleSeconds() * 1000)
		{
			notifier.notify("You stopped attacking the Gemstone Crab");
			idleNotified = true;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		// the first update after login carries the current total, which becomes the session baseline
		startXp.putIfAbsent(event.getSkill(), event.getXp());
	}

	private boolean isCrab(NPC npc)
	{
		String name = npc.getName();
		return name != null && name.equalsIgnoreCase(config.crabName().trim());
	}

	NPC getCrab()
	{
		return crab;
	}

	int getCrabsDone()
	{
		return crabsDone;
	}

	Duration getSessionTime()
	{
		return Duration.between(sessionStart, Instant.now());
	}

	int getCombatXpGained()
	{
		int gained = 0;
		for (Skill skill : COMBAT_SKILLS)
		{
			Integer start = startXp.get(skill);
			if (start != null)
			{
				gained += client.getSkillExperience(skill) - start;
			}
		}
		return gained;
	}
}
