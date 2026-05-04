package com.gmail.ianlim224.advancedlottery.tasks;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.gui.HelpGUI;
import com.gmail.ianlim224.advancedlottery.gui.LotteryGUI;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.object.LotteryPot;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import com.gmail.ianlim224.advancedlottery.utils.SpigotCommons;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemTask implements Runnable {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final AdvancedLottery plugin;
    private final LotteryPot lotteryPot;
    private final LotteryGUI lotteryInventory;

    public ItemTask(AdvancedLottery plugin) {
        this.plugin           = plugin;
        this.lotteryPot       = LotteryPot.getInstance(AdvancedLottery.getInstance());
        this.lotteryInventory = LotteryGUI.getInstance();
    }

    @Override
    public void run() {
        String money     = SpigotCommons.formatMoney(lotteryPot.getMoneyInPot());
        String time      = plugin.getLotteryTimer().time(false);
        String timeShort = plugin.getLotteryTimer().time(true);

        ItemStack pot   = buildPot(money);
        ItemStack clock = buildClock(time, timeShort);

        for (var inv : lotteryInventory.getMenuInventory()) {
            inv.setItem(47, pot);
            inv.setItem(51, clock);
        }

        var helpInv = HelpGUI.getInstance(plugin).getInventory();
        helpInv.setItem(39, pot);
        helpInv.setItem(41, clock);
    }

    private ItemStack buildPot(String money) {
        Material mat = Material.matchMaterial(MenuItems.MONEY_POT_MATERIAL.getRaw());
        if (mat == null) mat = Material.BUCKET;
        return new ItemBuilder(mat)
                .displayName(MenuItems.MONEY_POT_NAME.asComponent())
                .lore(MenuItems.MONEY_POT_LORE.getRawList().stream()
                        .map(l -> MM.deserialize(l, Placeholder.unparsed("money", money)))
                        .toList())
                .build();
    }

    private ItemStack buildClock(String time, String timeShort) {
        Material mat = Material.matchMaterial(MenuItems.TIME_PREVIEW_MATERIAL.getRaw());
        if (mat == null) mat = Material.CLOCK;
        return new ItemBuilder(mat)
                .displayName(MenuItems.TIME_PREVIEW_NAME.asComponent())
                .lore(MenuItems.TIME_PREVIEW_LORE.getRawList().stream()
                        .map(l -> MM.deserialize(l,
                                Placeholder.unparsed("time", time),
                                Placeholder.unparsed("time_short", timeShort)))
                        .toList())
                .build();
    }
}
