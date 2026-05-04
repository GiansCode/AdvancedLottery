package com.gmail.ianlim224.advancedlottery.sounds;

import com.gmail.ianlim224.advancedlottery.AdvancedLottery;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CancelSound implements ISound {
    @Override
    public void playSound(Player player, AdvancedLottery plugin) {
        if (plugin.getConfig().getBoolean("sounds.anvil_cancel_sound")) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 0f);
        }
    }
}
