package eu.server.dungeon.utils;

import eu.server.dungeon.models.Dungeon;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorSession {

    private static final Map<UUID, Dungeon> sessions = new HashMap<>();
    private static final Map<UUID, String> awaitingInput = new HashMap<>(); // hrac -> typ vstupu

    public static void startSession(Player player, Dungeon dungeon) {
        sessions.put(player.getUniqueId(), dungeon);
    }

    public static void endSession(Player player) {
        sessions.remove(player.getUniqueId());
        awaitingInput.remove(player.getUniqueId());
    }

    public static boolean hasSession(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public static Dungeon getEditedDungeon(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public static void setAwaitingInput(Player player, String type) {
        awaitingInput.put(player.getUniqueId(), type);
    }

    public static boolean isAwaitingInput(Player player) {
        return awaitingInput.containsKey(player.getUniqueId());
    }

    public static String getInputType(Player player) {
        return awaitingInput.get(player.getUniqueId());
    }

    public static void clearInput(Player player) {
        awaitingInput.remove(player.getUniqueId());
    }
}
