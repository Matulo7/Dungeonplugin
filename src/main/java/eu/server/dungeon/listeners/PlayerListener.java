package eu.server.dungeon.listeners;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.models.DungeonInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final DungeonPlugin plugin;

    public PlayerListener(DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DungeonInstance inst = plugin.getInstanceManager().getInstanciaHraca(player.getUniqueId());
        if (inst == null) return;

        // Respawnuj hraca a teleportuj spat na spawn dungeonu
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
                if (inst.getDungeon().getSpawnLokacia() != null) {
                    player.teleport(inst.getDungeon().getSpawnLokacia());
                } else {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                player.sendMessage(plugin.msg("hrac-zomrel"));
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonInstance inst = plugin.getInstanceManager().getInstanciaHraca(player.getUniqueId());
        if (inst != null) {
            inst.getHraci().remove(player.getUniqueId());
        }

        // Vycisti party
        if (plugin.getPartyManager().jeVParty(player.getUniqueId())) {
            plugin.getPartyManager().odisielZParty(player);
        }
    }
}
