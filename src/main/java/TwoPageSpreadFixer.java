package main.java;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TwoPageSpreadFixer {
    static int pageCount = 0;
    public static void main(String[] args) {
        File folder = new File("test-folder");
        scanDirectory(folder);
    }

    //function to recursively look through the files until it finds the directory with images
   public static void scanDirectory(File parent) {
       //put contents of file in an array
       File[] children = parent.listFiles();

       //return if there are no children
       if (children == null) {
           return;
       }

       //for each child
       for (File child : children) {
           //if the child is a directory
           if (child.isDirectory()) {
               //reset page count
               pageCount = 0;
               //recur scan inside
               System.out.println("folder " + child.getName());
               scanDirectory(child);
           }
           //else the child is an image
           else {
               //once we reach the bottom directory we can scan the images
               System.out.println("image " + child.getName());

               //load image from file
               BufferedImage image = loadImage(child);

               //make sure image is not null
               if (image == null) {
                   continue;
               }


               if (imageIsOffset(image)) {
                   //insert a
                   insertFillerPage(parent);
               }
           }
      }
   }
   public static boolean imageIsOffset(BufferedImage image) {
       boolean landscape = false;
       boolean onEvenPage = false;

       //check if image is landscape
       if (image.getWidth() > image.getHeight()) {
           //increment page count by two (landscape means two pages)
           pageCount += 2;
           landscape = true;
       }
       else {
           pageCount += 1;
       }
       System.out.println(pageCount);

       onEvenPage = (pageCount % 2 != 0);

       //if the image is landscape and not on an even page, the image is offset
       return landscape && !onEvenPage;
   }

   public  static void insertFillerPage(File folder) {
       //check if a blank already exists in the file
       System.out.println("inserting image: " + folder.getName());
       File existingBlank = new File(folder.getName(), "!00.jpg");
       if (existingBlank.exists()) {
           //delete the image from the subfolder
           existingBlank.delete();
           System.out.println(folder + " Re-fixed (Image deleted)\n" +
                   "The Spreads are inherently offset, fixing based on final spread in chapter.\n" +
                   "Opening spreads such as cover-art may be off.");
           //subtract from pageCount when image is deleted
           pageCount -= 1;
       }

       System.out.println("Copied Blank Page");
       //if not use the default "00.jpg"
       File originalBlank = new File("images/!00.jpg");
       BufferedImage blankImage = loadImage(originalBlank);
       writeImage(blankImage, folder);

   }

   //method to load image from file
   public static BufferedImage loadImage(File file) {
       BufferedImage image = null;
       try {
           image = ImageIO.read(file);
       } catch (IOException e) {
           System.out.println("Error reading the image: " + file.getName());
       }
       return image;
   }

   //method to write image to file
    public static void writeImage(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
            System.out.println("Error writing the image to: " + file);
        }
    }
}