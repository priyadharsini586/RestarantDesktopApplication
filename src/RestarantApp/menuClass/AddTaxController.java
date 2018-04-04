package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddTaxController implements Initializable {
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    APIService retrofitClient;
    @FXML
    ComboBox comboTax1,comboTax2,comboStatus;
    ArrayList<String> comboTaxList = new ArrayList<>();
    ArrayList<String> comboTaxIdList = new ArrayList<>();
    @FXML
    TextField textFiledValue,textFieldName;
    @FXML
    CheckBox checkValue;
    int checkActive;
    String comboValue1 = "",comboValue2 = "";
    HashMap<Integer, String> getComboId = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = AddTaxController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        comboTax1.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                int index = comboTax1.getSelectionModel().getSelectedIndex();

                if (index != 0) {
                    comboTax2.getItems().remove(newValue);
                    comboTax2.getItems().clear();
                    for (int i=0;i<comboTaxList.size();i++)
                    {
                        if (!newValue.equals(comboTaxList.get(i)))
                        {
                            comboTax2.getItems().add(comboTaxList.get(i));
                        }
                    }


                    comboTax2.getSelectionModel().select(0);
                    comboValue1 =String.valueOf( UtilsClass.getKeyFromValue(getComboId,newValue));


                }

            }
        });
        comboTax2.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = comboTax2.getSelectionModel().getSelectedIndex();
                if (index != 0 && index != -1) {
//                    comboValue2 = comboTaxIdList.get(index);
                    comboValue2 =String.valueOf(UtilsClass.getKeyFromValue(getComboId,newValue));
                    System.out.println(comboValue2);
                }

            }
        });


        textFiledValue.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    textFiledValue.setText(oldValue);
                }
            }
        });
        comboStatus.getItems().add("Status");
        comboStatus.getItems().add("Active");
        comboStatus.getItems().add("DeActive");
        comboStatus.getSelectionModel().select(0);
        comboTax1.setDisable(true);
        comboTax2.setDisable(true);
        checkValue.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (checkValue.isSelected())
                {
                    textFiledValue.setDisable(true);
                    textFiledValue.setText("");
                    comboTax1.setDisable(false);
                    comboTax2.setDisable(false);
                }else
                {
                    comboTax1.setDisable(true);
                    comboTax2.setDisable(true);
                    textFiledValue.setDisable(false);

                }
                System.out.println(checkValue.isSelected());
            }
        });

        comboStatus.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = comboStatus.getSelectionModel().getSelectedIndex();
                if (newValue.equals("Active"))
                {
                    checkActive = 1;
                }else if (newValue.equals("DeActive"))
                {
                    checkActive = 0;
                }


            }
        });
       /* comboTax2.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                System.out.println("new select--->"+newValue);
                int index = comboTax2.getSelectionModel().getSelectedIndex();
                if (index != 0) {
                    comboTax1.getItems().remove(newValue);
                    comboTax1.getItems().clear();
                    for (int i=0;i<comboTaxList.size();i++)
                    {
                        if (!newValue.equals(comboTaxList.get(i)))
                        {
                            comboTax1.getItems().add(comboTaxList.get(i));
                        }
                    }

                }

            }
        });*/


        getTaxFiled();
    }

    private void getTaxFiled() {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> getTaxCall = retrofitClient.getTaxList();
        getTaxCall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();

                        comboTax1.getItems().add("Tax List");
                        comboTax2.getItems().add("Tax List");
                         comboTaxList.add("Tax List");
                        comboTaxIdList.add("-1");
                        for (int i=0;i < requestAndResponseModel.getList().size() ; i ++)
                        {
                            RequestAndResponseModel.list list = requestAndResponseModel.getList().get(i);
                            System.out.println(list.getName());
                            comboTax1.getItems().add(list.getName());
                            comboTax2.getItems().add(list.getName());
                            comboTaxList.add(list.getName());
                            comboTaxIdList.add(list.getId());

                            getComboId.put(Integer.valueOf(list.getId()),list.getName());
                        }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        comboTax1.getSelectionModel().select(0);
                        comboTax2.getSelectionModel().select(0);
                        }
                    });



                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void btnAddtax(ActionEvent actionEvent) {

        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        String value ;
        if (textFiledValue.getText().isEmpty())
        {
            value = "0";
        }else
        {
            value = textFiledValue.getText();
        }
        System.out.println(getComboId);
        try {
            jsonObject.put("tax_name",textFieldName.getText());
            jsonObject.put("value",value);
            jsonObject.put("comp1",comboValue1);
            jsonObject.put("comp2",comboValue2);
            jsonObject.put("active",checkActive);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> addTaxCall = retrofitClient.addTax(jsonObject);
        addTaxCall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
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
                                System.out.println(requestAndResponseModel.getSuccessCode());
                        textFiledValue.setText("");
                        textFieldName.setText("");
                        comboTax1.getSelectionModel().select(0);
                        comboTax2.getSelectionModel().select(0);
                        comboStatus.getSelectionModel().select(0);


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


}
