package RestarantApp;

import RestarantApp.aaditionalClass.AsyncTask;
import RestarantApp.chat.rabbitmq_server.RabbitmqServer;
import RestarantApp.chat.rabbitmq_stomp.Client;
import RestarantApp.chat.rabbitmq_stomp.Listener;
import RestarantApp.model.Constants;
import RestarantApp.model.NetworkConnection;
import com.jfoenix.controls.JFXSnackbar;
import com.rabbitmq.client.ConnectionFactory;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Main extends Application {


    public Client client;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login/Login.fxml"));
        primaryStage.setTitle("Prawn And Crabs");
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setMaximized(true);
        Image image = new Image(Main.class.getResourceAsStream("/RestarantApp/images/yesball.png"));
        primaryStage.getIcons().add(image);

        primaryStage.show();




    }


    public static void main(String[] args) {
        launch(args);

    }



}
