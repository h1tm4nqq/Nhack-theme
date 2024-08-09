package dev.sf.theme;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sf.theme.items.ModuleItem;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.RenderContext;
import org.rusherhack.client.api.ui.panel.PanelHandlerBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.sf.theme.Panel.run;

public class ClickGUIHandler extends PanelHandlerBase<Panel> {
    public ClickGUIHandler(boolean scaledWithMinecraftGui) {
        super(scaledWithMinecraftGui);
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
            for(IModule module : RusherHackAPI.getModuleManager().getFeatures()) {
                if(module.getCategory() == moduleCategory) {
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

        for(IModule module : RusherHackAPI.getModuleManager().getFeatures()) {
            if(!module.getClass().getClassLoader().equals(rusherhackClassLoader)) {
                pluginModules.add(new ModuleItem(module, pluginPanel));
            }
        }

        if(!pluginModules.isEmpty()) {
            pluginPanel.setModuleItems(pluginModules);
            addPanel(pluginPanel);
        }


    }
    @Override
    public void renderElements(RenderContext renderContext, double mouseX, double mouseY) {
        final PoseStack matrixStack = renderContext.pose();
        final IRenderer2D renderer = this.getRenderer();
        final boolean building = renderer.isBuilding();
        if(building) renderer.end();
        renderer.begin(matrixStack, this.getFontRenderer());
        renderer.drawRectangle(0, 0, mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight(), NhackPlugin.theme.backgroundColor.getValueRGB());
        renderer.end();

        for(Panel element : this.getElements()) {
            if(!this.isEnabled(element)) continue;
            if(element == null) continue;

            renderer.begin(matrixStack, this.getFontRenderer());
            matrixStack.pushPose();
          //  matrixStack.translate(-element.getX(), -element.getY(), 0);
            element.render(renderContext, mouseX, mouseY);
            matrixStack.popPose();
            renderer.end();
        }

    }

    @Override
    public void setDefaultPositions() {

    }

    @Override
    public void render(RenderContext context, double mouseX, double mouseY) {
        super.render(context, mouseX, mouseY);
        if(run != null) {
            run.run();
            run = null;
        }
    }
}
