package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import ir.ghararemaghzha.game.R;

public class TimeDialog extends Dialog {

    private Context context;


    public TimeDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time);


        findViewById(R.id.alert_dialog_set).setOnClickListener(v->context.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS)));

    }
}
