package com.example.infinitycrop.ui.list_machine;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.logmail.RegisterActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.BreakIterator;

public class MainActivityMachineList extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private Adapter adapter;
    Button addBtn;//boton añadir nueva maquina
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_machines);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);

        //Query
        Query query=firebaseFirestore.collection("MachineNumber").orderBy("priority",Query.Direction.ASCENDING);
        //RecyclerView
        FirestoreRecyclerOptions<MachineModel> options=new FirestoreRecyclerOptions.Builder<MachineModel>()
                .setQuery(query, MachineModel.class)
                .build();
        //View Holder
        adapter= new Adapter(options);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getApplicationContext(), "Maquina eliminada con éxito", Toast.LENGTH_SHORT).show();
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mFirestoreList);
        FloatingActionButton addbtn = (FloatingActionButton) findViewById(R.id.button_add_machine);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), AddMachine.class);
                startActivity(intent);

            }
        });

    }

    static class ProductsViewHolder extends RecyclerView.ViewHolder{
        protected TextView list_name;
        protected ImageView list_price;
        protected TextView list_desc;
        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_name);
            list_price=itemView.findViewById(R.id.list_price);
            list_desc=itemView.findViewById(R.id.list_desc);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
