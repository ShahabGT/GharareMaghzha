package ir.ghararemaghzha.game.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import ir.ghararemaghzha.game.R;
import ir.ghararemaghzha.game.classes.DateConverter;
import ir.ghararemaghzha.game.classes.Utils;
import ir.ghararemaghzha.game.models.BuyHistoryModel;

public class BuyHistoryAdapter extends RecyclerView.Adapter<BuyHistoryAdapter.ViewHolder> {

    private List<BuyHistoryModel> data;
    private Context context;

    public BuyHistoryAdapter(Context context, List<BuyHistoryModel> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buyhistory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BuyHistoryModel model = data.get(position);
        if (model != null) {
            h.title.setText(context.getString(R.string.buyhistory_row_title, model.getPlan()+"000"));

            h.price.setText(context.getString(R.string.buyhistory_row_price, Utils.moneySeparator(model.getAmount())));
            if (model.getInfluencerCode() != null && !model.getInfluencerCode().isEmpty()) {
                h.influencer.setVisibility(View.VISIBLE);
                h.influencer.setText(context.getString(R.string.buyhistory_row_price, Utils.moneySeparator(model.getAmount())));
            } else
                h.influencer.setVisibility(View.GONE);

            if (model.getUserId().equals("1")) {
                h.stat.setText(context.getString(R.string.buyhistory_row_stat, "موفق"));
                h.resCode.setVisibility(View.VISIBLE);
                h.resCode.setText(context.getString(R.string.buyhistory_row_rescode, model.getResCode()));
            } else {
                h.stat.setText(context.getString(R.string.buyhistory_row_stat, "ناموفق"));
                h.resCode.setVisibility(View.GONE);


            }

            String date = model.getDate();
            DateConverter dateConverter = new DateConverter();

            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
            h.date.setText(context.getString(R.string.buyhistory_row_date, date.substring(11, 16)+ " " + dateConverter.getYear() + "/" + dateConverter.getMonth() + "/" + dateConverter.getDay() ));


        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView title, price, date, influencer, stat, resCode;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.buyhistory_row_description);
            price = v.findViewById(R.id.buyhistory_row_price);
            date = v.findViewById(R.id.buyhistory_row_date);
            influencer = v.findViewById(R.id.buyhistory_row_influencer);
            stat = v.findViewById(R.id.buyhistory_row_stat);
            resCode = v.findViewById(R.id.buyhistory_row_rescode);

        }
    }
}
