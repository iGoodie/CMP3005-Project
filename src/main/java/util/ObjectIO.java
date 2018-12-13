package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectIO {

	public static Object readObject(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {			
			return null;
		}
	}
	
	public static boolean writeObject(Object object, String path) {
		try {
			File file = new File(path);
			
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(object);
			oos.flush();
			oos.close();
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
