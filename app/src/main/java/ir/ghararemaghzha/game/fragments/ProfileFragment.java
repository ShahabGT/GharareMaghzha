package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.content.Intent;
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
import ir.ghararemaghzha.game.activities.ProfileActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.models.QuestionModel;

public class ProfileFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView myScore, totalQuestions, remainingQuestion, remainingTime, remainingTimeTitle, myCode, myName;
    private MaterialCardView buy, edit;

    private Realm db;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    private void init(View v) {
        db = Realm.getDefaultInstance();
        myScore = v.findViewById(R.id.profile_score);
        totalQuestions = v.findViewById(R.id.profile_questions);
        remainingQuestion = v.findViewById(R.id.profile_remaining);
        remainingTime = v.findViewById(R.id.profile_time);
        remainingTimeTitle = v.findViewById(R.id.profile_time_title);
        myCode = v.findViewById(R.id.profile_code);
        myName = v.findViewById(R.id.profile_name);

        buy = v.findViewById(R.id.profile_buy_card);
        edit = v.findViewById(R.id.profile_edit_card);

        myName.setText(MySharedPreference.getInstance(context).getUsername());
        myCode.setText(context.getString(R.string.profile_code, MySharedPreference.getInstance(context).getUserCode()));
        myScore.setText(MySharedPreference.getInstance(context).getScore());
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());

        if (passed < 0) {
            remainingTimeTitle.setText(context.getString(R.string.profile_time_card_tostart));
            remainingTime.setText(context.getString(R.string.profile_time,
                    String.valueOf(Math.abs(passed))));

        } else {
            remainingTimeTitle.setText(context.getString(R.string.profile_time_card));
            if (passed == 0)
                remainingTime.setText(context.getString(R.string.profile_time_lastday));
            if (passed == 10)
                remainingTime.setText(context.getString(R.string.profile_time_end));
            else
                remainingTime.setText(context.getString(R.string.profile_time, String.valueOf(10 - passed)));

        }


        int total = db.where(QuestionModel.class).findAll().size();
        totalQuestions.setText(String.valueOf(db.where(QuestionModel.class).findAll().size()));
        int remain = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        remainingQuestion.setText(String.valueOf(db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size()));


        onClicks();
    }

    private void onClicks() {
        buy.setOnClickListener(v -> {
            ImageViewCompat.setImageTintList(MainActivity.buy, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            ImageViewCompat.setImageTintList(MainActivity.profile, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.messages, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            ImageViewCompat.setImageTintList(MainActivity.highscore, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                    .replace(R.id.main_container, new BuyFragment())
                    .commit();
            MainActivity.whichFragment = 4;

        });
        edit.setOnClickListener(v -> startActivity(new Intent(context, ProfileActivity.class)));
    }
}