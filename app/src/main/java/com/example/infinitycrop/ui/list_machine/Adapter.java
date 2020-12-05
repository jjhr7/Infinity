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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Adapter extends FirestoreRecyclerAdapter<MachineModel, Adapter.ProductsViewHolder> {

    private OnItemClickListener listener;
    public Adapter(@NonNull FirestoreRecyclerOptions<MachineModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.machine_item,parent,false);
        return new ProductsViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull MachineModel model) {
        holder.list_name.setText(model.getName());
        if(model.getPriority()==1){
            holder.list_price.setImageResource(R.drawable.icons_star);
        }
        holder.list_desc.setText(model.getDescription());
    }

    protected void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
        String qrScan= (String) getSnapshots().getSnapshot(position).getData().get("description");
        //eliminar de la coleccion Mediciones
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference itemsRef = rootRef.collection("Mediciones");
        Query query = itemsRef.whereEqualTo("machineID", qrScan);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                    }
                } else {

                }
            }
        });
        //eliminar de la coleccion Actuadores
        final CollectionReference itemsRef1 = rootRef.collection("Actuadores");
        Query query1 = itemsRef1.whereEqualTo("machineID", qrScan);
        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef1.document(document.getId()).delete();
                    }
                } else {

                }
            }
        });
    }

    class ProductsViewHolder extends RecyclerView.ViewHolder{
        protected TextView list_name;
        protected ImageView list_price;
        protected TextView list_desc;
        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_name);
            list_price=itemView.findViewById(R.id.list_price);
            list_desc=itemView.findViewById(R.id.list_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
