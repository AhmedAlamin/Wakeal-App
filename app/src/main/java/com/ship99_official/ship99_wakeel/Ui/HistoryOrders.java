package com.ship99_official.ship99_wakeel.Ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.ship99_official.ship99_wakeel.Pojo.RequestModel;
import com.ship99_official.ship99_wakeel.Pojo.RequestViewHolder3;
import com.ship99_official.ship99_wakeel.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class HistoryOrders extends Fragment {

    //FirebaseRecyclerAdapter adapter;
    private    Query query2 ;
    private RecyclerView recview;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, userRef;
    private FirebaseRecyclerOptions<RequestModel> options;
    private FirebaseRecyclerAdapter<RequestModel, RequestViewHolder3> adapter;
    private Long orderNum = Long.valueOf(0);
    private ImageView imageView;
    private List<RequestModel> model;
    private ProgressBar progressbar;


    public HistoryOrders() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_history_orders, container, false);



        mAuth = FirebaseAuth.getInstance();


        reference = FirebaseDatabase.getInstance()
                .getReference("Requests");

        imageView = v.findViewById(R.id.reservation_empty_view);
        recview = (RecyclerView) v.findViewById(R.id.reservationsRV);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));



        Query query = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("status_WakeelUid")
                .equalTo("Shipped:"+mAuth.getCurrentUser().getUid());



        options = new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(query, RequestModel.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder3>(options) {


            @Override
            public RequestViewHolder3 onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.singlerow3, parent, false);
                return new RequestViewHolder3(view);
            }

            @Override
            public int getItemCount() {

                if (super.getItemCount() == 0) {

                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    // Changes the height and width to the specified *pixels*
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    imageView.setLayoutParams(params);

                    ViewGroup.LayoutParams params2 = recview.getLayoutParams();
                    // Changes the height and width to the specified *pixels*
                    params2.height = 0;
                    params2.width = 0;
                    recview.setLayoutParams(params2);
                }else {
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    // Changes the height and width to the specified *pixels*
                    params.height = 0;
                    params.width = 0;
                    imageView.setLayoutParams(params);

                    ViewGroup.LayoutParams params2 = recview.getLayoutParams();
                    params2.height = RecyclerView.LayoutParams.MATCH_PARENT;
                    params2.width = RecyclerView.LayoutParams.MATCH_PARENT;
                    recview.setLayoutParams(params2);

                }
                return super.getItemCount();





            }


            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder3 holder, int position, @NonNull RequestModel model) {


                                holder.orderId.setText(String.valueOf(model.getOrderNumber()));

                                holder.status.setText(model.getStatus());

                                Picasso.with(holder.img.getContext()).load(model.getPhotoOfProduct()).fit().centerCrop()
                                        .placeholder(R.drawable.loading)
                                        .error(R.drawable.loading)
                                        .into(holder.img);

                            }
                            



        };


        recview.setAdapter(adapter);
        adapter.startListening();


        return v;
    }


}