package com.example.infinitycrop.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.LoadAnimations.IntroductoryActivity;
import com.example.infinitycrop.ui.list_machine.MainActivityMachineList;
import com.example.infinitycrop.ui.login.LogActivity;
import com.example.infinitycrop.ui.profile.settings.AboutInfinityCrap;
import com.example.infinitycrop.ui.profile.settings.HelpProfile;
/*import com.example.infinitycrop.ui.profile.settings.ModifyProfile;*/
import com.example.infinitycrop.ui.profile.settings.ModifyProfile;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    //atributos para confirmación de correo
    private FirebaseUser us;
    private TextView txtverf;
    private Button btnconf;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View v= inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView irAPerfil =v.findViewById(R.id.irAEditarPerfil);
        irAPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getActivity(), ModifyProfile.class);
                startActivity(intent);
            }
        });

        //Enlaces de los botones de configuracion
        //personalice colors
        RelativeLayout customColors_btn =v.findViewById(R.id.relativeLayout4);
        customColors_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //about us
        RelativeLayout aboutInfinity_btn =v.findViewById(R.id.relativeLayout6);
        aboutInfinity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutInfinityCrap.class);
                startActivity(intent);
            }
        });
        //help
        RelativeLayout help_profile =v.findViewById(R.id.relativeLayout5);
        help_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpProfile.class);
                startActivity(intent);
            }
        });


        //Recojo los datos del usuario
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //firebase
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        String idUser=mAuth.getCurrentUser().getUid();
        final GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso;
        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        //guardo la imagen en un ImagenView
        final ImageView img = (ImageView) v.findViewById(R.id.profile);
        //guardo el nombre en un textView
        final TextView nombre = (TextView) v.findViewById(R.id.username);
        nombre.setText(usuario.getDisplayName());
        DocumentReference documentReference=fStore.collection("Usuarios").document(idUser);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(nombre.getText() == ""){
                    nombre.setText(snapshot.getString("username"));
                    img.setImageResource(R.drawable.icons_user);
                }
            }
        });

        //guardo el mail en un textView
        TextView correo = (TextView) v.findViewById(R.id.mailUser);
        correo.setText(usuario.getEmail());
        //cargar imágen con glide:
        Glide.with(this).load(usuario.getPhotoUrl()).into(img);
        //boton cerrar sesion

        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        RelativeLayout cerrarSesion =(RelativeLayout) v.findViewById(R.id.btn_end_session);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estas seguro que quieres cerrar sesión?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Cerrar session con Firebase
                                mAuth.signOut();
                                //Cerrar sesión con google tambien: Google sign out
                                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Abrir MainActivity con SigIn button
                                        if(task.isSuccessful()){
                                            Intent logActivity = new Intent(getActivity().getApplicationContext(), IntroductoryActivity.class);
                                            startActivity(logActivity);
                                            getActivity().finish();
                                        }else{
                                            Toast.makeText(getActivity().getApplicationContext(), "No se pudo cerrar sesión con google",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();


            }
        });

        //Confirmación de verificación de correo
        TextView txtverify = (TextView) v.findViewById(R.id.txtVerif);
        Button btnverify = (Button) v.findViewById(R.id.btnverif);
        final FirebaseUser user = mAuth.getCurrentUser();
        if(!user.isEmailVerified()){
            us=user;
            txtverf = txtverify;
            btnconf = btnverify;

            txtverify.setVisibility(View.VISIBLE);
            btnverify.setVisibility(View.VISIBLE);

            btnverify.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {

                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Enviando correo de confirmación",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(),"Error en el envío de correo",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            /*}else{
            txtverify.setVisibility(View.GONE);
            btnverify.setVisibility(View.GONE);*/
        }
        /*RequestQueue colaPeticiones = Volley.newRequestQueue(getActivity()
                .getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView fotoUsuario = (NetworkImageView)
                    v.findViewById(R.id.profile);
            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        }else{
            NetworkImageView fotoUsuario = (NetworkImageView)
                    v.findViewById(R.id.profile);
            fotoUsuario.setImageUrl(String.valueOf(R.drawable.icons_user), lectorImagenes);
        }*/
        return v;
    }
}
