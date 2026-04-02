package net.jadenxgamer.elysium_api.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class RegistryAccessHelper {

    public static RegistryAccess getServer() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) throw new IllegalStateException("Server has not been initialised");
        return server.registryAccess();
    }

    @OnlyIn(Dist.CLIENT)
    public static RegistryAccess getClient() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) throw new IllegalStateException("Client has not been initialised");
        return client.level.registryAccess();
    }

    public static boolean isRegistryAccessAvailable() {
        if (FMLEnvironment.dist == Dist.CLIENT) return  Minecraft.getInstance().level != null;
        else return ServerLifecycleHooks.getCurrentServer() != null;
    }
}
