package com.gmail.ianlim224.advancedlottery.object;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.clickablechat.ClickableText;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Purchase {
    private final OfflinePlayer player;
    private final TicketTransaction ticket;
    private final AdvancedLottery plugin;

    public Purchase(OfflinePlayer player, int ticketAmount, AdvancedLottery plugin) {
        this(player, new TicketTransaction(ticketAmount, plugin), plugin);
    }

    public Purchase(OfflinePlayer player, TicketTransaction ticket, AdvancedLottery plugin) {
        this.player = player;
        this.ticket = ticket;
        this.plugin = plugin;
    }

    public void executePurchase(boolean saveToDatabase, boolean deductMoney) {
        if (deductMoney) plugin.getVaultEcon().takeMoney(ticket.getTotalPrice(), player);
        registerTicket();
        if (plugin.getConfig().getBoolean("allow_broadcast")) broadcastPurchase();
        sendConfirmation();
        LotteryGUI.getInstance().addPlayer(player);
        if (saveToDatabase) LotterySql.getInstance(plugin).addMoney(player, ticket.getTotalPrice());
    }

    private void sendConfirmation() {
        if (!(player instanceof Player p)) return;
        Messages.BUY_SUCCESS.send(p,
                Placeholder.unparsed("balance", SpigotCommons.formatMoney(plugin.getVaultEcon().getBalance(player))),
                Placeholder.unparsed("money", SpigotCommons.formatMoney(ticket.getTotalPrice())),
                Placeholder.unparsed("time", plugin.getLotteryTimer().time(false)),
                Placeholder.unparsed("time_short", plugin.getLotteryTimer().time(true)),
                Placeholder.unparsed("ticket", Integer.toString(ticket.getAmount())));
    }

    private void registerTicket() {
        plugin.getFileLogging().debug(String.format("%s bought %d tickets for %.2f",
                player.getName(), ticket.getAmount(), ticket.getTotalPrice()));
        LotteryTicket.getInstance(plugin).addPlayer(player, ticket.getAmount());
        LotteryPot.getInstance(plugin).addMoneyInPot(ticket.getTotalPrice());
    }

    private void broadcastPurchase() {
        if (!player.isOnline()) return;
        new ClickableText().sendToPlayer((Player) player, ticket.getAmount());
    }
}
