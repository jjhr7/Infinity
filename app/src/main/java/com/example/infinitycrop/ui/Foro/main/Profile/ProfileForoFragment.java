package com.example.infinitycrop.ui.Foro.main.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost.CreatePost;
import com.example.infinitycrop.ui.MachineControl.planta1;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileForoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileForoFragment extends Fragment {
    private StorageReference mStorageRef;
    private String image_url;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String Nuser;
    Button btnEdit,btnSave;
    EditText txtNamep,descriptionP;
    ImageView imgP;
    ImageView lapiz1,lapiz2,lapiz3;
    TextView txtPost,txtFollow;
    LinearLayout l1p;
    private  Uri resultUri;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileForoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileForoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileForoFragment newInstance(String param1, String param2) {
        ProfileForoFragment fragment = new ProfileForoFragment();
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
        final View root = inflater.inflate(R.layout.fragment_profile_foro, container, false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        //Declaracion de los elementos
        lapiz1 = root.findViewById(R.id.pencil1);
        lapiz2 = root.findViewById(R.id.pencil2);
        lapiz3 = root.findViewById(R.id.pencil3);
        l1p = root.findViewById(R.id.layout1P);
        imgP = root.findViewById(R.id.imgP);
        imgP.setEnabled(false);
        btnEdit = root.findViewById(R.id.btneditDesc);
        btnSave = root.findViewById(R.id.btn_saveP);
        txtNamep = root.findViewById(R.id.nameP);
        txtNamep.setEnabled(false);
        descriptionP = root.findViewById(R.id.progiledescription);
        descriptionP.setEnabled(false);
        txtFollow = root.findViewById(R.id.followingP);
        txtPost = root.findViewById(R.id.txtPostN);


        String name;
            //firebase
        DocumentReference docRef = db.collection("Foro user").document(uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String nombre = snapshot.getString("name");
                    String descipcion = snapshot.getString("description");
                    String foto =snapshot.getString("url");

                    txtNamep.setText(nombre);
                    /*imgP.setText(link);*/
                    descriptionP.setText(descipcion);
                    if(!foto.equals("")){
                        Picasso.get().load(foto).into(imgP);
                    }
                } else {

                }
            }
        });

        db.collection("Foro user").document(uid).collection("Following")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                            return;
                        }

                        int cont=0;
                        for (DocumentSnapshot doc : snapshots) {
                            cont++;

                        }
                        String contador=String.valueOf(cont);
                        txtFollow.setText(contador);
                    }
                });
        db.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    return;
                }

                int cont=0;
                for (DocumentSnapshot doc : snapshots) {
                    if(doc.getString("creator").equals(uid)) {
                        cont++;
                    }
                }
                String contador=String.valueOf(cont);
                txtPost.setText(contador);
            }
        });

        //boton editar
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), EditProfileActivity.class);
                startActivity(intent);
            }

            });


        return root;

    }


}