package me.portalgun.portalgunplus.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.portalgun.portalgunplus.PortalMode;
import me.portalgun.portalgunplus.PotionSettings;

public final class PlayerSettingsManager {
    private static final Map<UUID, PortalMode> PORTAL_MODES = new ConcurrentHashMap<>();
    private static final Map<UUID, PotionSettings> POTION_SETTINGS = new ConcurrentHashMap<>();

    private PlayerSettingsManager() {}

    public static PortalMode getPortalMode(UUID uuid) {
        return PORTAL_MODES.getOrDefault(uuid, PortalMode.NETHER);
    }

    public static void setPortalMode(UUID uuid, PortalMode mode) {
        PORTAL_MODES.put(uuid, mode);
    }

    public static PotionSettings getPotionSettings(UUID uuid) {
        return POTION_SETTINGS.getOrDefault(uuid, PotionSettings.DEFAULT);
    }

    public static void setPotionSettings(UUID uuid, PotionSettings settings) {
        POTION_SETTINGS.put(uuid, settings);
    }

    public static void remove(UUID uuid) {
        PORTAL_MODES.remove(uuid);
        POTION_SETTINGS.remove(uuid);
    }
}
