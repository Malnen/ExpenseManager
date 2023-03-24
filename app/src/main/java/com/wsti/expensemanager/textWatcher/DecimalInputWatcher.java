package com.wsti.expensemanager.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class DecimalInputWatcher implements TextWatcher {
    private final EditText editText;

    public DecimalInputWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString().trim();
        if (input.isEmpty()) {
            return;
        }

        int decimalIndex = input.indexOf('.');
        if (decimalIndex >= 0) {
            int numDigitsAfterDecimal = input.length() - decimalIndex - 1;
            if (numDigitsAfterDecimal > 2) {
                String newInput = input.substring(0, decimalIndex + 3);
                editText.setText(newInput);
                editText.setSelection(newInput.length());
            }
        }
    }
}