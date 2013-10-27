package org.monroe.team.playwall.mvp.presenter.state;

import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.mvp.model.GameDetails;
import org.monroe.team.playwall.mvp.model.Launcher;
import org.monroe.team.playwall.mvp.presenter.DefaultUIPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/27/13 Time: 2:57 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class GameMenuState extends DefaultUIPresenter.AbstractDefaultUIPresenterState{

    private boolean awaitingForRelease = false;
    private ArrayList<Launcher> gameLaunchers;
    private int selectedLauncherIndx = 0;
    private boolean awaitTransition = false;
    private boolean awaitForProcessToFinish;

    public GameMenuState(String id, DefaultUIPresenter owner) {
        super(id, owner);
    }

    @Override
    protected void onFirstTimeActivateImpl() {
        new Thread(){
            @Override
            public void run() {
                GameDetails details = getOwner().getGameInfo(gameLaunchers.get(selectedLauncherIndx));
                if (details.covertImage != null){
                    details.prepearedCover = getOwner().scaleCover(details.covertImage);
                }
                getOwner().closeSystemPanel();
                getOwner().openGameView(details);
            }
        }.start();
      }

    @Override
    protected void onActivateImpl() {
        getOwner().closeSystemPanel();
        getOwner().openGameView();
    }

    @Override
    protected void onDeactivateImpl() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onApplyUp() {
        if(!isActive()) return;
        if (awaitTransition) return;
        if (awaitForProcessToFinish) return;
        awaitingForRelease = false;
        String action = getOwner().releaseSelectedGameAction();
        if (action.toLowerCase().contains("back")){
            getOwner().activateHomeMode();
        } else {
            awaitForProcessToFinish = true;
            getOwner().closeGameView();
            getOwner().launch(gameLaunchers.get(selectedLauncherIndx));
        }
    }

    @Override
    public void onProcessFail(Launcher launcher) {
        if(!isActive()) return;
        awaitForProcessToFinish=false;
        getOwner().openGameView();
    }

    @Override
    public void onProcessStop(Launcher launcher) {
        if(!isActive()) return;
        awaitForProcessToFinish=false;
        getOwner().openGameView();
    }

    @Override
    public void onApplyDown() {
        if(!isActive()) return;
        if (awaitTransition) return;
        if (awaitForProcessToFinish) return;
        awaitingForRelease = true;
        getOwner().pushSelectedGameAction();
    }

    @Override
    public void onLeft() {
        if(!isActive()) return;
        if(awaitingForRelease) return;
        if (awaitForProcessToFinish) return;
        processHorizontalMovement();
    }

    @Override
    public void onRight() {
        if(!isActive()) return;
        if(awaitingForRelease) return;
        if (awaitForProcessToFinish) return;
        processHorizontalMovement();
    }

    @Override
    public void onUp() {
        if(!isActive()) return;
        if (awaitTransition) return;
        if (awaitingForRelease) return;
        if (awaitForProcessToFinish) return;
        awaitTransition = true;
        Launcher launcher = getPrevLauncher();
        GameDetails gameDetails = getOwner().getGameInfo(launcher);
        gameDetails.prepearedCover = getOwner().scaleCover(gameDetails.covertImage);
        getOwner().selectGameAnimateFromBottom(gameDetails, new Closure<Void, Void>() {
            @Override
            public Void call(Void in) {
                awaitTransition = false;
                return null;
            }
        });
    }

    @Override
    public void onDown() {
        if(!isActive()) return;
        if (awaitTransition) return;
        if (awaitingForRelease) return;
        if (awaitForProcessToFinish) return;
        awaitTransition = true;
        Launcher launcher = getNextLauncher();
        GameDetails gameDetails = getOwner().getGameInfo(launcher);
        gameDetails.prepearedCover = getOwner().scaleCover(gameDetails.covertImage);
        getOwner().selectGameAnimateFromTop(gameDetails, new Closure<Void, Void>() {
            @Override
            public Void call(Void in) {
                awaitTransition = false;
                return null;
            }
        });
    }

    private void processHorizontalMovement() {
        getOwner().selectNextGameAction();
    }

    @Override
    public void onLauncherListUpdate(List<Launcher> launcherList) {
        gameLaunchers = new ArrayList<Launcher>(launcherList.size());
        for (Launcher launcher : launcherList) {
            if ("game".equalsIgnoreCase(launcher.type)){
                gameLaunchers.add(launcher);
            }
        }
    }

    private Launcher getNextLauncher() {
        selectedLauncherIndx -= 1;
        if (selectedLauncherIndx < 0){
            selectedLauncherIndx = gameLaunchers.size()-1;
        }
        return gameLaunchers.get(selectedLauncherIndx);
    }

    public Launcher getPrevLauncher() {
        selectedLauncherIndx += 1;
        if (selectedLauncherIndx >= gameLaunchers.size()){
            selectedLauncherIndx = 0;
        }
        return gameLaunchers.get(selectedLauncherIndx);
    }
}
