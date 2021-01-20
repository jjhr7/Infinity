package com.example.infinitycrop.ui.Foro.main.Subscription;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.AddCommunity;
import com.example.infinitycrop.ui.Foro.lets_start.EditCommunity;
import com.example.infinitycrop.ui.Foro.lets_start.choose_community;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityAdapter;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PanelForoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PanelForoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PanelForoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PanelForoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PanelForoFragment newInstance(String param1, String param2) {
        PanelForoFragment fragment = new PanelForoFragment();
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
    private FloatingActionButton btn_addCommunity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_panel_foro, container, false);

        //1. initialice the ETE SETCH recyclerView
        mFirestoreList=v.findViewById(R.id.myCommunityList_panelForo);
        communitiesRv=v.findViewById(R.id.communities_panelForo);
        //2. firebase variables
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage= FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        //variables del layout
        btn_addCommunity=v.findViewById(R.id.button_add_community_panelForo);
        //onClick
        btn_addCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCommunity.class);
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
        mFirestoreList.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
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
        communitiesRv.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
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


        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();
        communityAdapter.startListening();
        communitiesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        communityAdapter.stopListening();
        communitiesAdapter.startListening();
    }

    private void bottomSheetCommunity(final CommunityModel communityModel, final String uiDocument, final int position){
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
                    //Intent modificar community
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