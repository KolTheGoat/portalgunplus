package me.portalgun.portalgunplus.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import me.portalgun.portalgunplus.ModItems;
import me.portalgun.portalgunplus.PortalMode;
import me.portalgun.portalgunplus.util.RaycastUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class PortalManager {
    private record ActivePortal(
            ServerLevel level,
            Map<BlockPos, BlockState> originalBlocks,
            Vec3 center,
            long expiresAtTick,
            UUID ownerId
    ) {}

    private static final List<ActivePortal> ACTIVE_PORTALS = new ArrayList<>();
    private static final Map<UUID, Long> RECENT_TELEPORTS = new HashMap<>();

    private PortalManager() {}

    public static void fire(ServerPlayer player) {
        if (!player.getMainHandItem().is(ModItems.PORTAL_GUN)) {
            return;
        }

        if (!CooldownManager.tryStart(player, CooldownManager.GunType.PORTAL)) {
            player.sendSystemMessage(Component.literal(
                    "Portal Gun cooldown: " + CooldownManager.remainingSeconds(player, CooldownManager.GunType.PORTAL) + "s"
            ), true);
            return;
        }

        BlockHitResult hit = RaycastUtil.raycastBlock(player, 50.0);
        if (hit.getType() == HitResult.Type.MISS) {
            player.sendSystemMessage(Component.literal("No block in range."), true);
            return;
        }

        List<BlockPos> portalBlocks = buildPortalShape(hit);
        Map<BlockPos, BlockState> originals = new HashMap<>();

        for (BlockPos pos : portalBlocks) {
            BlockState state = player.level().getBlockState(pos);
            if (!state.canBeReplaced() && !state.isAir()) {
                player.sendSystemMessage(Component.literal("Portal area is blocked."), true);
                return;
            }
            originals.put(pos.immutable(), state);
        }

        Direction.Axis portalAxis = hit.getDirection().getAxis() == Direction.Axis.X
                ? Direction.Axis.Z
                : Direction.Axis.X;

        BlockState portalState = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, portalAxis);

        for (BlockPos pos : portalBlocks) {
            player.level().setBlock(pos, portalState, Block.UPDATE_CLIENTS);
        }

        Vec3 center = Vec3.atCenterOf(portalBlocks.get(2));
        long expiresAt = player.level().getGameTime() + 10 * 20L;
        ACTIVE_PORTALS.add(new ActivePortal(player.level(), originals, center, expiresAt, player.getUUID()));

        player.level().sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 80, 1.0, 1.5, 0.2, 0.05);
        player.level().sendParticles(ParticleTypes.PORTAL, center.x, center.y, center.z, 30, 0.8, 1.0, 0.2, 0.02);
        player.level().playSound(null, BlockPos.containing(center), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.8F, 1.2F);
    }

    public static void tick(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        RECENT_TELEPORTS.entrySet().removeIf(entry -> now - entry.getValue() > 40);

        Iterator<ActivePortal> iterator = ACTIVE_PORTALS.iterator();
        while (iterator.hasNext()) {
            ActivePortal portal = iterator.next();

            if (portal.level.getGameTime() >= portal.expiresAtTick) {
                restore(portal);
                iterator.remove();
                continue;
            }

            for (ServerPlayer player : List.copyOf(portal.level.players())) {
                if (portal.center.distanceToSqr(player.position()) > 1.5 * 1.5) {
                    continue;
                }

                Long previous = RECENT_TELEPORTS.get(player.getUUID());
                if (previous != null && now - previous < 20) {
                    continue;
                }

                teleport(player, PlayerSettingsManager.getPortalMode(portal.ownerId));
                RECENT_TELEPORTS.put(player.getUUID(), now);
            }
        }
    }

    private static List<BlockPos> buildPortalShape(BlockHitResult hit) {
        BlockPos base = hit.getBlockPos().relative(hit.getDirection());
        Direction side = hit.getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

        List<BlockPos> blocks = new ArrayList<>();
        for (int y = 0; y < 3; y++) {
            blocks.add(base.above(y));
            blocks.add(base.relative(side).above(y));
        }
        return blocks;
    }

    private static void restore(ActivePortal portal) {
        portal.originalBlocks.forEach((pos, state) -> portal.level.setBlock(pos, state, Block.UPDATE_ALL));
    }

    private static void teleport(ServerPlayer player, PortalMode mode) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }

        switch (mode) {
            case NETHER -> teleportNether(player, server);
            case END -> teleportEnd(player, server, 0, 0);
            case END_GATEWAY -> {
                int distance = ThreadLocalRandom.current().nextInt(1000, 2501);
                double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2.0);
                teleportEnd(player, server, (int) (Math.cos(angle) * distance), (int) (Math.sin(angle) * distance));
            }
            case SPAWN -> {
                ServerLevel overworld = server.overworld();
                BlockPos spawn = overworld.getRespawnData().pos();
                teleportTo(player, overworld, spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5);
            }
        }
    }

    private static void teleportNether(ServerPlayer player, MinecraftServer server) {
        ServerLevel target;
        double scale;

        if (player.level().dimension() == Level.NETHER) {
            target = server.overworld();
            scale = 8.0;
        } else {
            target = server.getLevel(Level.NETHER);
            scale = 0.125;
        }

        if (target == null) {
            return;
        }

        double x = player.getX() * scale;
        double z = player.getZ() * scale;
        BlockPos safe = safeSurface(target, (int) x, (int) z);
        teleportTo(player, target, safe.getX() + 0.5, safe.getY() + 1.0, safe.getZ() + 0.5);
    }

    private static void teleportEnd(ServerPlayer player, MinecraftServer server, int x, int z) {
        ServerLevel end = server.getLevel(Level.END);
        if (end == null) {
            return;
        }

        BlockPos safe = safeSurface(end, x, z);
        teleportTo(player, end, safe.getX() + 0.5, safe.getY() + 1.0, safe.getZ() + 0.5);
    }

    private static BlockPos safeSurface(ServerLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, Math.max(level.getMinY() + 2, y), z);
    }

    private static void teleportTo(ServerPlayer player, ServerLevel target, double x, double y, double z) {
        player.teleportTo(target, x, y, z, Set.of(), player.getYRot(), player.getXRot(), true);
        target.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
