package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleSlabBakedModel implements IDynamicBakedModel {

    private IBakedModel getFallback() {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

//    @Override
//    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
////        System.out.println(state);
//        if (state == null)
//            return getFallback().getQuads(state, side, rand);
//        IBlockState topState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.TOP);
//        IBlockState bottomState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.BOTTOM);
////        topState = Blocks.PURPUR_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
////        bottomState = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
//        IBakedModel topModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(topState);
//        IBakedModel bottomModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(bottomState);
//        List<BakedQuad> topQuads = new ArrayList<>(topModel.getQuads(topState, side, rand));
//        topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
//        List<BakedQuad> bottomQuads = new ArrayList<>(bottomModel.getQuads(bottomState, side, rand));
//        bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
//        topQuads.addAll(bottomQuads);
//        return topQuads;
//    }

    @Override
    public boolean isAmbientOcclusion() {
        return getFallback().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getFallback().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getFallback().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        if (data.hasProperty(TileEntityDoubleSlab.TOP_STATE))
            return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(data.getData(TileEntityDoubleSlab.TOP_STATE)).getParticleTexture(data);
        return getFallback().getParticleTexture(data);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return getFallback().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getFallback().getOverrides();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (extraData.hasProperty(TileEntityDoubleSlab.TOP_STATE) && extraData.hasProperty(TileEntityDoubleSlab.BOTTOM_STATE)) {
            BlockState topState = extraData.getData(TileEntityDoubleSlab.TOP_STATE);
            BlockState bottomState = extraData.getData(TileEntityDoubleSlab.BOTTOM_STATE);
            IBakedModel topModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(topState);
            IBakedModel bottomModel = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(bottomState);
            List<BakedQuad> topQuads = new ArrayList<>(topModel.getQuads(topState, side, rand, extraData));
            topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.DOWN);
            List<BakedQuad> bottomQuads = new ArrayList<>(bottomModel.getQuads(bottomState, side, rand, extraData));
            bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == Direction.UP);
            topQuads.addAll(bottomQuads);
            return topQuads;
        }
        return getFallback().getQuads(state, side, rand, extraData);

//        topState = Blocks.PURPUR_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
//        bottomState = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
    }

//    @Override
//    public ItemOverrideList getOverrides() {
//        return getFallback().getOverrides();
//    }
}
