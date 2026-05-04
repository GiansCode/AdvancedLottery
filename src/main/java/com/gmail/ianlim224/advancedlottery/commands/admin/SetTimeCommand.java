package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.time.TimeParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class SetTimeCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length < 2) return CommandResponse.INCORRECT_ARGS;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) sb.append(args[i]);

        TimeParser parser = new TimeParser(sb.toString(), plugin);
        if (!parser.isValid()) {
            sender.sendMessage(MM.deserialize("<red>Invalid time format!"));
            return CommandResponse.SUCCESS;
        }

        plugin.getLotteryTimer().setDuration(parser.getTime().toMilis());
        sender.sendMessage(MM.deserialize("<green>Lottery timer updated!"));
        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "settime"; }
    @Override public Permissions getPermission() { return Permissions.ADMIN; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
