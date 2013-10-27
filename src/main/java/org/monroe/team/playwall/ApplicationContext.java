package org.monroe.team.playwall;

import org.monroe.team.playwall.mvp.presenter.UIPresenter;
import org.monroe.team.playwall.mvp.ui.TopViewContainer;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:54 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface ApplicationContext {
    public UIPresenter getUI();
    public <Type> Type getManager(Class<Type> managerClass);
    void destroy();

    public interface ApplicationContextAware {
        public void onContextStart();
        public void onContextStop();
    }
}
