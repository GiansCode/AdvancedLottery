package com.gmail.ianlim224.advancedlottery;

import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemGrabber {

    private ItemStack previousArrow;
    private ItemStack nextArrow;
    private ItemStack bookInstructions;
    private ItemStack winNavigator;
    private ItemStack confirmBuy;
    private ItemStack cancelBuy;
    private ItemStack buyNavigator;
    private ItemStack buyButton;
    private ItemStack commandHelp;
    private ItemStack add;
    private ItemStack minus;

    private static ItemGrabber instance;

    public static ItemGrabber getInstance(AdvancedLottery plugin) {
        if (instance == null) instance = new ItemGrabber(plugin);
        return instance;
    }

    public static ItemGrabber reload(AdvancedLottery plugin) {
        instance = new ItemGrabber(plugin);
        return instance;
    }

    private ItemGrabber(AdvancedLottery plugin) {
        try {
            previousArrow    = build(MenuItems.PREVIOUS_PAGE_MATERIAL,  MenuItems.PREVIOUS_PAGE_NAME,  MenuItems.PREVIOUS_PAGE_LORE);
            nextArrow        = build(MenuItems.NEXT_PAGE_MATERIAL,       MenuItems.NEXT_PAGE_NAME,      MenuItems.NEXT_PAGE_LORE);
            bookInstructions = build(MenuItems.INSTRUCTIONS_MATERIAL,    MenuItems.INSTRUCTIONS_NAME,   MenuItems.INSTRUCTIONS_LORE);
            winNavigator     = build(MenuItems.HELP_TUTORIAL_MATERIAL,   MenuItems.HELP_TUTORIAL_NAME,  MenuItems.HELP_TUTORIAL_LORE);
            confirmBuy       = build(MenuItems.CONFIRM_BLOCK_MATERIAL,   MenuItems.CONFIRM_BLOCK_NAME);
            cancelBuy        = build(MenuItems.CANCEL_BLOCK_MATERIAL,    MenuItems.CANCEL_BLOCK_NAME);
            buyNavigator     = build(MenuItems.CONFIRM_INFO_MATERIAL,    MenuItems.CONFIRM_INFO_NAME,   MenuItems.CONFIRM_INFO_LORE);
            buyButton        = build(MenuItems.BUY_TICKET_MATERIAL,      MenuItems.BUY_TICKET_NAME,     MenuItems.BUY_TICKET_LORE);
            commandHelp      = build(MenuItems.COMMAND_INTRO_MATERIAL,   MenuItems.COMMAND_INTRO_NAME,  MenuItems.COMMAND_INTRO_LORE);
            add              = build(MenuItems.ADD_TICKET_MATERIAL,      MenuItems.ADD_TICKET_NAME,     MenuItems.ADD_TICKET_LORE);
            minus            = build(MenuItems.MINUS_TICKET_MATERIAL,    MenuItems.MINUS_TICKET_NAME,   MenuItems.MINUS_TICKET_LORE);
        } catch (Exception e) {
            plugin.getLogger().warning("Error loading items.yml — reverting to defaults. Check material names.");
            for (MenuItems i : MenuItems.values()) {
                MenuItems.getFc().set(i.getPath(), i.getDefaultListValue());
            }
            plugin.getItemsManager().saveConfig();
        }
    }

    private static ItemStack build(MenuItems materialKey, MenuItems nameKey, MenuItems loreKey) {
        Material mat = resolveMaterial(materialKey.getRaw());
        return new ItemBuilder(mat)
                .displayName(nameKey.asComponent())
                .lore(loreKey.asComponentList())
                .build();
    }

    private static ItemStack build(MenuItems materialKey, MenuItems nameKey) {
        Material mat = resolveMaterial(materialKey.getRaw());
        return new ItemBuilder(mat)
                .displayName(nameKey.asComponent())
                .build();
    }

    private static Material resolveMaterial(String name) {
        Material mat = Material.matchMaterial(name);
        return mat != null ? mat : Material.STONE;
    }

    public ItemStack getPreviousArrow()   { return previousArrow; }
    public ItemStack getNextArrow()       { return nextArrow; }
    public ItemStack getBookInstructions(){ return bookInstructions; }
    public ItemStack getWinNavigator()    { return winNavigator; }
    public ItemStack getConfirmBuy()      { return confirmBuy; }
    public ItemStack getCancelBuy()       { return cancelBuy; }
    public ItemStack getBuyNavigator()    { return buyNavigator; }
    public ItemStack getBuyButton()       { return buyButton; }
    public ItemStack getCommandHelp()     { return commandHelp; }
    public ItemStack getAdd()             { return add; }
    public ItemStack getMinus()           { return minus; }
}
