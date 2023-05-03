package com.wsti.expensemanager.ui.expense;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.ExpenseRepository;
import com.wsti.expensemanager.data.UserRepository;
import com.wsti.expensemanager.data.enums.ExpensePriority;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;
import com.wsti.expensemanager.services.NotificationService;
import com.wsti.expensemanager.textWatcher.DecimalInputWatcher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
    private ExpenseRepository expenseRepository;
    private ExpenseRecord record;
    private ExpenseType expenseType;
    private ExpensePriority priority;
    private ActivityResultLauncher<String[]> launcher;
    private List<String> attachments = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayAttachments();
            } else {
                Button button = findViewById(R.id.attachment_button);
                LinearLayout attachments = findViewById(R.id.attachment_list);
                button.setVisibility(View.GONE);
                attachments.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        super.onCreate(savedInstanceState);
        expenseRepository = ExpenseRepository.getInstance();
        setContentView(R.layout.activity_expense);
        setFabClick();
        setExpenseAmount();
        setExpenseTypeGroup();
        setExpensePriorityGroup();
        setExistingRecordIfCan();
        setDate();
        setReminderDate();
        setAttachmentButton();
    }

    private LocalDateTime getDate() {
        if (record == null) {
            return LocalDateTime.now();
        }

        LocalDateTime localDateTime = record.getInsertedDate();
        if (localDateTime == null) {
            return LocalDateTime.now();
        }

        return localDateTime;
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
        BigDecimal currencyValue = getExpenseCurrencyValue();
        LocalDateTime date = getLocalDateTimeValue(R.id.expense_inserted_date);
        LocalDateTime reminderDate = expenseType == ExpenseType.outcome ? getLocalDateTimeValue(R.id.expense_reminder_date) : null;
        ExpenseRecord newRecord = new ExpenseRecord(
                expenseName,
                expenseType,
                expenseGuid,
                currencyValue,
                date,
                priority,
                reminderDate,
                attachments
        );
        expenseRepository.saveExpenseRecord(record, newRecord, user);
        manageNotification(newRecord);
    }

    private void saveNewRecord() {
        User user = getUser();
        String expenseNameValue = getExpenseName();
        BigDecimal currencyValue = getExpenseCurrencyValue();
        LocalDateTime date = getLocalDateTimeValue(R.id.expense_inserted_date);
        LocalDateTime reminderDate = expenseType == ExpenseType.outcome ? getLocalDateTimeValue(R.id.expense_reminder_date) : null;
        ExpenseRecord expenseRecord = expenseRepository.saveExpenseRecord(
                expenseNameValue,
                expenseType,
                currencyValue,
                user,
                date,
                priority,
                reminderDate,
                attachments
        );
        manageNotification(expenseRecord);
    }

    private void manageNotification(ExpenseRecord newRecord) {
        NotificationService instance = NotificationService.getInstance();
        if (expenseType == ExpenseType.outcome) {
            instance.scheduleOutcomeNotification(newRecord, this);
        } else {
            instance.cancelNotification(this, newRecord);
        }
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
        TextView currencyAmount = findViewById(R.id.expense_amount);
        CharSequence text = currencyAmount.getText();
        String value = text.toString();
        if (value.isEmpty()) {
            return null;
        }

        return new BigDecimal(value);
    }

    private LocalDateTime getLocalDateTimeValue(int id) {
        TextInputEditText expenseInsertedDateEditText = findViewById(id);
        String dateString = expenseInsertedDateEditText.getText().toString();
        if (dateString.isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(dateString, formatter);
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
        } else {
            hidePriority();
            hideReminderDate();
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

        TextView editText = findViewById(R.id.expense_amount);
        BigDecimal currencyValue = record.getCurrencyValue();
        if (currencyValue != null) {
            String text = currencyValue.toString();
            editText.setText(text);
        }

        priority = record.getPriority();
        checkPriority();

        if (expenseType == ExpenseType.outcome) {
            showPriority();
            showReminderDate();
        } else {
            hidePriority();
            hideReminderDate();
        }

        attachments = record.getAttachments();
        if (attachments == null) {
            attachments = new ArrayList<>();
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
                    hidePriority();
                    hideReminderDate();
                    priority = ExpensePriority.none;
                    expenseType = ExpenseType.income;
                } else if (checkedId == R.id.outcome) {
                    showPriority();
                    showReminderDate();
                    priority = ExpensePriority.normal;
                    expenseType = ExpenseType.outcome;
                    checkPriority();
                } else {
                    expenseType = null;
                }
            }
        });
    }

    private void setExpensePriorityGroup() {
        RadioGroup expensePriorityGroup = findViewById(R.id.priority);
        expensePriorityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.high) {
                    priority = ExpensePriority.high;
                } else if (checkedId == R.id.normal) {
                    priority = ExpensePriority.normal;
                } else if (checkedId == R.id.low) {
                    priority = ExpensePriority.low;
                } else {
                    priority = ExpensePriority.none;
                }
            }
        });
    }

    private void setDate() {
        TextInputEditText expenseInsertedDateEditText = findViewById(R.id.expense_inserted_date);
        expenseInsertedDateEditText.setFocusable(false);
        LocalDateTime date = getDate();
        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int day = date.getDayOfMonth();
        int hour = date.getHour();
        int minute = date.getMinute();
        setDateValue(R.id.expense_inserted_date, year, month, day, hour, minute);

        expenseInsertedDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDateTime date = getDate();
                int year = date.getYear();
                int month = date.getMonthValue() - 1;
                int day = date.getDayOfMonth();

                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    }
                }, year, month, day);
                datePickerDialog.show();
                datePickerDialog.setOnDateSetListener((datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    int hour = date.getHour();
                    int minute = date.getMinute();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            ExpenseActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    setDateValue(R.id.expense_inserted_date, selectedYear, selectedMonth, selectedDay, hourOfDay, minute);
                                }
                            },
                            hour,
                            minute,
                            true
                    );
                    timePickerDialog.show();
                });
            }
        });
    }

    private void setDateValue(int id, int selectedYear, int selectedMonth, int selectedDay, int hourOfDay, int minute) {
        TextInputEditText expenseInsertedDateEditText = findViewById(id);
        String formattedDate = String.format("%02d/%02d/%04d %02d:%02d", selectedDay, selectedMonth + 1, selectedYear, hourOfDay, minute);
        expenseInsertedDateEditText.setText(formattedDate);
    }

    private void setReminderDate() {
        TextInputEditText expenseInsertedDateEditText = findViewById(R.id.expense_reminder_date);
        expenseInsertedDateEditText.setFocusable(false);
        expenseInsertedDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDateTime date = getDate();
                int year = date.getYear();
                int month = date.getMonthValue() - 1;
                int day = date.getDayOfMonth();

                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    }
                }, year, month, day);
                datePickerDialog.show();
                datePickerDialog.setOnDateSetListener((datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    int hour = date.getHour();
                    int minute = date.getMinute();
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            ExpenseActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    setDateValue(R.id.expense_reminder_date, selectedYear, selectedMonth, selectedDay, hourOfDay, minute);
                                }
                            },
                            hour,
                            minute,
                            true
                    );
                    timePickerDialog.show();
                });
            }
        });

        if (record == null) {
            return;
        }

        LocalDateTime date = record.getReminderDate();
        if (date != null) {
            int year = date.getYear();
            int month = date.getMonthValue() - 1;
            int day = date.getDayOfMonth();
            int hour = date.getHour();
            int minute = date.getMinute();
            setDateValue(R.id.expense_reminder_date, year, month, day, hour, minute);
        }
    }

    private void hidePriority() {
        RadioGroup expensePriorityGroup = findViewById(R.id.priority);
        TextView priorityText = findViewById(R.id.priority_text);
        priorityText.setVisibility(View.GONE);
        expensePriorityGroup.setVisibility(View.GONE);
    }

    private void showPriority() {
        RadioGroup expensePriorityGroup = findViewById(R.id.priority);
        TextView priorityText = findViewById(R.id.priority_text);
        priorityText.setVisibility(View.VISIBLE);
        expensePriorityGroup.setVisibility(View.VISIBLE);
    }

    private void hideReminderDate() {
        TextInputLayout reminderDate = findViewById(R.id.inserted_reminder_date_layout);
        reminderDate.setVisibility(View.GONE);
    }

    private void showReminderDate() {
        TextInputLayout reminderDate = findViewById(R.id.inserted_reminder_date_layout);
        reminderDate.setVisibility(View.VISIBLE);
    }

    private void checkPriority() {
        RadioGroup expensePriorityGroup = findViewById(R.id.priority);
        int expensePriorityIndex = record != null ? getExpensePriorityIndex() : R.id.normal;
        if (expensePriorityIndex >= 0) {
            expensePriorityGroup.check(expensePriorityIndex);
        }
    }

    private int getExpensePriorityIndex() {
        ExpensePriority priority = record.getPriority();
        boolean overridePriority = priority == ExpensePriority.none && expenseType == ExpenseType.outcome;
        priority = overridePriority ?
                ExpensePriority.normal : priority;
        if (priority == null) {
            return -1;
        }

        switch (priority) {
            case high:
                return R.id.high;
            case normal:
                return R.id.normal;
            case low:
                return R.id.low;
            default:
                return -1;
        }
    }

    private void setAttachmentButton() {
        launcher = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                result -> {
                    if (result != null) {
                        String uri = result.toString();
                        attachments.add(uri);
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        displayAttachments();
                    }
                }
        );
        Button button = findViewById(R.id.attachment_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new String[]{"application/pdf"});
            }
        });
    }

    private void displayAttachments() {
        LinearLayout linearLayout = findViewById(R.id.attachment_list);
        linearLayout.removeAllViews();
        for (String uri : attachments) {
            processUri(linearLayout, uri);
        }
    }

    private void processUri(LinearLayout linearLayout, String uri) {
        String name = getFileName(uri);
        if (name != null) {
            displayFile(linearLayout, name, uri);
        }
    }

    private void displayFile(LinearLayout linearLayout, String name, String uri) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.CENTER);
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setMaxWidth(600);
        ImageButton delete = getImageButton(uri);
        ImageButton preview = getPreviewButton(uri);
        wrapper.addView(textView);
        wrapper.addView(preview);
        wrapper.addView(delete);
        linearLayout.addView(wrapper);
    }

    private ImageButton getPreviewButton(String uri) {
        ImageButton preview = new ImageButton(this);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri correctUri = Uri.parse(uri);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(correctUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        preview.setImageResource(R.drawable.ic_menu_statistics);
        preview.setBackgroundColor(Color.TRANSPARENT);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(120, 120);
        params.leftMargin = 10;
        preview.setLayoutParams(params);

        return preview;
    }

    private ImageButton getImageButton(String uri) {
        ImageButton delete = new ImageButton(this);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachments.remove(uri);
                displayAttachments();
            }
        });
        delete.setImageResource(R.drawable.ic_menu_expenses);
        delete.setBackgroundColor(Color.TRANSPARENT);
        delete.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(120, 120);
        params.leftMargin = 10;
        delete.setLayoutParams(params);

        return delete;
    }

    private String getFileName(String attachmentUri) {
        Uri uri = Uri.parse(attachmentUri);
        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        ContentResolver contentResolver = getContentResolver();
        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                return cursor.getString(nameIndex);
            }
        }

        return null;
    }
}