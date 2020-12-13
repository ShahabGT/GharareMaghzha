package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.RetryInterface;
import ir.ghararemaghzha.game.classes.Utils;

public class NoInternetDialog extends Dialog {
    private final Context context;
    private final RetryInterface retryInterface;

    public NoInternetDialog(@NonNull Context context, RetryInterface retryInterface) {
        super(context);
        this.context = context;
        this.retryInterface = retryInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nointernet);
        findViewById(R.id.alert_dialog_set).setOnClickListener(v -> {
            if (Utils.checkInternet(context)) {
                dismiss();
                retryInterface.retry();
            } else {
                Toast.makeText(context, R.string.general_internet_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
