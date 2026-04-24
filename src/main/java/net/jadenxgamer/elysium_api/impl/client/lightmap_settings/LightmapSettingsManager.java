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
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Map;

public class LightmapSettingsManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    private static final Vector3f currentSkyColor = new Vector3f(1.0f, 1.0f, 1.0f);
    private static final Vector3f currentBlockColor = new Vector3f(1.0f, 1.0f, 1.0f);
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

                if (json.has("dimensions")) {
                    JsonElement dimensionsElement = json.get("dimensions");
                    if (dimensionsElement.isJsonArray()) {
                        for (JsonElement entry : dimensionsElement.getAsJsonArray()) {
                            ResourceLocation dimension = ResourceLocation.tryParse(entry.getAsString());
                            if (dimension != null) LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS.put(dimension, settings);
                        }
                    } else if (dimensionsElement.isJsonPrimitive()) {
                        ResourceLocation dimension = ResourceLocation.tryParse(dimensionsElement.getAsString());
                        if (dimension != null) LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS.put(dimension, settings);
                    }
                } else if (json.has("biomes")) {
                    JsonElement biomesElement = json.get("biomes");
                    if (biomesElement.isJsonArray()) {
                        for (JsonElement entry : biomesElement.getAsJsonArray()) {
                            ResourceLocation biome = ResourceLocation.tryParse(entry.getAsString());
                            if (biome != null) LightmapSettings.LIGHTMAP_SETTINGS.put(biome, settings);
                        }
                    } else if (biomesElement.isJsonPrimitive()) {
                        ResourceLocation biome = ResourceLocation.tryParse(biomesElement.getAsString());
                        if (biome != null) LightmapSettings.LIGHTMAP_SETTINGS.put(biome, settings);
                    }
                }
            } catch (Exception e) {
                Elysium.LOGGER.warn("Couldn't load lightmap settings: {}", e.getMessage());
            }
        }
    }

    public Pair<Vector3f, Vector3f> getSettings(Player player) {
        if (player == null) {
            return Pair.of(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
        }

        Level level = player.level();
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeId = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);

        LightmapSettings settings = LightmapSettings.LIGHTMAP_SETTINGS.get(biomeId);
        var defaultColors = getDefaultForDimension(level);

        Vector3f targetSky = settings != null ? settings.skyLightColor() : defaultColors.getLeft();
        Vector3f targetBlock = settings != null ? settings.blockLightColor() : defaultColors.getRight();

        float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        currentSkyColor.lerp(targetSky, delta * 0.01f);
        currentBlockColor.lerp(targetBlock, delta * 0.01f);

        return Pair.of(currentSkyColor, currentBlockColor);
    }

    private static Pair<Vector3f, Vector3f> getDefaultForDimension(Level level) {
        var fallback = Pair.of(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
        LightmapSettings settings = LightmapSettings.DIMENSION_LIGHTMAP_SETTINGS.get(level.dimension().location());
        return settings != null ? Pair.of(settings.skyLightColor(), settings.blockLightColor()) : fallback;
    }


    // It's based on how Polytone solves gui being affected by the lightmaps
    // Elysium will do absolutely nothing if Polytone is present and will just let that mod handle this
    // https://github.com/MehVahdJukaar/polytone/blob/1.21.1/common/src/main/java/net/mehvahdjukaar/polytone/lightmap/LightmapsManager.java#L161
    public void setupForGUI(boolean gui) {
        usingGuiLightmap = gui;
    }

    public boolean isGui() {
        return usingGuiLightmap;
    }
}