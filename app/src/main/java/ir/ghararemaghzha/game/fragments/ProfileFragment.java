package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.ghararemaghzha.game.R;

public class ProfileFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        activity = getActivity();

        return v;
    }
}