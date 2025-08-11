package net.jadenxgamer.elysium_api.impl.client.fog;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FogManager {
    private static boolean canRenderCustomFog = false;
    private static float fogStart = 0.35f;
    private static float fogEnd = 1.0f;
    private static boolean isSphere = false;
    private static boolean hasCaveFog = false;
    private static int caveFogColor = 1118483;
    private static float caveFogDensity = 1.0f;
    private static boolean undergroundState = false;
    private static int lastUpdate = -1;
    private static float depthFactor = 0f;
    private static float depthColorFactor = 0f;
    private static float skyLight = 16f;

    private static final Map<ResourceLocation, FogSetting> FOG_SETTINGS = new HashMap<>();
    private static final Map<ResourceLocation, FogSetting> DEFAULTS = new HashMap<>();

    public static void tick(Player player) {
        Level level = player.level();
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        Biome biome = level.getBiome(pos).value();
        ResourceLocation biomeId = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);

        FogSetting setting = FOG_SETTINGS.getOrDefault(biomeId, getDefault(level));
        canRenderCustomFog = setting != null;
        if (setting == null) return;

        fogEnd = interpolateFloat(fogEnd, setting.fogEnd());
        fogStart = interpolateFloat(fogStart, setting.fogStart());
        isSphere = setting.isSphere();
        hasCaveFog = setting.hasCaveFog();
        caveFogDensity = interpolateFloat(caveFogDensity, setting.caveFogDensity());
        caveFogColor = setting.caveFogColor();

        float targetDepthFactor;
        float targetDepthFactorColor;
        if (!canRenderCaveFog()) {
            targetDepthFactor = 0f;
            targetDepthFactorColor = 0f;
        } else {
            if (player.tickCount - lastUpdate >= 7) {
                undergroundState = isUnderground(player, level);
                lastUpdate = player.tickCount;
            }
            skyLight = level.getBrightness(LightLayer.SKY, pos);

            float seaLevel = level.getSeaLevel();
            float y = (float) player.getY();

            targetDepthFactor = undergroundState ? clampDepth(seaLevel, y, 48) * (1 - skyLight / 15f) : 0f;
            targetDepthFactorColor = undergroundState ? clampDepth(seaLevel, y, -48) * (1 - skyLight / 15f) : 0f;
        }

        depthFactor = Mth.lerp(0.1f, depthFactor, targetDepthFactor);
        depthColorFactor = Mth.lerp(0.1f, depthColorFactor, targetDepthFactorColor);
    }

    private static FogSetting getDefault(Level level) {
        return DEFAULTS.getOrDefault(level.dimension().location(), null);
    }

    public static void applyFog(float viewDistance) {
        float start = fogStart;
        float end = fogEnd;

        if (hasCaveFog && depthFactor > 0.001f) {
            float density = depthFactor * caveFogDensity;
            end /= (1 + density * 1.2f);
            start *= 1 - (0.2f * density);
            start *= 1 - (skyLight / 30f) * density;
        }

        RenderSystem.setShaderFogStart(viewDistance * start);
        RenderSystem.setShaderFogEnd(viewDistance * end);
        RenderSystem.setShaderFogShape(isSphere ? FogShape.SPHERE : FogShape.CYLINDER);
    }

    public static void applyFogColor(ViewportEvent.ComputeFogColor event) {
        if (!hasCaveFog && depthColorFactor < 0.001f) return;

        float red = (caveFogColor >> 16 & 0xFF) / 255f;
        float green = (caveFogColor >> 8 & 0xFF) / 255f;
        float blue = (caveFogColor & 0xFF) / 255f;

        float blend = depthColorFactor;
        event.setRed(Mth.lerp(blend, event.getRed(), red));
        event.setGreen(Mth.lerp(blend, event.getGreen(), green));
        event.setBlue(Mth.lerp(blend, event.getBlue(), blue));
    }

    private static float clampDepth(float seaLevel, float y, int max) {
        return Mth.clamp((seaLevel - y) / (seaLevel + max), 0.0f, 1.0f);
    }

    private static boolean isUnderground(Player player, Level level) {
        if (player.isUnderWater()) return false;

        double x = player.getX();
        double y = player.getEyeY();
        double z = player.getZ();
        float seaLevel = level.getSeaLevel() - 0.25f;
        int checksum = 0;

        // looks around in a grid to ensure the player is in-fact underground
        // the sea-level check exists to prevent false-positives inside houses or sheer cliffs, but I want to try and add better detection for mountain caves n such
        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                int sampleX = Mth.floor(x + xOffset);
                int sampleZ = Mth.floor(z + zOffset);

                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, sampleX, sampleZ);

                if (y < surfaceY && y < seaLevel) {
                    checksum++;
                }
            }
        }

        return checksum >= 17;
    }

    private static float interpolateFloat(float current, float target) {
        if (Math.abs(current - target) < 0.001f) return target;
        float step = 0.05f / 20;
        return current < target ? Math.min(current + step, target) : Math.max(current - step, target);
    }

    public static boolean canRenderCustomFog() {
        return canRenderCustomFog;
    }

    public static boolean canRenderCaveFog() {
        Minecraft client = Minecraft.getInstance();
        return client.level != null && client.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL && client.gameRenderer.getMainCamera().getFluidInCamera() == FogType.NONE;
    }

    private record FogSetting(boolean isDefault, ResourceLocation defaultForDimension,
                              float fogStart, float fogEnd, boolean isSphere,
                              boolean hasCaveFog, int caveFogColor, float caveFogDensity) {}

    public static class FogSettingsReloadListener extends SimpleJsonResourceReloadListener {
        private static final Gson GSON = new Gson();

        public FogSettingsReloadListener() {
            super(GSON, "elysium_api/fog_settings");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> elements, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
            FOG_SETTINGS.clear();
            DEFAULTS.clear();

            for (JsonElement element : elements.values()) {
                try {
                    JsonObject json = element.getAsJsonObject();
                    FogSetting fogSetting = parseSetting(json);

                    if (json.has("default") && json.get("default").getAsBoolean()) {
                        ResourceLocation dimension = fogSetting.defaultForDimension();
                        if (dimension != null) DEFAULTS.put(dimension, fogSetting);
                        continue;
                    }

                    if (json.has("biomes")) {
                        JsonElement biomesElement = json.get("biomes");
                        if (biomesElement.isJsonArray()) {
                            for (JsonElement entry : biomesElement.getAsJsonArray()) {
                                ResourceLocation biome = ResourceLocation.tryParse(entry.getAsString());
                                if (biome != null) FOG_SETTINGS.put(biome, fogSetting);
                            }
                        } else if (biomesElement.isJsonPrimitive()) {
                            ResourceLocation biome = ResourceLocation.tryParse(biomesElement.getAsString());
                            if (biome != null) FOG_SETTINGS.put(biome, fogSetting);
                        }
                    }
                } catch (Exception e) {
                    Elysium.LOGGER.warn("Couldn't load fog settings: {}", e.getMessage());
                }
            }
        }

        private FogSetting parseSetting(JsonObject json) {
            boolean isDefault = json.has("default") && json.get("default").getAsBoolean();
            ResourceLocation defaultForDimension = json.has("default_for_dimension") ? ResourceLocation.parse(json.get("default_for_dimension").getAsString()) : null;
            float fogStart = json.get("fog_start").getAsFloat();
            float fogEnd = json.get("fog_end").getAsFloat();
            boolean isSphere = json.has("is_sphere") && json.get("is_sphere").getAsBoolean();
            boolean hasCaveFog = json.has("has_cave_fog") && json.get("has_cave_fog").getAsBoolean();
            int caveFogColor = json.has("cave_fog_color") ? json.get("cave_fog_color").getAsInt() : 1118483;
            float caveFogDensity = json.has("cave_fog_density") ? json.get("cave_fog_density").getAsFloat() : 1.0f;
            return new FogSetting(isDefault, defaultForDimension, fogStart, fogEnd, isSphere, hasCaveFog, caveFogColor, caveFogDensity);
        }
    }
}