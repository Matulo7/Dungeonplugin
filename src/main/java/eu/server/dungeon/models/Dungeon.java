package eu.server.dungeon.models;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class Dungeon {

    private String id;
    private String nazov;
    private String popis;
    private Material ikonka;
    private int obtiznost; // 1-10
    private Location spawnLokacia;
    private Location bossSpawnLokacia;
    private String bossTyp; // MythicMobs mob name
    private int minMobov;
    private int maxMobov;
    private boolean potrebujeKluc;
    private String svetnazov; // Minecraft world name kde je dungeon postaveny
    private List<DungeonMistnost> miestnosti;
    private List<DungeonChest> chesty;
    private List<DungeonMobArea> mobAreas;

    public Dungeon(String id) {
        this.id = id;
        this.nazov = id;
        this.popis = "Dungeon " + id;
        this.ikonka = Material.CHEST;
        this.obtiznost = 1;
        this.minMobov = 1;
        this.maxMobov = 3;
        this.potrebujeKluc = false;
        this.miestnosti = new ArrayList<>();
        this.chesty = new ArrayList<>();
        this.mobAreas = new ArrayList<>();
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getNazov() { return nazov; }
    public void setNazov(String nazov) { this.nazov = nazov; }
    public String getPopis() { return popis; }
    public void setPopis(String popis) { this.popis = popis; }
    public Material getIkonka() { return ikonka; }
    public void setIkonka(Material ikonka) { this.ikonka = ikonka; }
    public int getObtiznost() { return obtiznost; }
    public void setObtiznost(int obtiznost) { this.obtiznost = obtiznost; }
    public Location getSpawnLokacia() { return spawnLokacia; }
    public void setSpawnLokacia(Location spawnLokacia) { this.spawnLokacia = spawnLokacia; }
    public Location getBossSpawnLokacia() { return bossSpawnLokacia; }
    public void setBossSpawnLokacia(Location bossSpawnLokacia) { this.bossSpawnLokacia = bossSpawnLokacia; }
    public String getBossTyp() { return bossTyp; }
    public void setBossTyp(String bossTyp) { this.bossTyp = bossTyp; }
    public int getMinMobov() { return minMobov; }
    public void setMinMobov(int minMobov) { this.minMobov = minMobov; }
    public int getMaxMobov() { return maxMobov; }
    public void setMaxMobov(int maxMobov) { this.maxMobov = maxMobov; }
    public boolean isPotrebujeKluc() { return potrebujeKluc; }
    public void setPotrebujeKluc(boolean potrebujeKluc) { this.potrebujeKluc = potrebujeKluc; }
    public String getSvetnazov() { return svetnazov; }
    public void setSvetnazov(String svetnazov) { this.svetnazov = svetnazov; }
    public List<DungeonMistnost> getMiestnosti() { return miestnosti; }
    public List<DungeonChest> getChesty() { return chesty; }
    public List<DungeonMobArea> getMobAreas() { return mobAreas; }

    public int getRandomMobCount() {
        if (minMobov == maxMobov) return minMobov;
        return minMobov + new Random().nextInt(maxMobov - minMobov + 1);
    }
}
