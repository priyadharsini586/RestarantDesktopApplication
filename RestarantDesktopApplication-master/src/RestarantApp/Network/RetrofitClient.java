package RestarantApp.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    public static Retrofit retrofit = null;
    public static final String BASE_URL = "https://prawnandcrab.com/webservice/";
//public static final String BASE_URL = "http://192.168.1.2/prawnandcrab/webservice/";

    public static Retrofit getClient()
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
