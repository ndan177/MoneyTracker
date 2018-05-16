package com.example.nohai.moneytracker;


import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.UI.Debts;

import java.util.List;

public class DebtListAdapter extends
        RecyclerView.Adapter<DebtListAdapter.DebtViewHolder> {
    AppDatabase db;
    private Context myContext;
    private SparseBooleanArray mSelectedItemsIds;

    class DebtViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactItemView;
        private final TextView dateItemView;
        private final TextView debtPriceItemView;
        private final TextView currency;
        private final TextView debtItemViewNotes;
        private final ImageButton imageButton;


        private DebtViewHolder(View itemView) {
            super(itemView);
            contactItemView = itemView.findViewById(R.id.person);
            dateItemView = itemView.findViewById(R.id.date);
            debtItemViewNotes =  itemView.findViewById(R.id.notes);//notes
            imageButton = itemView.findViewById(R.id.img);//arrow image
            currency = itemView.findViewById(R.id.currency);
            debtPriceItemView = itemView.findViewById(R.id.price);
        }
    }

    private final LayoutInflater mInflater;
    private List<Debt> mDebts; // Cached copy of categories

    DebtListAdapter(Context context) { mInflater = LayoutInflater.from(context);
        db = Room.databaseBuilder(context, AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        mSelectedItemsIds = new SparseBooleanArray();
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
        holder.contactItemView.setText(getContactName(current.contactId));


        holder.debtPriceItemView.setText(String.format ("%.2f",current.price));
       if(current.dateLimit!=null)
       {
           holder.dateItemView.setText(String.valueOf((current.dateLimit)).substring(0,10));
       }

        //holder.dateItemView.setText(String.valueOf((current.date)).substring(0,10));
         try {
             if (!current.notes.equals("")) {
                 holder.debtItemViewNotes.setText(String.valueOf(current.notes));
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
         }catch (Exception ex){ holder.imageButton.setVisibility(View.INVISIBLE);}

      //  holder.currency.setText(((Activity) myContext).getIntent().getStringExtra("currency"));
        holder.currency.setText(Debts.currency);

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

    public String getContactName(int contactId) {

        Cursor cursor = null;
        String contactName = "";
        try {
            cursor = myContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)},
                    null);

            int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            // let's just get the first phone
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(phoneIdx);
            }
        } catch (Exception e) {
        }   finally {
            if (cursor != null) {
                cursor.close();
            }
            return contactName;
        }
    }
    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


}



