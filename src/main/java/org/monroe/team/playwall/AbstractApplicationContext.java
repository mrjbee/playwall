package org.monroe.team.playwall;

import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.gamepad.GamePadManager;
import org.monroe.team.playwall.integration.DesktopManager;
import org.monroe.team.playwall.integration.EnvManager;
import org.monroe.team.playwall.integration.FSManager;
import org.monroe.team.playwall.lifecycle.ShutdownManager;
import org.monroe.team.playwall.process.ProcessManager;

import java.util.HashMap;
import java.util.Map;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:58 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    private final Map<Class,Object> managerMap = new HashMap<Class, Object>();

    protected AbstractApplicationContext(final Closure<Void,Void> shutdownClosure) {
        initManagers(shutdownClosure);
    }

    private void initManagers(Closure<Void, Void> shutdownClosure) {
        managerMap.put(DesktopManager.class, new DesktopManager());
        managerMap.put(GamePadManager.class, GamePadManager.newInstance());
        managerMap.put(EnvManager.class, new EnvManager());
        managerMap.put(FSManager.class, new FSManager(getManager(EnvManager.class)));
        managerMap.put(ProcessManager.class,new ProcessManager(getManager(EnvManager.class)));
        initShutdownManager(shutdownClosure);

        for (Map.Entry<Class, Object> managerEntry : managerMap.entrySet()) {
            if (managerEntry.getValue() instanceof ApplicationContextAware){
                ((ApplicationContextAware)managerEntry.getValue()).onContextStart();
            }
        }
    }

    private void initShutdownManager(final Closure<Void, Void> shutdownClosure) {
        managerMap.put(ShutdownManager.class, new ShutdownManager() {
            @Override
            public void shutdown() {
                shutdownClosure.call(null);
            }
        });
    }

    @Override
    public <Type> Type getManager(Class<Type> managerClass) {
        if (!managerMap.containsKey(managerClass))
            throw new IllegalStateException("Unknown manager for = " + managerClass.getName());
        return (Type) managerMap.get(managerClass);
    }

    /*final protected ContentView createContentView(){
        ContentView answer = new SimpleLauncherListView(this);
        answer.initialize();
        return answer;
    }*/

    @Override
    public void destroy() {
        for (Map.Entry<Class, Object> managerEntry : managerMap.entrySet()) {
            if (managerEntry.getValue() instanceof ApplicationContextAware){
                ((ApplicationContextAware)managerEntry.getValue()).onContextStop();
            }
        }
    }

}
