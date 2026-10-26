package com.gemcrab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

class GemCrabNpcOverlay extends Overlay
{
	private final GemCrabPlugin plugin;
	private final GemCrabConfig config;
	private final ModelOutlineRenderer outlineRenderer;

	@Inject
	GemCrabNpcOverlay(GemCrabPlugin plugin, GemCrabConfig config, ModelOutlineRenderer outlineRenderer)
	{
		this.plugin = plugin;
		this.config = config;
		this.outlineRenderer = outlineRenderer;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		NPC crab = plugin.getCrab();
		if (crab == null)
		{
			return null;
		}

		if (config.highlight())
		{
			outlineRenderer.drawOutline(crab, 2, config.highlightColor(), 4);
		}

		int ratio = crab.getHealthRatio();
		int scale = crab.getHealthScale();
		if (config.showHp() && ratio >= 0 && scale > 0)
		{
			String text = Math.round(100.0 * ratio / scale) + "%";
			Point loc = crab.getCanvasTextLocation(graphics, text, crab.getLogicalHeight() + 40);
			if (loc != null)
			{
				OverlayUtil.renderTextLocation(graphics, loc, text, Color.WHITE);
			}
		}
		return null;
	}
}
