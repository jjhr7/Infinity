package com.example.infinitycrop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StaticRvAdapter extends RecyclerView.Adapter<StaticRvAdapter.StaticTVViewHolder> {

    private ArrayList<StaticRvModel> items;
    int row_index=-1;

    public StaticRvAdapter(ArrayList<StaticRvModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public StaticTVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_item,parent,false);
        StaticTVViewHolder staticTVViewHolder=new StaticTVViewHolder(view);
        return  staticTVViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StaticTVViewHolder holder, final int position) {
        StaticRvModel currentItem=items.get(position);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if(row_index == position){
            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_selected);

        }else{
            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_bg);

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class  StaticTVViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;
        public StaticTVViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            textView=itemView.findViewById(R.id.text);
            linearLayout=itemView.findViewById(R.id.linearLayoutItem);
        }
    }
}
