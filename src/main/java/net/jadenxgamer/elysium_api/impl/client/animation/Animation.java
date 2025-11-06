package net.jadenxgamer.elysium_api.impl.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.jadenxgamer.elysium_api.Elysium;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public interface Animation {

    Animation DODGE_ROLL = animation("dodge_roll", AnimationLayers.MOVEMENT);

    private static Animation animation(String id, ResourceLocation layerId) {
        ResourceLocation animationId = Elysium.id(id);
        return player -> {
            //noinspection unchecked
            ModifierLayer<IAnimation> layer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(layerId);
            if (layer == null) {
                Elysium.LOGGER.error("ModifierLayer {} is null!", layerId);
                return;
            }

            KeyframeAnimation animation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(animationId);
            if (animation == null) {
                Elysium.LOGGER.error("KeyframeAnimation {} is null!", animationId);
                return;
            }

            // hmmmm
            layer.replaceAnimationWithFade(
                    AbstractFadeModifier.standardFadeIn(animation.beginTick, Ease.INOUTSINE),
                    new KeyframeAnimationPlayer(animation));
        };
    }

    void play(AbstractClientPlayer player);

}
