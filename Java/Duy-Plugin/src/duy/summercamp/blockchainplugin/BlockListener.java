package duy.summercamp.blockchainplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Base64;
public class BlockListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getClickedBlock();

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (block.getType().equals(Material.WARPED_WALL_SIGN)) {
                player.sendMessage(ChatColor.GREEN + "...Decrypting Messages in this Block...");
                BlockState state = block.getState();
                Sign sign = (Sign) state;
                String signline1 = sign.getLine(1);
                player.sendMessage(ChatColor.YELLOW + signline1);

                String originalInput = "test input";
                String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
                System.out.println(encodedString);
                System.out.println(signline1);
                byte[] decodedBytes = Base64.getDecoder().decode(signline1.replace("\"", ""));
                String decodedString = new String(decodedBytes);
                player.sendMessage(ChatColor.GREEN + decodedString);

            } else {
                player.sendMessage(
                        ChatColor.RED + "You clicked: " + ChatColor.RED + block.getType().toString().toUpperCase());
            }
        }
//
    }
}
