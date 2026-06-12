package me.portalgun.portalgunplus.network;

import me.portalgun.portalgunplus.First;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PotionSettingsPayload(int effectId, int level, int durationId) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(First.MOD_ID, "potion_settings");
    public static final Type<PotionSettingsPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PotionSettingsPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, PotionSettingsPayload::effectId,
                    ByteBufCodecs.INT, PotionSettingsPayload::level,
                    ByteBufCodecs.INT, PotionSettingsPayload::durationId,
                    PotionSettingsPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
