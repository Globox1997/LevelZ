package net.levelz.mixin.player;

import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;

import net.minecraft.client.network.ClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    // No use yet

    // @Inject(method = "<init>", at = @At(value = "TAIL"))
    // private void initMixin(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking,
    // boolean lastSprinting, CallbackInfo info) {
    // }
}