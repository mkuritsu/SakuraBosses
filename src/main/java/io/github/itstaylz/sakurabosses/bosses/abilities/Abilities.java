package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Abilities {

    public static final IBossAbility<ArrowRainAbility> ARROW_RAIN = new ArrowRainAbility(TargetType.CLOSEST, 0, 0);

    public static final IBossAbility<ThrowAbility> THROW_ABILITY = new ThrowAbility(TargetType.CLOSEST, new Vector(0, 0, 0));

    public static final IBossAbility<MessageAbility> MESSAGE_ABILITY = new MessageAbility(TargetType.CLOSEST, null);

    public static final IBossAbility<FireballAbility> FIREBALL_ABILITY = new FireballAbility(TargetType.CLOSEST, 0, 0);

    private static final HashMap<String, IBossAbility<?>> ABILITY_REGISTRY = new HashMap<>();

    static {
        ABILITY_REGISTRY.put("arrow_rain", ARROW_RAIN);
        ABILITY_REGISTRY.put("throw", THROW_ABILITY);
        ABILITY_REGISTRY.put("message", MESSAGE_ABILITY);
        ABILITY_REGISTRY.put("fireball", FIREBALL_ABILITY);
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
