package com.wsti.expensemanager.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.wsti.expensemanager.data.model.User;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserMapAdapter implements JsonSerializer<Map<User, String>>, JsonDeserializer<Map<User, String>> {

    private static final String USER_KEY = "user";
    private static final String VALUE_KEY = "value";

    @Override
    public JsonElement serialize(Map<User, String> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<User, String> entry : src.entrySet()) {
            serializeEntry(context, jsonObject, entry);
        }

        return jsonObject;
    }

    @Override
    public Map<User, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<User, String> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            deserializeEntry(context, map, entry);
        }

        return map;
    }

    private void serializeEntry(JsonSerializationContext context, JsonObject jsonObject, Map.Entry<User, String> entry) {
        User user = entry.getKey();
        String value = entry.getValue();
        JsonObject userJson = new JsonObject();
        JsonElement serializedUser = context.serialize(user);
        String userId = user.getUserId();
        userJson.add(USER_KEY, serializedUser);
        userJson.addProperty(VALUE_KEY, value);
        jsonObject.add(userId, userJson);
    }

    private void deserializeEntry(JsonDeserializationContext context, Map<User, String> map, Map.Entry<String, JsonElement> entry) {
        JsonObject userJson = entry.getValue().getAsJsonObject();
        JsonElement userKey = userJson.get(USER_KEY);
        User user = context.deserialize(userKey, User.class);
        String value = userJson.get(VALUE_KEY).getAsString();
        map.put(user, value);
    }
}
