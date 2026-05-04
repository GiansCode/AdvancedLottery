package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.clickablechat.ClickableText;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.legacy.SkullManager;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerStatsGUI implements Listener {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final AdvancedLottery plugin;
    private final Inventory inv;
    private final OfflinePlayer player;

    public PlayerStatsGUI(OfflinePlayer player, AdvancedLottery plugin) {
        this.plugin  = plugin;
        this.player  = player;
        this.inv     = Bukkit.createInventory(new StatsHolder(), 54,
                MM.deserialize(AdvancedLottery.getLotteryGrabber().getPlayerMenuName()));
        Bukkit.getPluginManager().registerEvents(this, plugin);

        var pane = new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE)
                .displayName(net.kyori.adventure.text.Component.empty()).build();
        for (int i = 0; i < 54; i++) inv.setItem(i, pane);
    }

    public void openGui(Player viewer) {
        String playerName = player.getName() != null ? player.getName() : "Unknown";
        int tickets = LotteryTicket.getInstance(AdvancedLottery.getInstance())
                .getTicketsBought(player.getUniqueId());

        ItemStack head = new ItemBuilder(SkullManager.getSkull(player))
                .displayName(MM.deserialize(MenuItems.PLAYER_HEAD_NAME.getRaw(),
                        Placeholder.unparsed("player", playerName)))
                .lore(List.of())
                .build();
        inv.setItem(13, head);

        Material msgMat = Material.matchMaterial(MenuItems.SEND_MESSAGE_MATERIAL.getRaw());
        if (msgMat == null) msgMat = Material.PAPER;
        ItemStack message = new ItemBuilder(msgMat)
                .displayName(MenuItems.SEND_MESSAGE_NAME.asComponent())
                .lore(List.of(MM.deserialize(MenuItems.SEND_MESSAGE_LORE.getRaw(),
                        Placeholder.unparsed("player", playerName))))
                .build();

        Material ticketMat = Material.matchMaterial(MenuItems.TICKETS_BOUGHT_MATERIAL.getRaw());
        if (ticketMat == null) ticketMat = Material.EMERALD;
        ItemStack ticketItem = new ItemBuilder(ticketMat)
                .displayName(MenuItems.TICKETS_BOUGHT_NAME.asComponent())
                .lore(List.of(MM.deserialize(MenuItems.TICKETS_BOUGHT_LORE.getRaw(),
                        Placeholder.unparsed("player", playerName),
                        Placeholder.unparsed("ticket", Integer.toString(tickets)))))
                .build();

        inv.setItem(39, message);
        inv.setItem(41, ticketItem);

        viewer.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!(event.getInventory().getHolder() instanceof StatsHolder)) return;

        event.setCancelled(true);

        if (!player.isOnline()) {
            Messages.PLAYER_NOT_ONLINE.send((Player) event.getWhoClicked(),
                    Placeholder.unparsed("player", player.getName() != null ? player.getName() : "Unknown"));
            event.getWhoClicked().closeInventory();
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked.getType() == Material.PAPER) {
            Player sender = (Player) event.getWhoClicked();
            event.getWhoClicked().closeInventory();
            new ClickableText().sendMessageWithAction(
                    sender,
                    Messages.CLICK_ME_TEXT.asComponent(sender),
                    Messages.CLICK_ME_HOVER_TEXT.asComponent(sender,
                            Placeholder.unparsed("player", player.getName() != null ? player.getName() : "Unknown")),
                    player.getPlayer());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inv)) {
            HandlerList.unregisterAll(this);
        }
    }
}
