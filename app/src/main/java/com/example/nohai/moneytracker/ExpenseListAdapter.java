package com.example.nohai.moneytracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nohai.moneytracker.Database.Expense;


import java.util.List;

/**
 * Created by nohai on 4/4/2018.
 */

public class ExpenseListAdapter extends
        RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {
    AppDatabase db;

        class ExpenseViewHolder extends RecyclerView.ViewHolder {
            private final TextView expenseItemView;
            private final TextView expenseItemViewDate;
            private final TextView expenseItemViewCategory;
            private final TextView expenseItemViewNotes;
            private final ImageButton imageButton;

            private ExpenseViewHolder(View itemView) {
                super(itemView);
                  expenseItemView = itemView.findViewById(R.id.textView);//price
                  expenseItemViewDate= itemView.findViewById(R.id.date);//date
                  expenseItemViewCategory=itemView.findViewById(R.id.category);//category
                  expenseItemViewNotes =  itemView.findViewById(R.id.notes);//notes
                  imageButton = itemView.findViewById(R.id.img);//arrow image
            }
        }



        private final LayoutInflater mInflater;
        private List<Expense> mExpenses; // Cached copy of categories

        ExpenseListAdapter(Context context) { mInflater = LayoutInflater.from(context);
            db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

    @Override
    public ExpenseListAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_list, parent, false);

        return new ExpenseListAdapter.ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseListAdapter.ExpenseViewHolder holder, int position) {
        Expense current = mExpenses.get(position);

        holder.expenseItemView.setText(String.valueOf((current.price)));
        holder.expenseItemViewDate.setText(String.valueOf((current.date)).substring(0,10));
        holder.expenseItemViewCategory.setText(db.categoryDao().getCategoryName(current.getCategoryId()));
        if(current.notes!=null) {
            holder.expenseItemViewNotes.setText(String.valueOf(current.notes));
            holder.imageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    View parent =  (View)v.getParent().getParent().getParent();
                    if( parent.findViewById(R.id.notes).getVisibility()==View.GONE)
                        parent.findViewById(R.id.notes).setVisibility(View.VISIBLE);
                        else
                        parent.findViewById(R.id.notes).setVisibility(View.GONE);
                    //TODO: rotate image
                 }
            });
        }
       else
            holder.imageButton.setVisibility(View.INVISIBLE);
    }

        void setExpenses(List<Expense> expenses){
            mExpenses = expenses;
            notifyDataSetChanged();
        }

        // getItemCount() is called many times, and when it is first called,
        // mExpenses has not been updated (means initially, it's null, and we can't return null).
        @Override
        public int getItemCount() {
            if (mExpenses != null)
                return mExpenses.size();
            else return 0;
        }
    }



