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
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.Utils.DateHelper;
import java.util.List;

public class IncomeListAdapter extends
        RecyclerView.Adapter<IncomeListAdapter.IncomeViewHolder> {
    AppDatabase db;
    private Context myContext;

    class IncomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView IncomeItemView;
        private final TextView IncomeItemViewDate;
        private final TextView currency;
        private final TextView IncomeItemViewNotes;
        private final ImageButton imageButton;
        private final ImageButton moreItemView;

        private IncomeViewHolder(View itemView) {
            super(itemView);
            IncomeItemView = itemView.findViewById(R.id.price);//price
            IncomeItemViewDate= itemView.findViewById(R.id.date);//date
            IncomeItemViewNotes =  itemView.findViewById(R.id.notes);//notes
            imageButton = itemView.findViewById(R.id.img);//arrow image
            currency= itemView.findViewById(R.id.currency);//currency
            moreItemView = itemView.findViewById(R.id.more);
        }
    }

    private final LayoutInflater mInflater;
    private List<Income> mIncomes;

    IncomeListAdapter(Context context) { mInflater = LayoutInflater.from(context);
        db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        myContext = context;
    }

    @Override
    public IncomeListAdapter.IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_list_income, parent, false);
        return new IncomeListAdapter.IncomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IncomeListAdapter.IncomeViewHolder holder, int position) {
        Income current = mIncomes.get(position);

        holder.IncomeItemView.setText(String.format ("%.2f",current.price));
        holder.IncomeItemViewDate.setText(String.valueOf((DateHelper.displayDateFormatList(current.date))));
//        holder.IncomeItemViewCategory.setText(db.categoryDao().getCategoryName(current.getCategoryId()));
        if(!current.notes.equals("")) {
            holder.IncomeItemViewNotes.setText(String.valueOf(current.notes));
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
                        db.incomeDao().delete(current);
                        mIncomes.remove(current);
                        notifyDataSetChanged();

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

    void setIncomes(List<Income> Incomes){
        mIncomes = Incomes;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mIncomes has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mIncomes != null)
            return mIncomes.size();
        else return 0;
    }

}



