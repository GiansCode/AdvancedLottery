package com.gmail.ianlim224.advancedlottery.sounds;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WinSound implements ISound {
    @Override
    public void playSound(Player player, AdvancedLottery plugin) {
        if (plugin.getConfig().getBoolean("sounds.player_win_sound")) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 0f);
        }
    }
}
