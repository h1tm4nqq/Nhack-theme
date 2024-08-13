package dev.sf.theme;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.hud.HudElement;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.ui.hud.HudHandlerBase;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.client.api.ui.theme.ThemeBase;
import org.rusherhack.core.logging.ILogger;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;
import java.util.List;

public class Theme extends ThemeBase {
    public ILogger logger;

    public NumberSetting<Integer> alpha = new NumberSetting<>("Alpha", 128, 0, 255);
    public ColorSetting fontColor = new ColorSetting("FontColor", new Color(255, 255, 255));
    public BooleanSetting forceVanilla = new BooleanSetting("ForceVanilla", true);
    public ColorSetting categoryColor = new ColorSetting("CategoryColor", new Color(60, 200, 200, 128));
    public ColorSetting panelColor = new ColorSetting("PanelColor", new Color(0, 0, 0, 150));

    public BooleanSetting settingsOutline = new BooleanSetting("SettingsOutline", false);
    public NumberSetting<Float> outlineWidth = new NumberSetting<>("OutlineWidth", 1F, 0.1F, 5F);
    public ColorSetting outlineColor = new ColorSetting("OutlineColor", Color.WHITE);

    public ColorSetting backgroundColor = new ColorSetting("BackgroundColor", new Color(0, 0, 0, 100));

    public NumberSetting<Float> scrollSpeed = new NumberSetting<>("ScrollSpeed", 15F, 1F, 20F);

    public NumberSetting<Float> x = new NumberSetting<>("X", 0F, -200F, 200F);
    public NumberSetting<Float> y = new NumberSetting<>("Y", 0F, -200F, 200F);
    public NumberSetting<Float> x1 = new NumberSetting<>("X1", 0F, -200F, 200F);
    public NumberSetting<Float> y1 = new NumberSetting<>("Y1", 0F, -200F, 200F);
    public NumberSetting<Float> x2 = new NumberSetting<>("X2", 0F, -200F, 200F);
    public NumberSetting<Float> y2 = new NumberSetting<>("Y2", 0F, -200F, 200F);


    public Theme(String name, String description, Color defaultColor) {
        super(name, description, defaultColor);
        logger = RusherHackAPI.createLogger("nhack_theme");
        registerSettings(
                alpha,

                fontColor,
                forceVanilla,

                categoryColor,
                panelColor,

                outlineWidth,
                settingsOutline,
                outlineColor,

                backgroundColor,

                scrollSpeed,
                x,
                y,
                x1,
                y1,
                x2,
                y2
        );

        List<HudElement> elements = ImmutableList.copyOf(RusherHackAPI.getHudManager().getFeatures());
        for (HudElement element : elements) {
            registerIsOpen(element);
            registerShouldRender(element);
        }
    }


    public void registerIsOpen(HudElement element) {
        registerSettings(new BooleanSetting(element.getName().toLowerCase() + "#is_open", false).setHidden(true).setVisibility(() -> false));
    }

    public void registerShouldRender(HudElement element) {
        registerSettings(new BooleanSetting(element.getName().toLowerCase() + "#should_render", false).setHidden(true).setVisibility(() -> false));
    }

    @Override
    public @Nullable PanelHandlerBase<?> getClickGuiHandler() {
        return NhackPlugin.guiHandler;
    }

    @Override
    public @Nullable HudHandlerBase getHudHandler() {
        return NhackPlugin.hudHandler;
    }

    public static int changeAlpha(int color, int alpha) {
        Color c = new Color(color);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

}
