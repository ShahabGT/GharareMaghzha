package ir.ghararemaghzha.game.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

public class SupportActivity extends AppCompatActivity {

    private Realm db;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ChatAdapter adapter;
    private EditText message;
    private ImageView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        init();
    }

    private void init(){
        db = Realm.getDefaultInstance();
        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        db.executeTransaction(realm -> {
            RealmResults<MessageModel> results = realm.where(MessageModel.class).notEqualTo("sender", "admin").equalTo("read", 0).findAll();
            results.setInt("read", 1);
            sendBroadcast(intent);
        });


        recyclerView = findViewById(R.id.chat_recycler);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(this, db.where(MessageModel.class).notEqualTo("sender", "admin").sort("date", Sort.DESCENDING).findAll(), true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnLayoutChangeListener((v1, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.post(() -> {
                    if (adapter != null)
                        recyclerView.scrollToPosition(0);

                });
            }
        });

            getChatData();


        send = findViewById(R.id.chat_send);
        message = findViewById(R.id.chat_text);

        onClicks();

    }

    private void onClicks(){
        findViewById(R.id.chat_close).setOnClickListener(v->onBackPressed());

        send.setOnClickListener(v -> {
            String txt = message.getText().toString().trim();
            if (!txt.isEmpty()) {
                message.setText("");
                String userId = MySharedPreference.getInstance(SupportActivity.this).getUserId();
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
                recyclerView.scrollToPosition(0);
            }
        });
    }
    private void sendMessage(String message,int key) {
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this,true);
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
                        }else if(response.code()==401){
                            Utils.logout(SupportActivity.this,true);
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
        String number = MySharedPreference.getInstance(this).getNumber();
        String token = MySharedPreference.getInstance(this).getAccessToken();
        String lastUpdate = MySharedPreference.getInstance(this).getLastUpdateChat();
        if (number.isEmpty() || token.isEmpty()) {
            Utils.logout(this,true);
            return;
        }
        RetrofitClient.getInstance().getApi()
                .getMessages("Bearer " + token, number,lastUpdate)
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getResult().equals("success")) {

                            if(response.body().getMessage().equals("ok")) {

                                for (MessageModel model : response.body().getData()) {
                                    model.setStat(1);
                                    model.setRead(1);
                                    model.setMessageId(Utils.getNextKey(db));
                                    db.executeTransaction(realm -> realm.insertOrUpdate(model));
                                }
                                recyclerView.scrollToPosition(0);

                            }

                            MySharedPreference.getInstance(SupportActivity.this).setLastUpdateChat(Utils.currentDate());


                        }else if (response.code() == 401) {
                            Utils.logout(SupportActivity.this,true);
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