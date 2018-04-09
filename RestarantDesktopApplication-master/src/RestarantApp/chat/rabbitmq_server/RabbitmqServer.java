package RestarantApp.chat.rabbitmq_server;

import RestarantApp.aaditionalClass.AsyncTask;
import RestarantApp.chat.GetFromServerListener;
import RestarantApp.chat.rabbitmq_stomp.Client;
import RestarantApp.chat.rabbitmq_stomp.Listener;
import RestarantApp.model.Constants;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RabbitmqServer extends AsyncTask {
    public static Client client;

    public static GetFromServerListener fromServerListener;
    public RabbitmqServer(GetFromServerListener getFromServerListener)
    {
        this.fromServerListener = getFromServerListener;
    }
    @Override
    public void onPreExecute() {
        System.out.println("Pre Called");
    }

    @Override
    public Object doInBackground(Object[] params) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("IP Address:- " + inetAddress.getHostAddress());
            client = new Client(inetAddress.getHostAddress(),61613, "restaurantServer", "restaurant");
            System.out.println("Background Called");
            client.subscribe("/topic/resturantApp", new Listener() {
                @Override
                public void message(Map headers, String body) {
                    if (body.equals("null")) {
                        System.out.println("null");
                    }else
                    {
                        fromServerListener.getFromServer(body);
                    }

                 /*   Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation Dialog");
                            alert.setHeaderText("Look, a Confirmation Dialog");
                            alert.setContentText(body);

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK){
                                // ... user chose OK
                            } else {
                                // ... user chose CANCEL or closed the dialog
                            }
                        }
                    });*/

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.valueOf(e));
        } catch (LoginException e) {
            e.printStackTrace();
            System.out.println(String.valueOf(e));
        }
        return null;
    }

    @Override
    public void onPostExecute(Object params) {
        if (client.isConnected())
        {
            System.out.println("client connected");
        }

    }

    @Override
    public void progressCallback(Object[] params) {

    }


}
