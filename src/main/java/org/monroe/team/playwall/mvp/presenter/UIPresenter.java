package org.monroe.team.playwall.mvp.presenter;

import org.monroe.team.playwall.mvp.model.PublicModel;
import org.monroe.team.playwall.mvp.ui.TopViewContainer;

/**
 * User: MisterJBee
 * Date: 10/18/13 Time: 9:57 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface UIPresenter {
    public void showUI(PublicModel model);
    public void installTopViewContainer(TopViewContainer viewContainer);
    public void closeUI();
}
