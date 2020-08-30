package ir.ghararemaghzha.game.adapters;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Objects;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.dialogs.UserDetailsDialog;
import ir.ghararemaghzha.game.models.HighscoreModel;
import ir.ghararemaghzha.game.models.UserRankModel;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreAdapter.ViewHolder> {

    private FragmentActivity context;
    private List<HighscoreModel> data;
    private UserRankModel user;
    private boolean showUser;

    public HighscoreAdapter(FragmentActivity context, List<HighscoreModel> data, UserRankModel user, boolean showUser) {
        this.context = context;
        this.data = data;
        this.user = user;
        this.showUser = showUser;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_highscore, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        if (position < data.size()) {
            HighscoreModel model = data.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                h.rank.getBackground().setColorFilter(new BlendModeColorFilter(context.getResources().getColor(model.getColor(), null), BlendMode.SRC_ATOP));
            } else {
                h.rank.getBackground().setColorFilter(context.getResources().getColor(model.getColor()), PorterDuff.Mode.SRC_ATOP);

            }
            h.name.setText(model.getUserName());
            h.score.setText(model.getScoreCount());
            h.rank.setText(String.valueOf(position + 1));

            Glide.with(context)
                    .load(context.getString(R.string.avatar_url, model.getUserAvatar()))
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(h.avatar);

            h.itemView.setOnClickListener(v -> showDetailsDialog(model.getUserId()));
            if (!showUser && model.getUserId().equals(user.getUserId())) {
                h.card.setCardBackgroundColor(context.getResources().getColor(R.color.green2));
                h.name.setText("شما");

            }
        } else if (showUser) {
            h.name.setText("شما");
            h.score.setText(user.getScoreCount());
            h.rank.setText(user.getUserRank());
            h.card.setCardBackgroundColor(context.getResources().getColor(R.color.green2));
            Glide.with(context)
                    .load(context.getString(R.string.avatar_url, user.getUserAvatar()))
                    .circleCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(h.avatar);

            h.itemView.setOnClickListener(v -> showDetailsDialog(user.getUserId()));
        }

    }

    @Override
    public int getItemCount() {
        if (!showUser)
            return data.size();
        else
            return 11;
    }

    private void showDetailsDialog(String userId) {
        UserDetailsDialog dialog = new UserDetailsDialog(context, userId);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView name, score, rank;
        private ImageView avatar;
        private MaterialCardView card;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.highscore_row_name);
            score = v.findViewById(R.id.highscore_row_score);
            rank = v.findViewById(R.id.highscore_row_rank);

            avatar = v.findViewById(R.id.highscore_row_avatar);
            card = v.findViewById(R.id.highscore_row_card);
        }
    }
}
