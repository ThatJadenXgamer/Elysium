package net.jadenxgamer.elysium_api.impl.client.fog_settings;

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
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FogSettingsManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static float currentStartMultiplier;
    private static float currentEndMultiplier;

    public FogSettingsManager() {
        super(GSON, "elysium_api/fog_settings");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        FogSettings.FOG_SETTINGS.clear();
        FogSettings.DIMENSION_FOG_SETTINGS.clear();
        for (JsonElement element : elements.values()) {
            try {
                JsonObject json = element.getAsJsonObject();
                FogSettings settings = FogSettings.parseSetting(json);

                addToMap(json, "dimensions", FogSettings.DIMENSION_FOG_SETTINGS, settings);
                addToMap(json, "biomes", FogSettings.FOG_SETTINGS, settings);
            } catch (Exception e) {
                Elysium.LOGGER.warn("Couldn't load fog settings: {}", e.getMessage());
            }
        }
    }

    public Pair<Float, Float> getSettings(Player player, float fogStart, float fogEnd) {
        if (player == null) return null;

        Level level = player.level();
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeId = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        FogSettings settings = FogSettings.FOG_SETTINGS.getOrDefault(biomeId, null);
        var defaultForDimension = getDefaultForDimension(level);

        float targetStartMultiplier = settings != null ? settings.fogStartMultiplier() : defaultForDimension.getLeft();
        float targetEndMultiplier = settings != null ? settings.fogEndMultiplier() : defaultForDimension.getRight();

        float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        currentStartMultiplier = Mth.lerp(delta * 0.05f, currentStartMultiplier, targetStartMultiplier);
        currentEndMultiplier = Mth.lerp(delta * 0.05f, currentEndMultiplier, targetEndMultiplier);

        return Pair.of(fogStart * currentStartMultiplier, fogEnd * currentEndMultiplier);
    }

    private static Pair<Float, Float> getDefaultForDimension(Level level) {
        var fallback = Pair.of(1.0f, 1.0f);
        FogSettings settings = FogSettings.DIMENSION_FOG_SETTINGS.get(level.dimension().location());
        return settings != null ? Pair.of(settings.fogStartMultiplier(), settings.fogEndMultiplier()) : fallback;
    }

    private void addToMap(JsonObject json, String key, Map<ResourceLocation, FogSettings> targetMap, FogSettings settings) {
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
}
