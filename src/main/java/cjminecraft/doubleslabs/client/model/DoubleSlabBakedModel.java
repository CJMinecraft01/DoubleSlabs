package cjminecraft.doubleslabs.client.model;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class DoubleSlabBakedModel implements IBakedModel {

    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(new ResourceLocation(DoubleSlabs.MODID, "double_slab"), "normal");

    private IBakedModel getFallback() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
//        System.out.println(state);
        if (state == null)
            return getFallback().getQuads(state, side, rand);
        IBlockState topState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.TOP);
        IBlockState bottomState = ((IExtendedBlockState) state).getValue(BlockDoubleSlab.BOTTOM);
//        topState = Blocks.PURPUR_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
//        bottomState = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
        IBakedModel topModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(topState);
        IBakedModel bottomModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(bottomState);
        List<BakedQuad> topQuads = new ArrayList<>(topModel.getQuads(topState, side, rand));
        topQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.DOWN);
        List<BakedQuad> bottomQuads = new ArrayList<>(bottomModel.getQuads(bottomState, side, rand));
        bottomQuads.removeIf(bakedQuad -> bakedQuad.getFace() == EnumFacing.UP);
        topQuads.addAll(bottomQuads);
        return topQuads;
    }

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
    public TextureAtlasSprite getParticleTexture() {
        return getFallback().getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getFallback().getOverrides();
    }
}
