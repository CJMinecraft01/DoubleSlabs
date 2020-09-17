package cjminecraft.doubleslabs.client.gui;

import cjminecraft.doubleslabs.common.container.WrappedContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class WrappedGui extends GuiContainer {
    private GuiScreen wrapped;

    public WrappedGui(WrappedContainer screenContainer, int x, int y, int z) {
        super(screenContainer);
        this.wrapped = (GuiScreen) NetworkRegistry.INSTANCE.getLocalGuiContainer(screenContainer.mod, screenContainer.player, screenContainer.id, screenContainer.world, x, y, z);
    }

    private Optional<GuiScreen> getGui() {
        return this.wrapped != null ? Optional.of(this.wrapped) : Optional.empty();
    }

    private Optional<GuiContainer> getGuiContainer() {
        return getGui().filter(s -> s instanceof GuiContainer).map(s -> (GuiContainer) s);
    }

    @Override
    public void initGui() {
        super.initGui();
        getGui().ifPresent(GuiScreen::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        getGui().ifPresent(s -> s.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    public void onGuiClosed() {
        if (getGuiContainer().isPresent())
            getGuiContainer().get().onGuiClosed();
        else
            super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return getGuiContainer().map(GuiContainer::doesGuiPauseGame).orElseGet(super::doesGuiPauseGame);
    }

    @Override
    public void updateScreen() {
        if (getGui().isPresent())
            getGui().get().updateScreen();
        else
            super.updateScreen();
    }

    @Nullable
    @Override
    public Slot getSlotUnderMouse() {
        return getGuiContainer().map(GuiContainer::getSlotUnderMouse).orElseGet(super::getSlotUnderMouse);
    }

    @Override
    public int getGuiLeft() {
        return getGuiContainer().map(GuiContainer::getGuiLeft).orElseGet(super::getGuiLeft);
    }

    @Override
    public int getGuiTop() {
        return getGuiContainer().map(GuiContainer::getGuiTop).orElseGet(super::getGuiTop);
    }

    @Override
    public int getXSize() {
        return getGuiContainer().map(GuiContainer::getXSize).orElseGet(super::getXSize);
    }

    @Override
    public int getYSize() {
        return getGuiContainer().map(GuiContainer::getYSize).orElseGet(super::getYSize);
    }

    @Override
    public List<String> getItemToolTip(ItemStack stack) {
        return getGui().map(s -> s.getItemToolTip(stack)).orElseGet(() -> super.getItemToolTip(stack));
    }

    @Override
    public void setFocused(boolean hasFocusedControlIn) {
        getGui().ifPresent(s -> s.setFocused(hasFocusedControlIn));
        super.setFocused(hasFocusedControlIn);
    }

    @Override
    public boolean isFocused() {
        return getGui().map(GuiScreen::isFocused).orElseGet(super::isFocused);
    }

    @Override
    public boolean handleComponentClick(ITextComponent component) {
        return getGui().map(s -> s.handleComponentClick(component)).orElseGet(() -> super.handleComponentClick(component));
    }

    @Override
    public void handleInput() throws IOException {
        if (getGui().isPresent())
            getGui().get().handleInput();
        else
            super.handleInput();
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (getGui().isPresent())
            getGui().get().handleMouseInput();
        else
            super.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        if (getGui().isPresent())
            getGui().get().handleKeyboardInput();
        else
            super.handleKeyboardInput();
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (getGui().isPresent())
            getGui().get().confirmClicked(result, id);
        else
            super.confirmClicked(result, id);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        getGui().ifPresent(s -> s.onResize(mcIn, w, h));
    }
}
