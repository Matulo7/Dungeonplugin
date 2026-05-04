package eu.server.dungeon.managers;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyManager {

    private final DungeonPlugin plugin;
    private final Map<UUID, Party> party; // leader UUID -> Party
    private final Map<UUID, UUID> hracVParty; // hrac UUID -> leader UUID

    public PartyManager(DungeonPlugin plugin) {
        this.plugin = plugin;
        this.party = new HashMap<>();
        this.hracVParty = new HashMap<>();
    }

    public Party vytvorParty(Player leader) {
        Party p = new Party(leader.getUniqueId());
        party.put(leader.getUniqueId(), p);
        hracVParty.put(leader.getUniqueId(), leader.getUniqueId());
        return p;
    }

    public boolean pozviHraca(Player leader, Player ciel) {
        Party p = getPartyLeadera(leader.getUniqueId());
        if (p == null) return false;
        if (p.jePlna()) return false;
        p.getPozvanky().put(ciel.getUniqueId(), leader.getUniqueId());
        return true;
    }

    public boolean pridajHraca(Player hrac, UUID leaderUUID) {
        Party p = party.get(leaderUUID);
        if (p == null) return false;
        if (!p.getPozvanky().containsKey(hrac.getUniqueId())) return false;
        if (p.jePlna()) return false;
        p.pridajClena(hrac.getUniqueId());
        p.getPozvanky().remove(hrac.getUniqueId());
        hracVParty.put(hrac.getUniqueId(), leaderUUID);
        return true;
    }

    public void odisielZParty(Player hrac) {
        UUID leaderUUID = hracVParty.get(hrac.getUniqueId());
        if (leaderUUID == null) return;
        Party p = party.get(leaderUUID);
        if (p == null) return;
        p.odobrajClena(hrac.getUniqueId());
        hracVParty.remove(hrac.getUniqueId());

        if (p.getClenovia().isEmpty()) {
            party.remove(leaderUUID);
        } else if (leaderUUID.equals(hrac.getUniqueId())) {
            // Prevedie leadership
            UUID novyLeader = p.getClenovia().get(0);
            p.setLeader(novyLeader);
            party.remove(leaderUUID);
            party.put(novyLeader, p);
            hracVParty.put(novyLeader, novyLeader);
            Player novyLeaderHrac = Bukkit.getPlayer(novyLeader);
            if (novyLeaderHrac != null) {
                novyLeaderHrac.sendMessage(plugin.msg("party-vytvorena").replace("vytvorena", "si teraz leader party!"));
            }
        }
    }

    public Party getPartyHraca(UUID uuid) {
        UUID leaderUUID = hracVParty.get(uuid);
        if (leaderUUID == null) return null;
        return party.get(leaderUUID);
    }

    public Party getPartyLeadera(UUID leaderUUID) {
        return party.get(leaderUUID);
    }

    public boolean jeVParty(UUID uuid) {
        return hracVParty.containsKey(uuid);
    }

    public boolean jeMaParty(UUID uuid) {
        return party.containsKey(uuid);
    }

    public List<UUID> getHraciParty(UUID uuid) {
        Party p = getPartyHraca(uuid);
        if (p == null) {
            List<UUID> solo = new ArrayList<>();
            solo.add(uuid);
            return solo;
        }
        return p.getClenovia();
    }

    public UUID getPozvankaLeader(UUID hrac) {
        // Najdi prvu pozvanku pre hraca
        for (Party p : party.values()) {
            if (p.getPozvanky().containsKey(hrac)) {
                return p.getLeader();
            }
        }
        return null;
    }
}
