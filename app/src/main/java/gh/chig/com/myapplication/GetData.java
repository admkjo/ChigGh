package gh.chig.com.myapplication;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetData {
    @GET("/api/v1/clients")
    Call<List<RetroUsers>> getAllUsers();
}
