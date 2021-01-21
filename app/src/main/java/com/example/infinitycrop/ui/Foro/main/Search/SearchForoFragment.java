package com.example.infinitycrop.ui.Foro.main.Search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.PostView;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.RvMyPosts.AdapterMyPosts;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RVFollowed.AdapterFollowedCmty;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RvPost.AdapterPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchForoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchForoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchForoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchForoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchForoFragment newInstance(String param1, String param2) {
        SearchForoFragment fragment = new SearchForoFragment();
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
    private RecyclerView rv_myPost;
    private AdapterMyPosts adapterMyPosts;
    private List<PostModel> postModelList= new ArrayList<>();
    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String comunityID;
    private Set<String> cimmunitiesId = new HashSet<>();
    //swipe to refresh
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search_foro, container, false);

        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
//refresh
        swipeRefreshLayout=v.findViewById(R.id.swipeToRefreshSearch);
        //rv, adapter
        rv_myPost=v.findViewById(R.id.rv_UnFollowPosts);

        initRvMyPosts();
        getPosts();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
                adapterMyPosts.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    private void initRvMyPosts(){
        rv_myPost.setHasFixedSize(false);
        rv_myPost.setLayoutManager(new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false));
    }


    private void getPosts(){
        db.collection("Posts")
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
                            onclickPost();
                            rv_myPost.setAdapter(adapterMyPosts);
                        }
                    }
                });
    }

    private void onclickPost(){
        adapterMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=rv_myPost.getChildLayoutPosition(v);
                PostModel postModel=postModelList.get(position);
                intentPosts(postModel.getUid());
            }
        });
    }
    private void intentPosts(String idPost){
        Intent intent = new Intent(getActivity(), PostView.class);
        intent.putExtra("Post", idPost);
        startActivity(intent);
    }



}