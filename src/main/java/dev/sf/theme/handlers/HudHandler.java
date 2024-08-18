package dev.sf.theme.handlers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import dev.sf.theme.items.ModuleItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.Minecraft;
import org.rusherhack.client.api.Globals;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.render.EventRenderScreen;
import org.rusherhack.client.api.feature.hud.HudElement;
import org.rusherhack.client.api.feature.hud.IHudElement;
import org.rusherhack.client.api.feature.hud.ResizeableHudElement;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.hud.HudHandlerBase;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.interfaces.IClickable;
import org.rusherhack.core.interfaces.IDraggable;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.StreamSupport;

import static dev.sf.theme.Panel.run;

public class HudHandler extends HudHandlerBase {
    private final HudManagerHandler HudManagerHandler = new HudManagerHandler();

    public HudHandler(boolean scaledWithMinecraftGui) {
        super(scaledWithMinecraftGui);

    }

    @Override
    public PanelHandlerBase<?> getHudManagerPanel() {
        return this.HudManagerHandler;
    }


    @Override
    public void renderHudElement(HudElement hudElement, RenderContext renderContext, double mouseX, double mouseY) {
        if (mc.screen != RusherHackAPI.getThemeManager().getHudEditorScreen() && !shouldRender(hudElement)) {
            return;
        }

        final Window window = Globals.mc.getWindow();
        double x = hudElement.getX();
        double y = hudElement.getY();
        final IRenderer2D renderer = hudElement.getRenderer();
        final IFontRenderer fr = hudElement.getFontRenderer();
        final boolean building = renderer.isBuilding();
        final PoseStack matrixStack = renderContext.pose();

        if (x < 0) {
            hudElement.setX(0);
        } else if (x > window.getGuiScaledWidth()) {
            hudElement.setX(window.getGuiScaledWidth());
        }

        if (y < 0) {
            hudElement.setY(0);
        } else if (y > window.getGuiScaledHeight()) {
            hudElement.setY(window.getGuiScaledHeight());
        }

        double width = Math.max(hudElement.getScaledWidth(), renderer.getFontRenderer().getStringWidth(hudElement.getName()) + 30 + 12.5) + 1;

        if (!building) {
            renderer.begin(matrixStack, fr);
        }

        x = hudElement.getStartX();
        y = hudElement.getStartY();
        renderer.getMatrixStack().pushPose();

        renderer.getMatrixStack().translate(-x, -y, 0);
        renderer.drawOutlinedRectangle
                (
                        x, y,
                        width, 13.0, NhackPlugin.theme.outlineWidth.getValue(),
                        NhackPlugin.theme.categoryColor.getValueRGB(), NhackPlugin.theme.outlineColor.getValueRGB()
                );
        getFontRenderer().drawString
                (
                        hudElement.getName(),
                        x + 2,
                        y + 2,
                        NhackPlugin.theme.fontColor.getValue().getRGB()
                );

        renderer.drawOutlinedRectangle
                (
                        x + width - 11.5 - 12.5,
                        y + 1.5,
                        10,
                        10,
                        NhackPlugin.theme.outlineWidth.getValue(),
                        shouldRender(hudElement)
                                ? NhackPlugin.theme.buttonCColor.getValue().getRGB()
                                : ColorUtils.transparency(NhackPlugin.theme.buttonCColor.getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                        NhackPlugin.theme.outlineColor.getValueRGB()
                );

        renderer.drawOutlinedRectangle
                (
                        x + width - 11.5,
                        y + 1.5,
                        10,
                        10,
                        NhackPlugin.theme.outlineWidth.getValue(),
                        isOpen(hudElement)
                                ? NhackPlugin.theme.buttonCColor.getValue().getRGB()
                                : ColorUtils.transparency(NhackPlugin.theme.buttonCColor.getValue().getRGB(), NhackPlugin.theme.alpha.getValue()),
                        NhackPlugin.theme.outlineColor.getValueRGB()
                );

        renderer.getMatrixStack().popPose();

        if (isOpen(hudElement)) {
            renderer.getMatrixStack().pushPose();
            renderer.getMatrixStack().translate(-x, -y, 0);
            renderer.drawOutlinedRectangle(x, y + 13 + 2, width, hudElement.getScaledHeight() + 0, NhackPlugin.theme.outlineWidth.getValue(), NhackPlugin.theme.panelColor.getValueRGB(), NhackPlugin.theme.outlineColor.getValueRGB());
            renderer.getMatrixStack().popPose();

            renderer.getMatrixStack().pushPose();
            renderer.getMatrixStack().translate(1, 13 + 2 + 0.5, 0);
            hudElement.render(renderContext, mouseX, mouseY);
            renderer.getMatrixStack().popPose();


        }

        if (!building) {
            renderer.end();
        }

        if (isOpen(hudElement)) {
            renderer.getMatrixStack().pushPose();
            renderer.getMatrixStack().translate(1, 13 + 2 + 0.5, 0);
            hudElement.postRender(renderContext, mouseX, mouseY);
            renderer.getMatrixStack().popPose();
        }

    }

    @Override
    protected boolean consumeElementMouseClick(HudElement element, double mouseX, double mouseY, int button) {
        double width = Math.max(element.getScaledWidth(), element.getRenderer().getFontRenderer().getStringWidth(element.getName()) + 30 + 12.5) + 1;
        if (isHovering(mouseX, mouseY, element.getStartX(), element.getStartY(), width - 11.5 - 12.5, 13) && button == 0) {
            return element.mouseClicked(mouseX, mouseY, button);
        }

        if (isHovering(mouseX, mouseY, element.getStartX() + width - 11.5, element.getStartY() + 1.5, 10, 10) && button == 0) {
            setOpen(element, !isOpen(element));
            return true;
        }

        if (isHovering(mouseX, mouseY, element.getStartX() + width - 11.5 - 12.5, element.getStartY() + 1.5, 10, 10) && button == 0) {
            setShouldRender(element, !shouldRender(element));
            return true;
        }
        element.registerSettings();
        return false;
    }

    @Override
    public IFontRenderer getFontRenderer() {
        return NhackPlugin.theme.forceVanilla.getValue() ? RusherHackAPI.fonts().getVanillaFontRenderer() : super.getFontRenderer();
    }

    //code in 6:20 am haven't slept all night

    private boolean isOpen(HudElement element) {
        for (Setting<?> setting : NhackPlugin.theme.getSettings()) {
            if (setting.getName().equals(element.getName().toLowerCase() + "#is_open")) {
                return (boolean) setting.getValue();
            }
        }
        NhackPlugin.theme.registerIsOpen(element);
        return isOpen(element);
    }

    private boolean shouldRender(HudElement element) {
        for (Setting<?> setting : NhackPlugin.theme.getSettings()) {
            if (setting.getName().equals(element.getName().toLowerCase() + "#should_render")) {
                return (boolean) setting.getValue();
            }
        }
        NhackPlugin.theme.registerShouldRender(element);
        return shouldRender(element);
    }

    private void setOpen(HudElement element, boolean value) {
        for (Setting<?> setting : NhackPlugin.theme.getSettings()) {
            if (setting.getName().equals(element.getName().toLowerCase() + "#is_open")) {
                ((BooleanSetting) setting).setValue(value);
                return;
            }
        }
        NhackPlugin.theme.registerIsOpen(element);
        setOpen(element, value);
    }

    private void setShouldRender(HudElement element, boolean value) {
        for (Setting<?> setting : NhackPlugin.theme.getSettings()) {
            if (setting.getName().equals(element.getName().toLowerCase() + "#should_render")) {
                ((BooleanSetting) setting).setValue(value);
                return;
            }
        }
        NhackPlugin.theme.registerShouldRender(element);
        setShouldRender(element, value);
    }

    @Override
    protected void consumeMouseMove(double mouseX, double mouseY) {
        for (HudElement element : this.getElements()) {
            if (!this.isEnabled(element)) continue;

            if (element != null && this.consumeElementMouseMove(element, mouseX, mouseY)) {
                double x = element.getStartX() + element.getScaledWidth() + 1;
                double y = element.getStartY() + 1;
                Window window = Minecraft.getInstance().getWindow();
                if (x < 0) {
                    element.setX(0);
                } else if (x > window.getGuiScaledWidth()) {
                    element.setX(window.getGuiScaledWidth());
                }

                if (y < 0) {
                    element.setY(0);
                } else if (y > window.getGuiScaledHeight()) {
                    element.setY(window.getGuiScaledHeight());
                }
                break;
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderHudElementBackground(HudElement hudElement, RenderContext renderContext, IRenderer2D renderer, double width, double height, int color) {

    }

    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
        return x < mouseX && width + x > mouseX && y < mouseY && height + y > mouseY;
    }

    public static class HudManagerHandler extends PanelHandlerBase<Panel> {

        private final Panel panel = new Panel(this, "Hud Elements", 200, 50) {
            @Override
            public boolean isHovered(double mouseX, double mouseY) {
                return super.isHovered(mouseX, mouseY);
            }
        };

        public HudManagerHandler() {
            super(false);
        }

        @Override
        public void initialize() {
            this.addPanel(this.panel);

            //ensure empty
            this.panel.getItemList().clear();
            panel.setModuleItems(StreamSupport.stream(RusherHackAPI.getHudManager().getFeatures().spliterator(), false)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(HudElement::getName))//sort by name
                    .map(hudElement -> new ModuleItem(hudElement, panel))
                    .toList());
        }

        @Override
        public void setDefaultPositions() {
            this.panel.setX(200);
            this.panel.setY(50);
        }

        @Override
        public void render(RenderContext context, double mouseX, double mouseY) {

            //clamp hud manager position
            if (this.panel.getX() < 0) {
                this.panel.setX(0);
            }
            if (this.panel.getY() < 0) {
                this.panel.setY(0);
            }

            final Window window = Globals.mc.getWindow();
            final float scale = this.getScale();

            final double panelWidth = this.panel.getScaledWidth();
            if (this.panel.getX() * scale + panelWidth > window.getGuiScaledWidth()) {
                this.panel.setX((window.getGuiScaledWidth() - panelWidth) / scale);
            }

            super.render(context, mouseX, mouseY);
        }


        @Override
        public void renderElements(RenderContext renderContext, double mouseX, double mouseY) {
            final PoseStack matrixStack = renderContext.pose();
            final IRenderer2D renderer = this.getRenderer();

            if (!renderer.isBuilding()) renderer.begin(matrixStack, this.getFontRenderer());

            for (Panel element : this.getElements()) {
                if (!this.isEnabled(element)) continue;
                if (element == null) continue;

                matrixStack.translate(0, 0, 100);
                element.render(renderContext, mouseX, mouseY);

            }
            if (!renderer.isBuilding()) renderer.end();

        }


        @Override
        public IFontRenderer getFontRenderer() {
            return NhackPlugin.theme.forceVanilla.getValue() ? RusherHackAPI.fonts().getVanillaFontRenderer() : super.getFontRenderer();
        }

        @Override
        public float getScale() {
            return super.getScale();
        }

        @Override
        public Panel createPanel(String name) {
            return new Panel(this, name, 200, 50);
        }
    }


}
