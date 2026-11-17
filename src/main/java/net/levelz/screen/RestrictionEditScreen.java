package net.levelz.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.editor.EditorClientData;
import net.levelz.editor.RestrictionEntry;
import net.levelz.editor.RestrictionOverrides;
import net.levelz.network.packet.EditorDeletePacket;
import net.levelz.network.packet.EditorResultPacket;
import net.levelz.network.packet.EditorSavePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class RestrictionEditScreen extends Screen {

    private static final int SLOT_SIZE = 18;
    private static final int MAX_SUGGESTIONS = 3;

    private final RestrictionEditorScreen parent;
    private boolean existingOverride;
    private int baseHash;
    private boolean awaitingRefresh = false;

    private String key;
    private boolean replace = false;
    private boolean remove = false;
    private final List<SkillValue> skills = new ArrayList<>();
    private final Map<String, List<String>> values = new LinkedHashMap<>();
    private final JsonObject extra = new JsonObject();
    private String category = "items";
    private String addText = "";

    private Component status = Component.empty();
    private final List<String> problems = new ArrayList<>();

    private EditBox addField;
    private ValueList valueList;
    private final List<Button> suggestionButtons = new ArrayList<>();
    private int columnX;
    private int inventoryX;
    private int inventoryY;
    private int inventoryRows;

    public RestrictionEditScreen(RestrictionEditorScreen parent, @Nullable RestrictionEntry entry) {
        super(Component.translatable("screen.levelz.restriction_edit"));
        this.parent = parent;
        for (String category : RestrictionEditorScreen.CATEGORIES) {
            this.values.put(category, new ArrayList<>());
        }
        if (entry == null) {
            this.key = "";
            this.existingOverride = false;
            this.baseHash = 0;
            this.skills.add(new SkillValue("", "1"));
        } else {
            this.existingOverride = RestrictionOverrides.SOURCE.equals(entry.file());
            this.key = this.existingOverride ? entry.key() : getUniqueOverrideKey(entry.key());
            this.baseHash = this.existingOverride ? entry.json().hashCode() : 0;
            this.load(entry.json());
        }
    }

    @Override
    protected void init() {
        this.columnX = Math.max(this.width / 2 - 20, 200);
        int columnWidth = this.width - this.columnX - 10;

        EditBox nameField = new EditBox(this.font, 10, 22, Math.min(174, this.columnX - 20), 18, Component.translatable("editor.levelz.name"));
        nameField.setMaxLength(128);
        nameField.setValue(this.key);
        nameField.setHint(Component.translatable("editor.levelz.name").withStyle(ChatFormatting.GRAY));
        nameField.setEditable(!this.existingOverride);
        nameField.setResponder(text -> this.key = text);
        this.addRenderableWidget(nameField);

        this.addRenderableWidget(Checkbox.builder(Component.translatable("editor.levelz.replace_option"), this.font).pos(this.columnX, 22).selected(this.replace)
                .onValueChange((checkbox, checked) -> this.replace = checked).build());
        this.addRenderableWidget(Checkbox.builder(Component.translatable("editor.levelz.remove_option"), this.font).pos(this.columnX + 90, 22).selected(this.remove)
                .onValueChange((checkbox, checked) -> this.remove = checked).build());

        int y = 58;
        for (SkillValue skill : this.skills) {
            List<String> options = new ArrayList<>(EditorClientData.SKILLS);
            if (skill.key.isEmpty() && !options.isEmpty()) {
                skill.key = options.getFirst();
            }
            if (!options.contains(skill.key)) {
                options.add(skill.key);
            }
            this.addRenderableWidget(CycleButton.<String>builder(Component::literal, skill.key).withValues(options).displayOnlyValue()
                    .create(10, y, 110, 20, Component.translatable("editor.levelz.skill"), (button, value) -> skill.key = value));
            EditBox levelField = new EditBox(this.font, 124, y + 1, 36, 18, Component.translatable("editor.levelz.level"));
            levelField.setValue(skill.level);
            levelField.setResponder(text -> skill.level = text);
            this.addRenderableWidget(levelField);
            this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.remove_short"), button -> {
                this.skills.remove(skill);
                this.rebuildWidgets();
            }).bounds(164, y, 20, 20).build());
            y += 22;
        }
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.add_skill"), button -> {
            this.skills.add(new SkillValue("", "1"));
            this.rebuildWidgets();
        }).bounds(10, y, 174, 20).build());

        this.inventoryX = 10;
        this.inventoryY = y + 36;
        this.inventoryRows = Mth.clamp((this.height - 48 - this.inventoryY) / SLOT_SIZE, 0, 4);

        this.addRenderableWidget(CycleButton.<String>builder(category -> Component.translatable("editor.levelz.category." + category), this.category).withValues(RestrictionEditorScreen.CATEGORIES)
                .create(this.columnX, 46, Math.min(200, columnWidth), 20, Component.translatable("editor.levelz.category"), (button, value) -> {
                    this.category = value;
                    this.addText = "";
                    this.rebuildWidgets();
                }));

        this.valueList = new ValueList(this.minecraft, columnWidth, Math.max(24, this.height - 170), 70);
        this.valueList.setX(this.columnX);
        this.valueList.setValues(this.values.get(this.category));
        this.addRenderableWidget(this.valueList);

        this.addField = new EditBox(this.font, this.columnX, this.height - 96, columnWidth - 50, 18, Component.translatable("editor.levelz.add_value"));
        this.addField.setMaxLength(1024);
        this.addField.setValue(this.addText);
        this.addField.setHint(Component.translatable("editor.levelz.hint." + this.category).withStyle(ChatFormatting.GRAY));
        this.addField.setResponder(text -> {
            this.addText = text;
            this.updateSuggestions();
        });
        this.addRenderableWidget(this.addField);
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.add"), button -> this.addValue(this.addField.getValue()))
                .bounds(this.columnX + columnWidth - 46, this.height - 97, 46, 20).build());

        int buttonY = this.height - 26;
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.save"), button -> this.save()).bounds(this.width / 2 - 155, buttonY, 100, 20).build());
        Button deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.delete"), button -> this.confirmDelete())
                .bounds(this.width / 2 - 50, buttonY, 100, 20).build());
        deleteButton.active = this.existingOverride;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).bounds(this.width / 2 + 55, buttonY, 100, 20).build());

        this.suggestionButtons.clear();
        this.updateSuggestions();
    }

    private void load(String json) {
        JsonObject jsonObject;
        try {
            JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonObject()) {
                return;
            }
            jsonObject = element.getAsJsonObject();
        } catch (Exception e) {
            return;
        }
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String field = entry.getKey();
            JsonElement value = entry.getValue();
            boolean isBoolean = value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
            if (field.equals("replace") && isBoolean) {
                this.replace = value.getAsBoolean();
            } else if (field.equals("remove") && isBoolean) {
                this.remove = value.getAsBoolean();
            } else if (field.equals("skills") && value.isJsonObject()) {
                for (Map.Entry<String, JsonElement> skill : value.getAsJsonObject().entrySet()) {
                    this.skills.add(new SkillValue(skill.getKey(), getString(skill.getValue())));
                }
            } else if (field.equals("enchantments") && value.isJsonObject()) {
                for (Map.Entry<String, JsonElement> enchantment : value.getAsJsonObject().entrySet()) {
                    this.values.get(field).add(enchantment.getKey() + " " + getString(enchantment.getValue()));
                }
            } else if (this.values.containsKey(field) && value.isJsonArray()) {
                for (JsonElement element : value.getAsJsonArray()) {
                    this.values.get(field).add(getString(element));
                }
            } else if (!(this.values.containsKey(field) && value.isJsonObject() && value.getAsJsonObject().isEmpty())) {
                this.extra.add(field, value);
            }
        }
    }

    @Nullable
    private JsonObject toJson() {
        JsonObject json = this.extra.deepCopy();
        if (this.replace) {
            json.addProperty("replace", true);
        }
        if (this.remove) {
            json.addProperty("remove", true);
        }
        JsonObject skillObject = new JsonObject();
        for (SkillValue skill : this.skills) {
            if (skill.key.isBlank()) {
                continue;
            }
            try {
                skillObject.addProperty(skill.key, Integer.parseInt(skill.level.trim()));
            } catch (NumberFormatException e) {
                this.setStatus(Component.translatable("editor.levelz.invalid_level", skill.key).withStyle(ChatFormatting.RED));
                return null;
            }
        }
        if (skillObject.size() > 0) {
            json.add("skills", skillObject);
        } else if (!this.remove) {
            this.setStatus(Component.translatable("editor.levelz.missing_skills").withStyle(ChatFormatting.RED));
            return null;
        }
        for (String category : RestrictionEditorScreen.CATEGORIES) {
            List<String> categoryValues = this.values.get(category);
            if (categoryValues.isEmpty()) {
                continue;
            }
            if (category.equals("enchantments")) {
                JsonObject enchantments = new JsonObject();
                for (String value : categoryValues) {
                    String[] parts = value.trim().split("\\s+");
                    int level = 1;
                    if (parts.length > 1) {
                        try {
                            level = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            this.setStatus(Component.translatable("editor.levelz.invalid_level", parts[0]).withStyle(ChatFormatting.RED));
                            return null;
                        }
                    }
                    enchantments.addProperty(parts[0], level);
                }
                json.add(category, enchantments);
            } else {
                JsonArray array = new JsonArray();
                categoryValues.forEach(array::add);
                json.add(category, array);
            }
        }
        return json;
    }

    private void save() {
        String name = this.key.trim();
        if (name.isEmpty()) {
            this.setStatus(Component.translatable("editor.levelz.invalid_key").withStyle(ChatFormatting.RED));
            return;
        }
        if (!this.existingOverride && overrideExists(name)) {
            this.setStatus(Component.translatable("editor.levelz.name_taken").withStyle(ChatFormatting.RED));
            return;
        }
        JsonObject json = this.toJson();
        if (json == null) {
            return;
        }
        this.key = name;
        ClientPlayNetworking.send(new EditorSavePacket(name, json.toString(), this.baseHash));
        this.setStatus(Component.translatable("editor.levelz.saving").withStyle(ChatFormatting.GRAY));
    }

    private void confirmDelete() {
        this.minecraft.gui.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ClientPlayNetworking.send(new EditorDeletePacket(this.key, this.baseHash));
            }
            this.minecraft.gui.setScreen(this);
        }, Component.translatable("editor.levelz.delete_confirm", this.key), Component.translatable("editor.levelz.delete_confirm_message")));
    }

    public void onResult(EditorResultPacket result) {
        if (!result.key().equals(this.key)) {
            return;
        }
        if (result.success() && result.message().equals("editor.levelz.deleted")) {
            this.minecraft.gui.setScreen(this.parent);
            this.parent.onResult(result);
            return;
        }
        this.problems.clear();
        this.problems.addAll(result.problems());
        Component message = Component.translatable(result.message(), result.arguments().toArray()).withStyle(result.success() ? ChatFormatting.GREEN : ChatFormatting.RED);
        if (!this.problems.isEmpty()) {
            message = message.copy().append(" ").append(Component.translatable("editor.levelz.problem_count", this.problems.size()).withStyle(ChatFormatting.RED));
        }
        this.status = message;
        if (result.success()) {
            this.awaitingRefresh = true;
        }
    }

    public void onDataRefreshed() {
        if (!this.awaitingRefresh) {
            return;
        }
        this.awaitingRefresh = false;
        for (RestrictionEntry entry : EditorClientData.ENTRIES) {
            if (RestrictionOverrides.SOURCE.equals(entry.file()) && entry.key().equals(this.key)) {
                this.baseHash = entry.json().hashCode();
                this.existingOverride = true;
            }
        }
        this.rebuildWidgets();
    }

    private void setStatus(Component status) {
        this.status = status;
        this.problems.clear();
    }

    private void addValue(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        List<String> categoryValues = this.values.get(this.category);
        if (!categoryValues.contains(trimmed)) {
            categoryValues.add(trimmed);
        }
        this.valueList.setValues(categoryValues);
        this.addField.setValue("");
    }

    private void removeValue(String value) {
        this.values.get(this.category).remove(value);
        this.valueList.setValues(this.values.get(this.category));
    }

    private void addFromStack(ItemStack stack) {
        switch (this.category) {
            case "items", "crafting" -> this.addValue(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            case "blocks", "mining" -> {
                if (stack.getItem() instanceof BlockItem blockItem) {
                    this.addValue(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString());
                } else {
                    this.setStatus(Component.translatable("editor.levelz.not_a_block").withStyle(ChatFormatting.RED));
                }
            }
            case "entities" -> {
                EntityType<?> spawnEggType = stack.getItem() instanceof SpawnEggItem ? SpawnEggItem.getType(stack) : null;
                if (spawnEggType != null) {
                    this.addValue(BuiltInRegistries.ENTITY_TYPE.getKey(spawnEggType).toString());
                } else {
                    this.setStatus(Component.translatable("editor.levelz.not_a_spawn_egg").withStyle(ChatFormatting.RED));
                }
            }
            case "components" -> {
                if (stack.getComponentsPatch().isEmpty()) {
                    this.setStatus(Component.translatable("editor.levelz.no_components").withStyle(ChatFormatting.RED));
                } else {
                    this.addValue(serializeStack(stack, this.minecraft.level.registryAccess()));
                }
            }
            case "enchantments" -> {
                ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
                if (enchantments.isEmpty()) {
                    this.setStatus(Component.translatable("editor.levelz.no_enchantments").withStyle(ChatFormatting.RED));
                }
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                    this.addValue(entry.getKey().getRegisteredName() + " " + entry.getIntValue());
                }
            }
            default -> this.setStatus(Component.translatable("editor.levelz.no_item_value").withStyle(ChatFormatting.RED));
        }
    }

    private void updateSuggestions() {
        for (Button button : this.suggestionButtons) {
            this.removeWidget(button);
        }
        this.suggestionButtons.clear();
        if (this.addField == null) {
            return;
        }
        int columnWidth = this.width - this.columnX - 10;
        List<String> suggestions = this.getSuggestions(this.addText);
        for (int i = 0; i < suggestions.size(); i++) {
            String suggestion = suggestions.get(i);
            this.suggestionButtons.add(this.addRenderableWidget(Button.builder(Component.literal(suggestion), button -> {
                this.addField.setValue(suggestion);
                this.setFocused(this.addField);
            }).bounds(this.columnX, this.height - 76 + i * 14, columnWidth - 50, 14).build()));
        }
    }

    private List<String> getSuggestions(String text) {
        String query = text.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty() || query.contains("[") || query.contains(" ") || this.minecraft.level == null) {
            return List.of();
        }
        boolean tag = query.startsWith("#");
        String search = tag ? query.substring(1) : query;
        Stream<Identifier> ids = switch (this.category) {
            case "items", "crafting", "components" -> getIds(BuiltInRegistries.ITEM, tag && !this.category.equals("components"));
            case "blocks", "mining" -> getIds(BuiltInRegistries.BLOCK, tag);
            case "entities" -> getIds(BuiltInRegistries.ENTITY_TYPE, tag);
            case "enchantments" -> getIds(this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT), tag);
            case "dimensions" -> tag || this.minecraft.getConnection() == null ? Stream.empty() : this.minecraft.getConnection().levels().stream().map(ResourceKey::identifier);
            default -> Stream.empty();
        };
        return ids.map(Identifier::toString).filter(id -> id.contains(search))
                .sorted(Comparator.comparing((String id) -> !id.startsWith(search) && !id.contains(":" + search)).thenComparing(Comparator.naturalOrder()))
                .limit(MAX_SUGGESTIONS).map(id -> tag ? "#" + id : id).toList();
    }

    private static String serializeStack(ItemStack stack, HolderLookup.Provider registries) {
        StringBuilder builder = new StringBuilder(stack.typeHolder().getRegisteredName());
        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        DataComponentPatch.SplitResult split = stack.getComponentsPatch().split();
        List<String> parts = new ArrayList<>();
        for (TypedDataComponent<?> component : split.added()) {
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type());
            Optional<Tag> value = component.encodeValue(ops).result();
            if (id != null && value.isPresent()) {
                parts.add(id + "=" + value.get());
            }
        }
        for (DataComponentType<?> type : split.removed()) {
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
            if (id != null) {
                parts.add("!" + id);
            }
        }
        if (!parts.isEmpty()) {
            builder.append('[').append(String.join(",", parts)).append(']');
        }
        return builder.toString();
    }

    private static Stream<Identifier> getIds(Registry<?> registry, boolean tag) {
        if (tag) {
            return registry.listTagIds().map(TagKey::location);
        }
        return registry.keySet().stream();
    }

    private static String getString(JsonElement element) {
        return element.isJsonPrimitive() ? element.getAsString() : element.toString();
    }

    private static boolean overrideExists(String key) {
        for (RestrictionEntry entry : EditorClientData.ENTRIES) {
            if (RestrictionOverrides.SOURCE.equals(entry.file()) && entry.key().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private static String getUniqueOverrideKey(String key) {
        String uniqueKey = key;
        int index = 2;
        while (overrideExists(uniqueKey)) {
            uniqueKey = key + "_" + index++;
        }
        return uniqueKey;
    }

    private int getInventorySlot(double mouseX, double mouseY) {
        if (this.minecraft.player == null || mouseX < this.inventoryX || mouseY < this.inventoryY) {
            return -1;
        }
        int column = (int) ((mouseX - this.inventoryX) / SLOT_SIZE);
        int row = (int) ((mouseY - this.inventoryY) / SLOT_SIZE);
        if (column >= 9 || row >= this.inventoryRows) {
            return -1;
        }
        return row * 9 + column;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int slot = this.getInventorySlot(mouseX, mouseY);
        if (slot >= 0) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(slot);
            if (!stack.isEmpty()) {
                this.addFromStack(stack);
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.addField != null && this.addField.isFocused()) {
            if (event.isConfirmation()) {
                this.addValue(this.addField.getValue());
                return true;
            }
            if (event.key() == InputConstants.KEY_TAB) {
                List<String> suggestions = this.getSuggestions(this.addText);
                if (!suggestions.isEmpty()) {
                    this.addField.setValue(suggestions.getFirst());
                    return true;
                }
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);
        context.text(this.font, Component.translatable("editor.levelz.skills"), 10, 48, 0xFFFFFF55);

        if (this.inventoryRows > 0 && this.minecraft.player != null) {
            context.text(this.font, Component.translatable("editor.levelz.inventory_hint"), this.inventoryX, this.inventoryY - 11, 0xFFFFFF55);
            ItemStack hoveredStack = ItemStack.EMPTY;
            for (int row = 0; row < this.inventoryRows; row++) {
                for (int column = 0; column < 9; column++) {
                    int x = this.inventoryX + column * SLOT_SIZE;
                    int y = this.inventoryY + row * SLOT_SIZE;
                    ItemStack stack = this.minecraft.player.getInventory().getItem(row * 9 + column);
                    boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
                    context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF373737);
                    context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, hovered ? 0xFFC5C5C5 : 0xFF8B8B8B);
                    context.item(stack, x + 1, y + 1);
                    context.itemDecorations(this.font, stack, x + 1, y + 1);
                    if (hovered) {
                        hoveredStack = stack;
                    }
                }
            }
            if (!hoveredStack.isEmpty()) {
                context.setTooltipForNextFrame(this.font, hoveredStack, mouseX, mouseY);
            }
        }

        int statusWidth = this.columnX - 20;
        context.text(this.font, Language.getInstance().getVisualOrder(this.font.substrByWidth(this.status, statusWidth)), 10, this.height - 40, 0xFFFFFFFF);
        if (!this.problems.isEmpty() && mouseX >= 10 && mouseX < 10 + statusWidth && mouseY >= this.height - 42 && mouseY < this.height - 30) {
            List<FormattedCharSequence> lines = new ArrayList<>();
            for (String problem : this.problems) {
                lines.addAll(this.font.split(Component.literal(problem).withStyle(ChatFormatting.RED), 300));
            }
            context.setTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }

    private static class SkillValue {

        private String key;
        private String level;

        private SkillValue(String key, String level) {
            this.key = key;
            this.level = level;
        }
    }

    private class ValueList extends ObjectSelectionList<ValueRow> {

        public ValueList(Minecraft client, int width, int height, int y) {
            super(client, width, height, y, 12);
        }

        public void setValues(List<String> values) {
            this.clearEntries();
            for (String value : values) {
                this.addEntry(new ValueRow(value));
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 12;
        }

        @Override
        protected int scrollBarX() {
            return this.getX() + this.width - 6;
        }
    }

    private class ValueRow extends ObjectSelectionList.Entry<ValueRow> {

        private final String value;

        public ValueRow(String value) {
            this.value = value;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.getContentX();
            int y = this.getContentY();
            int entryWidth = this.getContentWidth();
            context.text(RestrictionEditScreen.this.font, RestrictionEditScreen.this.font.plainSubstrByWidth(this.value, entryWidth - 14), x + 2, y + 1, 0xFFFFFFFF);
            boolean removeHovered = hovered && mouseX >= x + entryWidth - 10;
            context.text(RestrictionEditScreen.this.font, Component.translatable("editor.levelz.remove_short"), x + entryWidth - 8, y + 1, removeHovered ? 0xFFFF5555 : 0xFFAA0000);
        }

        @Override
        public Component getNarration() {
            return Component.literal(this.value);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (event.x() >= RestrictionEditScreen.this.valueList.getRowRight() - 10) {
                RestrictionEditScreen.this.minecraft.schedule(() -> RestrictionEditScreen.this.removeValue(this.value));
                return true;
            }
            RestrictionEditScreen.this.valueList.setSelected(this);
            return true;
        }
    }

}
