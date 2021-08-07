package cjminecraft.doubleslabs.client.gui;

import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class WrappedScreen extends Screen implements MenuAccess<WrappedContainer>, ContainerListener {
    private Screen wrapped;
    private final WrappedContainer container;

    public WrappedScreen(WrappedContainer screenContainer, Inventory inv, Component titleIn) {
        super(titleIn);
        this.container = screenContainer;
        Inventory wrappedInv = new PlayerInventoryWrapper(inv, screenContainer.world);

        MenuScreens.getScreenFactory(screenContainer.wrapped.getType(), Minecraft.getInstance(), screenContainer.containerId, titleIn)
                .ifPresent(f -> wrapped = ((MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>)f).create(screenContainer.wrapped, wrappedInv, titleIn));
    }

    private Optional<Screen> getScreen() {
        return this.wrapped != null ? Optional.of(this.wrapped) : Optional.empty();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        getScreen().ifPresent(s -> s.render(stack, mouseX, mouseY, partialTicks));
    }

    @Override
    public Component getTitle() {
        return getScreen().map(Screen::getTitle).orElseGet(super::getTitle);
    }

    @Override
    public Component getNarrationMessage() {
        return getScreen().map(Screen::getNarrationMessage).orElseGet(super::getNarrationMessage);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return getScreen().map(s -> s.keyPressed(keyCode, scanCode, modifiers)).orElseGet(() -> super.keyPressed(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return getScreen().map(Screen::shouldCloseOnEsc).orElseGet(super::shouldCloseOnEsc);
    }

    @Override
    public void removed() {
        getScreen().ifPresent(Screen::removed);
        if (!getScreen().isPresent()) super.removed();
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        return getScreen().map(s -> s.getTooltipFromItem(itemStack)).orElseGet(() -> super.getTooltipFromItem(itemStack));
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        return getScreen().map(s -> s.handleComponentClicked(style)).orElseGet(() -> super.handleComponentClicked(style));
    }

    @Override
    protected void init() {
        super.init();
        getScreen().ifPresent(s -> s.init(this.minecraft, this.width, this.height));
    }

    @Override
    public List<? extends GuiEventListener> children() {
        // IntelliJ dislikes this but it's okay to compile
        return getScreen().map(Screen::children).orElseGet(super::children);
    }

    @Override
    public void tick() {
        getScreen().ifPresent(Screen::tick);
    }

    @Override
    public void onClose() {
        getScreen().ifPresent(Screen::onClose);
    }

    @Override
    public boolean isPauseScreen() {
        return getScreen().map(Screen::isPauseScreen).orElseGet(super::isPauseScreen);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        getScreen().ifPresent(s -> s.resize(minecraft, width, height));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getScreen().map(s -> s.isMouseOver(mouseX, mouseY)).orElseGet(() -> super.isMouseOver(mouseX, mouseY));
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return getScreen().map(Screen::getFocused).orElseGet(super::getFocused);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        getScreen().ifPresent(s -> s.setFocused(listener));
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        return getScreen().map(s -> s.getChildAt(mouseX, mouseY)).orElseGet(() -> super.getChildAt(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return getScreen().map(s -> s.mouseClicked(mouseX, mouseY, button)).orElseGet(() -> super.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return getScreen().map(s -> s.mouseReleased(mouseX, mouseY, button)).orElseGet(() -> super.mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseDragged(double mouseX1, double mouseY1, int button, double mouseX2, double mouseY2) {
        return getScreen().map(s -> s.mouseDragged(mouseX1, mouseY1, button, mouseX2, mouseY2)).orElseGet(() -> super.mouseDragged(mouseX1, mouseY1, button, mouseX2, mouseY2));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return getScreen().map(s -> s.mouseScrolled(mouseX, mouseY, amount)).orElseGet(() -> super.mouseScrolled(mouseX, mouseY, amount));
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return getScreen().map(s -> s.keyReleased(keyCode, scanCode, modifiers)).orElseGet(() -> super.keyReleased(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return getScreen().map(s -> s.charTyped(character, modifiers)).orElseGet(() -> super.charTyped(character, modifiers));
    }

    @Override
    public void setInitialFocus(@Nullable GuiEventListener listener) {
        super.setInitialFocus(listener);
        getScreen().ifPresent(s -> s.setInitialFocus(listener));
    }

    @Override
    public boolean changeFocus(boolean focus) {
        return getScreen().map(s -> s.changeFocus(focus)).orElseGet(() -> super.changeFocus(focus));
    }

    @Override
    public void mouseMoved(double xPos, double mouseY) {
        getScreen().ifPresent(s -> s.mouseMoved(xPos, mouseY));
    }

    @Override
    public WrappedContainer getMenu() {
        return this.container;
    }

    @Override
    public void slotChanged(AbstractContainerMenu container, int slot, ItemStack stack) {
        getScreen().filter(s -> s instanceof ContainerListener).ifPresent(s -> ((ContainerListener) s).slotChanged(container, slot, stack));
    }

    @Override
    public void dataChanged(AbstractContainerMenu container, int slot, int data) {
        getScreen().filter(s -> s instanceof ContainerListener).ifPresent(s -> ((ContainerListener) s).dataChanged(container, slot, data));
    }
}
