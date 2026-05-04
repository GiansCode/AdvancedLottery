package com.gmail.ianlim224.advancedlottery.commands.player;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.gui.ConfirmGUI;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.object.Purchase;
import com.gmail.ianlim224.advancedlottery.object.PurchaseCooldown;
import com.gmail.ianlim224.advancedlottery.object.TicketTransaction;
import com.gmail.ianlim224.advancedlottery.sounds.CancelSound;
import com.gmail.ianlim224.advancedlottery.text.TextConfirmer;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand implements Executable {

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length > 2) return CommandResponse.INCORRECT_ARGS;

        Player player = (Player) sender;
        LotteryTicket ticket = LotteryTicket.getInstance(plugin);
        CancelSound sound = new CancelSound();
        PurchaseCooldown cooldown = PurchaseCooldown.getInstance(plugin);

        if (!cooldown.getCooldown().isReady(player) && !player.hasPermission("advancedlottery.cooldown.bypass")) {
            Messages.BUY_TICKET_COOLDOWN.send(player,
                    Placeholder.unparsed("time",
                            String.valueOf(cooldown.getCooldown().getTimeLeft(player).getSeconds())));
            sound.playSound(player, plugin);
            return CommandResponse.SUCCESS;
        }

        cooldown.getCooldown().addCooldown(player);

        if (args.length == 1) {
            if (plugin.getVaultEcon().getBalance(player) < ticket.getTicketCost()) {
                Messages.NOT_ENOUGH_MONEY.send(player);
                sound.playSound(player, plugin);
                return CommandResponse.SUCCESS;
            }
            if (!ticket.isMaxTickets(player)) {
                queryConfirmation(1, player, false, plugin);
            } else {
                Messages.ALREADY_BOUGHT.send(player);
                sound.playSound(player, plugin);
            }
            return CommandResponse.SUCCESS;
        }

        if (args.length == 2) {
            if (!SpigotCommons.isInteger(args[1])) {
                if (!args[1].equalsIgnoreCase("confirm")) return CommandResponse.INCORRECT_ARGS;

                TextConfirmer confirmer = TextConfirmer.getInstance();
                if (!confirmer.hasPendingConfirmation(player)) {
                    Messages.NO_PENDING_CONFIRMATIONS.send(player);
                    return CommandResponse.SUCCESS;
                }
                TicketTransaction transaction = confirmer.completePendingConfirmation(player);
                new Purchase(player, transaction, plugin).executePurchase(true, true);
                return CommandResponse.SUCCESS;
            }

            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) return CommandResponse.INCORRECT_ARGS;

            if (plugin.getVaultEcon().getBalance(player) < ticket.getTicketCost() * amount) {
                Messages.NOT_ENOUGH_MONEY.send(player);
                sound.playSound(player, plugin);
                return CommandResponse.SUCCESS;
            }
            if (ticket.isMaxTickets(player)) {
                Messages.ALREADY_BOUGHT.send(player);
                sound.playSound(player, plugin);
            } else if (ticket.getMaxTicketsCanBeBought(player) < amount) {
                Messages.TOO_MANY_TICKETS.send(player);
                sound.playSound(player, plugin);
            } else {
                queryConfirmation(amount, player, true, plugin);
            }
        }

        return CommandResponse.SUCCESS;
    }

    private void queryConfirmation(int tickets, Player player, boolean hasAmount, AdvancedLottery plugin) {
        boolean useText;
        boolean openMenu;
        if (hasAmount) {
            useText = plugin.getConfig().getBoolean("use_text_confirmation_on_buy_amount");
            openMenu = plugin.getConfig().getBoolean("open_confirm_menu_on_buy_amount");
        } else {
            useText = plugin.getConfig().getBoolean("use_text_confirmation_on_buy");
            openMenu = plugin.getConfig().getBoolean("open_confirm_menu_on_buy");
        }

        TicketTransaction transaction = new TicketTransaction(tickets, plugin);
        if (useText) {
            TextConfirmer.getInstance().addPendingConfirmation(player, transaction);
            Messages.BUY_TEXT_CONFIRM.send(player,
                    Placeholder.unparsed("ticket", Integer.toString(tickets)),
                    Placeholder.unparsed("price", SpigotCommons.formatMoney(transaction.getTotalPrice())));
        } else if (openMenu) {
            ConfirmGUI gui = new ConfirmGUI(plugin);
            gui.openGui(player);
            gui.setCounter(player, tickets);
        } else {
            new Purchase(player, transaction, plugin).executePurchase(true, true);
        }
    }

    @Override public String getLabel()          { return "buy"; }
    @Override public Permissions getPermission() { return Permissions.DEFAULT; }
    @Override public boolean isCmdPlayerOnly()   { return true; }
}
