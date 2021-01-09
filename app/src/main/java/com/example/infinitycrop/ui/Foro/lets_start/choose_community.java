package com.example.infinitycrop.ui.Foro.lets_start;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityAdapter;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.list_machine.Adapter;
import com.example.infinitycrop.ui.list_machine.MachineModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class choose_community extends AppCompatActivity {

    //1. declaro la variable del recyclerView
    private RecyclerView mFirestoreList;
    private RecyclerView communitiesRv;
    //2. Firebase variables
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String uid;
    //5. declaro viewHolder
    private CommunityAdapter communityAdapter;
    private CommunityAdapter communitiesAdapter;
    //variables del layout
    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_community);

        //1. initialice the ETE SETCH recyclerView
        mFirestoreList=findViewById(R.id.myCommunity_list);
        communitiesRv=findViewById(R.id.communities_list);
        //2. firebase variables
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        //variables del layout
        btn_back=findViewById(R.id.btn_back_welcome2);
        //onClick
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //RV my communities

        //3.Query
        //3.1 saco todos los documentos
        Query query= firebaseFirestore.collection("Community")
                .whereEqualTo("creator",uid);

        //4.Recycler optiones
        FirestoreRecyclerOptions<CommunityModel> options=new FirestoreRecyclerOptions.Builder<CommunityModel>()
                .setQuery(query,CommunityModel.class)
                .build();

        //5. View holder
        communityAdapter=new CommunityAdapter(options);
        mFirestoreList.setHasFixedSize(false);
        mFirestoreList.setLayoutManager(new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false));
        mFirestoreList.setAdapter(communityAdapter);

       communityAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
               CommunityModel communityModel = documentSnapshot.toObject(CommunityModel.class);
               final String id = documentSnapshot.getId();
               //final String path = documentSnapshot.getString("name");
               //Toast.makeText(getApplicationContext(),id, Toast.LENGTH_LONG).show();
               bottomSheetCommunity(communityModel,id,position);

           }
       });

       //RV most popular communities

        //3.Query
        //3.1 saco todos los documentos
        Query queryCommunities= firebaseFirestore.collection("Community")
                .orderBy("followers",Query.Direction.DESCENDING)
                .limit(30);

        //4.Recycler optiones
        FirestoreRecyclerOptions<CommunityModel> optionsCommunities=new FirestoreRecyclerOptions.Builder<CommunityModel>()
                .setQuery(queryCommunities,CommunityModel.class)
                .build();

        //5. View holder
        communitiesAdapter=new CommunityAdapter(optionsCommunities);
        communitiesRv.setHasFixedSize(false);
        communitiesRv.setLayoutManager(new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false));
        communitiesRv.setAdapter(communitiesAdapter);

        communitiesAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                CommunityModel communityModel = documentSnapshot.toObject(CommunityModel.class);
                final String id = documentSnapshot.getId();
                //final String path = documentSnapshot.getString("name");
                //Toast.makeText(getApplicationContext(),id, Toast.LENGTH_LONG).show();
                bottomSheetCommunity(communityModel,id,position);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        communityAdapter.startListening();
        communitiesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        communityAdapter.stopListening();
        communitiesAdapter.startListening();
    }

    private void bottomSheetCommunity(final CommunityModel communityModel, final String uiDocument, final int position){
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(choose_community.this,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_community);
        if(communityModel.getCreator().equals("pepe")){ //cuando haces click sobre tu comunidad
            //variables from the botomSheet layout
            TextView name=bottomSheetDialog.findViewById(R.id.name_bS_community);
            TextView name2=bottomSheetDialog.findViewById(R.id.name_bS_community2);
            name.setText(communityModel.getName());
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
            firebaseFirestore.collection("Foro user").document(uid)
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
                        communitiesAdapter.unFollow(position,uiDocument);
                        communityAdapter.unFollow(position,uiDocument);
                    }else{ //no esta suscrito
                        //añadir comunidad a la coleccion Following
                        communitiesAdapter.follow(position,uiDocument);
                        communityAdapter.follow(position,uiDocument);
                    }
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.findViewById(R.id.btn_cancelBottomSheet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });

        }else if(communityModel.getCreator().equals("asfa")){ //cuando hago click en otra comunidad
            //variables from the botomSheet layout
            TextView name=bottomSheetDialog.findViewById(R.id.name_bS_community);
            TextView name2=bottomSheetDialog.findViewById(R.id.name_bS_community2);
            name.setText(communityModel.getName());
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
            firebaseFirestore.collection("Foro user").document(uid)
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
                        communitiesAdapter.unFollow(position,uiDocument);
                        communityAdapter.unFollow(position,uiDocument);
                    }else{ //no esta suscrito
                        //añadir comunidad a la coleccion Following
                        communitiesAdapter.follow(position,uiDocument);
                        communityAdapter.follow(position,uiDocument);
                    }
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
}