package eu.server.dungeon.listeners;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.managers.CooldownManager;
import eu.server.dungeon.managers.InstanceManager;
import eu.server.dungeon.models.Dungeon;
import eu.server.dungeon.models.DungeonMobArea;
import eu.server.dungeon.utils.DungeonGUI;
import eu.server.dungeon.utils.EditorSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonListener implements Listener {

    private final DungeonPlugin plugin;

    public DungeonListener(DungeonPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Hlavne menu
        if (title.equals(DungeonGUI.MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
            if (event.getCurrentItem().getItemMeta() == null) return;

            // Najdi dungeon podla slotu
            int slot = event.getSlot();
            List<Dungeon> dungeony = new ArrayList<>(plugin.getDungeonManager().getAllDungeony());
            if (slot >= dungeony.size()) return;

            Dungeon d = dungeony.get(slot);
            CooldownManager cm = plugin.getCooldownManager();

            if (cm.maCooldown(player.getUniqueId(), d.getId())) {
                player.sendMessage(plugin.msg("dungeon-cooldown").replace("{time}", cm.getZostatok(player.getUniqueId(), d.getId())));
                return;
            }

            if (plugin.getInstanceManager().jeHracVInstancii(player.getUniqueId())) {
                player.sendMessage(plugin.colorize("&cUz si v dungeone!"));
                return;
            }

            player.closeInventory();
            List<UUID> hraci = plugin.getPartyManager().getHraciParty(player.getUniqueId());
            plugin.getInstanceManager().vytvorInstanciu(d, hraci, player.getUniqueId());
            return;
        }

        // Editor menu
        if (title.startsWith(DungeonGUI.EDITOR_MENU_TITLE)) {
            event.setCancelled(true);
            if (!player.hasPermission("dungeon.admin")) return;
            if (!EditorSession.hasSession(player)) return;

            Dungeon d = EditorSession.getEditedDungeon(player);
            int slot = event.getSlot();
            boolean lklik = event.isLeftClick();

            switch (slot) {
                case 10: // Nazov
                    player.closeInventory();
                    EditorSession.setAwaitingInput(player, "nazov");
                    player.sendMessage(plugin.colorize("&eNapisaj novy nazov dungeonu do chatu:"));
                    break;

                case 11: // Popis
                    player.closeInventory();
                    EditorSession.setAwaitingInput(player, "popis");
                    player.sendMessage(plugin.colorize("&eNapisaj novy popis dungeonu do chatu:"));
                    break;

                case 12: // Obtiznost
                    if (lklik) d.setObtiznost(Math.min(10, d.getObtiznost() + 1));
                    else d.setObtiznost(Math.max(1, d.getObtiznost() - 1));
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 13: // Spawn lokacia
                    d.setSpawnLokacia(player.getLocation());
                    d.setSvetnazov(player.getWorld().getName());
                    player.sendMessage(plugin.msg("editor-spawn-nastaveny"));
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 14: // Boss spawn
                    d.setBossSpawnLokacia(player.getLocation());
                    player.sendMessage(plugin.msg("editor-boss-nastaveny"));
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 15: // Boss typ
                    player.closeInventory();
                    EditorSession.setAwaitingInput(player, "boss-typ");
                    player.sendMessage(plugin.colorize("&eNapisaj MythicMobs nazov bossa do chatu:"));
                    break;

                case 20: // Mob oblast - pridaj novu
                    String areaId = "oblast_" + (d.getMobAreas().size() + 1);
                    DungeonMobArea novaOblast = new DungeonMobArea(areaId, player.getLocation());
                    d.getMobAreas().add(novaOblast);
                    player.closeInventory();
                    EditorSession.setAwaitingInput(player, "mob-oblast-typ:" + areaId);
                    player.sendMessage(plugin.colorize("&aNova mob oblast vytvorena na tvojej pozicii!"));
                    player.sendMessage(plugin.colorize("&eNapisaj MythicMobs mob typy oddelene ciarkou (napr: Zombie,Skeleton):"));
                    break;

                case 21: // Chesty - zatial info
                    player.sendMessage(plugin.colorize("&eChesty nastavuj priamo v dungeony.yml alebo pockaj na update!"));
                    break;

                case 22: // Miestnosti
                    player.sendMessage(plugin.colorize("&eMiestnosti nastavuj priamo v dungeony.yml alebo pockaj na update!"));
                    break;

                case 23: // Kluc toggle
                    d.setPotrebujeKluc(!d.isPotrebujeKluc());
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 24: // Ikonka
                    if (player.getInventory().getItemInMainHand() != null &&
                            !player.getInventory().getItemInMainHand().getType().isAir()) {
                        d.setIkonka(player.getInventory().getItemInMainHand().getType());
                        player.sendMessage(plugin.colorize("&aIkonka nastavena!"));
                        DungeonGUI.openEditorMenu(plugin, player, d);
                    }
                    break;

                case 30: // Min mobov
                    if (lklik) d.setMinMobov(Math.min(d.getMaxMobov(), d.getMinMobov() + 1));
                    else d.setMinMobov(Math.max(1, d.getMinMobov() - 1));
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 31: // Max mobov
                    if (lklik) d.setMaxMobov(d.getMaxMobov() + 1);
                    else d.setMaxMobov(Math.max(d.getMinMobov(), d.getMaxMobov() - 1));
                    DungeonGUI.openEditorMenu(plugin, player, d);
                    break;

                case 49: // Uloz
                    plugin.getDungeonManager().saveDungeon(d);
                    player.sendMessage(plugin.msg("editor-ulozeny").replace("{name}", d.getNazov()));
                    player.closeInventory();
                    EditorSession.endSession(player);
                    break;

                case 45: // Zrus
                    player.closeInventory();
                    EditorSession.endSession(player);
                    plugin.getDungeonManager().loadDungeons(); // Obnov povodny stav
                    player.sendMessage(plugin.colorize("&cZmeny boli zrusene."));
                    break;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!EditorSession.isAwaitingInput(player)) return;

        event.setCancelled(true);
        String vstup = event.getMessage();
        String typ = EditorSession.getInputType(player);
        Dungeon d = EditorSession.getEditedDungeon(player);

        if (vstup.equalsIgnoreCase("zrus") || vstup.equalsIgnoreCase("cancel")) {
            EditorSession.clearInput(player);
            player.sendMessage(plugin.colorize("&cVstup zruseny."));
            plugin.getServer().getScheduler().runTask(plugin, () -> DungeonGUI.openEditorMenu(plugin, player, d));
            return;
        }

        if (typ.equals("nazov")) {
            d.setNazov(vstup);
            player.sendMessage(plugin.colorize("&aNazov nastaveny na: &e" + vstup));
        } else if (typ.equals("popis")) {
            d.setPopis(vstup);
            player.sendMessage(plugin.colorize("&aPopis nastaveny!"));
        } else if (typ.equals("boss-typ")) {
            d.setBossTyp(vstup);
            player.sendMessage(plugin.colorize("&aBoss typ nastaveny na: &e" + vstup));
        } else if (typ.startsWith("mob-oblast-typ:")) {
            String areaId = typ.split(":")[1];
            String[] moby = vstup.split(",");
            for (DungeonMobArea area : d.getMobAreas()) {
                if (area.getId().equals(areaId)) {
                    area.getMobTypy().clear();
                    for (String mob : moby) area.getMobTypy().add(mob.trim());
                    break;
                }
            }
            player.sendMessage(plugin.colorize("&aMob typy nastavene!"));
        }

        EditorSession.clearInput(player);
        plugin.getServer().getScheduler().runTask(plugin, () -> DungeonGUI.openEditorMenu(plugin, player, d));
    }
}
