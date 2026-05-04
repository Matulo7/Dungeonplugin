package eu.server.dungeon.utils;

import eu.server.dungeon.DungeonPlugin;
import eu.server.dungeon.models.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DungeonGUI {

    public static final String MAIN_MENU_TITLE = "\u00a78[\u00a76Dungeony\u00a78]";
    public static final String EDITOR_MENU_TITLE = "\u00a78[\u00a7eEditor\u00a78] ";

    public static void openMainMenu(DungeonPlugin plugin, Player player) {
        Collection<Dungeon> dungeony = plugin.getDungeonManager().getAllDungeony();
        int size = Math.max(9, (int) Math.ceil(dungeony.size() / 9.0) * 9);
        if (size > 54) size = 54;

        Inventory inv = Bukkit.createInventory(null, size, MAIN_MENU_TITLE);

        int slot = 0;
        for (Dungeon d : dungeony) {
            if (slot >= size) break;
            boolean maCooldown = plugin.getCooldownManager().maCooldown(player.getUniqueId(), d.getId());
            String zostatok = plugin.getCooldownManager().getZostatok(player.getUniqueId(), d.getId());

            ItemStack item = new ItemStack(maCooldown ? Material.RED_STAINED_GLASS_PANE : d.getIkonka());
            ItemMeta meta = item.getItemMeta();

            String nazov = plugin.colorize("&6" + d.getNazov() + " &7[Obtiznost: &e" + d.getObtiznost() + "&7]");
            meta.setDisplayName(nazov);

            List<String> lore = new ArrayList<>();
            lore.add(plugin.colorize("&7" + d.getPopis()));
            lore.add(plugin.colorize("&8-----------------------"));
            lore.add(plugin.colorize("&7Moby: &e" + d.getMinMobov() + " - " + d.getMaxMobov()));
            lore.add(plugin.colorize("&7Kluc: &e" + (d.isPotrebujeKluc() ? "Ano" : "Nie")));
            lore.add(plugin.colorize("&7Miestnosti: &e" + d.getMiestnosti().size()));
            lore.add(plugin.colorize("&8-----------------------"));
            if (maCooldown) {
                lore.add(plugin.colorize("&cCooldown: &e" + zostatok));
            } else {
                lore.add(plugin.colorize("&aKlikni pre vstup!"));
            }
            meta.setLore(lore);

            if (!maCooldown) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
        }

        if (dungeony.isEmpty()) {
            ItemStack prazdny = new ItemStack(Material.BARRIER);
            ItemMeta meta = prazdny.getItemMeta();
            meta.setDisplayName(plugin.colorize("&cZiadne dungeony nie su dostupne"));
            prazdny.setItemMeta(meta);
            inv.setItem(4, prazdny);
        }

        player.openInventory(inv);
    }

    public static void openEditorMenu(DungeonPlugin plugin, Player player, Dungeon d) {
        Inventory inv = Bukkit.createInventory(null, 54, EDITOR_MENU_TITLE + d.getNazov());

        // Nastavenia dungeonu
        inv.setItem(10, makeItem(Material.NAME_TAG, plugin.colorize("&eNazov: &f" + d.getNazov()),
                plugin.colorize("&7Klikni pre zmenu nazvu")));

        inv.setItem(11, makeItem(Material.BOOK, plugin.colorize("&ePopis: &f" + d.getPopis()),
                plugin.colorize("&7Klikni pre zmenu popisu")));

        inv.setItem(12, makeItem(Material.DIAMOND_SWORD, plugin.colorize("&eObtiznost: &f" + d.getObtiznost()),
                plugin.colorize("&7Lklik = +1, Pklik = -1")));

        inv.setItem(13, makeItem(Material.COMPASS, plugin.colorize("&eSpawn lokacia"),
                plugin.colorize("&7Klikni pre nastavenie spawnu na tvoju poziciu")));

        inv.setItem(14, makeItem(Material.NETHER_STAR, plugin.colorize("&eBoss spawn lokacia"),
                plugin.colorize("&7Klikni pre nastavenie boss spawnu")));

        inv.setItem(15, makeItem(Material.ZOMBIE_HEAD, plugin.colorize("&eBoss typ: &f" + (d.getBossTyp() != null ? d.getBossTyp() : "Nenastaveny")),
                plugin.colorize("&7Klikni pre zmenu MythicMobs boss typu")));

        inv.setItem(20, makeItem(Material.CREEPER_HEAD, plugin.colorize("&eMob oblasti"),
                plugin.colorize("&7Pridaj/uprav oblasti kde sa spawnu moby"),
                plugin.colorize("&7Pocet oblasti: &e" + d.getMobAreas().size())));

        inv.setItem(21, makeItem(Material.CHEST, plugin.colorize("&eChesty s lootom"),
                plugin.colorize("&7Pridaj/uprav chesty s nahodnym lootom"),
                plugin.colorize("&7Pocet chestov: &e" + d.getChesty().size())));

        inv.setItem(22, makeItem(Material.IRON_DOOR, plugin.colorize("&eMiestnosti a kluce"),
                plugin.colorize("&7Pridaj/uprav uzamknute miestnosti"),
                plugin.colorize("&7Pocet miestnosti: &e" + d.getMiestnosti().size())));

        inv.setItem(23, makeItem(d.isPotrebujeKluc() ? Material.LIME_DYE : Material.GRAY_DYE,
                plugin.colorize("&eKluc: &f" + (d.isPotrebujeKluc() ? "&aAno" : "&cNie")),
                plugin.colorize("&7Klikni pre prepnutie")));

        inv.setItem(24, makeItem(d.getIkonka(), plugin.colorize("&eIkonka v menu"),
                plugin.colorize("&7Drz item v ruke a klikni pre zmenu")));

        // Min/Max mobov
        inv.setItem(30, makeItem(Material.SLIME_BALL, plugin.colorize("&eMin mobov: &f" + d.getMinMobov()),
                plugin.colorize("&7Lklik = +1, Pklik = -1")));

        inv.setItem(31, makeItem(Material.MAGMA_CREAM, plugin.colorize("&eMax mobov: &f" + d.getMaxMobov()),
                plugin.colorize("&7Lklik = +1, Pklik = -1")));

        // Uloz
        inv.setItem(49, makeItem(Material.EMERALD, plugin.colorize("&a&lULOZ DUNGEON"),
                plugin.colorize("&7Klikni pre ulozenie vsetkych zmien")));

        // Zrus
        inv.setItem(45, makeItem(Material.BARRIER, plugin.colorize("&c&lZATVOR BEZ ULOZENIA"),
                plugin.colorize("&7Klikni pre zavrenie bez ulozenia")));

        player.openInventory(inv);
    }

    private static ItemStack makeItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) lore.add(line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
