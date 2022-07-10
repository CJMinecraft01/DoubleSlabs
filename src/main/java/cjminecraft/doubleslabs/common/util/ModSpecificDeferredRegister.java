package cjminecraft.doubleslabs.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class ModSpecificDeferredRegister<T> {

    private final DeferredRegister<T> register;
    private final String modid;

    public ModSpecificDeferredRegister(DeferredRegister<T> register, String modid) {
        this.register = register;
        this.modid = modid;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup  A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
        return this.register.register(name, sup);
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name          The new entry's name, it will automatically have the modid prefixed.
     * @param sup           A factory for the new entry, it should return a new instance every time it is called.
     * @param requiredModid The modid of the mod required before registering this entry
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup, String requiredModid) {
        RegistryObject<I> obj = this.register.register(name, sup);
        if (!ModList.get().isLoaded(requiredModid)) {
            // If the mod isn't loaded, remove the entry from the list of entries
            this.register.getEntries().remove(obj);
        }
        return obj;
    }

    /**
     * Only used for custom registries to fill the forge registry held in this DeferredRegister.
     * <p>
     * Calls {@link RegistryBuilder#setName} automatically.
     *
     * @param sup Supplier of a RegistryBuilder that initializes a {@link IForgeRegistry} during the {@link NewRegistryEvent} event
     * @return A supplier of the {@link IForgeRegistry} created by the builder.
     * Will always return null until after the {@link NewRegistryEvent} event fires.
     */
    public Supplier<IForgeRegistry<T>> makeRegistry(final Supplier<RegistryBuilder<T>> sup) {
        return this.register.makeRegistry(sup);
    }

    /**
     * Creates a tag key based on the current modid and provided path as the location and the registry name linked to this DeferredRegister.
     * To control the namespace, use {@link #createTagKey(ResourceLocation)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     *                               Use the factories that take {@link DeferredRegister#create(ResourceLocation, String) a registry name} or {@link DeferredRegister#create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(String, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull String path) {
        return this.register.createTagKey(path);
    }

    /**
     * Creates a tag key based on the provided resource location and the registry name linked to this DeferredRegister.
     * To use the current modid as the namespace, use {@link #createTagKey(String)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     *                               Use the factories that take {@link DeferredRegister#create(ResourceLocation, String) a registry name} or {@link DeferredRegister#create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull ResourceLocation location) {
        return this.register.createTagKey(location);
    }

    /**
     * Creates a tag key with the current modid and provided path that will use the set of defaults if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * To control the namespace, use {@link #createOptionalTagKey(ResourceLocation, Set)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     *                               Use the factories that take {@link DeferredRegister#create(ResourceLocation, String) a registry name} or {@link DeferredRegister#create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     * @see #addOptionalTagDefaults(TagKey, Set)
     */
    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull String path, @NotNull Set<? extends Supplier<T>> defaults) {
        return this.register.createOptionalTagKey(path, defaults);
    }

    /**
     * Creates a tag key with the provided location that will use the set of defaults if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * To use the current modid as the namespace, use {@link #createOptionalTagKey(String, Set)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     *                               Use the factories that take {@link DeferredRegister#create(ResourceLocation, String) a registry name} or {@link DeferredRegister#create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(String, Set)
     * @see #addOptionalTagDefaults(TagKey, Set)
     */
    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<T>> defaults) {
        return this.register.createOptionalTagKey(location, defaults);
    }

    /**
     * Adds defaults to an existing tag key.
     * The set of defaults will be bound to the tag if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     *
     * @throws IllegalStateException If the registry name was not set.
     *                               Use the factories that take {@link DeferredRegister#create(ResourceLocation, String) a registry name} or {@link DeferredRegister#create(IForgeRegistry, String) forge registry}.
     * @see #createOptionalTagKey(String, Set)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     */
    public void addOptionalTagDefaults(@NotNull TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults) {
        this.register.addOptionalTagDefaults(name, defaults);
    }

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus) {
        this.register.register(bus);
    }

    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries() {
        return this.register.getEntries();
    }

    /**
     * @return The registry name stored in this deferred register. Useful for creating new deferred registers based on an existing one.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return this.register.getRegistryName();
    }

}
