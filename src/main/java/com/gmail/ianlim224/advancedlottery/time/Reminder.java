package com.gmail.ianlim224.advancedlottery.time;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.messages.Messages;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Reminder implements Comparable<Reminder> {
    public static final String NOT_ANNOUNCED = "<red><bold>[NOT ANNOUNCED]";
    public static final String HAS_ANNOUNCED  = "<green><bold>[HAS ANNOUNCED]";

    private final Time time;
    private final AdvancedLottery plugin;
    private BukkitTask task;

    public Reminder(Time time, AdvancedLottery plugin) {
        this.time = time;
        this.plugin = plugin;

        if (plugin.getLotteryTimer().getDuration() - time.toMilis() > 0) {
            schedule();
        }
    }

    public boolean hasAnnounced() {
        return task == null;
    }

    public void cancel() {
        if (task != null) task.cancel();
        task = null;
    }

    public void schedule() {
        if (!hasAnnounced()) throw new IllegalStateException("Previous reminder task has not finished.");

        long delay = new Time(plugin.getLotteryTimer().getDuration() - time.toMilis(), false, plugin).toTicks();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                String money = SpigotCommons.formatMoney(LotteryPot.getInstance(plugin).getMoneyInPot());
                Messages.LOTTERY_REMINDER.sendToAll(Bukkit.getOnlinePlayers(),
                        Placeholder.unparsed("time", time.toString()),
                        Placeholder.unparsed("money", money));
                Reminder.this.cancel();
            }
        }.runTaskLater(plugin, delay);
    }

    public Time getTime()   { return time; }
    public long getMilis()  { return time.toMilis(); }

    @Override
    public String toString() {
        return time + " " + (hasAnnounced() ? HAS_ANNOUNCED : NOT_ANNOUNCED);
    }

    @Override
    public int compareTo(Reminder other) {
        return Long.compare(time.toMilis(), other.time.toMilis());
    }
}
