package org.example.theme.item.items;

import org.example.theme.ExamplePlugin;
import org.example.theme.Panel;
import org.example.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.setting.Setting;

public class NullItem extends ExtendableItem {
    public NullItem(ExtendableItem parent, IModule module, Panel panel, Setting<?> settingValue) {
        super(parent, module, panel, settingValue);
        open = false;
    }


    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        renderer.drawOutlinedRectangle(
                getX(),
                getY(),
                getWidth() - 14 - 1,
                getHeight(),
                ExamplePlugin.theme.outlineWidth.getValue(),
                Theme.changeAlpha(ExamplePlugin.theme.getColorSetting().getValue().getRGB(), 100),
                ExamplePlugin.theme.outlineColor.getValueRGB());

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

        drawTextEx(setting.getDisplayName());

        renderSubItems(context, mouseX, mouseY, subItems, open);

        super.render(context, mouseX, mouseY);
    }

    @Override
    public double getX() {
        return parent.getX() + 1.5;
    }

    @Override
    public double getY() {
        return super.getY();
    }

    @Override
    public double getWidth() {
        return super.getWidth();
    }

    @Override
    public double getHeight() {
        return super.getHeight();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


}
