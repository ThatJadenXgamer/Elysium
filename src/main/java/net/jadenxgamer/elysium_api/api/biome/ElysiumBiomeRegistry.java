package net.jadenxgamer.elysium_api.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import static net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper.*;

public class ElysiumBiomeRegistry {

    /**
     * Registers a BiomeReplacer for The Overworld
     * @param biome             - the biome to add
     * @param canReplace        - biomes it can replace
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceOverworldBiome(ResourceKey<Biome> biome, ResourceKey<Biome> canReplace, double rarity, int size, RegistryAccess registryAccess) {
        Holder<Biome> biomeHolder = registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(biome);
        Holder<Biome> replaceHolder = registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(canReplace);
        overworldPossibleBiomes.add(biomeHolder);
        overworldBiomes.add(new BiomeReplacer(LevelStem.OVERWORLD, biomeHolder, replaceHolder, rarity, size));
    }

    /**
     * Registers a BiomeReplacer for The Nether
     * @param biome             - the biome to add
     * @param canReplace        - biomes it can replace
     * @param rarity            - rarity of this BiomeReplacer
     * @param size              - replacement radius of this BiomeReplacer (values lower than 12 may produce micro-biomes)
     * @param registryAccess    - pass in a RegistryAccess, preferably from ServerAboutToStartEvent
     */
    public static void replaceNetherBiome(ResourceKey<Biome> biome, ResourceKey<Biome> canReplace, double rarity, int size, RegistryAccess registryAccess) {
        Holder<Biome> biomeHolder = registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(biome);
        Holder<Biome> replaceHolder = registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(canReplace);
        netherPossibleBiomes.add(biomeHolder);
        netherBiomes.add(new BiomeReplacer(LevelStem.NETHER, biomeHolder, replaceHolder, rarity, size));
    }
}
