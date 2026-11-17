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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.*;

@Environment(EnvType.CLIENT)
public class LineWidget {

    private final Minecraft client;
    @Nullable
    private final Component text;
    @Nullable
    private final Map<Integer, PlayerRestriction> restrictions;
    private final int code;

    private Map<Integer, ItemStack> customStacks;
    private Map<Integer, Identifier> customImages;

    /**
     * @param code 0 = item, 1 = block, 2 = entity, 3 = enchantment
     */
    public LineWidget(Minecraft client, @Nullable Component text, @Nullable Map<Integer, PlayerRestriction> restrictions, int code) {
        this.client = client;
        this.text = text;
        this.restrictions = restrictions;
        this.code = code;

        if (this.code == 2) {
            this.customStacks = new HashMap<>();
            this.customImages = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.byId(id);
                boolean imageExists = false;
                try {
                    client.getResourceManager().getResourceOrThrow(LevelzMain.identifierOf("textures/gui/sprites/entity/" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + ".png"));
                    imageExists = true;
                } catch (FileNotFoundException ignored) {
                }
                if (imageExists) {
                    this.customImages.put(id, LevelzMain.identifierOf("textures/gui/sprites/entity/" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + ".png"));
                } else if (SpawnEggItem.byId(entityType).isPresent()) {
                    this.customStacks.put(id, new ItemStack(SpawnEggItem.byId(entityType).get()));
                } else if (entityType.create(this.client.level, EntitySpawnReason.LOAD) instanceof VehicleEntity vehicleEntity) {
                    this.customStacks.put(id, new ItemStack(((VehicleEntityAccessor) vehicleEntity).callAsItem()));
                } else {
                    this.customImages.put(id, LevelzMain.identifierOf("textures/gui/sprites/entity/default.png"));
                }
            }
        } else if (this.code == 3) {
            this.customStacks = new HashMap<>();
            for (Integer id : this.restrictions.keySet()) {
                EnchantmentZ enchantmentZ = EnchantmentRegistry.getEnchantmentZ(id);
                this.customStacks.put(id, EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentZ.getEntry(), enchantmentZ.getLevel())));
            }
        }
    }

    public void render(GuiGraphicsExtractor drawContext, int x, int y, int mouseX, int mouseY) {
        if (text != null) {
            drawContext.text(this.client.font, this.text, x, y + 4, 0xFF3F3F3F, false);
        } else {
            int separator = 0;
            boolean showTooltip = false;
            for (Map.Entry<Integer, PlayerRestriction> entry : this.restrictions.entrySet()) {
                Component tooltipTitle;
                drawContext.blit(RenderPipelines.GUI_TEXTURED, LevelScreen.ICON_TEXTURE, x + separator - 1, y - 1, 0, 148, 18, 18, 256, 256);
                if (this.code == 0) {
                    Item item = BuiltInRegistries.ITEM.byId(entry.getKey());
                    tooltipTitle = Component.translatable(item.getDescriptionId());
                    drawContext.item(BuiltInRegistries.ITEM.byId(entry.getKey()).getDefaultInstance(), x + separator, y);
                } else if (this.code == 1) {
                    Block block = BuiltInRegistries.BLOCK.byId(entry.getKey());
                    tooltipTitle = block.getName();
                    drawContext.item(block.asItem().getDefaultInstance(), x + separator, y);
                } else if (this.code == 2) {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.byId(entry.getKey());
                    tooltipTitle = entityType.getDescription();
                    if (this.customStacks.containsKey(entry.getKey())) {
                        drawContext.item(this.customStacks.get(entry.getKey()), x + separator, y);
                    } else {
                        drawContext.blit(RenderPipelines.GUI_TEXTURED, this.customImages.get(entry.getKey()), x + separator, y, 0, 0, 16, 16, 16, 16);
                    }
                } else {// if (this.code == 3) {
                    ItemStack stack = this.customStacks.get(entry.getKey());
                    Holder<Enchantment> enchantment = EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().findFirst().get();
                    int level = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(enchantment);
                    tooltipTitle = Enchantment.getFullname(enchantment, level);
                    drawContext.item(stack, x + separator, y);
                }
                if (!showTooltip && LevelScreen.isPointWithinBounds(x + separator, y, 16, 16, mouseX, mouseY)) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(tooltipTitle);
                    for (Map.Entry<Integer, Integer> restriction : entry.getValue().getSkillLevelRestrictions().entrySet()) {
                        tooltip.add(Component.nullToEmpty(LevelManager.SKILLS.get(restriction.getKey()).getText().getString() + " " + Component.translatable("text.levelz.gui.short_level", restriction.getValue()).getString()));
                    }
                    drawContext.setComponentTooltipForNextFrame(this.client.font, tooltip, mouseX, mouseY);
                    showTooltip = true;
                }
                separator += 18;
            }
        }
    }
}

