package me.portalgun.portalgunplus.item;

import me.portalgun.portalgunplus.manager.ObsidianBoatManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ObsidianBoatItem extends Item {

    public ObsidianBoatItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var entityType = BuiltInRegistries.ENTITY_TYPE.getValue(
                Identifier.withDefaultNamespace("oak_boat")
        );

        if (entityType == null) {
            return InteractionResult.FAIL;
        }

        Entity boat = entityType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);

        if (boat == null) {
            return InteractionResult.FAIL;
        }

        boat.setPos(
                hit.getLocation().x,
                hit.getLocation().y + 0.15,
                hit.getLocation().z
        );

        boat.setYRot(player.getYRot());
        boat.addTag(ObsidianBoatManager.OBSIDIAN_BOAT_TAG);

        if (!level.noCollision(boat, boat.getBoundingBox())) {
            return InteractionResult.FAIL;
        }

        level.addFreshEntity(boat);
        ObsidianBoatManager.register(boat);

        ItemStack stack = player.getItemInHand(hand);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}