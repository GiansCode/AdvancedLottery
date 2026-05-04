package com.gmail.ianlim224.advancedlottery.commands.player;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.gui.HelpGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length != 1) return CommandResponse.INCORRECT_ARGS;

        if (!(sender instanceof Player)) {
            sender.sendMessage(MM.deserialize("""
                    <green>AdvancedLottery Commands:
                    <gray>Default:
                    <yellow>/lottery</yellow> <gray>- main command
                    <yellow>/lottery menu</yellow> <gray>- opens the lottery GUI
                    <yellow>/lottery help</yellow> <gray>- shows this help menu
                    <yellow>/lottery time</yellow> <gray>- shows time left until draw
                    <yellow>/lottery buy</yellow> <gray>- buy a ticket
                    <yellow>/lottery stats [player]</yellow> <gray>- show statistics
                    <gray>Admin:
                    <yellow>/lottery reload</yellow> <gray>- reload configuration
                    <yellow>/lottery end</yellow> <gray>- end the lottery immediately
                    <yellow>/lottery settime <time></yellow> <gray>- set lottery countdown
                    <yellow>/lottery reminder list</yellow> <gray>- list all reminders
                    <yellow>/lottery reminder add <time></yellow> <gray>- add a reminder
                    <yellow>/lottery reminder remove <time></yellow> <gray>- remove a reminder
                    <yellow>/lottery addtickets <player> <amount></yellow> <gray>- give free tickets"""));
            return CommandResponse.SUCCESS;
        }

        HelpGUI.getInstance(plugin).show((Player) sender);
        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "help"; }
    @Override public Permissions getPermission() { return Permissions.DEFAULT; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
