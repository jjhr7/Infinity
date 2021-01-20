package com.example.infinitycrop.ui.Foro.main.Community.InfoCommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.EditCommunity;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoCommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoCommunityFragment newInstance(String param1, String param2) {
        InfoCommunityFragment fragment = new InfoCommunityFragment();
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

    private RoundedImageView img_community;
    private TextView num_posts;
    private TextView followers;
    private String communityID;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private Button btn_negativeAction;
    private Button btn_followOrEdit;
    private int followerss;
    private TextView nameComm;
    private TextView descComm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_info_community, container, false);

        CommunityMain communityMain= (CommunityMain) getActivity();
        assert communityMain != null;
        communityID=communityMain.getCommunityUid();

        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        img_community=v.findViewById(R.id.img_infoCommunity);
        num_posts=v.findViewById(R.id.num_posts_infoCommunity);
        followers=v.findViewById(R.id.num_followers_infoCommunity);
        btn_negativeAction=v.findViewById(R.id.btn_actionInfoCommunity);
        btn_followOrEdit=v.findViewById(R.id.btn_infoCommunity);
        nameComm=v.findViewById(R.id.nameCommunity_infoCommunity);
        descComm=v.findViewById(R.id.desc_community_infoCommunity);

        db.collection("Community").document(communityID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()){
                            CommunityModel community=value.toObject(CommunityModel.class);
                            Picasso.get().load(community.getImg()).into(img_community);
                            String posts=String.valueOf(community.getPosts());
                            followerss=community.getFollowers();
                            String numfollowers=String.valueOf(community.getFollowers());
                            num_posts.setText(posts);
                            followers.setText(numfollowers);
                            nameComm.setText(community.getName());
                            descComm.setText(community.getDescription());
                        }
                    }
                });

        db.collection("Community").document(communityID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()){
                            if(value.getString("creator").equals(uid)){
                                btn_negativeAction.setVisibility(View.VISIBLE);
                                btn_followOrEdit.setText(R.string.community_sheet_text7);
                                btn_followOrEdit.setBackgroundResource(R.drawable.button_action_machine);
                                btn_followOrEdit.setTextColor(getResources().getColor(R.color.colorPrimaryDashboard));

                                btn_negativeAction.setText(R.string.editCommunity_text9);
                                btn_negativeAction.setTextSize(14);
                                btn_negativeAction.setTextColor(getResources().getColor(R.color.red));
                            }else{
                                notMyCommunity();
                            }
                        }
                    }
                });


        btn_followOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_followOrEdit.getText().equals(getResources().getString(R.string.community_sheet_text7))){ //edit mi communidad
                    Intent intent = new Intent(getContext(), EditCommunity.class);
                    intent.putExtra("community", communityID);
                    startActivity(intent);
                }else if(btn_followOrEdit.getText().equals(getResources().getString(R.string.community_sheet_text5))){//dejar de seguir

                    db.collection("Foro user").document(uid)
                            .collection("Following").document(communityID).delete();
                    db.collection("Community").document(communityID)
                            .collection("Following").document(uid).delete();
                    followerss--;
                    db.collection("Community").document(communityID).update("followers",followerss);

                } else if(btn_followOrEdit.getText().equals(getResources().getString(R.string.community_sheet_text4))){//seguir
                    Map<String, Object> data = new HashMap<>();
                    data.put("user id", uid);
                    db.collection("Foro user").document(uid)
                            .collection("Following").document(communityID).set(data);
                    db.collection("Community").document(communityID)
                            .collection("Following").document(uid).set(data);
                    followerss++;
                    db.collection("Community").document(communityID).update("followers",followerss);
                }
            }
        });

        btn_negativeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Seguro que quieres eliminar esta comunidad? Una vez hecho no se podrá recuperarla");
                builder.setTitle("¿Eliminar comunidad?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllCommunityData();
                        Toast.makeText(getContext(), "La comunidad y sus datos han sido borrados", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        return v;
    }

    private void notMyCommunity(){
        btn_negativeAction.setVisibility(View.GONE);
        db.collection("Foro user").document(uid).collection("Following")
                .document(communityID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()){
                            isFollowed();
                        }else{
                            isNotFollowed();
                        }
                    }
                });
    }

    public void isFollowed(){
        btn_followOrEdit.setText(R.string.community_sheet_text5);
        btn_followOrEdit.setBackgroundResource(R.drawable.button_unfollow);
        btn_followOrEdit.setTextColor(getResources().getColor(R.color.black));


    }
    public void isNotFollowed(){
        btn_followOrEdit.setText(R.string.community_sheet_text4);
        btn_followOrEdit.setBackgroundResource(R.drawable.button_enter_machine);
        btn_followOrEdit.setTextColor(getResources().getColor(R.color.white));
    }

    private void deleteAllCommunityData(){
        //borrar todos los posts de esa comunidad antes de borrar la communidad entera
        db.collection("Posts")
                .whereEqualTo("community", communityID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                            return;
                        }
                        if(value.size()!=0){
                            for (QueryDocumentSnapshot doc : value) {
                                db.collection("Posts").document(doc.getId()).delete();
                            }
                        }
                        //borrar all followers que tiene la communidad
                        db.collection("Community").document(communityID).collection("Following")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {

                                            return;
                                        }
                                        if(value.size()!=0){
                                            for (QueryDocumentSnapshot doc : value) {
                                                db.collection("Community")
                                                        .document(communityID)
                                                        .collection("Following")
                                                        .document(doc.getId()).delete();
                                            }
                                        }
                                        db.collection("Community").document(communityID).delete();
                                    }
                                });

                    }
                });
    }
}