package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import dev.sf.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;

public class NullItem extends ExtendableItem {
    public NullItem(ExtendableItem parent, IFeatureConfigurable module, Panel panel, Setting<?> settingValue) {
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
                NhackPlugin.theme.outlineWidth.getValue(),
                ColorUtils.transparency(NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.outlineColor.getValueRGB());

        renderer.drawOutlinedRectangle(
                getX() + (getWidth() - 14) + 1,
                getY(),
                13,
                getHeight(),
                NhackPlugin.theme.outlineWidth.getValue(),
                open
                        ? NhackPlugin.theme.buttonMColor.getValue().getRGB()
                        : ColorUtils.transparency(NhackPlugin.theme.buttonMColor.getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.outlineColor.getValueRGB());

        drawTextEx(setting.getDisplayName());

        renderSubItems(context, mouseX, mouseY, subItems, open);

        super.render(context, mouseX, mouseY);
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
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


}
