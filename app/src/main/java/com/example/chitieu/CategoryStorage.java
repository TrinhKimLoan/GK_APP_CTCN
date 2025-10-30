package com.example.chitieu;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryStorage {
    private static final String FILE_NAME = "categories.json";

    public static List<Category> loadCategories(Context ctx) {
        try {
            FileInputStream fis = ctx.openFileInput(FILE_NAME);
            InputStreamReader reader = new InputStreamReader(fis);
            Type listType = new TypeToken<ArrayList<Category>>() {}.getType();
            List<Category> list = new Gson().fromJson(reader, listType);
            reader.close();
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveCategories(Context ctx, List<Category> list) {
        try {
            FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            new Gson().toJson(list, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
