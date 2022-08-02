package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Abilities {

    public static final IBossAbility<ArrowRainAbility> ARROW_RAIN = new ArrowRainAbility();

    private static final HashMap<String, IBossAbility<?>> ABILITY_REGISTRY = new HashMap<>();

    static {
        ABILITY_REGISTRY.put("ARROW_RAIN", ARROW_RAIN);
    }

    public static List<IBossAbility<?>> loadBossAbilities(YamlFile yaml, String path) {
        List<IBossAbility<?>> abilities = new ArrayList<>();
        ConfigurationSection section = yaml.getSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                IBossAbility<?> ability = ABILITY_REGISTRY.get(key);
                if (ability != null) {
                    abilities.add(ability.create(yaml, path + "." + key));
                }
            }
        }
        return abilities;
    }
}
