package org.monroe.team.playwall.integration;

import org.monroe.team.playwall.logging.Logs;
import sun.misc.IOUtils;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 6:46 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class FSManager {

    private final File appFolder;

    public FSManager(EnvManager envManager) {
        this.appFolder = new File(envManager.getAppFolder());
    }


    public List<Properties> readLaunchers() {
        File launchersFolder = new File(appFolder, "launchers");
        File[] launchers = launchersFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().endsWith(".properties");
            }
        });
        List<Properties> answer = new ArrayList<Properties>(10);
        if (launchers != null){
            for (File launcher : launchers) {
                Properties properties = read(launcher);
                if (properties != null) answer.add(properties);
            }

        }
        return answer;
    }

    private Properties read(File launcher) {
        Properties answer = new Properties();
        try {
            answer.load(new FileInputStream(launcher));
            answer.setProperty("file.name",launcher.getName());
        } catch (IOException e) {
            Logs.MAIN.w(e, "Error reading launcher = %s",launcher);
            return null;
        }
        return answer;
    }

    public BufferedImage getBackgroundImage(){
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(appFolder, "background.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return img;
    }

    public File getGameInfoFolder(String dbId) {
        File userAppFolderFile = getUserFolder();
        return new File(userAppFolderFile,dbId);
    }

    public File getUserFolder() {
        String userFolder = System.getProperty("user.home", appFolder.getAbsolutePath());
        File userFolderFile = new File(userFolder);
        File userAppFolderFile = new File(userFolderFile, ".playwall");
        if (!userAppFolderFile.exists()){
            userAppFolderFile.mkdirs();
        }
        return userAppFolderFile;
    }
}
