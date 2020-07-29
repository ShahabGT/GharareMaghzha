package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.models.MessageModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST;


public class MessagesFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;
    private Realm db;

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

    private void init(View v){
        db = Realm.getDefaultInstance();

        db.executeTransaction(realm -> {
            RealmResults<MessageModel> results = realm.where(MessageModel.class).equalTo("sender","admin").equalTo("read",0).findAll();
            results.setInt("read",1);
        });
        Intent intent = new Intent();
        intent.setAction(GHARAREHMAGHZHA_BROADCAST);
        context.sendBroadcast(intent);
    }
}