/*Nomnom
* Copyright (c) 2017, Devin French <https://github.com/devinfrench>
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
*    and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
nomnom */
package com.recoilplugin.overlays;

import com.recoilplugin.RecoilConfig;
import com.recoilplugin.RecoilPlugin;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class RecoilOverlay extends Overlay {

  private static final ScaledImage previouslyScaledImage = new ScaledImage();
  private static BufferedImage recoilImage;
  private final RecoilPlugin plugin;
  private final RecoilConfig recoilConfig;

  @Inject
  RecoilOverlay(RecoilPlugin plugin, RecoilConfig config) throws PluginInstantiationException {
    super(plugin);
    setPriority(OverlayPriority.MED);
    setPosition(OverlayPosition.BOTTOM_LEFT);
    setLayer(OverlayLayer.ALWAYS_ON_TOP);
    this.plugin = plugin;
    this.recoilConfig = config;
    loadRecoilImage();
    previouslyScaledImage.scale = config.scale();
    previouslyScaledImage.scaledBufferedImage = recoilImage;
  }

  private static void loadRecoilImage() {
    recoilImage = ImageUtil.getResourceStreamFromClass(RecoilPlugin.class, "/ring_of_recoil.png");
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!plugin.isRecoilPresent()) {
      return null;
    }

    BufferedImage scaledRecoilImage = scaleImage(recoilImage);
    ImageComponent imagePanelComponent = new ImageComponent(scaledRecoilImage);
    return imagePanelComponent.render(graphics);
  }

  private BufferedImage scaleImage(BufferedImage recoilImage) {
    if (previouslyScaledImage.scale == recoilConfig.scale() || recoilConfig.scale() <= 0) {
      return previouslyScaledImage.scaledBufferedImage;
    }
    log.debug("Rescaling image!");
    int w = recoilImage.getWidth();
    int h = recoilImage.getHeight();
    BufferedImage scaledRecoilImage =
        new BufferedImage(
            recoilConfig.scale() * w, recoilConfig.scale() * h, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(recoilConfig.scale(), recoilConfig.scale());
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    scaledRecoilImage = scaleOp.filter(recoilImage, scaledRecoilImage);
    previouslyScaledImage.scaledBufferedImage = scaledRecoilImage;
    previouslyScaledImage.scale = recoilConfig.scale();
    return scaledRecoilImage;
  }

  private static class ScaledImage {
    private int scale;
    private BufferedImage scaledBufferedImage;
  }
}
