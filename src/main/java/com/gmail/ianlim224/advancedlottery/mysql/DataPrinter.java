package com.gmail.ianlim224.advancedlottery.mysql;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class DataPrinter extends DataHandler {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    protected void onDataReceive(CommandSender target, PlayerData data, UUID uuid, AdvancedLottery plugin) {
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        if (playerName == null) playerName = uuid.toString();

        for (String line : plugin.getConfig().getStringList("mysql.stats_message")) {
            target.sendMessage(MM.deserialize(line,
                    Placeholder.unparsed("player",     playerName),
                    Placeholder.unparsed("wins",       Integer.toString(data.getWins())),
                    Placeholder.unparsed("tickets",    Integer.toString(data.getTickets())),
                    Placeholder.unparsed("money",      SpigotCommons.formatMoney(data.getMoney())),
                    Placeholder.unparsed("money_won",  SpigotCommons.formatMoney(data.getMoneyWon()))));
        }
    }
}
