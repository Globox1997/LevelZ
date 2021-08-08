package net.levelz.gui;

import java.util.List;

import com.google.common.collect.Lists;

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
    private int type = 0;

    public ZWSprite(String name, Identifier image, float u1, float v1, float u2, float v2) {
        super(image, u1, v1, u2, v2);
        this.name = name;
    }

    public ZWSprite(Identifier image) {
        super(image);
    }

    public ZWSprite(int type) {
        super(new Identifier("levelz:textures/gui/info_icon.png"));
        this.type = type;
    }

    public void addText(String string) {
        text.add(Text.of(string).asOrderedText());
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (type != 0) {
            Identifier identifier = new Identifier("levelz:textures/gui/info_icon.png");
            if (type == 1) {
                boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight());
                if (hovered) {
                    identifier = new Identifier("levelz:textures/gui/hovered_info_icon.png");
                }
            } else if (type == 2 || type == 3) {
                identifier = new Identifier("levelz:textures/gui/list_icon.png");
            } else if (type == 4 || type == 5) {
                identifier = new Identifier("levelz:textures/gui/clicked_list_icon.png");
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
            MinecraftClient.getInstance().setScreen(new InfoScreen(new InfoGui(name)));
        } else if (type == 2) {
            MinecraftClient.getInstance().setScreen(new InfoScreen(new ListGui("mining")));
        } else if (type == 3) {
            MinecraftClient.getInstance().setScreen(new InfoScreen(new ListGui("alchemy")));
        } else if (type == 4) {
            MinecraftClient.getInstance().setScreen(new InfoScreen(new InfoGui("mining")));
        } else if (type == 5) {
            MinecraftClient.getInstance().setScreen(new InfoScreen(new InfoGui("alchemy")));
        }
        return InputResult.IGNORED;
    }

}