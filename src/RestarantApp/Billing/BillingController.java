package RestarantApp.Billing;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.AutoCompleteTextField;
import RestarantApp.aaditionalClass.EditingCell;
import RestarantApp.menuClass.ViewCategoryController;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    int intTaxPrice;
    ArrayList<ItemListRequestAndResponseModel.item_list> billingItemDetails = new ArrayList<>();
    @FXML
    TableView<BillingModel> tableBill;
    @FXML
    TableColumn colSno,colItem,colQty,colRate,colAmount;
    @FXML
    TextField txtQty,txtTotalAmount,txtFiledDiscount,txtFiledDiscountAmount,txtFileldGross,txtGstPercent,txtTotal,txtRounding,txtNetAmount;
    ObservableList<BillingModel> modelObservableList = FXCollections.observableArrayList();
    ItemListRequestAndResponseModel.item_list selectedItem;
    double subTotal;
    int serialNo = 1;
    ArrayList<Integer> itemIdList = new ArrayList<>();
    HashMap<Integer,Integer> getTaxListDetails = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemLoadProgres.setVisible(false);
        itemLoadProgres.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        String css = BillingController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        billingRootPane.getStylesheets().add(css);

        setTableDetails();

        getData();
        taxList();
//

        autoCompleteTextField = new AutoCompleteTextField(this);

        txtQty.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*") ) {

                    txtQty.setText(newValue.replaceAll("[^\\d]", ""));

                }else if ( !newValue.matches("^([1-9][0-9]{0,2}|1000)$"))
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


        txtFiledDiscount.textProperty().addListener(addDiscountPercentage);//discount percentage
        txtFiledDiscountAmount.textProperty().addListener(addDiscountAmount);//discount amount


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

        colQty.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {

                BillingModel billingModel = modelObservableList.get(event.getTablePosition().getRow());
                double amt = Double.valueOf(billingModel.getQuantity());
                double price = Double.valueOf(billingModel.getRate());
                amt = amt * price ;
                String amount = String.valueOf(amt);
                billingModel.setAmount(amount);
                modelObservableList.set(event.getTablePosition().getRow(),billingModel);
                setSubTotal();
            }
        });


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

            if (itemIdList.size() != 0)
            {
                int itemId = Integer.parseInt(selectedItem.getShort_code());
                System.out.println(itemId);
                if (itemIdList.contains(itemId))
                {
                    System.out.println("yes");
                    for (int j=0 ; j <modelObservableList.size() ;j++) {
                        BillingModel billingModelList = modelObservableList.get(j);
                        if (billingModelList.getItem_id().equals(selectedItem.getShort_code())) {
                            int alreadyValue = Integer.parseInt(billingModelList.getQuantity());
                            int newQty = Integer.parseInt(txtQty.getText());
                            newQty = newQty + alreadyValue;
                            double amt = Double.valueOf(newQty);
                            double price = Double.valueOf(selectedItem.getPrice());
                            amt = amt * price;
                            String amount = String.valueOf(amt);
                            serialNo = billingModelList.getS_no();
                            BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), String.valueOf(newQty), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                            modelObservableList.set(j, billingModel);
                            setSubTotal();
                        }
                    }
                }else
                {
                    double amt = Double.valueOf(txtQty.getText());
                    double price = Double.valueOf(selectedItem.getPrice());
                    amt = amt * price;
                    String amount = String.valueOf(amt);
                    BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                    modelObservableList.add(billingModel);
                    tableBill.setItems(modelObservableList);
                    itemIdList.add(Integer.parseInt(selectedItem.getShort_code()));
                    setSubTotal();
                }
            }else {
                double amt = Double.valueOf(txtQty.getText());
                double price = Double.valueOf(selectedItem.getPrice());
                amt = amt * price;
                String amount = String.valueOf(amt);
                BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                modelObservableList.add(billingModel);
                tableBill.setItems(modelObservableList);
                itemIdList.add(Integer.parseInt(selectedItem.getShort_code()));
                setSubTotal();
            }

            txtFieldId.clear();
            txtFieldName.clear();


           /* if (modelObservableList.size() != 0) {
                for (int j = 0; j < modelObservableList.size(); j++) {
                    BillingModel billingModelList = modelObservableList.get(j);
                    System.out.println("shor code--->"+modelObservableList.size());
                    if (billingModelList.getItem_id().equals(selectedItem.getShort_code())) {

                        int alreadyValue = Integer.parseInt(billingModelList.getQuantity());
                        int newQty = Integer.parseInt(txtQty.getText());
                        newQty = newQty + alreadyValue;
                        double amt = Double.valueOf(newQty);
                        double price = Double.valueOf(selectedItem.getPrice());
                        amt = amt * price;
                        String amount = String.valueOf(amt);
                        serialNo = billingModelList.getS_no();
                        BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(),String.valueOf(newQty), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                        modelObservableList.set(j,billingModel);
                        setSubTotal();
//                        tableBill.setItems(modelObservableList);
                    }else
                    {
                        double amt = Double.valueOf(txtQty.getText());
                        double price = Double.valueOf(selectedItem.getPrice());
                        amt = amt * price;
                        String amount = String.valueOf(amt);
                        BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                        modelObservableList.add(billingModel);
                    }
                }
            }else
            {
                double amt = Double.valueOf(txtQty.getText());
                double price = Double.valueOf(selectedItem.getPrice());
                amt = amt * price;
                String amount = String.valueOf(amt);
                BillingModel billingModel = new BillingModel(serialNo, selectedItem.getItem_name(), txtQty.getText(), selectedItem.getPrice(), amount, selectedItem.getShort_code());
                modelObservableList.add(billingModel);
                tableBill.setItems(modelObservableList);
                setSubTotal();
            }*/

        }

        serialNo++;


    }


    public void setSubTotal()
    {
        subTotal = 0.0;
        for (int j=0;j<modelObservableList.size();j++)
        {
            BillingModel billingModel = modelObservableList.get(j);
            double price = Double.parseDouble(billingModel.getAmount());
            subTotal = subTotal + price;
        }

        String subtotal = String.valueOf(subTotal);
        txtTotalAmount.setText(subtotal);
        double totalAmount = Double.valueOf(txtTotalAmount.getText());
        if (!txtFiledDiscount.getText().isEmpty())
        {
            System.out.println(txtFiledDiscount.getText());

            double getValue = Double.valueOf(txtFiledDiscount.getText());
            getValue = getValue/100;
            getValue = getValue*totalAmount;
            txtFiledDiscountAmount.setText(String.valueOf(getValue));
            txtFileldGross.setText(String.valueOf(totalAmount - getValue));

            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount - getValue));
            roundValue();

        }else if (!txtFiledDiscountAmount.getText().isEmpty())
        {

            double getValue = Double.valueOf(txtFiledDiscountAmount.getText());
            txtFileldGross.setText(String.valueOf(totalAmount - getValue));

            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount - getValue));
            roundValue();
        } else
        {
            txtFileldGross.setText(String.valueOf(totalAmount));
            double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
            double getGst = fromGst/100;
            getGst = getGst * totalAmount;
            txtTotal.setText(String.valueOf(getGst + totalAmount));
            roundValue();
        }


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
            int itemIdDelete = Integer.parseInt(list.getItem_id());
            System.out.println("item index---->"+itemIdList.indexOf(itemIdDelete));

            itemIdList.remove(itemIdList.indexOf(itemIdDelete));
            changeSNo();
        } else  {
            alert.close();

        }

    }


    private void changeSNo()
    {
        serialNo = 0;
        for (int i=0;i<modelObservableList.size();i++)
        {
            BillingModel billingModel = modelObservableList.get(i);
            billingModel.setS_no(i+1);
            modelObservableList.set(i,billingModel);
        }
        serialNo = modelObservableList.size()+1;

    }



    public void taxList()
    {
        retrofitService = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> getTaxCall = retrofitService.getTaxList();
        getTaxCall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();


                    for (int i=0;i < requestAndResponseModel.getList().size() ; i ++)
                    {
                        RequestAndResponseModel.list list = requestAndResponseModel.getList().get(i);
                        getTaxListDetails.put(Integer.valueOf(list.getId()),list.getValue());

                        if (list.getActive() == 1)
                        {
                            if (list.getComp1() != 0&& list.getComp2() != 0)
                            {
                                Integer comb1 = list.getComp1();
                                Integer comb2 = list.getComp2();
                                System.out.println("get combination--->"+getTaxListDetails.get(comb1));

                                intTaxPrice = comb1+comb2;
                                System.out.println("get combination--->"+intTaxPrice);
                                txtGstPercent.setText(String.valueOf(intTaxPrice)+"%");
                                txtGstPercent.setEditable(false);
                            }
                        }
                    }

                }
                System.out.println(getTaxListDetails);
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void roundValue()
    {
        String totalValue = txtTotal.getText();
        String[] totalValueSplit = totalValue.split("\\.");
        if (totalValueSplit[1] != null)
        {
            String firstChar = String.valueOf(totalValueSplit[1].charAt(0));
            int split2 = Integer.parseInt(firstChar);
            if (split2 != 0)
            {
                if (split2 > 5)
                {
                    System.out.println("greater than 5");

                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue + 1;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());
                    System.out.println(String.valueOf(rounding - actulaValue));
                    txtRounding.setText(String.valueOf(rounding - actulaValue));
                    String round = String.valueOf(rounding - actulaValue);

                    if (round.contains("-"))
                    {
                        round = "+ "+round.substring(1);
                        txtRounding.setText(round);
                    }else
                    {
                        round = "- "+round;
                        txtRounding.setText(round);
                    }


                }else if (split2 < 5)
                {
                    System.out.println("less than 5");
                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue ;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());
                    System.out.println(String.valueOf(rounding - actulaValue));
                    String round = String.valueOf(rounding - actulaValue);
//                    txtRounding.setText(round);
                    if (round.contains("-"))
                    {
                        round = "+ "+round.substring(1);
                        txtRounding.setText(round);
                    }else
                    {
                        round = "- "+round.substring(1);
                        txtRounding.setText(round);
                    }

                }else
                {
                    System.out.println("equals to 5");

                    int roundValue = Integer.parseInt(totalValueSplit[0]);
                    int actulaValue = roundValue + 1;
                    txtNetAmount.setText(String.valueOf(actulaValue));

                    double rounding = Double.valueOf(txtTotal.getText());
                    System.out.println(String.valueOf(rounding - actulaValue));
                    txtRounding.setText(String.valueOf(rounding - actulaValue));
                    String round = String.valueOf(rounding - actulaValue);
                    if (round.contains("-"))
                    {
                        round = "+ "+round.substring(1);
                        txtRounding.setText(round);
                    }else
                    {
                        txtRounding.setText(round);
                    }
                }
            }else
            {
                System.out.println("retuns 0");
                txtNetAmount.setText(totalValueSplit[0]);
                txtRounding.setText("0");
            }
        }

    }

    public void calculatePergentage(String discountValue)
    {
        double totalAmount = Double.valueOf(txtTotalAmount.getText());
        double givenValue = Double.valueOf(discountValue);
        double percentage = givenValue / totalAmount;
        percentage = percentage * 100;
        System.out.println("new percentage---->"+String.valueOf(percentage));
        String percent = String.valueOf(percentage);
        if (percent.contains("."))
        {
            String[] split= percent.split("//.");
            txtFiledDiscount.setText(split[0]);

        }else {
            txtFiledDiscount.setText(percent);
        }

    }

    ChangeListener<String> addDiscountPercentage = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {



            double totalAmount = Double.valueOf(txtTotalAmount.getText());

            if (!txtTotalAmount.getText().equals("0.0"))
            {
                double getValue = 0.0;

                if (newValue.isEmpty())
                {
                    txtFileldGross.setText(String.valueOf(totalAmount));
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * totalAmount ;
                    txtTotal.setText(String.valueOf(getGst + totalAmount));
                    roundValue();
                }else {
                    getValue = Double.valueOf(newValue);
                }

                System.out.println(newValue);

                getValue = getValue/100;
                getValue = getValue*totalAmount;

                txtFiledDiscountAmount.setText(String.valueOf(getValue));

                txtFileldGross.setText(String.valueOf(totalAmount - getValue));

                if (!newValue.isEmpty()) {
                    txtFileldGross.setText(String.valueOf(totalAmount - getValue));
                    double fromGross = Double.valueOf(txtFileldGross.getText());
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * fromGross;
                    txtTotal.setText(String.valueOf(getGst + fromGross));
                    roundValue();
                }
            }
        }
    };

    ChangeListener<String> addDiscountAmount = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            if (!newValue.isEmpty())
                calculatePergentage(newValue);
            if (!txtTotalAmount.getText().equals("0.0"))
            {

                double totalAmount = Double.valueOf(txtTotalAmount.getText());

                double getValue = 0.0;
                if (newValue.isEmpty())
                {
                    txtFileldGross.setText(String.valueOf(totalAmount));
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * totalAmount ;
                    txtTotal.setText(String.valueOf(getGst + totalAmount));
                    roundValue();
                }else {
                    getValue = Double.valueOf(newValue);
                }
                if (!newValue.isEmpty()) {
                    txtFileldGross.setText(String.valueOf(totalAmount - getValue));
                    double fromGross = Double.valueOf(txtFileldGross.getText());
                    double fromGst = Double.valueOf(txtGstPercent.getText().replace("%",""));
                    double getGst = fromGst/100;
                    getGst = getGst * fromGross;
                    txtTotal.setText(String.valueOf(getGst + fromGross));
                    roundValue();
                }
            }
        }
    };

}
