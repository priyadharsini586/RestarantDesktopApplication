package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.ItemActionPopup;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class TaxListController implements Initializable {

    @FXML
    AnchorPane taxEditRoot,taxViewRoot;
    @FXML
    StackPane rootPane;
    JFXSnackbar jfxSnackbar;
    @FXML
    TableColumn taxIdCol,taxNameCol,taxValueCol,taxCombinationCol,taxStatusColm;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    ArrayList pageNo = new ArrayList();
   public  static   ObservableList<ItemListRequestAndResponseModel> data;
    @FXML
    TableView tableTax;
    @FXML
    ImageView imgNext,imgPrevious;
    APIService retrofitClient;
    HashMap<Integer, String> getComboId = new HashMap<>();
    public void btnUpdateTax(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);
        // Set up the table data
        taxIdCol.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("id")
        );
        taxNameCol.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("name")
        );
        taxValueCol.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("value")
        );
        taxCombinationCol.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("combine")
        );
        taxStatusColm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("status")
        );

        tableTax.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableTax.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableTax.setFixedCellSize(45);
        tableTax.prefHeightProperty().bind(Bindings.size(tableTax.getItems()).multiply(tableTax.getFixedCellSize()).add(30));

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

                    deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
//                            ItemListRequestAndResponseModel selectedIndex =getTableView().getItems().get(getIndex());
                            showAlert(person);
//                            System.out.println("person id---------->"+ selectedIndex.getItemId());

                        }
                    });
                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
                            System.out.println("mouse clicked---------->"+person.getItemId());

                            Parent root;
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/edit_tax.fxml"));
                                Parent root1 = (Parent) fxmlLoader.load();
                                TaxActionPopup taxActionPopup =fxmlLoader.getController();
                                taxActionPopup.setItemsDetails(person,"edit");
                                taxActionPopup.setTaxHashMap(getComboId);
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




        imgNext.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                remainCount = remainCount + 1;
                pageNum = pageNum  + 1;
                imgPrevious.setVisible(true);
                getTableData();
            }
        });
        imgPrevious.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                pageNum = pageNum - 1;
                remainCount = (int)pageNo.get(pageNum);
                for(int j=0;j<pageNo.size();j++)
                {
                    System.out.println("get page Number from array----->"+String.valueOf(pageNo.get(j)));
                }

                getTableData();

            }
        });

        tableTax.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ItemListRequestAndResponseModel item = (ItemListRequestAndResponseModel) tableTax.getSelectionModel().getSelectedItem();

                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/edit_tax.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    TaxActionPopup itemActionPopup =fxmlLoader.getController();
                    itemActionPopup.setItemsDetails(item,"view");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("ABC");
                    stage.setScene(new Scene(root1));
                    stage.show();
                    // Hide this current window (if this is what you want)
//                    ((Node)(event.getSource())).getScene().getWindow().hide();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

               /* if (item != null) {
                    System.out.println(item.getItemName() + item.getItemName());


                }*/
            }
        });
        getTaxFiled();

        tableTax.getColumns().addAll(unfriendCol);
    }

    public void getTableData() {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ItemListRequestAndResponseModel> call = retrofitClient.taxView(jsonObject);
        call.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    } else {
                        dummyCount = remainCount - 1;
                    }

                    data = FXCollections.observableArrayList();
                    data.clear();
                    ItemListRequestAndResponseModel itemListRequestAndResponseModel = response.body();

                    ArrayList getTaxDetils = itemListRequestAndResponseModel.getTax_list();
                    totalcount = itemListRequestAndResponseModel.getTot_taxes();
                  /*  for (int i = 0; i < getTaxDetils.size(); i++) {
                        ItemListRequestAndResponseModel.list list = (ItemListRequestAndResponseModel.list) getTaxDetils.get(i);
                        System.out.println(list.getName());

                    }*/
                    for (int j= 0 ; j < getTaxDetils.size() ; j ++)
                    {
                        ItemListRequestAndResponseModel.list list = (ItemListRequestAndResponseModel.list) getTaxDetils.get(j);
                        System.out.println(list.getName());
                        ItemListRequestAndResponseModel itemListRequestAndResponseModel1 = new ItemListRequestAndResponseModel();
                        itemListRequestAndResponseModel1.setId(list.getId());
                        itemListRequestAndResponseModel1.setName(list.getName());
                        System.out.println(list.getComp1() +"+"+  list.getComp2());
                        if (list.getComp1().equals("") || list.getComp2().equals(""))
                        {
                            itemListRequestAndResponseModel1.setCombine("-");
                        }else
                        {

                            Integer combo1 = Integer.valueOf(list.getComp1());
                            Integer combo2 = Integer.valueOf(list.getComp2());

                            System.out.println("combo 1--->"+combo1 + "combo 2--->"+combo2);
                            if (combo1 != 0 || combo2 != 0) {
                                String combo1Value = getComboId.get(combo1);
                                String combo2Value = getComboId.get(combo2);
                                itemListRequestAndResponseModel1.setCombine(combo1Value + " + " + combo2Value);
                            }
                            else{
                                itemListRequestAndResponseModel1.setCombine("-");}

                        }
                        if (list.getActive().equals("1"))
                        {
                            itemListRequestAndResponseModel1.setStatus("Active");
                        }else if (list.getActive().equals("0"))
                        {
                            itemListRequestAndResponseModel1.setStatus("DeActive");
                        }
                        if (list.getValue().equals("0"))
                        {
                            itemListRequestAndResponseModel1.setValue("-");
                        }else
                        {
                            itemListRequestAndResponseModel1.setValue(list.getValue());
                        }


                        data.addAll(itemListRequestAndResponseModel1);
                        dummyCount = dummyCount + 1;
                    }
                    tableTax.setItems(data);
                    tableTax.refresh();
                    remainCount = dummyCount;
                    System.out.println("in response remain count--->" + String.valueOf(remainCount));
                    if (totalcount >= remainCount) {
                        imgNext.setVisible(true);
                    }

                    if (remainCount == totalcount) {
                        imgNext.setVisible(false);

                    }else
                    {
                        imgNext.setVisible(true);
                    }
                    if (pageNum == 0) {
                        imgPrevious.setVisible(false);
                    } else
                    {
                        imgPrevious.setVisible(true);
                    }

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });

    }


    private void getTaxFiled() {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> getTaxCall = retrofitClient.getTaxList();
        getTaxCall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();


                    for (int i=0;i < requestAndResponseModel.getList().size() ; i ++)
                    {
                        RequestAndResponseModel.list list = requestAndResponseModel.getList().get(i);
                        getComboId.put(Integer.valueOf(list.getId()),list.getName());
                    }
                    getTableData();

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
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
            jsonObject.put("tax_id", list.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deleteTax(jsonObject);
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




}
