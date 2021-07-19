package duy.summercamp.blockchainplugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.http.HttpEntity;
import org.bukkit.craftbukkit.libs.org.apache.http.NameValuePair;
import org.bukkit.craftbukkit.libs.org.apache.http.client.entity.UrlEncodedFormEntity;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpPost;
import org.bukkit.craftbukkit.libs.org.apache.http.impl.client.HttpClients;
import org.bukkit.craftbukkit.libs.org.apache.http.message.BasicNameValuePair;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockChat implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        Player p = Bukkit.getPlayer(commandSender.getName());
        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Too few arguments");
            p.sendMessage(ChatColor.RED + "/blocktalk <player> <message>");
            return true;
        }
        String recipientUsername = args[0];
        if (Bukkit.getPlayer(recipientUsername) == null) {
            p.sendMessage(ChatColor.RED + "That player is not online");
            return true;
        }
        Player p2 = Bukkit.getPlayer(recipientUsername);
        String msg = "";
        for (String s : args) {
            msg = msg + " " + s;
        }
        p2.sendMessage(p.getName() + " -> me: " + msg.replaceFirst(" " + args[0], ""));
        p.sendMessage("me -> " + args[0] + ": " + msg.replaceFirst(" " + args[0], ""));


        URL urlForGetRequest = null;
        String URLString = String.format("https://blockchain-client.ngrok.io/generate/transaction/message?sender_username=%1$s&recipient_username=%2$s&message=%3$s", p.getName(), recipientUsername, URLEncoder.encode(msg.replaceFirst(" " + args[0] + " ", "")), "UTF-8");
//        String URLString = String.format("https://blockchain-client.ngrok.io/generate/transaction/message");

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n" + URLString);

        try {
            urlForGetRequest = new URL(URLString);
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
//                System.out.println("JSON String Result " + response.toString());
                Gson gson = new Gson();
                Client_Transaction responseJSON = gson.fromJson(response.toString(), Client_Transaction.class);
//                String signature = responseJSON.signature;
                String postData = "sender_address=" + responseJSON.transaction.sender_address +
                        "&recipient_address=" + responseJSON.transaction.recipient_address +
                        "&amount=" + URLEncoder.encode(responseJSON.transaction.value) +
                        "&signature=" + responseJSON.signature;
                String postURLString = "https://blockchain-main.ngrok.io/transactions/new";
                URL url = new URL(postURLString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
//                conn.setRequestProperty ("Authorization", encodedCredentials);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                writer.write(postData);
                writer.flush();
                String line;
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));
                response = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    response.append(line);

                }


                // print result
                System.out.println("POST JSON String Result " + response.toString());

                writer.close();
                reader.close();

            } else {
                System.out.println(responseCode);
                System.out.println("GET NOT WORKED");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
