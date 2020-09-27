package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.textview.MaterialTextView;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySettingsPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context context;
    private FragmentActivity activity;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.settings_title);

        if (v != null) {
            v.setBackgroundResource(R.color.light_background);
            v.setClickable(true);
            v.setFocusable(true);
        }
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        context = getContext();
        activity = getActivity();

        SwitchPreferenceCompat music = findPreference("music");
        SwitchPreferenceCompat next = findPreference("next");
        SwitchPreferenceCompat notification = findPreference("notification");

        if (music != null) {
            music.setChecked(MySettingsPreference.getInstance(context).getMusic());
            music.setOnPreferenceChangeListener((preference, newValue) -> {
                MySettingsPreference.getInstance(context).setMusic((boolean) newValue);
                return true;
            });
        }

        if (notification != null) {
            notification.setChecked(MySettingsPreference.getInstance(context).getNotification());
            notification.setOnPreferenceChangeListener((preference, newValue) -> {
                MySettingsPreference.getInstance(context).setNotification((boolean) newValue);
                return true;
            });
        }

        if (next != null) {
            next.setChecked(MySettingsPreference.getInstance(context).getAutoNext());
            next.setOnPreferenceChangeListener((preference, newValue) -> {
                MySettingsPreference.getInstance(context).setAutoNext((boolean) newValue);
                return true;
            });
        }


    }

}