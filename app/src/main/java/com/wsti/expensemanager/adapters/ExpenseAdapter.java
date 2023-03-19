package com.wsti.expensemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.model.ExpenseRecord;

import java.util.List;


public class ExpenseAdapter extends ArrayAdapter<ExpenseRecord> {

    private Context context;
    private List<ExpenseRecord> expenseRecords;

    public ExpenseAdapter(Context context, List<ExpenseRecord> expenseRecords) {
        super(context, 0, expenseRecords);
        this.context = context;
        this.expenseRecords = expenseRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        }

        ExpenseRecord record = expenseRecords.get(position);
        TextView name = listItem.findViewById(R.id.expense_name);
        String recordName = record.getName();
        name.setText(recordName);

        return listItem;
    }
}