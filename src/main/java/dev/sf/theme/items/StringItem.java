package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import dev.sf.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;
import org.rusherhack.core.utils.Timer;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.rusherhack.client.api.Globals.mc;


public class StringItem extends ExtendableItem {

    private int count;
    private int index = 1;
    private final Timer idleTimer = new Timer();
    private boolean idling = false, listening = false, isIndex = false;
    private StringBuilder str = new StringBuilder();
    private final List<StringBuilder> ctrlz = new LinkedList<>();

    public StringItem(ExtendableItem parent, IFeatureConfigurable module, Panel panel, Setting<?> setting) {
        super(parent, module, panel, setting);
        ctrlz.add(new StringBuilder((String) (Objects.equals(setting.getValue(), "") ? setting.getDefaultValue() : setting.getValue())));
        ctrlz.add(str);
        count = ctrlz.size() - 1;
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

        if (!subItems.isEmpty()) {
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
        }

        if (isHovering(mouseX, mouseY)) {
            renderer.drawRectangle(getX(), getY(), getWidth(), getHeight(false), new Color(0, 0, 0, 70).getRGB());
        }

        if (listening) {
            
            getFontRenderer().drawString(getIdleSign(),
                    getFontRenderer().getStringWidth(setting.getDisplayName() + ": " + (listening ? str.toString() : setting.getValue())),
                    getY() + NhackPlugin.theme.x.getValue(),
                    NhackPlugin.theme.fontColor.getValueRGB());
            
            if (subItems.isEmpty()) drawText(setting.getDisplayName() + " - " + (listening ? str.toString() : setting.getValue()));
            else drawTextEx(setting.getDisplayName() + " - " + (listening ? str.toString() : setting.getValue()));
        } else {
            if (subItems.isEmpty()) drawText(setting.getDisplayName() + " - " + (listening ? str.toString() : setting.getValue()));
            else drawTextEx(setting.getDisplayName() + " - " + (listening ? str.toString() : setting.getValue()));
        }

        renderSubItems(context, mouseX, mouseY, subItems, open);
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1 || button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2) && parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            if (listening) {
                set();
            } else {
                listening = true;
            }
            return true;
        }
        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && !subItems.isEmpty() && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void set() {
        setting.setValue(str.toString());

        str = new StringBuilder();
        ctrlz.add(str);
        count = ctrlz.size() - 1;
        listening = false;
    }

    public static boolean isAllowedCharacter(char character) {
        return character != 167 && character >= ' ' && character != 127;
    }

    public String getIdleSign() {
        if (idleTimer.passed(500L)) {
            idling = !idling;
            idleTimer.reset();
        }
        if (idling) {
            return "|";
        }
        return "";
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
    public boolean keyTyped(int keyCode, int scanCode, int modifiers) {
        if(!parent.open) return false;
        if (isCopy(keyCode) && (listening || isHovering(mouseX, mouseY))) {
            mc.keyboardHandler.setClipboard(listening ? str.toString() : (String) setting.getValue());
            return true;
        }
        if (isPaste(keyCode) && (listening || isHovering(mouseX, mouseY))) {
            str = new StringBuilder(mc.keyboardHandler.getClipboard());
            return true;
        }
        if (isCTRLZ(keyCode) && (listening || isHovering(mouseX, mouseY))) {
            str = ctrlz.get(count = Math.min(count - 1, ctrlz.size() - 1));
            set();
            return true;
        }
        if (listening)
            switch (keyCode) {
                case GLFW_KEY_KP_ENTER, GLFW_KEY_ENTER -> {
                    set();
                }
                case GLFW_KEY_UP, GLFW_KEY_PAGE_UP -> str = ctrlz.get(count = Math.min(count + 1, ctrlz.size() - 1));
                case GLFW_KEY_DOWN, GLFW_KEY_PAGE_DOWN -> str = ctrlz.get(count = Math.max(count - 1, 0));
                case GLFW_KEY_BACKSPACE -> {
                    str.setLength(Math.max(str.length() - 1, 0));
                }
                case GLFW_KEY_DELETE -> str.setLength(0);
            }
        return super.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character) {
        if (listening && isAllowedCharacter(character)) str.append(character);
        return super.charTyped(character);
    }
}
