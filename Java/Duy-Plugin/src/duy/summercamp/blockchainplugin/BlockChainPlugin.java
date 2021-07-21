package duy.summercamp.blockchainplugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Base64;


public class BlockChainPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //Fired when the server enables the plugin
//        System.out.println("onEnable() works");
//        URL urlForGetRequest = null;
//        try {
//            urlForGetRequest = new URL("https://blockchain-main.ngrok.io/chain");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        String readLine = null;
//        HttpURLConnection conection = null;
//        try {
//            conection = (HttpURLConnection) urlForGetRequest.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            conection.setRequestMethod("GET");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }
//        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
//        int responseCode = 0;
//        try {
//            responseCode = conection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(conection.getInputStream()));
//                StringBuffer response = new StringBuffer();
//                while ((readLine = in.readLine()) != null) {
//                    response.append(readLine);
//                }
//                in.close();
//                // print result
//                System.out.println("JSON String Result " + response.toString());
//                //GetAndPost.POSTRequest(response.toString());
//            } else {
//                System.out.println("GET NOT WORKED");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        getCommand("blockchain").setExecutor(new BlockchainCommand());
        getCommand("blocktalk").setExecutor(new BlockChat());
        BlockListener blockListener = new BlockListener();

        PluginManager pm = getServer().getPluginManager();
//        pm.registerEvents(blockListener, this);
        pm.registerEvents(this, this);



    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins

    }
    @EventHandler
    public void onPlayerBreak(BlockBreakEvent e) {
        System.out.println("Block Break!");

        Location blockl = e.getBlock().getLocation();
        final Material block = e.getBlock().getType();

        final org.bukkit.block.Block blockb = e.getPlayer().getWorld().getBlockAt(blockl);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        if (block == Material.WARPED_WALL_SIGN) {
            BlockState state = e.getBlock().getState();
            Sign sign = (Sign) state;
//            String tempData = sign.getLine(1);
            Player player = e.getPlayer();
            e.getPlayer().sendMessage(ChatColor.BLUE + "...Decrypting Messages in this Block...");
            String data = sign.getLine(1);
            player.sendMessage(ChatColor.YELLOW + "Data: " + data);
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(data);
                String decodedString = new String(decodedBytes);

                if (decodedString.contains(player.getName())) {
                    sign.setLine(1, decodedString);
                    player.sendMessage(ChatColor.GREEN + "Decrypted Message: " + decodedString);
                } else {
                    player.sendMessage(ChatColor.RED + "This transaction is NOT for you!!!");

                }
            }
            catch (Exception ex) {
                System.out.println(ex);
                player.sendMessage(ChatColor.RED + "This block does not contain an encrypted message or transaction!");
            }
            final String transactionData = sign.getLine(1);

            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
//                System.out.println("run!");
                    blockb.setType(block);
                    System.out.println(block);
                    if (block == Material.WARPED_WALL_SIGN) {

                        BlockState newState = e.getBlock().getState();
                        Sign newSign = (Sign) newState;
                        newSign.setLine(0, "Transaction:");
                        newSign.setLine(1, transactionData);

                        newSign.update();
                        System.out.println(newSign.getLine(0));


                    }
                }
            }, 10L);
        }
        else {
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
//                System.out.println("run!");
                    blockb.setType(block);
                    System.out.println(block);
                }
            }, 10L);
        }



    }


}
