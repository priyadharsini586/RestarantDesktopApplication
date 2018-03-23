package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.CheckComboBox;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.scene.layout.StackPane;
import java.util.HashMap;
public class AddComboController implements Initializable {

    @FXML
    TextField textFieldComboNAme,textFieldComboRate;
    @FXML
    CheckComboBox checkComboList;
    ArrayList<String> itemId  = new ArrayList<>();
    ArrayList<String> itemPrice  = new ArrayList<>();
    APIService retrofitClient;
    @FXML
    Label textFieldActualCost;
    @FXML
    VBox vBoxLabel;
    @FXML
    ComboBox comboStatus;
    @FXML
    StackPane catRootPane;
    int checkActive = 0;
    HashMap<Integer,String> itemDetails ;
    JFXSnackbar jfxSnackbar;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = AddTaxController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        setComboList();
        getTaxFiled();

        checkComboList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {

                genertateTabele(checkComboList.getCheckModel().getCheckedItems());
                System.out.println(checkComboList.getCheckModel().getCheckedItems());

            }
        });

        comboStatus.getItems().add("Active");
        comboStatus.getItems().add("DeActive");

        comboStatus.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = comboStatus.getSelectionModel().getSelectedIndex();
                if (newValue != null) {
                    if (newValue.equals("Active")) {
                        checkActive = 1;
                    } else if (newValue.equals("DeActive")) {
                        checkActive = 0;
                    }

                }
            }
        });

        textFieldComboRate.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    textFieldComboRate.setText(oldValue);
                }
            }
        });

    }

    public void genertateTabele(ObservableList labelText)
    {
        vBoxLabel.getChildren().clear();
        double sum = 0;
            for (int j = 0; j < labelText.size(); j++) {
                if (!labelText.get(j).equals("Select Combo Item")) {
                    HBox hBox = new HBox();
                    String labelString = (String) labelText.get(j);
                    Label labelName = new Label(labelString);
                    labelName.setFont(Font.font("Times New Roman", FontWeight.BOLD, 17));
                    labelName.setPrefWidth(150.0);
                    labelName.setTextFill(Color.WHITE);

                    Label labelPrice = new Label(itemPrice.get(j));
                    labelPrice.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
                    labelPrice.setTextFill(Color.WHITE);
                    labelPrice.setPrefWidth(80.0);

                    hBox.getChildren().add(labelName);
                    hBox.getChildren().add(labelPrice);
                    vBoxLabel.getChildren().add(hBox);
                    double price =Double.valueOf( itemPrice.get(j));
                    sum = sum + price;
                }
            }

        textFieldActualCost.setText(String.valueOf(sum));

    }
    private void setComboList() {
        checkComboList.getItems().add("Select Combo Item");
        itemPrice.add("-1");
        itemId.add("-1");


    }


    private void getTaxFiled() {
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
                        checkComboList.getItems().add(item_list.getItem_name());
                        itemPrice.add(item_list.getPrice());
                        Integer itemId = Integer.valueOf(item_list.getItem_id());
                        itemDetails.put(itemId,item_list.getItem_name());
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                    checkComboList.getCheckModel().check(0);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void btnComboItemClicked(MouseEvent mouseEvent) {

        System.out.println(itemDetails);
        ArrayList getItemId = new ArrayList();
        ObservableList getItemList = checkComboList.getCheckModel().getCheckedItems();
        for (int i = 1 ; i < getItemList.size() ; i++)
        {
            String item = getItemList.get(i).toString().trim();

            System.out.println(UtilsClass.getKeyFromValue(itemDetails,item));
            getItemId.add(UtilsClass.getKeyFromValue(itemDetails,item));
        }

        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("combo_name",textFieldComboNAme.getText());
            jsonObject.put("item_list",getItemId.toString());
            jsonObject.put("active",checkActive);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> submitComboItem = retrofitClient.addComboItem(jsonObject);
        submitComboItem.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful())
                {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                        }
                    });

                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success))
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                textFieldActualCost.setText("0");
                                textFieldComboNAme.setText("");
                                textFieldComboRate.setText("");
                                comboStatus.getSelectionModel().clearSelection();
                                checkComboList.getCheckModel().clearChecks();
                                checkComboList.getCheckModel().check(0);
                                vBoxLabel.getChildren().clear();
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
