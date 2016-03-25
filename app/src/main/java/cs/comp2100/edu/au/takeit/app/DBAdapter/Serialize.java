package cs.comp2100.edu.au.takeit.app.DBAdapter;

import java.io.*;

/**
 * Created by Sina on 25/03/2016.
 */
public class Serialize {

    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream i = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(i);
        oo.writeObject(o);
        return i.toByteArray();
    }

    public static Object deserialize(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream i = new ByteArrayInputStream(b);
        ObjectInputStream ii = new ObjectInputStream(i);
        return ii.readObject();
    }
}
