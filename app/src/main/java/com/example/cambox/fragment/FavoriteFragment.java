package com.example.cambox.fragment;

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
import com.example.cambox.databinding.FragmentFavoriteBinding;
import com.example.cambox.interfaces.OnClickListenerProduct;
import com.example.cambox.model.Product;
import com.example.cambox.model.User;
import com.example.cambox.util.FragmentUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FavoriteFragment extends Fragment {
    private User user;
    private FragmentFavoriteBinding binding;
    private List<Product> productList;
    private DatabaseReference ref;

    public FavoriteFragment(User user){
        this.user = user;
    }
    public FavoriteFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        this.productList = new ArrayList<>();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        // Inflate the layout for this fragment

        binding.rvFavorite.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(final DataSnapshot i:snapshot.child("Favorite").child(user.getKey()).getChildren()){
                    Product p = snapshot.child("Item").child(i.getKey()).getValue(Product.class);
                    productList.add(p);
                }
                ProductAdapter adapter = new ProductAdapter(productList);
                adapter.setListener(new OnClickListenerProduct() {
                    @Override
                    public void onclick(Product product) {
                        ViewProductFragment fragment = new ViewProductFragment(product, FavoriteFragment.this);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", user);
                        fragment.setArguments(bundle);
                        FragmentUtil.getFragment(fragment, getActivity());
                    }
                });
                binding.rvFavorite.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}