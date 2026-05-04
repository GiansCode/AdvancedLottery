package com.gmail.ianlim224.advancedlottery.commands.player;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements Executable {

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 1) return CommandResponse.INCORRECT_ARGS;

        Player p = sender instanceof Player pl ? pl : null;
        sender.sendMessage(Messages.TIME_TO_DRAW.asComponent(p,
                Placeholder.unparsed("time", plugin.getLotteryTimer().time(false)),
                Placeholder.unparsed("time_short", plugin.getLotteryTimer().time(true))));
        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "time"; }
    @Override public Permissions getPermission() { return Permissions.DEFAULT; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
