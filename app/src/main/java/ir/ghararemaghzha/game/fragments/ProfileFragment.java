package ir.ghararemaghzha.game.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.adapters.ProfileViewPager;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog;
import ir.ghararemaghzha.game.models.ProfileModel;
import ir.ghararemaghzha.game.models.QuestionModel;

import static ir.ghararemaghzha.game.classes.Const.GHARAREHMAGHZHA_BROADCAST_REFRESH;

public class ProfileFragment extends Fragment {

    private NavController navController;
    private Context context;
    private FragmentActivity activity;
    private MaterialCardView buy, edit, stat, scoreHelper;
    private View v;

    private Realm db;

    private final BroadcastReceiver br = new BroadcastReceiver() {
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
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        activity = getActivity();
        init();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void init() {
        ((MaterialTextView) activity.findViewById(R.id.toolbar_title)).setText(R.string.profile_title);

        db = Realm.getDefaultInstance();


        buy = v.findViewById(R.id.profile_buy);
        edit = v.findViewById(R.id.profile_edit);
        stat = v.findViewById(R.id.profile_stat);
        scoreHelper = v.findViewById(R.id.profile_scorehelper);

        updateUI();

        onClicks();
    }

    private void initViewPager(List<ProfileModel> data) {
        UltraViewPager ultraViewPager = v.findViewById(R.id.profile_recycler);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        ultraViewPager.setPageTransformer(true, new UltraDepthScaleTransformer());
        ultraViewPager.setMultiScreen(0.6f);
        ultraViewPager.setItemRatio(1.0f);
        ultraViewPager.setRatio(1.6f);
      //  ultraViewPager.setMaxHeight(800);
     //   ultraViewPager.setAutoMeasureHeight(true);
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setAdapter(new ProfileViewPager(context,data));
        ultraViewPager.setAutoScroll(4000);


    }

    private void updateUI() {
        List<ProfileModel> data = new ArrayList<>();
        data.add(new ProfileModel(
                context.getString(R.string.avatar_url, MySharedPreference.getInstance(activity).getUserAvatar()),
                MySharedPreference.getInstance(context).getUsername(),
                "امتیاز من: "+MySharedPreference.getInstance(context).getScore()));
        int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());

        if (passed < 0) {

            data.add(new ProfileModel(R.drawable.profile_time,
                    context.getString(R.string.profile_time_card_tostart),
                    context.getString(R.string.profile_time, String.valueOf(Math.abs(passed)))));


        } else {
            ProfileModel model = new ProfileModel(R.drawable.profile_time,context.getString(R.string.profile_time_card),"");
            if (passed == 9) {
                model.setSubtitle(context.getString(R.string.profile_time_lastday));
            }else if (passed >= 10) {
                model.setSubtitle(context.getString(R.string.profile_time_end));
            }else {
                model.setSubtitle(context.getString(R.string.profile_time, String.valueOf(10 - passed)));
            }
            data.add(model);
        }


        data.add(new ProfileModel(R.drawable.profile_total,"سوالات این دوره",
                String.valueOf(db.where(QuestionModel.class).findAll().size())));

        data.add(new ProfileModel(R.drawable.profile_remain,"سوالات من",
                String.valueOf(db.where(QuestionModel.class)
                        .equalTo("bought",true)
                        .and().equalTo("userAnswer", "-1").findAll().size())));

        initViewPager(data);

    }

    private void onClicks() {
        buy.setOnClickListener(v ->
                navController.navigate(R.id.action_menu_profile_to_menu_buy)
        );

        edit.setOnClickListener(v -> navController.navigate(R.id.action_global_profileEditFragment));

        stat.setOnClickListener(v -> showDetailsDialog(MySharedPreference.getInstance(context).getUserId()));
        
        scoreHelper.setOnClickListener(v -> navController.navigate(R.id.action_global_scoreHelperFragment));
    }

    private void showDetailsDialog(String userId) {
        UserDetailsDialog dialog = new UserDetailsDialog(activity, userId);
        dialog.setCanceledOnTouchOutside(true);
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