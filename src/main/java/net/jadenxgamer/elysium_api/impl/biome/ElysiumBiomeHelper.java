package net.jadenxgamer.elysium_api.impl.biome;

import net.jadenxgamer.elysium_api.api.biome.ElysiumBiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElysiumBiomeHelper {
    public static Set<Holder<Biome>> overworldPossibleBiomes = new HashSet<>();
    public static Set<Holder<Biome>> netherPossibleBiomes = new HashSet<>();
    public static final List<BiomeReplacer> overworldBiomes = new ArrayList<>();
    public static final List<BiomeReplacer> netherBiomes = new ArrayList<>();

    public static List<BiomeReplacer> biomesForDimension(ResourceKey<LevelStem> dimension) {
        if (dimension.equals(LevelStem.OVERWORLD)) {
            return overworldBiomes;
        }
        else if (dimension.equals(LevelStem.NETHER)) {
            return netherBiomes;
        }
        else return null;
    }

    public record BiomeReplacer(ResourceKey<LevelStem> dimension, Holder<Biome> biome, Holder<Biome> canReplace, double rarity, int size) {

    }
}
