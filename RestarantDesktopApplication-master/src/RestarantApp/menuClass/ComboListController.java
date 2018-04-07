package RestarantApp.menuClass;

import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.ComboActionPopup;
import RestarantApp.popup.TaxActionPopup;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboListController implements Initializable {
    @FXML
    TableView<ItemListRequestAndResponseModel> tableComboList;
    APIService retrofitClient ;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    public static  ObservableList<ItemListRequestAndResponseModel> data;
    ArrayList pageNo = new ArrayList();
    @FXML
    TableColumn comboId,comboName,comboList,comboStatus;
    HashMap<Integer,String> itemDetails;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane rootPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);

        comboId.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("id")
        );
        comboName.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("name")
        );
        comboList.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("comboList")
        );
        comboStatus.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("status")
        );
        tableComboList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableComboList.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableComboList.setFixedCellSize(45);
        tableComboList.prefHeightProperty().bind(Bindings.size(tableComboList.getItems()).multiply(tableComboList.getFixedCellSize()).add(30));

        TableColumn<ItemListRequestAndResponseModel, ItemListRequestAndResponseModel> tableAction = new TableColumn<>("Action");
        tableAction.setMinWidth(40);
        tableAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableAction.setCellFactory(param -> {
            return new TableCell<ItemListRequestAndResponseModel,ItemListRequestAndResponseModel>()
            {
                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView editButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);
                @Override
                protected void updateItem(ItemListRequestAndResponseModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setGraphic(null);
                        return;
                    }
                    String TOOLTIP = "deleteTip",EDITTIP = "editTip";

                    Tooltip deleteTip = new Tooltip("Delete Item");
                    deleteButton.getProperties().put(TOOLTIP, deleteTip);
                    Tooltip.install(deleteButton, deleteTip);

                    Tooltip editTip = new Tooltip("Edit Item");
                    editButton.getProperties().put(EDITTIP, deleteTip);
                    Tooltip.install(editButton, editTip);

                    HBox pane = new HBox(editButton, button1);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(5);
                    pane.setPadding(new Insets(10, 0, 0, 10));
                    button1.setStyle("-fx-background-color: transparent;");
                    setGraphic(pane);
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                           showAlert(item);

                        }
                    });

                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();

                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/editcomboScene.fxml"));
                                Parent root1 = (Parent) fxmlLoader.load();
                                ComboActionPopup comboActionPopup =fxmlLoader.getController();
                                comboActionPopup.setItemHashMap(itemDetails);
                                comboActionPopup.setItemsDetails(item,"edit");
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setTitle("ABC");
                                stage.setScene(new Scene(root1));
                                stage.show();

                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
        });

        tableComboList.getColumns().addAll(tableAction);

        tableComboList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ItemListRequestAndResponseModel item = (ItemListRequestAndResponseModel) tableComboList.getSelectionModel().getSelectedItem();

                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/editcomboScene.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    ComboActionPopup comboActionPopup =fxmlLoader.getController();
                    comboActionPopup.setItemsDetails(item,"view");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("ABC");
                    stage.setScene(new Scene(root1));
                    stage.show();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

               /* if (item != null) {
                    System.out.println(item.getItemName() + item.getItemName());


                }*/
            }
        });
        getItemName();

    }
    void showAlert(ItemListRequestAndResponseModel list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getName() + " Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            deleteItem(list);
        } else  {
            alert.close();

        }

    }

    private void deleteItem(ItemListRequestAndResponseModel list) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("combo_id", list.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deleteCombo(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success))
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                            }
                        });
                        data.remove(list);
                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }
    private void getComboList() {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ItemListRequestAndResponseModel> call = retrofitClient.comboView(jsonObject);
        call.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful())
                {
                    ItemListRequestAndResponseModel itemListRequestAndResponseModel = response.body();
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    }else
                    {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    ArrayList getItemDetils = itemListRequestAndResponseModel.getList();
                    totalcount = itemListRequestAndResponseModel.getTot_items();

                    for (int i = 0; i < getItemDetils.size(); i++) {
                        ItemListRequestAndResponseModel.list list = (ItemListRequestAndResponseModel.list) getItemDetils.get(i);
                        ItemListRequestAndResponseModel itemList = new ItemListRequestAndResponseModel();
                        itemList.setId(list.getId());
                        itemList.setName(list.getName());
                        list.setItem_list(list.getItem_list().substring(1, list.getItem_list().length()-1));
                        ArrayList<String> itemListCombo = new ArrayList<String>(Arrays.asList(list.getItem_list().split(",")));
                        ArrayList itemListComboDetails = new ArrayList();
                        for (int j=0;j<itemListCombo.size();j++)
                        {

                            Integer itemId =Integer.valueOf(itemListCombo.get(j).trim());
                            String itemName = itemDetails.get(itemId);
                            itemListComboDetails.add(itemName);
                            System.out.println(itemName);
                        }
                        String item = itemListComboDetails.toString();
                        itemList.setComboList(item.substring(1,item.length()-1));
                        if (list.getActive().equals("1"))
                        {
                            itemList.setStatus("Active");
                        }else if (list.getActive().equals("0"))
                        {
                            itemList.setStatus("DeActive");
                        }
                        data.addAll(itemList);
                    }
                    tableComboList.setItems(data);
                    tableComboList.refresh();

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void getItemName()
    {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<ItemListRequestAndResponseModel> getItemCall = retrofitClient.getItemList();
        getItemCall.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    ItemListRequestAndResponseModel listRequestAndResponseModel = response.body();
                    itemDetails = new HashMap<>();
                    for (int j= 0 ; j < listRequestAndResponseModel.getItem_list().size() ; j++)
                    {
                        ItemListRequestAndResponseModel.item_list item_list = listRequestAndResponseModel.getItem_list().get(j);
                        Integer itemId = Integer.valueOf(item_list.getItem_id());

                        itemDetails.put(itemId,item_list.getItem_name());

                    }
                    getComboList();

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }
}
