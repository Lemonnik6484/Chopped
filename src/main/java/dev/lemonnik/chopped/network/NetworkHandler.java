package dev.lemonnik.chopped.network;

import dev.lemonnik.chopped.registers.TagsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

//? if fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?}

//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
*/
//?}

public class NetworkHandler {
    public static void initialize() {
        //? if fabric {
        PayloadTypeRegistry.playC2S().register(ChiselPayload.TYPE, ChiselPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChiselPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> handle(payload, context.player()));
        });
        //?}
    }

    //? if neoforge {
    /*public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Chopped.MOD_ID);
        registrar.playToServer(ChiselPayload.TYPE, ChiselPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> handle(payload, (ServerPlayer) context.player()));
        });
    }*/
    //?}

    public static void sendToServer(ChiselPayload payload) {
        //? if fabric
        ClientPlayNetworking.send(payload);
        //? if neoforge
        //PacketDistributor.sendToServer(payload);
    }

    private static void handle(ChiselPayload payload, ServerPlayer player) {
        Block targetBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(payload.blockId()));

        BlockState currentState = player.level().getBlockState(payload.pos());
        Block currentBlock = currentState.getBlock();

        List<Block> variants = TagsRegistry.listFromBlock(currentBlock);

        if (variants != null && variants.contains(targetBlock)) {
            BlockState newState = targetBlock.defaultBlockState();
            player.level().setBlockAndUpdate(payload.pos(), newState);
        }
    }
}
