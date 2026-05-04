package eu.server.dungeon.models;

import java.util.*;

public class Party {

    private UUID leader;
    private List<UUID> clenovia;
    private Map<UUID, UUID> pozvanky; // pozvanec -> leader

    public Party(UUID leader) {
        this.leader = leader;
        this.clenovia = new ArrayList<>();
        this.clenovia.add(leader);
        this.pozvanky = new HashMap<>();
    }

    public boolean pridajClena(UUID uuid) {
        if (clenovia.size() >= 4) return false;
        if (clenovia.contains(uuid)) return false;
        clenovia.add(uuid);
        return true;
    }

    public void odobrajClena(UUID uuid) {
        clenovia.remove(uuid);
    }

    public boolean jeVParty(UUID uuid) {
        return clenovia.contains(uuid);
    }

    public boolean jePlna() {
        return clenovia.size() >= 4;
    }

    public UUID getLeader() { return leader; }
    public void setLeader(UUID leader) { this.leader = leader; }
    public List<UUID> getClenovia() { return clenovia; }
    public Map<UUID, UUID> getPozvanky() { return pozvanky; }
}
