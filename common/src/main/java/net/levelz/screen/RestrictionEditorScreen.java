package net.levelz.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.levelz.editor.EditorClientData;
import net.levelz.editor.RestrictionEntry;
import net.levelz.editor.RestrictionOverrides;
import net.levelz.network.packet.EditorExportPacket;
import net.levelz.network.packet.EditorRequestPacket;
import net.levelz.network.packet.EditorResultPacket;
import net.levelz.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestrictionEditorScreen extends Screen {

    public static final List<String> CATEGORIES = List.of("blocks", "crafting", "entities", "items", "mining", "enchantments", "components", "dimensions");
    private static final List<String> FLAGS = List.of("skills", "replace", "remove");

    private EditBox searchField;
    private EntryList entryList;
    private Button problemsButton;
    private Button editButton;
    private Component status = Component.empty();
    private String search = "";
    private boolean showProblems = false;
    @Nullable
    private RestrictionEntry selectedEntry = null;

    private final List<FormattedCharSequence> detailLines = new ArrayList<>();
    private double detailScroll = 0.0D;
    private int detailX;
    private int detailY;
    private int detailWidth;
    private int detailHeight;

    public RestrictionEditorScreen() {
        super(Component.translatable("screen.levelz.restriction_editor"));
    }

    @Override
    protected void init() {
        int listWidth = Math.min(220, (this.width - 30) / 2);
        this.detailX = listWidth + 20;
        this.detailY = 44;
        this.detailWidth = this.width - this.detailX - 10;
        this.detailHeight = this.height - 104;

        this.searchField = new EditBox(this.font, 10, 22, listWidth, 18, Component.translatable("editor.levelz.search"));
        this.searchField.setHint(Component.translatable("editor.levelz.search").withStyle(ChatFormatting.GRAY));
        this.searchField.setValue(this.search);
        this.searchField.setResponder(text -> {
            this.search = text;
            this.refresh();
        });
        this.addRenderableWidget(this.searchField);

        this.entryList = new EntryList(this.minecraft, listWidth, this.detailHeight, this.detailY, 12);
        this.entryList.setX(10);
        this.addRenderableWidget(this.entryList);

        int editButtonY = this.height - 52;
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.new"), button -> this.minecraft.gui.setScreen(new RestrictionEditScreen(this, null)))
                .bounds(this.width / 2 - 155, editButtonY, 100, 20).build());
        this.editButton = this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.edit"), button -> {
            if (this.selectedEntry != null) {
                this.minecraft.gui.setScreen(new RestrictionEditScreen(this, this.selectedEntry));
            }
        }).bounds(this.width / 2 - 50, editButtonY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.export"), button -> Services.network().sendToServer(new EditorExportPacket()))
                .bounds(this.width / 2 + 55, editButtonY, 100, 20).build());

        int buttonY = this.height - 28;
        this.addRenderableWidget(Button.builder(Component.translatable("editor.levelz.refresh"), button -> Services.network().sendToServer(new EditorRequestPacket()))
                .bounds(this.width / 2 - 155, buttonY, 100, 20).build());
        this.problemsButton = this.addRenderableWidget(Button.builder(Component.empty(), button -> {
            this.showProblems = !this.showProblems;
            this.updateDetail();
        }).bounds(this.width / 2 - 50, buttonY, 100, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 2 + 55, buttonY, 100, 20).build());

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
        this.problemsButton.setMessage(Component.translatable("editor.levelz.problems", EditorClientData.PROBLEMS.size()));
        this.updateDetail();
    }

    private void select(@Nullable RestrictionEntry entry) {
        this.selectedEntry = entry;
        this.showProblems = false;
        if (this.editButton != null) {
            this.editButton.active = entry != null;
            this.editButton.setMessage(Component.translatable(entry == null || RestrictionOverrides.SOURCE.equals(entry.file()) ? "editor.levelz.edit" : "editor.levelz.override"));
        }
        this.updateDetail();
    }

    public void onResult(EditorResultPacket result) {
        this.status = Component.translatable(result.message(), result.arguments().toArray()).withStyle(result.success() ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    private void updateDetail() {
        this.detailLines.clear();
        this.detailScroll = 0.0D;
        List<Component> lines = new ArrayList<>();
        if (this.showProblems) {
            lines.add(Component.translatable("editor.levelz.problems.title").withStyle(ChatFormatting.BOLD));
            if (EditorClientData.PROBLEMS.isEmpty()) {
                lines.add(Component.translatable("editor.levelz.no_problems").withStyle(ChatFormatting.GREEN));
            }
            for (String problem : EditorClientData.PROBLEMS) {
                lines.add(Component.literal(problem).withStyle(ChatFormatting.RED));
            }
        } else if (this.selectedEntry == null) {
            lines.add(Component.translatable(EditorClientData.ENTRIES.isEmpty() ? "editor.levelz.no_entries" : "editor.levelz.select_entry").withStyle(ChatFormatting.GRAY));
        } else {
            addEntryLines(this.selectedEntry, lines);
        }
        for (Component line : lines) {
            if (line.getString().isEmpty()) {
                this.detailLines.add(FormattedCharSequence.EMPTY);
            } else {
                this.detailLines.addAll(this.font.split(line, this.detailWidth - 8));
            }
        }
    }

    private static void addEntryLines(RestrictionEntry entry, List<Component> lines) {
        lines.add(Component.literal(entry.key()).withStyle(ChatFormatting.BOLD));
        lines.add(Component.translatable("editor.levelz.source", entry.file()).withStyle(ChatFormatting.GRAY));

        JsonElement element;
        try {
            element = JsonParser.parseString(entry.json());
        } catch (Exception e) {
            element = null;
        }
        if (element == null || !element.isJsonObject()) {
            lines.add(Component.translatable("editor.levelz.invalid_entry").withStyle(ChatFormatting.RED));
            lines.add(Component.literal(entry.json()));
        } else {
            JsonObject jsonObject = element.getAsJsonObject();
            if (isTrue(jsonObject, "remove")) {
                lines.add(Component.translatable("editor.levelz.remove").withStyle(ChatFormatting.GOLD));
            }
            if (isTrue(jsonObject, "replace")) {
                lines.add(Component.translatable("editor.levelz.replace").withStyle(ChatFormatting.GOLD));
            }
            if (jsonObject.has("skills") && jsonObject.get("skills").isJsonObject()) {
                lines.add(Component.empty());
                lines.add(Component.translatable("editor.levelz.skills").withStyle(ChatFormatting.YELLOW));
                for (Map.Entry<String, JsonElement> skill : jsonObject.getAsJsonObject("skills").entrySet()) {
                    ChatFormatting formatting = EditorClientData.SKILLS.contains(skill.getKey()) ? ChatFormatting.WHITE : ChatFormatting.RED;
                    lines.add(Component.literal("  " + skill.getKey() + " " + skill.getValue()).withStyle(formatting));
                }
            }
            for (String category : CATEGORIES) {
                if (jsonObject.has(category)) {
                    lines.add(Component.empty());
                    lines.add(Component.translatable("editor.levelz.category." + category).withStyle(ChatFormatting.YELLOW));
                    addValueLines(jsonObject.get(category), lines);
                }
            }
            List<String> otherKeys = jsonObject.keySet().stream().filter(key -> !CATEGORIES.contains(key) && !FLAGS.contains(key)).toList();
            if (!otherKeys.isEmpty()) {
                lines.add(Component.empty());
                lines.add(Component.translatable("editor.levelz.other").withStyle(ChatFormatting.YELLOW));
                for (String key : otherKeys) {
                    lines.add(Component.literal("  " + key + ": " + jsonObject.get(key)).withStyle(ChatFormatting.GRAY));
                }
            }
        }

        List<String> problems = getProblems(entry);
        if (!problems.isEmpty()) {
            lines.add(Component.empty());
            lines.add(Component.translatable("editor.levelz.entry_problems").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            for (String problem : problems) {
                lines.add(Component.literal(problem).withStyle(ChatFormatting.RED));
            }
        }
    }

    private static void addValueLines(JsonElement element, List<Component> lines) {
        if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                lines.add(Component.literal("  " + (value.isJsonPrimitive() ? value.getAsString() : value.toString())));
            }
        } else if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> value : element.getAsJsonObject().entrySet()) {
                lines.add(Component.literal("  " + value.getKey() + " " + value.getValue()));
            }
        } else {
            lines.add(Component.literal("  " + element).withStyle(ChatFormatting.RED));
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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);
        context.text(this.font, Language.getInstance().getVisualOrder(this.font.substrByWidth(this.status, this.detailWidth)), this.detailX, 28, 0xFFFFFFFF);

        context.fill(this.detailX, this.detailY, this.detailX + this.detailWidth, this.detailY + this.detailHeight, 0x80000000);
        context.enableScissor(this.detailX, this.detailY, this.detailX + this.detailWidth, this.detailY + this.detailHeight);
        int y = this.detailY + 4 - (int) this.detailScroll;
        for (FormattedCharSequence line : this.detailLines) {
            if (y > this.detailY - 10 && y < this.detailY + this.detailHeight) {
                context.text(this.font, line, this.detailX + 4, y, 0xFFFFFFFF);
            }
            y += 10;
        }
        context.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.detailX && mouseX < this.detailX + this.detailWidth && mouseY >= this.detailY && mouseY < this.detailY + this.detailHeight) {
            int maxScroll = Math.max(0, this.detailLines.size() * 10 + 8 - this.detailHeight);
            this.detailScroll = Mth.clamp(this.detailScroll - verticalAmount * 20.0D, 0.0D, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private class EntryList extends ObjectSelectionList<EntryRow> {

        public EntryList(Minecraft client, int width, int height, int y, int itemHeight) {
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
        protected int scrollBarX() {
            return this.getX() + this.width - 6;
        }
    }

    private class EntryRow extends ObjectSelectionList.Entry<EntryRow> {

        private final RestrictionEntry entry;
        private final boolean hasProblems;

        public EntryRow(RestrictionEntry entry) {
            this.entry = entry;
            this.hasProblems = !getProblems(entry).isEmpty();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.getContentX();
            int y = this.getContentY();
            int entryWidth = this.getContentWidth();
            int color = RestrictionOverrides.SOURCE.equals(this.entry.file()) ? 0xFFFFD37F : 0xFFFFFFFF;
            int textWidth = entryWidth - (this.hasProblems ? 12 : 4);
            context.text(RestrictionEditorScreen.this.font, RestrictionEditorScreen.this.font.plainSubstrByWidth(this.entry.key(), textWidth), x + 2, y + 1, color);
            if (this.hasProblems) {
                context.text(RestrictionEditorScreen.this.font, Component.literal("!").withStyle(ChatFormatting.RED), x + entryWidth - 8, y + 1, 0xFFFFFFFF);
            }
        }

        @Override
        public Component getNarration() {
            return Component.literal(this.entry.key());
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            RestrictionEditorScreen.this.entryList.setSelected(this);
            return true;
        }
    }

}
