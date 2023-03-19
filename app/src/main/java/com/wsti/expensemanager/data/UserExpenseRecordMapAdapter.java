package com.wsti.expensemanager.data;

import com.google.gson.*;
import com.wsti.expensemanager.data.model.ExpenseRecord;
import com.wsti.expensemanager.data.model.User;

import java.lang.reflect.Type;
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
        for (Map.Entry<User, List<ExpenseRecord>> entry : src.entrySet()) {
            serializeEntry(context, jsonObject, entry);
        }

        return jsonObject;
    }

    @Override
    public Map<User, List<ExpenseRecord>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<User, List<ExpenseRecord>> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            deserializeEntry(context, map, entry);
        }

        return map;
    }

    private void serializeEntry(JsonSerializationContext context, JsonObject jsonObject, Map.Entry<User, List<ExpenseRecord>> entry) {
        User user = entry.getKey();
        List<ExpenseRecord> expenseRecords = entry.getValue();
        JsonObject userJson = new JsonObject();
        JsonElement serializedUser = context.serialize(user);
        userJson.add(USER_KEY, serializedUser);
        JsonElement serializedExpenseRecords = context.serialize(expenseRecords);
        userJson.add(EXPENSE_RECORDS_KEY, serializedExpenseRecords);
        jsonObject.add(user.getUserId(), userJson);
    }

    private void deserializeEntry(JsonDeserializationContext context, Map<User, List<ExpenseRecord>> map, Map.Entry<String, JsonElement> entry) {
        JsonObject userJson = entry.getValue().getAsJsonObject();
        JsonElement userKey = userJson.get(USER_KEY);
        User user = context.deserialize(userKey, User.class);
        JsonElement expenseRecordsKey = userJson.get(EXPENSE_RECORDS_KEY);
        List<ExpenseRecord> expenseRecords = new ArrayList<>();
        if (expenseRecordsKey != null) {
            JsonArray expenseRecordsJsonArray = expenseRecordsKey.getAsJsonArray();
            for (JsonElement expenseRecordJson : expenseRecordsJsonArray) {
                ExpenseRecord expenseRecord = context.deserialize(expenseRecordJson, ExpenseRecord.class);
                expenseRecords.add(expenseRecord);
            }
        }

        map.put(user, expenseRecords);
    }
}
