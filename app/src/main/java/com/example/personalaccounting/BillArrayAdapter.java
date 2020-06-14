package com.example.personalaccounting;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class BillArrayAdapter extends ArrayAdapter<Bill> {
    private Context context;
    private ArrayList<Bill> billList = new ArrayList<>();

    public BillArrayAdapter(@NonNull Context context, ArrayList<Bill> billList){
        super(context, 0 , billList);
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem =convertView;
        if (listItem == null) listItem = LayoutInflater.from(context).inflate(R.layout.bill_record_item, parent, false);

        Bill currentBill = billList.get(position);
        TextView dateView = listItem.findViewById(R.id.dateView_billRecord);
        dateView.setText(currentBill.getDateText());

        TextView typeView = listItem.findViewById(R.id.typeView);
        typeView.setText(currentBill.getType());

        TextView amountView = listItem.findViewById(R.id.amountView);
        amountView.setText(String.valueOf(currentBill.getAmount()));
        amountView.setGravity(Gravity.CENTER);
        if(currentBill.getAmount() < 0)
            amountView.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
        else
            amountView.setTextColor(ContextCompat.getColor(getContext(), R.color.income));
        return listItem;
    }
}