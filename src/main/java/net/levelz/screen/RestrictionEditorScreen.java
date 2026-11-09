package net.levelz.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.editor.EditorClientData;
import net.levelz.editor.RestrictionEntry;
import net.levelz.editor.RestrictionOverrides;
import net.levelz.network.packet.EditorExportPacket;
import net.levelz.network.packet.EditorRequestPacket;
import net.levelz.network.packet.EditorResultPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RestrictionEditorScreen extends Screen {

    public static final List<String> CATEGORIES = List.of("blocks", "crafting", "entities", "items", "mining", "enchantments", "components", "dimensions");
    private static final List<String> FLAGS = List.of("skills", "replace", "remove");

    private TextFieldWidget searchField;
    private EntryList entryList;
    private ButtonWidget problemsButton;
    private ButtonWidget editButton;
    private Text status = Text.empty();
    private String search = "";
    private boolean showProblems = false;
    @Nullable
    private RestrictionEntry selectedEntry = null;

    private final List<OrderedText> detailLines = new ArrayList<>();
    private double detailScroll = 0.0D;
    private int detailX;
    private int detailY;
    private int detailWidth;
    private int detailHeight;

    public RestrictionEditorScreen() {
        super(Text.translatable("screen.levelz.restriction_editor"));
    }

    @Override
    protected void init() {
        int listWidth = Math.min(220, (this.width - 30) / 2);
        this.detailX = listWidth + 20;
        this.detailY = 44;
        this.detailWidth = this.width - this.detailX - 10;
        this.detailHeight = this.height - 104;

        this.searchField = new TextFieldWidget(this.textRenderer, 10, 22, listWidth, 18, Text.translatable("editor.levelz.search"));
        this.searchField.setPlaceholder(Text.translatable("editor.levelz.search").formatted(Formatting.GRAY));
        this.searchField.setText(this.search);
        this.searchField.setChangedListener(text -> {
            this.search = text;
            this.refresh();
        });
        this.addDrawableChild(this.searchField);

        this.entryList = new EntryList(this.client, listWidth, this.detailHeight, this.detailY, 12);
        this.entryList.setX(10);
        this.addDrawableChild(this.entryList);

        int editButtonY = this.height - 52;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.new"), button -> this.client.setScreen(new RestrictionEditScreen(this, null)))
                .dimensions(this.width / 2 - 155, editButtonY, 100, 20).build());
        this.editButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.edit"), button -> {
            if (this.selectedEntry != null) {
                this.client.setScreen(new RestrictionEditScreen(this, this.selectedEntry));
            }
        }).dimensions(this.width / 2 - 50, editButtonY, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.export"), button -> ClientPlayNetworking.send(new EditorExportPacket()))
                .dimensions(this.width / 2 + 55, editButtonY, 100, 20).build());

        int buttonY = this.height - 28;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("editor.levelz.refresh"), button -> ClientPlayNetworking.send(new EditorRequestPacket()))
                .dimensions(this.width / 2 - 155, buttonY, 100, 20).build());
        this.problemsButton = this.addDrawableChild(ButtonWidget.builder(Text.empty(), button -> {
            this.showProblems = !this.showProblems;
            this.updateDetail();
        }).dimensions(this.width / 2 - 50, buttonY, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 + 55, buttonY, 100, 20).build());

        this.refresh();
    }

    public void refresh() {
        String query = this.search.toLowerCase(Locale.ROOT);
        List<RestrictionEntry> entries = new ArrayList<>();
        for (RestrictionEntry entry : EditorClientData.ENTRIES) {
            if (query.isEmpty() || entry.key().toLowerCase(Locale.ROOT).contains(query) || entry.file().toLowerCase(Locale.ROOT).contains(query)
                    || entry.json().toLowerCase(Locale.ROOT).contains(query)) {
                entries.add(entry);
            }
        }
        this.entryList.setEntries(entries, this.selectedEntry);
        this.problemsButton.setMessage(Text.translatable("editor.levelz.problems", EditorClientData.PROBLEMS.size()));
        this.updateDetail();
    }

    private void select(@Nullable RestrictionEntry entry) {
        this.selectedEntry = entry;
        this.showProblems = false;
        if (this.editButton != null) {
            this.editButton.active = entry != null;
            this.editButton.setMessage(Text.translatable(entry == null || RestrictionOverrides.SOURCE.equals(entry.file()) ? "editor.levelz.edit" : "editor.levelz.override"));
        }
        this.updateDetail();
    }

    public void onResult(EditorResultPacket result) {
        this.status = Text.translatable(result.message(), result.arguments().toArray()).formatted(result.success() ? Formatting.GREEN : Formatting.RED);
    }

    private void updateDetail() {
        this.detailLines.clear();
        this.detailScroll = 0.0D;
        List<Text> lines = new ArrayList<>();
        if (this.showProblems) {
            lines.add(Text.translatable("editor.levelz.problems.title").formatted(Formatting.BOLD));
            if (EditorClientData.PROBLEMS.isEmpty()) {
                lines.add(Text.translatable("editor.levelz.no_problems").formatted(Formatting.GREEN));
            }
            for (String problem : EditorClientData.PROBLEMS) {
                lines.add(Text.literal(problem).formatted(Formatting.RED));
            }
        } else if (this.selectedEntry == null) {
            lines.add(Text.translatable(EditorClientData.ENTRIES.isEmpty() ? "editor.levelz.no_entries" : "editor.levelz.select_entry").formatted(Formatting.GRAY));
        } else {
            addEntryLines(this.selectedEntry, lines);
        }
        for (Text line : lines) {
            if (line.getString().isEmpty()) {
                this.detailLines.add(OrderedText.EMPTY);
            } else {
                this.detailLines.addAll(this.textRenderer.wrapLines(line, this.detailWidth - 8));
            }
        }
    }

    private static void addEntryLines(RestrictionEntry entry, List<Text> lines) {
        lines.add(Text.literal(entry.key()).formatted(Formatting.BOLD));
        lines.add(Text.translatable("editor.levelz.source", entry.file()).formatted(Formatting.GRAY));

        JsonElement element;
        try {
            element = JsonParser.parseString(entry.json());
        } catch (Exception e) {
            element = null;
        }
        if (element == null || !element.isJsonObject()) {
            lines.add(Text.translatable("editor.levelz.invalid_entry").formatted(Formatting.RED));
            lines.add(Text.literal(entry.json()));
        } else {
            JsonObject jsonObject = element.getAsJsonObject();
            if (isTrue(jsonObject, "remove")) {
                lines.add(Text.translatable("editor.levelz.remove").formatted(Formatting.GOLD));
            }
            if (isTrue(jsonObject, "replace")) {
                lines.add(Text.translatable("editor.levelz.replace").formatted(Formatting.GOLD));
            }
            if (jsonObject.has("skills") && jsonObject.get("skills").isJsonObject()) {
                lines.add(Text.empty());
                lines.add(Text.translatable("editor.levelz.skills").formatted(Formatting.YELLOW));
                for (Map.Entry<String, JsonElement> skill : jsonObject.getAsJsonObject("skills").entrySet()) {
                    Formatting formatting = EditorClientData.SKILLS.contains(skill.getKey()) ? Formatting.WHITE : Formatting.RED;
                    lines.add(Text.literal("  " + skill.getKey() + " " + skill.getValue()).formatted(formatting));
                }
            }
            for (String category : CATEGORIES) {
                if (jsonObject.has(category)) {
                    lines.add(Text.empty());
                    lines.add(Text.translatable("editor.levelz.category." + category).formatted(Formatting.YELLOW));
                    addValueLines(jsonObject.get(category), lines);
                }
            }
            List<String> otherKeys = jsonObject.keySet().stream().filter(key -> !CATEGORIES.contains(key) && !FLAGS.contains(key)).toList();
            if (!otherKeys.isEmpty()) {
                lines.add(Text.empty());
                lines.add(Text.translatable("editor.levelz.other").formatted(Formatting.YELLOW));
                for (String key : otherKeys) {
                    lines.add(Text.literal("  " + key + ": " + jsonObject.get(key)).formatted(Formatting.GRAY));
                }
            }
        }

        List<String> problems = getProblems(entry);
        if (!problems.isEmpty()) {
            lines.add(Text.empty());
            lines.add(Text.translatable("editor.levelz.entry_problems").formatted(Formatting.RED, Formatting.BOLD));
            for (String problem : problems) {
                lines.add(Text.literal(problem).formatted(Formatting.RED));
            }
        }
    }

    private static void addValueLines(JsonElement element, List<Text> lines) {
        if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                lines.add(Text.literal("  " + (value.isJsonPrimitive() ? value.getAsString() : value.toString())));
            }
        } else if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> value : element.getAsJsonObject().entrySet()) {
                lines.add(Text.literal("  " + value.getKey() + " " + value.getValue()));
            }
        } else {
            lines.add(Text.literal("  " + element).formatted(Formatting.RED));
        }
    }

    private static boolean isTrue(JsonObject jsonObject, String key) {
        return jsonObject.has(key) && jsonObject.get(key).isJsonPrimitive() && jsonObject.get(key).getAsJsonPrimitive().isBoolean() && jsonObject.get(key).getAsBoolean();
    }

    private static List<String> getProblems(RestrictionEntry entry) {
        String source = " " + entry.key() + " in " + entry.file();
        return EditorClientData.PROBLEMS.stream().filter(problem -> problem.contains(source)).toList();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Language.getInstance().reorder(this.textRenderer.trimToWidth(this.status, this.detailWidth)), this.detailX, 28, 0xFFFFFF);

        context.fill(this.detailX, this.detailY, this.detailX + this.detailWidth, this.detailY + this.detailHeight, 0x80000000);
        context.enableScissor(this.detailX, this.detailY, this.detailX + this.detailWidth, this.detailY + this.detailHeight);
        int y = this.detailY + 4 - (int) this.detailScroll;
        for (OrderedText line : this.detailLines) {
            if (y > this.detailY - 10 && y < this.detailY + this.detailHeight) {
                context.drawTextWithShadow(this.textRenderer, line, this.detailX + 4, y, 0xFFFFFF);
            }
            y += 10;
        }
        context.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.detailX && mouseX < this.detailX + this.detailWidth && mouseY >= this.detailY && mouseY < this.detailY + this.detailHeight) {
            int maxScroll = Math.max(0, this.detailLines.size() * 10 + 8 - this.detailHeight);
            this.detailScroll = MathHelper.clamp(this.detailScroll - verticalAmount * 20.0D, 0.0D, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private class EntryList extends AlwaysSelectedEntryListWidget<EntryRow> {

        public EntryList(MinecraftClient client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
        }

        public void setEntries(List<RestrictionEntry> entries, @Nullable RestrictionEntry selected) {
            this.clearEntries();
            EntryRow selectedRow = null;
            for (RestrictionEntry entry : entries) {
                EntryRow row = new EntryRow(entry);
                this.addEntry(row);
                if (selected != null && selected.file().equals(entry.file()) && selected.key().equals(entry.key())) {
                    selectedRow = row;
                }
            }
            this.setSelected(selectedRow);
        }

        @Override
        public void setSelected(@Nullable EntryRow entry) {
            super.setSelected(entry);
            RestrictionEditorScreen.this.select(entry != null ? entry.entry : null);
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

    private class EntryRow extends AlwaysSelectedEntryListWidget.Entry<EntryRow> {

        private final RestrictionEntry entry;
        private final boolean hasProblems;

        public EntryRow(RestrictionEntry entry) {
            this.entry = entry;
            this.hasProblems = !getProblems(entry).isEmpty();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int color = RestrictionOverrides.SOURCE.equals(this.entry.file()) ? 0xFFD37F : 0xFFFFFF;
            int textWidth = entryWidth - (this.hasProblems ? 12 : 4);
            context.drawTextWithShadow(RestrictionEditorScreen.this.textRenderer, RestrictionEditorScreen.this.textRenderer.trimToWidth(this.entry.key(), textWidth), x + 2, y + 1, color);
            if (this.hasProblems) {
                context.drawTextWithShadow(RestrictionEditorScreen.this.textRenderer, Text.literal("!").formatted(Formatting.RED), x + entryWidth - 8, y + 1, 0xFFFFFF);
            }
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.entry.key());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RestrictionEditorScreen.this.entryList.setSelected(this);
            return true;
        }
    }

}
