package me.portalgun.portalgunplus.manager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class ObsidianBoatManager {

    public static final String OBSIDIAN_BOAT_TAG =
            "portalgunplus_obsidian_boat";

    private static final Set<UUID> TRACKED_BOATS =
            new HashSet<>();

    private ObsidianBoatManager() {
    }

    public static void register(Entity boat) {
        TRACKED_BOATS.add(boat.getUUID());
    }

    public static void tick(MinecraftServer server) {
        Iterator<UUID> iterator = TRACKED_BOATS.iterator();

        while (iterator.hasNext()) {
            UUID boatId = iterator.next();
            Entity boat = findEntity(server, boatId);

            if (boat == null || boat.isRemoved()) {
                iterator.remove();
                continue;
            }

            Double lavaSurfaceY = findLavaSurfaceY(boat);

            if (lavaSurfaceY == null) {
                boat.setNoGravity(false);
                continue;
            }

            boat.clearFire();
            boat.setNoGravity(true);

            Vec3 oldVelocity = boat.getDeltaMovement();

            double xVelocity = oldVelocity.x * 0.88;
            double zVelocity = oldVelocity.z * 0.88;

            Entity passenger = boat.getFirstPassenger();

            if (passenger instanceof Player player) {
                double forward = player.zza;
                double sideways = player.xxa;

                double angle =
                        Math.toRadians(player.getYRot());

                double forwardX =
                        -Math.sin(angle) * forward;

                double forwardZ =
                        Math.cos(angle) * forward;

                double sidewaysX =
                        Math.cos(angle) * sideways;

                double sidewaysZ =
                        Math.sin(angle) * sideways;

                double acceleration = 0.045;

                xVelocity +=
                        (forwardX + sidewaysX)
                                * acceleration;

                zVelocity +=
                        (forwardZ + sidewaysZ)
                                * acceleration;

                boat.setYRot(player.getYRot());
            }

            double speed =
                    Math.sqrt(
                            xVelocity * xVelocity
                                    + zVelocity * zVelocity
                    );

            double maximumSpeed = 0.45;

            if (speed > maximumSpeed) {
                double scale =
                        maximumSpeed / speed;

                xVelocity *= scale;
                zVelocity *= scale;
            }

            boat.setPos(
                    boat.getX(),
                    lavaSurfaceY + 0.05,
                    boat.getZ()
            );

            boat.setDeltaMovement(
                    xVelocity,
                    0.0,
                    zVelocity
            );
        }
    }

    private static Double findLavaSurfaceY(
            Entity boat
    ) {
        BlockPos origin =
                boat.blockPosition();

        for (int offset = 4;
             offset >= -4;
             offset--) {

            BlockPos position =
                    origin.offset(
                            0,
                            offset,
                            0
                    );

            boolean isLava =
                    boat.level()
                            .getFluidState(position)
                            .is(FluidTags.LAVA);

            boolean lavaAbove =
                    boat.level()
                            .getFluidState(
                                    position.above()
                            )
                            .is(FluidTags.LAVA);

            if (isLava && !lavaAbove) {
                return position.getY() + 1.0;
            }
        }

        return null;
    }

    private static Entity findEntity(
            MinecraftServer server,
            UUID uuid
    ) {
        for (ServerLevel level :
                server.getAllLevels()) {

            Entity entity =
                    level.getEntity(uuid);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }
}