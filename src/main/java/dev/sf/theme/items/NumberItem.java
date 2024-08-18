package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Theme;
import lombok.Getter;
import dev.sf.theme.Panel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;
import org.rusherhack.core.utils.MathUtils;
import org.rusherhack.core.utils.Timer;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class NumberItem extends ExtendableItem {
    private boolean isListening = false;
    private final Timer idleTimer = new Timer();
    private boolean idling = false, listening = false;
    private StringBuilder str = new StringBuilder();
    private final List<StringBuilder> ctrlz = new LinkedList<>();
    @Getter
    private double renderWidth;
    private int count;

    public NumberItem(ExtendableItem parent, IFeatureConfigurable module, Panel panel, Setting<?> settingValue) {
        super(parent, module, panel, settingValue);
        ctrlz.add(new StringBuilder(setting.getValue().toString()));
        ctrlz.add(str);
        count = ctrlz.size() - 1;
    }


    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);

        setRenderWidth(((subItems.isEmpty() ? getWidth() : getWidth() - 14 - 1) - 4) * partialMultiplier());

        renderer.drawRectangleOutline(getX(), getY(), getWidth(), getHeight(false),
                NhackPlugin.theme.outlineWidth.getValue(),
                NhackPlugin.theme.outlineColor.getValueRGB());

        renderer.drawRectangle(getX() + 2, getY() + 2, getRenderWidth(), getHeight(false) -4,
                NhackPlugin.theme.getColorSetting().getValue().getRGB());

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

        if(isHovering(mouseX, mouseY)) {
            renderer.drawRectangle(getX(), getY(), getRenderWidth(), getHeight(false), new Color(0,0,0, 70).getRGB());
        }

        drawText(setting.getDisplayName() + " - " + (listening ? str.toString() + getIdleSign() : setting.getValue()));
        if (isListening) {
            setSettingFromX((float) mouseX);
        }

        renderSubItems(context, mouseX, mouseY, subItems, open);
    }



    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            isListening = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            if(button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1) {
                isListening = true;
            }
            if(button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2) {
                if (listening) {
                    set();
                } else {
                    listening = true;
                }
            }
            return true;
        }
        if(button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && !subItems.isEmpty() && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) +  1, getY(), 13, getHeight(false))) {
            open = !open;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        if(!parent.open) return false;
        if (isCopy(key) && (listening || isHovering(mouseX, mouseY))) {
            String text = wrapString("Number" + genPrefix(), listening ? str.toString() : setting.getValue().toString());
            setClipboardString(text);
            return true;
        }
        if (isPaste(key) && (listening || isHovering(mouseX, mouseY))) {
            String text = getValueFromWrappedString("Number" + genPrefix(), getClipboardString());
            if (text != null) {
                str = new StringBuilder(text);
                set();
            }
            return true;
        }
        if (isCTRLZ(key) && (listening || isHovering(mouseX, mouseY))) {
            str = ctrlz.get(count = Math.min(count - 1, ctrlz.size() - 1));
            set();
            return true;
        }
        if (listening)
            switch (key) {
                case GLFW_KEY_KP_ENTER, GLFW_KEY_ENTER -> set();
                case GLFW_KEY_UP, GLFW_KEY_PAGE_UP -> str = ctrlz.get(count = Math.min(count + 1, ctrlz.size() - 1));
                case GLFW_KEY_DOWN, GLFW_KEY_PAGE_DOWN -> str = ctrlz.get(count = Math.max(count - 1, 0));
                case GLFW_KEY_BACKSPACE -> str.setLength(Math.max(str.length() - 1, 0));
                case GLFW_KEY_DELETE -> str.setLength(0);
                default -> {
                    if ((key >= 48 && key <= 57 || key >= 320 && key <= 329)
                            || key == GLFW_KEY_PERIOD
                            || key == GLFW_KEY_MINUS
                    ) {
                        String keyName = glfwGetKeyName(key, scanCode);
                        str.append(keyName);
                    }
                }
            }
        return super.keyTyped(key, scanCode, modifiers);
    }
    private void set() {
        setValueFromString(str.toString());
        str = new StringBuilder();
        ctrlz.add(str);
        count = ctrlz.size() - 1;
        listening = false;
    }
    private String genPrefix() {
        if (setting.getValue() instanceof Double) {
            return "Double";
        }
        if (setting.getValue() instanceof Float) {
            return "Double";
        }
        if (setting.getValue() instanceof Integer) {
            return "Int";
        }
        throw new IllegalStateException("Мне лень");
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

    public void setRenderWidth(double renderWidth) {
        if (this.renderWidth == renderWidth) return;
        this.renderWidth = renderWidth;
    }

    private void setValueFromString(String value) {
        try {
            if(setting instanceof NumberSetting<?> num) {
                if (setting.getValue() instanceof Double) {
                    double min = (double) num.getMinimum();
                    double max = (double) num.getMaximum();
                    double result = Double.parseDouble(value);
                    setting.setValue(Math.max(min, Math.min(max, result)));
                }
                if (setting.getValue() instanceof Float) {
                    float min = (float) num.getMinimum();
                    float max = (float) num.getMaximum();
                    float result = Float.parseFloat(value);
                    setting.setValue(Math.max(min, Math.min(max, result)));
                }
                if (setting.getValue() instanceof Integer) {
                    int min = (int) num.getMinimum();
                    int max = (int) num.getMaximum();
                    int result = Integer.parseInt(value);
                    setting.setValue(Math.max(min, Math.min(max, result)));
                }
            }
        } catch (Throwable ignored) {
        }
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

    private void setSettingFromX(float mouseX) {
        if (setting instanceof NumberSetting<?> num) {
            if (num.getValue() instanceof Double) {
                double min = (double) num.getMinimum();
                double max = (double) num.getMaximum();
                double step = (max - min) / getWidth();
                double newValue = (mouseX - getX()) * (max - min) / getWidth() + min;
                num.setValue(MathUtils.clamp(roundDouble(roundToStep(newValue, step), 2), min, max));
            } else if (num.getValue() instanceof Float) {
                float min = (float) num.getMinimum();
                float max = (float) num.getMaximum();
                double step = (max - min) / getWidth();
                float newValue = (float) (((mouseX - (getX())) * (max - min)) / getWidth() + min);
                num.setValue(MathUtils.clamp(roundFloat(roundToStep(newValue, (float) step), 2), min, max));
            } else if (num.getValue() instanceof Integer) {
                int min = (int) num.getMinimum();
                int max = (int) num.getMaximum();
                double step = (max - min) / getWidth();
                float newValue = (float) (((mouseX - (getX())) * (max - min)) / getWidth() + min);
                num.setValue(MathUtils.clamp(roundFloat(roundToStep(newValue, (float) step), 2), min, max));
            }
        }
    }
    private double middle() {
        if(setting instanceof NumberSetting<?> num)
            return (num.getMaximum().doubleValue() - num.getMinimum().doubleValue());
        return 0f;
    }
    public static float roundToStep(float value, float step) {
        return step * Math.round(value * (1 / step));
    }
    public static float roundFloat(float value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private double part() {
        if(setting instanceof NumberSetting<?> num)
            return (num.getValue().doubleValue() - num.getMinimum().doubleValue());
        return 0f;
    }

    private double partialMultiplier() {
        return part() / middle();
    }
    public static double roundToStep(double value, double step) {
        return step * Math.round(value * (1 / step));
    }
    public static double roundDouble(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
