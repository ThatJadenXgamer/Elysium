package net.jadenxgamer.elysium_api.impl.core.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.jadenxgamer.elysium_api.api.util.RegistryAccessHelper;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class MosaicBiomeSource extends BiomeSource {

    public static final MapCodec<MosaicBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("grid_cell_size").forGetter(s -> s.gridCellSize),
            Codec.INT.fieldOf("climate_count").forGetter(s -> s.climateCount),
            Codec.FLOAT.optionalFieldOf("jitter_strength", 0.35f).forGetter(s -> s.jitterStrength),
            Codec.BOOL.optionalFieldOf("avoid_diagonal_neighbors", false).forGetter(s -> s.avoidDiagonalNeighbors),

            Codec.DOUBLE.optionalFieldOf("distortion_strength", 24.0).forGetter(s -> s.distortionStrength),
            Codec.DOUBLE.optionalFieldOf("distortion_scale", 0.016).forGetter(s -> s.distortionScale),
            Codec.INT.optionalFieldOf("warp_iterations", 1).forGetter(s -> s.warpIterations),
            Codec.INT.optionalFieldOf("noise_octaves", 3).forGetter(s -> s.noiseOctaves),
            TagKey.codec(Registries.BIOME).optionalFieldOf("auto_populate_entries_from_tag").forGetter(s -> s.autoPopulateEntriesFromTag),
            TagKey.codec(Registries.BIOME).optionalFieldOf("biome_exclusion_tag").forGetter(s -> s.biomeExclusionTag)
    ).apply(instance, MosaicBiomeSource::new));

    private final int gridCellSize;
    private final int climateCount;
    private final float jitterStrength;
    private final boolean avoidDiagonalNeighbors;
    private final Optional<TagKey<Biome>> autoPopulateEntriesFromTag;
    private final Optional<TagKey<Biome>> biomeExclusionTag;

    private final double distortionStrength;
    private final double distortionScale;
    private final int warpIterations;
    private final int noiseOctaves;

    private final double halfCellSize;
    private final double jitterRange;

    private volatile boolean isInitialized = false;
    private long worldSeed;
    private long validClimatesMask = 0L;

    private WeightedBiomeList[] climateEntries;

    private PerlinNoise[][] warpNoisesX;
    private PerlinNoise[][] warpNoisesZ;

    private static final ThreadLocal<ClimateCache> CLIMATE_RESOLUTION_CACHE = ThreadLocal.withInitial(ClimateCache::new);

    public MosaicBiomeSource(int gridCellSize, int climateCount, float jitterStrength, boolean avoidDiagonalNeighbors,
                             double distortionStrength, double distortionScale, int warpIterations, int noiseOctaves,
                             Optional<TagKey<Biome>> autoPopulateEntriesFromTag,
                             Optional<TagKey<Biome>> biomeExclusionTag) {
        this.gridCellSize = gridCellSize;
        this.climateCount = climateCount;
        this.jitterStrength = jitterStrength;
        this.avoidDiagonalNeighbors = avoidDiagonalNeighbors;
        this.autoPopulateEntriesFromTag = autoPopulateEntriesFromTag;
        this.biomeExclusionTag = biomeExclusionTag;

        this.distortionStrength = distortionStrength;
        this.distortionScale = distortionScale;
        this.warpIterations = warpIterations;
        this.noiseOctaves = noiseOctaves;
        this.halfCellSize = gridCellSize / 2.0;
        this.jitterRange = gridCellSize * jitterStrength * 2.0;
        if (this.climateCount > 64) throw new IllegalStateException("climateCount beyond 64 is not supported due to bitmask avoidance");
    }

    // CORE //

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty(); // handled in initialize()
    }

    public void initialize(long seed, ResourceKey<LevelStem> dimension) {
        if (isInitialized) return;
        this.worldSeed = seed;

        // Initialize Noise
        RandomSource baseRand = RandomSource.create(worldSeed);
        warpNoisesX = new PerlinNoise[warpIterations][noiseOctaves];
        warpNoisesZ = new PerlinNoise[warpIterations][noiseOctaves];

        for (int iteration = 0; iteration < warpIterations; iteration++) {
            for (int octave = 0; octave < noiseOctaves; octave++) {
                long octaveSeed = baseRand.nextLong() ^ (iteration * 73421L) ^ (octave * 19381L);
                RandomSource octaveRandom = RandomSource.create(octaveSeed);
                warpNoisesX[iteration][octave] = PerlinNoise.create(octaveRandom, 1, DoubleList.of(1.0));
                warpNoisesZ[iteration][octave] = PerlinNoise.create(octaveRandom, 1, DoubleList.of(1.0));
            }
        }

        // Initialize Biome Entries
        WeightedBiomeList[] entriesByClimate = new WeightedBiomeList[climateCount];
        Set<ResourceKey<Biome>> assignedBiomeKeys = new HashSet<>();

        RegistryAccessHelper.getServer()
                .flatMap(access -> access.registry(ElysiumRegistries.Keys.MOSAIC_BIOME_ENTRY))
                .ifPresent(registry -> registry.forEach(entry -> {
                    if (!entry.dimension().equals(dimension.location())) return;
                    if (biomeExclusionTag.isPresent() && entry.biome().is(biomeExclusionTag.get())) return;
                    int climateP = entry.climatePoint();
                    if (climateP < 0 || climateP >= climateCount) return;
                    if (entriesByClimate[climateP] == null) entriesByClimate[climateP] = new WeightedBiomeList();
                    entriesByClimate[climateP].add(entry.biome(), entry.weight());
                    entry.biome().unwrapKey().ifPresent(assignedBiomeKeys::add);
                }));

        tagProvidedEntries(assignedBiomeKeys, entriesByClimate);

        Set<Holder<Biome>> providePossibleBiomes = new HashSet<>();
        for (WeightedBiomeList list : entriesByClimate) if (list != null) for (BiomeEntry entry : list.entries) providePossibleBiomes.add(entry.biome);
        this.possibleBiomes = Suppliers.memoize(() -> providePossibleBiomes.stream().distinct().collect(ImmutableSet.toImmutableSet()));

        boolean hasAnyValidEntry = false;
        long globalMask = 0L;

        for (int i = 0; i < climateCount; i++) {
            if (entriesByClimate[i] != null && !entriesByClimate[i].isEmpty()) {
                hasAnyValidEntry = true;
                globalMask |= (1L << i);
            } else entriesByClimate[i] = null;
        }

        if (!hasAnyValidEntry) throw new IllegalStateException("MosaicBiomeSource for dimension '" + dimension.location() + "' has no entries to populate any of the climate points");

        this.climateEntries = entriesByClimate;
        this.validClimatesMask = globalMask;
        this.isInitialized = true;

        ElysiumAPI.LOGGER.info("MosaicBiomeSource successfully initialized for dimension: '{}'", dimension.location());
        ElysiumAPI.LOGGER.debug(buildDebugInfo(seed, dimension, possibleBiomes.get()).toString());
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.@NotNull Sampler sampler) {
        double wx = x;
        double wz = z;
        for (int iteration = 0; iteration < warpIterations; iteration++) {
            double dx = fractalNoise(warpNoisesX[iteration], wx, wz, distortionScale) * distortionStrength;
            double dz = fractalNoise(warpNoisesZ[iteration], wx, wz, distortionScale) * distortionStrength;
            wx += dx;
            wz += dz;
        }
        int gridX = Mth.floor(wx / gridCellSize);
        int gridZ = Mth.floor(wz / gridCellSize);

        double minDistanceSq = Double.MAX_VALUE;
        int bestClimate = -1;
        int bestGridX = 0;
        int bestGridZ = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int neighborGridX = gridX + dx;
                int neighborGridZ = gridZ + dz;
                int climate = resolveClimateForGridCell(neighborGridX, neighborGridZ);
                double jitteredX = (neighborGridX * gridCellSize) + halfCellSize + computeJitterOffset(neighborGridX, neighborGridZ, 0);
                double jitteredZ = (neighborGridZ * gridCellSize) + halfCellSize + computeJitterOffset(neighborGridX, neighborGridZ, 1);

                double distSq = (wx - jitteredX) * (wx - jitteredX) + (wz - jitteredZ) * (wz - jitteredZ);
                if (distSq < minDistanceSq) {
                    minDistanceSq = distSq;
                    bestClimate = climate;
                    bestGridX = neighborGridX;
                    bestGridZ = neighborGridZ;
                }
            }
        }

        return selectBiomeFromClimateEntry(bestClimate, bestGridX, bestGridZ);
    }

    // NOISE //

    private double fractalNoise(PerlinNoise[] noises, double x, double z, double scale) {
        double value = 0.0;
        double amplitude = 1.0;
        double frequency = scale;
        for (PerlinNoise noise : noises) {
            value += noise.getValue(x * frequency, 0, z * frequency) * amplitude;
            amplitude *= 0.5;
            frequency *= 2.0;
        }
        return value;
    }

    // CLIMATE SELECTION & AVOIDANCE //

    private int resolveClimateForGridCell(int gridX, int gridZ) {
        long cellKey = ((long) gridX << 32) | (gridZ & 0xFFFFFFFFL);
        long instanceSalt = System.identityHashCode(this);
        long key = cellKey ^ this.worldSeed ^ (instanceSalt << 32);

        ClimateCache cache = CLIMATE_RESOLUTION_CACHE.get();
        int cached = cache.get(key);
        if (cached != -1) return cached;

        int result = computeClimateWithAvoidance(gridX, gridZ);
        cache.put(key, result);
        return result;
    }

    private int computeClimateWithAvoidance(int gridX, int gridZ) {
        int xMod = Mth.abs(gridX % 2);
        int zMod = Mth.abs(gridZ % 2);

        if (xMod == 0 && zMod == 0) {
            // Even-even: no neighbors to avoid
            return selectClimateWithAvoidance(gridX, gridZ, 0L, 0L);
        } else if (xMod == 1 && zMod == 0) {
            // Horizontal edge cell: avoid left and right
            long mask = toMask(resolveClimateForGridCell(gridX - 1, gridZ)) |
                    toMask(resolveClimateForGridCell(gridX + 1, gridZ));
            return selectClimateWithAvoidance(gridX, gridZ, mask, mask);
        } else if (xMod == 0) {
            // Vertical edge cell: avoid top and bottom
            long mask = toMask(resolveClimateForGridCell(gridX, gridZ - 1)) |
                    toMask(resolveClimateForGridCell(gridX, gridZ + 1));
            return selectClimateWithAvoidance(gridX, gridZ, mask, mask);
        } else {
            // Odd-odd (cardinal and diagonal cell)
            long cardinalMask = toMask(resolveClimateForGridCell(gridX - 1, gridZ)) |
                    toMask(resolveClimateForGridCell(gridX + 1, gridZ)) |
                    toMask(resolveClimateForGridCell(gridX, gridZ - 1)) |
                    toMask(resolveClimateForGridCell(gridX, gridZ + 1));

            if (avoidDiagonalNeighbors) {
                long diagonalMask = toMask(resolveClimateForGridCell(gridX - 1, gridZ - 1)) |
                        toMask(resolveClimateForGridCell(gridX + 1, gridZ - 1)) |
                        toMask(resolveClimateForGridCell(gridX - 1, gridZ + 1)) |
                        toMask(resolveClimateForGridCell(gridX + 1, gridZ + 1));

                long forbiddenMask = cardinalMask | diagonalMask;
                return selectClimateWithAvoidance(gridX, gridZ, forbiddenMask, cardinalMask);
            } else return selectClimateWithAvoidance(gridX, gridZ, cardinalMask, cardinalMask);
        }
    }

    private long toMask(int climate) {
        return (climate >= 0 && climate < climateCount) ? (1L << climate) : 0L;
    }

    private int selectClimateWithAvoidance(int gridX, int gridZ, long forbiddenMask, long cardinalMask) {
        long hash = this.worldSeed + gridX * 1234567L + gridZ * 7654321L;
        hash = (hash ^ (hash >> 16)) * 0x85ebca6bL;

        // Try avoiding all given indices
        long availableMask = ~forbiddenMask & this.validClimatesMask;
        if (availableMask != 0L) return pickClimateFromMask(hash, availableMask);

        // Fallback: avoid only cardinal directions
        long cardinalAvailableMask = ~cardinalMask & this.validClimatesMask;
        if (cardinalAvailableMask != 0L) return pickClimateFromMask(hash, cardinalAvailableMask);

        // Final Fallback: avoid nothing
        return pickClimateFromMask(hash, this.validClimatesMask);
    }

    private int pickClimateFromMask(long hash, long mask) {
        int count = Long.bitCount(mask);
        int target = (int) ((hash & Long.MAX_VALUE) % count);

        long tempMask = mask;
        for (int i = 0; i < target; i++) tempMask &= tempMask - 1;
        return Long.numberOfTrailingZeros(tempMask);
    }

    // CALCULATIONS & SELECTIONS //

    private double computeJitterOffset(int gridX, int gridZ, int axis) {
        long hash = this.worldSeed + gridX * 31337L + gridZ * 313373L + axis * 17L;
        hash = (hash ^ (hash >> 16)) * 0x85ebca6bL;
        hash = (hash ^ (hash >> 13)) * 0xc2b2ae35L;
        hash = hash ^ (hash >> 16);
        double value = (double) (hash & 0xFFFFFF) / (double) 0xFFFFFF;
        return (value - 0.5) * jitterRange;
    }

    private Holder<Biome> selectBiomeFromClimateEntry(int climate, int gridX, int gridZ) {
        WeightedBiomeList list = climateEntries[climate];
        long hash = this.worldSeed + gridX * 98765L + gridZ * 54321L;
        hash = (hash ^ (hash >> 16)) * 0x85ebca6bL;

        int roll = (int) ((hash & Long.MAX_VALUE) % list.totalWeight);

        for (BiomeEntry entry : list.entries) {
            roll -= entry.weight;
            if (roll < 0) return entry.biome;
        }

        return list.entries.getLast().biome;
    }

    // DEBUG //

    private StringBuilder buildDebugInfo(long seed, ResourceKey<LevelStem> dimension, Set<Holder<Biome>> possibleBiomes) {
        StringBuilder debug = new StringBuilder();
        debug.append("MosaicBiomeSource - '").append(dimension.location()).append("'\n");
        debug.append("\tSeed: ").append(seed).append("\n");
        debug.append("\tGrid Cell Size: ").append(gridCellSize).append(", climate count: ").append(climateCount).append("\n");
        debug.append("\tAvoid Diagonal Neighbors: ").append(avoidDiagonalNeighbors).append("\n");
        debug.append("\tPossible Biomes:\n");
        for (Holder<Biome> biome : possibleBiomes) {
            String biomeName = biome.unwrapKey()
                    .map(key -> "'" + key.location() + "'")
                    .orElse("'unknown'");
            debug.append("\t\t").append(biomeName).append("\n");
        }
        return debug;
    }

    // HELPERS //

    private void tagProvidedEntries(Set<ResourceKey<Biome>> assignedBiomeKeys, WeightedBiomeList[] entriesByClimate) {
        if (autoPopulateEntriesFromTag.isEmpty()) return;
        RegistryAccessHelper.getServer()
                .flatMap(access -> access.registry(Registries.BIOME))
                .ifPresent(biomeRegistry -> biomeRegistry.getTagOrEmpty(autoPopulateEntriesFromTag.get())
                        .forEach(holder -> holder.unwrapKey().ifPresent(key -> {
                            if (biomeExclusionTag.isPresent() && holder.is(biomeExclusionTag.get())) return;
                            if (!assignedBiomeKeys.contains(key)) {
                                int climate = Math.abs(key.location().toString().hashCode()) % climateCount;
                                WeightedBiomeList list = entriesByClimate[climate];
                                int weight = (list == null) ? 1 : Math.max(1, list.totalWeight / list.entries.size());

                                if (list == null) entriesByClimate[climate] = list = new WeightedBiomeList();
                                list.add(holder, weight);
                                assignedBiomeKeys.add(key);
                            }
                        }))
                );
    }

    private static class WeightedBiomeList {
        final List<BiomeEntry> entries = new ArrayList<>();
        int totalWeight = 0;

        void add(Holder<Biome> biome, int weight) {
            entries.add(new BiomeEntry(biome, weight));
            totalWeight += weight;
        }

        boolean isEmpty() {
            return entries.isEmpty();
        }
    }

    private record BiomeEntry(Holder<Biome> biome, int weight) {}

    // CACHE //

    private static class ClimateCache {
        private static final int CAPACITY = 4096;
        private static final int MASK = CAPACITY - 1;
        private final long[] keys = new long[CAPACITY];
        private final int[] values = new int[CAPACITY];

        public ClimateCache() {
            Arrays.fill(keys, Long.MIN_VALUE);
        }

        public int get(long key) {
            long h = key ^ (key >>> 32);
            h ^= (h >>> 16);
            int idx = (int) (h & MASK);

            if (keys[idx] == key) return values[idx];
            return -1;
        }

        public void put(long key, int value) {
            long h = key ^ (key >>> 32);
            h ^= (h >>> 16);
            int idx = (int) (h & MASK);

            keys[idx] = key;
            values[idx] = value;
        }
    }
}