package dev.sf.theme;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

import java.awt.*;

/**
 * Example rusherhack plugin
 *
 * @author xyzbtw and hitmanqq
 */
public class NhackPlugin extends Plugin {
	public static Theme theme = new Theme("Nhack", "Nhack theme", new Color(148, 141, 252));
	public static ClickGUIHandler handler;
	
	@Override
	public void onLoad() {
		handler = new ClickGUIHandler(true);
		RusherHackAPI.getThemeManager().registerTheme(theme);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Nhack plugin unloaded!");
	}




	
}