package duy.summercamp.blockchainplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class BlockListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getClickedBlock();

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (block.getType().equals(Material.WARPED_WALL_SIGN)) {
                player.sendMessage(ChatColor.BLUE + "...Decrypting Messages in this Block...");
                BlockState state = block.getState();
                Sign sign = (Sign) state;
                String signline1 = sign.getLine(1);
                player.sendMessage(ChatColor.YELLOW + "Data: " + signline1);


                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(signline1);
                    String decodedString = new String(decodedBytes);
                    System.out.println(block.getLocation());
//                    player.getWorld().getBlockAt(block.getLocation()).setType(Material.BEDROCK);
//                    Sign newSign = (Sign) player.getWorld().getBlockAt(block.getLocation()).getState();
//                    newSign.setLine(0, "Transaction: ");

                    if (decodedString.contains(player.getName())) {
                        sign.setLine(1, decodedString);
                        player.sendMessage(ChatColor.GREEN + "Decrypted Message: " + decodedString);
                    } else {
                        player.sendMessage(ChatColor.RED + "This transaction is NOT for you!!!");

                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                    player.sendMessage(ChatColor.RED + "This block does not contain an encrypted message or transaction!");
                }


            } else {
//                block.setType(Material.BEDROCK);
                player.sendMessage(
                        ChatColor.RED + "You clicked: " + ChatColor.RED + block.getType().toString().toUpperCase());


            }
        }
//
    }
//    @EventHandler
//    public void onBlockBreak(BlockBreakEvent event)
//    {
//        System.out.println("Block break!");
//        org.bukkit.block.Block b = event.getBlock();
//        org.bukkit.Material b1 = b.getType();
//        if (b1 == Material.DIAMOND_BLOCK  )
//        {
//            // Do you mean:
//            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
//                @Override
//                public void run() {
//                    // Code to replace the block
//                }
//            }, 20L); // 20 ticks delay (20 ticks = 1 second)
//            b.setType(Material.BEDROCK); // example to set it to log
//            b.setCancelled
//        }
//    }
}
