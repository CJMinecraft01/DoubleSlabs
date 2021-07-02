package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cjminecraft.doubleslabs.client.ClientConstants.getFallbackModel;

public class VerticalSlabItemBakedModel implements IBakedModel {

    public static VerticalSlabItemBakedModel INSTANCE;

    private final ItemStack stack;
    private final IBakedModel baseModel;

    public IBakedModel getBaseModel() {
        return this.baseModel;
    }

    public VerticalSlabItemBakedModel(IBakedModel baseModel) {
        this(baseModel, ItemStack.EMPTY);
    }

    public VerticalSlabItemBakedModel(IBakedModel baseModel, ItemStack stack) {
        this.baseModel = baseModel;
        this.stack = stack;
    }

    private Block getBlock() {
        return this.stack.getItem() instanceof ItemBlock ? ((ItemBlock) this.stack.getItem()).getBlock() : Blocks.AIR;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return getFallbackModel().getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        if (this == INSTANCE)
            return new DynamicItemOverrideList();
        return ItemOverrideList.NONE;
    }

    private static class DynamicItemOverrideList extends ItemOverrideList {

        private HashMap<ItemStack, IBakedModel> cache = new HashMap<>();

        public DynamicItemOverrideList() {
            super(new ArrayList<>());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            return ClientConstants.getVerticalModel(VerticalSlabItem.getStack(stack));
        }
    }
}
