package duy.summercamp.blockchainplugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.bukkit.craftbukkit.libs.org.apache.http.HttpEntity;
import org.bukkit.craftbukkit.libs.org.apache.http.client.methods.HttpPost;
import org.bukkit.entity.Player;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class DeepfakeCommand implements CommandExecutor {
    String imageDir = null;
    public DeepfakeCommand(String pluginDir){
        imageDir = pluginDir + "/Images/";
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {

        if (commandSender instanceof Player) {
            Player p = Bukkit.getPlayer(commandSender.getName());
            String port = "8888";
            String url = "http://127.0.0.1:" + port + "/deepfake/predict";

            String testImageName = args[0];
            if (testImageName == null) {
                p.sendMessage(ChatColor.RED + "Please enter a valid image name!");
                return true;
            }

            // 2. create obj for the URL class
            URL obj = null;
            try {
                obj = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // 3. open connection on the url
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) obj.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Server is down!");
                return true;
            }
            try {
                con.setRequestMethod("POST");
            } catch (ProtocolException e) {

                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Error Connecting to Server!");
                return true;
            }
            con.setRequestProperty("Content-Type","image/png");
            con.setDoInput(true);
            con.setDoOutput(true);
            OutputStream out = null;
            try {
                out = con.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataOutputStream image = new DataOutputStream(out);

            Path path = Paths.get(imageDir + testImageName);
            System.out.println(testImageName);
            byte[] fileContents = new byte[0];
            try {
                fileContents = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Error reading file! Please double check image path and try again!");
                return true;
            }
            try {
                image.write(fileContents, 0, fileContents.length);
            } catch (IOException e) {
                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Unable to write to server!");
                return true;
            }


            int responseCode = 0;
            String readLine = null;

            try {
                responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    StringBuffer response = new StringBuffer();
                    while ((readLine = in.readLine()) != null) {
                        response.append(readLine);
                    }
                    in.close();
                    // print result
                    System.out.println("JSON String Result " + response.toString());
                    Gson gson = new Gson();
                    PredictionResult prediction = gson.fromJson(response.toString(), PredictionResult.class);
                    System.out.println(prediction.result);

                    if (prediction.result.equals("real")) {
                        p.sendMessage(ChatColor.GREEN + "Our DeepFake Model detected image " + testImageName + " as REAL");
                    }
                    else if (prediction.result.equals("fake")) {
                        p.sendMessage(ChatColor.GREEN + "Our DeepFake Model detected image " + testImageName + " as FAKE");
                    }
                } else {
                    System.out.println("POST NOT WORKED");
                    p.sendMessage(ChatColor.RED + "Server returns error: " + responseCode);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                p.sendMessage(ChatColor.RED + "Error! Please try again!");
                return true;
            }
        }

        return false;
    }
}
