package RestarantApp.popup;

import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.menuClass.ComboListController;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.TaxActionPopup;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.CheckComboBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.Network.APIService;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboActionPopup implements Initializable {
    @FXML
    StackPane catRootPane;
    @FXML
    AnchorPane anchorView,anchorEdit;
    @FXML
    Label labComboId,labComboName,labComboList,labComboStatus,editComboId;
    @FXML
    ImageView viewCloseImage,viewMinImage,editCloseImage,editMinImage;
    @FXML
    TextField editComboName;
    @FXML
    ComboBox editComboStatus;
    @FXML
    CheckComboBox editCheckItemList;
    ItemListRequestAndResponseModel itemListRequestAndResponseModel;
    int checkActiveStatus;
    HashMap<Integer,String> itemList;
    JFXSnackbar jfxSnackbar;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = ComboActionPopup.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        editComboStatus.getItems().add("Active");
        editComboStatus.getItems().add("DeActive");

        editComboStatus.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = editComboStatus.getSelectionModel().getSelectedIndex();
                if (newValue != null) {

                    if (newValue.equals("Active")) {
                        checkActiveStatus = 1;
                    } else if (newValue.equals("DeActive")) {
                        checkActiveStatus = 0;
                    }

                }
            }
        });
    }

    public void setItemHashMap(HashMap<Integer,String> itemHashMap)
    {
        this.itemList = itemHashMap;

    }

    public void setItemsDetails(ItemListRequestAndResponseModel item, String view) {
        this.itemListRequestAndResponseModel = item;
        if (view.equals("view"))
        {
            anchorView.setVisible(true);
            anchorEdit.setVisible(false);
            labComboId.setText(item.getId());
            labComboName.setText(item.getName());
            labComboList.setText(item.getComboList());
            labComboList.setWrapText(true);
            labComboList.setMaxWidth(300);
            labComboStatus.setText(item.getStatus());
        }else if (view.equals("edit"))
        {
            anchorView.setVisible(false);
            anchorEdit.setVisible(true);
            editComboName.setText(item.getName());
            editComboId.setText(item.getId());
            if (item.getStatus().equals("Active"))
            {
                editComboStatus.getSelectionModel().select(0);
            }else if (item.getStatus().equals("DeActive"))
            {
                editComboStatus.getSelectionModel().select(1);
            }

            editCheckItemList.getItems().addAll(itemList.values());
            ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(item.getComboList().split(",")));
            if (itemList.size() != 0)
            {
                for (int i=0;i< itemList.size();i++)
                {
                    editCheckItemList.getCheckModel().check(editCheckItemList.getItems().indexOf(itemList.get(i).trim()));
                }
            }

        }
    }

    public void viewCloseImageAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) viewCloseImage.getScene().getWindow();
        stage.close();
    }

    public void viewMinImageAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) viewMinImage.getScene().getWindow();
        stage.setIconified(true);
    }

    public void editCloseImageAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) editCloseImage.getScene().getWindow();
        stage.close();
    }

    public void editMinImageAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) editMinImage.getScene().getWindow();
        stage.setIconified(true);
    }

    public void btnSubmitUpdate(MouseEvent mouseEvent) {

        ObservableList getCheckItems =  editCheckItemList.getCheckModel().getCheckedItems();
        ArrayList list = new ArrayList();
        for (int j =0 ; j < getCheckItems.size() ; j++)
        {
            System.out.println(UtilsClass.getKeyFromValue(itemList,getCheckItems.get(j).toString().trim()));
            list.add(UtilsClass.getKeyFromValue(itemList,getCheckItems.get(j).toString().trim()));
        }

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("combo_id",editComboId.getText());
            jsonObject.put("combo_name",editComboName.getText());
            jsonObject.put("item_list",list.toString());
            jsonObject.put("active",checkActiveStatus);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.editCombo(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success)) {


                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    ItemListRequestAndResponseModel itemListRequestAndResponseModelList = new ItemListRequestAndResponseModel();
                                    itemListRequestAndResponseModelList.setId(itemListRequestAndResponseModel.getId());
                                    itemListRequestAndResponseModelList.setName(editComboName.getText());
                                    if (checkActiveStatus == 0) {
                                        itemListRequestAndResponseModelList.setStatus("DeActive");
                                    }else {itemListRequestAndResponseModelList.setStatus("Active");}

                                    itemListRequestAndResponseModelList.setComboList(getCheckItems.toString().substring(1, getCheckItems.toString().length()-1));

                                    jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                                    ComboListController.data.set(ComboListController.data.indexOf(itemListRequestAndResponseModel),itemListRequestAndResponseModelList);

                                }
                            });
                        }else
                        {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
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
}
