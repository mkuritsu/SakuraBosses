package io.github.itstaylz.sakurabosses.spawning;

import io.github.itstaylz.hexlib.items.ItemBuilder;
import io.github.itstaylz.hexlib.storage.file.YamlFile;
import io.github.itstaylz.hexlib.utils.StringUtils;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.data.BossData;
import io.github.itstaylz.sakurabosses.utils.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;

public class BossWarp {

    private final String id, displayName;
    private Location location;
    private final ItemStack displayItem;
    private final List<BossSpawnData> spawnDataList;

    private final HashMap<String, BukkitTask> tasks = new HashMap<>();

    private final HashMap<String, Date> endTime = new HashMap<>();

    private BossWarp(String id, String displayName, Location location, ItemStack displayItem, List<BossSpawnData> spawnDataList) {
        this.id = id;
        this.displayName = displayName;
        this.location = location;
        this.displayItem = displayItem;
        this.spawnDataList = spawnDataList;
    }

    public static BossWarp createFromCommand(String id, Location location) {
        return new BossWarp(id, id, location, new ItemStack(Material.SKELETON_SKULL), new ArrayList<>());
    }

    public static BossWarp loadFromFile(File file) {
        YamlFile yaml = new YamlFile(file);
        try {
            String id = file.getName().replace(".yml", "");
            String displayName = yaml.contains("display_name") ? StringUtils.colorize(yaml.getConfig().getString("display_name")) : id;
            String worldName = yaml.getConfig().getString("location.world");
            World world = Bukkit.getWorld(worldName);
            if (world == null)
                throw new Exception("INVALID WORLD: " + worldName);
            double x = yaml.getConfig().getDouble("location.x");
            double y = yaml.getConfig().getDouble("location.y");
            double z = yaml.getConfig().getDouble("location.z");
            Location location = new Location(world, x, y, z);
            ItemStack displayItem = YamlUtils.loadItemStack(yaml, "display_item");
            if (displayItem == null)
                displayItem = new ItemStack(Material.SKELETON_SKULL);
            List<BossSpawnData> spawnDataList = new ArrayList<>();
            ConfigurationSection section = yaml.getConfig().getConfigurationSection("spawns");
            if (section != null) {
                Set<String> keys = section.getKeys(false);
                for (String key : keys) {
                    String bossId = yaml.getConfig().getString("spawns." + key + ".boss_id");
                    if (BossManager.getBossData(bossId) == null)
                        throw new Exception("INVALID BOSS ID: " + bossId);
                    int spawnTimer = yaml.getConfig().getInt("spawns." + key + ".spawn_timer");
                    BossSpawnData spawnData = new BossSpawnData(bossId, spawnTimer);
                    spawnDataList.add(spawnData);
                }
            }
            return new BossWarp(id, displayName, location, displayItem, spawnDataList);
        } catch (Exception e) {
            SakuraBossesPlugin.getPluginLogger().severe("Failed to load warp: " + file.getName());
            e.printStackTrace();
        }
        return null;
    }

    public void startTimer() {
        for (BossSpawnData spawnData : this.spawnDataList) {
            startTask(spawnData.bossId(), spawnData.spawnTimer());
        }
    }

    private void startTask(String bossId, int timer) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                spawnBoss(bossId);
                tasks.remove(bossId);
                endTime.remove(bossId);
                startTask(bossId, timer);
            }
        }.runTaskLater(JavaPlugin.getPlugin(SakuraBossesPlugin.class), timer);
        tasks.put(bossId, task);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, timer / 20);
        endTime.put(bossId, calendar.getTime());
    }

    public void spawnBoss(String bossId) {
        BossData bossData = BossManager.getBossData(bossId);
        BossManager.spawnBoss(bossId, this.location);
        String spawnMessage = bossData.settings().spawnMessage();
        if (spawnMessage != null) {
            spawnMessage = spawnMessage.replace("{BOSS}", bossData.settings().displayName())
                    .replace("{WARP}", this.displayName);
            Bukkit.broadcastMessage(spawnMessage);
        }
    }


    public ItemStack getMenuItem() {
        ItemBuilder builder = new ItemBuilder(displayItem.clone());
        builder.setDisplayName(this.displayName);
        builder.addLore(" ");
        for (BossSpawnData data : this.spawnDataList) {
            BossData bossData = BossManager.getBossData(data.bossId());
            String remainingTime = calculateRemainingTime(bossData.id());
            builder.addLore(StringUtils.colorize(bossData.settings().displayName() + "&f > " + remainingTime)).addLore(" ");
        }
        return builder.build();
    }

    private String calculateRemainingTime(String bossId) {
        Date now = new Date();
        Date spawnDate = endTime.getOrDefault(bossId, now);
        long diff = spawnDate.getTime() - now.getTime();
        long secondsDiff = diff / 1000;
        long minutesDiff = diff / (60 * 1000);
        long hoursDiff = diff / (60 * 60 * 1000);
        return hoursDiff + "h " + minutesDiff + "m " + secondsDiff + "s";
    }

    public void dispose() {
        for (BukkitTask task : this.tasks.values()) {
            task.cancel();
        }
    }

    public String getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
