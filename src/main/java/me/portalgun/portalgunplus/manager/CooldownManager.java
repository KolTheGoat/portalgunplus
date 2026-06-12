package me.portalgun.portalgunplus.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;

public final class CooldownManager {
    public enum GunType {
        PORTAL("Portal Gun", 5 * 20),
        POTION("Potion Gun", 15 * 20);

        private final String displayName;
        private final int cooldownTicks;

        GunType(String displayName, int cooldownTicks) {
            this.displayName = displayName;
            this.cooldownTicks = cooldownTicks;
        }
    }

    private record Key(UUID playerId, GunType gunType) {}
    private record ActiveCooldown(int initialTicks, int remainingTicks, ServerBossEvent bossBar) {}

    private static final Map<Key, ActiveCooldown> ACTIVE = new HashMap<>();

    private CooldownManager() {}

    public static boolean tryStart(ServerPlayer player, GunType type) {
        Key key = new Key(player.getUUID(), type);
        if (ACTIVE.containsKey(key)) {
            return false;
        }

        ServerBossEvent bar = new ServerBossEvent(
                UUID.randomUUID(),
                Component.literal(type.displayName + " cooldown"),
                BossEvent.BossBarColor.PURPLE,
                BossEvent.BossBarOverlay.PROGRESS
        );
        bar.addPlayer(player);
        ACTIVE.put(key, new ActiveCooldown(type.cooldownTicks, type.cooldownTicks, bar));
        return true;
    }

    public static int remainingSeconds(ServerPlayer player, GunType type) {
        ActiveCooldown cooldown = ACTIVE.get(new Key(player.getUUID(), type));
        return cooldown == null ? 0 : (cooldown.remainingTicks + 19) / 20;
    }

    public static void tick(MinecraftServer server) {
        Iterator<Map.Entry<Key, ActiveCooldown>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Key, ActiveCooldown> entry = iterator.next();
            Key key = entry.getKey();
            ActiveCooldown old = entry.getValue();
            int remaining = old.remainingTicks - 1;

            if (remaining <= 0) {
                old.bossBar.removeAllPlayers();
                iterator.remove();
                continue;
            }

            ServerPlayer player = server.getPlayerList().getPlayer(key.playerId);
            if (player == null) {
                old.bossBar.removeAllPlayers();
                iterator.remove();
                continue;
            }

            old.bossBar.setName(Component.literal(
                    key.gunType.displayName + ": " + ((remaining + 19) / 20) + "s"
            ));
            old.bossBar.setProgress((float) remaining / (float) old.initialTicks);
            entry.setValue(new ActiveCooldown(old.initialTicks, remaining, old.bossBar));
        }
    }

    public static void remove(UUID playerId) {
        ACTIVE.entrySet().removeIf(entry -> {
            if (entry.getKey().playerId.equals(playerId)) {
                entry.getValue().bossBar.removeAllPlayers();
                return true;
            }
            return false;
        });
    }
}
