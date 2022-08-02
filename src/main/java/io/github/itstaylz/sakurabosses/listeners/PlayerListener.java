package io.github.itstaylz.sakurabosses.listeners;

import io.github.itstaylz.hexlib.utils.ItemUtils;
import io.github.itstaylz.sakurabosses.bosses.BossManager;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlayerListener implements Listener {

    @EventHandler
    private void onSpawn(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        if (item != null && block != null && item.getType().name().contains("SPAWN_EGG") &&
                ItemUtils.hasPDCValue(item, BossManager.BOSS_SPAWN_EGG_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
            String bossId = ItemUtils.getPDCValue(item, BossManager.BOSS_SPAWN_EGG_KEY, PersistentDataType.STRING);
            Location spawnLocation = block.getLocation().add(event.getBlockFace().getDirection());
            BossManager.spawnBoss(bossId, spawnLocation);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            BossManager.updateTarget(player);
        }
    }

    @EventHandler
    private void onVanish(VanishStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        BossManager.updateTarget(player);
    }
}
