package com.recoilplugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RecoilPluginTest {

  public static void main(String[] args) throws Exception {
    ExternalPluginManager.loadBuiltin(RecoilPlugin.class);
    RuneLite.main(args);
  }
}
