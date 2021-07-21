package duy.summercamp.blockchainplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getClickedBlock();

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                player.sendMessage(ChatColor.RED + "You clicked: " + ChatColor.RED + block.getType().toString().toUpperCase());
        }
//
    }
}
