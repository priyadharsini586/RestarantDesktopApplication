package RestarantApp.model;

import javafx.scene.control.Alert;

public class Constants {

    public static String userName = "restaurantServer";
    public static String password = "restaurant";
    public static String Server_url = "192.168.1.8";

    public static String Success = "1";
    public static String Failure = "0";

    public static String CATEGORY_BASE_URL = "https://prawnandcrab.com/webservice/images/category/";
    public static String ITEM_BASE_URL = "https://prawnandcrab.com/webservice/images/item/";
//    public static String CATEGORY_BASE_URL = "http://192.168.1.2/prawnandcrab/webservice/images/category/";
//    public static String ITEM_BASE_URL = "http://192.168.1.2/prawnandcrab/webservice/images/item/";


    public static void showAlert(Alert.AlertType alertType,String title,String headerTxt,String content)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerTxt);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
