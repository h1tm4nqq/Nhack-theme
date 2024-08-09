package org.example.theme.item.items;

import net.minecraft.ChatFormatting;
import org.example.theme.ExamplePlugin;
import org.example.theme.Panel;
import org.example.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.setting.StringSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class EnumItem extends ExtendableItem{


    public EnumItem(ExtendableItem parent, IModule module, Panel panel, Setting<?> settingValue) {
        super(parent, module, panel, settingValue);
    }


    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);

        renderer.drawOutlinedRectangle(
                getX(),
                getY(),
                subItems.isEmpty() ? getWidth() : getWidth() - 14 - 1,
                getHeight(),
                ExamplePlugin.theme.outlineWidth.getValue(),
                Theme.changeAlpha(ExamplePlugin.theme.getColorSetting().getValue().getRGB(), 100),
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

        if(isHovering(mouseX, mouseY)) {
            renderer.drawRectangle(getX(), getY(), getWidth(), getHeight(), new Color(0,0,0, 70).getRGB());
        }
        if(subItems.isEmpty())  drawText(setting.getDisplayName() + " - " + setting.getDisplayValue());
        else drawTextEx(setting.getDisplayName() + " - " + setting.getDisplayValue());
        renderSubItems(context, mouseX, mouseY, subItems, open);

    }
    @Override
    public double getX() {
        return parent.getX() + 1.5;
    }

    public void next() {
        if(setting instanceof StringSetting && !((StringSetting) setting).getOptions().isEmpty()) {
            LinkedHashSet<String> options = ((StringSetting) setting).getOptions();
            String currentValue = (String) setting.getValue();
            int currentIndex = new ArrayList<>(options).indexOf(currentValue);
            currentIndex = (currentIndex + 1) % options.size();
            setting.setValue(new ArrayList<>(options).get(currentIndex));
        } else if (this.setting.getValue() instanceof Enum<?> value) {

            Enum<?>[] array = value.getDeclaringClass().getEnumConstants();
            this.setting.setValue((array.length - 1 == value.ordinal()
                    ? array[0]
                    : array[value.ordinal() + 1]));

        } else {
            throw new IllegalStateException("value isn't an enum or a StringSetting with options");
        }
    }
    public void previous() {
        if(setting instanceof StringSetting && !((StringSetting) setting).getOptions().isEmpty()) {

            List<String> options = new ArrayList<>(((StringSetting) setting).getOptions());
            String currentValue = (String) setting.getValue();
            int currentIndex = options.indexOf(currentValue);
            currentIndex = (currentIndex - 1 + options.size()) % options.size();
            setting.setValue(options.get(currentIndex));

        } else if (this.setting.getValue() instanceof Enum<?> value) {

            Enum<?>[] array = value.getDeclaringClass().getEnumConstants();
            this.setting.setValue((value.ordinal() - 1 < 0
                    ? array[array.length - 1]
                    : array[value.ordinal() - 1]));

        } else {
            throw new IllegalStateException("value isn't an enum or a StringSetting with options");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (parent.open && subItems.isEmpty()
                ? panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth(), getHeight())
                : panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 14, getHeight(false)))
        {
            switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_1 -> next();
                case GLFW.GLFW_MOUSE_BUTTON_2 -> previous();
            }
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
    public double getWidth() {
        return super.getWidth();
    }

    @Override
    public double getHeight() {
        return super.getHeight();
    }
}
