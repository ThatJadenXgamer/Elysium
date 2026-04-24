package net.jadenxgamer.elysium_api.impl.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.lightmap_settings.LightmapSettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Unique
    private static final Vector3f elysium$skyMultiplier = new Vector3f(1.0f, 1.0f, 1.0f);
    @Unique
    private static final Vector3f elysium$blockMultiplier = new Vector3f(1.0f, 1.0f, 1.0f);

    @Inject(method = "updateLightTexture", at = @At("HEAD"))
    private void elysium$updateLightmapMultipliers(float partialTicks, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            var multipliers = Elysium.LIGHTMAP_SETTINGS.getSettings(player);
            elysium$skyMultiplier.set(multipliers.getLeft());
            elysium$blockMultiplier.set(multipliers.getRight());
        } else {
            elysium$skyMultiplier.set(1.0f, 1.0f, 1.0f);
            elysium$blockMultiplier.set(1.0f, 1.0f, 1.0f);
        }
    }

    @Inject(
            method = "updateLightTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Vector3f;add(Lorg/joml/Vector3fc;)Lorg/joml/Vector3f;",
                    shift = At.Shift.BEFORE
            )
    )
    private void elysium$applyLightmapColors(
            float partialTicks, CallbackInfo ci, @Local(name = "vector3f1") Vector3f vector3f1, @Local(name = "vector3f2") Vector3f vector3f2) {
        vector3f1.mul(elysium$blockMultiplier);
        vector3f2.mul(elysium$skyMultiplier);
    }

    @Inject(
            method = "turnOnLightLayer",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void elysium$useGuiLightmap(CallbackInfo ci) {
        if (ModList.get().isLoaded("polytone")) return;
        if (Elysium.LIGHTMAP_SETTINGS.isGui()) {
            RenderSystem.setShaderTexture(2, LightmapSettingsManager.GUI_LIGHTMAP);
            Minecraft.getInstance().getTextureManager().bindForSetup(LightmapSettingsManager.GUI_LIGHTMAP);
            RenderSystem.texParameter(3553, 10241, 9729);
            RenderSystem.texParameter(3553, 10240, 9729);
            ci.cancel();
        }
    }
}