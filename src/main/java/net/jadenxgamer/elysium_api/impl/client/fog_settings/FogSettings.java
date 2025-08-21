package net.jadenxgamer.elysium_api.impl.client.fog_settings;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record FogSettings(float fogStartMultiplier, float fogEndMultiplier) {

    public static final Map<ResourceLocation, FogSettings> FOG_SETTINGS = new HashMap<>();
    public static final Map<ResourceLocation, FogSettings> DIMENSION_FOG_SETTINGS = new HashMap<>();

    public static FogSettings parseSetting(JsonObject json) {
        float fogStartMultiplier = json.get("fog_start_multiplier").getAsFloat();
        float fogEndMultiplier = json.get("fog_end_multiplier").getAsFloat();
        return new FogSettings(fogStartMultiplier, fogEndMultiplier);
    }
}
