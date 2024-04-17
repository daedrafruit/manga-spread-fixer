package main.java;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TwoPageSpreadFixer {
    public static void main(String[] args) {
        File folder = new File("test-folder");
        scanDirectory(folder);
    }
    // function to recursively look through the files until it finds the directory with images
   public static void scanDirectory(File parent) {
       File[] children = parent.listFiles();

      // return if there are no children
       if (children == null) {
           return;
       }

       // for each child
       for (File child : children) {
           // if the child is a directory
           if (child.isDirectory()) {
               // recur scan inside
               System.out.println("folder " + child.getName());
              scanDirectory(child);
           }
           else {
           // TODO: process images here
           // once we reach the bottom directory we can scan the images
               System.out.println("image " + child.getName());
           }
      }
   }

//   // TODO: rename variables "deepestFolder" and "folder" are confusing
     // TODO: complete remake class, there doesnt need to be a loop
//   public static void scanImages(File deepestFolder) {
//       File[] folder = deepestFolder.listFiles();
//       // keep count of current page number
//       int pageCount = 0;
//
//       // TODO: make sure possible that folder can be null
//       // return if no images
//       if (folder == null) {
//           return;
//       }
//
//       // for each file in folder
//       for (File file : folder) {
//
//           // load image from file
//           BufferedImage image = loadImage(file);
//
//           // check if image is null
//           if (image == null) {
//               // done processing this file
//               continue;
//           }
//
//           // check if image is landscape
//           if (image.getWidth() > image.getHeight()) {
//               // increment page count by two (landscape means two pages)
//               pageCount += 2;
//
//           }
//           else {
//               pageCount += 1;
//           }
//           System.out.println(pageCount);
//
//       }
//   }
//
//   // method to load image from file
//   public static BufferedImage loadImage(File file) {
//       BufferedImage image = null;
//       try {
//           image = ImageIO.read(file);
//       } catch (IOException e) {
//           System.out.println("Error reading the image: " + file.getName());
//       }
//       return image;
//   }

}