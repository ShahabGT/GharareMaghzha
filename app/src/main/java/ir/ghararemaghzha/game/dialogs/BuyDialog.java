package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.BuyInterface;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RemoteDataSource;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.GeneralResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDialog extends Dialog {

    private FragmentActivity context;
    private String amount;
    private String plan;
    private BuyInterface buyInterface;
    private String influencerId="";
    private String influencerAmount="";


    public BuyDialog(@NonNull FragmentActivity context, String amount, String plan, BuyInterface buyInterface) {
        super(context);
        this.context = context;
        this.amount = amount;
        this.plan = plan;
        this.buyInterface=buyInterface;
    }

    private MaterialButton buy, cancel, giftcodeBtn, giftcodeCheck;
    private MaterialTextView tAmount, tTitle, tDiscountAmount, tDiscountTitle;
    private EditText giftcode;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_buy);
        init();
    }

    private void init() {
        buy = findViewById(R.id.buy_dialog_buy);
        cancel = findViewById(R.id.buy_dialog_cancel);
        giftcodeBtn = findViewById(R.id.buy_dialog_giftcode_btn);
        giftcodeCheck = findViewById(R.id.buy_dialog_giftcode_check);
        giftcode = findViewById(R.id.buy_dialog_giftcode);
        linearLayout = findViewById(R.id.buy_dialog_linear);

        tTitle = findViewById(R.id.buy_dialog_title);
        tDiscountTitle = findViewById(R.id.buy_dialog_discount_title);
        tAmount = findViewById(R.id.buy_dialog_amount);
        tDiscountAmount = findViewById(R.id.buy_dialog_discount_amount);


        tAmount.setText(context.getString(R.string.amount_model, Utils.moneySeparator(amount)));
        onClicks();
    }


    private void onClicks() {
        cancel.setOnClickListener(View ->
                dismiss());

        giftcodeBtn.setOnClickListener((View v) -> {

            v.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        });

        giftcodeCheck.setOnClickListener(View -> {

            if (!Utils.checkInternet(getContext())) {
                Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }
            if (giftcode.length() > 2) {

                searchInfluencer(giftcode.getText().toString());
            } else
                Toast.makeText(context, context.getString(R.string.buy_dialog_giftcode_invalid), Toast.LENGTH_SHORT).show();


        });

        buy.setOnClickListener(View -> {
            buyInterface.buy(plan,amount,influencerId,influencerAmount);
            dismiss();
        });

    }



    private void searchInfluencer(String code) {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context,true);
            return;
        }
        giftcode.setEnabled(false);
        giftcodeCheck.setEnabled(false);

        RetrofitClient.getInstance().getApi()
                .searchInfluencer("Bearer "+token,number,code)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeneralResponse> call,@NonNull  Response<GeneralResponse> response) {
                        if(response.isSuccessful() && response.body()!=null && response.body().getResult().equals("success")){
                            int percent = Integer.parseInt(response.body().getInfluencerAmount());
                            influencerId = response.body().getInfluencerId();
                            influencerAmount = response.body().getInfluencerAmount();
                            tTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            tAmount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                            tDiscountTitle.setVisibility(View.VISIBLE);
                            tDiscountTitle.setText(context.getString(R.string.buy_dialog_discount_text, percent));
                            amount = ((Integer.parseInt(amount) * (100 - percent)) / 100) + "";
                            tDiscountAmount.setVisibility(View.VISIBLE);
                            tDiscountAmount.setText(context.getString(R.string.amount_model,Utils.moneySeparator(amount)));
                        }else if (response.code() == 401) {
                            Utils.logout(context,true);
                        }else if (response.code()==404){
                            Toast.makeText(context, context.getString(R.string.buy_dialog_giftcode_invalid), Toast.LENGTH_SHORT).show();
                            giftcode.setEnabled(true);
                            giftcodeCheck.setEnabled(true);
                        }else{
                            Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                            giftcode.setEnabled(true);
                            giftcodeCheck.setEnabled(true);
                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<GeneralResponse> call,@NonNull  Throwable t) {
                        giftcode.setEnabled(true);
                        giftcodeCheck.setEnabled(true);
                        Toast.makeText(context, context.getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
