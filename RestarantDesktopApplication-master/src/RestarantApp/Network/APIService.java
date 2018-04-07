package RestarantApp.Network;

import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @FormUrlEncoded
    @POST("category_add.php")
    Call<RequestAndResponseModel> sendCategoryDetails(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("category_view.php")
    Call<RequestAndResponseModel> viewCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("category_delete.php")
    Call<RequestAndResponseModel> deleteCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("category_edit.php")
    Call<RequestAndResponseModel> editCategory(@Field("x") JSONObject object);

    @POST("category_list.php")
    Call<RequestAndResponseModel> categoryList();

    @FormUrlEncoded
    @POST("item_add.php")
    Call<RequestAndResponseModel> sendItemDetails(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_view.php")
    Call<ItemListRequestAndResponseModel> itemView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_edit.php")
    Call<RequestAndResponseModel> itemEdit(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_delete.php")
    Call<RequestAndResponseModel> deleteItem(@Field("x") JSONObject object);

    @POST("tax_list.php")
    Call<RequestAndResponseModel> getTaxList();

    @FormUrlEncoded
    @POST("tax_add.php")
    Call<RequestAndResponseModel> addTax(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_view.php")
    Call<ItemListRequestAndResponseModel> taxView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_delete.php")
    Call<RequestAndResponseModel> deleteTax(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_edit.php")
    Call<RequestAndResponseModel> editTax(@Field("x") JSONObject object);

    @POST("item_list.php")
    Call<ItemListRequestAndResponseModel> getItemList();

    @FormUrlEncoded
    @POST("combo_add.php")
    Call<RequestAndResponseModel> addComboItem(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_view.php")
    Call<ItemListRequestAndResponseModel> comboView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_delete.php")
    Call<RequestAndResponseModel> deleteCombo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_edit.php")
    Call<RequestAndResponseModel> editCombo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("table_add.php")
    Call<RequestAndResponseModel> addTable(@Field("x") JSONObject object);

    @POST("table_list.php")
    Call<ItemListRequestAndResponseModel> getTableList();

    @FormUrlEncoded
    @POST("table_delete.php")
    Call<RequestAndResponseModel> deleteTable(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("table_edit.php")
    Call<RequestAndResponseModel> updateTable(@Field("x") JSONObject object);

}
