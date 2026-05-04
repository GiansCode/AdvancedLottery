package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.object.Purchase;
import com.gmail.ianlim224.advancedlottery.sounds.CancelSound;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmGUI implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Map<UUID, Integer> counter = new HashMap<>();

    private final Inventory inv;
    private final ItemGrabber grabber;
    private final CancelSound sound;
    private final AdvancedLottery plugin;
    private final LotteryTicket ticket;

    public ConfirmGUI(AdvancedLottery plugin) {
        this.plugin = plugin;
        this.grabber = ItemGrabber.getInstance(plugin);
        this.sound   = new CancelSound();
        this.ticket  = LotteryTicket.getInstance(plugin);
        this.inv     = Bukkit.createInventory(new ConfirmHolder(), 54,
                MM.deserialize(AdvancedLottery.getLotteryGrabber().getConfirmMenuName()));
    }

    public static int getCounter(Player player) {
        Integer c = counter.get(player.getUniqueId());
        if (c == null) throw new NullPointerException("Player not in counter map.");
        return c;
    }

    public static void removePlayer(Player player) { counter.remove(player.getUniqueId()); }
    public static void addPlayerToCounter(Player player) { counter.put(player.getUniqueId(), 1); }

    private ItemStack getTicketAmountItem(int amount) {
        Material mat = Material.matchMaterial(MenuItems.TICKET_AMOUNT_MATERIAL.getRaw());
        if (mat == null) mat = Material.PAPER;
        return new ItemBuilder(mat)
                .displayName(MM.deserialize(MenuItems.TICKET_AMOUNT_NAME.getRaw(),
                        Placeholder.unparsed("ticket", Integer.toString(amount))))
                .lore(MenuItems.TICKET_AMOUNT_LORE.getRawList().stream()
                        .map(l -> MM.deserialize(l,
                                Placeholder.unparsed("ticket", Integer.toString(amount)),
                                Placeholder.unparsed("money", SpigotCommons.formatMoney(
                                        amount * plugin.getConfig().getDouble("buy_price")))))
                        .toList())
                .build();
    }

    public void openGui(Player player) {
        addPlayerToCounter(player);
        load();
        setTicketAmountItem(getCounter(player), inv);
        player.openInventory(inv);
    }

    public void load() {
        inv.setItem(4, grabber.getBuyNavigator());
        for (int i = 0; i < 3; i++) {
            inv.setItem(33 + i, grabber.getCancelBuy());
            inv.setItem(42 + i, grabber.getCancelBuy());
            inv.setItem(51 + i, grabber.getCancelBuy());
            inv.setItem(27 + i, grabber.getConfirmBuy());
            inv.setItem(36 + i, grabber.getConfirmBuy());
            inv.setItem(45 + i, grabber.getConfirmBuy());
        }
        inv.setItem(21, grabber.getAdd());
        inv.setItem(23, grabber.getMinus());
        setTicketAmountItem(1, inv);
    }

    private void setTicketAmountItem(int amount, Inventory target) {
        target.setItem(22, getTicketAmountItem(amount));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!(event.getClickedInventory() != null
                && event.getClickedInventory().getHolder() instanceof ConfirmHolder)) return;

        Player player = (Player) event.getWhoClicked();
        int count = getCounter(player);
        double buyPrice = plugin.getConfig().getDouble("buy_price");
        ItemStack clicked = event.getCurrentItem();

        if (clicked.isSimilar(grabber.getConfirmBuy())) {
            if (ticket.getMaxTicketsCanBeBought(player) < count) {
                Messages.TOO_MANY_TICKETS.send(player);
                sound.playSound(player, plugin);
                player.closeInventory();
                event.setCancelled(true);
                return;
            }
            if (plugin.getVaultEcon().getBalance(player) < buyPrice * count) {
                Messages.NOT_ENOUGH_MONEY.send(player);
                sound.playSound(player, plugin);
                player.closeInventory();
                event.setCancelled(true);
                return;
            }
            new Purchase(player, count, plugin).executePurchase(true, true);
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        if (clicked.isSimilar(grabber.getCancelBuy())) {
            event.setCancelled(true);
            sound.playSound(player, plugin);
            player.closeInventory();
            return;
        }

        if (clicked.isSimilar(grabber.getAdd())) {
            event.setCancelled(true);
            if (ticket.getMaxTicketsCanBeBought(player) >= count + 1) {
                setCounter(player, count + 1);
                setTicketAmountItem(getCounter(player), event.getClickedInventory());
            }
            return;
        }

        if (clicked.isSimilar(grabber.getMinus())) {
            event.setCancelled(true);
            if (count > 1) {
                setCounter(player, count - 1);
                setTicketAmountItem(getCounter(player), event.getClickedInventory());
            }
        }
    }

    public void setCounter(Player player, int value) {
        if (!counter.containsKey(player.getUniqueId()))
            throw new IllegalArgumentException("Player not in counter map.");
        counter.put(player.getUniqueId(), value);
        setTicketAmountItem(value, inv);
    }
}
