package net.levelz.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.levelz.stats.Skill;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkillArgumentType implements ArgumentType<String> {

    public static SkillArgumentType skill() {
        return new SkillArgumentType();
    }

    public static List<String> skillStrings() {
        List<String> skillStrings = new ArrayList<>();
        for(var skill: Skill.values()){
            skillStrings.add(skill.getName().toLowerCase());
        }
        skillStrings.add("level");
        skillStrings.add("points");
        skillStrings.add("experience");
        return skillStrings;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor(); // The starting position of the cursor is at the beginning of the argument.
        if (!reader.canRead()) {
            reader.skip();
        }

        // Now we check the contents of the argument till either we hit the end of the
        // command line (when ''canRead'' becomes false)
        // Otherwise we go till reach reach a space, which signifies the next argument
        while (reader.canRead() && reader.peek() != ' ') { // peek provides the character at the current cursor position.
            reader.skip(); // Tells the StringReader to move it's cursor to the next position.
        }

        // Now we substring the specific part we want to see using the starting cursor
        // position and the ends where the next argument starts.
        String skillRawString = reader.getString().substring(argBeginning, reader.getCursor());
        try {
            if(skillStrings().contains(skillRawString.toLowerCase())){
                return skillRawString.toLowerCase();
            }
            // And we return our type, in this case the parser will consider this
            // argument to have parsed properly and then move on.
        } catch (Exception ex) {
            // UUIDs can throw an exception when made by a string, so we catch the exception
            // and repackage it into a CommandSyntaxException type.
            // Create with context tells Brigadier to supply some context to tell the user
            // where the command failed at.
            // Though normal create method could be used.
            throw new SimpleCommandExceptionType(Text.literal(ex.getMessage())).createWithContext(reader);
        }
        throw new SimpleCommandExceptionType(Text.literal("Invalid skill")).createWithContext(reader);
    }

    @Override
    public Collection<String> getExamples() {
        return skillStrings();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        for(var skillString: skillStrings()){
            if(skillString.startsWith(builder.getRemaining().toLowerCase())){
                builder.suggest(skillString);
            }
        }
        return builder.buildFuture();
    }

}
