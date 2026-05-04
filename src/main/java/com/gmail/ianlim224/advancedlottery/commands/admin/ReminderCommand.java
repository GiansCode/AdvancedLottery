package com.gmail.ianlim224.advancedlottery.commands.admin;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.time.Reminder;
import com.gmail.ianlim224.advancedlottery.time.Time;
import com.gmail.ianlim224.advancedlottery.time.TimeParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class ReminderCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("list"))) {
            sender.sendMessage(MM.deserialize("<green>Reminders:"));
            for (Reminder reminder : plugin.getReminderManager().getReminders()) {
                sender.sendMessage(MM.deserialize("<gray>- <white>" + reminder));
            }
            return CommandResponse.SUCCESS;
        }

        if (args.length == 3) {
            String combined = args[0] + args[1] + args[2];
            TimeParser parser = new TimeParser(combined, plugin);

            if (!parser.isValid()) return CommandResponse.INCORRECT_ARGS;

            Time time = parser.getTime();

            if (args[1].equalsIgnoreCase("add")) {
                plugin.getReminderManager().addReminder(new Reminder(time, plugin));
                sender.sendMessage(MM.deserialize("<green>Lottery reminder added!"));
                return CommandResponse.SUCCESS;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                Reminder reminder = new Reminder(time, plugin);
                if (!plugin.getReminderManager().hasReminder(reminder)) {
                    sender.sendMessage(MM.deserialize(
                            "<red>That reminder does not exist. Use <yellow>/lottery reminder list</yellow> to view all reminders."));
                    return CommandResponse.SUCCESS;
                }
                plugin.getReminderManager().removeReminder(reminder);
                sender.sendMessage(MM.deserialize("<green>Lottery reminder removed!"));
                return CommandResponse.SUCCESS;
            }
        }

        return CommandResponse.INCORRECT_ARGS;
    }

    @Override public String getLabel()          { return "reminder"; }
    @Override public Permissions getPermission() { return Permissions.ADMIN; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
