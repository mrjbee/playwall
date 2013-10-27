package org.monroe.team.playwall.process;

import org.monroe.team.playwall.ApplicationContext;
import org.monroe.team.playwall.integration.EnvManager;
import org.monroe.team.playwall.logging.Logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 10:47 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class ProcessManager implements ApplicationContext.ApplicationContextAware{

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"PROCESS_RUNNER");
        }
    });

    private final EnvManager envManager;

    public ProcessManager(EnvManager envManager) {
        this.envManager = envManager;
    }

    public boolean execute(String[] exec, String dir, final String checkProcess, final ProcessCallback callback){
        Logs.MAIN.d("Starting process. Exec = %s", exec[0]);
        ProcessBuilder builder = new ProcessBuilder(exec);
        builder.directory(new File(dir));
        try {
            Process ps = builder.start();
        } catch (IOException e) {
            Logs.MAIN.w(e,"Couldn`t start process = %s in dir %s",exec[0],dir);
            return false;
        }

        if(callback == null) return true;

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                int checkDelayMs = 2000;
                int startupRetryCount = 10;
                int shutdownRetryCount = 1;

                Logs.MAIN.d("Assuming process start up");
                int retryDone = 0;
                boolean started = false;
                while (retryDone < startupRetryCount && !started){
                    retryDone++;
                    Logs.MAIN.d("Waiting process to start. Try count = %d", retryDone);
                    try {
                        Thread.sleep(checkDelayMs);
                    } catch (InterruptedException e) {
                        Logs.MAIN.w(e,"Awaiting for process start was interrupted");
                        return;
                    }
                    started = isProcessStarted(checkProcess);
                }
                if (!started){
                    Logs.MAIN.d("Process seems fails to start. Try count = %d", retryDone);
                    callback.onProcessFailsToStart();
                    return;
                }
                Logs.MAIN.d("Process seems to start. After try count = %d", retryDone);
                callback.onProcessStart();
                boolean running = true;
                int notRunningTimes = 0;
                while (running || notRunningTimes<shutdownRetryCount){
                    try {
                        Thread.sleep(checkDelayMs);
                    } catch (InterruptedException e) {
                        Logs.MAIN.w(e,"Awaiting for process stop was interrupted");
                        return;
                    }
                    running = isProcessStarted(checkProcess);
                    notRunningTimes++;
                    if (running) notRunningTimes = 0;
                }
                callback.onProcessDone();
            }

        });
        return true;
    }

    private boolean isProcessStarted(String checkProcess) {
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            Process p = null;
            if (envManager.isWindows()){
                p = Runtime.getRuntime().exec
                        (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            } else {
                p = Runtime.getRuntime().exec("ps -e");
            }
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                builder.append(line).append("\n");
            }
            input.close();
        } catch (Exception ex) {
            Logs.MAIN.w(ex,"During checking process");
            return false;
        }
        return builder.toString().contains(checkProcess);
    }

    @Override
    public void onContextStart() {}

    @Override
    public void onContextStop() {
    }

    public static interface ProcessCallback {
        public void onProcessStart();
        public void onProcessFailsToStart();
        public void onProcessDone();
    }

}
