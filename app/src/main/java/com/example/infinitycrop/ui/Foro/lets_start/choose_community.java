package com.example.infinitycrop.ui.Foro.lets_start;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityAdapter;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.ForoMain;
import com.example.infinitycrop.ui.list_machine.Adapter;
import com.example.infinitycrop.ui.list_machine.MachineModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class choose_community extends AppCompatActivity {

    //1. declaro la variable del recyclerView
    private RecyclerView mFirestoreList;
    private RecyclerView communitiesRv;
    //2. Firebase variables
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorage;
    private String uid;
    //5. declaro viewHolder
    private CommunityAdapter communityAdapter;
    private CommunityAdapter communitiesAdapter;
    //variables del layout
    private ImageView btn_back;
    private FloatingActionButton btn_addCommunity;
    private Button btn_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_community);

        //1. initialice the ETE SETCH recyclerView
        mFirestoreList=findViewById(R.id.myCommunity_list);
        communitiesRv=findViewById(R.id.communities_list);
        //2. firebase variables
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        //variables del layout
        btn_back=findViewById(R.id.btn_back_welcome2);
        btn_addCommunity=findViewById(R.id.button_add_community);
        btn_over=findViewById(R.id.btn_continue_2);
        //onClick
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_addCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCommunity.class);
                startActivity(intent);
            }
        });
        btn_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForoMain.class);
                startActivity(intent);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(communityModel.getCreator().equals(uid)){ //cuando haces click sobre tu comunidad
            //variables from the botomSheet layout
            TextView name=bottomSheetDialog.findViewById(R.id.name_bS_community);
            TextView name2=bottomSheetDialog.findViewById(R.id.name_bS_community2);
            name.setText(R.string.editCommunity_text9);
            name.setTextSize(14);
            name.setTextColor(getResources().getColor(R.color.red));
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
                    //Intent modificar community
                    Intent intent = new Intent(getBaseContext(), EditCommunity.class);
                    intent.putExtra("community", uiDocument);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("Seguro que quieres eliminar esta comunidad?");
                    builder.setTitle("Eliminar comunidad");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            communityAdapter.deleteItem(position);
                            firebaseStorage.child("foro/user/"+uid+"/community/"+communityModel.getName()).delete();
                            dialog.cancel();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
                        communitiesAdapter.unFollow(position,uiDocument,suscriptores);
                    }else{ //no esta suscrito
                        //añadir comunidad a la coleccion Following
                        communitiesAdapter.follow(position,uiDocument,suscriptores);
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