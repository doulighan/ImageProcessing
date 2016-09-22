import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Devin Oulighan on 9/19/2016.
 *
 * TODO: Description
 */
public class ImageProcessing {

  public static void main(String[] args) {
    BufferedImage coloredballs = readImage("res\\cballs.png");
    edgeDetect(coloredballs);
  }

  public static BufferedImage readImage(String path) {
    BufferedImage layer = null;
    try {
      layer = ImageIO.read(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    BufferedImage image = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_INT_RGB);
    return layer;
  }

  public static void edgeDetect(BufferedImage in) {
    long startTime = System.currentTimeMillis();
    int[][] kernal = new int[3][3];
    BufferedImage blurred = gaussianBlur(in, 7);
    BufferedImage image = greyScale(blurred);
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int x = 1; x < image.getWidth() - 1; x++) {
      for (int y = 1; y < image.getHeight() - 1; y++) {
        kernal[0][0] = new Color(image.getRGB(x - 1, y - 1)).getRed();
        kernal[0][1] = new Color(image.getRGB(x - 1, y)).getRed();
        kernal[0][2] = new Color(image.getRGB(x - 1, y + 1)).getRed();
        kernal[1][0] = new Color(image.getRGB(x, y - 1)).getRed();
        kernal[1][2] = new Color(image.getRGB(x, y + 1)).getRed();
        kernal[2][0] = new Color(image.getRGB(x + 1, y - 1)).getRed();
        kernal[2][1] = new Color(image.getRGB(x + 1, y)).getRed();
        kernal[2][2] = new Color(image.getRGB(x + 1, y + 1)).getRed();
        int gy = (kernal[0][0] * - 1) + (kernal[0][1] * - 2) + (kernal[0][2] * - 1) + (kernal[2][0]) + (kernal[2][1]
            * 2) + (kernal[2][2] * 1);
        int gx = (kernal[0][0]) + (kernal[0][2] * - 1) + (kernal[1][0] * 2) + (kernal[1][2] * - 2) + (kernal[2][0]) +
            (kernal[2][2] * - 1);
        float magnitude = (float) (Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2))) / 800;
        float orientation = (float) (Math.atan2(gx, gy) / Math.PI);
        int rgb = Color.HSBtoRGB(orientation, 1, magnitude);
        out.setRGB(x, y, rgb);
      }
    }
    File outputfile = new File("edges.png");
    try {
      ImageIO.write(out, "png", outputfile);
      System.out.println("edges.png saved");
    } catch (IOException e) {
      e.printStackTrace();
    }
    long timer = (System.currentTimeMillis() - startTime) / 1000;
    System.out.println(timer + " seconds.");

  }

  public static BufferedImage greyScale(BufferedImage image) {
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int pixelVal = image.getRGB(x, y);
        int rr = (pixelVal >> 16) & 0xff;
        int gg = (pixelVal >> 8) & 0xff;
        int bb = (pixelVal) & 0xff;
        int average = (rr + bb + gg) / 3;
        Color color = new Color(average, average, average);
        out.setRGB(x, y, color.getRGB());
      }
    }
    File outputfile = new File("grayscale.png");
    try {
      ImageIO.write(out, "png", outputfile);
      System.out.println("grayscale.png saved");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out;
  }

  public static BufferedImage standardBlur(BufferedImage image, int kernalSize) {
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    int pixelVal;
    int mKernal = (int) kernalSize / 2;
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int rr = 0;
        int gg = 0;
        int bb = 0;
        for (int i = x - mKernal; i <= x + mKernal; i++) {
          for (int j = y - mKernal; j <= y + mKernal; j++) {
            if (! (i < 0 || i > image.getWidth() - 1 || j < 0 || j > image.getHeight() - 1)) {
              pixelVal = image.getRGB(i, j);
              rr += (pixelVal >> 16) & 0xff;
              gg += (pixelVal >> 8) & 0xff;
              bb += (pixelVal) & 0xff;
            }
          }
        }
        Color color = new Color(rr / (kernalSize * kernalSize), gg / (kernalSize * kernalSize), bb / (kernalSize *
            kernalSize));
        out.setRGB(x, y, color.getRGB());
      }
    }
    File outputfile = new File("blurred.png");
    try {
      ImageIO.write(out, "png", outputfile);
      System.out.println("blurred.png saved");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out;
  }

  public static BufferedImage gaussianBlur(BufferedImage image, int stdDev) {
    System.out.print("Blurring");
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    int r = (int) Math.ceil(stdDev * 2.57); //significant radius
    int percent = -10;
    for (int x = 0; x < image.getWidth(); x++) {
      if (x % (int) (.10 * image.getWidth()) == 0) {
        percent+=10;
        System.out.print("..." + percent + "%");
      }
      for (int y = 0; y < image.getHeight(); y++) {
        double weightSum = 0;
        double rr = 0, gg = 0, bb = 0;
        for (int i = x - r; i <= x + r; i++) {
          for (int j = y - r; j <= y + r; j++) {
            int xx = Math.min(image.getWidth() - 1, Math.max(0, i));  //check bounds
            int yy = Math.min(image.getHeight() - 1, Math.max(0, j)); //''
            int dsq = (i - x) * (i - x) + (j - y) * (j - y);
            double weight = Math.exp(- dsq / 2 * stdDev * stdDev) / (Math.PI * 2 * r * r);
            int pixelVal = image.getRGB(xx, yy);
            rr += ((pixelVal >> 16) & 0xff) * weight;
            gg += ((pixelVal >> 8) & 0xff) * weight;
            bb += ((pixelVal) & 0xff) * weight;
            weightSum += weight;
          }
        }
        Color c = new Color((int) Math.round(rr / weightSum), (int) Math.round(gg / weightSum), (int) Math.round(bb /
            weightSum));
        out.setRGB(x, y, c.getRGB());
      }
    }
    File outputfile = new File("gaussian.png");
    try {
      ImageIO.write(out, "png", outputfile);
      System.out.println("\ngaussian.png saved");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out;
  }
}
