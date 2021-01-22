package com.example.infinitycrop.ui.notifications;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificacionesActivity extends AppCompatActivity implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private String foto;
    private String medicion1;
    private String s;

    private RViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Notificaciones> arrayNotificaciones=new ArrayList<>();

    //ArrayList<Notificaciones> listaNotificaciones;
    RecyclerView recyclerNotificaciones;
    FirebaseFirestore db;

    long date = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
    String currentDate = sdf.format(date);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiaciones);

        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile6);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        s = extras.getString("idMachine"); //declaras como priv la s
        Log.d("prueba s", s.toString());

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        init();

        recyclerNotificaciones.setLayoutManager(layoutManager);
        //recyclerNotificaciones.setAdapter(adapter);
        getListaNotificaciones();

        ItemTouchHelper.SimpleCallback simpleCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, NotificacionesActivity.this);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerNotificaciones);

    }

    private void init() {
        this.recyclerNotificaciones = findViewById(R.id.recycler_view);
        this.layoutManager = new LinearLayoutManager(getBaseContext());
        //this.adapter = new RViewAdapter(getBaseContext(), getListaNotificaciones());
    }

    /*private ArrayList<Notificaciones> getListaNotificaciones() {
        ArrayList<Notificaciones> arrayNotificaciones = new ArrayList<>();

        arrayNotificaciones.add(new Notificaciones("Temperatura", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAhFBMVEX///8AAADZ2dn4+Phqamqenp6jo6Pm5uaGhobPz88jIyNHR0f7+/vy8vL19fWurq51dXXExMS0tLTs7OxkZGS7u7vT09Pd3d0gICCRkZE5OTlMTEzBwcFvb2+oqKgtLS0RERFWVlZ/f3+Xl5dBQUE0NDRKSkoZGRkWFhZ7e3tUVFQMDAxumF5uAAAIo0lEQVR4nO2da1siPQyGqQdQFATxgKgoyK676///f68Mr7u086QHDEn06v15HPMwM22TJmmnswcGDy+jVf/ZuenPUfd0sI9/ocnt3ZPzWR1/J5HXPxzi6kDbMCbGfahvzeJW2zgGBgtS35o7bfs+zTiq751+T9vEz3GSEvjOg7aRn+EqQ6Bzx9pm7k78E/zHibahu5L3BL/wUzzOFujcWNvYXTgtEOjcF1zgHBYJdGfa9pYzKlPoLrUNLuWgUKBz59omF/KnWOGLtslllD9C57RtLuNoB4UX2kaXQA+kqzPsK74zI2426B1E6OnMM5dYw/JgPZwcjomPFHkZk5MwONCiv1RwT+B6bfbPELwamLdvNE/J2yC/6kNW3G9fMEB+/1XrPss8gc4diUnbcAtsmPqXTJCd4X0KVrbCsYJrYMJNcE0XXDPxL+nlC3RONqp11zbgvnVR+ld4LFG4ENK2AaxJ28tO8I35F52XCHTuUErdGjCUTloXgVfZH0xvyhSeSqlbAxS2f2EgwB/00dccAcw1+wOs2doKwdL1Uwq7UurW8ChMhlp9rqXUreFROChTKLp041HYuS8R+CSkbQOTwqLXVPQl5VLYWeQLbK8o9gqXws4sV2BfOMrDpjA33HMmuqDpcCrsPGQ8xh+y3+AaRoXvHsZlN8qlxgYkq0KTVIUNVaFpqsKGqtA0VWFDVWiaqrChKjQNq8LJeO47E/NxO7wsDaPCMfQPZ9o5VHwKyXwA6Q3DtF3scZqViBIKLoXR7bWRjBYMk8JE7p/oZlMAk8JEoE04ROohtG+hmLEptPek+JoK7R8qJmxWhQ1Jhck06jCBRRAehck86qGUnjZMs0WimqGdJCYHk8JEGq5mbR/Xqi2auKdaaMO28o68p5rvKKf3RNa+KZdKMXrAt9C/GGnXnvLGaQ5OAxTnwQ9qJKqhKjRNVdhQFZqmKmyoCk1TFTZUhaapChtyFZ4en0Q51ggMMyrMKtCTLwNnUzhIlshuEG/jw6Vw+CtPoBPfpeFSeJYtUHpLmElhUWmXbDU/k8LsaosGIW0beBTCYmiaL1jZ9f2r875/hWWhQtGhhkdhYQcY0cUb01haplC0eo1J4e8SgY9C2jYwKSwqBJZdmXKt2og2NwjRkZTRt8hubiLdBpXPP3x4yxIoXmLJ6eNfJAtlz+byrd6Y4zSTXoSJSie7GolqqApNUxU2VIWmqQobqkLTVIUNVaFpqsKGTIW9i9HizGMxmqsXkfIpPCQK9B6Vu3+zKYyETHVP4eFSGG3Rqnp6C5fCaUyhbJO9ACaFqBHvFqLtPAOYFCbSFKatW8rBozDZKFnxS+RR+JBSqHiMUq2wbPj8M5RvJvgXHoWos7uHYmkQ01iayogSUoNgUghau2+jeewek8JEU3bpnqXbcK3aotXcmm0x+HyLSNKQbg8ePv/wdoX1rZRLLDl9/JuT2bTvMZ2dqNdY1jhNQ1Vomu+vcLGjwi9zODBqspal8ItIhIdVZSoUPvFnN3o4iylToVuZP+iRiuHmKnSvin11ciC91myFrm9aIhkeey64VjNKmGJIZhKioh3qWtVmegnoIhc0DSzIq2UzmguItKpEwdtILE0zYB8hkrOMHwp5IqlqGI0mUqb0iie5SNi+L2x8FpFiQWpTOhKHUe4DhbggjT2ilykTIkjhDL6nQ9LU+P4JuUeo2ycYQDWPizzADeRjVNyVQFCDRo5PS5wz/2vvRhdBTIV5z4FoO2fqnG5ipsjdwsQbFKbWp/grzN/BxH9v6EvEWyglIYkFuoGh4RSu14o8BPwbqSYFecDlTJkjCw//NROYgpWQpSMhmjNU0562QQu2GbhucDlaT+/3SzTGwkWRlaXbAtjW3r/sbc+Zd+13GE0ZwpWhFGiUaA+DL8EVrakA3eaPiIAkaJBo2d9+zi3/CE2KMgpSzDMsQ6Pt7+AaFIm08SGCNWl4fgju6Ry2dwK5iDbWpiDPNUgjoCI4wRMCY81STkYEYPoweUVDMB6BoIaJoQa4hsFkSOf6+nMKOCrAREQK7D8EwUM61zc4tQlcYWErCgR283N9/Vst2hdopnZ9ANZsvmMY627hjzVgbWrBvQDTof95xVIMr1NX6haObADjiJ+yFDvD4aLsVjokzYo10vEVJl8HHcB36E/45AkOLvxiQbMWC88QDCS+3XS8P/zMwG9hYWEKckDzM9L9yQAk4qhXGnbgjB94Dc+kwGBRBvaQLcz4YEIP1pv0hxi4ke0L3uR0RACW+7882bPr1b8ReBtshEyB9xSM8WEE44MgIgWGUhs5C2B8CCMUOEkjNB8EAmyEosBPH34+MFQYBsXR9o6FCR/vxoeWTV6TAmFzNhsZYOiMu/aJWmGopp2KAG5jJeiNAmltr+dh2xG+b/sMyMmykpGBohToWLSb5SZHaHWHvL72e2xjVboGOvHYuPNBj/i0YIvEPRpdBgrEFH5CcJ9c9dhmDxhMK9v8g1uQVl5SallWUl0Gl67PezJ3F3DUPj+KhF1IGwuaDUTOdu6BBUSZoQXP6S/32MY8/5VIpTWzi99A5VHmBAPR7twaC8HgLRaEmel8ZiKtTeFolThkMOYqvng+IHvQCBmeDx2qiAyJh/RfKbb1oCBtdVMiwW1I+f7OZolX7DiKt7t23POU+gAbhE/GySN6xrRzjxc3w80Mdz447SZOFTf4jq6BhYc+s9XPVaTM4gM7S26fwoNhIr+DthKSaB+LAkx+hBsKDjOIYGHfl4RagZVgxyuEJLrIZWAjRBohtluYg4UNwwTJFl0xXi3sFyZJ9uiiMVwf6zEk/OEkVgLAGew23pgfY7bpFRzO+D8jYz59ksIx9YeF5KdCzhMN5bZ5U2zy+BkGmRqnRovTczifp4+7XRgqUduJmyWdTuPcWfdLTPEpet0j1MbyaXRp2E0qZjLu/r6avT6/uV9Pq6vl3eWNqrr/AEI2b6Z8PrrSAAAAAElFTkSuQmCC", currentDate));
        arrayNotificaciones.add(new Notificaciones("Humedad", "data:image/png;base64,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", currentDate));
        arrayNotificaciones.add(new Notificaciones("Luminosidad","", currentDate));
        arrayNotificaciones.add(new Notificaciones("Temperatura", "", currentDate));
r
        return arrayNotificaciones;

    }*/


    public void getListaNotificaciones(){
        db = FirebaseFirestore.getInstance();

        db.collection("Notificaciones")
                .whereEqualTo("machineId", s)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            return;
                        }
                        arrayNotificaciones.clear();
                        for(QueryDocumentSnapshot doc : value){
                            Log.d("for", String.valueOf(doc));
                            String tipo = doc.getString("Tipo");
                            String medicion = doc.getString("Medicion");
                            if(medicion.equals("luminosidad")){
                                if (tipo.equals("1")) {
                                    foto=getString(R.string.alerta);
                                    medicion1=medicion;

                                }else{
                                    foto=getString(R.string.aviso);
                                    medicion1=medicion;
                                }
                            }

                            if(medicion.equals("humedad")){
                                if (tipo.equals("1")) {
                                    foto=getString(R.string.alerta);
                                    medicion1=medicion;
                                }else{
                                    foto=getString(R.string.aviso);
                                    medicion1=medicion;
                                }
                            }


                            if(medicion.equals("humedad ambiente")){
                                if (tipo.equals("1")) {
                                    foto=getString(R.string.alerta);
                                    medicion1=medicion;
                                }else{
                                    foto=getString(R.string.aviso);
                                    medicion1=medicion;
                                }
                            }


                            if(medicion.equals("temperatura")){
                                if (tipo.equals("1")) {
                                    foto=getString(R.string.alerta);
                                    medicion1=medicion;
                                }else{
                                    foto=getString(R.string.aviso);
                                    medicion1=medicion;
                                }
                            }
                            Notificaciones notificacion = new Notificaciones(medicion1,foto,currentDate);
                            arrayNotificaciones.add(notificacion);
                            Log.d("array", medicion1);
                        }

                        adapter = new RViewAdapter(getBaseContext(), arrayNotificaciones);
                        recyclerNotificaciones.setAdapter(adapter);

                    }
                });




    }



    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RViewAdapter.DataObjectHolder) {


            String nombre = arrayNotificaciones.get(viewHolder.getAdapterPosition()).getNombre();
            final Notificaciones notificacionBorrada = arrayNotificaciones.get(viewHolder.getAdapterPosition());
            final int deletedIntex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());

            recuperarNotificacion(viewHolder, nombre, notificacionBorrada, deletedIntex);

        }
    }


    private void recuperarNotificacion(RecyclerView.ViewHolder viewHolder, String nombre,
                                       final Notificaciones notificacionBorrada, final int deletedIntex) {

        Snackbar snackbar = Snackbar.make(((RViewAdapter.DataObjectHolder) viewHolder).layoutABorrar,
                nombre + " eliminado", Snackbar.LENGTH_LONG);
        snackbar.setAction("Deshacer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restoreItem(notificacionBorrada, deletedIntex);
            }
        });

        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }
}
