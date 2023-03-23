package com.wsti.expensemanager.ui.expenses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.adapters.ExpenseAdapter;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.databinding.FragmentExpensesBinding;
import com.wsti.expensemanager.ui.expense.ExpenseActivity;

import java.util.List;
import java.util.stream.Collectors;


public class ExpensesFragment extends Fragment {
    private ExpenseAdapter adapter;
    private FragmentExpensesBinding binding;
    private ActivityResultLauncher<Intent> launcher;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        ExpensesViewModel expensesViewModel = viewModelProvider.get(ExpensesViewModel.class);
        binding = FragmentExpensesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LiveData<List<ExpenseRecord>> expensesData = expensesViewModel.getExpenses();
        List<ExpenseRecord> expenses = expensesData.getValue();
        Context context = getContext();
        ListView listView = root.findViewById(R.id.expenses_list_view);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        adapter.notifyDataSetChanged();
                    }
                });

        adapter = new ExpenseAdapter(context, expenses, launcher);
        listView.setAdapter(adapter);
        setFabClick(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
}