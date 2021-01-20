package com.example.infinitycrop.ui.Foro.main.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.EditCommunity;
import com.example.infinitycrop.ui.Foro.lets_start.choose_community;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.lets_start.welcome_forum;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RVFollowed.AdapterFollowedCmty;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RvPost.AdapterPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.CheckedOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeForoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeForoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeForoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeForoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeForoFragment newInstance(String param1, String param2) {
        HomeForoFragment fragment = new HomeForoFragment();
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

    //attributes
    //Rv
    private RecyclerView followed_comunities;
    private AdapterFollowedCmty adapterFollowedCmty;
    private RecyclerView followed_posts;
    private AdapterPost adapterPost;
    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;

    //List
    private List<CommunityModel> communityModels= new ArrayList<>();
    //Set para que no se repitan las id de las comunidades y que no se dupliquen
    private Set<String> cimmunitiesId = new HashSet<>();
    private List<PostModel> posts= new ArrayList<>();

    //swipe to refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home_foro, container, false);
        //refresh
        swipeRefreshLayout=v.findViewById(R.id.swipeToRefreshHomeForo);
        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        //rv declaration
        followed_comunities=v.findViewById(R.id.followed_communities);
        followed_posts=v.findViewById(R.id.followed_posts);
        initRvFollowedCommunities();
        initRvFollowedPosts();
        getIdFollowedCommunities();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIdFollowedCommunities();
                adapterFollowedCmty.notifyDataSetChanged();
                adapterPost.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }
    private void initRvFollowedCommunities(){
        followed_comunities.setHasFixedSize(true);
        followed_comunities.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
    }
    private void getIdFollowedCommunities(){
        db.collection("Foro user").document(uid).collection("Following")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if(value!=null){
                            cimmunitiesId.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                cimmunitiesId.add(doc.getId());
                            }
                            //guardo en la lista todas las comunidades que ha creado el user logged
                            db.collection("Community")
                                    .whereEqualTo("creator",uid)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                return;
                                            }

                                            for (QueryDocumentSnapshot doc : value) {
                                                cimmunitiesId.add(doc.getId());
                                            }
                                            getFollowedCommunities();
                                            getFollowedPosts();

                                        }
                                    });
                        }
                    }
                });
    }
    private void getFollowedCommunities(){
        //guardo en la lista todos los comunidades que se ha suscrito
        db.collection("Community")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if(value!=null){
                            communityModels.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                for(String id: cimmunitiesId){
                                    if(id.equals(doc.getId())){
                                        CommunityModel communityModel=doc.toObject(CommunityModel.class);
                                        communityModel.setUid(doc.getId());
                                        communityModels.add(communityModel);
                                    }
                                }
                            }
                            adapterFollowedCmty= new AdapterFollowedCmty(communityModels,getContext());
                            onclickAdapterFollowedCmty();
                            followed_comunities.setAdapter(adapterFollowedCmty);
                        }
                    }
                });

    }
    private void onclickAdapterFollowedCmty(){
        adapterFollowedCmty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=followed_comunities.getChildLayoutPosition(v);
                CommunityModel community=communityModels.get(position);
                bottomSheetCommunity(community,position);
            }
        });
    }
    //posts rv
    private void initRvFollowedPosts(){
        followed_posts.setHasFixedSize(false);
        followed_posts.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
    private void getFollowedPosts(){
        posts.clear();
        //get all the post where i was subscribed.
        db.collection("Posts")
                .orderBy("date",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for(String id: cimmunitiesId){
                                    if(document.getString("community").equals(id)){
                                        PostModel postModel=document.toObject(PostModel.class);
                                        postModel.setUid(document.getId().toString());
                                        posts.add(postModel);
                                    }
                                }
                            }
                            adapterPost=new AdapterPost(posts,getContext());
                            followed_posts.setAdapter(adapterPost);
                        } else {

                        }
                    }
                });

    }

    //bottom sheet community
    private void bottomSheetCommunity(CommunityModel communityModel, final int position){
        final String uiDocument=communityModel.getUid();
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_community);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if(communityModel.getCreator().equals(uid)){ //cuando haces click sobre tu comunidad
            //variables from the botomSheet layout
            TextView name=bottomSheetDialog.findViewById(R.id.name_bS_community);
            TextView name2=bottomSheetDialog.findViewById(R.id.name_bS_community2);
            name.setText(R.string.home_foro2);
            name.setTextSize(14);
            name.setTextColor(getResources().getColor(R.color.colorPrimaryDashboard));
            name2.setText(communityModel.getName());
            RoundedImageView img=bottomSheetDialog.findViewById(R.id.img_bS_community);
            Picasso.get().load(communityModel.getImg()).into(img);
            TextView desc=bottomSheetDialog.findViewById(R.id.desc_bS_community);
            desc.setText(communityModel.getDescription());
            TextView posts=bottomSheetDialog.findViewById(R.id.num_posts);
            String post= String.valueOf(communityModel.getPosts());
            posts.setText(post);
            TextView followers=bottomSheetDialog.findViewById(R.id.num_followers);
            String followersString= String.valueOf(communityModel.getFollowers());
            followers.setText(followersString);
            //button editar , eliminar
            final Button button=bottomSheetDialog.findViewById(R.id.btn_follow);
            button.setText(R.string.community_sheet_text7);
            button.setBackgroundResource(R.drawable.button_action_machine);
            button.setTextColor(getResources().getColor(R.color.colorPrimaryDashboard));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent
                    Intent intent = new Intent(getContext(), EditCommunity.class);
                    intent.putExtra("community", uiDocument);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initComunityLayout(uiDocument);
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.findViewById(R.id.btn_cancelBottomSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });

        }else{ //cuando hago click en otra comunidad
            //variables from the botomSheet layout
            final int suscriptores=communityModel.getFollowers();
            TextView name=bottomSheetDialog.findViewById(R.id.name_bS_community);
            TextView name2=bottomSheetDialog.findViewById(R.id.name_bS_community2);
            name.setText(R.string.home_foro2);
            name.setTextSize(14);
            name.setTextColor(getResources().getColor(R.color.colorPrimaryDashboard));
            name2.setText(communityModel.getName());
            RoundedImageView img=bottomSheetDialog.findViewById(R.id.img_bS_community);
            Picasso.get().load(communityModel.getImg()).into(img);
            TextView desc=bottomSheetDialog.findViewById(R.id.desc_bS_community);
            desc.setText(communityModel.getDescription());
            TextView posts=bottomSheetDialog.findViewById(R.id.num_posts);
            String post= String.valueOf(communityModel.getPosts());
            posts.setText(post);
            TextView followers=bottomSheetDialog.findViewById(R.id.num_followers);
            String followersString= String.valueOf(communityModel.getFollowers());
            followers.setText(followersString);
            //button follow , unfollow
            final Button button=bottomSheetDialog.findViewById(R.id.btn_follow);
            db.collection("Foro user").document(uid)
                    .collection("Following").document(uiDocument)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value.exists()){
                                button.setText(R.string.community_sheet_text5);
                                button.setBackgroundResource(R.drawable.button_unfollow);
                                button.setTextColor(getResources().getColor(R.color.black));
                            }else{
                                button.setText(R.string.community_sheet_text4);
                            }
                        }
                    });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ya es suscrito
                    String textButton=button.getText().toString();
                    String ifText=getResources().getString(R.string.community_sheet_text5);
                    if(textButton.equals(ifText)){
                        //eliminar de la coleccion following de Foro user
                        adapterFollowedCmty.unFollow(position,uiDocument,suscriptores);
                    }else{ //no esta suscrito
                        //añadir comunidad a la coleccion Following
                        //communitiesAdapter.follow(position,uiDocument,suscriptores);
                    }
                    bottomSheetDialog.dismiss();
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initComunityLayout(uiDocument);
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.findViewById(R.id.btn_cancelBottomSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.show();
    }
    //funcion que abre el layout de comunidadMain
    private void initComunityLayout(String uiDocument){
        Intent intent = new Intent(getActivity(), CommunityMain.class);
        intent.putExtra("community", uiDocument);
        startActivity(intent);
    }
}