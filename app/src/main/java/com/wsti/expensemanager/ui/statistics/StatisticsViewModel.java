package com.wsti.expensemanager.ui.statistics;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends ViewModel {

    private final MutableLiveData<List<ExpenseRecord>> income;
    private final MutableLiveData<List<ExpenseRecord>> outcome;

    public StatisticsViewModel() {
        income = new MutableLiveData<>();
        outcome = new MutableLiveData<>();
        update();
    }

    public void update() {
        ExpenseRepository repository = ExpenseRepository.getInstance();
        UserRepository userRepository = UserRepository.getInstance();
        User user = userRepository.getUser();
        List<ExpenseRecord> expenses = repository.getExpenses(user);
        List<ExpenseRecord> incomeExpenses = new ArrayList<>();
        List<ExpenseRecord> outcomeExpenses = new ArrayList<>();
        for (ExpenseRecord expenseRecord : expenses) {
            processExpense(incomeExpenses, outcomeExpenses, expenseRecord);
        }

        income.setValue(incomeExpenses);
        outcome.setValue(outcomeExpenses);
    }

    private void processExpense(List<ExpenseRecord> incomeExpenses, List<ExpenseRecord> outcomeExpenses, ExpenseRecord expenseRecord) {
        ExpenseType type = expenseRecord.getExpenseType();
        boolean isIncome = type == ExpenseType.income;
        if (isIncome) {
            incomeExpenses.add(expenseRecord);
        } else {
            outcomeExpenses.add(expenseRecord);
        }
    }

    public MutableLiveData<List<ExpenseRecord>> getIncomeData() {
        return income;
    }

    public MutableLiveData<List<ExpenseRecord>> getOutcomeData() {
        return outcome;
    }
}