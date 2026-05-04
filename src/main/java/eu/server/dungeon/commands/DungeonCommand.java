package eu.server.dungeon.commands;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.managers.DungeonManager;
import eu.server.dungeon.managers.InstanceManager;
import eu.server.dungeon.models.Dungeon;
import eu.server.dungeon.models.DungeonInstance;
import eu.server.dungeon.models.DungeonMobArea;
import eu.server.dungeon.utils.DungeonGUI;
import eu.server.dungeon.utils.EditorSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DungeonCommand implements CommandExecutor {

    private final DungeonPlugin plugin;

    public DungeonCommand(DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("prikaz-iba-hrac"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Otvor hlavne GUI
            DungeonGUI.openMainMenu(plugin, player);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "vytvor": {
                if (!player.hasPermission("dungeon.admin")) {
                    player.sendMessage(plugin.msg("nedostatok-prav")); return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/dungeon vytvor <id>"); return true;
                }
                String id = args[1];
                if (plugin.getDungeonManager().existuje(id)) {
                    player.sendMessage(plugin.colorize("&cDungeon s tymto ID uz existuje!")); return true;
                }
                Dungeon d = new Dungeon(id);
                d.setSvetnazov(player.getWorld().getName());
                plugin.getDungeonManager().saveDungeon(d);
                player.sendMessage(plugin.msg("dungeon-vytvoreny").replace("{name}", id));
                break;
            }

            case "zmaz": {
                if (!player.hasPermission("dungeon.admin")) {
                    player.sendMessage(plugin.msg("nedostatok-prav")); return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/dungeon zmaz <id>"); return true;
                }
                String id = args[1];
                if (!plugin.getDungeonManager().existuje(id)) {
                    player.sendMessage(plugin.msg("dungeon-neexistuje")); return true;
                }
                plugin.getDungeonManager().deleteDungeon(id);
                player.sendMessage(plugin.msg("dungeon-zmazany").replace("{name}", id));
                break;
            }

            case "edit": {
                if (!player.hasPermission("dungeon.admin")) {
                    player.sendMessage(plugin.msg("nedostatok-prav")); return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/dungeon edit <id>"); return true;
                }
                String id = args[1];
                Dungeon d = plugin.getDungeonManager().getDungeon(id);
                if (d == null) {
                    player.sendMessage(plugin.msg("dungeon-neexistuje")); return true;
                }
                EditorSession.startSession(player, d);
                DungeonGUI.openEditorMenu(plugin, player, d);
                player.sendMessage(plugin.msg("editor-rezim").replace("{name}", d.getNazov()));
                break;
            }

            case "spusti": {
                if (!player.hasPermission("dungeon.admin")) {
                    player.sendMessage(plugin.msg("nedostatok-prav")); return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/dungeon spusti <id>"); return true;
                }
                String id = args[1];
                Dungeon d = plugin.getDungeonManager().getDungeon(id);
                if (d == null) {
                    player.sendMessage(plugin.msg("dungeon-neexistuje")); return true;
                }
                List<UUID> hraci = plugin.getPartyManager().getHraciParty(player.getUniqueId());
                plugin.getInstanceManager().vytvorInstanciu(d, hraci, player.getUniqueId());
                break;
            }

            case "reload": {
                if (!player.hasPermission("dungeon.admin")) {
                    player.sendMessage(plugin.msg("nedostatok-prav")); return true;
                }
                plugin.getDungeonManager().loadDungeons();
                player.sendMessage(plugin.colorize(plugin.getConfig().getString("prefix") + "&aDungeony boli znovu nacitane!"));
                break;
            }

            case "info": {
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/dungeon info <id>"); return true;
                }
                Dungeon d = plugin.getDungeonManager().getDungeon(args[1]);
                if (d == null) { player.sendMessage(plugin.msg("dungeon-neexistuje")); return true; }
                player.sendMessage(plugin.colorize("&6=== &e" + d.getNazov() + " &6==="));
                player.sendMessage(plugin.colorize("&7Popis: &f" + d.getPopis()));
                player.sendMessage(plugin.colorize("&7Obtiznost: &e" + d.getObtiznost()));
                player.sendMessage(plugin.colorize("&7Moby: &e" + d.getMinMobov() + " - " + d.getMaxMobov()));
                player.sendMessage(plugin.colorize("&7Kluc: &e" + (d.isPotrebujeKluc() ? "Ano" : "Nie")));
                player.sendMessage(plugin.colorize("&7Boss: &e" + (d.getBossTyp() != null ? d.getBossTyp() : "Nenastaveny")));
                player.sendMessage(plugin.colorize("&7Miestnosti: &e" + d.getMiestnosti().size()));
                player.sendMessage(plugin.colorize("&7Mob oblasti: &e" + d.getMobAreas().size()));
                player.sendMessage(plugin.colorize("&7Chesty: &e" + d.getChesty().size()));
                break;
            }

            default: {
                player.sendMessage(plugin.colorize("&6=== &eDungeon Pomoc &6==="));
                player.sendMessage(plugin.colorize("&e/dungeon &7- Otvor menu dungeonov"));
                player.sendMessage(plugin.colorize("&e/dungeon vytvor <id> &7- Vytvor novy dungeon"));
                player.sendMessage(plugin.colorize("&e/dungeon zmaz <id> &7- Zmaz dungeon"));
                player.sendMessage(plugin.colorize("&e/dungeon edit <id> &7- Uprav dungeon"));
                player.sendMessage(plugin.colorize("&e/dungeon info <id> &7- Info o dungeone"));
                player.sendMessage(plugin.colorize("&e/dungeon spusti <id> &7- Spusti dungeon (admin)"));
                player.sendMessage(plugin.colorize("&e/dungeon reload &7- Znovu nacitaj dungeony"));
                break;
            }
        }
        return true;
    }
}
