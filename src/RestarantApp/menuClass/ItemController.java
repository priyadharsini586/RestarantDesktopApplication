package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemController {

    @FXML
    CheckComboBox checkCombo;
    private int i = 0;

    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    ArrayList<RequestAndResponseModel.cat_list> cat_listArrayList;
    String outputImage;
    BufferedImage bufferedImage;
    @FXML
    ImageView imgItemIamge,imgUpload;
    @FXML
    TextField txtItem,itemDes,txtPrice,txtItemId;
    ArrayList itemId = new ArrayList();

    public void initialize() {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        txtPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    txtPrice.setText(oldValue);
                }
            }
        });
        getData();
        imgUpload.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                try {

                    bufferedImage = ImageIO.read(UtilsClass.selectImage());
                    if (bufferedImage != null){
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        imgItemIamge.setImage(image);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        txtItemId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    txtItemId.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }


    public void btnUploadAction(ActionEvent actionEvent) {


    }

    public void btnAddCategory(ActionEvent actionEvent) {

        ObservableList getIndex =  checkCombo.getCheckModel().getCheckedIndices();
        ArrayList checkItem = new ArrayList();
        for (int i=1;i<getIndex.size();i++)
        {
            int index = (int) getIndex.get(i);


            checkItem.add(itemId.get(index));
        }


        sendItemDetails(checkItem);
    }

    private void sendItemDetails(ArrayList checkItem) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");
        try {
            jsonObject.put("item_name",txtItem.getText());
            jsonObject.put("short_code",txtItemId.getText());
            jsonObject.put("item_desc",itemDes.getText());
            jsonObject.put( "item_image",outputImage);
            jsonObject.put("item_price",txtPrice.getText());
            jsonObject.put("item_cat_list",checkItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.sendItemDetails(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println("select index------->"+requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                        }
                    });

                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {



                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    checkCombo.getCheckModel().clearChecks();
                                    txtItem.setText("");
                                    itemDes.setText("");
                                    txtPrice.setText("");
                                    imgItemIamge.setImage(null);
                                    checkCombo.getCheckModel().check(0);
                                }
                            });


                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }


    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> call = retrofitClient.categoryList();
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success)) {
                        cat_listArrayList = requestAndResponseModel.getCat_list();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setGridPane();
                            }
                        });


                    }else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });


    }

    private void setGridPane() {

        checkCombo.getItems().add("Select Catagory");
        itemId.add("-1");

        for (int i=0;i<cat_listArrayList.size();i++)
        {
            RequestAndResponseModel.cat_list cat_list = cat_listArrayList.get(i);
            checkCombo.getItems().add(cat_list.getCat_name());
            itemId.add(cat_list.getCat_id());
            System.out.println(cat_list.getCat_name());
        }

        checkCombo.getCheckModel().check(0);

        checkCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {

                System.out.println(checkCombo.getCheckModel().getCheckedItems());
            }
        });



    }

}
