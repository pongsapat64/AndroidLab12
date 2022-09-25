package com.example.androidlab12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GundamAdapter extends RecyclerView.Adapter<GundamAdapter.ViewHolder> {

    private Context context;
    private List<Gundam> gundams;
    private ValueEventListener valueEventListener;

    public GundamAdapter(List<Gundam> foods, Context context) {
        this.gundams = foods;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_gundam, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Gundam upload = gundams.get(position);
        holder.textViewName.setText(upload.getName());
        holder.textViewPrice.setText(upload.getPrice() + " บาท");
        Glide.with(context).load(upload.getUrl()).into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return gundams.size();
    }

    /*
       INNER class
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewPrice;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.name);
            textViewPrice = (TextView) itemView.findViewById(R.id.price);
            imageView = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
