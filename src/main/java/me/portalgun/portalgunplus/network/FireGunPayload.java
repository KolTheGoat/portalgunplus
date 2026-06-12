package me.portalgun.portalgunplus.network;

import me.portalgun.portalgunplus.First;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record FireGunPayload(int gunId) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(First.MOD_ID, "fire_gun");
    public static final Type<FireGunPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FireGunPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, FireGunPayload::gunId, FireGunPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
