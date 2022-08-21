package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.IBlockEntities;
import cjminecraft.doubleslabs.forge.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.forge.common.init.DSRegistries;
import cjminecraft.doubleslabs.platform.services.IPlatformHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public List<IHorizontalSlabSupport> getHorizontalSlabSupports() {
        return DSRegistries.HORIZONTAL_SLAB_SUPPORTS.getEntries().stream().filter(o -> isModLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
    }

    @Override
    public List<IVerticalSlabSupport> getVerticalSlabSupports() {
        return DSRegistries.VERTICAL_SLAB_SUPPORTS.getEntries().stream().filter(o -> isModLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
    }

    @Override
    public List<IContainerSupport> getContainerSupports() {
        return DSRegistries.CONTAINER_SUPPORTS.getEntries().stream().filter(o -> isModLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
    }

    @Override
    public IBlockEntities getBlockEntities() {
        return DSBlockEntities.INSTANCE;
    }

    @Override
    public double getReachDistance(Player player) {
        return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
    }
}
