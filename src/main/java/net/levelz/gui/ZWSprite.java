package net.levelz.gui;

import java.util.List;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private String name;
    private int type = 0;
    @Nullable
    private MinecraftClient client;

    public ZWSprite(String name, Identifier image, MinecraftClient client, float u1, float v1, float u2, float v2) {
        super(image, u1, v1, u2, v2);
        this.name = name;
        this.client = client;
        this.type = 0;
    }

    public ZWSprite(String name, MinecraftClient client, int type) {
        super(new Identifier("levelz:textures/gui/info_icon.png"));
        this.type = type;
        this.client = client;
        this.name = name;
    }

    public void addText(String string) {
        text.add(Text.of(string).asOrderedText());
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (type != 0 && name != null) {
            Identifier identifier = new Identifier("levelz:textures/gui/info_icon.png");
            boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight());
            if (name.equals("info")) {
                if (hovered)
                    identifier = new Identifier("levelz:textures/gui/hovered_info_icon.png");
            } else if (type == 1)
                identifier = new Identifier("levelz:textures/gui/list_icon.png");
            else if (type == 2)
                identifier = new Identifier("levelz:textures/gui/clicked_list_icon.png");
            else if (type == 3 || type == 4) {
                identifier = new Identifier("levelz:textures/gui/crafting_book.png");
                if (hovered)
                    identifier = new Identifier("levelz:textures/gui/hovered_crafting_book.png");
            }
            paintFrame(matrices, x, y, new Texture(identifier));
        } else
            super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        for (int i = 0; i < text.size(); i++) {
            tooltip.add(text.get(i));
        }
    }

    // type 1: list, 2: clicked list, 3: recipe list

    @Override
    public InputResult onClick(int x, int y, int button) {
        if (client != null && name != null) {
            if (type == 1 || type == 3)
                client.setScreen(new InfoScreen(new ListGui(name, client)));
            else if (type == 4)
                client.setScreen(new LevelzScreen(new LevelzGui(client)));
            else
                client.setScreen(new InfoScreen(new InfoGui(name, client)));
        }
        return InputResult.IGNORED;
    }

}