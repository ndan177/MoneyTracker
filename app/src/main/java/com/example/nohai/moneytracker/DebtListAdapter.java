package com.example.nohai.moneytracker;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.nohai.moneytracker.Database.Debt;
import java.util.List;

public class DebtListAdapter extends
        RecyclerView.Adapter<DebtListAdapter.DebtViewHolder> {
    AppDatabase db;
    private Context myContext;

    class DebtViewHolder extends RecyclerView.ViewHolder {

        private DebtViewHolder(View itemView) {
            super(itemView);

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
        View itemView = mInflater.inflate(R.layout.recyclerview_item_list, parent, false);
        return new DebtListAdapter.DebtViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DebtListAdapter.DebtViewHolder holder, int position) {
        Debt current = mDebts.get(position);

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



