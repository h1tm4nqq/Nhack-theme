package dev.sf.theme.items;

import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Theme;
import net.minecraft.ChatFormatting;
import dev.sf.theme.Panel;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.core.feature.IFeatureConfigurable;
import org.rusherhack.core.logging.ILogger;
import org.rusherhack.core.setting.Setting;
import org.rusherhack.core.utils.ColorUtils;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//3 года назад я нашел этот колорпикер в каком-то убогом чите, эхх настольгия. А сколько же он повидал: первый форевор, второй форевор, тему хуза и мою тему.
//3 years ago I found this colorpicker in some shitty cheat, ehhh nostalgia. And how much it has seen: first forevor, second forevor, xyz and my theme.
public class ColorItem extends ExtendableItem {
    private static final Color TRANSPARENT = new Color(0, 0, 0, 1);


    public ColorItem(ExtendableItem parent, IFeatureConfigurable module, Panel panel, Setting<?> settingValue) {
        super(parent, module, panel, settingValue);
        colorMode = ((ColorSetting) setting).getRainbowMode();

        // size
        this.svPickerWidth = getWidth() - 15;
        this.svPickerHeight = getWidth() - 15;

        this.hPickerWidth = 6;
        this.hPickerHeight = getWidth() - 15;

        this.aPickerWidth = getWidth() - 15;
        this.aPickerHeight = 6;

        // cursors
        Color color = ((ColorSetting) setting).getValue();
        float[] hsv = Color.RGBtoHSB(color.getRed(), color.getBlue(), color.getGreen(), null);
        hCursorY = hsv[0] * (hPickerHeight - 3F);
        hCursorX = hPickerWidth / 2;
        svCursorX = hsv[1] * (svPickerWidth - 3F);
        svCursorY = (1.0F - hsv[2]) * (svPickerHeight - 3F);
        aCursorX = (color.getAlpha() / 255F) * (aPickerWidth - 3F);
        aCursorY = aPickerHeight / 2;
        prevHeight = renderHeight = getHeight(false);


        small = !((ColorSetting) setting).isAlphaAllowed();

    }

    ColorSetting.RainbowMode colorMode;
    Pattern pattern = Pattern.compile("(?<=ColorItem\\[).+(?=])");
    private double svPickerWidth;
    private double hPickerWidth;
    private double aPickerWidth;
    private double hCursorX;
    boolean changed = false;
    double h = 0;
    // positions
    private double svPickerX;
    private double svPickerY;
    private double svPickerHeight;
    private double hPickerX;
    private double hPickerY;
    private double hPickerHeight;
    private double aPickerX;
    private double aPickerY;
    private double aPickerHeight;
    // cursor
    private double svCursorX, svCursorY;
    private double hCursorY;
    private double aCursorX;
    private double renderHeight;
    private double prevHeight;
    private double aCursorY;
    private boolean svChanging, hChanging, aChanging, sChanging, small = false;
    double posYRainbow;


    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        possibleHeightUpdate();
        double x = parent.getX() + 1.5;

        if (isHovering(mouseX, mouseY)) {
            renderer.drawRectangle(x, getY(), getWidth(), getHeight(), new Color(0, 0, 0, 70).getRGB());
        }

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

        double rectX = getX() + (getWidth() - 14 - 1) - 12 - 1;
        double rectY = getY() + 1;

        renderer.drawRectangle(rectX, rectY, 12, 12, Color.WHITE.getRGB());
        renderer.drawOutlinedRectangle(
                rectX,
                rectY,
                12,
                12,
                1.5F,
                ((Color) setting.getValue()).getRGB(),
                Color.BLACK.getRGB()
        );
        if (open || getHeight() > 14) {
            Color c = ((ColorSetting) this.setting).getValue();
            double pickerX = x + 1.5F;
            double pickerY = this.getY() + this.getHeight();
            // sv
            h = ((hPickerHeight - 3) - hCursorY) / (hPickerHeight - 3); //0-80
            this.svPickerX = pickerX + 1;
            this.svPickerY = pickerY + 1 + 2;

            renderer.drawRectangle(
                    svPickerX,
                    svPickerY,
                    svPickerWidth,
                    svPickerHeight - 1,
                    Color.HSBtoRGB((float) h, 1F, 1F));

            renderer.drawGradientRectangle(
                    svPickerX,
                    svPickerY,
                    svPickerX + svPickerWidth,
                    svPickerY,
                    svPickerWidth,
                    svPickerHeight,
                    Color.WHITE.getRGB(), TRANSPARENT.getRGB());

            renderer.drawGradientRectangle(
                    svPickerX,
                    svPickerY,
                    svPickerX,
                    svPickerY + svPickerHeight,
                    svPickerWidth,
                    svPickerHeight,
                    TRANSPARENT.getRGB(), Color.BLACK.getRGB());

            // sv cursor
            double svCx = svCursorX + svPickerX;
            double svCy = svCursorY + svPickerY;
            renderCursor(svCx, svCy, renderer);

            if (svChanging) {
                svCursorX = mouseX - svPickerX;
                if (svCursorX < 0)
                    svCursorX = 0;
                if (svCursorX > svPickerWidth)
                    svCursorX = svPickerWidth;
                if (svCursorX + 3 > svPickerWidth)
                    svCursorX = svPickerWidth - 3.F;

                svCursorY = mouseY - svPickerY;
                if (svCursorY < 0)
                    svCursorY = 0;
                if (svCursorY > svPickerHeight)
                    svCursorY = svPickerHeight;
                if (svCursorY + 3 > svPickerHeight)
                    svCursorY = svPickerHeight - 3.F;

                changed = true;
            }

            // h

            this.hPickerX = svPickerX + svPickerWidth + 4;
            this.hPickerY = svPickerY - 0.3F;
            double r = 1.0F / hPickerHeight;
            for (int i = 1; i < hPickerHeight -1; i++) {
                double h0 = r * (hPickerHeight - i);
                double h1 = r * (hPickerHeight - (i + 1));
                int top = Color.HSBtoRGB((float) h0, 1.0F, 1.0F);
                int bottom = Color.HSBtoRGB((float) h1, 1.0F, 1.0F);
                renderer.drawGradientRectangle(hPickerX, hPickerY + i -1, hPickerX + hPickerWidth, hPickerY + hPickerHeight + 1, hPickerWidth, 2, top, bottom);
                //мне похуй, я не хочу это фиксить
            }
            // h cursor
            renderCursorNoWith(hCursorX + hPickerX, hCursorY + hPickerY, renderer);

            if (hChanging) {
                hCursorY = mouseY - hPickerY;
                if (hCursorY < 0)
                    hCursorY = 0;
                if (hCursorY > hPickerHeight)
                    hCursorY = hPickerHeight;
                if (hCursorY + 3 > hPickerHeight)
                    hCursorY = hPickerHeight - 3;

                changed = true;
            }

            if (!small) {
                // alpha
                this.aPickerX = svPickerX;
                this.aPickerY = svPickerY + svPickerHeight + 3;
                // rect
                double aRectScale = aPickerHeight / 2;
                double rectCount = (aPickerWidth / aRectScale) - 1;
                for (int i = 0; i < rectCount; i++) {
                    double aRectX1 = aPickerX + i * aRectScale;
                    double aRectX2 = aRectScale; //(i + 1) * aRectScale;
                    if (aRectX2 > aPickerX + aPickerWidth)
                        aRectX2 = aPickerX + aPickerWidth;

                    renderer.drawRectangle(aRectX1, aPickerY, aRectX2, aRectScale, i % 2 == 0 ? new Color(255, 255, 255).getRGB() : new Color(204, 204, 204).getRGB());
                    renderer.drawRectangle(aRectX1, aPickerY + aRectScale, aRectX2, aRectScale, (i + 1) % 2 == 0 ? new Color(255, 255, 255).getRGB() : new Color(204, 204, 204).getRGB());
                }
                int right = toRGBA(c.getRed(), c.getGreen(), c.getBlue(), 1);
                int left = toRGBA(c.getRed(), c.getGreen(), c.getBlue(), 255);
                renderer.drawGradientRectangle(aPickerX, aPickerY, aPickerWidth + aPickerX, aPickerHeight + aPickerY,
                        aPickerWidth, aPickerHeight, right, left);
                // cursor
                double aCx = aCursorX + aPickerX;
                double aCy = aCursorY + aPickerY;
                renderCursorNoHeight(aCx, aCy, renderer);
                if (aChanging) {
                    aCursorX = mouseX - aPickerX;
                    if (aCursorX < 0)
                        aCursorX = 0;
                    if (aCursorX > aPickerWidth)
                        aCursorX = aPickerWidth;
                    if (aCursorX + 3 > aPickerWidth)
                        aCursorX = aPickerWidth - 3;
                    changed = true;
                }
            }

            if (changed) {
                double s = svCursorX / (svPickerWidth - 3F);
                double v = ((svPickerHeight - 3F) - svCursorY) / (svPickerHeight - 3F);
                double a = aCursorX / ((aPickerWidth - 3F));
                Color color = new Color(Color.HSBtoRGB((float) h, (float) s, (float) v));
                setting.setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (a * 255)))));

                changed = false;
            }
            posYRainbow = !small ? aPickerY + 9 : svPickerY + svPickerHeight + 1;
            //renderer.drawRectangle(x, posYRainbow, getWidth() - 1, getHeight(), isHovering(mouseX, mouseY, x, posYRainbow, x + getWidth(), posYRainbow + getHeight()) ? new Color(0, 0, 0, 70).getRGB() : ExamplePlugin.theme.getColorSetting().getValueRGB());
            //getFontRenderer().drawText(getFontRenderer().trimStringToWidth("ColorMode: " + colorMode.name(), getWidth()), x + 3.5, posYRainbow, ExamplePlugin.theme.fontColor.getValueRGB(), getWidth(), getHeight());
            if (sChanging) {
                setSettingFromX(mouseX);
            }
        }
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    @Override
    public boolean keyTyped(int keyCode, int scanCode, int modifiers) {
        if (!parent.open) return false;
        if (isCopy(keyCode) && isHovering(mouseX, mouseY, getX(), getY(), getX() + getWidth(), getY() + getHeight())) {

            setClipboardString("ColorItem[" +
                    ((ColorSetting) setting).getValue().getRed() + "/" +
                    ((ColorSetting) setting).getGreen() + "/" +
                    ((ColorSetting) setting).getBlue() + "/" +
                    ((ColorSetting) setting).getAlpha() +
                    "]");
        }
        if (isPaste(keyCode) && isHovering(mouseX, mouseY, getX(), getY(), getX() + getWidth(), getY() + getHeight()) && getClipboardString().startsWith("ColorItem[")) {
            Matcher matcher = pattern.matcher(getClipboardString());

            while (matcher.find()) {
                String[] split = matcher.group(0).split("/");
                Color color = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                setting.setValue(color);
                float[] hsv = Color.RGBtoHSB(color.getRed(), color.getBlue(), color.getGreen(), null);
                hCursorY = hsv[0] * (hPickerHeight - 3F);
                svCursorX = hsv[1] * (svPickerWidth - 3F);
                svCursorY = (1.0F - hsv[2]) * (svPickerHeight - 3F);
                aCursorX = (color.getAlpha() / 255F) * (aPickerWidth - 3F);
                changed = true;
            }
        }
        if (isCTRLR(keyCode) && isHovering(mouseX, mouseY, getX(), getY(), getX() + getWidth(), getY() + getHeight())) {
            Color color = ((ColorSetting) setting).getDefaultValue();
            setting.setValue(color);
            float[] hsv = Color.RGBtoHSB(color.getRed(), color.getBlue(), color.getGreen(), null);
            hCursorY = hsv[0] * (hPickerHeight - 3F);
            svCursorX = hsv[1] * (svPickerWidth - 3F);
            svCursorY = (1.0F - hsv[2]) * (svPickerHeight - 3F);
            aCursorX = (color.getAlpha() / 255F) * (aPickerWidth - 3F);
            changed = true;
        }
        return super.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && parent.open && panel.isHovering(mouseX, mouseY, getX() + 1 + (getWidth() - 14) + 1, getY(), 13, getHeight(false))) {
            open = !open;
            return true;
        }

        if (open) {
            if (button == 0) {
                if (isHovering(mouseX, mouseY, svPickerX, svPickerY, svPickerX + svPickerWidth, svPickerY + svPickerHeight)) {
                    svChanging = true;
                    return true;
                }
                if (isHovering(mouseX, mouseY, hPickerX, hPickerY, hPickerX + hPickerWidth, hPickerY + hPickerHeight)) {
                    hChanging = true;
                    return true;
                }
                if (isHovering(mouseX, mouseY, aPickerX, aPickerY, aPickerX + aPickerWidth, aPickerY + aPickerHeight) && !small) {
                    aChanging = true;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setSettingFromX(double mouseX) {
        double percent = (mouseX - getX()) / (getWidth());
        int speed = 1 + (int) (254 * percent);
        if (speed >= 1 && speed <= 255) {
            //  this.speed = speed;
        }
    }

    public double getCenter(double a, double b, double c) {
        return a + (b - c) / 2;
    }

    private void renderCursor(double x, double y, IRenderer2D renderer) {
        renderer.drawRectangle(x, y, 3, 3, new Color(20, 20, 20).getRGB());
        renderer.drawRectangle(x + 0.5, y + 0.5, 1.8, 1.8, new Color(250, 250, 250).getRGB());
    }

    private void renderCursorNoWith(double x, double y, IRenderer2D renderer) {
        renderer.drawRectangle(x - 5, y, 10, 3, new Color(20, 20, 20).getRGB());
        renderer.drawRectangle(x - 4, y + 1, 8, 1, new Color(250, 250, 250).getRGB());
    }

    private void renderCursorNoHeight(double x, double y, IRenderer2D renderer) {
        renderer.drawRectangle(x, y - 5, 3, 10, new Color(20, 20, 20).getRGB());
        renderer.drawRectangle(x + 1, y - 4, 1, 8, new Color(250, 250, 250).getRGB());
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (!open) return;
        svChanging = false;
        hChanging = false;
        aChanging = false;
        sChanging = false;
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public double getHeight(boolean total) {
        if (total) return renderHeight;
        return super.getHeight(false);
    }

    protected void possibleHeightUpdate() {
        double temp;
        if (open) {
            temp = (!small ? svPickerHeight + aPickerHeight + 3 : svPickerHeight) + 17;// ура фикс хохла пикера
        } else {
            temp = super.getHeight();
        }
        if (this.renderHeight == temp) {
            return;
        }
        prevHeight = this.renderHeight;
        this.renderHeight = temp;
    }
}
