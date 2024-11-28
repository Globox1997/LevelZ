package net.levelz.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.LevelzMain;
import net.levelz.level.LevelManager;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.mixin.entity.VehicleEntityAccessor;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.registry.EnchantmentZ;
import net.levelz.screen.LevelScreen;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.*;

@Environment(EnvType.CLIENT)
public class LineWidget {

    private final MinecraftClient client;
    @Nullable
    private final Text text;
    @Nullable
    private final Map<Integer, PlayerRestriction> restrictions;
    private final int code;

    private Map<Integer, ItemStack> customStacks;
    private Map<Integer, Identifier> customImages;

    /**
     * @param code 0 = item, 1 = block, 2 = entity, 3 = enchantment
     */
    public LineWidget(MinecraftClient client, @Nullable Text text, @Nullable Map<Integer, PlayerRestriction> restrictions, int code) {
        this.client = client;
        this.text = text;
        this.restrictions = restrictions;
        this.code = code;

        if (this.code == 2) {
            this.customStacks = new HashMap<>();
            this.customImages = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(id);
                boolean imageExists = false;
                try {
                    client.getResourceManager().getResourceOrThrow(LevelzMain.identifierOf("textures/gui/sprites/entity/" + Registries.ENTITY_TYPE.getId(entityType).getPath() + ".png"));
                    imageExists = true;
                } catch (FileNotFoundException ignored) {
                }
                if (imageExists) {
                    this.customImages.put(id, LevelzMain.identifierOf("textures/gui/sprites/entity/" + Registries.ENTITY_TYPE.getId(entityType).getPath() + ".png"));
                } else if (SpawnEggItem.forEntity(entityType) != null) {
                    this.customStacks.put(id, new ItemStack(Objects.requireNonNull(SpawnEggItem.forEntity(entityType))));
                } else if (entityType.create(this.client.world) instanceof VehicleEntity vehicleEntity) {
                    this.customStacks.put(id, new ItemStack(((VehicleEntityAccessor) vehicleEntity).callAsItem()));
                } else {
                    this.customImages.put(id, LevelzMain.identifierOf("textures/gui/sprites/entity/default.png"));
                }
            }
        } else if (this.code == 3) {
            this.customStacks = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                EnchantmentZ enchantmentZ = EnchantmentRegistry.getEnchantmentZ(id);
                this.customStacks.put(id, EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantmentZ.getEntry(), enchantmentZ.getLevel())));
            }
        }
    }

    public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if (text != null) {
            drawContext.drawText(this.client.textRenderer, this.text, x, y + 4, 0x3F3F3F, false);
        } else {
            int separator = 0;
            boolean showTooltip = false;
            for (Map.Entry<Integer, PlayerRestriction> entry : this.restrictions.entrySet()) {
                Text tooltipTitle;
                drawContext.drawTexture(LevelScreen.ICON_TEXTURE, x + separator - 1, y - 1, 0, 148, 18, 18);
                if (this.code == 0) {
                    Item item = Registries.ITEM.get(entry.getKey());
                    tooltipTitle = item.getName();
                    drawContext.drawItem(Registries.ITEM.get(entry.getKey()).getDefaultStack(), x + separator, y);
                } else if (this.code == 1) {
                    Block block = Registries.BLOCK.get(entry.getKey());
                    tooltipTitle = block.getName();
                    drawContext.drawItem(block.asItem().getDefaultStack(), x + separator, y);
                } else if (this.code == 2) {
                    EntityType<?> entityType = Registries.ENTITY_TYPE.get(entry.getKey());
                    tooltipTitle = entityType.getName();
                    if (this.customStacks.containsKey(entry.getKey())) {
                        drawContext.drawItem(this.customStacks.get(entry.getKey()), x + separator, y);
                    } else {
                        drawContext.drawTexture(this.customImages.get(entry.getKey()), x + separator, y, 0, 0, 16, 16);
                    }
                } else {// if (this.code == 3) {
                    ItemStack stack = this.customStacks.get(entry.getKey());
                    RegistryEntry<Enchantment> enchantment = EnchantmentHelper.getEnchantments(stack).getEnchantments().stream().findFirst().get();
                    int level = stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getLevel(enchantment);
                    tooltipTitle = Enchantment.getName(enchantment, level);
                    drawContext.drawItem(stack, x + separator, y);
                }
                if (!showTooltip && LevelScreen.isPointWithinBounds(x + separator, y, 16, 16, mouseX, mouseY)) {
                    List<Text> tooltip = new ArrayList<>();
                    tooltip.add(tooltipTitle);
                    for (Map.Entry<Integer, Integer> restriction : entry.getValue().getSkillLevelRestrictions().entrySet()) {
                        tooltip.add(Text.of(LevelManager.SKILLS.get(restriction.getKey()).getText().getString() + " " + Text.translatable("text.levelz.gui.short_level", restriction.getValue()).getString()));
                    }
                    drawContext.drawTooltip(this.client.textRenderer, tooltip, mouseX, mouseY);
                    showTooltip = true;
                }
                separator += 18;
            }
        }
    }
}

