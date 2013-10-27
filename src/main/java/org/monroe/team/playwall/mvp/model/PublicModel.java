package org.monroe.team.playwall.mvp.model;

import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:46 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface PublicModel {

    void initialize();
    void destroy();
    void setListener(PublicModelListener listener);
    void requestLauncherListUpdate();

    void launch(Launcher launcher);

    GameDetails getGameInfo(Launcher launcher);

    public interface PublicModelListener {
        public void onLauncherListUpdateDone(List<Launcher> launcherList);
        public void onNavigationDone(Launcher launcher);
        public void onProcessStart(Launcher launcher);
        public void onProcessFailToStart(Launcher launcher);
        public void onProcessStop(Launcher launcher);
    }
}
