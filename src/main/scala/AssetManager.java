package com.github.jarlah.scalagraphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private final Map<String, Image> assets = new HashMap<>();

    public Image getImage(String assetName) {
        if (!assets.containsKey(assetName)) {
            assets.put(assetName, loadImage(assetName));
        }
        return assets.get(assetName);
    }

    public Image getScaledImage(String assetName, Dimension dimension) {
        var image = getImage(assetName);
        var originalSize = new Dimension(image.getWidth(null), image.getHeight(null));
        var newSize = getScaledDimension(originalSize, dimension);
        return image.getScaledInstance(newSize.width, newSize.height, Image.SCALE_SMOOTH);
    }

    private Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {
        double widthRatio = boundary.getWidth() / imageSize.getWidth();
        double heightRatio = boundary.getHeight() / imageSize.getHeight();
        double ratio = Math.max(widthRatio, heightRatio);

        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }

    private Image loadImage(String assetName) {
        try {
            return ImageIO.read(getClass().getClassLoader().getResourceAsStream("assets/" + assetName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}