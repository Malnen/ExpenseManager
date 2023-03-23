package com.wsti.expensemanager.adapters;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.ui.expense.ExpenseActivity;

import java.util.List;


public class ExpenseAdapter extends ArrayAdapter<ExpenseRecord> {

    private final ActivityResultLauncher<Intent> launcher;
    private final Context context;
    private final List<ExpenseRecord> expenseRecords;

    public ExpenseAdapter(Context context, List<ExpenseRecord> expenseRecords, ActivityResultLauncher<Intent> launcher) {
        super(context, 0, expenseRecords);
        this.launcher = launcher;
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

        View finalListItem = listItem;
        ImageButton editButton = listItem.findViewById(R.id.edit_button);
        editButton.setOnClickListener(view -> {
            onEdit(record, finalListItem);
        });
        ImageButton deleteButton = listItem.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> {
            onDelete(finalListItem, record);
        });

        return listItem;
    }

    private void onEdit(ExpenseRecord record, View finalListItem) {
        Context applicationContext = finalListItem.getContext();
        Intent intent = new Intent(applicationContext, ExpenseActivity.class);
        String json = record.toJson();
        intent.putExtra("record", json);
        launcher.launch(intent);
    }

    private void onDelete(View listItem, ExpenseRecord record) {
        deleteAndNotify(record);
        showToast(listItem, record);
    }

    private void deleteAndNotify(ExpenseRecord record) {
        ExpenseRepository repository = ExpenseRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        User user = userRepository.getUser();
        repository.deleteExpense(record, user);
        notifyDataSetChanged();
    }

    private void showToast(View listItem, ExpenseRecord record) {
        Context applicationContext = listItem.getContext();
        String recordName = record.getName();
        String welcome = context.getString(R.string.expense_deleted, recordName);
        Toast toast = Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG);
        toast.show();
    }
}