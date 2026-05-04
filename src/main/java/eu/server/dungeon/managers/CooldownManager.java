package eu.server.dungeon.managers;

import eu.server.dungeon.DungeonPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final DungeonPlugin plugin;
    // hrac UUID -> (dungeon id -> cas posledneho vstupu)
    private final Map<UUID, Map<String, Long>> cooldowny;

    public CooldownManager(DungeonPlugin plugin) {
        this.plugin = plugin;
        this.cooldowny = new HashMap<>();
    }

    public boolean maCooldown(UUID uuid, String dungeonId) {
        Map<String, Long> hracCooldowny = cooldowny.get(uuid);
        if (hracCooldowny == null) return false;
        Long cas = hracCooldowny.get(dungeonId);
        if (cas == null) return false;
        long cooldownMs = plugin.getConfig().getLong("cooldown", 14400) * 1000L;
        return (System.currentTimeMillis() - cas) < cooldownMs;
    }

    public String getZostatok(UUID uuid, String dungeonId) {
        Map<String, Long> hracCooldowny = cooldowny.get(uuid);
        if (hracCooldowny == null) return "0s";
        Long cas = hracCooldowny.get(dungeonId);
        if (cas == null) return "0s";
        long cooldownMs = plugin.getConfig().getLong("cooldown", 14400) * 1000L;
        long zostatokMs = cooldownMs - (System.currentTimeMillis() - cas);
        if (zostatokMs <= 0) return "0s";
        long sekundy = zostatokMs / 1000;
        long hodiny = sekundy / 3600;
        long minuty = (sekundy % 3600) / 60;
        long sek = sekundy % 60;
        if (hodiny > 0) return hodiny + "h " + minuty + "m " + sek + "s";
        if (minuty > 0) return minuty + "m " + sek + "s";
        return sek + "s";
    }

    public void nastavCooldown(UUID uuid, String dungeonId) {
        cooldowny.computeIfAbsent(uuid, k -> new HashMap<>()).put(dungeonId, System.currentTimeMillis());
    }

    public void resetCooldown(UUID uuid, String dungeonId) {
        Map<String, Long> hracCooldowny = cooldowny.get(uuid);
        if (hracCooldowny != null) hracCooldowny.remove(dungeonId);
    }
}
