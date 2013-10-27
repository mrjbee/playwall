package org.monroe.team.playwall.mvp.ui;

import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * User: MisterJBee
 * Date: 10/19/13 Time: 3:04 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
final public class ImageLoader {
   private ImageLoader() {}

   public static Image loadImage(String name){
       return loadImage(name,"png");
   }

    private static Image loadImage(String name, String png) {
        return loadIcon(name, png).getImage();
    }

    public static ImageIcon loadIcon(String name, String ext){
      String path = "/images/"+name+"."+ext;
      try{
        return new ImageIcon(ImageLoader.class.getResource(path));
      } catch (RuntimeException ex){
         throw new RuntimeException("Exception during loading image = "+path,ex);
      }
   }

    public static BufferedImage loadBufImage(String name) {
        ToolkitImage image = (ToolkitImage) loadImage(name);
        BufferedImage answer = image.getBufferedImage();
        if (answer == null){
           //do work around with draw in buffred image
            throw new IllegalStateException("Nt implemented");
        }
        return answer;
    }

    public static Icon loadImageAsIcon(String name) {
        return loadIcon(name,"png");
    }

    public static BufferedImage loadFromPath(String gameCovertPath) {
        try {
            Image image = ImageIO.read(new File(gameCovertPath));
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
            bufferedImage.getGraphics().drawImage(image,0,0,null);
            return bufferedImage;
        } catch (IOException e) {
            return null;
        }
    }
}
