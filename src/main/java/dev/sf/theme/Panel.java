package dev.sf.theme;

import dev.sf.theme.items.ModuleItem;
import lombok.Getter;
import lombok.Setter;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.panel.IPanelItem;
import org.rusherhack.client.api.ui.panel.PanelBase;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.core.feature.IFeature;

import java.awt.*;
import java.util.List;


public class Panel extends PanelBase<IPanelItem> {
    public Panel(PanelHandlerBase handler, String category, double x, double y) {
        super(handler, category);
        this.category = category;
        setX(x);
        setY(y);
    }

    @Setter
    public static Runnable run;
    @Setter
    @Getter
    private double scroll;
    private double diffX;
    private double diffY;


    @Setter
    @Getter
    private double prevYModule;
    @Setter
    @Getter
    private double prevX;
    @Setter
    @Getter
    private double prevY;
    @Setter
    private boolean open = true, drag = false;
    @Setter
    @Getter
    private List<ModuleItem> moduleItems;
    private final String category;

    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        if (drag) {
            setX(mouseX + diffX);
            setY(mouseY + diffY);
        }
        double x = getX();
        double y = getY();
        final IRenderer2D renderer = RusherHackAPI.getRenderer2D();
        double height = this.getHeight();
        renderer.drawOutlinedRectangle
        (
                x, getYTop(),
                getWidth(), 13.0, NhackPlugin.theme.outlineWidth.getValue(),
                NhackPlugin.theme.categoryColor.getValueRGB(), NhackPlugin.theme.categoryLineColor.getValueRGB()
        );
        getFontRenderer().drawString
        (
                category,
                x + 2,
                y - 9.5 - 2,
                NhackPlugin.theme.fontColor.getValue().getRGB()
        );
        renderer.drawOutlinedRectangle
                (
                        x + 103,
                        y - 11.5 - 2,
                        10,
                        10,
                        NhackPlugin.theme.outlineWidth.getValue(),
                     !open
                             ? new Color(NhackPlugin.theme.getColorSetting().getValue().getRed(), NhackPlugin.theme.getColorSetting().getValue().getGreen(), NhackPlugin.theme.getColorSetting().getValue().getBlue(), NhackPlugin.theme.alpha.getValue()).getRGB()
                             : NhackPlugin.theme.getColorSetting().getValue().getRGB(), NhackPlugin.theme.categoryLineColor.getValueRGB()
                );

        if (open) {
            if (height > 0) {
                renderer.drawOutlinedRectangle
                (
                        x, y,
                        getWidth(), height + 1.5F, NhackPlugin.theme.outlineWidth.getValue(),
                        NhackPlugin.theme.backColor.getValueRGB(),
                        NhackPlugin.theme.outlineColor.getValueRGB()
                );
            }
            double y0 = y + 2;
            if (height > 0) {
                for (ModuleItem frame : moduleItems) {
                    frame.setX(x);
                    frame.setY(y0);
                    frame.render(context, mouseX, mouseY);
                    y0 += frame.getHeight(true) + 3;
                }
            }
        }
    }

    private double getYTop() {
        return getY() - 13.0 - 2;
    }


    @Override
    public double getWidth() {
        return 115F;
    }

    @Override
    public double getHeight() {
        double i = 0;
        for (ModuleItem item : moduleItems) {
            i += item.getHeight(true) + 3;
        }
        return i;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (open) moduleItems.forEach(frame -> frame.mouseClicked(mouseX, mouseY, button));
        if (isHovering(mouseX, mouseY, getX(), getYTop(), getWidth(), 13)) {
            if(isHovering(
                    mouseX,
                    mouseY,
                    getX() + 103,
                    getY() - 11.5 - 1,
                    10,
                    10)
            ) {
                open = !open;
                return false;
            }
            if (button == 0) {
                NhackPlugin.theme.getClickGuiHandler().getElements().forEach(element -> drag = false);
                diffX = getX() - mouseX;
                diffY = getY() - mouseY;
                drag = true;
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (button == 0) drag = false;
        if (open) moduleItems.forEach(frame -> frame.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        //if(isHovering(mouseX, mouseY, getX(), getY() - 14.5F, getWidth(), getHeight())) {
        if (delta >= 0) {
            setY(getY() + NhackPlugin.theme.scrollSpeed.getValue());
        } else {
            setY(getY() - NhackPlugin.theme.scrollSpeed.getValue());
        }
        //   }
        return false;
    }
    @Override
    public IFontRenderer getFontRenderer() {
        return NhackPlugin.theme.forceVanilla.getValue() ? RusherHackAPI.fonts().getVanillaFontRenderer() : super.getFontRenderer();
    }
    public boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
        return x < mouseX && width + x > mouseX && y < mouseY && height + y > mouseY;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean charTyped(char character) {
        if (open) moduleItems.forEach(frame -> frame.charTyped(character));
        return false;
    }

    @Override
    public boolean keyTyped(int key, int scanCode, int modifiers) {
        if (open) moduleItems.forEach(frame -> frame.keyTyped(key, scanCode, modifiers));
        return false;
    }

    @Override
    public IPanelItem createFeatureItem(IFeature feature) {
        if (feature instanceof IModule module) {
            return new ModuleItem(module, this);
        }
        return null;
    }
}
