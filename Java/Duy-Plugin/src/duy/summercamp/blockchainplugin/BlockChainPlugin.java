package duy.summercamp.blockchainplugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class BlockChainPlugin extends JavaPlugin{

    @Override
    public void onEnable() {
        //Fired when the server enables the plugin
        System.out.println("onEnable() works");
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
                //GetAndPost.POSTRequest(response.toString());
            } else {
                System.out.println("GET NOT WORKED");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins

    }
}
