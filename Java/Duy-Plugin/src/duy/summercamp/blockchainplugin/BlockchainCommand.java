package duy.summercamp.blockchainplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftCommandBlock;
import org.bukkit.craftbukkit.v1_17_R1.command.CraftBlockCommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import org.bukkit.event.Listener;


public class BlockchainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        System.out.println(commandSender.getClass());
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;


            URL urlForGetRequest = null;
            try {
                urlForGetRequest = new URL("https://blockchain-main.ngrok.io/chain");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String readLine = null;
            HttpURLConnection conection = null;
            try {
                conection = (HttpURLConnection) urlForGetRequest.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                conection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
            int responseCode = 0;
            try {
                responseCode = conection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conection.getInputStream()));
                    StringBuffer response = new StringBuffer();
                    while ((readLine = in.readLine()) != null) {
                        response.append(readLine);
                    }
                    in.close();
                    // print result
                    System.out.println("JSON String Result " + response.toString());
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject rootObj = parser.parse(response.toString()).getAsJsonObject();
                    JsonElement chainJSON = rootObj.get("chain");
                    Type listType = new TypeToken<List<Block>>() {}.getType();
//                    System.out.println(rootObj.get("chain").getAsJsonArray());
                    List<Block> blockchain = new Gson().fromJson(chainJSON, listType);

                    float startingX = 0;
                    Location playerLoc = player.getLocation();
                    for (Block block: blockchain) {
                        // 1 - can call methods of element

                        Timestamp ts=new Timestamp(block.timestamp);
                        Date transactionDate = new Date(ts.getTime());
                        // ...
                        int currentLine = 1;
//                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n" + block.nonce);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n" + block.block_number);

                        Location newSpawnLocation = playerLoc.add(startingX, 0, 0);
                        player.getWorld().getBlockAt(player.getLocation().add(startingX,1,1)).setType(Material.DIAMOND_BLOCK);
                        player.getWorld().getBlockAt(player.getLocation().add(startingX,1,0)).setType(Material.WARPED_WALL_SIGN);
                        player.getWorld().getBlockAt(player.getLocation().add(startingX,2,1)).setType(Material.DIAMOND_BLOCK);
                        player.getWorld().getBlockAt(player.getLocation().add(startingX,2,0)).setType(Material.WARPED_WALL_SIGN);

                        Sign topSign= (Sign) player.getWorld().getBlockAt(player.getLocation().add(startingX,2,0)).getState();
                        Sign bottomSign= (Sign) player.getWorld().getBlockAt(player.getLocation().add(startingX,1,0)).getState();

                        topSign.setLine(0, "Block ID: " + String.valueOf(block.block_number));
                        topSign.setLine(1, "Nonce: " + String.valueOf(block.nonce));
                        topSign.setLine(2, "Prev:" + String.valueOf(block.previous_hash));
                        topSign.setLine(3, "Date:" + transactionDate);

                        bottomSign.setLine(0, "Transactions:" );
                        for (Transaction transaction : block.transactions) {

                            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "\n" + transaction.value);
                            bottomSign.setLine(currentLine,  "\"" + transaction.value + "\"");
                            currentLine+=1;

                        }

                        topSign.update();
                        bottomSign.update();
                        startingX-=1;

                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\nSign Update");

//                        break;

                    }




                } else {
                    System.out.println("GET NOT WORKED");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }




        }
        else if (commandSender instanceof CraftBlockCommandSender) {
            System.out.println("Command Blockkk");
            BlockCommandSender blockCommandSender = (BlockCommandSender) commandSender;
            System.out.println(blockCommandSender);

            org.bukkit.block.Block cmdBlock = (org.bukkit.block.Block) blockCommandSender.getBlock();
            System.out.println(cmdBlock);

            URL urlForGetRequest = null;
            try {
                urlForGetRequest = new URL("https://blockchain-main.ngrok.io/chain");
//                System.out.println("try 1");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String readLine = null;
            HttpURLConnection conection = null;
            try {
                conection = (HttpURLConnection) urlForGetRequest.openConnection();
//                System.out.println("try 2");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                conection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
            int responseCode = 0;
            try {
//                System.out.println("try 3");
                responseCode = conection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conection.getInputStream()));
                    StringBuffer response = new StringBuffer();
                    while ((readLine = in.readLine()) != null) {
                        response.append(readLine);
                    }
                    in.close();
                    // print result
                    System.out.println("JSON String Result " + response.toString());
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject rootObj = parser.parse(response.toString()).getAsJsonObject();
                    JsonElement chainJSON = rootObj.get("chain");
                    Type listType = new TypeToken<List<Block>>() {
                    }.getType();
//                    System.out.println(rootObj.get("chain").getAsJsonArray());
                    List<Block> blockchain = new Gson().fromJson(chainJSON, listType);
//                    Block[] blockchain = gson.fromJson(response.toString(), Block[].class);
//                    player.getWorld().getBlockAt(player.getLocation().add(1,0,1)).setType(Material.DIAMOND_BLOCK);
//                    player.getWorld().getBlockAt(player.getLocation().add(2,0,1)).setType(Material.DIAMOND_BLOCK);
//                    player.getWorld().getBlockAt(player.getLocation().add(3,0,1)).setType(Material.DIAMOND_BLOCK);

//                    player.getWorld().getBlockAt(player.getLocation()).setBlockData(new Material());

                    //                    System.out.println(blockchain);
                    float startingX = 0;
                    for (Block block : blockchain) {
                        // 1 - can call methods of element

                        Timestamp ts = new Timestamp(block.timestamp);
                        Date transactionDate = new Date(ts.getTime());
                        // ...
                        int currentLine = 1;
//                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n" + block.nonce);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n" + block.block_number);

                        cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,1,1)).setType(Material.DIAMOND_BLOCK);
                        org.bukkit.block.Block topSignBlock = cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,2,0));
                        topSignBlock.setType(Material.WARPED_WALL_SIGN);
                        org.bukkit.block.Block topSign2Block = cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,2,2));
                        topSign2Block.setType(Material.WARPED_WALL_SIGN);

                        cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,2,1)).setType(Material.DIAMOND_BLOCK);
                        org.bukkit.block.Block bottomSignBlock = cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,1,0));
                        bottomSignBlock.setType(Material.WARPED_WALL_SIGN);
                        org.bukkit.block.Block bottomSign2Block = cmdBlock.getWorld().getBlockAt(cmdBlock.getLocation().add(startingX,1,2));
                        bottomSign2Block.setType(Material.WARPED_WALL_SIGN);



                        Sign topSign = (Sign) topSignBlock.getState();
                        Sign bottomSign = (Sign) bottomSignBlock.getState();


                        Sign topSign2 = (Sign) topSign2Block.getState();
                        Sign bottomSign2 = (Sign) bottomSign2Block.getState();




//                        System.out.println(((Directional) topSign2Block.getBlockData()).getFacing());

                        System.out.println(topSign);
                        topSign.setLine(0, "Block ID: " + String.valueOf(block.block_number));
                        topSign.setLine(1, "Nonce: " + String.valueOf(block.nonce));
                        topSign.setLine(2, "Prev:" + String.valueOf(block.previous_hash));
                        topSign.setLine(3, "Date:" + transactionDate);

                        topSign2.setLine(0, "Block ID: " + String.valueOf(block.block_number));
                        topSign2.setLine(1, "Nonce: " + String.valueOf(block.nonce));
                        topSign2.setLine(2, "Prev:" + String.valueOf(block.previous_hash));
                        topSign2.setLine(3, "Date:" + transactionDate);

                        topSign.update();
                        topSign2.update();

                        bottomSign.setLine(0, "Transactions:");
                        bottomSign2.setLine(0, "Transactions:");

                        for (int i = 0; i < block.transactions.length; i++) {
                            if (i<=2) {
                                Transaction transaction = block.transactions[i];
                                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "\n" + transaction.value);
                                bottomSign.setLine(currentLine, "\"" + transaction.value + "\"");
                                bottomSign2.setLine(currentLine, "\"" + transaction.value + "\"");

                                currentLine += 1;
                            }


                        }



                        bottomSign.update();
                        bottomSign2.update();

                        Directional dir = (Directional) topSign2Block.getBlockData();
                        dir.setFacing(BlockFace.SOUTH);
                        topSign2Block.setBlockData(dir);
                        System.out.println(((Directional) topSign2Block.getBlockData()).getFacing());
                        System.out.println(((Directional) topSignBlock.getBlockData()).getFacing());

//
//
                        Directional dir2 = (Directional) bottomSign2Block.getBlockData();
                        dir2.setFacing(BlockFace.SOUTH);
                        bottomSign2Block.setBlockData(dir2);
                        startingX -= 1;

                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\nSign Update");

//                        break;

                    }


                } else {
                    System.out.println("GET NOT WORKED");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
