package com.gmail.ianlim224.advancedlottery.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public final class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public ItemBuilder(ItemStack existing) {
        this.item = existing.clone();
    }

    public ItemBuilder displayName(Component name) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(Component... lines) {
        return lore(List.of(lines));
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = item.getItemMeta();
        meta.lore(List.of());
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchant, level, true);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchants) {
        enchants.forEach(this::addEnchant);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
