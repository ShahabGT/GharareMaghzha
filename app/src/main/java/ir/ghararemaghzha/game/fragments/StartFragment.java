package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.activities.QuestionActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.models.QuestionModel;


public class StartFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView info;
    private MaterialCardView profile, highscore, start;

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

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    private void updateInfo() {
        //  int remain = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());
        if (passed >= 0 && passed < 10)
            info.setText(context.getString(R.string.start_info, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size())));
        else if (passed < 0)
            info.setText(context.getString(R.string.start_info, String.valueOf(0)));
        else {
            info.setText(context.getString(R.string.start_info_passed));
            start.setEnabled(false);
        }
        Utils.updateServerQuestions(activity, String.valueOf(db.where(QuestionModel.class).equalTo("visible", true).findAll().size()));
    }

    private void init(View v) {
        ((MaterialTextView)activity.findViewById(R.id.toolbar_title)).setText(R.string.start_title);


        db = Realm.getDefaultInstance();

        info = v.findViewById(R.id.start_info);
        profile = v.findViewById(R.id.start_profile_card);
        highscore = v.findViewById(R.id.start_highscore_card);
        start = v.findViewById(R.id.start_start_card);




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
        start.setOnClickListener(v -> {
            int size = db.where(QuestionModel.class).equalTo("visible", true).findAll().size();
            if (size > 0)
                startActivity(new Intent(activity, QuestionActivity.class));
            else
                Toast.makeText(context, context.getString(R.string.general_noquestions), Toast.LENGTH_SHORT).show();
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(db!=null)db.close();
    }

}