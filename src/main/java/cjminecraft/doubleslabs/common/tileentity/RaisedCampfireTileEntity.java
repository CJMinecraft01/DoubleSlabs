package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.common.init.DSTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RaisedCampfireTileEntity extends CampfireBlockEntity {

    public RaisedCampfireTileEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return DSTiles.CAMPFIRE.get();
    }
}
