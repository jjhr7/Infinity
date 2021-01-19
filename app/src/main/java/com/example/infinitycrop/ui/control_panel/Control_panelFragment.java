package com.example.infinitycrop.ui.control_panel;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.welcome_forum;
import com.example.infinitycrop.ui.graphics.GraphicsActivity;
import com.example.infinitycrop.ui.notifications.NotificacionesActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Control_panelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Control_panelFragment extends Fragment {

    private String uid;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ConstraintLayout btn_forum;
    private ConstraintLayout btn_grafics;
    private ConstraintLayout btn_notificaciones;

    public Control_panelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment control_panel.
     */
    // TODO: Rename and change types and number of parameters
    public static Control_panelFragment newInstance(String param1, String param2) {
        Control_panelFragment fragment = new Control_panelFragment();
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
        View v= inflater.inflate(R.layout.fragment_control_panel, container, false);
        MainActivity myActivity = (MainActivity)getActivity();
        uid=myActivity.getMachineUID();
        //buttons
        btn_forum=v.findViewById(R.id.btn_forum);

        //onClick
        btn_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), welcome_forum.class);
                startActivity(intent);
            }
        });



        //buttons
        btn_grafics=v.findViewById(R.id.btn_graficas);

        //onClick
        btn_grafics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GraphicsActivity.class);
                startActivity(intent);
            }
        });


        //buttons
        btn_notificaciones=v.findViewById(R.id.btn_notificaciones);

        //onClick
        btn_notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificacionesActivity.class);
                intent.putExtra("idMachine", uid);
                startActivity(intent);
            }
        });
        return v;
    }
}