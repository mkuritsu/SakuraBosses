package io.github.itstaylz.sakurabosses.bosses;

import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class BossDataKeys {

    public static final NamespacedKey ENTITY_BOSS_KEY;

    public static final NamespacedKey BOSS_SPAWN_EGG_KEY;

    public static final NamespacedKey BOSS_MINION_KEY;

    public static final NamespacedKey MINION_DAMAGE_KEY;

    static {
        JavaPlugin plugin = JavaPlugin.getPlugin(SakuraBossesPlugin.class);
        ENTITY_BOSS_KEY = new NamespacedKey(plugin, "entity_boss");
        BOSS_SPAWN_EGG_KEY = new NamespacedKey(plugin, "boss_spawn_egg");
        BOSS_MINION_KEY = new NamespacedKey(plugin, "boss_minion");
        MINION_DAMAGE_KEY = new NamespacedKey(plugin, "boss_minion_damage");
    }
}
