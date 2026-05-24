package net.jadenxgamer.elysium_api.impl.core.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.jadenxgamer.elysium_api.Elysium;
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
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            TagKey.codec(Registries.BIOME).optionalFieldOf("auto_populate_entries_from_tag", null).forGetter(s -> s.autoPopulateEntriesFromTag)
    ).apply(instance, MosaicBiomeSource::new));

    private final int gridCellSize;
    private final int climateCount;
    private final float jitterStrength;
    private final boolean avoidDiagonalNeighbors;
    @Nullable private final TagKey<Biome> autoPopulateEntriesFromTag;

    private final double distortionStrength;
    private final double distortionScale;
    private final int warpIterations;
    private final int noiseOctaves;

    private final double halfCellSize;
    private final double jitterRange;

    private volatile boolean isInitialized = false;
    private long worldSeed;

    private WeightedBiomeList[] climateEntries;

    private PerlinNoise[][] warpNoisesX;
    private PerlinNoise[][] warpNoisesZ;
    private volatile boolean noiseInitialized = false;

    private static final ThreadLocal<Map<Long, Integer>> CLIMATE_RESOLUTION_CACHE =
            ThreadLocal.withInitial(() -> new LinkedHashMap<>(64, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                    return size() > 256;
                }
            });

    public MosaicBiomeSource(int gridCellSize, int climateCount, float jitterStrength, boolean avoidDiagonalNeighbors,
                             double distortionStrength, double distortionScale, int warpIterations, int noiseOctaves,
                             @Nullable TagKey<Biome> autoPopulateEntriesFromTag) {
        this.gridCellSize = gridCellSize;
        this.climateCount = climateCount;
        this.jitterStrength = jitterStrength;
        this.avoidDiagonalNeighbors = avoidDiagonalNeighbors;
        this.autoPopulateEntriesFromTag = autoPopulateEntriesFromTag;

        this.distortionStrength = distortionStrength;
        this.distortionScale = distortionScale;
        this.warpIterations = warpIterations;
        this.noiseOctaves = noiseOctaves;
        this.halfCellSize = gridCellSize / 2.0;
        this.jitterRange = gridCellSize * jitterStrength * 2.0;
        if (this.climateCount > 64) throw new IllegalStateException("climateCount beyond 64 is not supported due to bitmask avoidance");
    }

    //////////
    // CORE //
    //////////

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty(); // handled in initialize()
    }

    /**
     * Lazy-Initialization of entriesByClimate, possibleBiomes and worldSeed since we need RegistryAccess to be present for this.
     * @see net.jadenxgamer.elysium_api.impl.event.ElysiumEvents#onServerAboutToStart(ServerAboutToStartEvent)
     */
    public void initialize(long seed, ResourceKey<LevelStem> dimension) {
        if (isInitialized) return;
        this.worldSeed = seed;

        WeightedBiomeList[] entriesByClimate = new WeightedBiomeList[climateCount];
        Set<ResourceKey<Biome>> assignedBiomeKeys = new HashSet<>();

        RegistryAccessHelper.getServer()
                .flatMap(access -> access.registry(ElysiumRegistries.Keys.MOSAIC_BIOME_ENTRY))
                .ifPresent(registry -> registry.forEach(entry -> {
                    if (!entry.dimension().equals(dimension.location())) return;
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
        for (int i = 0; i < climateCount; i++) {
            if (entriesByClimate[i] != null && !entriesByClimate[i].isEmpty()) hasAnyValidEntry = true;
            else entriesByClimate[i] = null;
        }
        if (!hasAnyValidEntry) throw new IllegalStateException("MosaicBiomeSource for dimension '" + dimension.location() + "' has no entries to populate any of the climate points");

        this.climateEntries = entriesByClimate;
        this.isInitialized = true;

        Elysium.LOGGER.info("MosaicBiomeSource successfully initialized for dimension: '{}'", dimension.location());
        Elysium.LOGGER.debug(buildDebugInfo(seed, dimension, possibleBiomes.get()).toString());
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.@NotNull Sampler sampler) {
        double[] warped = warpPoint(x, z);
        double wx = warped[0];
        double wz = warped[1];
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

    ///////////
    // NOISE //
    ///////////

    //TODO: Add more noise warping types, right now Mosaic only supports fractional Brownian motion.
    private void initNoise() {
        if (noiseInitialized) return;
        synchronized (this) {
            if (noiseInitialized) return;
            RandomSource baseRand = RandomSource.create(worldSeed);
            warpNoisesX = new PerlinNoise[warpIterations][noiseOctaves];
            warpNoisesZ = new PerlinNoise[warpIterations][noiseOctaves];

            for (int iter = 0; iter < warpIterations; iter++) {
                for (int oct = 0; oct < noiseOctaves; oct++) {
                    long seed = baseRand.nextLong() ^ (iter * 73421L) ^ (oct * 19381L);
                    RandomSource octaveRand = RandomSource.create(seed);
                    warpNoisesX[iter][oct] = PerlinNoise.create(octaveRand, 1, DoubleList.of(1.0));
                    warpNoisesZ[iter][oct] = PerlinNoise.create(octaveRand, 1, DoubleList.of(1.0));
                }
            }
            noiseInitialized = true;
        }
    }

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

    private double[] warpPoint(double x, double z) {
        initNoise();
        double wx = x;
        double wz = z;
        for (int iter = 0; iter < warpIterations; iter++) {
            double dx = fractalNoise(warpNoisesX[iter], wx, wz, distortionScale) * distortionStrength;
            double dz = fractalNoise(warpNoisesZ[iter], wx, wz, distortionScale) * distortionStrength;
            wx += dx;
            wz += dz;
        }
        return new double[]{wx, wz};
    }

    ///////////////////////////////////
    // CLIMATE SELECTION & AVOIDANCE //
    ///////////////////////////////////

    private int resolveClimateForGridCell(int gridX, int gridZ) {
        long key = packGridCoordinates(gridX, gridZ);
        Map<Long, Integer> cache = CLIMATE_RESOLUTION_CACHE.get();
        Integer cached = cache.get(key);
        if (cached != null) return cached;

        int result = computeClimateWithAvoidance(gridX, gridZ);
        cache.put(key, result);
        return result;
    }

    private int computeClimateWithAvoidance(int gridX, int gridZ) {
        int xMod = Mth.abs(gridX % 2);
        int zMod = Mth.abs(gridZ % 2);

        if (xMod == 0 && zMod == 0) {
            // Even-even: no neighbors to avoid
            return selectClimateWithAvoidance(gridX, gridZ, new int[0]);
        } else if (xMod == 1 && zMod == 0) {
            // Horizontal edge cell: avoid left and right
            int left = resolveClimateForGridCell(gridX - 1, gridZ);
            int right = resolveClimateForGridCell(gridX + 1, gridZ);
            return selectClimateWithAvoidance(gridX, gridZ, new int[]{left, right});
        } else if (xMod == 0) {
            // Vertical edge cell: avoid top and bottom
            int top = resolveClimateForGridCell(gridX, gridZ - 1);
            int bottom = resolveClimateForGridCell(gridX, gridZ + 1);
            return selectClimateWithAvoidance(gridX, gridZ, new int[]{top, bottom});
        } else {
            // Odd-odd (cardinal and diagonal cell)
            int left = resolveClimateForGridCell(gridX - 1, gridZ);
            int right = resolveClimateForGridCell(gridX + 1, gridZ);
            int top = resolveClimateForGridCell(gridX, gridZ - 1);
            int bottom = resolveClimateForGridCell(gridX, gridZ + 1);

            if (avoidDiagonalNeighbors) {
                int topLeft = resolveClimateForGridCell(gridX - 1, gridZ - 1);
                int topRight = resolveClimateForGridCell(gridX + 1, gridZ - 1);
                int bottomLeft = resolveClimateForGridCell(gridX - 1, gridZ + 1);
                int bottomRight = resolveClimateForGridCell(gridX + 1, gridZ + 1);
                int[] avoids = {left, right, top, bottom, topLeft, topRight, bottomLeft, bottomRight};
                return selectClimateWithAvoidance(gridX, gridZ, avoids);
            } else {
                return selectClimateWithAvoidance(gridX, gridZ, new int[]{left, right, top, bottom});
            }
        }
    }

    private static long packGridCoordinates(int gridX, int gridZ) {
        return ((long) gridX << 32) | (gridZ & 0xFFFFFFFFL);
    }

    /**
     * Chooses a climate index for the given grid cell while trying to avoid specified indices.
     * Fallback behavior:
     * 1. Avoid all provided indices.
     * 2. If no climate is left, avoid only the first 4 indices (cardinal directions) if available.
     * 3. If still none, avoid nothing (select any climate that has biome entries).
     */
    private int selectClimateWithAvoidance(int gridX, int gridZ, int[] avoids) {
        long hash = this.worldSeed + gridX * 1234567L + gridZ * 7654321L;
        hash = (hash ^ (hash >> 16)) * 0x85ebca6bL;
        long forbiddenMask = 0L;
        for (int a : avoids) if (a >= 0 && a < this.climateCount) forbiddenMask |= (1L << a); // is it even standard to format code this way? ehhhhh whatever, I like it compact

        // Try avoiding all given indices
        int[] available = buildAvailableClimateIndices(forbiddenMask);
        if (available.length > 0) return pickClimateFromAvailable(hash, available);

        // Fallback: avoid only the first 4 indices (cardinal directions)
        if (avoids.length >= 4) {
            long cardinalMask = 0L;
            for (int i = 0; i < 4; i++) {
                int a = avoids[i];
                if (a >= 0 && a < this.climateCount) cardinalMask |= (1L << a);
            }
            available = buildAvailableClimateIndices(cardinalMask);
            if (available.length > 0) return pickClimateFromAvailable(hash, available);
        }

        // Final Fallback: avoid nothing
        available = buildAvailableClimateIndices(0L);
        return pickClimateFromAvailable(hash, available);
    }

    private int[] buildAvailableClimateIndices(long forbiddenMask) {
        int[] temp = new int[this.climateCount];
        int count = 0;
        for (int i = 0; i < this.climateCount; i++) {
            boolean isForbidden = (forbiddenMask & (1L << i)) != 0;
            if (!isForbidden && climateEntries[i] != null) {
                temp[count++] = i;
            }
        }
        return Arrays.copyOf(temp, count);
    }

    private int pickClimateFromAvailable(long hash, int[] available) {
        int index = (int) ((hash & Long.MAX_VALUE) % available.length);
        return available[index];
    }

    ///////////////////////////////
    // CALCULATIONS & SELECTIONS //
    ///////////////////////////////

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

    ///////////
    // DEBUG //
    ///////////

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

    /////////////
    // HELPERS //
    /////////////

    private void tagProvidedEntries(Set<ResourceKey<Biome>> assignedBiomeKeys, WeightedBiomeList[] entriesByClimate) {
        if (autoPopulateEntriesFromTag == null) return;
        RegistryAccessHelper.getServer()
                .flatMap(access -> access.registry(Registries.BIOME))
                .ifPresent(biomeRegistry -> biomeRegistry.getTagOrEmpty(autoPopulateEntriesFromTag)
                        .forEach(holder -> holder.unwrapKey().ifPresent(key -> {
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
}