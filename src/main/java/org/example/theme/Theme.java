package org.example.theme;

import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.client.api.ui.theme.ThemeBase;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;

public class Theme extends ThemeBase {
    public ColorSetting fontColor = new ColorSetting("FontColor", new Color(255, 255, 255));
    public ColorSetting categoryColor = new ColorSetting("CategoryColor", new Color(112, 104, 255));
    public ColorSetting categoryLineColor = new ColorSetting("CategoryLineColor", new Color(112, 104, 255));
    public ColorSetting backColor = new ColorSetting("BackColor", new Color(0, 0, 0, 150));
    public ColorSetting outlineColor = new ColorSetting("OutlineColor", new Color(145, 145, 145, 100));
    public ColorSetting moduleOutlineColor = new ColorSetting("ModuleOutlineColor", new Color(145, 145, 145, 100));
    public ColorSetting backgroundColor = new ColorSetting("BackgroundColor", new Color(0, 0, 0, 100));
    public NumberSetting<Float> outlineWidth = new NumberSetting<>("OutlineWidth", 3F, 0.1F, 5F);
    public NumberSetting<Float> scrollSpeed = new NumberSetting<>("ScrollSpeed", 15F, 1F, 20F);

    public NumberSetting<Float> x = new NumberSetting<>("X", 0F, -200F, 200F);
    public NumberSetting<Float> y = new NumberSetting<>("Y", 0F, -200F, 200F);
    public NumberSetting<Float> x1 = new NumberSetting<>("X1", 0F, -200F, 200F);
    public NumberSetting<Float> y1 = new NumberSetting<>("Y1", 0F, -200F, 200F);
    public NumberSetting<Float> x2 = new NumberSetting<>("X2", 0F, -200F, 200F);
    public NumberSetting<Float> y2 = new NumberSetting<>("Y2", 0F, -200F, 200F);


    public BooleanSetting settingsOutline = new BooleanSetting("SettingsOutline", false);

    public Theme(String name, String description, Color defaultColor) {
        super(name, description, defaultColor);
        getColorSetting().setValue(new Color(112, 104, 255));
        registerSettings(
                categoryColor,
                fontColor,
                backColor,
                outlineColor,
                moduleOutlineColor,
                categoryLineColor,
                settingsOutline,
                outlineWidth,
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
        return ExamplePlugin.handler;
    }



    public static int changeAlpha(int color, int alpha) {
        Color c = new Color(color);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

}
