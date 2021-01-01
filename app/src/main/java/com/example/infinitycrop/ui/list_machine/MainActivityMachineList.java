package com.example.infinitycrop.ui.list_machine;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.logmail.RegisterActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.BreakIterator;
import java.util.Map;

public class MainActivityMachineList extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private Adapter adapter;
    private FirebaseAuth firebaseAuth;
    Button addBtn;//boton añadir nueva maquina
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_machines);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);
        firebaseAuth=FirebaseAuth.getInstance();
        String uid=firebaseAuth.getUid();
        //Query
        Query query=firebaseFirestore
                .collection("Machine")
                .whereEqualTo("userUID", uid)
                .orderBy("priority",Query.Direction.ASCENDING);
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

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //documentSnapshot.getData() -> devuelve all que tiene la maquina en firebase
                MachineModel machine = documentSnapshot.toObject(MachineModel.class);
                final String id = documentSnapshot.getId();
                final String path = documentSnapshot.getString("description"); //devuelve ruta en firebase Machine\madara
                //botttom sheet
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivityMachineList.this);
                bottomSheetDialog.setContentView(R.layout.machine_bottom_sheet);
                Button btnenter=bottomSheetDialog.findViewById(R.id.enterMachineButton);
                Button btnmodify=bottomSheetDialog.findViewById(R.id.madifyMachineButton);
                Button btncancel=bottomSheetDialog.findViewById(R.id.cancelMachineButton);
                btnenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("machine", id);
                        intent.putExtra("description", path);
                        startActivity(intent);
                    }
                });
                btnmodify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //poner aca Intent para acceder al layout de modificar maquina
                        //hay que pasarle un intent con extra como he hecho en el onClick de arriba
                        //lo que necesitas para modificar es la variable id
                        //encuentras en firebase la maquina con el id y haces la magia de modificar etc..
                        //lo de maquina a favoritos es facil , si es 1 es favoirto si es 2 no lo es.
                        //ejemplo
                        /*Intent intent = new Intent(getBaseContext(), MainActivity.class);//Main por tu clase
                        intent.putExtra("machine", id);
                        startActivity(intent);*/
                    }
                });
                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.show();


            }
        });

        FloatingActionButton addbtn = (FloatingActionButton) findViewById(R.id.button_add_machine);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), AddMachine.class);
                startActivity(intent);

            }
        });

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
