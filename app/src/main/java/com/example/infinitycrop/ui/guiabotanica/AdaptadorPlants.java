package com.example.infinitycrop.ui.guiabotanica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;

import java.util.ArrayList;

public class AdaptadorPlants extends RecyclerView.Adapter<AdaptadorPlants.ViewHolderNotificaciones>
        implements View.OnClickListener{
    Context context;

    public AdaptadorPlants(Context context, ArrayList<Guia> listaPlants) {
        this.listaPlants = listaPlants;
        this.context=context;
    }

    ArrayList<Guia> listaPlants;
    private View.OnClickListener listener;


    @Override
    public ViewHolderNotificaciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_plant, parent, false);
        view.setOnClickListener(this);
        return new ViewHolderNotificaciones(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotificaciones holder, int position) {
        holder.nombre.setText(listaPlants.get(position).getNombrePlanta());
        holder.info.setText(listaPlants.get(position).getDescripcionPlanta());
    }

    @Override
    public int getItemCount() {
        return listaPlants.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class ViewHolderNotificaciones extends RecyclerView.ViewHolder {

        TextView nombre, info;
        ImageView foto;

        public ViewHolderNotificaciones(@NonNull View itemView) {
            super(itemView);
            nombre=(TextView)itemView.findViewById(R.id.idNombre);
            info=(TextView)itemView.findViewById(R.id.idDescripcion);
            foto=(ImageView)itemView.findViewById(R.id.idImagen);
        }
    }
}
