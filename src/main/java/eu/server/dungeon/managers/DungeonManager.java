package eu.server.dungeon.managers;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.models.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonManager {

    private final DungeonPlugin plugin;
    private final Map<String, Dungeon> dungeony;
    private File dungeonyFile;
    private FileConfiguration dungeonyConfig;

    public DungeonManager(DungeonPlugin plugin) {
        this.plugin = plugin;
        this.dungeony = new LinkedHashMap<>();
    }

    public void loadDungeons() {
        dungeonyFile = new File(plugin.getDataFolder(), "dungeony.yml");
        if (!dungeonyFile.exists()) {
            try { dungeonyFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dungeonyConfig = YamlConfiguration.loadConfiguration(dungeonyFile);

        dungeony.clear();
        ConfigurationSection sekcia = dungeonyConfig.getConfigurationSection("dungeony");
        if (sekcia == null) return;

        for (String id : sekcia.getKeys(false)) {
            ConfigurationSection ds = sekcia.getConfigurationSection(id);
            if (ds == null) continue;

            Dungeon d = new Dungeon(id);
            d.setNazov(ds.getString("nazov", id));
            d.setPopis(ds.getString("popis", ""));
            d.setObtiznost(ds.getInt("obtiznost", 1));
            d.setMinMobov(ds.getInt("min-mobov", 1));
            d.setMaxMobov(ds.getInt("max-mobov", 3));
            d.setPotrebujeKluc(ds.getBoolean("potrebuje-kluc", false));
            d.setSvetnazov(ds.getString("svet", "world"));
            d.setBossTyp(ds.getString("boss-typ", ""));

            String ikonkaStr = ds.getString("ikonka", "CHEST");
            try { d.setIkonka(Material.valueOf(ikonkaStr)); } catch (Exception ignored) {}

            // Spawn lokacia
            if (ds.contains("spawn")) {
                d.setSpawnLokacia(loadLocation(ds.getConfigurationSection("spawn")));
            }
            if (ds.contains("boss-spawn")) {
                d.setBossSpawnLokacia(loadLocation(ds.getConfigurationSection("boss-spawn")));
            }

            // Miestnosti
            ConfigurationSection miestnostiSec = ds.getConfigurationSection("miestnosti");
            if (miestnostiSec != null) {
                for (String mid : miestnostiSec.getKeys(false)) {
                    ConfigurationSection ms = miestnostiSec.getConfigurationSection(mid);
                    DungeonMistnost m = new DungeonMistnost(mid);
                    m.setZamknuta(ms.getBoolean("zamknuta", false));
                    m.setMiniBossTyp(ms.getString("mini-boss", ""));
                    if (ms.contains("dvere")) m.setDvereLocation(loadLocation(ms.getConfigurationSection("dvere")));
                    if (ms.contains("kluc-drop")) m.setKlucDropLocation(loadLocation(ms.getConfigurationSection("kluc-drop")));
                    m.getMobTypy().addAll(ms.getStringList("moby"));
                    d.getMiestnosti().add(m);
                }
            }

            // Mob areas
            ConfigurationSection mobSec = ds.getConfigurationSection("mob-oblasti");
            if (mobSec != null) {
                for (String amid : mobSec.getKeys(false)) {
                    ConfigurationSection ms = mobSec.getConfigurationSection(amid);
                    Location loc = loadLocation(ms.getConfigurationSection("lokacia"));
                    DungeonMobArea area = new DungeonMobArea(amid, loc);
                    area.setMinMobov(ms.getInt("min", 1));
                    area.setMaxMobov(ms.getInt("max", 3));
                    area.getMobTypy().addAll(ms.getStringList("moby"));
                    d.getMobAreas().add(area);
                }
            }

            // Chesty
            ConfigurationSection chestSec = ds.getConfigurationSection("chesty");
            if (chestSec != null) {
                for (String cid : chestSec.getKeys(false)) {
                    ConfigurationSection cs = chestSec.getConfigurationSection(cid);
                    Location loc = loadLocation(cs.getConfigurationSection("lokacia"));
                    DungeonChest chest = new DungeonChest(cid, loc);
                    ConfigurationSection lootSec = cs.getConfigurationSection("loot");
                    if (lootSec != null) {
                        for (String lid : lootSec.getKeys(false)) {
                            ConfigurationSection ls = lootSec.getConfigurationSection(lid);
                            try {
                                Material mat = Material.valueOf(ls.getString("material", "DIRT"));
                                double sanca = ls.getDouble("sanca", 0.5);
                                int min = ls.getInt("min", 1);
                                int max = ls.getInt("max", 1);
                                chest.getLootTabula().add(new DungeonChest.LootItem(mat, sanca, min, max));
                            } catch (Exception ignored) {}
                        }
                    }
                    d.getChesty().add(chest);
                }
            }

            dungeony.put(id, d);
        }
        plugin.getLogger().info("Nacitanych " + dungeony.size() + " dungeono(v).");
    }

    public void saveDungeon(Dungeon d) {
        String path = "dungeony." + d.getId();
        dungeonyConfig.set(path + ".nazov", d.getNazov());
        dungeonyConfig.set(path + ".popis", d.getPopis());
        dungeonyConfig.set(path + ".obtiznost", d.getObtiznost());
        dungeonyConfig.set(path + ".min-mobov", d.getMinMobov());
        dungeonyConfig.set(path + ".max-mobov", d.getMaxMobov());
        dungeonyConfig.set(path + ".potrebuje-kluc", d.isPotrebujeKluc());
        dungeonyConfig.set(path + ".svet", d.getSvetnazov());
        dungeonyConfig.set(path + ".boss-typ", d.getBossTyp());
        dungeonyConfig.set(path + ".ikonka", d.getIkonka().name());

        if (d.getSpawnLokacia() != null) saveLocation(dungeonyConfig, path + ".spawn", d.getSpawnLokacia());
        if (d.getBossSpawnLokacia() != null) saveLocation(dungeonyConfig, path + ".boss-spawn", d.getBossSpawnLokacia());

        // Miestnosti
        for (DungeonMistnost m : d.getMiestnosti()) {
            String mp = path + ".miestnosti." + m.getId();
            dungeonyConfig.set(mp + ".zamknuta", m.isZamknuta());
            dungeonyConfig.set(mp + ".mini-boss", m.getMiniBossTyp());
            dungeonyConfig.set(mp + ".moby", m.getMobTypy());
            if (m.getDvereLocation() != null) saveLocation(dungeonyConfig, mp + ".dvere", m.getDvereLocation());
            if (m.getKlucDropLocation() != null) saveLocation(dungeonyConfig, mp + ".kluc-drop", m.getKlucDropLocation());
        }

        // Mob oblasti
        for (DungeonMobArea area : d.getMobAreas()) {
            String ap = path + ".mob-oblasti." + area.getId();
            dungeonyConfig.set(ap + ".min", area.getMinMobov());
            dungeonyConfig.set(ap + ".max", area.getMaxMobov());
            dungeonyConfig.set(ap + ".moby", area.getMobTypy());
            if (area.getLokacia() != null) saveLocation(dungeonyConfig, ap + ".lokacia", area.getLokacia());
        }

        // Chesty
        for (DungeonChest chest : d.getChesty()) {
            String cp = path + ".chesty." + chest.getId();
            if (chest.getLokacia() != null) saveLocation(dungeonyConfig, cp + ".lokacia", chest.getLokacia());
            int i = 0;
            for (DungeonChest.LootItem item : chest.getLootTabula()) {
                String lp = cp + ".loot.item" + i;
                dungeonyConfig.set(lp + ".material", item.getMaterial().name());
                dungeonyConfig.set(lp + ".sanca", item.getSanca());
                dungeonyConfig.set(lp + ".min", item.getMinAmount());
                dungeonyConfig.set(lp + ".max", item.getMaxAmount());
                i++;
            }
        }

        try {
            dungeonyConfig.save(dungeonyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dungeony.put(d.getId(), d);
    }

    public void deleteDungeon(String id) {
        dungeonyConfig.set("dungeony." + id, null);
        try { dungeonyConfig.save(dungeonyFile); } catch (IOException e) { e.printStackTrace(); }
        dungeony.remove(id);
    }

    private Location loadLocation(ConfigurationSection sec) {
        if (sec == null) return null;
        World world = Bukkit.getWorld(sec.getString("svet", "world"));
        double x = sec.getDouble("x");
        double y = sec.getDouble("y");
        double z = sec.getDouble("z");
        float yaw = (float) sec.getDouble("yaw", 0);
        float pitch = (float) sec.getDouble("pitch", 0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    private void saveLocation(FileConfiguration config, String path, Location loc) {
        config.set(path + ".svet", loc.getWorld() != null ? loc.getWorld().getName() : "world");
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }

    public Dungeon getDungeon(String id) { return dungeony.get(id); }
    public Collection<Dungeon> getAllDungeony() { return dungeony.values(); }
    public boolean existuje(String id) { return dungeony.containsKey(id); }
}
