package lk.tnm.eshop.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import lk.tnm.eshop.R;
import lk.tnm.eshop.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listner;

    private FirebaseStorage storage;

    public CategoryAdapter(List<Category> categories,OnCategoryClickListener listner) {
        this.categories = categories;
        this.listner = listner;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.categoryName.setText(category.getName());



        storage.getReference(category.getImageUrl())
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {



                    Glide.with(holder.itemView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(holder.categoryImage);
                });






        holder.itemView.setOnClickListener(view -> {

            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.click_animation);
            view.startAnimation(animation);
            if (listner != null){

                listner.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  categories.size() ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    public interface  OnCategoryClickListener {
        void onCategoryClick(Category category);

    }

}