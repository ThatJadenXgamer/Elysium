package net.jadenxgamer.elysium_api.api.biome;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import static net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper.*;

public class ElysiumBiomeRegistry {

    /**
     * Registers a BiomeReplacer for The Overworld
     * @param replaceBiome      - biome ResourceKey it can replace
     * @param withBiome         - the withBiome to swap out with
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param id                - unique named identifier for your BiomeReplacer
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceOverworldBiome(ResourceKey<Biome> replaceBiome, ResourceKey<Biome> withBiome, double rarity, int size, ResourceLocation id, RegistryAccess registryAccess) {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        HolderSet<Biome> replaceBiomeSet = HolderSet.direct(biomeRegistry.getHolderOrThrow(replaceBiome));
        overworldPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(withBiome));
        overworldBiomes.add(new BiomeReplacer(LevelStem.OVERWORLD, withBiome, replaceBiomeSet, rarity, size, id));
    }

    /**
     * Registers a BiomeReplacer for The Nether
     * @param replaceBiome      - biome ResourceKey it can replace
     * @param withBiome         - the withBiome to swap out with
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param id                - unique named identifier for your BiomeReplacer
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceNetherBiome(ResourceKey<Biome> replaceBiome, ResourceKey<Biome> withBiome, double rarity, int size, ResourceLocation id, RegistryAccess registryAccess) {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        HolderSet<Biome> replaceBiomeSet = HolderSet.direct(biomeRegistry.getHolderOrThrow(replaceBiome));
        netherPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(withBiome));
        netherBiomes.add(new BiomeReplacer(LevelStem.NETHER, withBiome, replaceBiomeSet, rarity, size, id));
    }

    /**
     * Registers a BiomeReplacer for The Overworld
     * @param replaceBiome      - biome TagKey it can replace
     * @param withBiome         - the withBiome to swap out with
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param id                - unique named identifier for your BiomeReplacer
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceOverworldBiome(TagKey<Biome> replaceBiome, ResourceKey<Biome> withBiome, double rarity, int size, ResourceLocation id, RegistryAccess registryAccess) {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        HolderSet<Biome> replaceBiomeSet = biomeRegistry.getOrCreateTag(replaceBiome);
        overworldPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(withBiome));
        overworldBiomes.add(new BiomeReplacer(LevelStem.OVERWORLD, withBiome, replaceBiomeSet, rarity, size, id));
    }

    /**
     * Registers a BiomeReplacer for The Nether
     * @param replaceBiome      - biome TagKey it can replace
     * @param withBiome         - the withBiome to swap out with
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param id                - unique named identifier for your BiomeReplacer
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceNetherBiome(TagKey<Biome> replaceBiome, ResourceKey<Biome> withBiome, double rarity, int size, ResourceLocation id, RegistryAccess registryAccess) {
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        HolderSet<Biome> replaceBiomeSet = biomeRegistry.getOrCreateTag(replaceBiome);
        netherPossibleBiomes.add(registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(withBiome));
        netherBiomes.add(new BiomeReplacer(LevelStem.NETHER, withBiome, replaceBiomeSet, rarity, size, id));
    }
}
