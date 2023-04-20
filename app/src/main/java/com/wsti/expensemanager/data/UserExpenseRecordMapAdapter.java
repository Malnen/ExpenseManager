package com.wsti.expensemanager.data;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.wsti.expensemanager.adapters.LocalDateTimeAdapter;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserExpenseRecordMapAdapter implements JsonSerializer<Map<User, List<ExpenseRecord>>>, JsonDeserializer<Map<User, List<ExpenseRecord>>> {

    private static final String USER_KEY = "user";
    private static final String EXPENSE_RECORDS_KEY = "expense_records";

    @Override
    public JsonElement serialize(Map<User, List<ExpenseRecord>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        for (Map.Entry<User, List<ExpenseRecord>> entry : src.entrySet()) {
            serializeEntry(context, jsonObject, gson, entry);
        }

        return jsonObject;
    }

    @Override
    public Map<User, List<ExpenseRecord>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<User, List<ExpenseRecord>> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            deserializeEntry(context, map, gson, entry);
        }

        return map;
    }

    private void serializeEntry(JsonSerializationContext context, JsonObject jsonObject, Gson gson, Map.Entry<User, List<ExpenseRecord>> entry) {
        Type expensesType = new TypeToken< List<ExpenseRecord>>() {
        }.getType();
        User user = entry.getKey();
        List<ExpenseRecord> expenseRecords = entry.getValue();
        JsonObject userJson = new JsonObject();
        JsonElement serializedUser = context.serialize(user);
        userJson.add(USER_KEY, serializedUser);
        JsonElement serializedExpenseRecords = gson.toJsonTree(expenseRecords, expensesType);
        userJson.add(EXPENSE_RECORDS_KEY, serializedExpenseRecords);
        String userId = user.getUserId();
        jsonObject.add(userId, userJson);
    }

    private void deserializeEntry(JsonDeserializationContext context, Map<User, List<ExpenseRecord>> map, Gson gson, Map.Entry<String, JsonElement> entry) {
        JsonObject userJson = entry.getValue().getAsJsonObject();
        JsonElement userKey = userJson.get(USER_KEY);
        User user = context.deserialize(userKey, User.class);
        JsonElement expenseRecordsKey = userJson.get(EXPENSE_RECORDS_KEY);
        List<ExpenseRecord> expenseRecords = new ArrayList<>();
        if (expenseRecordsKey != null) {
            JsonArray expenseRecordsJsonArray = expenseRecordsKey.getAsJsonArray();
            for (JsonElement expenseRecordJson : expenseRecordsJsonArray) {
                ExpenseRecord expenseRecord = gson.fromJson(expenseRecordJson, ExpenseRecord.class);
                expenseRecords.add(expenseRecord);
            }
        }

        map.put(user, expenseRecords);
    }

}
