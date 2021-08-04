package net.levelz.gui;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ZWSprite extends WSprite {
    private List<OrderedText> text = Lists.newArrayList();

    public ZWSprite(Identifier image, float u1, float v1, float u2, float v2) {
        super(image, u1, v1, u2, v2);
    }

    public void addText(String string) {
        text.add(Text.of(string).asOrderedText());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addTooltip(TooltipBuilder tooltip) {
        for (int i = 0; i < text.size(); i++) {
            tooltip.add(text.get(i));
        }
    }

}