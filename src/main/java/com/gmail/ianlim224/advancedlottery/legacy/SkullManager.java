package com.gmail.ianlim224.advancedlottery.legacy;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import com.gmail.ianlim224.advancedlottery.items.MenuItems;
import com.gmail.ianlim224.advancedlottery.object.LotteryTicket;
import com.gmail.ianlim224.advancedlottery.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public final class SkullManager {

    private SkullManager() {}

    public static ItemStack getSkull(OfflinePlayer player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        Objects.requireNonNull(meta);
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));

        int tickets = LotteryTicket.getInstance(AdvancedLottery.getInstance())
                .getTicketsBought(player.getUniqueId());
        String playerName = Objects.requireNonNullElse(player.getName(), "Unknown");

        meta.displayName(MiniMessage.miniMessage().deserialize(
                MenuItems.PLAYER_HEAD_NAME.getRaw(),
                Placeholder.unparsed("player", playerName)));
        meta.lore(MenuItems.PLAYER_HEAD_LORE.getRawList().stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line,
                        Placeholder.unparsed("tickets", Integer.toString(tickets)),
                        Placeholder.unparsed("player", playerName)))
                .toList());

        skull.setItemMeta(meta);
        return skull;
    }

    public static OfflinePlayer getPlayer(ItemStack skull) {
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        return meta.getOwningPlayer();
    }
}
