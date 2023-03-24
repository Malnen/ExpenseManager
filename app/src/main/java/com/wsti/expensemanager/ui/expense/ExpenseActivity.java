package com.wsti.expensemanager.ui.expense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.textWatcher.DecimalInputWatcher;

import java.math.BigDecimal;

public class ExpenseActivity extends AppCompatActivity {
    private ExpenseRepository expenseRepository;
    private ExpenseRecord record;
    private ExpenseType expenseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseRepository = ExpenseRepository.getInstance();
        setContentView(R.layout.activity_expense);
        setFabClick();
        setExpenseAmount();
        setExpenseTypeGroup();
        setExistingRecordIfCan();
    }

    private void setFabClick() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });
    }

    private void onFabClick() {
        boolean isValidName = validateExpenseName();
        boolean isValidAmount = validateExpenseCurrencyAmount();
        boolean isValidType = validateExpenseType();
        if (isValidName && isValidAmount && isValidType) {
            save();
            finish();
        }
    }

    private void save() {
        if (record != null) {
            saveExistingRecord();
        } else {
            saveNewRecord();
        }
    }

    private void saveExistingRecord() {
        User user = getUser();
        String expenseName = getExpenseName();
        String expenseGuid = record.getGuid();
        BigDecimal currencyValue = record.getCurrencyValue();
        ExpenseRecord newRecord = new ExpenseRecord(expenseName, expenseType, expenseGuid, currencyValue);
        expenseRepository.saveExpenseRecord(record, newRecord, user);
    }

    private void saveNewRecord() {
        User user = getUser();
        String expenseNameValue = getExpenseName();
        BigDecimal currencyValue = getExpenseCurrencyValue();
        expenseRepository.saveExpenseRecord(expenseNameValue, expenseType, currencyValue, user);
    }

    private boolean validateExpenseName() {
        String expenseNameValue = getExpenseName();
        boolean isValid = expenseNameValue.length() > 0;
        if (!isValid) {
            showToast("Fill expense name");
        }

        return isValid;
    }

    private boolean validateExpenseCurrencyAmount() {
        BigDecimal currencyValue = getExpenseCurrencyValue();
        boolean isValid = currencyValue != null;
        if (!isValid) {
            showToast("Amount cannot be empty");
        }

        return isValid;
    }

    private boolean validateExpenseType() {
        boolean isValid = expenseType != null;
        if (!isValid) {
            showToast("Choose expense type");
        }

        return isValid;
    }

    private void showToast(String message) {
        Context applicationContext = getApplicationContext();
        Toast toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG);
        toast.show();
    }

    private String getExpenseName() {
        TextView expenseName = findViewById(R.id.expense_name);
        CharSequence text = expenseName.getText();

        return text.toString();
    }

    private BigDecimal getExpenseCurrencyValue() {
        EditText currencyAmount = findViewById(R.id.expense_amount);
        CharSequence text = currencyAmount.getText();
        String value = text.toString();
        if (value.isEmpty()) {
            return null;
        }

        return new BigDecimal(value);
    }

    private User getUser() {
        UserRepository repository = UserRepository.getInstance();
        return repository.getUser();
    }

    private void setExistingRecordIfCan() {
        Intent intent = getIntent();
        String record = intent.getStringExtra("record");
        if (record != null) {
            setExistingRecord(record);
        }
    }

    private void setExistingRecord(String json) {
        record = ExpenseRecord.fromJson(json);
        TextView expenseName = findViewById(R.id.expense_name);
        String recordName = record.getName();
        expenseName.setText(recordName);

        expenseType = record.getExpenseType();
        RadioGroup expenseTypeGroup = findViewById(R.id.income_outcome);
        int expenseTypeIndex = getExpenseTypeIndex();
        if (expenseTypeIndex >= 0) {
            expenseTypeGroup.check(expenseTypeIndex);
        }

        EditText editText = findViewById(R.id.expense_amount);
        BigDecimal currencyValue = record.getCurrencyValue();
        if (currencyValue != null) {
            String text = currencyValue.toString();
            editText.setText(text);
        }
    }

    private int getExpenseTypeIndex() {
        ExpenseType expenseType = record.getExpenseType();
        if (expenseType == null) {
            return -1;
        }

        switch (expenseType) {
            case income:
                return R.id.income;
            case outcome:
                return R.id.outcome;
            default:
                return -1;
        }
    }

    private void setExpenseAmount() {
        EditText editText = findViewById(R.id.expense_amount);
        editText.addTextChangedListener(new DecimalInputWatcher(editText));
    }

    private void setExpenseTypeGroup() {
        RadioGroup expenseTypeGroup = findViewById(R.id.income_outcome);
        expenseTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.income) {
                    expenseType = ExpenseType.income;
                } else if (checkedId == R.id.outcome) {
                    expenseType = ExpenseType.outcome;
                } else {
                    expenseType = null;
                }
            }
        });
    }

}