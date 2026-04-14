package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

public final class RegistryAccessHelper {

    private RegistryAccessHelper() {}

    /**
     * @return An Optional containing the server-side RegistryAccess if available,
     */
    public static Optional<RegistryAccess> getServer() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? Optional.empty() : Optional.of(server.registryAccess());
    }

    /**
     * @return An Optional containing the client-side RegistryAccess if a level is loaded,
     */
    @OnlyIn(Dist.CLIENT)
    public static Optional<RegistryAccess> getClient() {
        Minecraft client = Minecraft.getInstance();
        return client.level == null ? Optional.empty() : Optional.of(client.level.registryAccess());
    }

    /**
     * Returns the appropriate RegistryAccess depending on the current environment,
     * preferring server if available, falling back to client (if on client and level exists).
     *
     * @return Optional of the best currently available RegistryAccess
     */
    public static Optional<RegistryAccess> getCurrent() {
        if (FMLEnvironment.dist == Dist.CLIENT) return getClient().or(RegistryAccessHelper::getServer);
        else return getServer();
    }

    /**
     * Checks whether any RegistryAccess is currently available.
     */
    public static boolean isRegistryAccessAvailable() {
        if (FMLEnvironment.dist == Dist.CLIENT) return Minecraft.getInstance().level != null;
        else return ServerLifecycleHooks.getCurrentServer() != null;
    }
}