package me.portalgun.portalgunplus.network;

import me.portalgun.portalgunplus.First;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PortalModePayload(int modeId) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(First.MOD_ID, "portal_mode");
    public static final Type<PortalModePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PortalModePayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, PortalModePayload::modeId, PortalModePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
