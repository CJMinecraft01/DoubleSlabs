package cjminecraft.doubleslabs.client.gui;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.DSConfig;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

public class DSGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new DSConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class DSConfigGui extends GuiConfig {

        public DSConfigGui(GuiScreen parentScreen) {
            super(parentScreen, getConfigElements(), DoubleSlabs.MODID, false, false, I18n.format("doubleslabs.configgui.title"));
        }

        private static List<IConfigElement> getConfigElements() {
            return Lists.newArrayList(
                   new DummyConfigElement.DummyCategoryElement("Server", "doubleslabs.configgui.category.server", ServerCategoryEntry.class),
                   new DummyConfigElement.DummyCategoryElement("Client", "doubleslabs.configgui.category.client", ClientCategoryEntry.class)
            );
        }

        public static class ServerCategoryEntry extends GuiConfigEntries.CategoryEntry {

            public ServerCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
                super(owningScreen, owningEntryList, configElement);
            }

            @Override
            protected GuiScreen buildChildScreen() {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(DSConfig.getConfig().getCategory(Configuration.CATEGORY_GENERAL))).getChildElements(),
                        this.owningScreen.modID, Configuration.CATEGORY_GENERAL, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(DSConfig.getConfig().toString()));
            }
        }

        public static class ClientCategoryEntry extends GuiConfigEntries.CategoryEntry {

            public ClientCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
                super(owningScreen, owningEntryList, configElement);
            }

            @Override
            protected GuiScreen buildChildScreen() {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(DSConfig.getConfig().getCategory(Configuration.CATEGORY_CLIENT))).getChildElements(),
                        this.owningScreen.modID, Configuration.CATEGORY_CLIENT, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(DSConfig.getConfig().toString()));
            }
        }
    }
}
