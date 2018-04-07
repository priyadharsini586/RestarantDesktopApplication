package RestarantApp.menuClass;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RestarantApp.Network.*;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;

public class CategoryController  {
    @FXML
    ImageView imgCategoryIamge,imgUpload;
    String outputImage;
    BufferedImage bufferedImage;
    private NetworkClient networkClient;
    @FXML
    StackPane catRootPane;
    @FXML
    TextField txtCatagory,txtCatagoryName;
    JFXSnackbar jfxSnackbar;
    @FXML
    ProgressIndicator progressCategory;
    public void initialize() {
        networkClient = new NetworkClient();

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        progressCategory.setVisible(false);
        progressCategory.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        imgUpload.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                try {
                        bufferedImage = ImageIO.read(UtilsClass.selectImage());
                    if (bufferedImage != null){
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        imgCategoryIamge.setImage(image);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }



    public void btnAddCategory(ActionEvent actionEvent) throws IOException {

        progressCategory.setVisible(true);
        sendCategoryDetails();

      /*  JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cat_name", "starter");
            jsonObject.put("cat_tag", "tag");
            jsonObject.put("cat_image", outputImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String output = networkClient.makeHTTPPOSTRequest(jsonObject, Api.addCategory);

        System.out.println("network output"+output);*/

    }

    public void sendCategoryDetails()
    {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");

        try {
            jsonObject.put("cat_name", txtCatagory.getText());
            jsonObject.put("cat_tag", txtCatagoryName.getText());
            jsonObject.put("cat_image", outputImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.sendCategoryDetails(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    progressCategory.setVisible(false);
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                        }
                    });

                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {
                        txtCatagory.setText("");
                        txtCatagoryName.setText("");
                        imgCategoryIamge.setImage(null);
                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });

    }

}
