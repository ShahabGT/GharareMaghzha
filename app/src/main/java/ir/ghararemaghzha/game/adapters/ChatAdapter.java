package ir.ghararemaghzha.game.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.DateConverter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.MessageModel;
import ir.ghararemaghzha.game.models.TimeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAdapter extends RealmRecyclerViewAdapter<MessageModel, RecyclerView.ViewHolder> {

    private FragmentActivity context;
    private final static int TYPE_ME = 1;
    private final static int TYPE_OTHER = 2;

    public ChatAdapter(FragmentActivity context, @Nullable OrderedRealmCollection<MessageModel> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        //return super.getItemId(position);
        return position;
      //  return getItem(position).getMessageId();
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.requireNonNull(getItem(position)).getSender().equals("1"))
            return TYPE_OTHER;
        else
            return TYPE_ME;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME)
            return new MeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_me, parent, false));
        else
            return new OtherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_other, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MessageModel model = getItem(position);
        if (model != null)
            try {
                if (getItemViewType(position) == TYPE_ME) {
                    MeViewHolder h = (MeViewHolder) holder;
                    h.message.setText(model.getMessage());
                    String date = model.getDate();
                    DateConverter dateConverter = new DateConverter();

                    dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
                    h.date.setText(date.substring(11, 16));

                    switch (model.getStat()) {
                        case 0:
                            h.stat.setImageResource(R.drawable.vector_sending);
                            break;
                        case 1:
                            h.stat.setImageResource(R.drawable.vector_sent);
                            break;
                        case -1:
                            h.stat.setImageResource(R.drawable.vector_failed);
                            break;
                    }

                    h.stat.setOnClickListener(v->{
                        if(model.getStat()==-1){
                            h.stat.setImageResource(R.drawable.vector_sending);
                            sendMessage(model.getMessage(),model.getMessageId(),position);
                        }
                    });


                } else {
                    OtherViewHolder h = (OtherViewHolder) holder;
                    h.message.setText(model.getMessage());
                    String date = model.getDate();
                    DateConverter dateConverter = new DateConverter();

                    dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
                    h.date.setText(date.substring(11, 16));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final MessageModel model = getItem(position);
//        if(model!=null)
//        try {
//        //    if (model.getSender().equals("1")||model.getSender().equals("admin")) {
//            if (model.getSender().equals("1")||model.getSender().equals("support")) {
//                holder.main.setGravity(Gravity.LEFT);
//                holder.message.setBackground(context.getResources().getDrawable(R.drawable.shape_other));
//            } else {
//                holder.main.setGravity(Gravity.RIGHT);
//                holder.message.setBackground(context.getResources().getDrawable(R.drawable.shape_me));
//            }
//
//            holder.message.setText(model.getMessage());
//            String date = model.getDate();
//            DateConverter dateConverter = new DateConverter();
//
//            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
//            holder.date.setText(date.substring(11,16));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    static class OtherViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView message, date;

        public OtherViewHolder(@NonNull View v) {
            super(v);
            message = v.findViewById(R.id.chat_row_message);
            date = v.findViewById(R.id.chat_row_date);
        }
    }

    static class MeViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView message, date;
        private ImageView stat;

        public MeViewHolder(@NonNull View v) {
            super(v);
            message = v.findViewById(R.id.chat_row_message);
            date = v.findViewById(R.id.chat_row_date);
            stat = v.findViewById(R.id.chat_row_stat);
        }
    }

    private void sendMessage(String message,int key,int pos) {
        Realm db = Realm.getDefaultInstance();
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(context);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendMessage("Bearer " + token, number, message)
                .enqueue(new Callback<TimeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {
                        if(response.isSuccessful() && response.body()!=null && response.body().getResult().equals("success")) {
                            db.beginTransaction();
                            RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("messageId", key).findAll();
                            models.first().setStat(1);
                            db.commitTransaction();

                        }else{
                            db.beginTransaction();
                            RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("messageId", key).findAll();
                            models.first().setStat(-1);
                            db.commitTransaction();
                        }
                        notifyItemChanged(pos);

                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {
                        db.beginTransaction();
                        RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("messageId", key).findAll();
                        models.first().setStat(-1);
                        db.commitTransaction();
                        notifyItemChanged(pos);

                    }
                });
    }

}
