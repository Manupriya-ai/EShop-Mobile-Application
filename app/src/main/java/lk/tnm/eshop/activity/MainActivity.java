package lk.tnm.eshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreKt;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import lk.tnm.eshop.R;
import lk.tnm.eshop.databinding.ActivityMainBinding;
import lk.tnm.eshop.databinding.SideNavHeaderBinding;
import lk.tnm.eshop.fragment.CartFragment;
import lk.tnm.eshop.fragment.CategoryFragment;
import lk.tnm.eshop.fragment.HomeFragment;
import lk.tnm.eshop.fragment.LoginFragment;
import lk.tnm.eshop.fragment.MessageFragment;
import lk.tnm.eshop.fragment.ProfileFragment;
import lk.tnm.eshop.fragment.SettingFragment;
import lk.tnm.eshop.fragment.WhistlistFragment;
import lk.tnm.eshop.model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnItemSelectedListener {

    private ActivityMainBinding binding;

    private SideNavHeaderBinding sideNavHeaderBinding;

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        View headerView = binding.sideNavigationView.getHeaderView(0);
        sideNavHeaderBinding = SideNavHeaderBinding.bind(headerView);




        drawerLayout = binding.drawlerLayout;
        toolbar = binding.toolbar;
        navigationView = binding.sideNavigationView;
        bottomNavigationView = binding.bottomNavigationView;

        // Toolbar set
        setSupportActionBar(toolbar);

        // Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // IMPORTANT - Set Listeners

        // Back press handle
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            finish();
                        }
                    }
                });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());

            navigationView.getMenu().findItem(R.id.side_nav_home).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);

        }

        firebaseAuth = firebaseAuth.getInstance();
        firebaseFirestore = firebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(ds -> {

                        if (ds.exists()) {
                            User user = ds.toObject(User.class);
                            sideNavHeaderBinding.headerUserName.setText(user.getName());
                            sideNavHeaderBinding.headerUserEmail.setText(user.getEmail());

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference("profile-images/" + user.getProfilePicUrl()).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Glide.with(MainActivity.this)
                                                .load(uri)
                                                .circleCrop()
                                                .into(sideNavHeaderBinding.headerProfileProfilePic);
                                    });

                        } else {
                            Log.e("Firestore", "Document Doesn't exist");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("Listener", "Mainactivity 173");

                    });

            navigationView.getMenu().findItem(R.id.side_nav_login).setVisible(false);


            navigationView.getMenu().findItem(R.id.side_nav_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_order).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_whistlist).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_cart).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_message).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_logout).setVisible(true);

            sideNavHeaderBinding.headerProfileProfilePic.setOnClickListener(v ->{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(intent);


            });

        }


    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                       Uri uri = result.getData().getData();
                       Log.i("ImageURI", uri.getPath());

                        Glide.with(MainActivity.this)
                                .load(uri)
                                .circleCrop()
                                .into(sideNavHeaderBinding.headerProfileProfilePic);

                       String imageId = UUID.randomUUID().toString();

                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference imageReferance = storage.getReference("profile-images").child(imageId);
                        imageReferance.putFile(uri)
                                .addOnSuccessListener(taskSnapshot -> {

                                    firebaseFirestore.collection("users")
                                            .document(firebaseAuth.getUid())
                                            .update("profilePicUrl", imageId)
                                            .addOnSuccessListener(aVoid ->{
                                                Toast.makeText(MainActivity.this, "Profile Image change", Toast.LENGTH_SHORT).show();
                                            });

                                });

                    }
            }
    );


    // Side Navigation Click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        int itemId = item.getItemId();

        Menu navMenu = navigationView.getMenu();
        Menu bottomNavMenu = bottomNavigationView.getMenu();

        for (int i = 0; i < navMenu.size(); i++) {
            navMenu.getItem(i).setChecked(false);

        }
        for (int i = 0; i < bottomNavMenu.size(); i++) {
            bottomNavMenu.getItem(i).setChecked(false);

        }


        if (itemId == R.id.side_nav_home || itemId == R.id.bottom_nav_home) {
            loadFragment(new HomeFragment());

            navigationView.getMenu().findItem(R.id.side_nav_home).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);

        } else if (itemId == R.id.side_nav_profile || itemId == R.id.bottom_nav_profile) {
            if (firebaseAuth.getCurrentUser() == null){
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
            loadFragment(new ProfileFragment());
            navigationView.getMenu().findItem(R.id.side_nav_profile).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_profile).setChecked(true);

        } else if (itemId == R.id.side_nav_order) {
            navigationView.getMenu().findItem(R.id.side_nav_order).setChecked(true);

        } else if (itemId == R.id.side_nav_whistlist) {
            loadFragment(new WhistlistFragment());
            navigationView.getMenu().findItem(R.id.side_nav_whistlist).setChecked(true);

        } else if (itemId == R.id.side_nav_cart || itemId == R.id.bottom_nav_cart) {
            if (firebaseAuth.getCurrentUser() == null){
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
            loadFragment(new CartFragment());
            navigationView.getMenu().findItem(R.id.side_nav_cart).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_cart).setChecked(true);


        } else if (itemId == R.id.side_nav_message) {
            loadFragment(new MessageFragment());
            navigationView.getMenu().findItem(R.id.side_nav_message).setChecked(true);


        } else if (itemId == R.id.side_nav_setting) {
            loadFragment(new SettingFragment());
            navigationView.getMenu().findItem(R.id.side_nav_setting).setChecked(true);


        } else if (itemId == R.id.bottom_nav_category) {
            loadFragment(new CategoryFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_category).setChecked(true);


        } else if (itemId == R.id.side_nav_login) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);


        } else if (itemId == R.id.side_nav_logout) {
            firebaseAuth.signOut();
           loadFragment(new HomeFragment());
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.side_nav_menu);

            navigationView.removeHeaderView(sideNavHeaderBinding.getRoot());
            navigationView.inflateHeaderView(R.layout.side_nav_header);


        }


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
