package com.example.infinitycrop.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;

import java.util.ArrayList;

public class RViewAdapter extends RecyclerView.Adapter<RViewAdapter.DataObjectHolder> {

    private Context context;
    private ArrayList<Notificaciones> listaNotificaciones;

    public RViewAdapter(Context context, ArrayList<Notificaciones> listaNotificaciones) {
        this.context = context;
        this.listaNotificaciones = listaNotificaciones;
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView txtNombre;
        private TextView txtDescripcion;
        private TextView txtFecha;
        public RelativeLayout layoutABorrar;

        public DataObjectHolder(@NonNull View itemView) {
            super(itemView);
            this.img = itemView.findViewById(R.id.idImagen);
            this.txtNombre = itemView.findViewById(R.id.idNombre);
            this.txtDescripcion = itemView.findViewById(R.id.idDescripcion);
            this.layoutABorrar = itemView.findViewById(R.id.layoutABorrar);
            this.txtFecha=itemView.findViewById(R.id.idFecha);
        }
    }

    public void removeItem(int position){
        listaNotificaciones.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Notificaciones coche,int position){
        listaNotificaciones.add(position, coche);
        notifyItemInserted(position);
    }


    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_list_notificaciones, viewGroup, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataObjectHolder holder, int position) {

        holder.txtNombre.setText(listaNotificaciones.get(position).getNombre());
        holder.txtDescripcion.setText(listaNotificaciones.get(position).getInfo());
        holder.txtFecha.setText(listaNotificaciones.get(position).getFecha());

        Glide.with(context).load(listaNotificaciones.get(position).getFoto()).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Position: " +
                        holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

}
