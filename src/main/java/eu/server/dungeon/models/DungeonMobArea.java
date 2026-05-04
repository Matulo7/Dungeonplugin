package eu.server.dungeon.models;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class DungeonMobArea {

    private String id;
    private Location lokacia;
    private List<String> mobTypy; // MythicMobs mob names
    private int minMobov;
    private int maxMobov;

    public DungeonMobArea(String id, Location lokacia) {
        this.id = id;
        this.lokacia = lokacia;
        this.mobTypy = new ArrayList<>();
        this.minMobov = 1;
        this.maxMobov = 3;
    }

    public String getId() { return id; }
    public Location getLokacia() { return lokacia; }
    public List<String> getMobTypy() { return mobTypy; }
    public int getMinMobov() { return minMobov; }
    public void setMinMobov(int minMobov) { this.minMobov = minMobov; }
    public int getMaxMobov() { return maxMobov; }
    public void setMaxMobov(int maxMobov) { this.maxMobov = maxMobov; }

    public int getRandomCount() {
        if (minMobov == maxMobov) return minMobov;
        return minMobov + new java.util.Random().nextInt(maxMobov - minMobov + 1);
    }
}
