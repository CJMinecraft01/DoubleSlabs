package cjminecraft.doubleslabs.test.client.gui;

import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.container.ChestSlabContainer;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ChestSlabScreen extends GuiContainer {
    /**
     * The ResourceLocation containing the gui texture for the chest slab
     */
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation(DoubleSlabsTest.MODID, "textures/gui/container/chest_slab.png");

    private final InventoryPlayer playerInv;

    public ChestSlabScreen(InventoryPlayer inv, @Nullable ChestSlabTileEntity tile) {
        super(new ChestSlabContainer(inv, tile));
        this.playerInv = inv;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("gui.doubleslabstest.chest_slab"), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
