package org.monroe.team.playwall.gamepad;

import org.monroe.team.playwall.logging.Logs;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 1:37 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
//TODO: not supporting game pad hotswap
public class GamePadManager {

    private final GamePadAdapter gamePadAdapter;
    private GamePadManagerListener listener;
    private Timer timer;
    private boolean[] lastButtonPressedState;
    private PovDirection lastPovDirection = PovDirection.CENTER;

    public static GamePadManager newInstance(){
        GamePadAdapter padAdapter = new GamePadAdapter();
        try{
           padAdapter.initialize();
        } catch (GamePadDetectException e){
            Logs.JINPUT.w(e,"Gamepad disabled");
            padAdapter = null;
        }
        return new GamePadManager(padAdapter);
    }

    private GamePadManager(GamePadAdapter gamePadAdapter) {
        this.gamePadAdapter = gamePadAdapter;
    }


    public boolean isGamePadAvailable() {
        return gamePadAdapter != null;
    }

    public void setListener(GamePadManagerListener listener) {
        this.listener = listener;
    }

    public GamePadManagerListener getListener() {
        return listener;
    }

    public void start(){
        if(!isGamePadAvailable()){
           return;
        }
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePadAdapter.poll();
                boolean[] buttonPressedState = gamePadAdapter.getButtons();
                updateButtonPressedState(buttonPressedState.clone());
                PovDirection newPovDirection = PovDirection.from(gamePadAdapter.getHatDir());
                updatePovDirection(newPovDirection);
            }
        });
        timer.start();
    }

    private void updatePovDirection(PovDirection newPovDirection) {
        PovDirection oldPovDirection = lastPovDirection;
        lastPovDirection = newPovDirection;
        if (oldPovDirection != newPovDirection && listener != null){
            listener.onPovDirectionChanged(oldPovDirection, newPovDirection);
        }
    }

    private synchronized void updateButtonPressedState(boolean[] buttonPressedState) {
       if (lastButtonPressedState == null){
           lastButtonPressedState = buttonPressedState;
           return;
       }
       List<Integer> downButtons = new ArrayList<Integer>(4);
       List<Integer> upButtons = new ArrayList<Integer>(4);
       for(int i=0; i<buttonPressedState.length; i++){
           if (lastButtonPressedState[i]!=buttonPressedState[i]){
               if (lastButtonPressedState[i]){
                //was pressed last time
                 upButtons.add(i);
               } else {
                //was not pressed last time
                 downButtons.add(i);
               }
           }
       }
       lastButtonPressedState = buttonPressedState;
       notifyAboutButtonStateChange(downButtons, upButtons);
    }

    private void notifyAboutButtonStateChange(List<Integer> downButtons, List<Integer> upButtons) {
        if (((!upButtons.isEmpty()) || (!downButtons.isEmpty())) && listener != null){
           listener.onButtonStateChange(upButtons, downButtons);
        }
    }

    public void stop(){
        if (timer != null && timer.isRunning()){
            timer.stop();
        }
        lastButtonPressedState = null;
        lastPovDirection = PovDirection.CENTER;
    }

    public boolean isRunning(){
        return timer != null && timer.isRunning();
    }

    public static interface GamePadManagerListener{
       void onButtonStateChange(List<Integer> releasedBtnsIndx, List<Integer> pressedBtnsIndx);
       void onPovDirectionChanged(PovDirection oldPovDirection, PovDirection newPovDirection);
    }

    public static enum PovDirection{
        NW,NORTH,NE,WEST,CENTER,EAST,SW,SOUTH,SE;
        public static PovDirection from(int source){
            switch (source){
                case GamePadAdapter.EAST: return EAST;
                case GamePadAdapter.NE: return NE;
                case GamePadAdapter.NONE: return CENTER;
                case GamePadAdapter.NORTH: return NORTH;
                case GamePadAdapter.NW: return NW;
                case GamePadAdapter.SE: return SE;
                case GamePadAdapter.SOUTH: return SOUTH;
                case GamePadAdapter.SW: return SW;
                case GamePadAdapter.WEST: return WEST;
            }
            return CENTER;
        }
    }

}
