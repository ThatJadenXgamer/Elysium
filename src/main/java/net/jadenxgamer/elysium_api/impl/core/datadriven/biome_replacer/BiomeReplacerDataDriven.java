package net.jadenxgamer.elysium_api.impl.core.datadriven.biome_replacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.util.RegistryAccessHelper;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import static net.jadenxgamer.elysium_api.impl.core.biome.ElysiumBiomeHelper.*;

public record BiomeReplacerDataDriven(HolderSet<Biome> replaceBiomes, Holder<Biome> withBiome, double rarity, int size, ResourceLocation uniqueId, ResourceLocation dimension) {
    public static final Codec<BiomeReplacerDataDriven> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("replace_biomes").forGetter(BiomeReplacerDataDriven::replaceBiomes),
            Biome.CODEC.fieldOf("with_biome").forGetter(BiomeReplacerDataDriven::withBiome),
            Codec.DOUBLE.fieldOf("rarity").forGetter(BiomeReplacerDataDriven::rarity),
            Codec.INT.fieldOf("size").forGetter(BiomeReplacerDataDriven::size),
            ResourceLocation.CODEC.fieldOf("unique_id").forGetter(BiomeReplacerDataDriven::uniqueId),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(BiomeReplacerDataDriven::dimension)
    ).apply(instance, BiomeReplacerDataDriven::new));

    public static void addDataDrivenPossibleBiomes() {
        Registry<BiomeReplacerDataDriven> biomeReplacer = RegistryAccessHelper.getServerAccess().orElseThrow().registryOrThrow(ElysiumRegistries.BIOME_REPLACER);

        biomeReplacer.stream().forEach(replacer -> {
            if (replacer.dimension().equals(Elysium.idPath("minecraft", "overworld"))) {
                overworldPossibleBiomes.add(replacer.withBiome());
            }
            else if (replacer.dimension().equals(Elysium.idPath("minecraft", "the_nether"))) {
                netherPossibleBiomes.add(replacer.withBiome());
            }
        });
    }
}