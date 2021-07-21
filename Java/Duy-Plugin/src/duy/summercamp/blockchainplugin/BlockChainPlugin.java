package duy.summercamp.blockchainplugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;



public class BlockChainPlugin extends JavaPlugin{

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
        pm.registerEvents(blockListener, this);



    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins

    }
}
