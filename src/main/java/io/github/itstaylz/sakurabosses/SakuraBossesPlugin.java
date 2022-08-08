package io.github.itstaylz.sakurabosses;

import com.earth2me.essentials.Essentials;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.bosses.BossTargeting;
import io.github.itstaylz.sakurabosses.commands.BossMenuCommand;
import io.github.itstaylz.sakurabosses.commands.BossReloadCommand;
import io.github.itstaylz.sakurabosses.commands.SpawnBossCommand;
import io.github.itstaylz.sakurabosses.listeners.AbilityListener;
import io.github.itstaylz.sakurabosses.listeners.BossListener;
import io.github.itstaylz.sakurabosses.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SakuraBossesPlugin extends JavaPlugin {

    private static Essentials essentials;
    private static Logger logger;

    @Override
    public void onEnable() {
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        logger = getLogger();
        BossManager.loadBosses();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BossListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(), this);
        getCommand("bossmenu").setExecutor(new BossMenuCommand());
        getCommand("bossesreload").setExecutor(new BossReloadCommand());
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
