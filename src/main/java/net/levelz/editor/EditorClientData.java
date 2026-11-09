package net.levelz.editor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.network.packet.EditorResultPacket;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EditorClientData {

    public static final List<RestrictionEntry> ENTRIES = new ArrayList<>();
    public static final List<String> SKILLS = new ArrayList<>();
    public static final List<String> PROBLEMS = new ArrayList<>();
    @Nullable
    public static EditorResultPacket lastResult = null;

    public static void update(List<RestrictionEntry> entries, List<String> skills, List<String> problems) {
        ENTRIES.clear();
        ENTRIES.addAll(entries);
        SKILLS.clear();
        SKILLS.addAll(skills);
        PROBLEMS.clear();
        PROBLEMS.addAll(problems);
    }

}
