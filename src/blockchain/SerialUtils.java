package blockchain;

import java.io.*;

public class SerialUtils {

    public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object loadedChain = ois.readObject();
        ois.close();
        fis.close();
        return loadedChain;
    }

    public static void serialize(Object obj, String fileName) throws IOException{
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
        fos.flush();
        fos.close();
    }
}
