package com.gmail.ianlim224.advancedlottery.clickablechat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Clickable {
    void sendToPlayer(Player buyer, int tickets);

    void sendMessageWithAction(Player sender, Component message, Component hoverText, Player target);
}
