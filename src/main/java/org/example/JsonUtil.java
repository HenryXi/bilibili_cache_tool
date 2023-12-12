package org.example;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;


public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapDeserializerDoubleAsIntFix())
            .create();

    private static final Type STR_AND_OBJ_MAP = new TypeToken<HashMap<String, Object>>() {
    }.getType();
    private static final Type STR_LIST = new TypeToken<ArrayList<String>>() {
    }.getType();

    private JsonUtil() {
    }


    public static List<String> getStrListFromJsonObject(JsonObject jsonObject, String str) {
        if (jsonObject == null) {
            return new ArrayList<>();
        }
        JsonArray jsonArray = jsonObject.getAsJsonArray(str);
        return GSON.fromJson(jsonArray, STR_LIST);
    }


    public static String toJsonString(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return GSON.fromJson(str, clazz);
    }

    public static Map<String, Object> getStrAndObjMap(String str) {
        return GSON.fromJson(str, STR_AND_OBJ_MAP);
    }

    public static List<String> getStrList(String str) {
        return GSON.fromJson(str, STR_LIST);
    }

    public static List<String> getStrList(JsonArray jsonArray) {
        return GSON.fromJson(jsonArray, STR_LIST);
    }

    public static String getString(JsonObject jsonObject, String key) {
        return getString(jsonObject, key, null);
    }

    public static String getString(JsonObject jsonObject, String key, String defaultValue) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsString();
            } else {
                return defaultValue;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static Integer getInteger(JsonObject jsonObject, String key) {
        return getInteger(jsonObject, key, null);
    }

    public static Integer getInteger(JsonObject jsonObject, String key, Integer defaultValue) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsInt();
            } else {
                return defaultValue;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static Long getLong(JsonObject jsonObject, String key) {
        if (!jsonObject.has(key)) {
            return 0L;
        }
        return jsonObject.get(key).getAsLong();
    }

    public static Boolean getBoolean(JsonObject jsonObject, String key) {
        return getBoolean(jsonObject, key, false);
    }

    public static Boolean getBoolean(JsonObject jsonObject, String key, Boolean defaultValue) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsBoolean();
            } else {
                return defaultValue;
            }
        } catch (Exception ex) {
            return defaultValue;
        }
    }


    // 防止int、float转换成double
    private static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

        @Override
        public Map<String, Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return deserializeMethod(jsonElement);
        }

        public Map<String, Object> deserializeMethod(JsonElement in) {
            Map<String, Object> map = new LinkedTreeMap<>();
            JsonObject obj = in.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entitySet) {
                map.put(entry.getKey(), read(entry.getValue()));
            }
            return map;
        }

        public Object read(JsonElement in) {
            if (in.isJsonArray()) {
                List<Object> list = new ArrayList<>();
                JsonArray arr = in.getAsJsonArray();
                for (JsonElement anArr : arr) {
                    list.add(read(anArr));
                }
                return list;
            } else if (in.isJsonObject()) {
                Map<String, Object> map = new LinkedTreeMap<>();
                JsonObject obj = in.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for (Map.Entry<String, JsonElement> entry : entitySet) {
                    map.put(entry.getKey(), read(entry.getValue()));
                }
                return map;
            } else if (in.isJsonPrimitive()) {
                JsonPrimitive prim = in.getAsJsonPrimitive();
                if (prim.isBoolean()) {
                    return prim.getAsBoolean();
                } else if (prim.isString()) {
                    return prim.getAsString();
                } else if (prim.isNumber()) {
                    Number num = prim.getAsNumber();
                    // here you can handle double int/long values
                    // and return any type you want
                    // this solution will transform 3.0 float to long values
                    if (Math.ceil(num.doubleValue()) == num.longValue()) {
                        if (num.longValue() > Integer.MAX_VALUE || num.longValue() < Integer.MIN_VALUE) {
                            return num.longValue();
                        } else {
                            return num.intValue();
                        }
                    } else {
                        return num.doubleValue();
                    }
                }
            }
            return null;
        }
    }
}
