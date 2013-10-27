package org.monroe.team.playwall;

import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.gamepad.GamePadManager;
import org.monroe.team.playwall.logging.Logs;
import org.monroe.team.playwall.mvp.model.DefaultPublicModel;
import org.monroe.team.playwall.mvp.model.PublicModel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * User: MisterJBee
 * Date: 10/16/13 Time: 11:13 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class Starter {

    private static ApplicationContext context;
    private static PublicModel publicModel;

    static public void main(String[] args) throws IOException {
        Logs.MAIN.i("Starting up PlayWall...");
        disableJavaLogging();
        context = new DefaultApplicationContext(createShutdownOperation());
        publicModel = new DefaultPublicModel(context);
        publicModel.initialize();
        registrateShutdown();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                context.getUI().showUI(publicModel);
            }
        });
    }

    private static Closure<Void, Void> createShutdownOperation() {
        return new Closure<Void, Void>() {
            @Override
            public Void call(Void in) {
                //WILL invoke shutdown hook
                System.exit(0);
                return null;
            }
        };
    }

    private static void registrateShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread("DOWN-HOOK"){
            @Override
            public void run() {
                doShutdown();
            }
        });
    }

    private static void doShutdown() {
        Logs.MAIN.i("Shutdown PlayWall...");
        context.getUI().closeUI();
        publicModel.destroy();
        context.destroy();
    }

    private static void disableJavaLogging() {
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);
    }
}
