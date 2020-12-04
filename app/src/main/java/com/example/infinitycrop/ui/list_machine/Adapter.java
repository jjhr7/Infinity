package com.example.infinitycrop.ui.list_machine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Adapter extends FirestoreRecyclerAdapter<MachineModel, MainActivityMachineList.ProductsViewHolder> {
    public Adapter(@NonNull FirestoreRecyclerOptions<MachineModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public MainActivityMachineList.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.machine_item,parent,false);
        return new MainActivityMachineList.ProductsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainActivityMachineList.ProductsViewHolder holder, int position, @NonNull MachineModel model) {
        holder.list_name.setText(model.getName());
        if(model.getPriority()==1){
            holder.list_price.setImageResource(R.drawable.icons_star);
        }
        holder.list_desc.setText(model.getDescription());
    }

    protected void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

}
