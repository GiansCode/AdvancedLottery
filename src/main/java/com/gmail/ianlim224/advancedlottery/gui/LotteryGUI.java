package com.gmail.ianlim224.advancedlottery.gui;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.ItemGrabber;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.legacy.SkullManager;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

class HeadLocation {
    private final int inventoryIndex;
    private final int slotIndex;

    HeadLocation(int inventoryIndex, int slotIndex) {
        this.inventoryIndex = inventoryIndex;
        this.slotIndex = slotIndex;
    }

    int getInventoryIndex() { return inventoryIndex; }
    int getSlotIndex()      { return slotIndex; }
}

public class LotteryGUI {
    private static final int[] PANE_SLOTS = { 45, 46, 47, 51, 52, 53 };
    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static LotteryGUI instance;

    private ArrayList<Inventory> menuInventory;
    private HashMap<Player, Integer> playerPage;
    private HashMap<UUID, HeadLocation> heads;
    private ItemGrabber grabber;

    private LotteryGUI() {}

    public static LotteryGUI getInstance() {
        if (instance == null) instance = new LotteryGUI();
        return instance;
    }

    public void load(AdvancedLottery plugin) {
        menuInventory = new ArrayList<>();
        playerPage    = new HashMap<>();
        heads         = new HashMap<>();
        grabber       = ItemGrabber.getInstance(plugin);
        addNewPage();
        loadMenu();
    }

    public void reset(AdvancedLottery plugin) {
        menuInventory = null;
        playerPage    = null;
        heads         = null;
        grabber       = null;
        load(plugin);
    }

    public Inventory createNewInventory() {
        Component title = MM.deserialize(
                AdvancedLottery.getLotteryGrabber().getLotteryMenuName(),
                Placeholder.unparsed("number", Integer.toString(menuInventory.size() + 1)));
        return Bukkit.createInventory(new LotteryHolder(), 54, title);
    }

    public void openFirstPage(Player player) {
        player.openInventory(menuInventory.get(0));
        playerPage.put(player, 1);
    }

    public void openNextPage(Player player) {
        int current = playerPage.getOrDefault(player, 1);
        if (menuInventory.size() - current > 0) {
            player.openInventory(menuInventory.get(current));
            playerPage.put(player, current + 1);
        }
    }

    public void openPreviousPage(Player player) {
        int current = playerPage.getOrDefault(player, 1);
        if (current - 1 >= 0) {
            player.openInventory(menuInventory.get(current - 1));
            playerPage.put(player, current - 1);
        }
    }

    public void addPlayer(OfflinePlayer player) {
        if (heads.containsKey(player.getUniqueId())) {
            HeadLocation loc = heads.get(player.getUniqueId());
            menuInventory.get(loc.getInventoryIndex())
                    .setItem(loc.getSlotIndex(), SkullManager.getSkull(player));
            return;
        }

        Inventory last = getLastPage();
        int slot = last.firstEmpty();
        if (slot == -1) {
            addNewPage();
            last = getLastPage();
            slot = last.firstEmpty();
        }
        last.setItem(slot, SkullManager.getSkull(player));
        heads.put(player.getUniqueId(), new HeadLocation(menuInventory.size() - 1, slot));
    }

    private Inventory getLastPage() {
        return menuInventory.get(menuInventory.size() - 1);
    }

    public List<Inventory> getMenuInventory() {
        return menuInventory;
    }

    public void addNewPage() {
        menuInventory.add(createNewInventory());
        loadMenu();
    }

    public void loadMenu() {
        Material paneMat = Material.matchMaterial(MenuItems.MENU_PANE_MATERIAL.getRaw());
        if (paneMat == null) paneMat = Material.GREEN_STAINED_GLASS_PANE;

        var pane = new ItemBuilder(paneMat).displayName(Component.empty()).build();

        for (Inventory inv : menuInventory) {
            for (int slot : PANE_SLOTS) {
                inv.setItem(slot, pane);
            }
            inv.setItem(48, grabber.getPreviousArrow());
            inv.setItem(49, grabber.getBuyButton());
            inv.setItem(50, grabber.getNextArrow());
        }
    }
}
