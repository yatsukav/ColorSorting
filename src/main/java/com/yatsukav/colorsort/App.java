package com.yatsukav.colorsort;

import com.yatsukav.colorsort.image.ColorModel;
import com.yatsukav.colorsort.image.ImageData;
import com.yatsukav.colorsort.movie.MovieMaker;
import com.yatsukav.colorsort.sorts.ImageSorter;
import com.yatsukav.colorsort.ui.MainWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

public final class App {
    private static final StatusUpdater statusUpdater = new StatusUpdater();

    private App() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            MainWindow.getInstance().show();
            statusUpdater.setProgressBar(MainWindow.getInstance().getProgressBar());
            statusUpdater.setStatusLabel(MainWindow.getInstance().getStatusBarLabel());
            statusUpdater.startUpdate();
            Runtime.getRuntime().addShutdownHook(new Thread(statusUpdater::stopUpdate));
        } else {
            start(args[0], args[1], ColorModel.valueOf(args[2]), args[3], Integer.parseInt(args[4]));
        }
    }

    public static void start(String inputPath, String outputPath, ColorModel colorModel, String sortMethod, int maxDuration) {
        try {
            long time = System.currentTimeMillis();
            final String TEMP_PATH = "_tmp" + UUID.randomUUID();

            statusUpdater.setMessage("Loading image...");
            ImageData imageData = new ImageData().load(new File(inputPath).toURI()).setColorModel(colorModel);
            ImageSorter imageSorter = ImageSorter.of(sortMethod).setImage(imageData);

            statusUpdater.setMessage("Calculating total time of work...");
            System.out.println("Image size: " + imageData.getWidth() + " x " + imageData.getHeight() + " px");
            System.out.println("Colors array size: " + imageData.getColors().length);
            long tmpTime = System.currentTimeMillis();
            long maxOutputImages = imageSorter.calcMaxOutputImages();
            System.out.println("Only sorting time, ms: " + (System.currentTimeMillis() - tmpTime));
            int frameRate = 30;
            int step = 1;
            if (maxOutputImages > frameRate * maxDuration) {
                step = (int) (maxOutputImages / (frameRate * maxDuration));
            } else {
                frameRate = (int) (maxOutputImages / maxDuration);
            }
            System.out.println("Max output images: " + maxOutputImages);
            System.out.println("Frame rate: " + frameRate);
            System.out.println("Step: " + step);

            statusUpdater.setMessage("Draw frames...");
            statusUpdater.setMaxSteps((int) (maxOutputImages / step));
            tmpTime = System.currentTimeMillis();
            imageSorter.setStatusUpdater(statusUpdater)
                    .setPath(TEMP_PATH)
                    .save(step);
            System.out.println("Sorting and saving to jpeg time, ms: " + (System.currentTimeMillis() - tmpTime));

            System.out.println("Images: ");
            imageSorter.getImages().forEach(System.out::println);

            statusUpdater.setMessage("Combine frames in video...");
            MovieMaker.makeVideo(outputPath, imageSorter.getImages(), imageData.getWidth(), imageData.getHeight(), frameRate);

            statusUpdater.setMessage("Clean up...");
            delete(new File(TEMP_PATH));
            delete(new File("jmf.log"));

            String totalTime = "Converting time: " + (System.currentTimeMillis() - time) + "ms";
            System.out.println(totalTime);
            statusUpdater.setMessage(totalTime);
        } catch (Exception e) {
            statusUpdater.setMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void delete(File f) {
        try {
            if (f.isDirectory()) {
                for (File c : f.listFiles()) delete(c);
            }
            if (!f.delete()) throw new FileNotFoundException("Failed to delete file: " + f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
