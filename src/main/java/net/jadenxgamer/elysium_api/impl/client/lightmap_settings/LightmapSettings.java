package net.jadenxgamer.elysium_api.impl.client.lightmap_settings;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public record LightmapSettings(Vector3f skyLightColor, Vector3f blockLightColor, float ambientBrightnessMultiplier) {

    public static final Map<ResourceLocation, LightmapSettings> LIGHTMAP_SETTINGS = new HashMap<>();
    public static final Map<ResourceLocation, LightmapSettings> DIMENSION_LIGHTMAP_SETTINGS = new HashMap<>();

    public static LightmapSettings parseSetting(JsonObject json) {
        Vector3f skyColor = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f blockColor = new Vector3f(1.0f, 1.0f, 1.0f);
        float ambientBrightnessMultiplier = 1.0f;

        if (json.has("sky_light_color")) skyColor = parseHex(json.get("sky_light_color").getAsString());
        if (json.has("block_light_color")) blockColor = parseHex(json.get("block_light_color").getAsString());
        if (json.has("ambient_brightness_multiplier")) ambientBrightnessMultiplier = json.get("ambient_brightness_multiplier").getAsFloat();

        return new LightmapSettings(skyColor, blockColor, ambientBrightnessMultiplier);
    }

    private static Vector3f parseHex(String hexString) {
        if (hexString.startsWith("#")) hexString = hexString.substring(1);
        int color = Integer.parseInt(hexString, 16);
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new Vector3f(r, g, b);
    }
}