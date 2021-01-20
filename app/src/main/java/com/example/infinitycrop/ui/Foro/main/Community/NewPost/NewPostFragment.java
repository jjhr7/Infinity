package com.example.infinitycrop.ui.Foro.main.Community.NewPost;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost.CreatePost;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.RvMyPosts.AdapterMyPosts;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RVFollowed.AdapterFollowedCmty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
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

    private String comunityID;
    private ConstraintLayout btn_create;
    private RecyclerView rv_myPost;
    private AdapterMyPosts adapterMyPosts;

    private List<PostModel> postModelList= new ArrayList<>();
    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_new_post, container, false);

        CommunityMain communityMain= (CommunityMain) getActivity();
        comunityID=communityMain.getCommunityUid();

        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        //rv, adapter
        rv_myPost=v.findViewById(R.id.rv_myGalleryPosts);

        btn_create=v.findViewById(R.id.btn_layoutCreatePost);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePost.class);
                intent.putExtra("community", comunityID);
                startActivity(intent);
            }
        });

        initRvMyPosts();
        getMyPosts();

        return v;
    }

    private void initRvMyPosts(){
        rv_myPost.setHasFixedSize(false);
        rv_myPost.setLayoutManager(new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false));
    }
    private void getMyPosts(){
        db.collection("Posts")
                .whereEqualTo("creator",uid)
                .whereEqualTo("community",comunityID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if(value!=null){
                            postModelList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                PostModel postModel=doc.toObject(PostModel.class);
                                postModel.setUid(doc.getId());
                                postModelList.add(postModel);
                            }
                            adapterMyPosts= new AdapterMyPosts(postModelList,getContext());
                            //onclickAdapterFollowedCmty();
                            rv_myPost.setAdapter(adapterMyPosts);
                        }
                    }
                });
    }

}