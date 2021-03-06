package com.example.infinitycrop.ui.guiabotanica;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;

import java.util.ArrayList;

public class AdaptadorGuia extends RecyclerView.Adapter<AdaptadorGuia.ViewHolderNotificaciones>
        implements View.OnClickListener{
    Context context;

    public AdaptadorGuia(Context context, ArrayList<Guia> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
        this.context=context;
    }

    ArrayList<Guia> listaNotificaciones;
    private View.OnClickListener listener;


    @Override
    public ViewHolderNotificaciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guia_botanica, parent, false);
        view.setOnClickListener(this);
        return new ViewHolderNotificaciones(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotificaciones holder, final int position) {
        holder.nombre.setText(listaNotificaciones.get(position).getNombrePlanta());
        holder.info.setText(listaNotificaciones.get(position).getDescripcionPlanta());
        Glide.with(holder.itemView.getContext())
                .load(listaNotificaciones.get(position).fotoPlanta)
                .into(holder.foto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PlantActivity.class);
                i.putExtra("nombre", listaNotificaciones.get(position).getNombrePlanta());
                i.putExtra("info",listaNotificaciones.get(position).getDescripcionPlanta());
                i.putExtra("foto", listaNotificaciones.get(position).getFotoPlanta());
                i.putExtra("humedad", listaNotificaciones.get(position).getHumedadPlanta());
                i.putExtra("temperatura", listaNotificaciones.get(position).getTemperaturaPlanta());
                i.putExtra("luminosidad", listaNotificaciones.get(position).getLuminosidadPlanta());

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
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
