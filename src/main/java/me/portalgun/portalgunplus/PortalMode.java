package me.portalgun.portalgunplus;

public enum PortalMode {
    NETHER,
    END,
    END_GATEWAY,
    SPAWN;

    public static PortalMode fromNetworkId(int id) {
        PortalMode[] values = values();
        return values[Math.floorMod(id, values.length)];
    }
}
