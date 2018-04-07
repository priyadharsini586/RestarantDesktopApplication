package RestarantApp.popup;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.menuClass.CategoryController;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ItemActionPopup implements Initializable {
    @FXML
    ImageView closeButton,minizeButton,imgItemViewIamge,imgeCloseEditButton,imgeMiniEditButton,updateItemIamge;
    @FXML
    Label labId,labItemName,labItemDescription,labItemPrice,labItemCategory,updateItemId;
    @FXML
    AnchorPane ancViewItem,ancEditItem;
    @FXML
    CheckComboBox comboEditItem;
    @FXML
    TextField updateItemName,updateItemDescription,updateItemPrice;
    ArrayList<RequestAndResponseModel.cat_list> cat_listArrayList;
    @FXML
    Button btnCancel,btnSubmitItem;
    ArrayList itemId = new ArrayList();
    ArrayList<String> itemList;
    String outputImage,strItemId;
    BufferedImage bufferedImage;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;

    public void closeImageClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void minimizemageClicked(MouseEvent event) {
        Stage stage = (Stage) minizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ItemActionPopup.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        getData();
    }

    public void setItemsDetails(ItemListRequestAndResponseModel msg,String from)
    {
        if (from.equals("view")) {
            ancEditItem.setVisible(false);
            ancViewItem.setVisible(true);
            labId.setText(msg.getItemShortCode());
            labItemName.setText(msg.getItemName());
            labItemDescription.setText(msg.getItemDescription());
            labItemPrice.setText(msg.getItemPrice());
            labItemCategory.setText(msg.getItemCategoryList());
            Image image = new Image(Constants.ITEM_BASE_URL + msg.getItemImage());
            imgItemViewIamge.setImage(image);
            System.out.println("hello message----------->" + msg.getItemName());
        }else if (from.equals("edit"))
        {
            ancEditItem.setVisible(true);
            ancViewItem.setVisible(false);

            updateItemId.setText(msg.getItemShortCode());
            updateItemName.setText(msg.getItemName());
            updateItemDescription.setText(msg.getItemDescription());
            updateItemPrice.setText(msg.getItemPrice());
            strItemId = msg.getItemId();
            Image image = new Image(Constants.ITEM_BASE_URL + msg.getItemImage());
            updateItemIamge.setImage(image);

            itemList = new ArrayList<String>(Arrays.asList(msg.getItemCategoryList().split(",")));
        }
    }

    public void closeEditButton(MouseEvent mouseEvent) {
        Stage stage = (Stage) imgeCloseEditButton.getScene().getWindow();
        stage.close();
    }

    public void minizeEditbutton(MouseEvent mouseEvent) {
        Stage stage = (Stage) imgeMiniEditButton.getScene().getWindow();
        stage.setIconified(true);
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

        ArrayList updateItemList = new ArrayList();
        updateItemList.add("Select Catagory");
//        comboEditItem.getItems().add("Select Catagory");
        itemId.add("-1");

        for (int i=0;i<cat_listArrayList.size();i++)
        {
            RequestAndResponseModel.cat_list cat_list = cat_listArrayList.get(i);
            updateItemList.add(cat_list.getCat_name());
            itemId.add(cat_list.getCat_id());
            System.out.println(cat_list.getCat_name());
        }

        comboEditItem.getItems().addAll(updateItemList);
        if (itemList != null) {
            for (int j = 0; j < itemList.size(); j++) {
                String itemValue = itemList.get(j).trim();
//            int index = updateItemList.indexOf(itemList.get(j));
                comboEditItem.getCheckModel().check(updateItemList.indexOf(itemValue));

            }
        }

        comboEditItem.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {

                System.out.println(comboEditItem.getCheckModel().getCheckedItems());
            }
        });



    }

    public void btnSubmitItem(ActionEvent actionEvent) {
        updateItem();
    }

    public void btnCanelItem(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }


    public void updateItem()
    {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        ObservableList getIndex =  comboEditItem.getCheckModel().getCheckedIndices();
        ArrayList checkItem = new ArrayList();
        for (int i=1;i<getIndex.size();i++)
        {
            int index = (int) getIndex.get(i);


            checkItem.add(itemId.get(index));
        }

        if (bufferedImage != null)
            outputImage = UtilsClass.encodeToString(bufferedImage,"png");
        else
            outputImage = " ";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("item_id",strItemId);
            jsonObject.put("item_name",updateItemName.getText());
            jsonObject.put("item_desc",updateItemDescription.getText());
            jsonObject.put("item_image",outputImage);
            jsonObject.put("item_price",updateItemPrice.getText());
            jsonObject.put("item_cat_list",checkItem);
            jsonObject.put("short_code",updateItemId.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.itemEdit(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success)) {
                        cat_listArrayList = requestAndResponseModel.getCat_list();
                        System.out.println(requestAndResponseModel.getSuccessMessage());
                       /* Stage stage = (Stage) btnSubmitItem.getScene().getWindow();
                        stage.close();*/
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
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
                System.out.println(throwable.getMessage());
            }
        });
    }

    public void updateUploadItem(MouseEvent mouseEvent) {
        try {

            bufferedImage = ImageIO.read(UtilsClass.selectImage());
            if (bufferedImage != null){
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                updateItemIamge.setImage(image);
            }
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
