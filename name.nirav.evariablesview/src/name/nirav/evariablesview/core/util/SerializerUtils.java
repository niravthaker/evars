package name.nirav.evariablesview.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

public class SerializerUtils {
	public static void serialize(File file,Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		JBossObjectOutputStream outputStream = new JBossObjectOutputStream(
				new BufferedOutputStream(fos));
		outputStream.writeObject(obj);
		outputStream.close();
		fos.close();
	}
	public static Object deserialize(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		JBossObjectInputStream inputStream = new JBossObjectInputStream(new BufferedInputStream(fis));
		Object object = inputStream.readObject();
		inputStream.close();
		fis.close();
		return object;
	}
}
