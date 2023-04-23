package com.wsti.expensemanager.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.wsti.expensemanager.R;
import com.wsti.expensemanager.data.enums.ExpenseType;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.databinding.FragmentStatisticsBinding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private CheckBox incomeCheckbox;
    private CheckBox outcomeCheckbox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        StatisticsViewModel statisticsViewModel = viewModelProvider.get(StatisticsViewModel.class);
        statisticsViewModel.update();
        MutableLiveData<List<ExpenseRecord>> income = statisticsViewModel.getIncomeData();
        MutableLiveData<List<ExpenseRecord>> outcome = statisticsViewModel.getOutcomeData();
        incomeCheckbox = root.findViewById(R.id.income_checkbox);
        outcomeCheckbox = root.findViewById(R.id.outcome_checkbox);
        incomeCheckbox.setChecked(true);
        outcomeCheckbox.setChecked(true);
        incomeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChart(root, income, outcome);
            }
        });
        outcomeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChart(root, income, outcome);
            }
        });
        setChart(root, income, outcome);

        return root;
    }

    private void setChart(View root, MutableLiveData<List<ExpenseRecord>> income, MutableLiveData<List<ExpenseRecord>> outcome) {
        LineChart chart = root.findViewById(R.id.chart);
        Map<LocalDate, BigDecimal> incomeData = new TreeMap<>();
        Map<LocalDate, BigDecimal> outcomeData = new TreeMap<>();

        if (incomeCheckbox.isChecked()) {
            setChartValues(income, incomeData);
        }

        if (outcomeCheckbox.isChecked()) {
            setChartValues(outcome, outcomeData);
        }

        List<Entry> incomeEntries = new ArrayList<>();
        List<Entry> outcomeEntries = new ArrayList<>();
        Set<String> labels = new HashSet<>();

        setDataEntries(incomeData, incomeEntries, labels);
        setDataEntries(outcomeData, outcomeEntries, labels);

        LineDataSet incomeDataSet = getLineDataSet(incomeEntries, R.string.income, Color.GREEN);
        LineDataSet outcomeDataSet = getLineDataSet(outcomeEntries, R.string.outcome, Color.RED);

        LineData lineData = new LineData(incomeDataSet, outcomeDataSet);

        chart.setData(lineData);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setCenterAxisLabels(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getDescription().setEnabled(false);
        chart.animateXY(200, 200);
    }

    private void setChartValues(MutableLiveData<List<ExpenseRecord>> income, Map<LocalDate, BigDecimal> incomeData) {
        for (ExpenseRecord expense : income.getValue()) {
            LocalDateTime insertedDate = expense.getInsertedDate();
            LocalDate date = insertedDate.toLocalDate();
            BigDecimal amount = incomeData.getOrDefault(date, BigDecimal.ZERO);
            BigDecimal currencyValue = expense.getCurrencyValue();
            BigDecimal bigDecimal = amount.add(currencyValue);
            incomeData.put(date, bigDecimal);
        }
    }

    private void setDataEntries(Map<LocalDate, BigDecimal> data, List<Entry> entries, Set<String> labels) {
        int i = 0;
        for (Map.Entry<LocalDate, BigDecimal> entry : data.entrySet()) {
            BigDecimal value = entry.getValue();
            float floatValue = value.floatValue();
            Entry newEntry = new Entry(i, floatValue);
            entries.add(newEntry);
            LocalDate key = entry.getKey();
            String date = key.toString();
            labels.add(date);
            i++;
        }
    }

    private LineDataSet getLineDataSet(List<Entry> incomeEntries, int label, int color) {
        String labelValue = getString(label);
        LineDataSet incomeDataSet = new LineDataSet(incomeEntries, labelValue);
        incomeDataSet.setColor(color);
        incomeDataSet.setLineWidth(4f);
        incomeDataSet.setValueTextSize(14f);
        incomeDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.2f", value) + " PLN";
            }
        });
        return incomeDataSet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}