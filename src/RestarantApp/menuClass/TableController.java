package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.ItemActionPopup;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class TableController implements Initializable {
    @FXML
    TableColumn table_id,table_name,table_status;
    ObservableList<ItemListRequestAndResponseModel> data;
    @FXML
    TableView<ItemListRequestAndResponseModel> table;
    @FXML
    AnchorPane updateTable,viewTable;
    @FXML
    Label tableId,tableName,tableStatus,updateTableId;
    @FXML
    TextField updateTableName;
    @FXML
    ComboBox updateTableStatus;
    int checkActive;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = AddTaxController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        viewTable.setVisible(false);
        updateTable.setVisible(false);

        // Set up the table data

        updateTableStatus.getItems().add("Active");
        updateTableStatus.getItems().add("DeActive");



        table_id.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("id")
        );
        table_name.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("name")
        );
        table_status.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("status")
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        table.setFixedCellSize(40);
//        tableIndex.prefHeightProperty().bind(Bindings.size(tableIndex.getItems()).multiply(tableIndex.getFixedCellSize()).add(30));

        TableColumn<ItemListRequestAndResponseModel, ItemListRequestAndResponseModel> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<ItemListRequestAndResponseModel,ItemListRequestAndResponseModel>() {

                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView editButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(ItemListRequestAndResponseModel person, boolean empty) {
                    super.updateItem(person, empty);

                    if (person == null) {
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

                            ItemListRequestAndResponseModel selectedIndex = getTableView().getItems().get(getIndex());
                            showAlert(person);
                        }
                    });

                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
                            updateTable.setVisible(true);
                            viewTable.setVisible(false);
                             updateTableId.setText(person.getId());
                             updateTableName.setText(person.getName());

                             if (person.getStatus().equals("Active"))
                             {
                                 updateTableStatus.getSelectionModel().select(0);
                             }else if (person.getStatus().equals("DeActive"))
                             {
                                 updateTableStatus.getSelectionModel().select(1);
                              }

                        }
                    });
                }
            };

        });

        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                ItemListRequestAndResponseModel item = table.getSelectionModel().getSelectedItem();

                updateTable.setVisible(false);
                viewTable.setVisible(true);

                tableStatus.setText(item.getStatus());
                tableId.setText(item.getId());
                tableName.setText(item.getName());

            }
        });

        updateTableStatus.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue.equals("Active"))
                {
                    checkActive = 1;
                }else if (newValue.equals("DeActive"))
                {
                    checkActive = 0;
                }


            }
        });
        getData();

        table.getColumns().addAll(unfriendCol);
    }



    void showAlert(ItemListRequestAndResponseModel list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getName() + " Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            deleteItem(list);
        } else  {
            alert.close();
            // ... user chose CANCEL or closed the dialog
        }

    }

    private void deleteItem(ItemListRequestAndResponseModel itemId) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("table_id", itemId.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deleteTable(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getSuccessMessage());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                        }
                    });
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success))
                    {

                        data.remove(itemId);
                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });

    }




    private void updateItem(ItemListRequestAndResponseModel itemId) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("table_id", itemId.getId());
            jsonObject.put("table_nam", updateTableName.getText());
            jsonObject.put("active", checkActive);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.updateTable(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);

                            updateTable.setVisible(false);
                            viewTable.setVisible(false);
                        }
                    });
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {
                        ItemListRequestAndResponseModel itemListRequestAndResponseModelList = new ItemListRequestAndResponseModel();
                        if (checkActive == 0) {
                            itemListRequestAndResponseModelList.setStatus("DeActive");
                        }else {itemListRequestAndResponseModelList.setStatus("Active");}

                        itemListRequestAndResponseModelList.setId(itemId.getId());
                        itemListRequestAndResponseModelList.setName(updateTableName.getText());

                        data.set(data.indexOf(itemId),itemListRequestAndResponseModelList);


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

        Call<ItemListRequestAndResponseModel> call = retrofitClient.getTableList();
        call.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {

                    data = FXCollections.observableArrayList();
                    ItemListRequestAndResponseModel itemListRequestAndResponseModel = response.body();

                    ArrayList getItemDetils = itemListRequestAndResponseModel.getList();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        ItemListRequestAndResponseModel.list list = (ItemListRequestAndResponseModel.list) getItemDetils.get(i);
                        ItemListRequestAndResponseModel itemList = new ItemListRequestAndResponseModel();
                        itemList.setId(list.getId());
                        itemList.setName(list.getName());
                        if (list.getActive().equals("1"))
                        {
                            itemList.setStatus("Active");
                        }else if (list.getActive().equals("0"))
                        {
                            itemList.setStatus("DeActive");
                        }
                        data.add(itemList);
                    }

                    table.setItems(data);

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });


    }

    public void btnUpdateAction(ActionEvent actionEvent) {
        ItemListRequestAndResponseModel item = table.getSelectionModel().getSelectedItem();

        updateItem(item);
    }
}
