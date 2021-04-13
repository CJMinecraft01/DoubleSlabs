package cjminecraft.doubleslabs.client.gui;

import cjminecraft.doubleslabs.api.PlayerInventoryWrapper;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class WrappedScreen extends Screen implements IHasContainer<WrappedContainer> {
    private Screen wrapped;
    private final WrappedContainer container;

    public WrappedScreen(WrappedContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(titleIn);
        this.container = screenContainer;
//        PlayerInventory wrappedInv = new PlayerInventoryWrapper(inv, screenContainer.world);
        ScreenManager.getScreenFactory(screenContainer.wrapped.getType(), Minecraft.getInstance(), screenContainer.windowId, titleIn)
                .ifPresent(f -> wrapped = ((ScreenManager.IScreenFactory<Container, ?>)f).create(screenContainer.wrapped, inv, titleIn));
    }

    private Optional<Screen> getScreen() {
        return this.wrapped != null ? Optional.of(this.wrapped) : Optional.empty();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        getScreen().ifPresent(s -> s.render(matrixStack, mouseX, mouseY, partialTicks));
    }

    @Override
    public ITextComponent getTitle() {
        return getScreen().map(Screen::getTitle).orElseGet(super::getTitle);
    }

    @Override
    public String getNarrationMessage() {
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
    public void closeScreen() {
        getScreen().ifPresent(Screen::closeScreen);
        if (!getScreen().isPresent()) super.closeScreen();
    }

    @Override
    public List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
        return getScreen().map(s -> s.getTooltipFromItem(itemStack)).orElseGet(() -> super.getTooltipFromItem(itemStack));
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        return getScreen().map(s -> s.handleComponentClicked(style)).orElseGet(() -> super.handleComponentClicked(style));
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        getScreen().ifPresent(s -> s.init(minecraft, width, height));
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        // IntelliJ dislikes this but it's okay to compile
        return getScreen().map(Screen::getEventListeners).orElseGet(super::getEventListeners);
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

    @Override
    public void func_230476_a_(List<Path> paths) {
        getScreen().ifPresent(s -> s.func_230476_a_(paths));
    }

    @Nullable
    @Override
    public IGuiEventListener getListener() {
        return getScreen().map(Screen::getListener).orElseGet(super::getListener);
    }

    @Override
    public void setListener(@Nullable IGuiEventListener listener) {
        super.setListener(listener);
        getScreen().ifPresent(s -> s.setListener(listener));
    }

    @Override
    public Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY) {
        return getScreen().map(s -> s.getEventListenerForPos(mouseX, mouseY)).orElseGet(() -> super.getEventListenerForPos(mouseX, mouseY));
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
    public void setFocusedDefault(@Nullable IGuiEventListener eventListener) {
        super.setFocusedDefault(eventListener);
        getScreen().ifPresent(s -> s.setFocusedDefault(eventListener));
    }

    @Override
    public void setListenerDefault(@Nullable IGuiEventListener eventListener) {
        super.setListenerDefault(eventListener);
        getScreen().ifPresent(s -> s.setListenerDefault(eventListener));
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
    public WrappedContainer getContainer() {
        return this.container;
    }
}
