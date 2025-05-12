package net.jadenxgamer.elysium_api.impl.mixin.block;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.jadenxgamer.elysium_api.impl.sound_transformer.SoundTransformer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = Block.class, priority = 69420)
public abstract class BlockMixin extends BlockBehaviour {

    public BlockMixin(Properties properties) {
        super(properties);
    }

    @Unique
    private final Block block = ((Block) (Object) this);

    @Inject(
            method = "getSoundType",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void elysium$soundTransformer(BlockState state, CallbackInfoReturnable<SoundType> cir) {
        if (Elysium.registryAccess != null) {
            Optional<SoundTransformer> registry = Elysium.registryAccess.registryOrThrow(ElysiumRegistries.BLOCK_SOUND_TRANSFORMER).stream().filter(s -> s.blocks().contains(state.getBlockHolder())).findFirst();
            if (registry.isEmpty()) {
                return;
            }

            if (registry.get().blocks().contains(state.getBlockHolder())) {
                cir.setReturnValue(registry.get().toSoundType());
            }
        }
    }
}
