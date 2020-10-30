package com.example.infinitycrop.ui.recycler_control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;

import java.util.ArrayList;

public class StaticRvAdapter extends RecyclerView.Adapter<StaticRvAdapter.StaticTVViewHolder> {
    private ArrayList<StaticRvModel> items;
    int row_index=-1;
    TextView textMedidasTemp;
    TextView textMedidasH;
    TextView textMedidasS;
    TextView textMedidasL;

    public StaticRvAdapter(ArrayList<StaticRvModel> items,TextView mediT,TextView mediH,TextView mediS,TextView mediL) {
        this.items = items;
        textMedidasTemp =mediT;
        textMedidasH =mediH;
        textMedidasS =mediS;
        textMedidasL =mediL;

    }

    @NonNull
    @Override
    public StaticTVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_item,parent,false);
        StaticTVViewHolder staticTVViewHolder=new StaticTVViewHolder(view);
        return  staticTVViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StaticTVViewHolder holder, final int position) {
        final StaticRvModel currentItem=items.get(position);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;

                if(currentItem.getText().equals("Soleado")){
                    textMedidasTemp.setText("23°C");
                    textMedidasH.setText("60%");
                    textMedidasS.setText("70%");
                    textMedidasL.setText("99%");
                }else if(currentItem.getText().equals("Nocturno")){
                    textMedidasTemp.setText("8°C");
                    textMedidasH.setText("32%");
                    textMedidasS.setText("19%");
                    textMedidasL.setText("70%");
                } else if(currentItem.getText().equals("Ahorro")){
                    textMedidasTemp.setText("26°C");
                    textMedidasH.setText("99%");
                    textMedidasS.setText("34%");
                    textMedidasL.setText("13%");
                } else if(currentItem.getText().equals("Apagado")){
                    textMedidasTemp.setText("0FF");
                    textMedidasH.setText("OFF");
                    textMedidasS.setText("OFF");
                    textMedidasL.setText("OFF");
                } else if(currentItem.getText().equals("Custom")){
                    textMedidasTemp.setText("19°C");
                    textMedidasH.setText("23%");
                    textMedidasS.setText("87%");
                    textMedidasL.setText("78%");
                }
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
