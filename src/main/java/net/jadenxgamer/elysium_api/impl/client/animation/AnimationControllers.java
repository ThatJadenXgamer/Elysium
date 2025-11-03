package net.jadenxgamer.elysium_api.impl.client.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.resources.ResourceLocation;

public final class AnimationControllers {

    public static final ResourceLocation MOVEMENT = Elysium.id("movement");

    public static void boostrap() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                MOVEMENT,
                1600,
                player -> new PlayerAnimationController(player, (c, d, s) -> PlayState.STOP)
        );
    }

}
