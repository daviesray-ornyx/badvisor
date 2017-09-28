package com.apptuned.betadvisor;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by davies on 9/26/17.
 */

public class JSONFileHandler {

    public Context context;
    private String jsonString;

    private FileOutputStream outputStream;
    private FileInputStream inputStream;


    public JSONFileHandler(Context context){
        this.context = context;
    }

    public String readJSONFile(String filename){
        // Reads file content from the JSON file
        File file = new File(context.getFilesDir(), filename);
        String json = null;
        try{
            if(!file.exists())
                return "";

            int length = (int) file.length();
            byte[] bytes = new byte[length];
             inputStream = new FileInputStream(file);
            try {
                inputStream.read(bytes);
            } finally {
                inputStream.close();
            }

            json = new String(bytes);

        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // TODO Write function that saves JSON content into file with option to overrite, append or prepend

    public boolean writeJSONFile(String filename, String jsonStringVal){
        File file = new File(context.getFilesDir(), filename);
        boolean status = true;
        try {
            if(!file.exists())
                file.createNewFile();

            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonStringVal.getBytes());
            outputStream.close();
        } catch (IOException e){
            status = false;
        }
        return status;
    }
}
