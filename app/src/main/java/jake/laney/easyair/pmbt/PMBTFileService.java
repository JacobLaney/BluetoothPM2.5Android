package jake.laney.easyair.pmbt;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jake.laney.easyair.pmbt.interfaces.EnumPMBTDates;

/**
 * Created by JakeL on 10/29/17.
 */

/*
 * Provides methods for storing and reading pm2.5 data from the local device
 * This method is a work in progress, provides some functionality for read/write of pm2.5 values
 * to a local storage file.
 */
public class PMBTFileService {
    private final String TAG = "PMBT File Service";
    public static final String DATA_FILE = "PM_SENSOR_DATA";

    private File dataFile; // data is stored as a Date and pmValue pair

    public PMBTFileService(Context context) {
        try {
            dataFile = getOrCreateFile(context, PMBTFileService.DATA_FILE);
            refactorFiles();
        } catch (IOException e) {
            Log.e(TAG, "File service failed to init!");
        }
    }

    private File getOrCreateFile(Context context, String path) throws IOException {
        File f = new File(context.getFilesDir(), path);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }

    // TODO - compress the file system to save space
    private void refactorFiles() throws IOException {

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // write a new value to the storage
    public void write(int pmValue, Date time) throws IOException {
        if (!isExternalStorageWritable()) {
            throw new IOException();
        }
        FileWriter fw = new FileWriter(dataFile, true); // append
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(new Date().getTime());
        pw.println(pmValue);
        pw.close();
    }

    // write a new value to the storage
    public void write(int pmValue) throws IOException {
        write(pmValue, new Date());
    }

    // read data the file that falls in a specific time period
    public List<PMBTDataModel> read(EnumPMBTDates timePeriod) throws IOException {
        if (!isExternalStorageWritable()) {
            throw new IOException();
        }

        Date desiredDate;
        if (timePeriod == EnumPMBTDates.DAY) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            desiredDate = c.getTime();
        } else {
            desiredDate = new Date();
        }

        FileReader fr = new FileReader(dataFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Date time;
        while ((line = br.readLine()) != null
                && br.readLine() != null
                && (time = new Date(Long.parseLong(line))).getTime() < desiredDate.getTime());

        // now the br is at the correct line
        List<PMBTDataModel> objects = new LinkedList<>();
        while ((line = br.readLine()) != null) {
            Date d = new Date(Long.parseLong(line));
            int v = Integer.valueOf(br.readLine());
            objects.add(new PMBTDataModel(d, v));
        }
        br.close();
        return objects;
    }

    // simply get all the pm2.5 data
    public List<PMBTDataModel> readAll() throws IOException {
        if (!isExternalStorageWritable()) {
            throw new IOException();
        }
        FileReader fr = new FileReader(dataFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        List<PMBTDataModel> objects = new LinkedList<>();
        while ((line = br.readLine()) != null) {
            try {
                Date d = new Date(Long.parseLong(line));
                int v = Integer.valueOf(br.readLine());
                objects.add(new PMBTDataModel(d, v));
            } catch (Exception e) {

            }
        }
        br.close();
        return objects;
    }
}
