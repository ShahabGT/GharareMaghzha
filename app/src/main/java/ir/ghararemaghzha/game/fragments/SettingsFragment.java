package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySettingsPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat music, next;
    private Context context;
    private FragmentActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundResource(R.color.light_background);
        v.setClickable(true);
        v.setFocusable(true);
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        context = getContext();
        activity = getActivity();

        music = findPreference("music");
        next = findPreference("next");

        if (music != null)
            music.setChecked(MySettingsPreference.getInstance(getContext()).getMusic());

        if (next != null)
            next.setChecked(MySettingsPreference.getInstance(getContext()).getAutoNext());
        if (next != null)
        next.setOnPreferenceChangeListener((preference, newValue) -> {
            MySettingsPreference.getInstance(context).setAutoNext((boolean)newValue);
            return true;
        });

        if (music != null)
            music.setOnPreferenceChangeListener((preference, newValue) -> {
                MySettingsPreference.getInstance(context).setMusic((boolean)newValue);
                return true;
            });
    }

}