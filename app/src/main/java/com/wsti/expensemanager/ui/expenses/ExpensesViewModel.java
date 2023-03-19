package com.wsti.expensemanager.ui.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.util.List;

public class ExpensesViewModel extends ViewModel {

    private final MutableLiveData<List<ExpenseRecord>> expenses;

    public ExpensesViewModel() {
        ExpenseRepository repository = ExpenseRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        User user = userRepository.getUser();
        List<ExpenseRecord> expenses = repository.getExpenses(user);
        this.expenses = new MutableLiveData<>();
        this.expenses.setValue(expenses);
    }

    public LiveData<List<ExpenseRecord>> getExpenses() {
        return expenses;
    }
}