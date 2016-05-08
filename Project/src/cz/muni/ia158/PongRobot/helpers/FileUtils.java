package cz.muni.ia158.PongRobot.helpers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {


	public static List<String> getLines(String path) {
        try {
            return java.nio.file.Files.readAllLines(Paths.get(path));
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
	}

}
