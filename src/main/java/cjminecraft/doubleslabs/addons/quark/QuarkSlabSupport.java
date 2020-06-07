package cjminecraft.doubleslabs.addons.quark;

import cjminecraft.doubleslabs.addons.VerticalSlabSupport;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class QuarkSlabSupport extends VerticalSlabSupport {

    public QuarkSlabSupport() {
        super("vazkii.quark.building.block.VerticalSlabBlock", "TYPE", "vazkii.quark.building.block.VerticalSlabBlock$VerticalSlabType");
    }
}
