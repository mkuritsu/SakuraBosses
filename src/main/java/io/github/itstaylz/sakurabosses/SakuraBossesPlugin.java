package io.github.itstaylz.sakurabosses;

import io.github.itstaylz.sakurabosses.bosses.BossManager;
import io.github.itstaylz.sakurabosses.commands.BossMenuCommand;
import io.github.itstaylz.sakurabosses.commands.BossReloadCommand;
import io.github.itstaylz.sakurabosses.listeners.BossListener;
import io.github.itstaylz.sakurabosses.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SakuraBossesPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        BossManager.loadBosses();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BossListener(this), this);
        getCommand("bossmenu").setExecutor(new BossMenuCommand());
        getCommand("bossesreload").setExecutor(new BossReloadCommand());
    }
}
