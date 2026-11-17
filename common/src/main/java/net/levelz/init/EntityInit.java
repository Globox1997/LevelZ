package net.levelz.init;

import net.levelz.LevelzMain;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;

public class EntityInit {

    public static final boolean isRedstoneBitsLoaded = Services.PLATFORM.isModLoaded("redstonebits");

    public static EntityType<LevelExperienceOrbEntity> LEVEL_EXPERIENCE_ORB;

    public static void register(BiConsumer<Identifier, EntityType<?>> registrar) {
        Identifier identifier = LevelzMain.identifierOf("level_experience_orb");
        LEVEL_EXPERIENCE_ORB = EntityType.Builder.<LevelExperienceOrbEntity>of(LevelExperienceOrbEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F).build(ResourceKey.create(Registries.ENTITY_TYPE, identifier));
        registrar.accept(identifier, LEVEL_EXPERIENCE_ORB);
    }

}
