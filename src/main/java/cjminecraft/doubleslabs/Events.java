package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ISlabSupport itemSupport = SlabSupport.getSupport(event.getItemStack(), event.getPlayer(), event.getHand());
            if (itemSupport == null)
                return;

            if (event.getPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                    return;
                Direction face = event.getFace();
                BlockState state = event.getWorld().getBlockState(pos);
                if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                    return;
                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                if (blockSupport == null) {
                    pos = pos.offset(face);
                    if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                        return;
                    state = event.getWorld().getBlockState(pos);
                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                        return;
                    if (!event.getPlayer().canPlayerEdit(pos.offset(face), face, event.getItemStack()))
                        return;
                    blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                    if (blockSupport == null)
                        return;
                    face = blockSupport.getHalf(event.getWorld(), pos, state) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                SlabType half = blockSupport.getHalf(event.getWorld(), pos, state);
                if (half == SlabType.DOUBLE)
                    return;

                if (!Config.REPLACE_SAME_SLAB.get() && blockSupport == itemSupport && blockSupport.areSame(event.getWorld(), pos, state, event.getItemStack()))
                    return;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
//                    BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(event.getPlayer(), event.getHand(), DistExecutor.runForDist(() -> () -> (BlockRayTraceResult) Minecraft.getInstance().objectMouseOver, () -> () -> event.getWorld().rayTraceBlocks(new RayTraceContext(event.getPlayer().getEyePosition(0f), event.getPlayer().getEyePosition(0f).add(event.getPlayer().getLookVec().scale(event.getPlayer().getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 3)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, event.getPlayer())))));
//                    BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, itemSupport.getStateFromStack(event.getItemStack(), context), half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);

                    BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), event.getPos(), event.getItemStack(), half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);

                    BlockState newState = Registrar.DOUBLE_SLAB.getDefaultState();

                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(slabState)))
                        return;

//                    if (!event.getWorld().checkBlockCollision(event.getPlayer().getBoundingBox().offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
                    if (event.getWorld().setBlockState(pos, newState, 11)) {
                        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) event.getWorld().getTileEntity(pos);
                        if (tile == null)
                            return;
                        tile.setTopState(half == SlabType.TOP ? state : slabState);
                        tile.setBottomState(half == SlabType.BOTTOM ? state : slabState);
                        SoundType soundtype = slabState.getSoundType(event.getWorld(), pos, event.getPlayer());
                        event.getWorld().playSound(event.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        if (!event.getPlayer().isCreative())
                            event.getItemStack().shrink(1);
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        event.setCanceled(true);
                        if (event.getPlayer() instanceof ServerPlayerEntity)
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) event.getPlayer(), pos, event.getItemStack());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderBlockHighlight(DrawBlockHighlightEvent.HighlightBlock event) {
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        BlockState state = Minecraft.getInstance().world.getBlockState(event.getTarget().getPos());
        // We are trying to render the block highlight for the double slab
        if (state.getBlock() == Registrar.DOUBLE_SLAB && !Minecraft.getInstance().player.abilities.isCreativeMode) {
            // Offset the position of the block for when we render
            double x = (double)event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
            double y = (double)event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
            double z = (double)event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);

            // Check if we are looking at the top slab or bottom slab
            if (event.getTarget().getHitVec().y - event.getTarget().getPos().getY() > 0.5) {
                // Draw the top slab bounding box
                WorldRenderer.drawBoundingBox(x, y + 0.5f, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
            } else {
                // Draw the bottom slab bounding box
                WorldRenderer.drawBoundingBox(x, y, z, x + 1, y + 0.5f, z + 1, 0, 0, 0, 0.4f);
            }

            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();

            // Don't draw the default block highlight
            event.setCanceled(true);
        }
    }

}
