package org.example.theme.item.items;

import org.example.theme.ExamplePlugin;
import org.example.theme.Panel;
import org.example.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.setting.BooleanSetting;

import java.awt.*;

public class BooleanItem extends ExtendableItem {
    public BooleanItem(IModule module, Panel panel, ExtendableItem parent, BooleanSetting setting) {
        super(parent, module, panel, setting);
        this.panel = panel;
        this.module = module;
        this.setting = setting;
        open = false;
    }
    Panel panel;
    IModule module;
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
                ExamplePlugin.theme.outlineWidth.getValue(),
                setting.getValue()
                ? ExamplePlugin.theme.getColorSetting().getValue().getRGB()
                : Theme.changeAlpha(ExamplePlugin.theme.getColorSetting().getValue().getRGB(), 100),
                ExamplePlugin.theme.outlineColor.getValueRGB());

        if(!subItems.isEmpty()) {
            renderer.drawOutlinedRectangle(
                    getX() + (getWidth() - 14) + 1,
                    getY(),
                    13,
                    getHeight(),
                    ExamplePlugin.theme.outlineWidth.getValue(),
                    open
                    ? ExamplePlugin.theme.getColorSetting().getValue().getRGB()
                    : Theme.changeAlpha(ExamplePlugin.theme.getColorSetting().getValue().getRGB(), 100),
                    ExamplePlugin.theme.outlineColor.getValueRGB());
        }


        if(panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())) {
            renderer.drawRectangle(getX(), getY(), getWidth(), getHeight(), new Color(0, 0, 0, 70).getRGB());
        }
      if(subItems.isEmpty())  drawText(setting.getDisplayName());
      else drawTextEx(setting.getDisplayName());
      renderSubItems(context, mouseX, mouseY, subItems, open);
    }
    @Override
    public double getX() {
        return parent.getX() + 1.5;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            setting.setValue(!setting.getValue());
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && !subItems.isEmpty() && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
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
