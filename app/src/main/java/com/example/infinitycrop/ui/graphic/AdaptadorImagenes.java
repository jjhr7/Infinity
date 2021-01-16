package com.example.infinitycrop.ui.graphic;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorImagenes extends
        FirestoreRecyclerAdapter<Imagen, AdaptadorImagenes.ViewHolder> {
    private Context context;
    protected View.OnClickListener onClickListener;
    public AdaptadorImagenes(Context context,
                             @NonNull FirestoreRecyclerOptions<Imagen> options) {
        super(options);
        this.context = context.getApplicationContext();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imagen;
        public final TextView titulo;
        public final TextView tiempo;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imagen = (ImageView) itemView.findViewById(R.id.imageView1);
            this.titulo = (TextView) itemView.findViewById(R.id.textView1);
            this.tiempo = (TextView) itemView.findViewById(R.id.textView2);
        }
    }
    @Override public AdaptadorImagenes.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_img, parent, false);
        return new AdaptadorImagenes.ViewHolder(view);
    }
    @Override protected void onBindViewHolder(@NonNull AdaptadorImagenes
            .ViewHolder holder, int position, @NonNull Imagen imagen) {
        holder.titulo.setText(imagen.getTitulo());
        CharSequence prettyTime = DateUtils.getRelativeDateTimeString(
                context, imagen.getTiempo(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, 0);
        holder.tiempo.setText(prettyTime);
        Glide.with(context)
                .load(imagen.getUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imagen);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }
}
