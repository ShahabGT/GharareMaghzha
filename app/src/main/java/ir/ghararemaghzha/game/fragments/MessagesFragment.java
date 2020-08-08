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


public class MessagesFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private Realm db;
    private MaterialTextView myCode, myName, empty;
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

        myName = v.findViewById(R.id.message_name);
        myCode = v.findViewById(R.id.message_code);
        myName.setText(MySharedPreference.getInstance(context).getUsername());
        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));
        empty = v.findViewById(R.id.message_empty);


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

        outgoingAdapter = new ChatAdapter(context, outgoingData, true);
        outgoingRecyclerView.setAdapter(outgoingAdapter);
        outgoingRecyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if ( bottom < oldBottom) {
                outgoingRecyclerView.post(() -> {
                    if(outgoingAdapter!=null)
                        outgoingRecyclerView.scrollToPosition(outgoingAdapter.getItemCount()-1);

                });
            }
        });

        incoming.setOnCheckedChangeListener((c, b) -> {
            if (b) {
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
                empty.setVisibility(View.GONE);

                chatLayout.setVisibility(View.VISIBLE);
                incomingRecyclerView.setVisibility(View.GONE);
            }
        });


        send = v.findViewById(R.id.chat_send);
        message = v.findViewById(R.id.chat_text);

        onClicks();
    }

    private void onClicks(){
        send.setOnClickListener(v->{
            String txt = message.getText().toString().trim();
            if(Utils.checkInternet(context)){
                if(!txt.isEmpty()) {
                    message.setText("");
                    String userId = MySharedPreference.getInstance(context).getUserId();
                    sendMessage(txt);
                    MessageModel model = new MessageModel();
                    model.setMessage(txt);
                    model.setReceiver("1");
                    model.setSender(userId);
                    model.setTitle("new");
                    model.setMessageId(getNextKey());
                    model.setDate(Utils.currentDate());
                    db.executeTransaction(realm1 -> realm1.insert(model));
                    //layoutManager.scrollToPosition(outgoingAdapter.getItemCount()-1);
                    outgoingRecyclerView.scrollToPosition(outgoingAdapter.getItemCount()-1);

                }
            }else{
                Toast.makeText(context, context.getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendMessage(String message) {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .sendMessage("Bearer "+token, number, message)
                .enqueue(new Callback<TimeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    private void getChatData() {
        String number = MySharedPreference.getInstance(context).getNumber();
        String token = MySharedPreference.getInstance(context).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(activity);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .getMessages("Bearer " + token, number)
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")
                                && response.body().getResult().equals("ok")) {

                            for (MessageModel model : response.body().getData())
                                db.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
                            //layoutManager.scrollToPosition(outgoingAdapter.getItemCount()-1);
                            outgoingRecyclerView.scrollToPosition(outgoingAdapter.getItemCount()-1);



                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {

                    }
                });
    }

    public int getNextKey() {
        try {
            Number number = db.where(MessageModel.class).max("messageId");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}