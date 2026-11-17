package net.levelz.mixin.network;

import net.levelz.network.LevelServerPacket;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.game.GameProtocols;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameProtocols.class)
public class PlayStateFactoriesMixin {

    @Inject(method = "lambda$static$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/ProtocolInfoBuilder;addPacket(Lnet/minecraft/network/protocol/PacketType;Lnet/minecraft/network/codec/StreamCodec;)Lnet/minecraft/network/protocol/ProtocolInfoBuilder;", ordinal = 1))
    private static void method_55958Mixin(ProtocolInfoBuilder builder, CallbackInfo info) {
        builder.addPacket(LevelServerPacket.ADD_LEVEL_EXPERIENCE_ORB, OrbPacket.CODEC);
    }
}
