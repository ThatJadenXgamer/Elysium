package net.jadenxgamer.elysium_api.impl.mixin.block;

import net.jadenxgamer.elysium_api.api.util.RegistryAccessHelper;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviorMixin {

    @Inject(
            method = "getSoundType",
            at = @At("HEAD"),
            cancellable = true
    )
    private void elysium$soundTransformer(BlockState state, CallbackInfoReturnable<SoundType> cir) {
        RegistryAccessHelper.getServer()
                .flatMap(access -> access.registryOrThrow(ElysiumRegistries.BLOCK_SOUND_TRANSFORMERS).stream()
                        .filter(s -> s.blocks().contains(state.getBlockHolder()))
                        .findFirst())
                .ifPresent(transformer -> cir.setReturnValue(transformer.toSoundType()));
    }
}