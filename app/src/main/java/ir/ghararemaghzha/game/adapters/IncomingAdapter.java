package ir.ghararemaghzha.game.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.DateConverter;
import ir.ghararemaghzha.game.dialogs.IncomingDialog;
import ir.ghararemaghzha.game.dialogs.TimeDialog;
import ir.ghararemaghzha.game.models.MessageModel;

public class IncomingAdapter extends RecyclerView.Adapter<IncomingAdapter.ViewHolder> {

    private Context context;
    private RealmResults<MessageModel> data;
    private NavController navController;
    public IncomingAdapter(Context context, NavController navController, RealmResults<MessageModel> data) {
        this.context=context;
        this.data=data;
        this.navController=navController;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_incoming,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MessageModel model = data.get(position);
        if(model!=null) {
            h.title.setText(model.getTitle());
            h.body.setText(model.getMessage());
            String date = model.getDate();
            DateConverter dateConverter = new DateConverter();

            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            h.date.setText(dateConverter.getMonth()+"/"+dateConverter.getDay());

            h.itemView.setOnClickListener(v->{
                IncomingDialog dialog = new IncomingDialog(context,navController,model);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            });

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView title,body,date;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.incoming_title);
            body = v.findViewById(R.id.incoming_body);
            date = v.findViewById(R.id.incoming_date);
        }
    }
}
