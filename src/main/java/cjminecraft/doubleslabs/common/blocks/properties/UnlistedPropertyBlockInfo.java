package cjminecraft.doubleslabs.common.blocks.properties;

import cjminecraft.doubleslabs.api.IBlockInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyBlockInfo implements IUnlistedProperty<IBlockInfo> {
    @Override
    public String getName() {
        return "block_info";
    }

    @Override
    public boolean isValid(IBlockInfo value) {
        return true;
    }

    @Override
    public Class<IBlockInfo> getType() {
        return IBlockInfo.class;
    }

    @Override
    public String valueToString(IBlockInfo value) {
        return value.toString();
    }
}
