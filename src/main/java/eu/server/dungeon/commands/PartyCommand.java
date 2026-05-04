package eu.server.dungeon.commands;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.managers.PartyManager;
import eu.server.dungeon.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand implements CommandExecutor {

    private final DungeonPlugin plugin;

    public PartyCommand(DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("prikaz-iba-hrac"));
            return true;
        }

        Player player = (Player) sender;
        PartyManager pm = plugin.getPartyManager();

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "vytvor":
            case "create": {
                if (pm.jeVParty(player.getUniqueId())) {
                    player.sendMessage(plugin.msg("party-uz-mas"));
                    return true;
                }
                pm.vytvorParty(player);
                player.sendMessage(plugin.msg("party-vytvorena"));
                break;
            }

            case "pozvi":
            case "invite": {
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/party pozvi <hrac>");
                    return true;
                }
                if (!pm.jeMaParty(player.getUniqueId())) {
                    player.sendMessage(plugin.colorize("&cNemas party! Pouzi /party vytvor"));
                    return true;
                }
                Player ciel = Bukkit.getPlayer(args[1]);
                if (ciel == null) {
                    player.sendMessage(plugin.colorize("&cHrac neexistuje alebo nie je online!"));
                    return true;
                }
                if (pm.jeVParty(ciel.getUniqueId())) {
                    player.sendMessage(plugin.colorize("&cTento hrac uz je v party!"));
                    return true;
                }
                Party p = pm.getPartyLeadera(player.getUniqueId());
                if (p != null && p.jePlna()) {
                    player.sendMessage(plugin.msg("party-plna"));
                    return true;
                }
                pm.pozviHraca(player, ciel);
                ciel.sendMessage(plugin.msg("party-pozvanка")
                        .replace("{player}", player.getName()));
                player.sendMessage(plugin.colorize("&aPozvanka bola odoslana hracovi &e" + ciel.getName() + "&a!"));
                break;
            }

            case "pripoj":
            case "join": {
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("nespravne-pouzitie") + "/party pripoj <leader>");
                    return true;
                }
                if (pm.jeVParty(player.getUniqueId())) {
                    player.sendMessage(plugin.msg("party-uz-v-party"));
                    return true;
                }
                Player leader = Bukkit.getPlayer(args[1]);
                if (leader == null) {
                    player.sendMessage(plugin.colorize("&cHrac neexistuje alebo nie je online!"));
                    return true;
                }
                boolean uspech = pm.pridajHraca(player, leader.getUniqueId());
                if (!uspech) {
                    player.sendMessage(plugin.colorize("&cNemas pozvanku do tejto party alebo je plna!"));
                    return true;
                }
                player.sendMessage(plugin.msg("party-pripojen").replace("{player}", leader.getName()));
                Party p = pm.getPartyLeadera(leader.getUniqueId());
                if (p != null) {
                    for (UUID uuid : p.getClenovia()) {
                        Player clen = Bukkit.getPlayer(uuid);
                        if (clen != null && !clen.equals(player)) {
                            clen.sendMessage(plugin.colorize("&e" + player.getName() + " &asa pripojil do party!"));
                        }
                    }
                }
                break;
            }

            case "odist":
            case "leave": {
                if (!pm.jeVParty(player.getUniqueId())) {
                    player.sendMessage(plugin.colorize("&cNie si v party!"));
                    return true;
                }
                Party p = pm.getPartyHraca(player.getUniqueId());
                pm.odisielZParty(player);
                player.sendMessage(plugin.msg("party-odisiel"));
                if (p != null) {
                    for (UUID uuid : p.getClenovia()) {
                        Player clen = Bukkit.getPlayer(uuid);
                        if (clen != null) {
                            clen.sendMessage(plugin.colorize("&e" + player.getName() + " &copustil party!"));
                        }
                    }
                }
                break;
            }

            case "info": {
                Party p = pm.getPartyHraca(player.getUniqueId());
                if (p == null) {
                    player.sendMessage(plugin.colorize("&cNie si v party!"));
                    return true;
                }
                Player leaderPlayer = Bukkit.getPlayer(p.getLeader());
                String leaderName = leaderPlayer != null ? leaderPlayer.getName() : "Neznamy";
                player.sendMessage(plugin.msg("party-info").replace("{leader}", leaderName));
                for (UUID uuid : p.getClenovia()) {
                    Player clen = Bukkit.getPlayer(uuid);
                    String meno = clen != null ? clen.getName() : uuid.toString();
                    player.sendMessage(plugin.msg("party-clen").replace("{player}", meno));
                }
                break;
            }

            default:
                sendHelp(player);
                break;
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.colorize("&6=== &eParty Pomoc &6==="));
        player.sendMessage(plugin.colorize("&e/party vytvor &7- Vytvor novu party"));
        player.sendMessage(plugin.colorize("&e/party pozvi <hrac> &7- Pozvi hraca do party"));
        player.sendMessage(plugin.colorize("&e/party pripoj <leader> &7- Pripoj sa do party"));
        player.sendMessage(plugin.colorize("&e/party odist &7- Odist z party"));
        player.sendMessage(plugin.colorize("&e/party info &7- Info o tvojej party"));
    }
}
