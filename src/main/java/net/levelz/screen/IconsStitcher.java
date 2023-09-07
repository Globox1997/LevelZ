package net.levelz.screen;

import net.levelz.init.RenderInit;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;

public class IconsStitcher{
    private static final ArrayList<Pair<Identifier, Pair<Integer, Integer>>> textures = new ArrayList<>();
    private static final int DEFAULT_SIZE = 16;

    static{
        addTexture(RenderInit.GUI_ICONS, 192, 256);
    }

    public static void addTexture(Identifier texture){
        addTexture(texture, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public static void addTexture(Identifier texture, int sizeX, int sizeY){
        textures.add(new Pair<>(texture, new Pair<>(sizeX, sizeY)));
    }

    public static void drawTexture(DrawContext context, int x, int y, int u, int v, int width, int height){
        int totalOffset = 0;
        for(var entry : textures){
            final Identifier taxtureID = entry.getLeft();
            final int textureWidth = entry.getRight().getLeft();
            final int textureHeight = entry.getRight().getRight();

            if (u - totalOffset >= textureWidth){
                totalOffset = totalOffset + textureWidth;
                continue;
            }

            final int texposX = u - totalOffset;
            final int texposY = v % textureHeight;
            context.drawTexture(taxtureID, x, y, texposX, texposY, width, height);
            return;
        }
    }

}
