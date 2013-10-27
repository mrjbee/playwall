package org.monroe.team.playwall.mvp.model;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: MisterJBee
 * Date: 10/27/13 Time: 11:46 AM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class GameDetails {
    public final String gameName;
    public final String gameDetails;
    public final BufferedImage covertImage;
    public final String fanartImagePath;
    public Image prepearedCover;

    public GameDetails(String gameName, String gameDetails, String fanartImagePath, BufferedImage covert) {
        this.gameName = gameName;
        this.gameDetails = gameDetails;
        this.covertImage = covert;
        this.fanartImagePath = fanartImagePath;
    }
}
