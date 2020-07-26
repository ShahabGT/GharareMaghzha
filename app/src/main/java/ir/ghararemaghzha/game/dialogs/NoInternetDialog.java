package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.RetryInterface;
import ir.ghararemaghzha.game.classes.Utils;

public class NoInternetDialog extends Dialog {
    private Context context;
    private RetryInterface retry;

    public NoInternetDialog(@NonNull Context context, RetryInterface retry) {
        super(context);
        this.context=context;
        this.retry=retry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nointernet);
        findViewById(R.id.alert_dialog_set).setOnClickListener(v->{
            if(Utils.checkInternet(context)) {
                dismiss();
                retry.retry();
            }else{
                Toast.makeText(context, context.getString(R.string.general_internet_error), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
