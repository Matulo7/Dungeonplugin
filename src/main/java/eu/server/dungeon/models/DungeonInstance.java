package eu.server.dungeon.models;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class DungeonInstance {

    private String instanceId;
    private Dungeon dungeon;
    private List<UUID> hraci;
    private UUID leader;
    private long startTime;
    private int skore;
    private Map<String, Boolean> miestnostiOtvorene; // mistnostId -> otvorena
    private List<UUID> ziviMoby; // entity UUIDs
    private boolean bossZivy;
    private boolean dokonceny;

    public DungeonInstance(String instanceId, Dungeon dungeon, List<UUID> hraci, UUID leader) {
        this.instanceId = instanceId;
        this.dungeon = dungeon;
        this.hraci = new ArrayList<>(hraci);
        this.leader = leader;
        this.startTime = System.currentTimeMillis();
        this.skore = 0;
        this.miestnostiOtvorene = new HashMap<>();
        this.ziviMoby = new ArrayList<>();
        this.bossZivy = false;
        this.dokonceny = false;

        for (DungeonMistnost m : dungeon.getMiestnosti()) {
            miestnostiOtvorene.put(m.getId(), !m.isZamknuta());
        }
    }

    public String getFormattedTime() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long min = elapsed / 60;
        long sec = elapsed % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public void pridajSkore(int body) { this.skore += body; }
    public boolean isMistnostOtvorena(String id) { return miestnostiOtvorene.getOrDefault(id, false); }
    public void otvorMistnost(String id) { miestnostiOtvorene.put(id, true); }

    public String getInstanceId() { return instanceId; }
    public Dungeon getDungeon() { return dungeon; }
    public List<UUID> getHraci() { return hraci; }
    public UUID getLeader() { return leader; }
    public long getStartTime() { return startTime; }
    public int getSkore() { return skore; }
    public List<UUID> getZiviMoby() { return ziviMoby; }
    public boolean isBossZivy() { return bossZivy; }
    public void setBossZivy(boolean bossZivy) { this.bossZivy = bossZivy; }
    public boolean isDokonceny() { return dokonceny; }
    public void setDokonceny(boolean dokonceny) { this.dokonceny = dokonceny; }
}
