package org.monroe.team.playwall.mvp.presenter;

import org.monroe.team.playwall.common.Closure;
import org.monroe.team.playwall.mvp.model.PublicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/22/13 Time: 11:55 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public abstract class AbstractStateUIPresenter <UIStateType extends AbstractStateUIPresenter.UIState> implements UIPresenter {

    private UIStateType currentState;
    private final List<UIStateType> stateList = new ArrayList<UIStateType>(3);

    final synchronized public void installState(UIStateType uiState){
       stateList.add(uiState);
    }

    final synchronized public void uninstallState(UIStateType uiState){
        stateList.remove(uiState);
    }

    final public synchronized UIStateType getCurrentState() {
        return currentState;
    }

    final public synchronized  void notifyUIState(Closure<UIStateType, Void> action){
        for(int i = stateList.size() -1; i>-1; i--){
            action.call(stateList.get(i));
        }
    }

    final public synchronized  void activate(String stateId){
        UIStateType answer = getUIStateById(stateId);
        activate(answer);
    }

    final public synchronized void activate(UIStateType answer) {
        if (answer == null) throw new NullPointerException();
        UIStateType was = currentState;
        currentState = answer;
        currentState.onActivate();
        if (was != null){
           was.onDeactivate();
        }
    }

    private UIStateType getUIStateById(String stateId) {
        UIStateType answer = null;
        for (UIStateType uiState : stateList) {
             if (stateId.equals(uiState.getId())){
                 answer = uiState;
                 break;
             };
        }
        return answer;
    }

    @Override
    final public void showUI(PublicModel model) {
        UIStateType type = showUIImpl(model);
        activate(type);
    }

    protected abstract UIStateType showUIImpl(PublicModel model);

    @Override
    final public void closeUI() {
        if(currentState != null) currentState.onDeactivate();
        closeUIImpl();
    }

    protected abstract void closeUIImpl();




    public static interface UIState {
        public String getId();
        public void onActivate();
        public void onDeactivate();


    }

    public static abstract class AbstractUIState implements UIState {

        private final String id;
        private boolean active = false;
        private boolean firstTimeActivation = true;

        protected AbstractUIState(String id) {
            this.id = id;
        }

        @Override
        final public String getId() {
            return id;
        }

        @Override
        final public void onActivate() {
            active = true;
            if (firstTimeActivation){
                firstTimeActivation = false;
                onFirstTimeActivateImpl();
            }  else {
                onActivateImpl();
            }
        }

        @Override
        final public void onDeactivate() {
            active = false;
            onDeactivateImpl();
        }

        public boolean isActive() {
            return active;
        }

        protected abstract void onActivateImpl();

        protected void onFirstTimeActivateImpl(){
            onActivateImpl();
        };

        protected abstract void onDeactivateImpl();



    }

}
