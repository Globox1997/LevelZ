package net.levelz.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.JsonHelper;

public class SkillPredicate {
    private final String jobName;

    public SkillPredicate(String jobName) {
        this.jobName = jobName;
    }

    public boolean test(String jobName) {
        if (this.jobName.equals(jobName)) {
            return true;
        } else {
            return false;
        }
    }

    public static SkillPredicate fromJson(JsonElement json) {
        String jobName = JsonHelper.asString(json, "skill_name");
        return new SkillPredicate(jobName);
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("skill_name", this.jobName);
        return jsonObject;
    }

}
