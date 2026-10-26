package com.gemcrab;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

class GemCrabSessionOverlay extends OverlayPanel
{
	private final GemCrabPlugin plugin;
	private final GemCrabConfig config;

	@Inject
	GemCrabSessionOverlay(GemCrabPlugin plugin, GemCrabConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showSession())
		{
			return null;
		}

		Duration time = plugin.getSessionTime();
		int xp = plugin.getCombatXpGained();
		long seconds = Math.max(1, time.getSeconds());
		long xpPerHour = xp * 3600L / seconds;

		panelComponent.getChildren().add(TitleComponent.builder().text("Gem Crab").build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Crabs").right(String.valueOf(plugin.getCrabsDone())).build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Time").right(String.format("%d:%02d", time.toHours(), time.toMinutesPart())).build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Combat XP").right(QuantityFormatter.quantityToStackSize(xp)).build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("XP/hr").right(QuantityFormatter.quantityToStackSize(xpPerHour)).build());
		return super.render(graphics);
	}
}
