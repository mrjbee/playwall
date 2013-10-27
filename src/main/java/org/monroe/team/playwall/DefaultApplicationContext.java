package org.monroe.team.playwall;

import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.integration.DesktopManager;
import org.monroe.team.playwall.mvp.presenter.DefaultUIPresenter;
import org.monroe.team.playwall.mvp.presenter.UIPresenter;
import org.monroe.team.playwall.mvp.ui.JFrameTopViewContainer;
import org.monroe.team.playwall.mvp.ui.TopViewContainer;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 3:58 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class DefaultApplicationContext extends AbstractApplicationContext {

    private UIPresenter uiPresenter;
    private TopViewContainer topViewContainer;

    public DefaultApplicationContext(Closure<Void, Void> shutdownClosure) {
        super(shutdownClosure);
    }

    private TopViewContainer getTopViewContainer() {
        if (topViewContainer == null){
            int[] screenSize = getManager(DesktopManager.class).getScreenSize();
            topViewContainer = new JFrameTopViewContainer(screenSize[0], screenSize[1]);
        }
        return topViewContainer;
    }


    @Override
    public UIPresenter getUI() {
        if (uiPresenter == null){
            DefaultUIPresenter answer = new DefaultUIPresenter(this);
            answer.initialize();
            uiPresenter = answer;
            uiPresenter.installTopViewContainer(getTopViewContainer());
        }
        return uiPresenter;
    }
}
