package eu.server.dungeon.managers;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.models.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class InstanceManager {

    private final DungeonPlugin plugin;
    private final Map<String, DungeonInstance> instancie; // instanceId -> instance
    private final Map<UUID, String> hracVInstancii; // hrac UUID -> instanceId

    public InstanceManager(DungeonPlugin plugin) {
        this.plugin = plugin;
        this.instancie = new HashMap<>();
        this.hracVInstancii = new HashMap<>();
    }

    public DungeonInstance vytvorInstanciu(Dungeon dungeon, List<UUID> hraci, UUID leader) {
        String instanceId = dungeon.getId() + "_" + System.currentTimeMillis();
        DungeonInstance inst = new DungeonInstance(instanceId, dungeon, hraci, leader);
        instancie.put(instanceId, inst);

        for (UUID uuid : hraci) {
            hracVInstancii.put(uuid, instanceId);
            plugin.getCooldownManager().nastavCooldown(uuid, dungeon.getId());
        }

        // Teleportuj hracov
        Location spawn = dungeon.getSpawnLokacia();
        if (spawn != null) {
            for (UUID uuid : hraci) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.teleport(spawn);
                    p.sendMessage(plugin.msg("dungeon-spusteny").replace("{name}", dungeon.getNazov()));
                }
            }
        }

        // Spusti moby s oneskorenim
        Bukkit.getScheduler().runTaskLater(plugin, () -> spawnMoby(inst), 60L);

        return inst;
    }

    private void spawnMoby(DungeonInstance inst) {
        Dungeon dungeon = inst.getDungeon();
        boolean mythicDostupny = Bukkit.getPluginManager().getPlugin("MythicMobs") != null;

        for (DungeonMobArea area : dungeon.getMobAreas()) {
            if (area.getMobTypy().isEmpty()) continue;
            int pocet = area.getRandomCount();
            Location loc = area.getLokacia();
            if (loc == null) continue;

            for (int i = 0; i < pocet; i++) {
                String mobTyp = area.getMobTypy().get(new Random().nextInt(area.getMobTypy().size()));
                if (mythicDostupny) {
                    spawnMythicMob(mobTyp, loc, inst);
                }
            }

            // Notifikuj hracov
            for (UUID uuid : inst.getHraci()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) p.sendMessage(plugin.msg("mob-spawn"));
            }
        }
    }

    private void spawnMythicMob(String typ, Location loc, DungeonInstance inst) {
        try {
            io.lumine.mythic.bukkit.MythicBukkit mythic = io.lumine.mythic.bukkit.MythicBukkit.inst();
            mythic.getMobManager().getActiveMob(
                mythic.getMobManager().spawnMob(typ, loc).getUniqueId()
            ).ifPresent(mob -> inst.getZiviMoby().add(mob.getUniqueId()));
        } catch (Exception e) {
            plugin.getLogger().warning("Nepodarilo sa spawnut mob: " + typ + " - " + e.getMessage());
        }
    }

    public void spawnBoss(DungeonInstance inst) {
        Dungeon dungeon = inst.getDungeon();
        if (dungeon.getBossTyp() == null || dungeon.getBossTyp().isEmpty()) return;
        Location bossLoc = dungeon.getBossSpawnLokacia();
        if (bossLoc == null) return;

        for (UUID uuid : inst.getHraci()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(plugin.msg("boss-spawn"));
        }

        inst.setBossZivy(true);
        boolean mythicDostupny = Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
        if (mythicDostupny) {
            spawnMythicMob(dungeon.getBossTyp(), bossLoc, inst);
        }
    }

    public void dokoncInstanciu(DungeonInstance inst) {
        inst.setDokonceny(true);
        String cas = inst.getFormattedTime();
        int skore = inst.getSkore();

        for (UUID uuid : inst.getHraci()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage(plugin.msg("dungeon-dokonceny")
                        .replace("{time}", cas)
                        .replace("{score}", String.valueOf(skore)));
            }
        }

        // Teleportuj hracov spat po 5 sekundach
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (UUID uuid : inst.getHraci()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                hracVInstancii.remove(uuid);
            }
            instancie.remove(inst.getInstanceId());
        }, 100L);
    }

    public void closeAllInstances() {
        for (DungeonInstance inst : instancie.values()) {
            for (UUID uuid : inst.getHraci()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    p.sendMessage(plugin.msg("dungeon-spusteny").replace("Spustam dungeon", "Server sa vypina, dungeon ukonceny"));
                }
            }
        }
        instancie.clear();
        hracVInstancii.clear();
    }

    public DungeonInstance getInstanciaHraca(UUID uuid) {
        String instanceId = hracVInstancii.get(uuid);
        if (instanceId == null) return null;
        return instancie.get(instanceId);
    }

    public boolean jeHracVInstancii(UUID uuid) {
        return hracVInstancii.containsKey(uuid);
    }

    public Map<String, DungeonInstance> getInstancie() { return instancie; }
}
