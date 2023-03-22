package net.levelz.gui.widget;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ZWItemSprite extends WWidget {

    private List<OrderedText> text = Lists.newArrayList();
    private Identifier identifier;
    private MinecraftClient client;

    public ZWItemSprite(Identifier identifier, MinecraftClient client) {
        this.identifier = identifier;
        this.client = client;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        client.getItemRenderer().renderGuiItemIcon(Registry.ITEM.get(identifier).getDefaultStack(), x, y);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        for (int i = 0; i < text.size(); i++) {
            tooltip.add(text.get(i));
        }
    }

    public void addText(String string) {
        text.add(Text.of(string).asOrderedText());
    }

}
