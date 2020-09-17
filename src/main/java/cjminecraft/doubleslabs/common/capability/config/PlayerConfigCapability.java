package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class PlayerConfigCapability {

    @CapabilityInject(IPlayerConfig.class)
    public static Capability<IPlayerConfig> PLAYER_CONFIG = null;

    public static final ResourceLocation PLAYER_CONFIG_RESOURCE_LOCATION = new ResourceLocation(DoubleSlabs.MODID, "player_config");

    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerConfig.class, new Capability.IStorage<IPlayerConfig>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IPlayerConfig> capability, IPlayerConfig instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IPlayerConfig> capability, IPlayerConfig instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, PlayerConfig::new);
    }

}
