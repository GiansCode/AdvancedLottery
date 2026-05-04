package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HelpGUI {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static HelpGUI instance;
    private final AdvancedLottery plugin;
    private Inventory inventory;

    private HelpGUI(AdvancedLottery plugin) {
        this.plugin = plugin;
        reload();
    }

    public static HelpGUI getInstance(AdvancedLottery plugin) {
        if (instance == null) instance = new HelpGUI(plugin);
        return instance;
    }

    public void show(Player player) {
        player.openInventory(inventory);
    }

    public void reload() {
        inventory = Bukkit.createInventory(new HelpHolder(), 54,
                MM.deserialize(AdvancedLottery.getLotteryGrabber().getHelpMenuName()));

        Material bgMat = Material.matchMaterial(MenuItems.HELP_BACKGROUND_MATERIAL.getRaw());
        if (bgMat == null) bgMat = Material.YELLOW_STAINED_GLASS_PANE;
        var bg = new ItemBuilder(bgMat).displayName(Component.empty()).build();

        for (int i = 0; i < 54; i++) inventory.setItem(i, bg);

        inventory.setItem(4,  ItemGrabber.getInstance(plugin).getBookInstructions());
        inventory.setItem(31, ItemGrabber.getInstance(plugin).getWinNavigator());
        inventory.setItem(49, ItemGrabber.getInstance(plugin).getCommandHelp());
    }

    public Inventory getInventory() { return inventory; }
}
