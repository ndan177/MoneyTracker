package com.example.nohai.moneytracker;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nohai.moneytracker.Database.Debt;
import java.util.List;

public class DebtListAdapter extends
        RecyclerView.Adapter<DebtListAdapter.DebtViewHolder> {
    AppDatabase db;
    private Context myContext;

    class DebtViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactItemView;
        private final TextView dateItemView;
        private final TextView expenseItemViewNotes;
        private final ImageButton imageButton;


        private DebtViewHolder(View itemView) {
            super(itemView);
            contactItemView = itemView.findViewById(R.id.person);
            dateItemView = itemView.findViewById(R.id.date);
            expenseItemViewNotes =  itemView.findViewById(R.id.notes);//notes
            imageButton = itemView.findViewById(R.id.img);//arrow image

        }
    }

    private final LayoutInflater mInflater;
    private List<Debt> mDebts; // Cached copy of categories

    DebtListAdapter(Context context) { mInflater = LayoutInflater.from(context);
        db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        myContext = context;
    }

    @Override
    public DebtListAdapter.DebtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.debt_item, parent, false);
        return new DebtListAdapter.DebtViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DebtListAdapter.DebtViewHolder holder, int position) {
        Debt current = mDebts.get(position);
        holder.contactItemView.setText(String.valueOf(current.contactId));
        holder.dateItemView.setText(String.valueOf((current.date)).substring(0,10));
         try {
             if (!current.notes.equals("")) {
                 holder.expenseItemViewNotes.setText(String.valueOf(current.notes));
                 holder.imageButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         View parent = (View) v.getParent().getParent().getParent();
                         if (parent.findViewById(R.id.notes).getVisibility() == View.GONE)
                             parent.findViewById(R.id.notes).setVisibility(View.VISIBLE);
                         else
                             parent.findViewById(R.id.notes).setVisibility(View.GONE);
                         //TODO: rotate image
                     }
                 });
             } else
                 holder.imageButton.setVisibility(View.INVISIBLE);
         }catch (Exception ex){   holder.imageButton.setVisibility(View.INVISIBLE);}

    }

    void setDebts(List<Debt> debts){
        mDebts = debts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mDebts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mDebts != null)
            return mDebts.size();
        else return 0;
    }
}



