package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.ghararemaghzha.game.R;


public class HighscoreFragment extends Fragment {
    private Context context;
    private FragmentActivity activity;


    public HighscoreFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_highscore, container, false);
        context = getContext();
        activity = getActivity();

        return v;
    }
}