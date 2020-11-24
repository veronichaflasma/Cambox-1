package com.example.cambox.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cambox.R;
import com.example.cambox.adapter.ProductAdapter;
import com.example.cambox.databinding.FragmentProductBinding;
import com.example.cambox.interfaces.OnClickListenerProduct;
import com.example.cambox.model.Product;
import com.example.cambox.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    FragmentProductBinding binding;
    User user;

    DatabaseReference dbRef;
    StorageReference storageRef;

    ProgressDialog progressDialog;
    List<Product> list;

    public ProductFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.show();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.child("Item").getChildren()){
                    Product products= data.getValue(Product.class);
                    list.add(products);
                }
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                ProductAdapter adapter = new ProductAdapter(list);
                adapter.setListener(new OnClickListenerProduct() {
                    @Override
                    public void onclick(Product product) {
                        ViewProductFragment fragment = new ViewProductFragment(product, ProductFragment.this);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", user);
                        fragment.setArguments(bundle);
                        getFragment(fragment);
                    }
                });
                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog.dismiss();

    }

    private void getFragment(Fragment fragment){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }
}