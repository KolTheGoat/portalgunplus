package me.portalgun.portalgunplus;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public enum PotionEffectChoice {
    SPEED(MobEffects.SPEED),
    SLOWNESS(MobEffects.SLOWNESS),
    HASTE(MobEffects.HASTE),
    MINING_FATIGUE(MobEffects.MINING_FATIGUE),
    STRENGTH(MobEffects.STRENGTH),
    REGENERATION(MobEffects.REGENERATION),
    RESISTANCE(MobEffects.RESISTANCE),
    JUMP_BOOST(MobEffects.JUMP_BOOST),
    FIRE_RESISTANCE(MobEffects.FIRE_RESISTANCE),
    INVISIBILITY(MobEffects.INVISIBILITY),
    POISON(MobEffects.POISON),
    WITHER(MobEffects.WITHER),
    HEALTH_BOOST(MobEffects.HEALTH_BOOST),
    ABSORPTION(MobEffects.ABSORPTION),
    SATURATION(MobEffects.SATURATION),
    NIGHT_VISION(MobEffects.NIGHT_VISION),
    WEAKNESS(MobEffects.WEAKNESS),
    HUNGER(MobEffects.HUNGER),
    BLINDNESS(MobEffects.BLINDNESS),
    NAUSEA(MobEffects.NAUSEA);

    private final Holder<MobEffect> effect;

    PotionEffectChoice(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    public Holder<MobEffect> effect() {
        return effect;
    }

    public static PotionEffectChoice fromNetworkId(int id) {
        PotionEffectChoice[] values = values();
        return values[Math.floorMod(id, values.length)];
    }
}
