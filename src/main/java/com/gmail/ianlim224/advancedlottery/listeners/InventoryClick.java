package com.gmail.ianlim224.advancedlottery.listeners;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.gui.*;
import com.gmail.ianlim224.advancedlottery.legacy.SkullManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryClick implements Listener {

    private final ItemGrabber grabber;
    private final LotteryGUI gui;
    private final AdvancedLottery plugin;

    public InventoryClick(AdvancedLottery plugin) {
        this.grabber = ItemGrabber.getInstance(plugin);
        this.gui     = LotteryGUI.getInstance();
        this.plugin  = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory top = event.getView().getTopInventory();

        if (top.getHolder() instanceof HelpHolder
                || top.getHolder() instanceof ConfirmHolder
                || top.getHolder() instanceof LotteryHolder) {
            event.setCancelled(true);
        }

        if (!(top.getHolder() instanceof LotteryHolder) || item == null) return;

        if (item.isSimilar(grabber.getNextArrow())) {
            gui.openNextPage(player);
            return;
        }

        if (item.isSimilar(grabber.getPreviousArrow())) {
            gui.openPreviousPage(player);
            return;
        }

        if (item.isSimilar(grabber.getBuyButton())) {
            Bukkit.dispatchCommand(player, "lottery buy");
            return;
        }

        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta) {
            OfflinePlayer target = SkullManager.getPlayer(item);
            if (target != null) {
                new PlayerStatsGUI(target, plugin).openGui(player);
            }
        }
    }
}
