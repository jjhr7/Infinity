package com.example.infinitycrop.ui.dashboard;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Planta1Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String uid;
    private FirebaseFirestore db;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Planta1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_planta1, container, false);



        db = FirebaseFirestore.getInstance();
        MainActivity myActivity = (MainActivity) getActivity();
        final TextView medidaTemp=v.findViewById(R.id.medidaTemperatura);
        final TextView medidaHum=v.findViewById(R.id.medidaHumedad);
        final TextView medidaHumAm=v.findViewById(R.id.medidaSalinidad);
        /*final TextView medidaLuz=v.findViewById(R.id.medidasLuminosidad);*/
        uid=myActivity.getMachineUID();
        db.collection("Mediciones planta 1")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            //Temperatura
                            String medidaT=snapshot.getString("Temperatura");
                            medidaTemp.setText(medidaT+"°C");
                            //Humedad
                            String medidaH=snapshot.getString("Humedad");
                            medidaHum.setText(medidaH+"%");
                            //Humedad Ambiente
                            String medidaHA=snapshot.getString("Humedad Ambiente");
                            medidaHumAm.setText(medidaHA+"%");
                            //Luminosidad
                            /*String medidaL=snapshot.getString("Luminosidad");
                            medidaLuz.setText(medidaL+"%");*/


                        } else {

                        }
                    }
                });
        return v;
    }
}