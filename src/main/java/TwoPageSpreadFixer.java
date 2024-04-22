import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TwoPageSpreadFixer {
    static int pageCount = 0;
//    public static void main(String[] args) {
//        File folder = new File("test-folder");
//        fixDirectorySpreads(folder);
//    }

    //function to recursively look through the files until it finds the directory with images
   public static void fixDirectorySpreads(File parent) {

       if (new ZipFile(parent).isValidZipFile()) {
           System.out.println("parent zip");
           processZip(parent);
       }

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
               fixDirectorySpreads(child);
           }

           //else the child is a zip file
           else if (new ZipFile(child).isValidZipFile()) {
               System.out.println("child zip");
               processZip(child);
           }
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

       //TODO: try to understand this logic, i dont think its working how i think it works
       //       this will be checking the *second* page of a two page spread, not the first
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

    public static void processZip(File file) {
        System.out.println("process zip");

        ZipFile zipFile = new ZipFile(file.getAbsolutePath());
        List fileHeaderList = null;

        try {
            fileHeaderList = zipFile.getFileHeaders();
        } catch (ZipException e) {
            System.out.println("Error reading the zip: " + file.getName());
            return;
        }

        if (fileHeaderList == null) {
            System.out.println("No files found in the zip: " + file.getName());
            return;
        }

        for (Object o : fileHeaderList) {
            FileHeader fileHeader = (FileHeader) o;
            // FileHeader contains all the properties of the file
            System.out.println("****File Details for: " + fileHeader.getFileName() + "*****");
            System.out.println("Name: " + fileHeader.getFileName());
            System.out.println("Compressed Size: " + fileHeader.getCompressedSize());
            System.out.println("Uncompressed Size: " + fileHeader.getUncompressedSize());
            System.out.println("************************************************************");

            // Various other properties are available in FileHeader. Please have a look at FileHeader
            // class to see all the properties
        }
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