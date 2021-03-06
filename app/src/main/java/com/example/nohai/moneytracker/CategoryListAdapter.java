package com.example.nohai.moneytracker;

/**
 * Created by nohai on 3/16/2018.
 */
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nohai.moneytracker.Database.Category;
import java.util.List;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>{
  AppDatabase db;
  Context myContext;

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryItemView;
        private final TextView totalExpensesItemView;
        private final ImageView iconItemView;

        private CategoryViewHolder(View itemView) {
            super(itemView);
            categoryItemView = itemView.findViewById(R.id.price);
            totalExpensesItemView = itemView.findViewById(R.id.expenses);
            iconItemView = itemView.findViewById(R.id.icon);
        }
    }

    private final LayoutInflater mInflater;
    private List<Category> mCategories; // Cached copy of categories

    CategoryListAdapter(Context context) { mInflater = LayoutInflater.from(context);
        db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        myContext=context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
         Category current = mCategories.get(position);
         holder.categoryItemView.setText(current.getCategory());
         holder.totalExpensesItemView.setText(String.format ("%.2f",current.expensesCost));
         holder.iconItemView.setImageDrawable(myContext.getResources().
                getDrawable(db.categoryIconDao().getIconId((current.getCategoryIcon()))));
    }

    void setCategories(List<Category> categories){
        mCategories = categories;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mCategories has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mCategories != null)
            return mCategories.size();
        else return 0;
    }
}


