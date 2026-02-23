package lk.tnm.eshop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.tnm.eshop.R;
import lk.tnm.eshop.adapter.CategoryAdapter;
import lk.tnm.eshop.databinding.FragmentCategoryBinding;
import lk.tnm.eshop.model.Category;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recycleViewCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Category c1 = new Category("cat1", "Toys", "");
//        Category c2 = new Category("cat2", "Clothes", "");
//        Category c3 = new Category("cat3", "Shoes", "");
//        Category c4 = new Category("cat4", "Cosmetics", "");
//        Category c5 = new Category("cat5", "Electronics", "");
//        Category c6 = new Category("cat6", "Furniture", "");
//        Category c7 = new Category("cat7", "Books", "");
//        Category c8 = new Category("cat8", "Sports", "");
//
//        List<Category> cats = List.of(c1, c2, c3, c4, c5, c6, c7, c8);
//
//        WriteBatch batch = db.batch();
//
//        for (Category c : cats) {
//            DocumentReference ref = db.collection("categories").document();
//            batch.set(ref, c);
//        }
//
//        batch.commit();

        db.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        List<Category> categories = task.getResult().toObjects(Category.class);
                        adapter = new CategoryAdapter(categories, category -> {

                            Bundle bundle = new Bundle();
                            bundle.putString("categoryId", category.getCategoryId());

                            ListingFragment listingFragment = new ListingFragment();
                            listingFragment.setArguments(bundle);

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, listingFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                        binding.recycleViewCategories.setAdapter(adapter);
                    }
                });
    }
}