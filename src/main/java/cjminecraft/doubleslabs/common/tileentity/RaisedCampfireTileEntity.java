package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.common.init.DSTiles;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class RaisedCampfireTileEntity extends CampfireTileEntity {

    @Override
    public TileEntityType<?> getType() {
        return DSTiles.CAMPFIRE.get();
    }
}
