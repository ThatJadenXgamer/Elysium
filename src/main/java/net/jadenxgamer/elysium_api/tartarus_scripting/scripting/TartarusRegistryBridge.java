package net.jadenxgamer.elysium_api.tartarus_scripting.scripting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class TartarusRegistryBridge {

    private static final String BLOCK = "BLOCK";
    private static final String ITEM = "ITEM";
    private static final String MOB_EFFECT = "MOB_EFFECT";
    private static final String FLUID = "FLUID";
    private static final String FLUID_TYPE = "FLUID_TYPE";
    private static final String ENTITY_TYPE = "ENTITY_TYPE";
    private static final String BLOCK_ENTITY_TYPE = "BLOCK_ENTITY_TYPE";
    private static final String SOUND_EVENT = "SOUND_EVENT";
    private static final String PARTICLE_TYPE = "PARTICLE_TYPE";
    private static final String POTION = "POTION";

    public final Map<ResourceLocation, Supplier<Object>> pendingBlocks = new LinkedHashMap<>();
    public final Map<ResourceLocation, Supplier<Object>> pendingItems = new LinkedHashMap<>();

    /**
     * Registers a deferred block or item from JavaScript.
     * The callback will be evaluated later during the actual registration event.
     *
     * @param type     Specify which registry the entry will be added to
     * @param id       ResourceLocation string (e.g., "minecraft:custom_block")
     * @param callback Rhino function that returns the registered entry's instance
     */
    public Object register(String type, String id, BaseFunction callback) {
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(callback, "Callback cannot be null");

        ResourceLocation location = ResourceLocation.parse(id);
        Supplier<Object> supplier = createSupplier(callback);

        switch (type) {
            case BLOCK -> pendingBlocks.put(location, supplier);
            case ITEM -> pendingItems.put(location, supplier);
            default -> throw new IllegalArgumentException("Invalid registry type: '" + type + "'");
        }
        return null;
    }

    /**
     * Retrieves an already-registered object from Minecraft's built-in registries.
     *
     * @param type Specify which registry you are trying to retrieve an entry from
     * @param id   ResourceLocation string (e.g., "minecraft:custom_block")
     * @return the registered Block or Item, or null if not found
     */
    public Object get(String type, String id) {
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");

        ResourceLocation location = ResourceLocation.parse(id);

        return switch (type) {
            case BLOCK -> BuiltInRegistries.BLOCK.get(location);
            case ITEM -> BuiltInRegistries.ITEM.get(location);
            case MOB_EFFECT -> BuiltInRegistries.MOB_EFFECT.get(location);
            case FLUID -> BuiltInRegistries.FLUID.get(location);
            case FLUID_TYPE -> NeoForgeRegistries.FLUID_TYPES.get(location);
            case ENTITY_TYPE -> BuiltInRegistries.ENTITY_TYPE.get(location);
            case BLOCK_ENTITY_TYPE -> BuiltInRegistries.BLOCK_ENTITY_TYPE.get(location);
            case SOUND_EVENT -> BuiltInRegistries.SOUND_EVENT.get(location);
            case PARTICLE_TYPE -> BuiltInRegistries.PARTICLE_TYPE.get(location);
            case POTION -> BuiltInRegistries.POTION.get(location);
            default -> null;
        };
    }

    private Supplier<Object> createSupplier(BaseFunction callback) {
        return () -> {
            Context cx = Context.enter();
            try {
                Scriptable scope = TartarusScriptManager.getGlobalScope();
                Object result = callback.call(cx, scope, scope, new Object[0]);
                return (result instanceof Wrapper wrapper) ? wrapper.unwrap() : result;
            } finally {
                Context.exit();
            }
        };
    }
}