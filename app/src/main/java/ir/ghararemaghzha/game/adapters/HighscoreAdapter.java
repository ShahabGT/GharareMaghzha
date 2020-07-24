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
import ir.ghararemaghzha.game.models.HighscoreModel;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreAdapter.ViewHolder> {

    private Context context;
    private List<HighscoreModel> data;

    public HighscoreAdapter (Context context, List<HighscoreModel> data){
        this.context=context;
        this.data=data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_highscore,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        HighscoreModel model = data.get(position);
        h.name.setText(model.getUserName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView name;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.row_highscore_name);
        }
    }
}
