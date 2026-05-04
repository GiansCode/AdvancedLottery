package com.gmail.ianlim224.advancedlottery.commands.player;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.commands.CommandResponse;
import com.gmail.ianlim224.advancedlottery.commands.Executable;
import com.gmail.ianlim224.advancedlottery.commands.Permissions;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.DataCollector;
import com.gmail.ianlim224.advancedlottery.mysql.DataPrinter;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements Executable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public CommandResponse onExecute(CommandSender sender, String[] args, AdvancedLottery plugin) {
        if (args.length > 2) return CommandResponse.INCORRECT_ARGS;

        LotterySql sql = LotterySql.getInstance(plugin);

        if (!sql.isEnabled()) {
            Player p = sender instanceof Player pl ? pl : null;
            sender.sendMessage(Messages.MYSQL_NOT_SUPPORTED.asComponent(p));
            return CommandResponse.SUCCESS;
        }

        if (args.length == 2) {
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                Player p = sender instanceof Player pl ? pl : null;
                sender.sendMessage(Messages.PLAYER_NOT_FOUND.asComponent(p));
                return CommandResponse.SUCCESS;
            }

            Player p = sender instanceof Player pl ? pl : null;
            sender.sendMessage(Messages.RETRIEVING_DATA.asComponent(p,
                    Placeholder.unparsed("player", target.getName() != null ? target.getName() : args[1])));
            new DataCollector(sender, target.getUniqueId(), sql, new DataPrinter(), plugin)
                    .runTaskAsynchronously(plugin);
            return CommandResponse.SUCCESS;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MM.deserialize("<red>Cannot look up stats for console."));
                return CommandResponse.SUCCESS;
            }

            sender.sendMessage(Messages.RETRIEVING_DATA.asComponent(player,
                    Placeholder.unparsed("player", player.getName())));
            new DataCollector(player, player.getUniqueId(), sql, new DataPrinter(), plugin)
                    .runTaskAsynchronously(plugin);
        }

        return CommandResponse.SUCCESS;
    }

    @Override public String getLabel()          { return "stats"; }
    @Override public Permissions getPermission() { return Permissions.DEFAULT; }
    @Override public boolean isCmdPlayerOnly()   { return false; }
}
