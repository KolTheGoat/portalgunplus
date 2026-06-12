package me.portalgun.portalgunplus.client;

import me.portalgun.portalgunplus.ModItems;
import me.portalgun.portalgunplus.client.screen.PortalModeScreen;
import me.portalgun.portalgunplus.client.screen.PotionGunScreen;
import me.portalgun.portalgunplus.network.FireGunPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

public class FirstClient implements ClientModInitializer {

    private static boolean attackWasDown = false;
    private static boolean useWasDown = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean attackIsDown = client.options.keyAttack.isDown();
            boolean useIsDown = client.options.keyUse.isDown();

            if (client.player != null && client.screen == null) {
                if (attackIsDown && !attackWasDown) {
                    if (client.player.getMainHandItem().is(ModItems.PORTAL_GUN)) {
                        ClientPlayNetworking.send(new FireGunPayload(0));
                    } else if (client.player.getMainHandItem().is(ModItems.POTION_GUN)) {
                        ClientPlayNetworking.send(new FireGunPayload(1));
                    }
                }

                if (useIsDown && !useWasDown) {
                    if (client.player.getMainHandItem().is(ModItems.PORTAL_GUN)) {
                        Minecraft.getInstance().setScreen(
                                new PortalModeScreen(Component.literal("Portal Gun Menu"))
                        );
                    } else if (client.player.getMainHandItem().is(ModItems.POTION_GUN)) {
                        Minecraft.getInstance().setScreen(
                                new PotionGunScreen(Component.literal("Potion Gun Menu"))
                        );
                    }
                }
            }

            attackWasDown = attackIsDown;
            useWasDown = useIsDown;
        });

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) ->
                player.getMainHandItem().is(ModItems.PORTAL_GUN)
                        || player.getMainHandItem().is(ModItems.POTION_GUN)
                        ? InteractionResult.SUCCESS
                        : InteractionResult.PASS
        );

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                player.getMainHandItem().is(ModItems.PORTAL_GUN)
                        || player.getMainHandItem().is(ModItems.POTION_GUN)
                        ? InteractionResult.SUCCESS
                        : InteractionResult.PASS
        );
    }
}