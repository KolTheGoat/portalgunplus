package me.portalgun.portalgunplus.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class InfiniteBucketItem extends Item {
    private final Block fluidBlock;

    public InfiniteBucketItem(Properties properties, Block fluidBlock) {
        super(properties);
        this.fluidBlock = fluidBlock;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        BlockPos placePos = hit.getBlockPos().relative(hit.getDirection());
        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            level.setBlock(placePos, fluidBlock.defaultBlockState(), Block.UPDATE_ALL);
        }

        return InteractionResult.SUCCESS;
    }
}
