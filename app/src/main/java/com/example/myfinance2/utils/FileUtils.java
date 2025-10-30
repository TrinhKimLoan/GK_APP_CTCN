package com.example.myfinance2.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static <T> List<T> readListFromJson(Context context, String fileName, Type typeOfT) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis)) {
            Gson gson = new GsonBuilder().create();
            List<T> list = gson.fromJson(isr, typeOfT);
            if (list == null) return new ArrayList<>();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T> void writeListToJson(Context context, String fileName, List<T> list) {
        File file = new File(context.getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(list, osw);
            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
