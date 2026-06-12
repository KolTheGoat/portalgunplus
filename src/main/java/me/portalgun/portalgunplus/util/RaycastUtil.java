package me.portalgun.portalgunplus.util;

import java.util.Comparator;
import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class RaycastUtil {
    private RaycastUtil() {}

    public static BlockHitResult raycastBlock(ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));
        return player.level().clip(new ClipContext(
                start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
        ));
    }

    public static Optional<LivingEntity> raycastLivingEntity(ServerPlayer player, double range) {
        ServerLevel level = player.level();
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));

        AABB box = new AABB(start, end).inflate(1.5);
        BlockHitResult blockHit = level.clip(new ClipContext(
                start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
        ));
        double maximumDistanceSquared = blockHit.getType() == HitResult.Type.MISS
                ? range * range
                : start.distanceToSqr(blockHit.getLocation());

        return level.getEntitiesOfClass(LivingEntity.class, box, entity -> entity != player && entity.isAlive())
                .stream()
                .filter(entity -> distanceToSegmentSquared(entity.getBoundingBox().getCenter(), start, end) <= 1.75)
                .filter(entity -> start.distanceToSqr(entity.getBoundingBox().getCenter()) <= maximumDistanceSquared)
                .min(Comparator.comparingDouble(entity -> start.distanceToSqr(entity.getBoundingBox().getCenter())));
    }

    private static double distanceToSegmentSquared(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSquared = segment.lengthSqr();
        if (lengthSquared == 0.0) {
            return point.distanceToSqr(start);
        }

        double projection = point.subtract(start).dot(segment) / lengthSquared;
        projection = Math.max(0.0, Math.min(1.0, projection));
        Vec3 closestPoint = start.add(segment.scale(projection));
        return point.distanceToSqr(closestPoint);
    }
}
