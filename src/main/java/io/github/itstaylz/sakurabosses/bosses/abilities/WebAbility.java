package io.github.itstaylz.sakurabosses.bosses.abilities;

import io.github.itstaylz.hexlib.storage.files.YamlFile;
import io.github.itstaylz.sakurabosses.SakuraBossesPlugin;
import io.github.itstaylz.sakurabosses.bosses.EntityBoss;
import io.github.itstaylz.sakurabosses.bosses.data.TargetType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WebAbility extends TargetAbility<WebAbility> {

    private final int x, y, z, duration;

    private final List<Block> tempBlocks = new ArrayList<>();

    WebAbility(TargetType targetType, int x, int y, int z, int duration) {
        super(targetType);
        this.x = x;
        this.y = y;
        this.z = z;
        this.duration = duration;
    }

    @Override
    public WebAbility create(YamlFile yaml, String path) {
        TargetType targetType = loadTargetType(yaml, path);
        int x = yaml.getConfig().getInt(path + ".x");
        int y = yaml.getConfig().getInt(path + ".y");
        int z = yaml.getConfig().getInt(path + ".z");
        int duration = yaml.getConfig().getInt(path + ".duration");
        return new WebAbility(targetType, x, y, z, duration);
    }

    @Override
    public void activate(EntityBoss entityBoss, LivingEntity target) {
        fillEmptyBlocks(target.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : tempBlocks) {
                    if (block.getType() == Material.COBWEB)
                        block.setType(Material.AIR);
                }
                tempBlocks.clear();
            }
        }.runTaskLater(JavaPlugin.getPlugin(SakuraBossesPlugin.class), this.duration);
    }

    private void fillEmptyBlocks(Location center) {
        if (center.getWorld() == null)
            return;
        Location top = center.clone().add(this.x, this.y, this.z);
        Location bot = center.clone().subtract(this.x, this.y, this.z);
        int xIncrement = top.getBlockX() < bot.getBlockX() ? 1 : -1;
        int yIncrement = top.getBlockY() < bot.getBlockY() ? 1 : -1;
        int zIncrement = top.getBlockZ() < bot.getBlockZ() ? 1 : -1;

        for (int x = top.getBlockX(); x != bot.getBlockX(); x += xIncrement) {
            for (int y = top.getBlockY(); y != bot.getBlockY(); y += yIncrement) {
                for (int z = top.getBlockZ(); z != bot.getBlockZ(); z += zIncrement) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.COBWEB);
                        tempBlocks.add(block);
                    }
                }
            }
        }
    }
}
