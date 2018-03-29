package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.ItemActionPopup;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemListController {

    String outputImage = "null";
    BufferedImage bufferedImage;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    @FXML
    TableView<ItemListRequestAndResponseModel> tableIndex;

    @FXML
    TableColumn itemIdColm,itemNameColm,itemDesColm,itemImagecolm,itemPricecolm,itemCatListcolm;
    // The table's data
    ObservableList<ItemListRequestAndResponseModel> data;
//    @FXML
//    AnchorPane anchorEditItem,anchorViewItem;


    @FXML
    ImageView imgNext,imgPrevious;


    @FXML
    StackPane rootPane;
    JFXSnackbar jfxSnackbar;

    ArrayList pageNo = new ArrayList();

    public void initialize() {

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);

        // Set up the table data
        itemIdColm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,Long>("itemShortCode")
        );
        itemNameColm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("itemName")
        );
        itemDesColm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("itemDescription")
        );
        itemImagecolm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("itemImage")
        );
        itemPricecolm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("itemPrice")
        );
        itemCatListcolm.setCellValueFactory(
                new PropertyValueFactory<ItemListRequestAndResponseModel,String>("itemCategoryList")
        );
        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(40);
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

                        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                event.consume();

//                                RequestAndResponseModel.list currentPerson = (RequestAndResponseModel.list) deleteButton.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                                ItemListRequestAndResponseModel selectedIndex =getTableView().getItems().get(getIndex());
                                showAlert(person);
                                System.out.println("person id---------->"+ selectedIndex.getItemId());

                            }
                        });
                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
                            System.out.println("mouse clicked---------->"+person.getItemId());

                            Parent root;
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/item_popup.fxml"));
                                Parent root1 = (Parent) fxmlLoader.load();
                                ItemActionPopup itemActionPopup =fxmlLoader.getController();
                                itemActionPopup.setItemsDetails(person,"edit");
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

                          /*  txtFieldId.setText(person.getItemId());
                            txtFieldTagLine.setText(person.getItemDescription());
                            txtFieldName.setText(person.getItemId());
                            Image image = new Image(Constants.CATEGORY_BASE_URL + person.getItemImage());
                            imgEditImage.setImage(image);*/

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
                getData();
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

                getData();

            }
        });

        getData();




        tableIndex.getColumns().addAll(unfriendCol);

//        addData();




        //select column value
        /*ObservableList<TablePosition> selectedCells = tableIndex.getSelectionModel().getSelectedCells() ;
        selectedCells.addListener((ListChangeListener.Change<? extends TablePosition> change) -> {
            if (selectedCells.size() > 0) {
                TablePosition selectedCell = selectedCells.get(0);
                TableColumn column = selectedCell.getTableColumn();
                int rowIndex = selectedCell.getRow();
                String data = (String) column.getCellObservableValue(rowIndex).getValue();
                System.out.println(data);

            }
        });*/


        tableIndex.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ItemListRequestAndResponseModel item = tableIndex.getSelectionModel().getSelectedItem();
                System.out.println(Constants.ITEM_BASE_URL + item.getItemImage());
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/item_popup.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    ItemActionPopup itemActionPopup =fxmlLoader.getController();
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



    }

  /*  public void btnUploadEditAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        try {

            bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
//            imgEditImage.setImage(image);
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

   /* private void setViewItem(ItemListRequestAndResponseModel item) {

        labId.setText(item.getItemId());
        labTagLine.setText(item.getItemDescription());
        labViewName.setText(item.getItemName());
        Image image = new Image(Constants.CATEGORY_BASE_URL + item.getItemImage());
        System.out.println(Constants.CATEGORY_BASE_URL + item.getItemName());
        imgViewImage.setImage(image);
    }*/


    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ItemListRequestAndResponseModel> call = retrofitClient.itemView(jsonObject);
        call.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    }else
                    {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    ItemListRequestAndResponseModel itemListRequestAndResponseModel = response.body();

                    ArrayList getItemDetils = itemListRequestAndResponseModel.getItem_lists();
                    totalcount = itemListRequestAndResponseModel.getTot_items();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        ItemListRequestAndResponseModel.item_list list = (ItemListRequestAndResponseModel.item_list) getItemDetils.get(i);
                        ItemListRequestAndResponseModel itemList = new ItemListRequestAndResponseModel();
                        itemList.setItemId(list.getItem_id());
                        itemList.setItemName(list.getItem_name());
                        itemList.setItemPrice(list.getPrice());
                        itemList.setItemShortCode(list.getShort_code());
                        itemList.setItemDescription(list.getDescription());
                        ArrayList itemCatList = new ArrayList();
                        String listCat = "null";
                        if (list.getCat_list() != null) {
                            for (int j = 0; j < list.getCat_list().size(); j++) {
                                ItemListRequestAndResponseModel.cat_list cat_list = list.getCat_list().get(j);
                                itemCatList.add(cat_list.getCat_name());
                            }
                            listCat = itemCatList.toString();
                            listCat = listCat.substring(1, listCat.length()-1);
                        }else
                        {
                            itemCatList.add("null");
                        }
                        itemList.setItemCategoryList(listCat);
                        itemList.setItemImage(list.getImage());
                        data.add(itemList);
                        dummyCount = dummyCount + 1;
                    }
                    remainCount = dummyCount;
                    System.out.println("in response remain count--->" + String.valueOf(remainCount));
                    tableIndex.setItems(data);
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
                System.out.println(throwable);
            }
        });


    }


    void showAlert(ItemListRequestAndResponseModel list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getItemName() + " Item?");

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
            jsonObject.put("item_id", itemId.getItemId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deleteItem(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                            }
                        });
                        data.remove(itemId);
                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });

    }

//    public void btnSubmitEdit(ActionEvent actionEvent) {
//        sendEditCategoryDetails();
//    }


  /*  public void sendEditCategoryDetails()
    {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        if (bufferedImage != null)
            outputImage = UtilsClass.encodeToString(bufferedImage,"png");
        else
            outputImage = " ";

        try {
            jsonObject.put("cat_name", txtFieldName.getText());
            jsonObject.put("cat_tag", txtFieldTagLine.getText());
            jsonObject.put("cat_image", outputImage);
            jsonObject.put("cat_id",txtFieldId.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.editCategory(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);

                            txtFieldId.setText("");
                            txtFieldTagLine.setText("");
                            txtFieldName.setText("");
                            imgEditImage.setImage(null);
                        }
                    });

                    data.clear();
                    remainCount = (int)pageNo.get(pageNum);
                    getData();


                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }*/
}

