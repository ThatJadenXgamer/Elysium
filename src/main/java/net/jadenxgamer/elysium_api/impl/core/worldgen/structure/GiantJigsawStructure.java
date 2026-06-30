package net.jadenxgamer.elysium_api.impl.core.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

// this is quite literally just the vanilla JigsawStructure class the only thing that changed was the max distance and depth
// I'll improve it later, for now this was needed to make JNE's sanctums work
public final class GiantJigsawStructure extends Structure {
    public static final DimensionPadding DEFAULT_DIMENSION_PADDING = DimensionPadding.ZERO;
    public static final LiquidSettings DEFAULT_LIQUID_SETTINGS = LiquidSettings.APPLY_WATERLOGGING;
    public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
    public static final int MIN_DEPTH = 0;
    public static final int MAX_DEPTH = 20;
    public static final MapCodec<GiantJigsawStructure> CODEC = RecordCodecBuilder.<GiantJigsawStructure>mapCodec(
                    p_227640_ -> p_227640_.group(
                                    settingsCodec(p_227640_),
                                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(p_227656_ -> p_227656_.startPool),
                                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(p_227654_ -> p_227654_.startJigsawName),
                                    Codec.intRange(0, 99).fieldOf("size").forGetter(p_227652_ -> p_227652_.maxDepth),
                                    HeightProvider.CODEC.fieldOf("start_height").forGetter(p_227649_ -> p_227649_.startHeight),
                                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(p_227646_ -> p_227646_.useExpansionHack),
                                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(p_227644_ -> p_227644_.projectStartToHeightmap),
                                    Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(p_227642_ -> p_227642_.maxDistanceFromCenter),
                                    Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(p_307187_ -> p_307187_.poolAliases),
                                    DimensionPadding.CODEC
                                            .optionalFieldOf("dimension_padding", DEFAULT_DIMENSION_PADDING)
                                            .forGetter(p_348455_ -> p_348455_.dimensionPadding),
                                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", DEFAULT_LIQUID_SETTINGS).forGetter(p_352036_ -> p_352036_.liquidSettings)
                            )
                            .apply(p_227640_, GiantJigsawStructure::new)
            )
            .validate(GiantJigsawStructure::verifyRange);
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    private static DataResult<GiantJigsawStructure> verifyRange(GiantJigsawStructure structure) {
        return DataResult.success(structure);
    }

    public GiantJigsawStructure(
            Structure.StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int maxDepth,
            HeightProvider startHeight,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            List<PoolAliasBinding> poolAliases,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    public GiantJigsawStructure(
            Structure.StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int maxDepth,
            HeightProvider startHeight,
            boolean useExpansionHack,
            Heightmap.Types projectStartToHeightmap
    ) {
        this(
                settings,
                startPool,
                Optional.empty(),
                maxDepth,
                startHeight,
                useExpansionHack,
                Optional.of(projectStartToHeightmap),
                80,
                List.of(),
                DEFAULT_DIMENSION_PADDING,
                DEFAULT_LIQUID_SETTINGS
        );
    }

    public GiantJigsawStructure(
            Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth, HeightProvider startHeight, boolean useExpansionHack
    ) {
        this(
                settings,
                startPool,
                Optional.empty(),
                maxDepth,
                startHeight,
                useExpansionHack,
                Optional.empty(),
                80,
                List.of(),
                DEFAULT_DIMENSION_PADDING,
                DEFAULT_LIQUID_SETTINGS
        );
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                this.startJigsawName,
                this.maxDepth,
                blockpos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter,
                PoolAliasLookup.create(this.poolAliases, blockpos, context.seed()),
                this.dimensionPadding,
                this.liquidSettings
        );
    }

    @Override
    public StructureType<?> type() {
        return StructureType.JIGSAW;
    }
}
