package io.github.itstaylz.sakurabosses.spawning;

import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WarpManager {

    public static final File WARPS_FOLDER;

    private static final HashMap<String, BossWarp> WARP_REGISTRY = new HashMap<>();

    static {
        JavaPlugin plugin = JavaPlugin.getPlugin(SakuraBossesPlugin.class);
        WARPS_FOLDER = new File(plugin.getDataFolder(), "spawns");
        if (!WARPS_FOLDER.exists())
            WARPS_FOLDER.mkdirs();
    }

    public static Set<String> getAllWarpIds() {
        return WARP_REGISTRY.keySet();
    }

    public static List<BossWarp> getAllWarps() {
        return List.copyOf(WARP_REGISTRY.values());
    }

    public static boolean warpExists(String id) {
        return WARP_REGISTRY.containsKey(id);
    }

    public static void removeWarp(String id) {
        BossWarp warp = WARP_REGISTRY.get(id);
        WARP_REGISTRY.remove(id);
        if (warp != null) {
            warp.dispose();
        }
    }

    public static void addWarp(String id, BossWarp warp) {
        removeWarp(id);
        WARP_REGISTRY.put(id, warp);
        warp.startTimer();
    }

    public static BossWarp getWarp(String id) {
        return WARP_REGISTRY.get(id);
    }

    public static void loadSpawns() {
        Set<String> ids = new HashSet<>(getAllWarpIds());
        for (String id : ids) {
            removeWarp(id);
        }
        File[] files = WARPS_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                BossWarp data = BossWarp.loadFromFile(file);
                if (data != null) {
                    addWarp(data.getId(), data);
                }
            }
        }
    }


}
