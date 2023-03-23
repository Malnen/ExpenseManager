package com.wsti.expensemanager.ui.expense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

public class ExpenseActivity extends AppCompatActivity {
    private ExpenseRepository expenseRepository;
    private ExpenseRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseRepository = ExpenseRepository.getInstance();
        setContentView(R.layout.activity_expense);
        setFabClick();
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
        boolean isValid = validateExpenseName();
        if (isValid) {
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
        ExpenseRecord newRecord = new ExpenseRecord(expenseName, expenseGuid);
        expenseRepository.saveExpenseRecord(record, newRecord, user);
    }

    private void saveNewRecord() {
        User user = getUser();
        String expenseNameValue = getExpenseName();
        expenseRepository.saveExpenseRecord(expenseNameValue, user);
    }

    private boolean validateExpenseName() {
        String expenseNameValue = getExpenseName();
        boolean isValid = expenseNameValue.length() > 0;
        if (!isValid) {
            showToast("Fill expense name");
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
    }

}