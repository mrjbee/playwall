package org.monroe.team.playwall.mvp.model;

import org.monroe.team.playwall.integration.FSManager;
import org.monroe.team.playwall.ApplicationContext;
import org.monroe.team.playwall.lifecycle.ShutdownManager;
import org.monroe.team.playwall.logging.Logs;
import org.monroe.team.playwall.process.ProcessManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 4:16 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class DefaultPublicModel implements PublicModel{

    private final FSManager fsManager;
    private final ProcessManager processManager;
    private final ShutdownManager shutdownManager;

    private PublicModelListener listener;

    private Future<List<Launcher>> launcherListHolder;
    private GameDetailsManager gameDetailsManager;

    private ExecutorService backgroundWorker = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"MODEL");
        }
    });

    public DefaultPublicModel(ApplicationContext applicationContext) {
        fsManager = applicationContext.getManager(FSManager.class);
        processManager = applicationContext.getManager(ProcessManager.class);
        shutdownManager = applicationContext.getManager(ShutdownManager.class);
        gameDetailsManager = new GameDetailsManager(fsManager);
    }


    @Override
    public void initialize() {
         launcherListHolder = submit(new Callable<List<Launcher>>() {
             @Override
             public List<Launcher> call() throws Exception {
                 List<Properties> propertieses = fsManager.readLaunchers();
                 List<Launcher> answer = new ArrayList<Launcher>(10);
                 for (Properties propertiese : propertieses) {
                     Launcher launcher = readLauncher(propertiese);
                     if (launcher != null) answer.add(launcher);
                 }
                 return answer;
             }
         });
        submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                List<Launcher> launcherList = launcherListHolder.get();
                List<Launcher> gameLaunchers = new ArrayList<Launcher>(launcherList.size());
                for (Launcher launcher : launcherList) {
                    if ("game".equalsIgnoreCase(launcher.type)){
                        gameLaunchers.add(launcher);
                    }
                }
                gameDetailsManager.start(gameLaunchers);
                return null;
            }
        });
    }

    @Override
    public void destroy() {
       gameDetailsManager.stop();
    }

    @Override
    public void setListener(PublicModelListener listener) {
        this.listener = listener;
    }

    @Override
    public void requestLauncherListUpdate() {
        submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                List<Launcher> launchers = launcherListHolder.get();
                if (listener != null) {
                    listener.onLauncherListUpdateDone(launchers);
                }
                return null;
            }
        });
    }


    @Override
    public void launch(final Launcher launcher) {
        submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (launcher.shutdown){
                    if (processManager.execute(launcher.exec, launcher.dir, launcher.proc, null)){
                        shutdownManager.shutdown();
                    } else {
                        if (listener != null){
                            listener.onProcessFailToStart(launcher);
                        }
                    }
                    return null;
                }
                boolean answer = processManager.execute(launcher.exec, launcher.dir, launcher.proc, new ProcessManager.ProcessCallback() {
                    @Override
                    public void onProcessStart() {
                        if (listener != null){
                            listener.onProcessStart(launcher);
                        }
                    }

                    @Override
                    public void onProcessFailsToStart() {
                        if (listener != null){
                            listener.onProcessFailToStart(launcher);
                        }
                    }

                    @Override
                    public void onProcessDone() {
                        if (listener != null){
                            listener.onProcessStop(launcher);
                        }
                    }
                });
                if (!answer){
                    if (listener != null){
                        listener.onProcessFailToStart(launcher);
                    }
                }
                return null;
            }
        });
    }

    @Override
    public GameDetails getGameInfo(Launcher launcher) {
        return gameDetailsManager.get(launcher);
    }

     private <Type> Future<Type> submit(final Callable<Type> callable){
        return backgroundWorker.submit(new Callable<Type>() {
            @Override
            public Type call() throws Exception {
                Type answer = null;
                try{
                    answer = callable.call();
                } catch (RuntimeException e){
                    Logs.MAIN.w(e, "During executing background work");
                    throw e;
                }
                return answer;
            }
        });
    }

    private Launcher readLauncher(Properties props) {
        try{
            String id = readProperty(props, "file.name", null);
            String title = readProperty(props, "title", null);
            String type = readProperty(props, "type", "game");
            String dir = readProperty(props, "dir", ".");
            String proc = readProperty(props, "proc", null);
            String dbId = readProperty(props, "id", "");
            String[] exec = readProperty(props, "exec", null).split(",,,");
            boolean shutdown = Boolean.parseBoolean(readProperty(props, "shutdown", "false"));
            return new Launcher(id,title,type, dir, exec, proc, dbId,shutdown);
        }catch (IllegalArgumentException e){
            Logs.MAIN.w(null, "Couldn`t parse launcher. No key = %s in props = %s",e.getMessage(), props);
            return null;
        }
    }

    private String readProperty(Properties props, String id, String defaultValue){
        String answer = props.getProperty(id, defaultValue);
        if (answer == null) throw new IllegalArgumentException(id);
        return answer;
    }
}
