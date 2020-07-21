package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ir.ghararemaghzha.game.R;


public class MessagesFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        context = getContext();
        activity = getActivity();

        return v;
    }
}