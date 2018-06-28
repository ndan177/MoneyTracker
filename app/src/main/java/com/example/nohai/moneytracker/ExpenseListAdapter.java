package com.example.nohai.moneytracker;

import android.app.Activity;
import android.app.DialogFragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.UI.NewBorrowFrom;
import com.example.nohai.moneytracker.UI.NewBorrowTo;
import com.example.nohai.moneytracker.Utils.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpenseListAdapter extends
        RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {
    AppDatabase db;
    private Context myContext;

        class ExpenseViewHolder extends RecyclerView.ViewHolder {
            private final TextView expenseItemView;
            private final TextView expenseItemViewDate;
            private final TextView expenseItemViewCategory;
            private final TextView currency;
            private final TextView expenseItemViewNotes;
            private final ImageButton imageButton;
            private final ImageButton moreItemView;

            private ExpenseViewHolder(View itemView) {
                super(itemView);
                  expenseItemView = itemView.findViewById(R.id.price);//price
                  expenseItemViewDate= itemView.findViewById(R.id.date);//date
                  expenseItemViewCategory=itemView.findViewById(R.id.contact);//category
                  expenseItemViewNotes =  itemView.findViewById(R.id.notes);//notes
                  imageButton = itemView.findViewById(R.id.img);//arrow image
                  currency= itemView.findViewById(R.id.currency);//currency
                moreItemView = itemView.findViewById(R.id.more);
            }
        }

        private final LayoutInflater mInflater;
        private List<Expense> mExpenses; // Cached copy of categories

        ExpenseListAdapter(Context context) { mInflater = LayoutInflater.from(context);
            db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            myContext = context;
        }

    @Override
    public ExpenseListAdapter.ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_list, parent, false);
        return new ExpenseListAdapter.ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseListAdapter.ExpenseViewHolder holder, int position) {
        Expense current = mExpenses.get(position);

        holder.expenseItemView.setText(String.format ("%.2f",current.price));
        holder.expenseItemViewDate.setText(String.valueOf((DateHelper.displayDateFormatList(current.date))));
        holder.expenseItemViewCategory.setText(db.categoryDao().getCategoryName(current.getCategoryId()));
        if(!current.notes.equals("")) {
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

        holder.currency.setText(((Activity) myContext).getIntent().getStringExtra("currency"));

        holder.moreItemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
/**Delete**/
                        db.expenseDao().delete(current);
                        mExpenses.remove(current);
                        notifyDataSetChanged();
//
//                        DialogFragment newFragment = new DebtListAdapter.FireMissilesDialogFragment();
//                        newFragment.show(((Activity) myContext).getFragmentManager(), "yesNo");
//                                if (deleteMe==true) {
//
//                                    db.debtDao().delete(current);
//                                    mDebts.remove(current);
//                                    notifyDataSetChanged();
//                                }
                        //TODO:DELETE
                        return true;
                    }
                });
            }
        });

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



