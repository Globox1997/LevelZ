package net.levelz.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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

    private Text status = Text.empty();
    private final List<String> problems = new ArrayList<>();

    private TextFieldWidget addField;
    private ValueList valueList;
    private final List<ButtonWidget> suggestionButtons = new ArrayList<>();
    private int columnX;
    private int inventoryX;
    private int inventoryY;
    private int inventoryRows;

    public RestrictionEditScreen(RestrictionEditorScreen parent, @Nullable RestrictionEntry entry) {
        super(Text.translatable("screen.levelz.restriction_edit"));
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

        TextFieldWidget nameField = new TextFieldWidget(this.textRenderer, 10, 22, Math.min(174, this.columnX - 20), 18, Text.translatable("editor.levelz.name"));
        nameField.setMaxLength(128);
        nameField.setText(this.key);
        nameField.setPlaceholder(Text.translatable("editor.levelz.name").formatted(Formatting.GRAY));
        nameField.setEditable(!this.existingOverride);
        nameField.setChangedListener(text -> this.key = text);
        this.addDrawableChild(nameField);

        this.addDrawableChild(CheckboxWidget.builder(Text.translatable("editor.levelz.replace_option"), this.textRenderer).pos(this.columnX, 22).checked(this.replace)
                .callback((checkbox, checked) -> this.replace = checked).build());
        this.addDrawableChild(CheckboxWidget.builder(Text.translatable("editor.levelz.remove_option"), this.textRenderer).pos(this.columnX + 90, 22).checked(this.remove)
                .callback((checkbox, checked) -> this.remove = checked).build());

        int y = 58;
        for (SkillValue skill : this.skills) {
            List<String> options = new ArrayList<>(EditorClientData.SKILLS);
            if (skill.key.isEmpty() && !options.isEmpty()) {
                skill.key = options.getFirst();
            }
            if (!options.contains(skill.key)) {
                options.add(skill.key);
            }
            this.addDrawableChild(CyclingButtonWidget.<String>builder(Text::literal).values(options).initially(skill.key).omitKeyText()
                    .build(10, y, 110, 20, Text.translatable("editor.levelz.skill"), (button, value) -> skill.key = value));
            TextFieldWidget levelField = new TextFieldWidget(this.textRenderer, 124, y + 1, 36, 18, Text.translatable("editor.levelz.level"));
            levelField.setText(skill.level);
            levelField.setChangedListener(text -> skill.level = text);
            this.addDrawableChild(levelField);
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.remove_short"), button -> {
                this.skills.remove(skill);
                this.clearAndInit();
            }).dimensions(164, y, 20, 20).build());
            y += 22;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.add_skill"), button -> {
            this.skills.add(new SkillValue("", "1"));
            this.clearAndInit();
        }).dimensions(10, y, 174, 20).build());

        this.inventoryX = 10;
        this.inventoryY = y + 36;
        this.inventoryRows = MathHelper.clamp((this.height - 48 - this.inventoryY) / SLOT_SIZE, 0, 4);

        this.addDrawableChild(CyclingButtonWidget.<String>builder(category -> Text.translatable("editor.levelz.category." + category)).values(RestrictionEditorScreen.CATEGORIES)
                .initially(this.category).build(this.columnX, 46, Math.min(200, columnWidth), 20, Text.translatable("editor.levelz.category"), (button, value) -> {
                    this.category = value;
                    this.addText = "";
                    this.clearAndInit();
                }));

        this.valueList = new ValueList(this.client, columnWidth, Math.max(24, this.height - 170), 70);
        this.valueList.setX(this.columnX);
        this.valueList.setValues(this.values.get(this.category));
        this.addDrawableChild(this.valueList);

        this.addField = new TextFieldWidget(this.textRenderer, this.columnX, this.height - 96, columnWidth - 50, 18, Text.translatable("editor.levelz.add_value"));
        this.addField.setMaxLength(1024);
        this.addField.setText(this.addText);
        this.addField.setPlaceholder(Text.translatable("editor.levelz.hint." + this.category).formatted(Formatting.GRAY));
        this.addField.setChangedListener(text -> {
            this.addText = text;
            this.updateSuggestions();
        });
        this.addDrawableChild(this.addField);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.add"), button -> this.addValue(this.addField.getText()))
                .dimensions(this.columnX + columnWidth - 46, this.height - 97, 46, 20).build());

        int buttonY = this.height - 26;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.save"), button -> this.save()).dimensions(this.width / 2 - 155, buttonY, 100, 20).build());
        ButtonWidget deleteButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.delete"), button -> this.confirmDelete())
                .dimensions(this.width / 2 - 50, buttonY, 100, 20).build());
        deleteButton.active = this.existingOverride;
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 + 55, buttonY, 100, 20).build());

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
                this.setStatus(Text.translatable("editor.levelz.invalid_level", skill.key).formatted(Formatting.RED));
                return null;
            }
        }
        if (skillObject.size() > 0) {
            json.add("skills", skillObject);
        } else if (!this.remove) {
            this.setStatus(Text.translatable("editor.levelz.missing_skills").formatted(Formatting.RED));
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
                            this.setStatus(Text.translatable("editor.levelz.invalid_level", parts[0]).formatted(Formatting.RED));
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
            this.setStatus(Text.translatable("editor.levelz.invalid_key").formatted(Formatting.RED));
            return;
        }
        if (!this.existingOverride && overrideExists(name)) {
            this.setStatus(Text.translatable("editor.levelz.name_taken").formatted(Formatting.RED));
            return;
        }
        JsonObject json = this.toJson();
        if (json == null) {
            return;
        }
        this.key = name;
        ClientPlayNetworking.send(new EditorSavePacket(name, json.toString(), this.baseHash));
        this.setStatus(Text.translatable("editor.levelz.saving").formatted(Formatting.GRAY));
    }

    private void confirmDelete() {
        this.client.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ClientPlayNetworking.send(new EditorDeletePacket(this.key, this.baseHash));
            }
            this.client.setScreen(this);
        }, Text.translatable("editor.levelz.delete_confirm", this.key), Text.translatable("editor.levelz.delete_confirm_message")));
    }

    public void onResult(EditorResultPacket result) {
        if (!result.key().equals(this.key)) {
            return;
        }
        if (result.success() && result.message().equals("editor.levelz.deleted")) {
            this.client.setScreen(this.parent);
            this.parent.onResult(result);
            return;
        }
        this.problems.clear();
        this.problems.addAll(result.problems());
        Text message = Text.translatable(result.message(), result.arguments().toArray()).formatted(result.success() ? Formatting.GREEN : Formatting.RED);
        if (!this.problems.isEmpty()) {
            message = message.copy().append(" ").append(Text.translatable("editor.levelz.problem_count", this.problems.size()).formatted(Formatting.RED));
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
        this.clearAndInit();
    }

    private void setStatus(Text status) {
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
        this.addField.setText("");
    }

    private void removeValue(String value) {
        this.values.get(this.category).remove(value);
        this.valueList.setValues(this.values.get(this.category));
    }

    private void addFromStack(ItemStack stack) {
        switch (this.category) {
            case "items", "crafting" -> this.addValue(Registries.ITEM.getId(stack.getItem()).toString());
            case "blocks", "mining" -> {
                if (stack.getItem() instanceof BlockItem blockItem) {
                    this.addValue(Registries.BLOCK.getId(blockItem.getBlock()).toString());
                } else {
                    this.setStatus(Text.translatable("editor.levelz.not_a_block").formatted(Formatting.RED));
                }
            }
            case "entities" -> {
                if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                    this.addValue(Registries.ENTITY_TYPE.getId(spawnEggItem.getEntityType(stack)).toString());
                } else {
                    this.setStatus(Text.translatable("editor.levelz.not_a_spawn_egg").formatted(Formatting.RED));
                }
            }
            case "components" -> {
                if (stack.getComponentChanges().isEmpty()) {
                    this.setStatus(Text.translatable("editor.levelz.no_components").formatted(Formatting.RED));
                } else {
                    this.addValue(new ItemStackArgument(stack.getRegistryEntry(), stack.getComponentChanges()).asString(this.client.world.getRegistryManager()));
                }
            }
            case "enchantments" -> {
                ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
                if (enchantments.isEmpty()) {
                    this.setStatus(Text.translatable("editor.levelz.no_enchantments").formatted(Formatting.RED));
                }
                for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
                    this.addValue(entry.getKey().getIdAsString() + " " + entry.getIntValue());
                }
            }
            default -> this.setStatus(Text.translatable("editor.levelz.no_item_value").formatted(Formatting.RED));
        }
    }

    private void updateSuggestions() {
        for (ButtonWidget button : this.suggestionButtons) {
            this.remove(button);
        }
        this.suggestionButtons.clear();
        if (this.addField == null) {
            return;
        }
        int columnWidth = this.width - this.columnX - 10;
        List<String> suggestions = this.getSuggestions(this.addText);
        for (int i = 0; i < suggestions.size(); i++) {
            String suggestion = suggestions.get(i);
            this.suggestionButtons.add(this.addDrawableChild(ButtonWidget.builder(Text.literal(suggestion), button -> {
                this.addField.setText(suggestion);
                this.setFocused(this.addField);
            }).dimensions(this.columnX, this.height - 76 + i * 14, columnWidth - 50, 14).build()));
        }
    }

    private List<String> getSuggestions(String text) {
        String query = text.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty() || query.contains("[") || query.contains(" ") || this.client.world == null) {
            return List.of();
        }
        boolean tag = query.startsWith("#");
        String search = tag ? query.substring(1) : query;
        Stream<Identifier> ids = switch (this.category) {
            case "items", "crafting", "components" -> getIds(Registries.ITEM, tag && !this.category.equals("components"));
            case "blocks", "mining" -> getIds(Registries.BLOCK, tag);
            case "entities" -> getIds(Registries.ENTITY_TYPE, tag);
            case "enchantments" -> getIds(this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT), tag);
            case "dimensions" -> tag || this.client.getNetworkHandler() == null ? Stream.empty() : this.client.getNetworkHandler().getWorldKeys().stream().map(RegistryKey::getValue);
            default -> Stream.empty();
        };
        return ids.map(Identifier::toString).filter(id -> id.contains(search))
                .sorted(Comparator.comparing((String id) -> !id.startsWith(search) && !id.contains(":" + search)).thenComparing(Comparator.naturalOrder()))
                .limit(MAX_SUGGESTIONS).map(id -> tag ? "#" + id : id).toList();
    }

    private static Stream<Identifier> getIds(Registry<?> registry, boolean tag) {
        if (tag) {
            return registry.streamTags().map(TagKey::id);
        }
        return registry.getIds().stream();
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
        if (this.client.player == null || mouseX < this.inventoryX || mouseY < this.inventoryY) {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int slot = this.getInventorySlot(mouseX, mouseY);
        if (slot >= 0) {
            ItemStack stack = this.client.player.getInventory().getStack(slot);
            if (!stack.isEmpty()) {
                this.addFromStack(stack);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.addField != null && this.addField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.addValue(this.addField.getText());
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB) {
                List<String> suggestions = this.getSuggestions(this.addText);
                if (!suggestions.isEmpty()) {
                    this.addField.setText(suggestions.getFirst());
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("editor.levelz.skills"), 10, 48, 0xFFFF55);

        if (this.inventoryRows > 0 && this.client.player != null) {
            context.drawTextWithShadow(this.textRenderer, Text.translatable("editor.levelz.inventory_hint"), this.inventoryX, this.inventoryY - 11, 0xFFFF55);
            ItemStack hoveredStack = ItemStack.EMPTY;
            for (int row = 0; row < this.inventoryRows; row++) {
                for (int column = 0; column < 9; column++) {
                    int x = this.inventoryX + column * SLOT_SIZE;
                    int y = this.inventoryY + row * SLOT_SIZE;
                    ItemStack stack = this.client.player.getInventory().getStack(row * 9 + column);
                    boolean hovered = mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
                    context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF373737);
                    context.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, hovered ? 0xFFC5C5C5 : 0xFF8B8B8B);
                    context.drawItem(stack, x + 1, y + 1);
                    context.drawItemInSlot(this.textRenderer, stack, x + 1, y + 1);
                    if (hovered) {
                        hoveredStack = stack;
                    }
                }
            }
            if (!hoveredStack.isEmpty()) {
                context.drawItemTooltip(this.textRenderer, hoveredStack, mouseX, mouseY);
            }
        }

        int statusWidth = this.columnX - 20;
        context.drawTextWithShadow(this.textRenderer, Language.getInstance().reorder(this.textRenderer.trimToWidth(this.status, statusWidth)), 10, this.height - 40, 0xFFFFFF);
        if (!this.problems.isEmpty() && mouseX >= 10 && mouseX < 10 + statusWidth && mouseY >= this.height - 42 && mouseY < this.height - 30) {
            List<OrderedText> lines = new ArrayList<>();
            for (String problem : this.problems) {
                lines.addAll(this.textRenderer.wrapLines(Text.literal(problem).formatted(Formatting.RED), 300));
            }
            context.drawOrderedTooltip(this.textRenderer, lines, mouseX, mouseY);
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

    private class ValueList extends AlwaysSelectedEntryListWidget<ValueRow> {

        public ValueList(MinecraftClient client, int width, int height, int y) {
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
        protected int getScrollbarX() {
            return this.getX() + this.width - 6;
        }
    }

    private class ValueRow extends AlwaysSelectedEntryListWidget.Entry<ValueRow> {

        private final String value;

        public ValueRow(String value) {
            this.value = value;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawTextWithShadow(RestrictionEditScreen.this.textRenderer, RestrictionEditScreen.this.textRenderer.trimToWidth(this.value, entryWidth - 14), x + 2, y + 1, 0xFFFFFF);
            boolean removeHovered = hovered && mouseX >= x + entryWidth - 10;
            context.drawTextWithShadow(RestrictionEditScreen.this.textRenderer, Text.translatable("editor.levelz.remove_short"), x + entryWidth - 8, y + 1, removeHovered ? 0xFF5555 : 0xAA0000);
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.value);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseX >= RestrictionEditScreen.this.valueList.getRowRight() - 10) {
                RestrictionEditScreen.this.client.send(() -> RestrictionEditScreen.this.removeValue(this.value));
                return true;
            }
            RestrictionEditScreen.this.valueList.setSelected(this);
            return true;
        }
    }

}
