package ir.ghararemaghzha.game.fragments;

import android.content.Context;
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

    public VerifyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_verify, container, false);
        context = getContext();
        activity = getActivity();

        return v;
    }



}