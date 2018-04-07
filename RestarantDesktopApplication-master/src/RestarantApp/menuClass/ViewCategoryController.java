package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ViewCategoryController {

    String outputImage = "null";
    BufferedImage bufferedImage;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    @FXML
    TableView<RequestAndResponseModel.list> tableIndex;

    @FXML
    TableColumn itemIdColm,itemNameColm,itemTaglineColm,itemImagecolm;
    // The table's data
    ObservableList<RequestAndResponseModel.list> data;
    @FXML
    AnchorPane anchorEditItem,anchorViewItem;

    @FXML
    Label labId,labViewName,labTagLine,txtFieldId;
    @FXML
    ImageView imgViewImage,imgEditImage,imgNext,imgPrevious;

    @FXML
    TextField txtFieldName,txtFieldTagLine;

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
                new PropertyValueFactory<RequestAndResponseModel.list,Long>("id")
        );
        itemNameColm.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.list,String>("name")
        );
        itemTaglineColm.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.list,String>("tag_line")
        );
        itemImagecolm.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.list,String>("image")
        );
        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(45);
        tableIndex.prefHeightProperty().bind(Bindings.size(tableIndex.getItems()).multiply(tableIndex.getFixedCellSize()).add(30));

        TableColumn<RequestAndResponseModel.list, RequestAndResponseModel.list> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
                return new TableCell<RequestAndResponseModel.list,RequestAndResponseModel.list>() {

                    Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                    Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                    ImageView editButton = new ImageView(image1);
                    ImageView deleteButton = new ImageView(image);
                    Button button1 = new Button("", deleteButton);

                    @Override
                    protected void updateItem(RequestAndResponseModel.list person, boolean empty) {
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

                                RequestAndResponseModel.list selectedIndex =getTableView().getItems().get(getIndex());
                                showAlert(person);
                                System.out.println("person id---------->"+ selectedIndex.getId());
                            }
                        });

                       /* deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                event.consume();

//                                RequestAndResponseModel.list currentPerson = (RequestAndResponseModel.list) deleteButton.this.getTableView().getItems().get(ButtonCell.this.getIndex());

                                RequestAndResponseModel.list selectedIndex =getTableView().getItems().get(getIndex());
                                showAlert(person);
                                System.out.println("person id---------->"+ selectedIndex.getId());

                            }
                        });*/
                        editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                event.consume();
                                System.out.println("mouse clicked---------->"+person.getId());
                                anchorEditItem.setVisible(true);
                                anchorViewItem.setVisible(false);
                                txtFieldId.setText(person.getId());
                                txtFieldTagLine.setText(person.getTag_line());
                                txtFieldName.setText(person.getName());
                                Image image = new Image(Constants.CATEGORY_BASE_URL + person.getImage());
                                imgEditImage.setImage(image);

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


        anchorEditItem.setVisible(false);
        anchorViewItem.setVisible(false);

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
                RequestAndResponseModel.list item = tableIndex.getSelectionModel().getSelectedItem();
                if (item != null) {
                    System.out.println(item.getName() + item.getImage());
                    anchorViewItem.setVisible(true);
                    anchorEditItem.setVisible(false);
                    setViewItem(item);
                }
            }
        });
    }

    public void btnUploadEditAction(ActionEvent actionEvent) {

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
            imgEditImage.setImage(image);
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setViewItem(RequestAndResponseModel.list item) {

        labId.setText(item.getId());
        labTagLine.setText(item.getTag_line());
        labViewName.setText(item.getName());
        Image image = new Image(Constants.CATEGORY_BASE_URL + item.getImage());
        System.out.println(Constants.CATEGORY_BASE_URL + item.getImage());
        imgViewImage.setImage(image);
    }


    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.viewCategory(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    }else
                    {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    RequestAndResponseModel requestAndResponseModel = response.body();

                    ArrayList getItemDetils = requestAndResponseModel.getList();
                    totalcount = requestAndResponseModel.getTot_cats();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        RequestAndResponseModel.list list = (RequestAndResponseModel.list) getItemDetils.get(i);
                        data.add(list);
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
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });


    }


     void showAlert(RequestAndResponseModel.list list)
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

    private void deleteItem(RequestAndResponseModel.list itemId) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cat_id", itemId.getId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deleteCategory(jsonObject);
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

    public void btnSubmitEdit(ActionEvent actionEvent) {
        sendEditCategoryDetails();
    }


    public void sendEditCategoryDetails()
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

    }
}
