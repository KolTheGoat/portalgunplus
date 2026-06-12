package me.portalgun.portalgunplus.manager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ServerSystems {
    private ServerSystems() {}

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            CooldownManager.tick(server);
            PortalManager.tick(server);
            ObsidianBoatManager.tick(server);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var uuid = handler.player.getUUID();
            PlayerSettingsManager.remove(uuid);
            CooldownManager.remove(uuid);
        });
    }
}
