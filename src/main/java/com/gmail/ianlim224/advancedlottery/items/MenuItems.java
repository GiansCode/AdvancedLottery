package com.gmail.ianlim224.advancedlottery.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public enum MenuItems {
    // time preview
    TIME_PREVIEW_NAME("time_preview.name", "<light_purple><bold>TIME TO DRAW"),
    TIME_PREVIEW_LORE("time_preview.lore", "<gray>Time left until next draw is <dark_green><time>"),
    TIME_PREVIEW_MATERIAL("time_preview.material", "CLOCK"),

    // money pot
    MONEY_POT_NAME("money_pot.name", "<yellow><bold>MONEY IN POT"),
    MONEY_POT_LORE("money_pot.lore", "<gray>There are currently <aqua>$<money></aqua> in the pot"),
    MONEY_POT_MATERIAL("money_pot.material", "BUCKET"),

    // help menu
    INSTRUCTIONS_NAME("help_menu.instructions_name", "<yellow><bold>Instructions"),
    INSTRUCTIONS_LORE("help_menu.instructions_lore", "<gray>This page shows instructions for lottery"),
    INSTRUCTIONS_MATERIAL("help_menu.instructions_material", "WRITABLE_BOOK"),
    HELP_TUTORIAL_NAME("help_menu.help_tutorial_name", "<yellow><bold>Prize"),
    HELP_TUTORIAL_LORE("help_menu.help_tutorial_lore",
            "<gray>Only one player will be selected as the winner",
            "<gray>That player would win the total amount of money in pot"),
    HELP_TUTORIAL_MATERIAL("help_menu.help_tutorial_material", "EMERALD"),
    COMMAND_INTRO_NAME("help_menu.command_intro_name", "<dark_blue><bold>COMMANDS"),
    COMMAND_INTRO_LORE("help_menu.command_intro_lore",
            "<green>/lottery <dark_green>- main command",
            "<green>/lottery help <dark_green>- shows help menu",
            "<green>/lottery menu <dark_green>- opens the menu gui",
            "<green>/lottery time <dark_green>- shows countdown time left to draw",
            "<green>/lottery buy <dark_green>- buys a ticket",
            "<green>/lottery stats [player] <dark_green>- shows stats"),
    COMMAND_INTRO_MATERIAL("help_menu.command_intro_material", "BOOK"),
    HELP_BACKGROUND_MATERIAL("help_menu.background_material", "YELLOW_STAINED_GLASS_PANE"),

    // lottery menu
    NEXT_PAGE_NAME("lottery_menu.next_page_name", "<light_purple><bold>Next Page"),
    NEXT_PAGE_LORE("lottery_menu.next_page_lore", "<gray>View the next page of listings"),
    NEXT_PAGE_MATERIAL("lottery_menu.next_page_material", "ARROW"),
    PREVIOUS_PAGE_NAME("lottery_menu.previous_page_name", "<light_purple><bold>Previous Page"),
    PREVIOUS_PAGE_LORE("lottery_menu.previous_page_lore", "<gray>View the previous page of listings"),
    PREVIOUS_PAGE_MATERIAL("lottery_menu.previous_page_material", "ARROW"),
    BUY_TICKET_NAME("lottery_menu.buy_ticket_name", "<aqua><bold>BUY A TICKET"),
    BUY_TICKET_LORE("lottery_menu.buy_ticket_lore", "<gray>Click me to buy a ticket"),
    BUY_TICKET_MATERIAL("lottery_menu.buy_ticket_material", "COMPASS"),
    MENU_PANE_MATERIAL("lottery_menu.menu_pane_material", "GREEN_STAINED_GLASS_PANE"),
    PLAYER_HEAD_NAME("lottery_menu.player_head_name", "<green><bold><player>"),
    PLAYER_HEAD_LORE("lottery_menu.player_head_lore",
            "<gray>Tickets bought: <tickets>",
            "<gray>Click for more info"),

    // confirm menu
    CONFIRM_INFO_NAME("confirm_menu.confirm_info_name", "<gold><bold>CONFIRM PURCHASE"),
    CONFIRM_INFO_LORE("confirm_menu.confirm_info_lore", "<gray>Click to confirm purchase of one ticket"),
    CONFIRM_INFO_MATERIAL("confirm_menu.confirm_info_material", "BOOK"),

    CONFIRM_BLOCK_NAME("confirm_menu.confirm_block_name", "<green><bold>CONFIRM"),
    CONFIRM_BLOCK_MATERIAL("confirm_menu.confirm_block_material", "EMERALD_BLOCK"),

    CANCEL_BLOCK_NAME("confirm_menu.cancel_block_name", "<red><bold>CANCEL"),
    CANCEL_BLOCK_MATERIAL("confirm_menu.cancel_block_material", "REDSTONE_BLOCK"),

    TICKET_AMOUNT_NAME("confirm_menu.ticket_amount_name", "<yellow><bold><ticket></bold> <aqua>ticket(s) to be bought"),
    TICKET_AMOUNT_LORE("confirm_menu.ticket_amount_lore", "<gray>Buy <yellow><ticket></yellow> ticket(s) for <yellow>$<money>"),
    TICKET_AMOUNT_MATERIAL("confirm_menu.ticket_amount_material", "PAPER"),

    ADD_TICKET_NAME("confirm_menu.add_ticket_name", "<green><bold>Add"),
    ADD_TICKET_LORE("confirm_menu.add_ticket_lore", "<gray>Add a ticket to be purchased"),
    ADD_TICKET_MATERIAL("confirm_menu.add_ticket_material", "ARROW"),

    MINUS_TICKET_NAME("confirm_menu.minus_ticket_name", "<red><bold>Minus"),
    MINUS_TICKET_LORE("confirm_menu.minus_ticket_lore", "<gray>Remove a ticket from the purchase"),
    MINUS_TICKET_MATERIAL("confirm_menu.minus_ticket_material", "ARROW"),

    // player statistics
    SEND_MESSAGE_NAME("player_stats_menu.send_message_name", "<gold><bold>Send a message"),
    SEND_MESSAGE_MATERIAL("player_stats_menu.send_message_material", "PAPER"),
    SEND_MESSAGE_LORE("player_stats_menu.send_message_lore", "<gray>Send a message to <yellow><player>"),

    TICKETS_BOUGHT_NAME("player_stats_menu.tickets_bought_name", "<dark_blue><bold>Tickets Bought"),
    TICKETS_BOUGHT_MATERIAL("player_stats_menu.tickets_bought_material", "EMERALD"),
    TICKETS_BOUGHT_LORE("player_stats_menu.tickets_bought_lore", "<gray><player> has bought <ticket> ticket(s)");

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final EnumMap<MenuItems, List<String>> configValues = new EnumMap<>(MenuItems.class);
    private static FileConfiguration fc;

    private final String path;
    private final List<String> defaults;

    MenuItems(String path, String... defaults) {
        this.path = path;
        this.defaults = Arrays.asList(defaults);
    }

    public static void setFc(FileConfiguration newFc) {
        fc = newFc;
    }

    public static FileConfiguration getFc() {
        return fc;
    }

    public static void loadConfigValues() {
        for (MenuItems item : values()) {
            List<String> vals = fc.getStringList(item.path);
            if (vals.isEmpty()) {
                String single = fc.getString(item.path);
                if (single != null) {
                    vals = List.of(single);
                } else {
                    vals = item.defaults;
                }
            }
            configValues.put(item, vals);
        }
    }

    public String getPath() {
        return path;
    }

    public List<String> getDefaultListValue() {
        return defaults;
    }

    public String getDefaultStringValue() {
        return defaults.get(0);
    }

    /** Returns the raw MiniMessage string from config (first line). */
    public String getRaw() {
        List<String> vals = configValues.get(this);
        return vals != null && !vals.isEmpty() ? vals.get(0) : defaults.get(0);
    }

    /** Returns all raw MiniMessage strings (for lore). */
    public List<String> getRawList() {
        List<String> vals = configValues.get(this);
        return vals != null ? vals : defaults;
    }

    /** Deserializes to an Adventure Component (for display names). */
    public Component asComponent() {
        return MM.deserialize(getRaw());
    }

    /** Deserializes all lines to a list of Components (for lore). */
    public List<Component> asComponentList() {
        return getRawList().stream().map(MM::deserialize).toList();
    }

    public boolean isLore() {
        return defaults.size() > 1 || path.endsWith(".lore") || path.endsWith("_lore")
                || path.endsWith("background_material");
    }
}
