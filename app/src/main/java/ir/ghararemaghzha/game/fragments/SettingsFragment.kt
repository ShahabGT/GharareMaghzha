package ir.ghararemaghzha.game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.textview.MaterialTextView
import ir.ghararemaghzha.game.R
import ir.ghararemaghzha.game.classes.MySettingsPreference

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        requireActivity().findViewById<MaterialTextView>(R.id.toolbar_title).setText(R.string.settings_title)
        
        v?.setBackgroundResource(R.color.light_background)
        v?.isClickable = true
        v?.isFocusable = true
        
        return v
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val ctx = requireContext()

        val music = findPreference<SwitchPreferenceCompat>("music")
        val next = findPreference<SwitchPreferenceCompat>("next")
        val notification = findPreference<SwitchPreferenceCompat>("notification")

        if (music != null) {
            music.isChecked = MySettingsPreference.getInstance(ctx).getMusic()
            music.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                MySettingsPreference.getInstance(ctx).setMusic(newValue as Boolean)
                true
            }
        }

        if (notification != null) {
            notification.isChecked = MySettingsPreference.getInstance(ctx).getNotification()
            notification.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                MySettingsPreference.getInstance(ctx).setNotification(newValue as Boolean)
                true
            }
        }

        if (next != null) {
            next.isChecked = MySettingsPreference.getInstance(ctx).getAutoNext()
            next.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                MySettingsPreference.getInstance(ctx).setAutoNext(newValue as Boolean)
                true
            }
        }
    }
}