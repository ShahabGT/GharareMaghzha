package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ir.ghararemaghzha.game.R;

public class RulesDialog extends Dialog {

    private MaterialTextView text;
    private Context context;


    public RulesDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rules);
        text = findViewById(R.id.rules_text);
        text.setText(getText());
        findViewById(R.id.rules_close).setOnClickListener(v->dismiss());
    }

    private String getText(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.rules);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder text = new StringBuilder();
        String line;
        try {
            while ( (line = reader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return text.toString();
    }
}
