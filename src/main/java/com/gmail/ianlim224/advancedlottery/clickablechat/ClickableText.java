package com.gmail.ianlim224.advancedlottery.clickablechat;

import com.gmail.ianlim224.advancedlottery.messages.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClickableText implements Clickable {

    @Override
    public void sendToPlayer(Player buyer, int tickets) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Component msg = Messages.BROADCAST_BUYER.asComponent(viewer,
                            Placeholder.unparsed("player", buyer.getName()),
                            Placeholder.unparsed("ticket", Integer.toString(tickets)))
                    .clickEvent(ClickEvent.runCommand("/lottery buy"))
                    .hoverEvent(HoverEvent.showText(Messages.HOVER_CHAT_MSG.asComponent(viewer)));
            viewer.sendMessage(msg);
        }
    }

    @Override
    public void sendMessageWithAction(Player sender, Component message, Component hoverText, Player target) {
        Component msg = message
                .clickEvent(ClickEvent.suggestCommand("/msg " + target.getName()))
                .hoverEvent(HoverEvent.showText(hoverText));
        sender.sendMessage(msg);
    }
}
