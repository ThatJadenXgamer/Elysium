package net.jadenxgamer.elysium_api.impl.client.animation;


import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.minecraft.resources.ResourceLocation;

public final class AnimationLayers {

    public static final ResourceLocation MOVEMENT = ElysiumAPI.elysiumPath("movement");

    public static void boostrap() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                MOVEMENT,
                1600,
                player -> new ModifierLayer<>()
        );
    }

}
