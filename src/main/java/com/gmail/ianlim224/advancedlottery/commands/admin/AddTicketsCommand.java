package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.Purchase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTicketsCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 3) return CommandResponse.INCORRECT_ARGS;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MM.deserialize("<red>Cannot find player <yellow><name></yellow>.",
                    Placeholder.unparsed("name", args[1])));
            return CommandResponse.SUCCESS;
        }

        int tickets;
        try {
            tickets = Integer.parseInt(args[2]);
            if (tickets <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(MM.deserialize(
                    "<red>Invalid ticket amount — must be a positive integer."));
            return CommandResponse.SUCCESS;
        }

        Messages.FREE_TICKETS_GIVEN.send(target,
                Placeholder.unparsed("ticket", Integer.toString(tickets)));
        new Purchase(target, tickets, plugin).executePurchase(true, false);
        sender.sendMessage(MM.deserialize("<green>Successfully gave tickets to <yellow><name></yellow>!",
                Placeholder.unparsed("name", target.getName())));
        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "addtickets"; }
    @Override public Permissions getPermission() { return Permissions.ADMIN; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
