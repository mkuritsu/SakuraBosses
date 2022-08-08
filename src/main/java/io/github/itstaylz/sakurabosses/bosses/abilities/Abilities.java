package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Abilities {

    public static final IBossAbility<ArrowRainAbility> ARROW_RAIN = new ArrowRainAbility();

    public static final IBossAbility<ThrowAbility> THROW_ABILITY = new ThrowAbility();

    public static final IBossAbility<MessageAbility> MESSAGE_ABILITY = new MessageAbility();

    public static final IBossAbility<FireballAbility> FIREBALL_ABILITY = new FireballAbility();

    public static final IBossAbility<ExplosionAbility> EXPLOSION_ABILITY = new ExplosionAbility();

    public static final IBossAbility<CommandAbility> COMMAND_ABILITY = new CommandAbility();

    public static final IBossAbility<PotionAbility> POTION_ABILITY = new PotionAbility();

    public static final IBossAbility<PursuingArrowAbility> PURSUING_ARROW_ABILITY = new PursuingArrowAbility();

    public static final IBossAbility<WebAbility> WEB_ABILITY = new WebAbility();

    public static final IBossAbility<LifeStealAbility> LIFESTEAL = new LifeStealAbility();

    public static final IBossAbility<SmiteAbility> SMITE = new SmiteAbility();

    public static final IBossAbility<ImmunityAbility> IMMUNITY = new ImmunityAbility();

    public static final IBossAbility<TeleportAbility> TELEPORT = new TeleportAbility();

    public static final IBossAbility<DurabilityAbility> DURABILITY = new DurabilityAbility();

    public static final IBossAbility<BombAbility> BOMB = new BombAbility();

    public static final IBossAbility<MirrorAbility> MIRROR = new MirrorAbility();

    public static final IBossAbility<ParticleAbility> PARTICLE = new ParticleAbility();

    public static final IBossAbility<TreasureAbility> TREASURE = new TreasureAbility();

    public static final IBossAbility<MinionsAbility> MINIONS = new MinionsAbility();

    private static final HashMap<String, IBossAbility<?>> ABILITY_REGISTRY = new HashMap<>();

    static {
        ABILITY_REGISTRY.put("arrow_rain", ARROW_RAIN);
        ABILITY_REGISTRY.put("throw", THROW_ABILITY);
        ABILITY_REGISTRY.put("message", MESSAGE_ABILITY);
        ABILITY_REGISTRY.put("fireball", FIREBALL_ABILITY);
        ABILITY_REGISTRY.put("explosion", EXPLOSION_ABILITY);
        ABILITY_REGISTRY.put("command", COMMAND_ABILITY);
        ABILITY_REGISTRY.put("potion", POTION_ABILITY);
        ABILITY_REGISTRY.put("pursuing_arrow", PURSUING_ARROW_ABILITY);
        ABILITY_REGISTRY.put("web", WEB_ABILITY);
        ABILITY_REGISTRY.put("lifesteal", LIFESTEAL);
        ABILITY_REGISTRY.put("smite", SMITE);
        ABILITY_REGISTRY.put("immunity", IMMUNITY);
        ABILITY_REGISTRY.put("teleport", TELEPORT);
        ABILITY_REGISTRY.put("durability", DURABILITY);
        ABILITY_REGISTRY.put("bomb", BOMB);
        ABILITY_REGISTRY.put("mirror", MIRROR);
        ABILITY_REGISTRY.put("particle", PARTICLE);
        ABILITY_REGISTRY.put("treasure", TREASURE);
        ABILITY_REGISTRY.put("minions", MINIONS);
    }

    public static List<IBossAbility<?>> loadBossAbilities(YamlFile yaml, String path) {
        List<IBossAbility<?>> abilities = new ArrayList<>();
        ConfigurationSection section = yaml.getSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String keyPath = path + "." + key;
                String type = yaml.getConfig().getString(keyPath + ".type");
                if (type != null && ABILITY_REGISTRY.containsKey(type)) {
                    IBossAbility<?> ability = ABILITY_REGISTRY.get(type);
                    abilities.add(ability.create(yaml, keyPath));
                }
            }
        }
        return abilities;
    }
}
