package org.example.theme;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

import java.awt.*;

/**
 * Example rusherhack plugin
 *
 * @author xyzbtw and hitmanqq
 */
public class ExamplePlugin extends Plugin {
	public static Theme theme = new Theme("FirstExternal", "First external theme!", new Color(148, 141, 252));
	public static ClickGUIHandler handler;
	
	@Override
	public void onLoad() {
		handler = new ClickGUIHandler(true);
		RusherHackAPI.getThemeManager().registerTheme(theme);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example plugin unloaded!");
	}
}