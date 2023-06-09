package net.levelz.entity.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

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
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class LevelExperienceOrbEntityRenderer extends EntityRenderer<LevelExperienceOrbEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/experience_orb.png");
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
        int j = experienceOrbEntity.getOrbSize();
        float h = (float) (j % 4 * 16 + 0) / 64.0f;
        float k = (float) (j % 4 * 16 + 16) / 64.0f;
        float l = (float) (j / 4 * 16 + 0) / 64.0f;
        float m = (float) (j / 4 * 16 + 16) / 64.0f;

        // blinking
        // float r = ((float) experienceOrbEntity.age + g) / 2.0f;
        // int u = (int) ((MathHelper.sin(r + 0.0f) + 1.0f) * 0.5f * 255.0f);
        // int s = (int) ((MathHelper.sin(r + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        int s = 63;
        int t = 201;
        int u = 255;

        matrixStack.translate(0.0, 0.1f, 0.0);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

        matrixStack.scale(0.3f, 0.3f, 0.3f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, -0.25f, s, t, u, h, m, i);
        LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, -0.25f, s, t, u, k, m, i);
        LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, 0.75f, s, t, u, k, l, i);
        LevelExperienceOrbEntityRenderer.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, 0.75f, s, t, u, h, l, i);
        matrixStack.pop();
        super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public Identifier getTexture(LevelExperienceOrbEntity experienceOrbEntity) {
        return TEXTURE;
    }
}
