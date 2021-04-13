package cjminecraft.doubleslabs.common.capability;

import cjminecraft.doubleslabs.api.capability.blockhalf.BlockHalf;
import cjminecraft.doubleslabs.api.capability.blockhalf.IBlockHalf;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class DoubleSlabsCapabilities {

    public static final ResourceLocation PLAYER_CONFIG_RESOURCE_LOCATION = new ResourceLocation(DoubleSlabs.MODID, "player_config");
    public static final ResourceLocation BLOCK_HALF_RESOURCE_LOCATION = new ResourceLocation(DoubleSlabs.MODID, "block_half");

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerConfig.class, new Capability.IStorage<IPlayerConfig>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IPlayerConfig> capability, IPlayerConfig instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IPlayerConfig> capability, IPlayerConfig instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, PlayerConfig::new);

        CapabilityManager.INSTANCE.register(IBlockHalf.class, new Capability.IStorage<IBlockHalf>() {

            @Nullable
            @Override
            public INBT writeNBT(Capability<IBlockHalf> capability, IBlockHalf instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IBlockHalf> capability, IBlockHalf instance, Direction side, INBT nbt) {
                instance.deserializeNBT((ByteNBT) nbt);
            }
        }, BlockHalf::new);
    }

}
