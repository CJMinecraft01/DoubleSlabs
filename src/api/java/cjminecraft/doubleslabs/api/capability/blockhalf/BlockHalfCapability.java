package cjminecraft.doubleslabs.api.capability.blockhalf;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BlockHalfCapability {

    @CapabilityInject(IBlockHalf.class)
    public static Capability<IBlockHalf> BLOCK_HALF = null;

}
