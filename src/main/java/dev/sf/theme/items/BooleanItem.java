package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.utils.ColorUtils;

import java.awt.*;

public class BooleanItem extends ExtendableItem {
    public BooleanItem(IFeatureConfigurable module, dev.sf.theme.Panel panel, ExtendableItem parent, BooleanSetting setting) {
        super(parent, module, panel, setting);
        this.panel = panel;
        this.module = module;
        this.setting = setting;
        open = false;
    }
    Panel panel;
    IFeatureConfigurable module;
    BooleanSetting setting;



    @Override
    public boolean isHovered(double mouseX, double mouseY, boolean includeSubItems) {
        return false;
    }


    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        IRenderer2D renderer = RusherHackAPI.getRenderer2D();

        renderer.drawOutlinedRectangle(
                getX(),
                getY(),
                subItems.isEmpty() ? getWidth() : getWidth() - 14 - 1,
                getHeight(),
                NhackPlugin.theme.outlineWidth.getValue(),
                setting.getValue()
                ? NhackPlugin.theme.getColorSetting().getValue().getRGB()
                : ColorUtils.transparency(NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.outlineColor.getValueRGB());

        if(!subItems.isEmpty()) {
            renderer.drawOutlinedRectangle(
                    getX() + (getWidth() - 14) + 1,
                    getY(),
                    13,
                    getHeight(),
                    NhackPlugin.theme.outlineWidth.getValue(),
                    open
                    ?  NhackPlugin.theme.buttonMColor.getValue().getRGB()
                    : ColorUtils.transparency( NhackPlugin.theme.buttonMColor.getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                    NhackPlugin.theme.outlineColor.getValueRGB());
        }


        if(panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            renderer.drawRectangle(getX(), getY(), getWidth(), getHeight(), new Color(0, 0, 0, 70).getRGB());
        }

      if(subItems.isEmpty())  drawText(setting.getDisplayName());
      else drawTextEx(setting.getDisplayName());
      renderSubItems(context, mouseX, mouseY, subItems, open);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            setting.setValue(!setting.getValue());
            return true;
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && !subItems.isEmpty() && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public double getY() {
        return super.getY();
    }

    @Override
    public double getHeight() {
        return super.getHeight();
    }

}
