package RestarantApp.popup;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.menuClass.TaxListController;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

public class TaxActionPopup implements Initializable {
    JFXSnackbar jfxSnackbar;
    @FXML
    AnchorPane taxEditRoot,taxViewRoot;
    @FXML
    Label labTaxName,labTaxValue,labTaxCombine,labTaxStatus;
    @FXML
    ImageView viewMin,viewClose,editClose,editMin;
    @FXML
    TextField txtFiledName,txtFiledValue;
    HashMap<Integer,String> taxHashMap;
    @FXML
    ComboBox comboStatus,comboComb1,comboComb2;
    @FXML
    StackPane catRootPane;
    ItemListRequestAndResponseModel itemListRequestAndResponseModel;
    String comboValue1 = "",comboValue2 = "";
    ArrayList<String> comboTaxList = new ArrayList<>();
    @FXML
    CheckBox checkIsCombine;
    APIService retrofitClient;
    int checkActive;
    @FXML
    Button btnUpdate;

    public void btnUpdateTax(ActionEvent actionEvent) {

        updateTax();
    }

    private void updateTax() {



        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tax_id",itemListRequestAndResponseModel.getId());
            jsonObject.put("tax_name",txtFiledName.getText());
            jsonObject.put("value",txtFiledValue.getText());
            jsonObject.put("comp1",comboValue1);
            jsonObject.put("comp2",comboValue2);
            jsonObject.put("active",checkActive);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> addTaxCall = retrofitClient.editTax(jsonObject);
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


                                ItemListRequestAndResponseModel itemListRequestAndResponseModelList = new ItemListRequestAndResponseModel();
                                itemListRequestAndResponseModelList.setId(itemListRequestAndResponseModel.getId());
                                itemListRequestAndResponseModelList.setName(txtFiledName.getText());
                                if (checkActive == 0) {
                                    itemListRequestAndResponseModelList.setStatus("DeActive");
                                }else {itemListRequestAndResponseModelList.setStatus("Active");}

                                if (txtFiledValue.getText().equals(""))
                                {
                                    itemListRequestAndResponseModelList.setValue("-");
                                }else
                                {
                                    itemListRequestAndResponseModelList.setValue(txtFiledValue.getText());
                                }
                                String combine = taxHashMap.get(Integer.parseInt(comboValue1)) +" + "+ taxHashMap.get(Integer.parseInt(comboValue2));
                                itemListRequestAndResponseModelList.setCombine(combine);
                                TaxListController.data.set(TaxListController.data.indexOf(itemListRequestAndResponseModel),itemListRequestAndResponseModelList);

                                txtFiledValue.setText("");
                                txtFiledName.setText("");
                                comboComb2.getSelectionModel().select(0);
                                comboComb1.getSelectionModel().select(0);
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

    public void setItemsDetails(ItemListRequestAndResponseModel msg, String from)
    {
        if (from.equals("view")) {
            taxEditRoot.setVisible(false);
            taxViewRoot.setVisible(true);
            labTaxName.setText(msg.getName());
            labTaxValue.setText(msg.getValue());
            labTaxCombine.setText(msg.getCombine());
            labTaxStatus.setText(msg.getStatus());

        }else if (from.equals("edit"))
        {
            taxEditRoot.setVisible(true);
            taxViewRoot.setVisible(false);

            txtFiledName.setText(msg.getName());
            txtFiledValue.setText(msg.getValue());

        }
        this.itemListRequestAndResponseModel = msg;
        comboStatus.getItems().add("Status");
        comboStatus.getItems().add("Active");
        comboStatus.getItems().add("DeActive");

        comboStatus.getSelectionModel().select(msg.getStatus());
        if (msg.getValue().equals("-"))
        {
            checkIsCombine.setSelected(true);
            txtFiledValue.setDisable(true);
            comboComb1.setDisable(false);
            comboComb2.setDisable(false);
        }else
        {
            checkIsCombine.setSelected(false);
            txtFiledValue.setDisable(false);
            comboComb1.setDisable(true);
            comboComb2.setDisable(true);
        }

//        comboValue1 = msg.getCombine();
        checkIsCombine.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (checkIsCombine.isSelected())
                {
                    txtFiledValue.setDisable(true);
                    txtFiledValue.setText("");
                    comboComb1.setDisable(false);
                    comboComb2.setDisable(false);
                    comboValue1 = "";
                    comboValue2 = "";
                    comboComb1.getSelectionModel().select(0);
                    comboComb2.getSelectionModel().select(0);
                }else
                {
                    comboComb1.setDisable(true);
                    comboComb2.setDisable(true);
                    txtFiledValue.setDisable(false);

                }
                System.out.println(checkIsCombine.isSelected());
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

    }

    public void setTaxHashMap(HashMap<Integer,String> taxHashMap)
    {
        this.taxHashMap = taxHashMap;
        taxHashMap.put(-1,"Tax List");
//        comboComb1.getItems().addAll(taxHashMap.values());
//        comboComb2.getItems().addAll(taxHashMap.values());

        comboTaxList.addAll(taxHashMap.values());
        comboComb1.getItems().addAll(comboTaxList);
        comboComb2.getItems().addAll(comboTaxList);

        String combine = itemListRequestAndResponseModel.getCombine();

        if(combine.contains(" + ")) {
            String[] combineSplit = combine.split("\\+");
            String combine1 = combineSplit[0];
            String combine2 = combineSplit[1];
            if (!combine1.equals("null") && !combine2.equals("null")) {
                comboComb1.getSelectionModel().select(combine1);
                comboComb2.getSelectionModel().select(combine2);
            }

            comboValue1 = String.valueOf( UtilsClass.getKeyFromValue(taxHashMap,combine1.trim()));;
            comboValue2 = String.valueOf( UtilsClass.getKeyFromValue(taxHashMap,combine2.trim() ));;
        }else
        {
            comboComb1.getSelectionModel().select(0);
            comboComb2.getSelectionModel().select(0);
        }

        comboComb1.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                int index = comboComb1.getSelectionModel().getSelectedIndex();

                if (index != 0) {
                    comboComb2.getItems().remove(newValue);
                    comboComb2.getItems().clear();
                    for (int i=0;i<comboTaxList.size();i++)
                    {
                        if (!newValue.equals(comboTaxList.get(i)))
                        {
                            comboComb2.getItems().add(comboTaxList.get(i));
                        }
                    }


                    comboComb2.getSelectionModel().select(0);
                    comboValue1 =String.valueOf( UtilsClass.getKeyFromValue(taxHashMap,newValue));


                }

            }
        });

        comboComb2.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = comboComb2.getSelectionModel().getSelectedIndex();
                if (index != 0 && index != -1) {
//                    comboValue2 = comboTaxIdList.get(index);
                    comboValue2 =String.valueOf(UtilsClass.getKeyFromValue(taxHashMap,newValue));
                    System.out.println(comboValue2);
                }

            }
        });

    }
    public void viewCloseEvent(MouseEvent mouseEvent) {
        Stage stage = (Stage) viewClose.getScene().getWindow();
        stage.close();
    }

    public void viewMinEvent(MouseEvent mouseEvent) {

        Stage stage = (Stage) viewMin.getScene().getWindow();
        stage.setIconified(true);
    }

    public void editCloseEvent(MouseEvent mouseEvent) {
        Stage stage = (Stage) editClose.getScene().getWindow();
        stage.close();
    }


    public void editMinEvent(MouseEvent mouseEvent) {
        Stage stage = (Stage) editMin.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = TaxActionPopup.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
      /*  comboComb1.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                int index = comboComb1.getSelectionModel().getSelectedIndex();

                if (index != 0) {
                    comboComb2.getItems().remove(newValue);
                    comboComb2.getItems().clear();
                    for (int i=0;i<taxHashMap.values().size();i++)
                    {
                        if (!newValue.equals(taxHashMap.get(i)))
                        {
                            comboComb2.getItems().add(taxHashMap.get(i));
                        }
                    }


                    comboComb2.getSelectionModel().select(0);
//                    comboValue1 =String.valueOf( UtilsClass.getKeyFromValue(taxHashMap,newValue));


                }

            }
        });*/
       /* comboComb2.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = comboComb2.getSelectionModel().getSelectedIndex();
                if (index != 0 && index != -1) {
//                    comboValue2 = comboTaxIdList.get(index);
//                    comboValue2 =String.valueOf(UtilsClass.getKeyFromValue(taxHashMap,newValue));
                    System.out.println(comboValue2);
                }

            }
        });*/


        txtFiledValue.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    txtFiledValue.setText(oldValue);
                }
            }
        });
    }
}
