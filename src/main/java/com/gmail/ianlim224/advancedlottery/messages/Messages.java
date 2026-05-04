package com.gmail.ianlim224.advancedlottery.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.function.Function;

public enum Messages {
    LOTTERY_WIN("lottery_win",
            "<light_purple>Congrats to <yellow><player></yellow> <light_purple>for winning <red>$<money>"),
    BUY_SUCCESS("buy_success",
            "<gold>Congrats, you have successfully bought <ticket> ticket(s) for <green>$<money></green>, you now have <green>$<balance>"),
    NOT_ENOUGH_MONEY("not_enough_money",
            "<dark_red>You do not have enough money!"),
    ALREADY_BOUGHT("already_bought",
            "<dark_red>You have already bought the maximum number of tickets"),
    WRONG_USAGE("wrong_usage",
            "<red>Wrong usage: do <yellow>/lottery help</yellow> for help!"),
    TIME_TO_DRAW("time_to_draw",
            "<aqua>Time to draw is <yellow><time>"),
    BROADCAST_NO_WINNER("broadcast_no_winner",
            "<dark_green>There were no winners!"),
    BROADCAST_BUYER("broadcast_buyer",
            "<light_purple><player> has just bought <ticket> lottery ticket(s)!"),
    WINNER_TITLE_MESSAGE("winner_title_message",
            "<aqua><bold><player></bold> <yellow>won the lottery!"),
    WINNER_SUBTITLE_MESSAGE("winner_subtitle_message",
            "<light_purple>With an amount of <gold>$<money>"),
    NOT_ENOUGH_PERMISSIONS("not_enough_permissions",
            "<dark_red>You have insufficient permissions!"),
    LOTTERY_REMINDER("lottery_reminder",
            "<light_purple>Time left to draw is less than <time>!"),
    LOTTERY_COUNTDOWN("lottery_countdown",
            "<light_purple>The lottery will be drawn in <yellow><time></yellow> <light_purple>seconds"),
    HOVER_CHAT_MSG("hover_chat_msg",
            "<aqua>Click me to buy a ticket"),
    CLICK_ME_TEXT("click_me_text",
            "<aqua><bold>[Click Me]"),
    CLICK_ME_HOVER_TEXT("click_me_hover_text",
            "<gray>Click me to send a message to <player>"),
    TOO_MANY_TICKETS("too_many_tickets",
            "<red><bold>OOPS!</bold> <gray>You cannot buy that many tickets"),
    MYSQL_NOT_SUPPORTED("mysql_not_supported",
            "<dark_red>MySQL is not supported!"),
    RETRIEVING_DATA("retrieving_data",
            "<green>Retrieving data for player <player>..."),
    PLAYER_NOT_FOUND("player_not_found",
            "<red>That player cannot be found"),
    TICKET_MONEY_REFUNDED("ticket_money_refunded",
            "<green>Due to server closing, players who bought tickets have been refunded!"),
    BUY_TICKET_COOLDOWN("buy_ticket_cooldown",
            "<red>[Cooldown] <gray>Please wait another <yellow><time></yellow> <gray>seconds!"),
    PLAYER_NOT_ONLINE("player_not_online",
            "<red>Can't send message. <yellow><player></yellow> <red>is not online."),
    FREE_TICKETS_GIVEN("free_tickets_given",
            "<green>Christmas came earlier this year! Here are <ticket> ticket(s) from your beloved admins :D"),
    BUY_TEXT_CONFIRM("buy_text_confirm",
            "<aqua>You are attempting to purchase <ticket> ticket(s) for a total cost of <price>. Please type <yellow>/lottery buy confirm</yellow> to continue."),
    NO_PENDING_CONFIRMATIONS("no_pending_confirmation",
            "<red>You do not have any pending confirmations!");

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final String path;
    private final String defaultValue;

    private static FileConfiguration fc;
    private static Function<String, String> placeholderFunction = s -> s;

    Messages(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public static void setFc(FileConfiguration newFc) {
        fc = newFc;
    }

    public static void setPlaceholderFunction(Function<String, String> fn) {
        placeholderFunction = fn;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Component asComponent(Player player, TagResolver... resolvers) {
        String raw = fc != null ? fc.getString(path, defaultValue) : defaultValue;
        if (player != null) {
            raw = placeholderFunction.apply(raw);
        }
        return resolvers.length == 0 ? MM.deserialize(raw) : MM.deserialize(raw, resolvers);
    }

    public void send(Player player, TagResolver... resolvers) {
        player.sendMessage(asComponent(player, resolvers));
    }

    public void sendToAll(Iterable<? extends Player> players, TagResolver... resolvers) {
        for (Player p : players) {
            p.sendMessage(asComponent(p, resolvers));
        }
    }
}
