package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import dev.sf.theme.Theme;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.setting.BindSetting;

import java.awt.*;


public class ModuleItem extends ExtendableItem {
    IModule module;
    Panel panel;
    public boolean open = false;
    private double rendererHeight;

    public ModuleItem(IModule module, Panel panel){
        super(null, module, panel, null);
        this.module = module;
        this.panel = panel;

        if(module instanceof ToggleableModule) {
            addSubItem(new BindItem(this, module, panel, new BindSetting("Bind", RusherHackAPI.getBindManager().getBind((ToggleableModule) module)), true));
        }

        addSettingItems(module.getSettings());

        rendererHeight = 14F;
    }
    @Override
    public double getX() {
        return panel.getX() + 1;
    }
    @Override
    public double getWidth() {
        return panel.getWidth() - 2;
    }

    @Override
    public double getHeight() {
        return rendererHeight;
    }

    @Override
    public double getHeight(boolean total) {
        if(total) return rendererHeight;
        return 14;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY, boolean includeSubItems) {
        return false;
    }

    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        final IRenderer2D renderer = RusherHackAPI.getRenderer2D();
        final IFontRenderer fontRenderer = getFontRenderer();
        boolean isToggled = !(module instanceof ToggleableModule) || ((ToggleableModule) module).isToggled();
        

        renderer.drawOutlinedRectangle(
                getX() + 1,
                getY(),
                getWidth() - 1 - 16,
                getHeight(false),
                NhackPlugin.theme.outlineWidth.getValue(),
                isToggled ? NhackPlugin.theme.getColorSetting().getValue().getRGB() : Theme.changeAlpha(NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.categoryLineColor.getValueRGB()
        );

        renderer.drawOutlinedRectangle(
                getX() + 1 + (getWidth() - 16) +  1,
                getY(),
                13,
                getHeight(false),
                NhackPlugin.theme.outlineWidth.getValue(),
                !subItems.isEmpty() && open ? NhackPlugin.theme.getColorSetting().getValue().getRGB() : Theme.changeAlpha(NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                NhackPlugin.theme.categoryLineColor.getValueRGB()
        );


//        if(module instanceof ToggleableModule){
//            if(((ToggleableModule) module).isToggled())
//                renderer.drawOutlinedRectangle(
//                        getX(),
//                        getY() - 1,
//                        getWidth(),
//                        getHeight(false) + 1,
//                        ExamplePlugin.theme.outlineWidth.getValue(),
//                        ExamplePlugin.theme.getColorSetting().getValue().getRGB(),
//                        ExamplePlugin.theme.moduleOutlineColor.getValueRGB()
//                );
//        }
//        else{
//            renderer.drawOutlinedRectangle(
//                    getX(),
//                    getY() - 1,
//                    getWidth(),
//                    getHeight(false) + 1,
//                    ExamplePlugin.theme.outlineWidth.getValue(),
//                    ExamplePlugin.theme.getColorSetting().getValue().getRGB(),
//                    ExamplePlugin.theme.moduleOutlineColor.getValueRGB()
//            );
//        }

        if(NhackPlugin.theme.settingsOutline.getValue() && open){
            renderer.drawOutlinedRectangle(getX(), getY() + getHeight(false), getWidth(), getHeight(true) - getHeight(false), 2.5f, new Color(0,0,0,0.5f).getRGB(), NhackPlugin.theme.outlineColor.getValueRGB());
        }

        renderSubItems(context, mouseX, mouseY, subItems, open);


//        if(panel.isHovering(mouseX,mouseY, getX(), getY(), getWidth(), getHeight(false))) {
//            renderer.drawRectangle(getX(), getY() - 1, getWidth(), getHeight(false) + 1, new Color(0, 0, 0, 70).getRGB());
//            String description =
//                    (module.getDescription().isEmpty() ?
//                            "A " + module.getCategory() +" Module." + ChatFormatting.GREEN + " Name" + ChatFormatting.RESET + " «" +  module.getName() + "»."
//                            : module.getDescription());
//
//            drawDesc(renderer, mouseX + 8,mouseY + 8, description);
//        }
        
        fontRenderer.drawString(module.getName(),
                getX() + (getWidth() - 1 - 16) / 2 - fontRenderer.getStringWidth(module.getName()) / 2,
                getY() + getHeight(false) / 2 - fontRenderer.getFontHeight() / 2,
                NhackPlugin.theme.fontColor.getValue().getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && panel.isHovering(mouseX, mouseY, getX(), getY(), getWidth() - 1 - 16, getHeight(false))) {
            if(module instanceof ToggleableModule){
                ((ToggleableModule) module).toggle();
            }
            return true;
        }
        
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 16) +  1, getY(), 13, getHeight(false))) {
            this.open = !this.open;
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char character) {
        return super.charTyped(character);
    }

    @Override
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        return super.keyTyped(key, scanCode, modifiers);
    }
    protected void possibleHeightUpdate() {
        double temp = 14f;
        if (open)
            temp += subItems.stream().mapToDouble(i -> i.setting.isHidden() ? 0 : (i.getHeight(true) + 3)).sum();
        rendererHeight = temp;
    }




}
