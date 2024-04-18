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
               System.out.println("Folder: " + child.getName());
               scanDirectory(child);
           }
           //else the child is an image
           else {
               //increment the page count
               pageCount += 1;
               //load image from file
               BufferedImage image = loadImage(child);

               //make sure image is not null
               if (image == null) {
                   continue;
               }

               //if the image is offset
               if (imageIsOffsetLandscape(image)) {
                   System.out.println("Attempting to insert image at: " + parent.getName());
                   //insert a filler image
                   insertFillerPage(parent);
               }
           }
      }
   }
   public static boolean imageIsOffsetLandscape(BufferedImage image) {
       boolean landscape = false;
       boolean onEvenPage = false;

       //check if image is landscape
       if (image.getWidth() > image.getHeight()) {
           //increment page count again (landscape means two pages)
           pageCount += 1;
           landscape = true;
       }

       //check if image falls on even page
       onEvenPage = (pageCount % 2 != 0);

       //if the image is landscape and not on an even page, the image is offset
       return landscape && !onEvenPage;
   }

   public static void insertFillerPage(File folder) {
       //TODO: optimize, dont need to keep scanning images after re-fix
       File filler = new File(folder + "/!00.jpg");
       if (filler.exists()){
           filler.delete();
           System.out.println(folder + " Re-fixed (Image deleted)\n" +
                   "The Spreads are inherently offset, fixing based on final spread in chapter.\n" +
                   "Opening spreads such as cover-art may be off.");
           // Subtract from pageCount when image is deleted
           pageCount -= 1;
           return;
       }
       //if not use the default "00.jpg"
       BufferedImage newFiller = loadImage(new File("images/!00.jpg"));

       // Write the blank image to the folder
       writeImage(newFiller, new File(folder, "!00.jpg"));
       pageCount += 1;
       System.out.println("Copied Filler Image");
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