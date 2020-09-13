package ir.ghararemaghzha.game.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.BuyInterface;
import ir.ghararemaghzha.game.classes.MySharedPreference;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.models.PlanModel;

public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.ViewHolder> {

    private Context context;
    private List<PlanModel> data;
    private BuyInterface buyInterface;
    private static int HEADER_TYPE = 0;
    private static int LIST_TYPE = 1;

    public BuyAdapter(Context context, List<PlanModel> data, BuyInterface buyInterface) {
        this.buyInterface = buyInterface;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;
        return LIST_TYPE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == LIST_TYPE)
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_buy, parent, false), LIST_TYPE);
        else
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_buy_header, parent, false), HEADER_TYPE);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        PlanModel model = data.get(position);
        if (model != null) {
            int passed = Integer.parseInt(MySharedPreference.getInstance(context).getDaysPassed());
            if(passed>=10){
                h.buy.setText(R.string.profile_time_end);
                h.buy.setEnabled(false);
            }
            if (h.viewType == LIST_TYPE) {
                h.title.setText(context.getString(R.string.buy_plan_title, model.getPlanCount()));
                h.price.setText(context.getString(R.string.buy_price, Utils.moneySeparator(model.getPlanPrice())));

                h.buy.setOnClickListener(view -> buyInterface.buy(model.getPlanId(), model.getPlanPrice(), null, null));
                switch (position) {
                    case 0:
                        ImageViewCompat.setImageTintList(h.bg, ColorStateList.valueOf(context.getResources().getColor(R.color.light1)));
                        h.price.setTextColor(context.getResources().getColor(R.color.dark1));
                        h.title.setTextColor(context.getResources().getColor(R.color.dark1));
                        h.buy.setBackgroundColor(context.getResources().getColor(R.color.dark1));
                        break;
                    case 1:
                        ImageViewCompat.setImageTintList(h.bg, ColorStateList.valueOf(context.getResources().getColor(R.color.light2)));
                        h.price.setTextColor(context.getResources().getColor(R.color.dark2));
                        h.title.setTextColor(context.getResources().getColor(R.color.dark2));
                        h.buy.setBackgroundColor(context.getResources().getColor(R.color.dark2));
                        break;
                    case 2:
                        ImageViewCompat.setImageTintList(h.bg, ColorStateList.valueOf(context.getResources().getColor(R.color.light3)));
                        h.price.setTextColor(context.getResources().getColor(R.color.dark3));
                        h.title.setTextColor(context.getResources().getColor(R.color.dark3));
                        h.buy.setBackgroundColor(context.getResources().getColor(R.color.dark3));
                        break;
                    case 3:
                        ImageViewCompat.setImageTintList(h.bg, ColorStateList.valueOf(context.getResources().getColor(R.color.light4)));
                        h.price.setTextColor(context.getResources().getColor(R.color.dark4));
                        h.title.setTextColor(context.getResources().getColor(R.color.dark4));
                        h.buy.setBackgroundColor(context.getResources().getColor(R.color.dark4));
                        break;
                    case 4:
                        ImageViewCompat.setImageTintList(h.bg, ColorStateList.valueOf(context.getResources().getColor(R.color.light5)));
                        h.price.setTextColor(context.getResources().getColor(R.color.dark5));
                        h.title.setTextColor(context.getResources().getColor(R.color.dark5));
                        h.buy.setBackgroundColor(context.getResources().getColor(R.color.dark5));
                        break;

                }
            } else {
                if (MySharedPreference.getInstance(context).getBoosterValue() != 1f) {
                    h.title.setText(context.getString(R.string.buy_booster_title_inuse));
                    h.price.setText(context.getString(R.string.buy_price, "0"));
                    h.buy.setVisibility(View.GONE);
                } else if (!model.getPlanId().equals("-1")) {
                    h.title.setText(context.getString(R.string.buy_booster_title, model.getValue()));
                    h.price.setText(context.getString(R.string.buy_price, Utils.moneySeparator(model.getPlanPrice())));
                } else {
                    h.title.setText(context.getString(R.string.buy_booster_title_invalid));
                    h.price.setText(context.getString(R.string.buy_price, "0"));
                    h.buy.setVisibility(View.GONE);
                }

                h.buy.setOnClickListener(view -> buyInterface.buy(model.getPlanId(), model.getPlanPrice(), null, null));
            }

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView title, price;
        private MaterialButton buy;
        private ImageView bg;
        private int viewType;


        public ViewHolder(@NonNull View v, int type) {
            super(v);
            if (type == LIST_TYPE) {
                title = v.findViewById(R.id.row_buy_title);
                price = v.findViewById(R.id.row_buy_price);
                buy = v.findViewById(R.id.row_buy_buy);
                bg = v.findViewById(R.id.row_buy_bg);
                viewType = LIST_TYPE;
            } else {
                viewType = HEADER_TYPE;
                title = v.findViewById(R.id.row_buy_title);
                price = v.findViewById(R.id.row_buy_price);
                buy = v.findViewById(R.id.row_buy_buy);
            }
        }
    }
}
