package com.example.theexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.theexplorer.R;
import com.example.theexplorer.databinding.FragmentProfileBinding;
import com.example.theexplorer.ui.auth.LogIn;
import com.example.theexplorer.ui.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private TextView txtemail,txtname;
    private Toolbar toolbar;
    private CircleImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textProfile;
        Button logoutBtn = root.findViewById(R.id.logout);

        // Set click listener for logout button
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(requireContext(), LogIn.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Explorer");
        txtemail = view.findViewById(R.id.txtemail);
        txtname = view.findViewById(R.id.txtname);
        img = view.findViewById(R.id.img);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
    }

    private void checkUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(requireContext(), Register.class));
            requireActivity().finish();
        } else {
            if (account != null) {
                txtemail.setText(account.getEmail());
                txtname.setText(account.getDisplayName());
                Glide.with(this).load(account.getPhotoUrl()).into(img);
            } else {
                String email = firebaseUser.getEmail();
                String name = firebaseUser.getDisplayName();
                txtemail.setText(email);
                txtname.setText(name);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

