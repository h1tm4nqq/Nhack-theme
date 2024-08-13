package dev.sf.theme.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sf.theme.NhackPlugin;
import dev.sf.theme.Panel;
import dev.sf.theme.items.ModuleItem;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.render.EventRenderScreen;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;
import org.rusherhack.core.event.listener.EventListener;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.sf.theme.Panel.run;

public class ClickGUIHandler extends PanelHandlerBase<Panel> implements EventListener {
    public ClickGUIHandler(boolean scaledWithMinecraftGui) {
        super(scaledWithMinecraftGui);
        RusherHackAPI.getEventBus().subscribe(this);
    }

    @Override
    public Panel createPanel(String name) {
        return new Panel(this, name, x1, 17);
    }

    private double x1;

    @Override
    public void initialize() {
        x1 = 5;

        Arrays.stream(ModuleCategory.values()).forEach(moduleCategory -> {
            Panel panel = new Panel(this, moduleCategory.getName().substring(0, 1).toUpperCase() + moduleCategory.getName().substring(1).toLowerCase(), x1, 17);
            List<ModuleItem> items = new ArrayList<>();
            for (IModule module : RusherHackAPI.getModuleManager().getFeatures()) {
                if (module.getCategory() == moduleCategory) {
                    items.add(new ModuleItem(module, panel));
                }

            }

            panel.setModuleItems(items);
            addPanel(panel);
            x1 += panel.getWidth() + 4;
        });

        List<ModuleItem> pluginModules = new ArrayList<>();
        final ClassLoader rusherhackClassLoader = RusherHackAPI.getModuleManager().getFeature("Aura").get().getClass().getClassLoader();
        Panel pluginPanel = new Panel(this, "Plugins", x1, 17);
        if(x1 + pluginPanel.getWidth() + 5 > mc.getWindow().getGuiScaledWidth()) {
            pluginPanel.setX(panels.get(panels.size() - 1).getX());
            pluginPanel.setY(panels.get(panels.size() - 1).getY() + panels.get(panels.size() - 1).getHeight());
        }

        for (IModule module : RusherHackAPI.getModuleManager().getFeatures()) {
            if (!module.getClass().getClassLoader().equals(rusherhackClassLoader)) {
                pluginModules.add(new ModuleItem(module, pluginPanel));
            }
        }

        if (!pluginModules.isEmpty()) {
            pluginPanel.setModuleItems(pluginModules);
            addPanel(pluginPanel);
        }


    }

    @Override
    public void renderElements(RenderContext renderContext, double mouseX, double mouseY) {
        final PoseStack matrixStack = renderContext.pose();
        final IRenderer2D renderer = this.getRenderer();

        for (Panel element : this.getElements()) {
            if (!this.isEnabled(element)) continue;
            if (element == null) continue;
            renderer.begin(matrixStack, this.getFontRenderer());
            matrixStack.translate(0, 0, 100);
            element.render(renderContext, mouseX, mouseY);
            renderer.end();
        }
    }

    @Override
    public void setDefaultPositions() {

    }

    @Override
    public IFontRenderer getFontRenderer() {
        return NhackPlugin.theme.forceVanilla.getValue() ? RusherHackAPI.fonts().getVanillaFontRenderer() : super.getFontRenderer();
    }

    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        if (run != null) {
            run.run();
            run = null;
        }
    }

    @Override
    public boolean isListening() {
        return RusherHackAPI.getThemeManager().getClickGuiHandler().equals(this)
                && mc.screen == RusherHackAPI.getThemeManager().getClickGuiScreen();
    }

    @Subscribe(stage = Stage.PRE)
    private void onScreenRender(EventRenderScreen event) {
        //background
        final IRenderer2D renderer = this.getRenderer();
        renderer.begin(event.getMatrixStack());
        renderer.drawRectangle(0, 0, mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight(), NhackPlugin.theme.backgroundColor.getValueRGB());
        renderer.end();
    }

}
