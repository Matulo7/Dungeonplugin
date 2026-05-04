package eu.server.dungeon;

import eu.server.dungeon.commands.DungeonCommand;
import eu.server.dungeon.commands.PartyCommand;
import eu.server.dungeon.listeners.DungeonListener;
import eu.server.dungeon.listeners.PlayerListener;
import eu.server.dungeon.managers.CooldownManager;
import eu.server.dungeon.managers.DungeonManager;
import eu.server.dungeon.managers.InstanceManager;
import eu.server.dungeon.managers.PartyManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonPlugin extends JavaPlugin {

    private static DungeonPlugin instance;
    private DungeonManager dungeonManager;
    private PartyManager partyManager;
    private CooldownManager cooldownManager;
    private InstanceManager instanceManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.dungeonManager = new DungeonManager(this);
        this.partyManager = new PartyManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.instanceManager = new InstanceManager(this);

        dungeonManager.loadDungeons();

        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this));

        getServer().getPluginManager().registerEvents(new DungeonListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("DungeonPlugin bol uspesne spusteny!");
    }

    @Override
    public void onDisable() {
        if (instanceManager != null) {
            instanceManager.closeAllInstances();
        }
        getLogger().info("DungeonPlugin bol vypnuty.");
    }

    public static DungeonPlugin getInstance() {
        return instance;
    }

    public DungeonManager getDungeonManager() { return dungeonManager; }
    public PartyManager getPartyManager() { return partyManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public InstanceManager getInstanceManager() { return instanceManager; }

    public String msg(String key) {
        String prefix = getConfig().getString("prefix", "&8[&6Dungeon&8] &r");
        String msg = getConfig().getString("spravy." + key, "&cSprava nenajdena: " + key);
        return colorize(prefix + msg);
    }

    public String msgRaw(String key) {
        return colorize(getConfig().getString("spravy." + key, "&cSprava nenajdena: " + key));
    }

    public String colorize(String text) {
        return text.replace("&", "\u00a7");
    }
}
