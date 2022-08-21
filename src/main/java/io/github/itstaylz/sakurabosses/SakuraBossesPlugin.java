package io.github.itstaylz.sakurabosses;

import com.earth2me.essentials.Essentials;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.BossTargeting;
import io.github.itstaylz.sakurabosses.commands.*;
import io.github.itstaylz.sakurabosses.listeners.AbilityListener;
import io.github.itstaylz.sakurabosses.listeners.BossListener;
import io.github.itstaylz.sakurabosses.listeners.EffectListener;
import io.github.itstaylz.sakurabosses.listeners.PlayerListener;
import io.github.itstaylz.sakurabosses.spawning.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public final class SakuraBossesPlugin extends JavaPlugin implements Listener {

    private static Essentials essentials;
    private static Logger logger;

    @Override
    public void onEnable() {
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        logger = getLogger();
        BossManager.loadBosses();
        new BukkitRunnable() {

            @Override
            public void run() {
                WarpManager.loadSpawns();
            }
        }.runTaskLater(this, 1L);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BossListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new EffectListener(), this);
        getCommand("bossmenu").setExecutor(new BossMenuCommand());
        getCommand("bossesreload").setExecutor(new BossReloadCommand());
        getCommand("bosswarps").setExecutor(new WarpMenuCommand());
        getCommand("setbosswarp").setExecutor(new SetWarpCommand());
        getCommand("deletebosswarp").setExecutor(new DeleteWarpCommand());
        SpawnBossCommand spawnBossCommand = new SpawnBossCommand();
        PluginCommand spawnBoss = getCommand("spawnboss");
        spawnBoss.setExecutor(spawnBossCommand);
        spawnBoss.setTabCompleter(spawnBossCommand);
        new BossTargeting(this).startTask();
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
