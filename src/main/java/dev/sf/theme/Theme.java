package dev.sf.theme;

import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.client.api.ui.theme.ThemeBase;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;

public class Theme extends ThemeBase {



    public NumberSetting<Integer> alpha = new NumberSetting<>("Alpha", 128, 0, 255);
    public ColorSetting fontColor = new ColorSetting("FontColor", new Color(255, 255, 255));
    public BooleanSetting forceVanilla = new BooleanSetting("ForceVanilla", true);
    public ColorSetting categoryColor = new ColorSetting("CategoryColor", new Color(60, 200, 200, 128));
    public ColorSetting panelColor = new ColorSetting("PanelColor", new Color(0, 0, 0, 150));

    public BooleanSetting settingsOutline = new BooleanSetting("SettingsOutline", false);
    public NumberSetting<Float> outlineWidth = new NumberSetting<>("OutlineWidth", 3F, 0.1F, 5F);
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
        getColorSetting().setAlphaAllowed(false);
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
    }

    @Override
    public @Nullable PanelHandlerBase<?> getClickGuiHandler() {
        return NhackPlugin.handler;
    }



    public static int changeAlpha(int color, int alpha) {
        Color c = new Color(color);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

}
