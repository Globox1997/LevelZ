package net.levelz.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");
    private static final RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);

    public LevelExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    protected int getBlockLight(LevelExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
        return MathHelper.clamp(super.getBlockLight(experienceOrbEntity, blockPos) + 7, 0, 15);
    }

    @Override
    public void render(LevelExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        int j = experienceOrbEntity.getOrbSize()*100;
        float h = (float)(j % 4 * 16) / 64.0F;
        float k = (float)(j % 4 * 16 + 16) / 64.0F;
        float l = (float)(j / 4 * 16) / 64.0F;
        float m = (float)(j / 4 * 16 + 16) / 64.0F;
        float r = ((float)experienceOrbEntity.age + g) / 2.0F;
        int s = Math.max(80,(int)((MathHelper.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F));
        int u = Math.max(100,(int)((MathHelper.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F));
        matrixStack.translate(0.0F, 0.1F, 0.0F);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        vertex(vertexConsumer, entry, -0.5F, -0.25F, u,s, 255, h, m, i);
        vertex(vertexConsumer, entry, 0.5F, -0.25F, u,s, 255, k, m, i);
        vertex(vertexConsumer, entry, 0.5F, 0.75F, u,s, 255, k, l, i);
        vertex(vertexConsumer, entry, -0.5F, 0.75F, u,s, 255, h, l, i);
        matrixStack.pop();
        super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(matrix, x, y, 0.0F).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public Identifier getTexture(LevelExperienceOrbEntity experienceOrbEntity) {
        return TEXTURE;
    }
}
