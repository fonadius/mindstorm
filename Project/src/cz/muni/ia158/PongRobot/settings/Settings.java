/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package cz.muni.ia158.PongRobot.settings;

import cz.muni.ia158.PongRobot.helpers.FileUtils;

/**
 *
 * @author Michal Keda
 */
public class Settings {
    public static String SETTING_PATH = "./SETTINGS.txt";
    public static RuntimeSettings runtimeSettings = new RuntimeSettings();
    
    public static long QueueWaitingTime = 5000; //miliseconds
    
    public static final int COLOR_TO_ORIGIN_TRANSFORM = 200; //distance between color sensor and the center of robot in degrees of wheel rotation
    
    public static  class RuntimeSettings {
        
        private final int serverPort;
        private final String serverUrl;

        
        
        
        private RuntimeSettings() {
        //    String[] settings = FileUtils.getLines(Settings.SETTING_PATH).get(0).split(";");
            
            this.serverUrl = "192.168.43.168";
            this.serverPort = 27015;
      
        }
        
        public int getControlUnitPort() {
            return serverPort;
        }
        
        public String getServerUrl() {
            return serverUrl;
        }
        
        
    }
    
    
}
