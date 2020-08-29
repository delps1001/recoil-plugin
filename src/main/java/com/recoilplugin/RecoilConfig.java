package com.recoilplugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ringofrecoilnotifier")
public interface RecoilConfig extends Config {

  @ConfigItem(
      keyName = "scale",
      name = "Scale",
      description = "The scale of the ring of recoil image.")
  default int scale() {
    return 1;
  }
}
