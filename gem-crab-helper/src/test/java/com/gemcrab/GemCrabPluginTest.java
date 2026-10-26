package com.gemcrab;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

// Run this to start RuneLite with the plugin loaded, for trying it out locally
public class GemCrabPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GemCrabPlugin.class);
		RuneLite.main(args);
	}
}
