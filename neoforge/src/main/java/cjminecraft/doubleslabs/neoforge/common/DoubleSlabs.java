package cjminecraft.doubleslabs.neoforge.common;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlockEntities;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeBlocks;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeItems;
import cjminecraft.doubleslabs.neoforge.common.init.DSNeoForgeRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleSlabs {

    public DoubleSlabs(IEventBus modBus) {
        DSNeoForgeBlocks.BLOCKS.register(modBus);
        DSNeoForgeItems.ITEMS.register(modBus);
        DSNeoForgeItems.DATA_COMPONENTS.register(modBus);
        DSNeoForgeItems.CREATIVE_MODE_TABS.register(modBus);
        DSNeoForgeBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        DSNeoForgeRecipes.RECIPE_SERIALIZERS.register(modBus);
        Internal.initialise();
    }
}