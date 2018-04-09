package RestarantApp.Network;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkClient {


    public String makeHTTPPOSTRequest(JSONObject finalJsonObject,String requestUrl) {
        String output = null;
        URL url;
        try {

            if (finalJsonObject != null) {
                String converStr = "x=" + finalJsonObject.toString();
               url = new URL("https://prawnandcrab.com/webservice/"+requestUrl +converStr);
//                    url = new URL("http://192.168.1.2/prawnandcrab/webservice/"+requestUrl +converStr);
            }else
            {
                url = new URL("https://prawnandcrab.com/webservice/"+requestUrl);
//                url = new URL("http://192.168.1.2/prawnandcrab/webservice/"+requestUrl);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",  "application/json; charset=UTF-8");

            String finalStr = "";
            if (finalJsonObject != null) {
                finalStr = "x=" + finalJsonObject.toString();
            }

            OutputStreamWriter  os = new OutputStreamWriter(conn.getOutputStream());
            os.write(finalStr);
            os.flush();

            /*if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }*/

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server .... \n");
            System.out.println("Output from Server .... "+conn.getResponseCode());
            System.out.println("Output from Server .... "+conn.getURL());

            while ((output = br.readLine()) != null) {
                System.out.println(output);
                return output;
            }

            conn.disconnect();


        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return output;
    }
}

