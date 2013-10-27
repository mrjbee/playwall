package org.monroe.team.playwall.mvp.presenter.state;

import org.monroe.team.playwall.mvp.model.Launcher;
import org.monroe.team.playwall.mvp.presenter.DefaultUIPresenter;
import org.monroe.team.playwall.mvp.ui.components.SystemItemsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: MisterJBee
 * Date: 10/23/13 Time: 12:40 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class SystemActionState extends DefaultUIPresenter.AbstractDefaultUIPresenterState{

    private final static SystemItemsView.Item GAME_LIST_ITEM = new SystemItemsView.Item(
            "GAME_LAUNCHER",
            "Games",
            null);
    private Map<SystemItemsView.Item, Launcher> itemLauncherMap = new HashMap<SystemItemsView.Item, Launcher>();
    private boolean awaitingForApplyRelease = false;
    private boolean awaitingForDecision = false;
    private boolean awaitingForApplyReleaseForDecision = false;
    private Launcher awaitingLauncher;

    public SystemActionState(String id, DefaultUIPresenter owner) {
        super(id, owner);
    }

    @Override
    protected void onFirstTimeActivateImpl() {
        getOwner().requestLauncherList();
    }

    @Override
    protected void onActivateImpl() {
       getOwner().showSystemPanel();
       getOwner().closeGameView();
    }

    @Override
    protected void onDeactivateImpl() {

    }

    @Override
    public void onLauncherListUpdate(List<Launcher> launcherList) {
        if (isActive()){
            itemLauncherMap.clear();
            List<SystemItemsView.Item> itemList = new ArrayList<SystemItemsView.Item>(3);
            for (Launcher launcher : launcherList) {
                if ("system".equalsIgnoreCase(launcher.type)){
                    SystemItemsView.Item item = convert(launcher);
                    itemList.add(item);
                    itemLauncherMap.put(item, launcher);
                }
            }
            itemList.add(0, GAME_LIST_ITEM);
            getOwner().reInitSystemPanel(itemList);
            getOwner().showSystemPanel();
        }
    }

    private SystemItemsView.Item convert(Launcher launcher) {
        return new SystemItemsView.Item(
                launcher.id,
                launcher.title,
                null);
    }

    @Override
    public void onDown() {
       if (!isActive() || !isMenuTraversAllowed()) return;
       getOwner().selectNextSystemItem();
    }

    @Override
    public void onUp() {
        if (!isActive() || !isMenuTraversAllowed()) return;
        getOwner().selectPrevSystemItem();
    }

    @Override
    public void onLeft() {
        onNextDecision();
    }

    private void onNextDecision() {
        if (!isActive()) return;
        if (!awaitingForDecision) return;
        if (awaitingForApplyReleaseForDecision) return;
        getOwner().selectOtherSystemDecision();
    }

    @Override
    public void onRight() {
        onNextDecision();
    }

    @Override
    public void onApplyDown() {
        if (!isActive()) return;
        if (!awaitingForDecision){
            awaitingForApplyRelease = true;
            getOwner().pushSelectedSystemItem();
        } else {
            awaitingForApplyReleaseForDecision = true;
            getOwner().pushSelectedSystemDecition();
        }

    }

    @Override
    public void onApplyUp() {
        if (!isActive()) return;
        if (!awaitingForApplyRelease && !awaitingForApplyReleaseForDecision) return;
        if(!awaitingForDecision){
            awaitingForApplyRelease = false;
            SystemItemsView.Item selectedItem = getOwner().getSelectedSystemItem();
            processSelecteditem(selectedItem);
        }else {
            boolean yes = getOwner().releaseSelectedSystemDecision();
            Runnable closeDecisionMenuCallback = null;
            if (yes){
                closeDecisionMenuCallback = new Runnable() {
                  @Override
                  public void run() {
                     // getOwner().releaseSelectedSystemItem();
                      getOwner().closeSystemPanel();
                      getOwner().launch(awaitingLauncher);
                  }
              };
            } else {
                awaitingForApplyReleaseForDecision = false;
                awaitingForApplyRelease=false;
                awaitingForDecision = false;
                getOwner().releaseSelectedSystemItem();
            }
            getOwner().closeApprovalPanel(closeDecisionMenuCallback);
        }
    }

    private void processSelecteditem(SystemItemsView.Item selectedItem) {
        Launcher launcher = this.itemLauncherMap.get(selectedItem);
        if (GAME_LIST_ITEM.equals(selectedItem)){
            awaitingForDecision = false;
            awaitingForApplyRelease = false;
            getOwner().releaseSelectedSystemItem();
            getOwner().activateGameMode();
        } else if (launcher.shutdown){
            awaitingForDecision = true;
            awaitingLauncher = launcher;
            getOwner().showApprovalPanel();
        } else {
            throw new RuntimeException("Not supported!");
        }
    }

    public boolean isMenuTraversAllowed() {
        return !awaitingForApplyRelease && !awaitingForDecision;
    }
}
