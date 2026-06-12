package me.portalgun.portalgunplus.manager;

import java.util.List;

import me.portalgun.portalgunplus.ModItems;
import me.portalgun.portalgunplus.PotionSettings;
import me.portalgun.portalgunplus.util.RaycastUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PotionManager {
    private static final double RANGE = 26.0;
    private static final double INNER_RADIUS = 1.5;
    private static final double TOTAL_RADIUS = 4.0;

    private PotionManager() {}

    public static void fire(ServerPlayer player) {
        if (!player.getMainHandItem().is(ModItems.POTION_GUN)) {
            return;
        }

        if (!CooldownManager.tryStart(player, CooldownManager.GunType.POTION)) {
            player.sendSystemMessage(Component.literal(
                    "Potion Gun cooldown: " + CooldownManager.remainingSeconds(player, CooldownManager.GunType.POTION) + "s"
            ), true);
            return;
        }

        ServerLevel level = player.level();
        LivingEntity directTarget = RaycastUtil.raycastLivingEntity(player, RANGE).orElse(player);
        Vec3 impact = directTarget.position();
        PotionSettings settings = PlayerSettingsManager.getPotionSettings(player.getUUID());

        AABB area = new AABB(impact, impact).inflate(TOTAL_RADIUS);
        List<LivingEntity> affected = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive() && entity.distanceToSqr(impact) <= TOTAL_RADIUS * TOTAL_RADIUS
        );

        for (LivingEntity entity : affected) {
            double distance = entity.position().distanceTo(impact);
            int levelValue = distance <= INNER_RADIUS ? settings.level() : Math.max(1, settings.level() - 1);
            entity.addEffect(new MobEffectInstance(
                    settings.effect().effect(),
                    settings.durationTicks(),
                    levelValue - 1,
                    false,
                    true,
                    true
            ));
        }

        level.sendParticles(ParticleTypes.WITCH, impact.x, impact.y + 0.5, impact.z, 50, 1.5, 1.0, 1.5, 0.03);
        level.sendParticles(ParticleTypes.ENCHANT, impact.x, impact.y + 0.5, impact.z, 30, 1.0, 0.8, 1.0, 0.02);
        level.playSound(null, player.blockPosition(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
