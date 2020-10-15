package com.sgtc.launcher.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreams {

    public static InputStream get(Context context, String path, boolean external) throws IOException {
        return (external)?get(path):get(context, path);
    }

    public static InputStream get(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static InputStream get(String path) throws FileNotFoundException {
        return get(new File(path));
    }

    public static InputStream get(Context context, String path) throws IOException {
        return context.getAssets().open(path);
    }

    public static String toString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
