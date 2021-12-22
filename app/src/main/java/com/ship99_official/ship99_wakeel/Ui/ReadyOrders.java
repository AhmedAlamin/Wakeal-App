package com.ship99_official.ship99_wakeel.Ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ship99_official.ship99_wakeel.Pojo.PickupModel;
import com.ship99_official.ship99_wakeel.Pojo.RequestModel;
import com.ship99_official.ship99_wakeel.Pojo.RequestViewHolder2;
import com.ship99_official.ship99_wakeel.R;
import com.squareup.picasso.Picasso;

import static com.ship99_official.ship99_wakeel.Ui.LoginPickNameAddress.PERMISSION_REQUEST_CODE;


public class ReadyOrders extends Fragment {
    private RecyclerView recview;
    private FirebaseAuth mAuth;
    private Query query ;
    private DatabaseReference reference;
    private FirebaseRecyclerOptions<RequestModel> options;
    private FirebaseRecyclerAdapter<RequestModel, RequestViewHolder2> adapter;
    private Button qrButton;
    private String orderNumberForInsertQrCode = "";
    private Boolean addButtonClicked = true;
    private FloatingActionButton fab;
    private String resutlCode;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.activity_ready_orders, container, false);


        resutlCode = "";

        fab = v.findViewById(R.id.fab);
        qrButton = v.findViewById(R.id.qrButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    scanCode(true);

                }else{
                    requestPermission();
                }

            }
        });






        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Requests");


        recview = (RecyclerView) v.findViewById(R.id.reservationsRV);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        recview.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("ProcessNum")
                .equalTo("Second");



        options = new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(query, RequestModel.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder2>(options) {

            @Override
            public RequestViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.singlerow4, parent, false);
                return new RequestViewHolder2(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();

            }


            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder2 holder, int position, @NonNull RequestModel model) {


                if (model.getRequestQrCode() != null){
                    ViewGroup.LayoutParams params = qrButton.getLayoutParams();
                // Changes the height and width to the specified *pixels*
                params.height = 0;
                params.width = 0;
                qrButton.setLayoutParams(params);

                }
                else{
                  holder.qrButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED)
                            {
                                orderNumberForInsertQrCode = model.getOrderNumber().toString();
                                scanCode(false);
                                Log.d("pojoorder",model.getOrderNumber().toString());


                            }else{
                                requestPermission();
                            }
                        }
                    });
                }

                    holder.price.setText(model.getTotalPrice());

                    holder.orderId.setText(String.valueOf(model.getOrderNumber()));

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(),InfoOfTracking.class);
                            i.putExtra("status",model.getStatus());
                            i.putExtra("date",model.getDate());
                            startActivity(i);
                        }
                    });

                    holder.clientAddress.setText(model.getClientAddress());

                    holder.status.setText(model.getStatus());

                    holder.name.setText(model.getName());

                    if (model.getNumberOfClient() == null) {
                        Toast.makeText(getContext(), "null object", Toast.LENGTH_SHORT).show();
                    } else {
                        holder.numberOfClient.setText(model.getNumberOfClient());
                    }

//                    Log.d("pojo photoTarckFragment",model.getPhotoOfProduct());
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

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void scanCode(boolean isClicked){
        addButtonClicked = isClicked;
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ReadyOrders.this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    private void pickFromDriver(String driverUid){

      FirebaseDatabase.getInstance().getReference("Requests").orderByChild("DriverUid").equalTo(driverUid).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {

                        RequestModel requestModel = new RequestModel();
                        requestModel = nodeDataSnapshot.getValue(RequestModel.class);

                        if (requestModel.getProcessNum().equals("First") && requestModel.getStatus().equals("pickedUp")){
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            reference.child(key).child("ProcessNum").setValue("Second");
                            reference.child(key).child("WakeelUid").setValue(mAuth.getCurrentUser().getUid());
                            reference.child(key).child
                                    ("status").setValue("preparing");


                            Log.d("pojokin22",key);
                        }



                }
                }else{
                    Toast.makeText(getContext(),"Noting Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() != null ){

//                try {
//                    super.onActivityResult(requestCode, resultCode, data);
//
//                    if (requestCode == ADD_QR_CODE) {
//
//                        Toast.makeText(getContext(), "from add",
//                                Toast.LENGTH_SHORT).show();
//
//                    }else if (requestCode == ADD_REQUEST){
//                        Toast.makeText(getContext(), "from QrButton",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception ex) {
//                    Toast.makeText(getContext(), ex.toString(),
//                            Toast.LENGTH_SHORT).show();
//                }

                if (addButtonClicked){
                    Toast.makeText(getContext(),"add button",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(result.getContents());
                resutlCode = result.getContents();
                builder.setTitle("Scanning Result");
                //49374
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        pickFromDriver(resutlCode);
                        Toast.makeText(getContext(),"request Added",Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            finalize();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(result.getContents());
                    resutlCode = result.getContents();
                    builder.setTitle("Scanning Result");
                    //49374
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            addQRCodeForSingelOrder(resutlCode);
                            Log.d("pojo qr",resutlCode);
                            Toast.makeText(getContext(),"QrCode is Added",Toast.LENGTH_SHORT).show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
//                    Toast.makeText(getContext(),"Qr button",Toast.LENGTH_SHORT).show();


                }


            }
            else {
                Toast.makeText(getContext(),"No Result",Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void addQRCodeForSingelOrder(String resutlCode) {

        FirebaseDatabase.getInstance().getReference("Requests").orderByChild("orderNumber").equalTo(orderNumberForInsertQrCode).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {

                        RequestModel requestModel = new RequestModel();
                        requestModel = nodeDataSnapshot.getValue(RequestModel.class);

                        if (requestModel.getOrderNumber().equals(orderNumberForInsertQrCode)){
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            reference.child(key).child("ProcessNum").setValue("Third");
                            reference.child(key).child("WakeelUid").setValue(mAuth.getCurrentUser().getUid());
                            reference.child(key).child
                                    ("status").setValue("preparing");
                            reference.child(key).child("QrCode").setValue(resutlCode);
                        }



                    }
                }else{
                    Toast.makeText(getContext(),"Noting Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}