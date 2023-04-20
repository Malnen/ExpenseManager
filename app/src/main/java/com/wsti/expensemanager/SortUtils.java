package com.wsti.expensemanager;

import com.wsti.expensemanager.data.enums.SortOption;

import java.util.Arrays;
import java.util.List;

public class SortUtils {

    private static final List<Integer> NAME_BUTTONS = Arrays.asList(
            R.id.ascending_name_button,
            R.id.descending_name_button
    );
    private static final List<Integer> AMOUNT_BUTTONS
            = Arrays.asList(
            R.id.ascending_amount_button,
            R.id.descending_amount_button
    );
    private static final List<Integer> TYPE_BUTTONS = Arrays.asList(
            R.id.ascending_type_button,
            R.id.descending_type_button
    );
    private static final List<Integer> INSERTED_DATE_BUTTONS = Arrays.asList(
            R.id.ascending_inserted_date_button,
            R.id.descending_inserted_date_button
    );

    public static boolean isAsc(int checkedId) {
        List<Integer> ascButtons = Arrays.asList(
                R.id.ascending_name_button,
                R.id.ascending_amount_button,
                R.id.ascending_type_button,
                R.id.ascending_inserted_date_button
        );
        return ascButtons.stream().anyMatch(integer -> integer == checkedId);
    }

    public static SortOption getSortOption(int checkedId) {
        if (NAME_BUTTONS.contains(checkedId)) {
            return SortOption.NAME;
        } else if (AMOUNT_BUTTONS.contains(checkedId)) {
            return SortOption.AMOUNT;
        } else if (TYPE_BUTTONS.contains(checkedId)) {
            return SortOption.TYPE;
        } else if (INSERTED_DATE_BUTTONS.contains(checkedId)) {
            return SortOption.INSERTED_DATE;
        }

        return null;
    }

    public static Integer getButtonId(SortOption sortOption, boolean asc) {
        if (sortOption == null) {
            return null;
        }

        switch (sortOption) {
            case NAME:
                return getButtonIdFromList(NAME_BUTTONS, asc);
            case AMOUNT:
                return getButtonIdFromList(AMOUNT_BUTTONS, asc);
            case TYPE:
                return getButtonIdFromList(TYPE_BUTTONS, asc);
            case INSERTED_DATE:
                return getButtonIdFromList(INSERTED_DATE_BUTTONS, asc);
            default:
                return null;
        }
    }

    private static int getButtonIdFromList(List<Integer> list, boolean asc) {
        return asc ? list.get(0) : list.get(1);
    }

}
