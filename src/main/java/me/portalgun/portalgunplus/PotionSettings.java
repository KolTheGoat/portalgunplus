package me.portalgun.portalgunplus;

public record PotionSettings(PotionEffectChoice effect, int level, int durationTicks) {
    public static final PotionSettings DEFAULT = new PotionSettings(PotionEffectChoice.SPEED, 1, 10 * 20);

    public PotionSettings {
        level = Math.max(1, Math.min(5, level));
        durationTicks = durationTicks < 0 ? Integer.MAX_VALUE : Math.max(20, durationTicks);
    }

    public static int durationFromNetworkId(int id) {
        return switch (Math.floorMod(id, 5)) {
            case 0 -> 5 * 20;
            case 1 -> 10 * 20;
            case 2 -> 20 * 20;
            case 3 -> 40 * 20;
            default -> Integer.MAX_VALUE;
        };
    }
}
