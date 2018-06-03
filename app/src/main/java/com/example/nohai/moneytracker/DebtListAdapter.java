package com.example.nohai.moneytracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.UI.Debts;
import com.example.nohai.moneytracker.dao.DebtDao;

import java.util.List;

public class DebtListAdapter extends
        RecyclerView.Adapter<DebtListAdapter.DebtViewHolder> {
    AppDatabase db;
    private Context myContext;
    private static boolean deleteMe = false;
    public static class FireMissilesDialogFragment extends DialogFragment {

        @Override
        public   Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteMe = true;
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    class DebtViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactItemView;
        private final TextView dateItemView;
        private final TextView debtPriceItemView;
        private final TextView currency;
        private final TextView debtItemViewNotes;
        private final ImageButton imageButton;
        private final ImageButton moreItemView;


        private DebtViewHolder(View itemView) {
            super(itemView);
            contactItemView = itemView.findViewById(R.id.person);
            dateItemView = itemView.findViewById(R.id.date);
            debtItemViewNotes =  itemView.findViewById(R.id.notes);//notes
            imageButton = itemView.findViewById(R.id.img);//arrow image
            currency = itemView.findViewById(R.id.currency);
            debtPriceItemView = itemView.findViewById(R.id.price);
            moreItemView = itemView.findViewById(R.id.more);

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
        final Debt current = mDebts.get(position);
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
        holder.moreItemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add("edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                //do what u want
                                return true;
                            }
                        });
                        if(current.resolved)
                            menu.add("unresolved").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    current.resolved = false;
                                    db.debtDao().update(current);
                                    mDebts.remove(current);
                                    notifyDataSetChanged();
                                    return true;
                                }
                            });
                        else
                            menu.add("resolved").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    current.resolved = true;
                                    db.debtDao().update(current);
                                    mDebts.remove(current);
                                    notifyDataSetChanged();

                                    return true;
                                }
                            });
                        menu.add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                DialogFragment newFragment = new FireMissilesDialogFragment();
                                newFragment.show(((Activity) myContext).getFragmentManager(), "yesNo");
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






}



