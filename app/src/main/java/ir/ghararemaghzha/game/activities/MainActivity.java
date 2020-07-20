package ir.ghararemaghzha.game.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.fragments.LoginFragment;
import ir.ghararemaghzha.game.fragments.VerifyFragment;
import ir.ghararemaghzha.game.models.TimeResponse;
import ir.ghararemaghzha.game.models.VerifyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TimeDialog timeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container,new VerifyFragment())
                .commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        checkTime();
        verify();
    }

    private void verify() {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(MainActivity.this);
            return;
        }

        RetrofitClient.getInstance().getApi()
                .verify("Bearer "+token,number)
                .enqueue(new Callback<VerifyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VerifyResponse> call,@NonNull  Response<VerifyResponse> response) {
                        if(response.isSuccessful() && response.body()!=null && response.body().getResult().equals("success")){
                            int myVersion = Utils.getVersionCode(MainActivity.this);
                            int newVersion = Integer.parseInt(response.body().getVersion());
                            if(newVersion>myVersion){
                                if(response.body().getVersionEssential().equals("1")){
                                    Toast.makeText(MainActivity.this, "version "+newVersion+" available ESSENTIAL", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "version "+newVersion+" available", Toast.LENGTH_SHORT).show();

                                }


                            }
                        }else if(response.code()==401){
                            Utils.logout(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerifyResponse> call,@NonNull  Throwable t) {

                    }
                });
    }

    private void checkTime() {
        if (timeDialog != null)
            timeDialog.dismiss();
        RetrofitClient.getInstance().getApi()
                .getServerTime()
                .enqueue(new Callback<TimeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {
                            if (!Utils.isTimeAcceptable(response.body().getTime()))
                                timeDialog = Utils.showTimeError(MainActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {

                    }
                });

    }
}