package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTableController implements Initializable {

    @FXML
    TextField textFieldName;
    @FXML
    ComboBox tableStatus;
    int checkActive;
    APIService retrofitClient;
    @FXML
    ProgressIndicator progressCategory;
    @FXML
    StackPane catRootPane;
    JFXSnackbar jfxSnackbar;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableStatus.getItems().add("Active");
        tableStatus.getItems().add("DeActive");
        tableStatus.getSelectionModel().select(0);
        progressCategory.setVisible(false);
        progressCategory.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        String css = AddTaxController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        tableStatus.valueProperty().addListener(new ChangeListener() {
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
    }


    public void btnAddTableAction(ActionEvent actionEvent) {

        sendTableDetails();
    }

    private void sendTableDetails() {

        progressCategory.setVisible(true);
        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject tableJSon = new JSONObject();
        try {
            tableJSon.put("table_nam",textFieldName.getText());
            tableJSon.put("active",checkActive);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> getTaxCall = retrofitClient.addTable(tableJSon);
        getTaxCall.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    progressCategory.setVisible(false);

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
                                textFieldName.setText("");
                                tableStatus.getSelectionModel().select(0);


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
