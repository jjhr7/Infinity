package com.example.infinitycrop.ui.Foro.main.Community.Posts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RvPost.AdapterPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsCommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsCommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostsCommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsCommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsCommunityFragment newInstance(String param1, String param2) {
        PostsCommunityFragment fragment = new PostsCommunityFragment();
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

    private String communityID;

    //Rv, adapter
    private RecyclerView rv_postCommunity;
    private AdapterPost adapterPost;
    //List
    private List<PostModel> posts= new ArrayList<>();
    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_posts_community, container, false);
        CommunityMain communityMain= (CommunityMain) getActivity();
        assert communityMain != null;
        communityID=communityMain.getCommunityUid();
        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        //rv
        rv_postCommunity=v.findViewById(R.id.post_community);

        initRvPosts();
        getPostsFromCommunity();

        swipeRefreshLayout=v.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostsFromCommunity();
                adapterPost.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    private void initRvPosts(){
        rv_postCommunity.setHasFixedSize(false);
        rv_postCommunity.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
    private void getPostsFromCommunity(){
        posts.clear();
        //get all the post of the community
        db.collection("Posts")
                .whereEqualTo("community",communityID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PostModel postModel=document.toObject(PostModel.class);
                                postModel.setUid(document.getId().toString());
                                posts.add(postModel);
                            }
                            adapterPost=new AdapterPost(posts,getContext());
                            rv_postCommunity.setAdapter(adapterPost);
                        } else {

                        }
                    }
                });
    }
}