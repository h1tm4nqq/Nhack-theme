package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.bind.IBindable;
import org.rusherhack.core.bind.key.IKey;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;
import org.rusherhack.core.utils.Timer;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

public class BindItem extends ExtendableItem{
    public boolean isListening = false;
    private final Timer idleTimer = new Timer();
    private boolean moduleBind = false;
    private int idling = 0;
    private static final String[] strings = new String[]{"", ".", "..", "..."};

    public BindItem(ExtendableItem parent, IFeatureConfigurable module, Panel panel, Setting<?> settingValue, Boolean moduleBind) {
        super(parent, module, panel, settingValue);
        this.moduleBind = moduleBind;
        open = false;
    }

    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);

        renderer.drawOutlinedRectangle(
                getX(),
                getY(),
                subItems.isEmpty() ? getWidth() : getWidth() - 14 - 1,
                getHeight(),
                NhackPlugin.theme.outlineWidth.getValue(),
                ColorUtils.transparency(NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.outlineColor.getValueRGB());

        if(!subItems.isEmpty()) {
            renderer.drawOutlinedRectangle(
                    getX() + (getWidth() - 14) + 1,
                    getY(),
                    13,
                    getHeight(),
                    NhackPlugin.theme.outlineWidth.getValue(),
                    open
                            ? NhackPlugin.theme.buttonMColor.getValue().getRGB()
                            : ColorUtils.transparency( NhackPlugin.theme.buttonMColor.getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                    NhackPlugin.theme.outlineColor.getValueRGB());
        }

        if(isHovering(mouseX, mouseY)) {
            renderer.drawRectangle(getX(), getY(), getWidth(), getHeight(), new Color(0,0,0, 70).getRGB());
        }

        String displayString = setting.getDisplayValue().equalsIgnoreCase("unknown") ? "None" : setting.getDisplayValue();

        String text = isListening
                ? setting.getDisplayName() + " - Waiting" + getIdleSign()
                : setting.getDisplayName() + " - " + displayString;

        if(subItems.isEmpty())  drawText(text);
        else drawTextEx(text);

        renderSubItems(context, mouseX, mouseY, subItems, open);
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
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        if(!parent.open) return false;
        if (isListening) {
            IKey bind = RusherHackAPI.getBindManager().createKeyboardKey(key);
            if (key == GLFW_KEY_ESCAPE
                    || key == GLFW_KEY_DELETE
                    || key == GLFW_KEY_BACKSPACE

            ) {
                bind = RusherHackAPI.getBindManager().createKeyboardKey(GLFW_KEY_UNKNOWN);
            }
            setting.setValue(bind);
            if(moduleBind)
                RusherHackAPI.getBindManager().setBind((IBindable) module, bind);
            isListening = false;
        }
        return super.keyTyped(key, scanCode, modifiers);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            if (isListening) {
                setting.setValue(RusherHackAPI.getBindManager().createMouseKey(button));
                if(moduleBind) RusherHackAPI.getBindManager().setBind((IBindable) module, RusherHackAPI.getBindManager().createMouseKey(button));
                isListening = false;
            } else {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    isListening = !isListening;
                }
            }
            return true;
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && !subItems.isEmpty() && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public String getIdleSign() {
        if (idleTimer.passed(500L)) {
            idleTimer.reset();
            idling++;
            if (idling > 3) idling = 0;

        }
        return strings[idling];
    }
}
