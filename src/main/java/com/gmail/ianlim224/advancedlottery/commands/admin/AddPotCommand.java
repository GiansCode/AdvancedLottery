package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class AddPotCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 2) return CommandResponse.INCORRECT_ARGS;

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            return CommandResponse.INCORRECT_ARGS;
        }

        LotteryPot.getInstance(plugin).addMoneyInPot(amount);
        sender.sendMessage(MM.deserialize(
                "<green>Added <yellow>$<amount></yellow> to the lottery pot!",
                Placeholder.unparsed("amount", SpigotCommons.formatMoney(amount))));
        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "addpot"; }
    @Override public Permissions getPermission() { return Permissions.ADMIN; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
