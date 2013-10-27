package org.monroe.team.playwall.common;

import org.jdesktop.animation.timing.Animator;

/**
 * User: MisterJBee
 * Date: 10/20/13 Time: 12:52 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class AnimationUtils {
    public static float calculateOverheadFraction(float fraction, float overhead, float overheadFraction) {
        if (fraction <= overheadFraction){
            fraction = overhead / overheadFraction * fraction;
        } else {
            fraction = overhead - (fraction - overheadFraction)/(1-overheadFraction) * (overhead-1);
        }
        return fraction;
    }

}
