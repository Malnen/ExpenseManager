package com.wsti.expensemanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.material.textview.MaterialTextView;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.enums.ExpensePriority;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.enums.SortOption;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.ui.expense.ExpenseActivity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ExpenseAdapter extends ArrayAdapter<ExpenseRecord> implements Filterable {

    private final ActivityResultLauncher<Intent> launcher;
    private final Context context;
    private final ExpenseRepository expenseRepository;

    private List<ExpenseRecord> expenseRecords;

    public ExpenseAdapter(Context context, List<ExpenseRecord> expenseRecords, ActivityResultLauncher<Intent> launcher) {
        super(context, 0, expenseRecords);
        this.launcher = launcher;
        this.context = context;
        this.expenseRepository = ExpenseRepository.getInstance();
        this.expenseRecords = new ArrayList<>(expenseRecords);
    }

    public void refresh() {
        List<ExpenseRecord> expenseRecords = expenseRepository.getExpenses();
        this.expenseRecords = new ArrayList<>(expenseRecords);
    }

    @Override
    public int getCount() {
        return expenseRecords.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        }

        ExpenseRecord record = expenseRecords.get(position);

        TextView priorityLabel = listItem.findViewById(R.id.expense_item_priority_label);
        TextView priorityView = listItem.findViewById(R.id.expense_item_priority);
        ExpensePriority priority = record.getPriority();
        int showPriority = priority == ExpensePriority.none ? View.GONE : View.VISIBLE;
        priorityLabel.setVisibility(showPriority);
        priorityView.setVisibility(showPriority);
         int priorityColor = getPriorityColor(priority);
        priorityView.setTextColor(priorityColor);

        TextView insertedDateView = listItem.findViewById(R.id.expense_item_inserted_date);
        LocalDateTime date = record.getInsertedDate();
        String insertedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        insertedDateView.setText(insertedDate);

        TextView name = listItem.findViewById(R.id.expense_name);
        String recordName = record.getName();
        name.setText(recordName);

        Context applicationContext = listItem.getContext();
        MaterialTextView amount = listItem.findViewById(R.id.expense_item_amount);
        setAmountText(record, applicationContext, amount);

        ImageView expenseTypeImage = listItem.findViewById(R.id.expense_type_image);
        setExpenseTypeImage(expenseTypeImage, record);

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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ExpenseRecord> originalExpenses = expenseRepository.getExpenses();
                FilterResults filterResults = new FilterResults();
                List<ExpenseRecord> filteredExpenseRecords = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredExpenseRecords.addAll(originalExpenses);
                } else {
                    setFilter(constraint, filteredExpenseRecords, originalExpenses);
                }
                filterResults.values = filteredExpenseRecords;
                filterResults.count = filteredExpenseRecords.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                expenseRecords.clear();
                expenseRecords.addAll((List<ExpenseRecord>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void sort(boolean asc, SortOption sortOption) {
        sort(new Comparator<ExpenseRecord>() {
            @Override
            public int compare(ExpenseRecord firstRecord, ExpenseRecord secondRecord) {
                ExpenseRecord firstToCompare = asc ? secondRecord : firstRecord;
                ExpenseRecord secondToCompare = asc ? firstRecord : secondRecord;

                switch (sortOption) {
                    case NAME:
                        return firstToCompare.getName().compareTo(secondToCompare.getName());
                    case TYPE:
                        return firstToCompare.getExpenseType().compareTo(secondToCompare.getExpenseType());
                    case AMOUNT:
                        return firstToCompare.getCurrencyValue().compareTo(secondToCompare.getCurrencyValue());
                    case INSERTED_DATE:
                        return firstToCompare.getInsertedDate().compareTo(secondToCompare.getInsertedDate());
                    default:
                        return 0;
                }
            }
        });
    }

    private int getPriorityColor(ExpensePriority priority) {
        switch (priority) {
            case high:
                return Color.RED;
            case normal:
                return Color.BLUE;
            case low:
                return Color.GREEN;
            default:
                return 0;
        }
    }

    private void setFilter(CharSequence constraint, List<ExpenseRecord> filteredExpenseRecords, List<ExpenseRecord> originalExpenses) {
        String filterPattern = constraint.toString().toLowerCase().trim();
        for (ExpenseRecord expenseRecord : originalExpenses) {
            String rawName = expenseRecord.getName();
            String name = rawName.toLowerCase();
            if (name.contains(filterPattern)) {
                filteredExpenseRecords.add(expenseRecord);
            }
        }
    }

    private void setAmountText(ExpenseRecord record, Context applicationContext, MaterialTextView amount) {
        BigDecimal currencyValue = record.getCurrencyValue();
        if (currencyValue != null) {
            setItemAmountText(applicationContext, amount, currencyValue);
        } else {
            amount.setText("");
        }
    }

    private void setItemAmountText(Context applicationContext, MaterialTextView amount, BigDecimal currencyValue) {
        String currencyTextValue = currencyValue.toString();
        String currencyText = applicationContext.getString(R.string.item_amount, currencyTextValue);
        amount.setText(currencyText);
    }

    private void setExpenseTypeImage(ImageView expenseTypeImage, ExpenseRecord record) {
        int id = getExpenseTypeImageId(record);
        expenseTypeImage.setImageResource(id);
    }

    private int getExpenseTypeImageId(ExpenseRecord record) {
        ExpenseType expenseType = record.getExpenseType();
        if (expenseType == null) {
            return 0;
        }

        switch (expenseType) {
            case income:
                return R.drawable.ic_arrow_up;
            case outcome:
                return R.drawable.ic_arrow_down;
            default:
                return 0;
        }
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
        UserRepository userRepository = UserRepository.getInstance();
        User user = userRepository.getUser();
        expenseRepository.deleteExpense(record, user);
        expenseRecords = expenseRepository.getExpenses();
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