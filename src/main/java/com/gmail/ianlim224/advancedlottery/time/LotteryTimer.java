package com.gmail.ianlim224.advancedlottery.time;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.fireworks.Fireworks;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.mysql.LotterySql;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.sounds.WinSound;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class LotteryTimer {
    private final AdvancedLottery plugin;
    private final long duration;
    private long end;
    private BukkitTask task;

    public LotteryTimer(AdvancedLottery plugin) {
        this.plugin = plugin;
        this.duration = TimeUnit.MINUTES.toMillis(plugin.getConfig().getInt("count_down_time"));
    }

    public void start() {
        start(duration);
    }

    public void start(long millis) {
        if (task != null) task.cancel();

        this.end = System.currentTimeMillis() + millis;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                end();
            }
        }.runTaskLater(plugin, millis / 50);

        plugin.getReminderManager().reload();
    }

    public void end() {
        plugin.getFileLogging().debug("Ending lottery...");

        if (LotteryTicket.getInstance(plugin).isEmpty()) {
            plugin.getFileLogging().debug("No participants, no winner.");

            if (plugin.getConfig().getBoolean("allow_broadcast")) {
                Messages.BROADCAST_NO_WINNER.sendToAll(Bukkit.getOnlinePlayers());
            }
        } else {
            LotteryPot pot = LotteryPot.getInstance(plugin);
            double tax = Math.min(1.0, plugin.getConfig().getDouble("lottery_tax_in_percentage"));
            if (LotteryTicket.getInstance(plugin).getPlayers().size() == 1) tax = 0;

            double prize = pot.getMoneyInPot() * (1 - tax);
            OfflinePlayer winner = Bukkit.getOfflinePlayer(LotteryTicket.getInstance(plugin).selectWinner());

            processWinner(winner, prize);
            broadcastWin(winner, prize);
            plugin.getWinnerRegistry().setWinner(winner);
        }

        cleanUp();
        setDuration(duration);
    }

    private void processWinner(OfflinePlayer winner, double prize) {
        plugin.getFileLogging().debug(
                String.format("%s won the lottery with a prize of %.2f", winner.getName(), prize));
        plugin.getVaultEcon().payMoney(prize, winner);

        if (winner.isOnline()) {
            Player online = winner.getPlayer();
            if (plugin.getConfig().getBoolean("shoot_fireworks_on_win")) {
                new Fireworks().shootFireworks(online, plugin);
            }
            new WinSound().playSound(online, plugin);
        }

        LotterySql.getInstance(plugin).addWins(winner);
        LotterySql.getInstance(plugin).addMoneyWon(winner, prize);
    }

    private void broadcastWin(OfflinePlayer winner, double prize) {
        String winnerName = winner.getName() != null ? winner.getName() : "Unknown";
        String money = SpigotCommons.formatMoney(prize);

        Messages.LOTTERY_WIN.sendToAll(Bukkit.getOnlinePlayers(),
                Placeholder.unparsed("player", winnerName),
                Placeholder.unparsed("money", money));

        if (plugin.getConfig().getBoolean("play_out_title_when_win")) {
            LotteryGrabber grabber = AdvancedLottery.getLotteryGrabber();
            Title.Times times = Title.Times.times(
                    Duration.ofMillis((long) (grabber.getFadeInSeconds() * 1000)),
                    Duration.ofMillis((long) (grabber.getStaySeconds() * 1000)),
                    Duration.ofMillis((long) (grabber.getFadeOutSeconds() * 1000)));

            for (Player p : Bukkit.getOnlinePlayers()) {
                Title title = Title.title(
                        Messages.WINNER_TITLE_MESSAGE.asComponent(p,
                                Placeholder.unparsed("player", winnerName)),
                        Messages.WINNER_SUBTITLE_MESSAGE.asComponent(p,
                                Placeholder.unparsed("money", money)),
                        times);
                p.showTitle(title);
            }
        }
    }

    private void cleanUp() {
        LotteryTicket.getInstance(plugin).clear();
        LotteryGUI.getInstance().reset(plugin);
        LotteryPot.getInstance(plugin).clearMoneyInPot();
        plugin.getReminderManager().reload();
        LotterySql.getInstance(plugin).clearCache();
    }

    public long timeLeft() {
        return end - System.currentTimeMillis();
    }

    public String time(boolean isShort) {
        if (timeLeft() < 0) end();
        return new Time(timeLeft(), isShort, plugin).toString();
    }

    public long getDuration() {
        return end - System.currentTimeMillis();
    }

    public void setDuration(long millis) {
        start(millis);
    }
}
