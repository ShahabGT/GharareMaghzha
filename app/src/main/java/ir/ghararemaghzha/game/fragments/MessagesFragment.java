package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.ChatAdapter;
import ir.ghararemaghzha.game.adapters.IncomingAdapter;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.data.RetrofitClient;
import ir.ghararemaghzha.game.models.ChatResponse;
import ir.ghararemaghzha.game.models.MessageModel;
import ir.ghararemaghzha.game.models.TimeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;
import static ir.ghararemaghzha.game.classes.Utils.getNextKey;


public class MessagesFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private Realm db;
    private MaterialTextView myCode, myName, empty, title;
    private MaterialRadioButton incoming, outgoing;
    private RealmResults<MessageModel> incomingData;
    private RealmResults<MessageModel> outgoingData;
    private RecyclerView incomingRecyclerView;
    private RecyclerView outgoingRecyclerView;
    private LinearLayoutManager layoutManager;
    private IncomingAdapter incomingAdapter;
    private ChatAdapter outgoingAdapter;

    private View chatLayout;
    private EditText message;
    private ImageView send;

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        context = getContext();
        activity = getActivity();
        init(v);
        return v;
    }

    private void init(View v) {
        db = Realm.getDefaultInstance();

        db.executeTransaction(realm -> {
            RealmResults<MessageModel> results = realm.where(MessageModel.class).equalTo("sender", "admin").equalTo("read", 0).findAll();
            results.setInt("read", 1);
        });
        incomingData = db.where(MessageModel.class).equalTo("sender", "admin").sort("date", Sort.DESCENDING).findAll();
        outgoingData = db.where(MessageModel.class).notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll();

        if (outgoingData == null || outgoingData.isEmpty()) {
            getChatData();
        }
        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        context.sendBroadcast(intent);

        chatLayout = v.findViewById(R.id.message_chat_layout);

//        myName = v.findViewById(R.id.message_name);
//        myCode = v.findViewById(R.id.message_code);
//        myName.setText(MySharedPreference.getInstance(context).getUsername());
//        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));
        empty = v.findViewById(R.id.message_empty);
        title = v.findViewById(R.id.message_title);

        incoming = v.findViewById(R.id.message_radio_incoming);
        outgoing = v.findViewById(R.id.message_radio_outgoing);
        incomingRecyclerView = v.findViewById(R.id.message_recycler);
        outgoingRecyclerView = v.findViewById(R.id.chat_recycler);
        incomingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setReverseLayout(true);
        outgoingRecyclerView.setLayoutManager(layoutManager);
        if (incomingData.isEmpty())
            empty.setVisibility(View.VISIBLE);
        else {
            incomingAdapter = new IncomingAdapter(context, incomingData);
            incomingRecyclerView.setAdapter(incomingAdapter);
            incomingAdapter.notifyDataSetChanged();
        }

        outgoingAdapter = new ChatAdapter(activity, db.where(MessageModel.class).notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll(), true);
        outgoingRecyclerView.setAdapter(outgoingAdapter);
        outgoingRecyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                outgoingRecyclerView.post(() -> {
                    if (outgoingAdapter != null)
                        outgoingRecyclerView.scrollToPosition(0);

                });
            }
        });

        incoming.setOnCheckedChangeListener((c, b) -> {
            if (b) {
                Utils.hideKeyboard(activity);
                title.setText(context.getString(R.string.message_incoming));
                incomingRecyclerView.setVisibility(View.VISIBLE);
                chatLayout.setVisibility(View.GONE);
                if (incomingData.isEmpty())
                    empty.setVisibility(View.VISIBLE);
                else {
                    incomingAdapter = new IncomingAdapter(context, incomingData);
                    incomingRecyclerView.setAdapter(incomingAdapter);
                    incomingAdapter.notifyDataSetChanged();
                }
            }
        });
        outgoing.setOnCheckedChangeListener((c, b) -> {
            if (b) {
                title.setText(context.getString(R.string.message_outgoing));
                db.executeTransaction(realm -> {
                    RealmResults<MessageModel> results = realm.where(MessageModel.class).notEqualTo("sender", "admin").equalTo("read", 0).findAll();
                    results.setInt("read", 1);
                    context.sendBroadcast(intent);
                });
                empty.setVisibility(View.GONE);
                chatLayout.setVisibility(View.VISIBLE);
                incomingRecyclerView.setVisibility(View.GONE);
            }
        });


        send = v.findViewById(R.id.chat_send);
        message = v.findViewById(R.id.chat_text);

        onClicks();
       // uploadMessages();
    }

    private void onClicks() {
        send.setOnClickListener(v -> {
            String txt = message.getText().toString().trim();
                if (!txt.isEmpty()) {
                    message.setText("");
                    String userId = MySharedPreference.getInstance(context).getUserId();
                    MessageModel model = new MessageModel();
                    model.setStat(0);
                    model.setMessage(txt);
                    model.setReceiver("1");
                    model.setRead(1);
                    model.setSender(userId);
                    model.setTitle("new");
                    int key= getNextKey(db);
                    model.setMessageId(getNextKey(db));
                    model.setDate(Utils.currentDate());
                    db.executeTransaction(realm1 -> realm1.insert(model));
                    sendMessage(txt,key);
                    outgoingRecyclerView.scrollToPosition(0);
                }
        });
    }

    private void uploadMessages(){
        String userId = MySharedPreference.getInstance(context).getUserId();

        RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("sender",userId).equalTo("stat",-1).findAll();
        for(MessageModel model : models)
            sendMessage(model.getMessage(),model.getMessageId());
    }

    private void sendMessage(String message,int key) {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity,true);
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
                        }if(response.code()==401){
                            Utils.logout(activity,true);

                        }else{
                            db.beginTransaction();
                            RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("messageId", key).findAll();
                            models.first().setStat(-1);
                            db.commitTransaction();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {
                        db.beginTransaction();
                        RealmResults<MessageModel> models = db.where(MessageModel.class).equalTo("messageId", key).findAll();
                        models.first().setStat(-1);
                        db.commitTransaction();
                    }
                });
    }

    private void getChatData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity,true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .getMessages("Bearer " + token, number)
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")
                                && response.body().getMessage().equals("ok")) {

                            for (MessageModel model : response.body().getData()) {
                                model.setStat(1);
                                model.setRead(1);
                                model.setMessageId(Utils.getNextKey(db));
                                db.executeTransaction(realm -> realm.insertOrUpdate(model));
                            }


                        }else if (response.code() == 401) {
                            Utils.logout(activity,true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(db!=null)db.close();
    }


}