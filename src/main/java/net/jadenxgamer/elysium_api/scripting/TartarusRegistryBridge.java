package net.jadenxgamer.elysium_api.scripting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
            default -> throw new IllegalArgumentException("Invalid registry type: '" + type + "'. Must be 'BLOCK' or 'ITEM' (screaming case).");
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