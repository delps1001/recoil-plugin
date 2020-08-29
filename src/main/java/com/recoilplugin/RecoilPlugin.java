package com.recoilplugin;

import com.google.inject.Provides;
import com.recoilplugin.overlays.RecoilOverlay;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(name = "Ring of Recoil Notifier")
public class RecoilPlugin extends Plugin {

  @Inject private Client client;

  @Inject private RecoilConfig config;

  @Inject private RecoilOverlay recoilOverlay;

  @Inject private OverlayManager overlayManager;

  private boolean recoilPresent = true;

  @Override
  protected void startUp() {
    log.info("Recoil Plugin started!");
    overlayManager.add(recoilOverlay);
  }

  @Override
  protected void shutDown() {
    log.info("Recoil plugin stopped!");
    overlayManager.remove(recoilOverlay);
  }

  @Subscribe
  public void onGameTick(GameTick event) {
    final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
    if (Optional.ofNullable(equipment).isPresent()) {
      this.recoilPresent = !equipment.contains(2550);
    }
  }

  @Provides
  RecoilConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(RecoilConfig.class);
  }

  public boolean isRecoilPresent() {
    return recoilPresent;
  }

  private void setRecoilPresent(boolean recoilPresent) {
    this.recoilPresent = recoilPresent;
  }
}
