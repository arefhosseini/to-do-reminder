package com.fearefull.todoreminder.data.model.other;

import androidx.room.TypeConverter;

import com.fearefull.todoreminder.data.model.db.Repeat;
import com.fearefull.todoreminder.data.model.other.type.AlarmTitleType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class DataConverter implements Serializable {

    @TypeConverter
    public String fromIntegerList(List<Integer> integerList) {
        if (integerList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.toJson(integerList, type);
    }

    @TypeConverter
    public List<Integer> toIntegerList(String integerListString) {
        if (integerListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(integerListString, type);
    }

    @TypeConverter
    public String fromListIntegerList(List<List<Integer>> listIntegerList) {
        if (listIntegerList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        return gson.toJson(listIntegerList, type);
    }

    @TypeConverter
    public List<List<Integer>> toListIntegerList(String listIntegerListString) {
        if (listIntegerListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        return gson.fromJson(listIntegerListString, type);
    }

    @TypeConverter
    public String fromBooleanList(List<Boolean> booleanList) {
        if (booleanList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Boolean>>() {
        }.getType();
        return gson.toJson(booleanList, type);
    }

    @TypeConverter
    public List<Boolean> toBooleanList(String booleanListString) {
        if (booleanListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Boolean>>() {
        }.getType();
        return gson.fromJson(booleanListString, type);
    }

    @TypeConverter
    public String fromRepeatList(List<Repeat> repeatList) {
        if (repeatList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Repeat>>() {
        }.getType();
        return gson.toJson(repeatList, type);
    }

    @TypeConverter
    public List<Repeat> toRepeatList(String repeatListString) {
        if (repeatListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Repeat>>() {
        }.getType();
        return gson.fromJson(repeatListString, type);
    }

    @TypeConverter
    public String fromAlarmTitleType(AlarmTitleType titleType) {
        if (titleType == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<AlarmTitleType>() {
        }.getType();
        return gson.toJson(titleType, type);
    }

    @TypeConverter
    public AlarmTitleType toAlarmTitleType(String AlarmTitleTypeString) {
        if (AlarmTitleTypeString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<AlarmTitleType>() {
        }.getType();
        return gson.fromJson(AlarmTitleTypeString, type);
    }
}