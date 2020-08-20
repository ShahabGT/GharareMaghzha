package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.IncomingAdapter;
import ir.ghararemaghzha.game.models.MessageModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;


public class MessagesFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private Realm db;
    private MaterialTextView myCode, myName, empty, title;
    private RealmResults<MessageModel> data;
    private RecyclerView recyclerView;
    private IncomingAdapter adapter;

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
        data = db.where(MessageModel.class).equalTo("sender", "admin").sort("date", Sort.DESCENDING).findAll();

        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        context.sendBroadcast(intent);


//        myName = v.findViewById(R.id.message_name);
//        myCode = v.findViewById(R.id.message_code);
//        myName.setText(MySharedPreference.getInstance(context).getUsername());
//        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));
        empty = v.findViewById(R.id.message_empty);
        title = v.findViewById(R.id.message_title);


        recyclerView = v.findViewById(R.id.message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (data.isEmpty())
            empty.setVisibility(View.VISIBLE);
        else {
            adapter = new IncomingAdapter(context, data);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }


}