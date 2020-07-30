package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.models.QuestionModel;


public class StartFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView info, myCode, myName;
    private MaterialCardView profile, highscore,start;

    private Realm db;

    public StartFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    private void init(View v) {
        db = Realm.getDefaultInstance();

        info = v.findViewById(R.id.start_info);
        myName = v.findViewById(R.id.start_name);
        myCode = v.findViewById(R.id.start_code);
        profile = v.findViewById(R.id.start_profile_card);
        highscore = v.findViewById(R.id.start_highscore_card);
        start = v.findViewById(R.id.start_start_card);

        myName.setText(MySharedPreference.getInstance(context).getUsername());
        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));
        //  int remain = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());
        if (passed >= 0 && passed < 10)
            info.setText(context.getString(R.string.start_info, String.valueOf(db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size())));
        else if(passed<0)
            info.setText(context.getString(R.string.start_info, String.valueOf(db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size())));
        else {
            info.setText(context.getString(R.string.start_info_passed));
            start.setEnabled(false);
        }



        onClicks();
    }

    private void onClicks() {
        profile.setOnClickListener(v -> {
            ImageViewCompat.setImageTintList(MainActivity.buy, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.profile, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            ImageViewCompat.setImageTintList(MainActivity.messages, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.highscore, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.main_container, new ProfileFragment())
                    .commit();
            MainActivity.whichFragment = 1;
        });
        highscore.setOnClickListener(v -> {
            ImageViewCompat.setImageTintList(MainActivity.buy, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.profile, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.messages, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.highscore, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.main_container, new HighscoreFragment())
                    .commit();
            MainActivity.whichFragment = 3;
        });

    }
}