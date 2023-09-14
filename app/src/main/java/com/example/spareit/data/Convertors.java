package com.example.spareit.data;

import android.util.Pair;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Convertors {

    @TypeConverter
    public static String fromPairToString(Pair<String,Integer> p){
        return p.first+"~"+p.second;
    }

    @TypeConverter
    public static Pair<String,Integer> fromStringToPair(String s){
        String[] a=s.split("~");
        return new Pair<String,Integer>(a[0],Integer.parseInt(a[1]));
    }

    @TypeConverter
    public static List<Pair<String,Integer>> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Pair<String,Integer>>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Pair<String,Integer>> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }
}
