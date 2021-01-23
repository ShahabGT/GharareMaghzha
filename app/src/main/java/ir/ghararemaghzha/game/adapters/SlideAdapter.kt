package ir.ghararemaghzha.game.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.ghararemaghzha.game.R

class SlideAdapter(private val ctx: Context): RecyclerView.Adapter<SlideAdapter.ViewHolder>() {


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val pic: ImageView = v.findViewById(R.id.slide_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_slide,parent,false))

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
       when(position){
           0->h.pic.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.slide1))
           1->h.pic.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.slide2))
           2->h.pic.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.slide3))
           3->h.pic.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.slide4))
       }
    }

    override fun getItemCount(): Int =4
}