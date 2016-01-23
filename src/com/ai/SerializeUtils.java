package com.ai;

import java.io.*;

/**
 * Created by victor on 26.12.15.
 */
public class SerializeUtils {
    public static Object getObject(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fin);
        return ois.readObject();
    }

    public static void saveObject(Object object, String filename) throws IOException {
        FileOutputStream fout = new FileOutputStream(filename, false);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(object);
    }
}
