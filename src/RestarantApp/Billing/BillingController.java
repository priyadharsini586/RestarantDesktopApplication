package RestarantApp.Billing;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.AutoCompleteTextField;
import RestarantApp.aaditionalClass.EditingCell;
import RestarantApp.menuClass.AddTaxController;
import RestarantApp.menuClass.ViewCategoryController;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import RestarantApp.popup.ItemActionPopup;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class BillingController implements Initializable, ItemSelectedListener  {

    @FXML
    AnchorPane billingRootPane;
    @FXML
    ProgressIndicator itemLoadProgres;
    @FXML
    AutoCompleteTextField txtFieldName,txtFieldId;

    AutoCompleteTextField  autoCompleteTextField;

    Set<String> possibleWordSet = new HashSet<>();
    private AutoCompletionBinding<String> autoCompletionBinding;

    APIService retrofitService;
    Set<String> itemName = new HashSet<>();
    Set<String> itemId =  new HashSet<>();
    ArrayList<ItemListRequestAndResponseModel.item_list> billingItemDetails = new ArrayList<>();
    @FXML
    TableView<BillingModel> tableBill;
    @FXML
    TableColumn colSno,colItem,colQty,colRate,colAmount;
    @FXML
    TextField txtQty,txtTotalAmount;
    ObservableList<BillingModel> modelObservableList = FXCollections.observableArrayList();
    ItemListRequestAndResponseModel.item_list selectedItem;
    double subTotal;
    int serialNo = 1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemLoadProgres.setVisible(false);
        itemLoadProgres.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        String css = BillingController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        billingRootPane.getStylesheets().add(css);

        setTableDetails();

        getData();
//

        autoCompleteTextField = new AutoCompleteTextField(this);

        txtQty.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtQty.setText(newValue.replaceAll("[^\\d]", ""));

                }else if ( !newValue.matches("[1-9]*"))
                {
                    txtQty.setText("1");
                }
            }
        });

        txtFieldId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtFieldId.setText(newValue.replaceAll("[^\\d]", ""));

                }
            }
        });

        txtQty.setText("1");
    }


    private void setTableDetails() {

        tableBill.setEditable(true);
        colSno.setResizable(false);
        colSno.setCellValueFactory(new PropertyValueFactory<BillingModel,String>("s_no"));
        colSno.setPrefWidth( 100 );

        colItem.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("item_name")
        );
        colItem.setPrefWidth( 250 );
        colQty.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("quantity")
        );
        colQty.setPrefWidth( 100 );
        tableBill.setEditable( true );
        javafx.util.Callback<TableColumn, TableCell> cellFactory =
                new javafx.util.Callback<TableColumn, TableCell>() {

                    @Override
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        colQty.setCellFactory(cellFactory);

        colRate.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("rate")
        );
        colRate.setPrefWidth( 100 );
        colAmount.setCellValueFactory(
                new PropertyValueFactory<BillingModel,String>("amount")
        );
        colAmount.setPrefWidth( 120 );


        tableBill.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableBill.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableBill.setFixedCellSize(35);



        TableColumn<BillingModel, BillingModel> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setPrefWidth( 160 );
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<BillingModel,BillingModel>() {

                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(BillingModel billingModel, boolean empty) {
                    super.updateItem(billingModel, empty);

                    if (billingModel == null) {
                        setGraphic(null);
                        return;
                    }
                    String TOOLTIP = "deleteTip",EDITTIP = "editTip";

                    Tooltip deleteTip = new Tooltip("Delete Item");
                    deleteButton.getProperties().put(TOOLTIP, deleteTip);
                    Tooltip.install(deleteButton, deleteTip);

                    Tooltip editTip = new Tooltip("Edit Item");


                    HBox pane = new HBox( button1);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(5);
                    pane.setPadding(new Insets(10, 0, 10, 10));

                    button1.setStyle("-fx-background-color: transparent;");
                    setGraphic(pane);
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {


                            showAlert(billingModel);

                        }
                    });

                    deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();

                        }
                    });

                }
            };

        });
        tableBill.getColumns().addAll(unfriendCol);


    }


    private void getData() {
        itemLoadProgres.setVisible(true);
        retrofitService = RetrofitClient.getClient().create(APIService.class);
        Call<ItemListRequestAndResponseModel> getItemList = retrofitService.getItemList();
        getItemList.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    ItemListRequestAndResponseModel listRequestAndResponseModel = response.body();
                    for (int j= 0 ; j < listRequestAndResponseModel.getItem_list().size() ; j++)
                    {
                        ItemListRequestAndResponseModel.item_list item_list = listRequestAndResponseModel.getItem_list().get(j);
                        itemName.add(item_list.getItem_name());
                        itemId.add(item_list.getShort_code());

                        billingItemDetails.add(item_list);

                    }
//                    TextFields.bindAutoCompletion(txtFieldName,itemName);
                    txtFieldName.getEntries().addAll(itemName);
                    txtFieldId.getEntries().addAll(itemId);
                    itemLoadProgres.setVisible(false);

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void textFiledNamePressed(javafx.scene.input.KeyEvent keyEvent) {
        switch (keyEvent.getCode())
        {
            case ENTER:
                learnWord(txtFieldName.getText());
                break;
        }
    }

    public void learnWord(String text)
    {
        possibleWordSet.add(text);
        if (autoCompletionBinding != null)
        {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding =  TextFields.bindAutoCompletion(txtFieldName,possibleWordSet);

    }


    public void btnAddItem(MouseEvent mouseEvent) {

        if (selectedItem != null)
        {
            double amt = Double.valueOf(txtQty.getText());
            double price = Double.valueOf(selectedItem.getPrice());
            amt = amt * price ;
            subTotal = subTotal + amt;
            String amount = String.valueOf(amt);
            BillingModel billingModel = new BillingModel(serialNo,selectedItem.getItem_name(),txtQty.getText(),selectedItem.getPrice(),amount);
            modelObservableList.add(billingModel);
            tableBill.setItems(modelObservableList);
            String subtotal = String.valueOf(subTotal);
            System.out.println(subtotal);
            txtTotalAmount.setText(subtotal);

        }

        serialNo++;

    }



    @Override
    public void getSelectedResult(String result) {

        for (int j= 0 ; j < billingItemDetails.size() ; j ++)
        {
            ItemListRequestAndResponseModel.item_list item_list = billingItemDetails.get(j);
            if (item_list.getItem_name().equals(result))
            {
                selectedItem = item_list;
                txtFieldId.setText(item_list.getShort_code());
                txtFieldId.hidePopUp();
            }else if (item_list.getShort_code().equals(result))
            {
                selectedItem = item_list;
                txtFieldName.setText(item_list.getItem_name());
                txtFieldName.hidePopUp();
            }
        }

    }

    void showAlert(BillingModel list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getItem_name() + " Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            double deletedItemPrice = Double.parseDouble(list.getRate());
            double deletedItemQty = Double.parseDouble(list.getQuantity());
            deletedItemPrice = deletedItemPrice * deletedItemQty ;
            subTotal = subTotal - deletedItemPrice;
            String subtotal = String.valueOf(subTotal);
            System.out.println(subtotal);
            txtTotalAmount.setText(subtotal);
            modelObservableList.remove(list);
        } else  {
            alert.close();

        }

    }


}
