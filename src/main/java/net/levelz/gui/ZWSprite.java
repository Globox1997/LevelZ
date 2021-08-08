package net.levelz.gui;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZWSprite extends WSprite {
    private List<OrderedText> text = Lists.newArrayList();
    private String name;
    private boolean info = false;

    public ZWSprite(String name, Identifier image, float u1, float v1, float u2, float v2) {
        super(image, u1, v1, u2, v2);
        this.name = name;
    }

    public ZWSprite(Identifier image) {
        super(image);
    }

    public ZWSprite(boolean info) {
        super(new Identifier("levelz:textures/gui/info_icon.png"));
        this.info = info;
    }

    public void addText(String string) {
        text.add(Text.of(string).asOrderedText());
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (info) {
            boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight());
            Identifier identifier;
            identifier = new Identifier("levelz:textures/gui/info_icon.png");
            if (hovered) {
                identifier = new Identifier("levelz:textures/gui/hovered_info_icon.png");
            }
            ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), new Texture(identifier), tint);
        } else
            super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        for (int i = 0; i < text.size(); i++) {
            tooltip.add(text.get(i));
        }
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        if (name != null) {
            MinecraftClient.getInstance().setScreen(new InfoScreen(getGui(name)));
        }
        return InputResult.IGNORED;
    }

    private LightweightGuiDescription getGui(String string) {
        switch (string) {
        case "mining":
            return new ListGui(string);
        case "alchemy":
            return new ListGui(string);
        default:
            return new InfoGui(string);
        }

    }

}