package net.jadenxgamer.elysium_api.impl.client.assetdriven.lightmap_settings;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LightmapSettingsType implements StringRepresentable {
    GLOBAL("global"),
    BIOME("biome"),
    DIMENSION("dimension"),
    NOT_BIOME("not_biome"),
    NOT_DIMENSION("not_dimension");

    private final String name;

    LightmapSettingsType(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public static LightmapSettingsType byName(String name, LightmapSettingsType fallback) {
        for (LightmapSettingsType type : values()) {
            if (type.name.equals(name)) return type;
        }
        return fallback;
    }
}