package net.jadenxgamer.elysium_api.impl.client.lightmap_settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;

public class LightmapSettingsManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    private static final Vector3f currentSkyColor = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final Vector3f currentBlockColor = new Vector3f(1.0f, 1.0f, 1.0f);
    private static float currentAmbientBrightness = 1.0f;
    public static final ResourceLocation GUI_LIGHTMAP = Elysium.elysiumPath("textures/misc/gui.png");
    private boolean usingGuiLightmap = false;

    public LightmapSettingsManager() {
        super(GSON, "elysium_api/lightmap_settings");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        LightmapSettings.LIGHTMAP_SETTINGS.clear();
        LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS.clear();
        for (JsonElement element : elements.values()) {
            try {
                JsonObject json = element.getAsJsonObject();
                LightmapSettings settings = LightmapSettings.parseSetting(json);

                addToMap(json, "dimensions", LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS, settings);
                addToMap(json, "biomes", LightmapSettings.LIGHTMAP_SETTINGS, settings);
            } catch (Exception e) {
                Elysium.LOGGER.warn("Couldn't load lightmap settings: {}", e.getMessage());
            }
        }
    }

    public Triple<Vector3f, Vector3f, Float> getSettings(Player player) {
        if (player == null) return Triple.of(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);

        Level level = player.level();
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeId = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);

        LightmapSettings settings = LightmapSettings.LIGHTMAP_SETTINGS.get(biomeId);
        var defaultForDimension = getDefaultForDimension(level);

        Vector3f targetSky = settings != null ? settings.skyLightColor() : defaultForDimension.getLeft();
        Vector3f targetBlock = settings != null ? settings.blockLightColor() : defaultForDimension.getMiddle();
        float targetBrightness = settings != null ? settings.ambientBrightnessMultiplier() : defaultForDimension.getRight();

        float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        currentSkyColor.lerp(targetSky, delta * 0.03f);
        currentBlockColor.lerp(targetBlock, delta * 0.03f);
        currentAmbientBrightness = Mth.lerp(delta * 0.03f, currentAmbientBrightness, targetBrightness);

        return Triple.of(currentSkyColor, currentBlockColor, currentAmbientBrightness);
    }

    private static Triple<Vector3f, Vector3f, Float> getDefaultForDimension(Level level) {
        var fallback = Triple.of(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
        LightmapSettings settings = LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS.get(level.dimension().location());
        return settings != null ? Triple.of(settings.skyLightColor(), settings.blockLightColor(), settings.ambientBrightnessMultiplier()) : fallback;
    }

    private void addToMap(JsonObject json, String key, Map<ResourceLocation, LightmapSettings> targetMap, LightmapSettings settings) {
        if (!json.has(key)) return;

        JsonElement element = json.get(key);
        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                ResourceLocation location = ResourceLocation.tryParse(entry.getAsString());
                if (location != null) targetMap.put(location, settings);
            }
        } else if (element.isJsonPrimitive()) {
            ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
            if (location != null) targetMap.put(location, settings);
        }
    }

    // It's based on how Polytone solves gui being affected by the lightmaps
    // Elysium will do absolutely nothing if Polytone is present, and will just let that mod handle this
    // https://github.com/MehVahdJukaar/polytone/blob/1.21.1/common/src/main/java/net/mehvahdjukaar/polytone/lightmap/LightmapsManager.java#L161
    public void setupForGUI(boolean gui) {
        usingGuiLightmap = gui;
    }

    public boolean isGui() {
        return usingGuiLightmap;
    }
}