package ir.ghararemaghzha.game.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.atomic.AtomicReference;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;


public class VerifyFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;

    public VerifyFragment() { }
    private BroadcastReceiver rec = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getExtras()!=null) {
                String code = intent.getExtras().getString("code");
                if (code != null && code.length() == 6) {

                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_verify, container, false);
        context = getContext();
        activity = getActivity();
        context.registerReceiver(rec,new IntentFilter("codeReceived"));

        init(v);

        return v;
    }

    private void init(View v){

    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(rec);
        super.onDestroy();
    }
}