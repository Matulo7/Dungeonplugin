package eu.server.dungeon.models;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DungeonChest {

    private String id;
    private Location lokacia;
    private List<LootItem> lootTabula;

    public DungeonChest(String id, Location lokacia) {
        this.id = id;
        this.lokacia = lokacia;
        this.lootTabula = new ArrayList<>();
    }

    public List<ItemStack> generujLoot() {
        List<ItemStack> result = new ArrayList<>();
        Random rand = new Random();
        for (LootItem item : lootTabula) {
            if (rand.nextDouble() <= item.getSanca()) {
                int amount = item.getMinAmount() + rand.nextInt(Math.max(1, item.getMaxAmount() - item.getMinAmount() + 1));
                result.add(new ItemStack(item.getMaterial(), amount));
            }
        }
        return result;
    }

    public String getId() { return id; }
    public Location getLokacia() { return lokacia; }
    public List<LootItem> getLootTabula() { return lootTabula; }

    public static class LootItem {
        private Material material;
        private double sanca; // 0.0 - 1.0
        private int minAmount;
        private int maxAmount;

        public LootItem(Material material, double sanca, int minAmount, int maxAmount) {
            this.material = material;
            this.sanca = sanca;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }

        public Material getMaterial() { return material; }
        public double getSanca() { return sanca; }
        public int getMinAmount() { return minAmount; }
        public int getMaxAmount() { return maxAmount; }
    }
}
