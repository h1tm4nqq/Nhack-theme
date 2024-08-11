package dev.sf.theme;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.core.logging.ILogger;

import java.awt.*;

/**
 * Example rusherhack plugin
 *
 * @author xyzbtw and hitmanqq
 */
public class NhackPlugin extends Plugin {
	public static Theme theme = new Theme("nhack", "nhack theme", new Color(60, 200, 200, 255));
	public static ClickGUIHandler handler;
	public static ILogger logger;
	
	@Override
	public void onLoad() {
		logger = this.getLogger();
		handler = new ClickGUIHandler(true);
		RusherHackAPI.getThemeManager().registerTheme(theme);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("nhack plugin unloaded!");
	}




	
}