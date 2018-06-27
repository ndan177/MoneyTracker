package com.example.nohai.moneytracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.example.nohai.moneytracker.UI.NewBorrowFrom;
import com.example.nohai.moneytracker.UI.NewBorrowTo;
import com.example.nohai.moneytracker.Utils.DateHelper;
import com.example.nohai.moneytracker.dao.DebtDao;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DebtListAdapter extends
        RecyclerView.Adapter<DebtListAdapter.DebtViewHolder> {
    AppDatabase db;
    private static Context myContext;

    public static class FireMissilesDialogFragment extends DialogFragment {

        @Override
        public   Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

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
        private final TextView dateLimitItemView;
        private final TextView debtPriceItemView;
        private final TextView currency;
        private final TextView debtItemViewNotes;
        private final ImageButton imageButton;
        private final ImageButton moreItemView;


        private DebtViewHolder(View itemView) {
            super(itemView);
            contactItemView = itemView.findViewById(R.id.person);
            dateItemView = itemView.findViewById(R.id.date);
            dateLimitItemView = itemView.findViewById(R.id.dateLimit);
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
        holder.contactItemView.setText(Debts.getContactName(current.contactId));

        holder.debtPriceItemView.setText(String.format ("%.2f",current.price));
       if(current.dateLimit!=null)
       {
           holder.dateLimitItemView.setText(String.valueOf((DateHelper.displayDateFormatList(current.dateLimit)+"(until)")));
       }
        if(current.date!=null)
        {
            holder.dateItemView.setText(String.valueOf((DateHelper.displayDateFormatList(current.date))));
        }

         try {
             if (!current.notes.equals("")) {
                 holder.debtItemViewNotes.setText(String.valueOf(current.notes));
                 holder.imageButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         View parent = (View) v.getParent().getParent();
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

        holder.currency.setText(Debts.currency);

        holder.moreItemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add("edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(current.borrowTo) {
                                    Intent intentBorrowTo = new Intent (myContext, NewBorrowTo.class);
                                    intentBorrowTo.putExtra( "borrowId",current.getId());
                                    myContext.startActivity(intentBorrowTo);
                                }
                                if(!current.borrowTo) {
                                    Intent intentBorrowFrom = new Intent (myContext, NewBorrowFrom.class);
                                    intentBorrowFrom.putExtra( "borrowId",current.getId());
                                    myContext.startActivity(intentBorrowFrom);
                                }
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
/**Delete**/
                                db.debtDao().delete(current);
                                    mDebts.remove(current);
                                    notifyDataSetChanged();

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


}



