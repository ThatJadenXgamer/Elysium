package net.jadenxgamer.elysium_api.impl.networking;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollPayload;
import net.jadenxgamer.elysium_api.impl.networking.to_client.ScreenFlashPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ElysiumPayloads {
    private static final String VERSION = "0.1.0";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Elysium.MOD_ID).versioned(VERSION);

        registrar.playToClient(ScreenFlashPayload.TYPE, ScreenFlashPayload.CODEC, ScreenFlashPayload::handleDataOnClient);
        registrar.playToClient(DodgeRollPayload.TYPE, DodgeRollPayload.CODEC, DodgeRollPayload::handleDataOnClient);
    }
}
