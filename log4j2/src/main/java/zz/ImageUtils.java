package zz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author zhangzheng
 * @date 2020/10/6
 */
public class ImageUtils {
    public static void convert(File source, File target) throws IOException {
        if (!source.exists()) {
            return;
        }
        BufferedImage image = ImageIO.read(source);
        ImageIcon imageIcon = new ImageIcon(image);
        BufferedImage bufferedImage = new BufferedImage(
                imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
        g2D.drawImage(imageIcon.getImage(), 0, 0,
                imageIcon.getImageObserver());
        for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
                .getHeight(); j1++) {
            for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
                    .getWidth(); j2++) {
                int rgb = bufferedImage.getRGB(j2, j1);
                int alpha = rgb >> 24;
                if (alpha >= 0) {
                    rgb = Color.WHITE.getRGB();
                }
                bufferedImage.setRGB(j2, j1, rgb);
            }
        }
        g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
        String name = source.getName();
        ImageIO.write(bufferedImage, name.substring(name.lastIndexOf(".") + 1), target);
    }
}
