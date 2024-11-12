package net.levelz.mixin.network;

import net.levelz.network.LevelServerPacket;
import net.levelz.network.packet.OrbPacket;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.state.PlayStateFactories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayStateFactories.class)
public class PlayStateFactoriesMixin {

    @Inject(method = "method_55958", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkStateBuilder;add(Lnet/minecraft/network/packet/PacketType;Lnet/minecraft/network/codec/PacketCodec;)Lnet/minecraft/network/NetworkStateBuilder;", ordinal = 1))
    private static void method_55958Mixin(NetworkStateBuilder builder, CallbackInfo info) {
        builder.add(LevelServerPacket.ADD_LEVEL_EXPERIENCE_ORB, OrbPacket.CODEC);
    }
}
