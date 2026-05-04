package eu.server.dungeon.models;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class DungeonMistnost {

    private String id;
    private Location dvereLocation;
    private Location klucDropLocation; // kde dropne kluc mini boss
    private String miniBossTyp; // MythicMobs mob
    private boolean zamknuta;
    private List<String> mobTypy; // MythicMobs mob names

    public DungeonMistnost(String id) {
        this.id = id;
        this.zamknuta = false;
        this.mobTypy = new ArrayList<>();
    }

    public String getId() { return id; }
    public Location getDvereLocation() { return dvereLocation; }
    public void setDvereLocation(Location dvereLocation) { this.dvereLocation = dvereLocation; }
    public Location getKlucDropLocation() { return klucDropLocation; }
    public void setKlucDropLocation(Location klucDropLocation) { this.klucDropLocation = klucDropLocation; }
    public String getMiniBossTyp() { return miniBossTyp; }
    public void setMiniBossTyp(String miniBossTyp) { this.miniBossTyp = miniBossTyp; }
    public boolean isZamknuta() { return zamknuta; }
    public void setZamknuta(boolean zamknuta) { this.zamknuta = zamknuta; }
    public List<String> getMobTypy() { return mobTypy; }
}
