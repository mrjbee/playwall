package org.monroe.team.playwall.common;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 4:00 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public interface Closure<InType, OutType> {
    public OutType call(InType in);
}
