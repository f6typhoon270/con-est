package com.gemcrab;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("gemcrab")
public interface GemCrabConfig extends Config
{
	@ConfigItem(
		keyName = "crabName",
		name = "Crab name",
		description = "NPC name to track. Change it if the in-game name differs.",
		position = 0
	)
	default String crabName()
	{
		return "Gemstone Crab";
	}

	@ConfigItem(
		keyName = "highlight",
		name = "Highlight crab",
		description = "Draw an outline around the crab",
		position = 1
	)
	default boolean highlight()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "highlightColor",
		name = "Highlight colour",
		description = "Outline colour",
		position = 2
	)
	default Color highlightColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "showHp",
		name = "Show HP %",
		description = "Show the crab's remaining HP above it (updates while its health bar is visible)",
		position = 3
	)
	default boolean showHp()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSession",
		name = "Show session panel",
		description = "Show crabs done, time and combat XP per hour",
		position = 4
	)
	default boolean showSession()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifySpawn",
		name = "Notify on spawn",
		description = "Notify when the crab appears near you",
		position = 5
	)
	default boolean notifySpawn()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyGone",
		name = "Notify when it moves",
		description = "Notify when the crab you were fighting disappears",
		position = 6
	)
	default boolean notifyGone()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyIdle",
		name = "Notify when idle",
		description = "Notify when you stop attacking the crab while it is still there",
		position = 7
	)
	default boolean notifyIdle()
	{
		return true;
	}

	@Range(min = 1, max = 120)
	@Units(Units.SECONDS)
	@ConfigItem(
		keyName = "idleSeconds",
		name = "Idle delay",
		description = "How long you can be idle before the idle notification",
		position = 8
	)
	default int idleSeconds()
	{
		return 10;
	}
}
