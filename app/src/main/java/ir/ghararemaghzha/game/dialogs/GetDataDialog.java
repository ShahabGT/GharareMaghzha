package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import ir.ghararemaghzha.game.R;

public class GetDataDialog extends Dialog {


    public GetDataDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_getdata);
    }
}
