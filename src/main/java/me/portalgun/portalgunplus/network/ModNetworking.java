package me.portalgun.portalgunplus.network;

import me.portalgun.portalgunplus.PortalMode;
import me.portalgun.portalgunplus.PotionEffectChoice;
import me.portalgun.portalgunplus.PotionSettings;
import me.portalgun.portalgunplus.manager.PlayerSettingsManager;
import me.portalgun.portalgunplus.manager.PortalManager;
import me.portalgun.portalgunplus.manager.PotionManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
    private ModNetworking() {}

    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(FireGunPayload.TYPE, FireGunPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PortalModePayload.TYPE, PortalModePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PotionSettingsPayload.TYPE, PotionSettingsPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(FireGunPayload.TYPE, (payload, context) -> {
            if (payload.gunId() == 0) {
                PortalManager.fire(context.player());
            } else if (payload.gunId() == 1) {
                PotionManager.fire(context.player());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(PortalModePayload.TYPE, (payload, context) ->
                PlayerSettingsManager.setPortalMode(context.player().getUUID(), PortalMode.fromNetworkId(payload.modeId()))
        );

        ServerPlayNetworking.registerGlobalReceiver(PotionSettingsPayload.TYPE, (payload, context) -> {
            PotionSettings settings = new PotionSettings(
                    PotionEffectChoice.fromNetworkId(payload.effectId()),
                    payload.level(),
                    PotionSettings.durationFromNetworkId(payload.durationId())
            );
            PlayerSettingsManager.setPotionSettings(context.player().getUUID(), settings);
        });
    }
}
