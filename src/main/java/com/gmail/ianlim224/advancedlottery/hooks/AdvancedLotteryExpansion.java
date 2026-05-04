package com.gmail.ianlim224.advancedlottery.hooks;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class AdvancedLotteryExpansion extends PlaceholderExpansion {
    private final AdvancedLottery plugin;

    public AdvancedLotteryExpansion(AdvancedLottery plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("money")) {
            return SpigotCommons.formatMoney(LotteryPot.getInstance(plugin).getMoneyInPot());
        }

        if (identifier.equals("winner")) {
            if (plugin.getWinnerRegistry().getWinner() == null) {
                return "None";
            } else {
                return plugin.getWinnerRegistry().getWinner().getName();
            }
        }

        if (identifier.equals("time")) {
            return plugin.getLotteryTimer().time(false);
        }

        if (identifier.equals("time_short")) {
            return plugin.getLotteryTimer().time(true);
        }

        if (identifier.equals("tickets")) {
            if (player == null)
                return "0";

            return LotteryTicket.getInstance(plugin).getTicketsBought(player.getUniqueId()) + "";
        }

        if (identifier.equals("stats.total.tickets")) {
            LotterySql sql = LotterySql.getInstance(plugin);
            if (player == null || !sql.isEnabled())
                return "0";

            return "" + sql.getStats(player.getUniqueId()).getTickets();
        }

        return null;
    }

    @Override
    public String getIdentifier() {
        return "AdvancedLottery";
    }

    @Override
    public String getAuthor() {
        return "Ean244";
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}
