package ir.ghararemaghzha.game.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.DateConverter;
import ir.ghararemaghzha.game.models.MessageModel;



public class IncomingDialog extends Dialog {

    private MessageModel model;
    private MaterialTextView title,body,date;
    public IncomingDialog(@NonNull Context context, MessageModel model) {
        super(context);
        this.model = model;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_incoming);

        findViewById(R.id.incoming_back).setOnClickListener(v->dismiss());

        title = findViewById(R.id.incoming_title);
        body = findViewById(R.id.incoming_body);
        date = findViewById(R.id.incoming_date);

        title.setText(model.getTitle());
        body.setText(model.getMessage());
        String d = model.getDate();
        DateConverter dateConverter = new DateConverter();

        dateConverter.gregorianToPersian(Integer.parseInt(d.substring(0,4)),Integer.parseInt(d.substring(5,7)),Integer.parseInt(d.substring(8,10)));
        date.setText(dateConverter.getYear()+"/"+dateConverter.getMonth()+"/"+dateConverter.getDay()+" "+d.substring(11, 16));
    }
}
