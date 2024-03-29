package cjminecraft.doubleslabs.common.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import java.util.*;
import java.util.function.Supplier;

public class ModSpecificDeferredRegister<T extends IForgeRegistryEntry<T>> {
    private final Class<T> superType;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());
    private IForgeRegistry<T> type;
    private Supplier<RegistryBuilder<T>> registryFactory;
    private boolean seenRegisterEvent = false;
    private ModSpecificDeferredRegister(Class<T> base, String modid) {
        this.superType = base;
        this.modid = modid;
    }
    private ModSpecificDeferredRegister(IForgeRegistry<T> reg, String modid) {
        this(reg.getRegistrySuperType(), modid);
        this.type = reg;
    }

    /**
     * Use for vanilla/forge registries. See example above.
     */
    public static <B extends IForgeRegistryEntry<B>> ModSpecificDeferredRegister<B> create(IForgeRegistry<B> reg, String modid) {
        return new ModSpecificDeferredRegister<B>(reg, modid);
    }

    /**
     * Use for custom registries that are made during the NewRegistry event.
     */
    public static <B extends IForgeRegistryEntry<B>> ModSpecificDeferredRegister<B> create(Class<B> base, String modid) {
        return new ModSpecificDeferredRegister<B>(base, modid);
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup  A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
        if (seenRegisterEvent)
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegistryEvent.Register has been fired.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);

        RegistryObject<I> ret;
        if (this.type != null)
            ret = RegistryObject.of(key, this.type);
        else if (this.superType != null)
            ret = RegistryObject.of(key, this.superType, this.modid);
        else
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");

        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return ret;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup  A factory for the new entry, it should return a new instance every time it is called.
     * @param requiredModid The modid that is required for this object to be registered
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup, final String requiredModid) {
        if (seenRegisterEvent)
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegistryEvent.Register has been fired.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);

        RegistryObject<I> ret;
        if (this.type != null)
            ret = RegistryObject.of(key, this.type);
        else if (this.superType != null)
            ret = RegistryObject.of(key, this.superType, this.modid);
        else
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");

        if (!ModList.get().isLoaded(requiredModid))
            return ret;

        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return ret;
    }

    /**
     * For custom registries only, fills the {@link #registryFactory} to be called later see {@link #register(IEventBus)}
     * <p>
     * Calls {@link RegistryBuilder#setName} and {@link RegistryBuilder#setType} automatically.
     *
     * @param name Path of the registry's {@link ResourceLocation}
     * @param sup  Supplier of the RegistryBuilder that is called to fill {@link #type} during the NewRegistry event
     * @return A supplier of the {@link IForgeRegistry} created by the builder.
     */
    public Supplier<IForgeRegistry<T>> makeRegistry(final String name, final Supplier<RegistryBuilder<T>> sup) {
        if (this.superType == null)
            throw new IllegalStateException("Cannot create a registry without specifying a base type");
        if (this.type != null || this.registryFactory != null)
            throw new IllegalStateException("Cannot create a registry for a type that already exists");

        this.registryFactory = () -> sup.get().setName(new ResourceLocation(modid, name)).setType(this.superType);
        return () -> this.type;
    }

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
        if (this.type == null) {
            if (this.registryFactory != null)
                bus.addListener(this::createRegistry);
            else
                bus.addListener(EventPriority.LOWEST, this::captureRegistry);
        }
    }

    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries() {
        return entriesView;
    }

    private void addEntries(RegistryEvent.Register<?> event) {
        if (this.type != null && event.getGenericType() == this.type.getRegistrySuperType()) {
            this.seenRegisterEvent = true;
            @SuppressWarnings("unchecked")
            IForgeRegistry<T> reg = (IForgeRegistry<T>) event.getRegistry();
            for (Map.Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet()) {
                reg.register(e.getValue().get());
                e.getKey().updateReference(reg);
            }
        }
    }

    private void createRegistry(RegistryEvent.NewRegistry event) {
        this.type = this.registryFactory.get().create();
    }

    private void captureRegistry(RegistryEvent.NewRegistry event) {
        if (this.superType != null) {
            this.type = RegistryManager.ACTIVE.getRegistry(this.superType);
            if (this.type == null)
                throw new IllegalStateException("Unable to find registry for type " + this.superType.getName() + " for modid \"" + modid + "\" after NewRegistry event");
        } else
            throw new IllegalStateException("Unable to find registry for mod \"" + modid + "\" No lookup criteria specified.");
    }
}