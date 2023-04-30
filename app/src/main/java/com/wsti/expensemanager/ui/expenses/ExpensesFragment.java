package com.wsti.expensemanager.ui.expenses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.SortUtils;
import com.wsti.expensemanager.adapters.ExpenseAdapter;
import com.wsti.expensemanager.data.enums.SortOption;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.databinding.FragmentExpensesBinding;
import com.wsti.expensemanager.ui.expense.ExpenseActivity;

import java.util.Comparator;
import java.util.List;


public class ExpensesFragment extends Fragment {
    private ExpenseAdapter adapter;
    private FragmentExpensesBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private boolean asc = false;
    private SortOption sortOption = SortOption.INSERTED_DATE;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<ExpenseRecord> expenses = getExpenses();
        Context context = getContext();
        ListView listView = root.findViewById(R.id.expenses_list_view);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        sort();
                        adapter.notifyDataSetChanged();
                    }
                });

        adapter = new ExpenseAdapter(context, expenses, launcher);
        sort();
        listView.setAdapter(adapter);
        setFabClick(root);
        setFilterField(root);
        setSortButton();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<ExpenseRecord> getExpenses() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        ExpensesViewModel expensesViewModel = viewModelProvider.get(ExpensesViewModel.class);
        LiveData<List<ExpenseRecord>> expensesData = expensesViewModel.getExpenses();

        return expensesData.getValue();
    }

    private void setFabClick(View root) {
        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = root.getContext();
                Intent intent = new Intent(context, ExpenseActivity.class);
                launcher.launch(intent);
            }
        });
    }

    private void setFilterField(View root) {
        EditText filterEditText = root.findViewById(R.id.filter_text_field);
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void filterExpenses(CharSequence s) {
        Filter filter = adapter.getFilter();
        filter.filter(s);
        sort();
    }

    private void setSortButton() {
        View root = binding.getRoot();
        ImageButton sortButton = root.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortDialog();
            }
        });
    }

    private void showSortDialog() {
        View root = binding.getRoot();
        Context context = root.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sort_dialog, null);
        LinearLayout dialogContent = dialogView.findViewById(R.id.sort_dialog_content);
        RadioGroup radioGroup = dialogContent.findViewById(R.id.sort_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onSort(checkedId);
            }
        });
        initSelectedSortOption(radioGroup);
        builder.setView(dialogView);
        builder.setTitle("Sort by");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initSelectedSortOption(RadioGroup radioGroup) {
        Integer selectedSortButtonId = SortUtils.getButtonId(sortOption, asc);
        if (selectedSortButtonId != null) {
            RadioButton radioButton = radioGroup.findViewById(selectedSortButtonId);
            radioButton.setChecked(true);
        }
    }

    private void onSort(int checkedId) {
        asc = SortUtils.isAsc(checkedId);
        sortOption = SortUtils.getSortOption(checkedId);
        sort();
        adapter.notifyDataSetChanged();
    }

    private void sort() {
        List<ExpenseRecord> freshRecords = getExpenses();
        adapter.refresh();
        adapter.sort(asc, sortOption);
    }
}