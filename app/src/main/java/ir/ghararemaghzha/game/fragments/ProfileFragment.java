package ir.ghararemaghzha.game.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.MySharedPreference;

public class ProfileFragment extends Fragment {

    private Context context;
    private FragmentActivity activity;
    private Realm realm;
    private MaterialTextView myScore,totalQuestions,remainingQuestion,remainingTime,myCode,myName;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        activity = getActivity();
        init(v);

        return v;
    }

    private void init(View v){
        realm = Realm.getDefaultInstance();
        myScore = v.findViewById(R.id.profile_score);
        totalQuestions = v.findViewById(R.id.profile_questions);
        remainingQuestion = v.findViewById(R.id.profile_remaining);
        remainingTime = v.findViewById(R.id.profile_time);
        myCode = v.findViewById(R.id.profile_code);
        myName = v.findViewById(R.id.profile_name);

        myName.setText(MySharedPreference.getInstance(context).getUsername());
        myCode.setText(context.getString(R.string.profile_code,MySharedPreference.getInstance(context).getUserCode()));
        myScore.setText(MySharedPreference.getInstance(context).getScore());


    }
}