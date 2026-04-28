package dev.lemonnik.chopped.network;

import dev.lemonnik.chopped.Chopped;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ChiselPayload(BlockPos pos, String blockId) implements CustomPacketPayload {
    public static final Type<ChiselPayload> TYPE = new Type<>(Chopped.id("chisel_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChiselPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ChiselPayload::pos,
            ByteBufCodecs.STRING_UTF8, ChiselPayload::blockId,
            ChiselPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
