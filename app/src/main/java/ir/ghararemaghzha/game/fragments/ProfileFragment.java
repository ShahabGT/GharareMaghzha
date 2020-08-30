package ir.ghararemaghzha.game.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.activities.MainActivity;
import ir.ghararemaghzha.game.activities.ProfileActivity;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog;
import ir.ghararemaghzha.game.models.QuestionModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH;

public class ProfileFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private MaterialTextView myScore, totalQuestions, remainingQuestion, remainingTime, remainingTimeTitle;
    private MaterialCardView buy, edit, stat;

    private Realm db;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    public ProfileFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(br, new IntentFilter(GHARAREHMAGHZHA_BROADCAST_REFRESH));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(br);
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
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.profile_title);


        db = Realm.getDefaultInstance();
        myScore = v.findViewById(R.id.profile_score);
        totalQuestions = v.findViewById(R.id.profile_questions);
        remainingQuestion = v.findViewById(R.id.profile_remaining);
        remainingTime = v.findViewById(R.id.profile_time);
        remainingTimeTitle = v.findViewById(R.id.profile_time_title);


        buy = v.findViewById(R.id.profile_buy_card);
        edit = v.findViewById(R.id.profile_edit_card);
        stat = v.findViewById(R.id.profile_stat_card);

        updateUI();

        onClicks();
    }

    private void updateUI() {
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
            if (passed >= 10)
                remainingTime.setText(context.getString(R.string.profile_time_end));
            else
                remainingTime.setText(context.getString(R.string.profile_time, String.valueOf(10 - passed)));

        }


        int total = db.where(QuestionModel.class).findAll().size();
        totalQuestions.setText(String.valueOf(db.where(QuestionModel.class).findAll().size()));
        int remain = db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size();
        remainingQuestion.setText(String.valueOf(db.where(QuestionModel.class).equalTo("userAnswer", "-1").findAll().size()));

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

        stat.setOnClickListener(v -> showDetailsDialog(MySharedPreference.getInstance(context).getUserId()));
    }

    private void showDetailsDialog(String userId) {
        UserDetailsDialog dialog = new UserDetailsDialog(activity, userId);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}