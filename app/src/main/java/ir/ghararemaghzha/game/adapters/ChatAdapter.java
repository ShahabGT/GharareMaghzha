package ir.ghararemaghzha.game.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.DateConverter;
import ir.ghararemaghzha.game.models.MessageModel;

public class ChatAdapter extends RealmRecyclerViewAdapter<MessageModel,ChatAdapter.ViewHolder> {

    private Context context;

    public ChatAdapter(Context context, @Nullable OrderedRealmCollection<MessageModel> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context=context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MessageModel model = getItem(position);
        if(model!=null)
        try {
        //    if (model.getSender().equals("1")||model.getSender().equals("admin")) {
            if (model.getSender().equals("1")||model.getSender().equals("support")) {
                holder.main.setGravity(Gravity.LEFT);
                holder.message.setBackground(context.getResources().getDrawable(R.drawable.shape_other));
            } else {
                holder.main.setGravity(Gravity.RIGHT);
                holder.message.setBackground(context.getResources().getDrawable(R.drawable.shape_me));
            }

            holder.message.setText(model.getMessage());
            String date = model.getDate();
            DateConverter dateConverter = new DateConverter();

            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.date.setText(date.substring(11,16));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView message,date;
        private LinearLayout main;

        public ViewHolder(@NonNull View v) {
            super(v);
            message = v.findViewById(R.id.chat_row_message);
            date = v.findViewById(R.id.chat_row_date);
            main = v.findViewById(R.id.chat_row_main_linear);
        }
    }
}
