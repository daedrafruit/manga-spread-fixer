import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TwoPageSpreadFixer {
    
	private static int pageCount = 0;
	
    static void scanVolumes(File directory) {
        File[] volumes = directory.listFiles();        
        
        if (volumes == null) {
            return;
        }
        
//        for (File volume : volumes) {
//            if (volume.isDirectory()) {
//                renameChapters(volume); // Scan files inside the subfolder                
//            }
//        }
        
        for (File volume : volumes) {
            if (volume.isDirectory()) {
                System.out.println();
                System.out.println(volume.getName() + ":");
                //reset page count at beginning of volume
                pageCount = 0; 
                //scan files inside the subfolder  
                scanChapters(volume);
                System.out.println("Done.");
                System.out.println();
            }
        }
//        for (File volume : volumes) {
//            if (volume.isDirectory()) {
//                System.out.println();
//                System.out.println(volume.getName() + ":");
//                restoreNames(volume); // Scan files inside the subfolder 
//            }
//        }
        System.out.println("Scanning Finished.");
    }
    

    private static void scanChapters(File directory) {
        File[] chapters = directory.listFiles();
        if (chapters == null) {
            return;
        }

        String currentChapter = null;
        String chapterName = null;
        boolean hasSpread = false;
        //for each image in chapter folder
        for (File page : chapters) {
            //make sure not folder (rather than img file)
            if (!page.isDirectory()) {
                try {
                	//get chapter name (for debug msgs)
                    File checkFolder = page.getParentFile();
                    chapterName = checkFolder.getName();
                    //start working on image
                    BufferedImage image = ImageIO.read(page);
                    if (image != null) {
                    	//get image dimensions
                        int width = image.getWidth();
                        int height = image.getHeight();
                        //increment page count
                        pageCount += 1;
                        //if image is landscape
                        if (width > height) {
                        	hasSpread = true;
                        	//increment page count again (landscape pages are two pages)
                            pageCount += 1;
                            //check if landscape page is on even numbered page (counting from zero it should be on odd)
                            if ((pageCount) % 2 == 0) {

                                //check if filler image already exists                           	
                                File existingBlank = new File(checkFolder, "!00.jpg");
                                if (existingBlank.exists()) {
                                    //delete the image from the subfolder (thi
                                    if (existingBlank.delete()) {
                                        System.out.println(chapterName + " Refixed (Image deleted)");
                                        System.out.println("The Spreads are inherently offset, fixing based on final spread in chapter.");
                                        System.out.println("Opening spreads such as cover-art may be off.");
                                        //subtract from pageCount when image is deleted
                                        pageCount -= 1;
                                        continue;
                                    }
                                }

                                // Copy the first image in the subfolder and name it "00.jpg"
                                
                                //get the first page of the chapter
                                File firstPage = chapters[0];
                                BufferedImage firstImage = ImageIO.read(firstPage);
                                
                                //check if the first page is portrait (that way we can use it as the filler image)
                                if (firstImage.getWidth() > firstImage.getHeight()) {
                                	System.out.println("Copied Blank Page");
                                    //if not use the default "00.jpg"
                                    File originalBlank = new File("images/!00.jpg");
                                    BufferedImage blankImage = ImageIO.read(originalBlank);
                                    ImageIO.write(blankImage, "jpg", existingBlank);
                                } 
                                else {
                                	System.out.println("Copied First Page");
                                    //if it is then copy it
                                    ImageIO.write(firstImage, "jpg", existingBlank);
                                }


                                if (!chapterName.equals(currentChapter)) {
                                    System.out.println(chapterName + " Fixed");
                                    currentChapter = chapterName;
                                }
                                pageCount += 1;
                            }
                        }
                    }
                    else {
                    	System.out.println("Error reading the image: " + page.getName());
                    }
                } 
                catch (IOException e) {
                    System.out.println("Error reading the image: " + page.getName());
                }
            }

            if (page.isDirectory()) {
                scanChapters(page); // Recursive call for subdirectory
            }
        }
        if (hasSpread == false) {
        	System.out.println("No spreads found in " + chapterName + ", check manually");
        }
        hasSpread = false;
    }

    
    private static void renameChapters(File directory) {
    	File[] chapters = directory.listFiles();

        if (chapters == null) {
            return;
        }

        for (File chapter : chapters) {
            if (chapter.isDirectory()) {
                String chapterName = chapter.getName();
                String formattedChapterName = formatNumber(chapterName);               
                		
                File newFile = new File(chapter.getParent(), formattedChapterName);
                if (chapter.renameTo(newFile)) {                  
                    chapter = newFile; // Update the file reference to the renamed directory
                } 
                else {                   
                    continue; // Skip further processing if renaming failed
                }
            }
        }
    }
    
    public static String formatNumber(String input) {
        // Remove unwanted characters
        String cleanedInput = input.replaceAll("[^0-9.]", "");

        StringBuilder formattedNumber = new StringBuilder();
        String[] parts = cleanedInput.split("\\.");

        // Format the whole number part
        int wholeNumber = Integer.parseInt(parts[0]);
        formattedNumber.append(String.format("%04d", wholeNumber));

        // Format the decimal part if it exists
        if (parts.length > 1) {
            int decimalNumber = Integer.parseInt(parts[1]);
            formattedNumber.append(".");
            formattedNumber.append(String.format("%04d", decimalNumber));
        }

        return formattedNumber.toString();
    }
//    
//    private static void restoreNames(File directory) {
//    	File[] chapters = directory.listFiles();
//
//        if (chapters == null) {
//            return;
//        }
//
//        for (File chapter : chapters) {
//            if (chapter.isDirectory()) {
//                String chapterName = chapter.getName();
//                String formattedChapterName = "Chapter " + reformatNumber(chapterName);               
//                		
//                File newFile = new File(chapter.getParent(), formattedChapterName);
//                if (chapter.renameTo(newFile)) {
//                	System.out.println("Renamed " + formattedChapterName);
//                    chapter = newFile; // Update the file reference to the renamed directory
//                } 
//                else {
//                	System.out.println("Failed to rename directory: " + chapter.getName());
//                    continue; // Skip further processing if renaming failed
//                }
//            }
//        }
//    }
//    public static String reformatNumber(String input) {
//        // Remove unwanted characters
//        String cleanedInput = input.replaceAll("[^0-9.]", "");
//
//        StringBuilder formattedNumber = new StringBuilder();
//        String[] parts = cleanedInput.split("\\.");
//
//        // Remove leading zeros from the whole number part
//        int wholeNumber = Integer.parseInt(parts[0]);
//        String wholeNumberString = String.valueOf(wholeNumber).replaceAll("^0+(?!$)", "");
//        formattedNumber.append(wholeNumberString);
//
//        // Format the decimal part if it exists
//        if (parts.length > 1) {
//            int decimalNumber = Integer.parseInt(parts[1]);
//            String decimalNumberString = String.valueOf(decimalNumber).replaceAll("0*$", "");
//            formattedNumber.append(".");
//            formattedNumber.append(decimalNumberString);
//        }
//
//        return formattedNumber.toString();
//    }


} 
