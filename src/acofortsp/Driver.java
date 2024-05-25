package acofortsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;





public class Driver {
    static final int NUMBER_OF_ANTS = 500;
    static final double PROCESSING_CYCLE_PROBABILITY = 0.8;
    static ArrayList<City> initialRoute = new ArrayList<City>(Arrays.asList(
            //Wisata1 = Batu Nenek
            new City("Batu Nenek", 2.0169258127531218, 109.6019487294292),
            //Wisata2 = Telok Atong
            new City("Telok Atong", 2.0443856526121738, 109.61733054870913),
            //Wisata3 = Rumah Terbalik
            new City("Rumah Terbalik", 1.9898566277234258, 109.59078350582624),
            //Wisata4 = Rumah Melayu Sambas
            new City("Rumah Melayu Sambas", 1.3202705522869782, 109.27547908873095),
            //Wisata5 = Pantai Lestari
            new City("Pantai Lestari", 1.6335926050315535, 109.22226011621417),
            //Wisata6 = Pantai Cemara
            new City("Pantai Cemara", 1.6516981190469233, 109.24404132375452),
            //Wisata7 = Pantai Kampak
            new City("Pantai Kampak", 1.907931720058708, 109.33836115301669),
            //Wisata8 = Konservasi Penyu
            new City("Konservasi Penyu", 1.9459899822552298, 109.3750165440529),
            //Wisata9 = Taman Lunggi
            new City("Taman Lunggi", 1.3645112190073638, 109.3231379984187),
            //Wisata10 = Riam Sunge Banokng
            new City("Riam Sunge Banokng", 1.6608567170453907, 109.60471026580414),
            //Wisata11 = Riam Pancarek
            new City("Riam Pancarek", 1.673110565167944, 109.61524531226637),
            //Wisata12 = Riam Berasap
            new City("Riam Berasap", 1.6306527560467103, 109.64515328387422),
            //Wisata13 = PLBN Aruk
            new City("PLBN Aruk", 1.6109571322721137, 109.67730760317335),
            //Wisata14 = Gua Maria Santok
            new City("Gua Maria Santok", 1.6159614901402288, 109.49266165479014),
            //Wisata15 = Bukit Piantus
            new City("Bukit Piantus", 1.4597925465844697, 109.31532673519054),
            //Wisata16 = Batu Lapak & Canggar
            new City("Batu Lapak & Canggar", 1.2714404393840537, 108.98691672867382),
            //Wisata17 = Pantai Bahari
            new City("Pantai Bahari", 1.2941885534426116, 109.00815211225873),
            //Wisata18 = Pantai Putri Serayi
            new City("Pantai Putri Serayi", 1.2779069532087386, 108.99286419135412),
            //Wisata19 = Keraton Alwatzikhoebillah
            new City("Keraton Alwatzikhoebillah", 1.3619102126316192, 109.31584507209138),
            //Wisata20 = Danau Sebedang
            new City("Danau Sebedang", 1.2579344070917218, 109.19563704191216),
            //Wisata21 = Pantai Tanjung Batu
            new City("Pantai Tanjung Batu", 1.1817947159000077, 108.96956317108949),
            //Wisata22 = Pantai Sinam
            new City("Pantai Sinam", 1.1755632515154302, 108.97104978542092),
            //Wisata23 = Danau Biru
            new City("Danau Biru", 1.0510290254511634, 109.11869198063148),
            //Wisata24 = Pantai Polaria
            new City("Pantai Polaria", 1.0351891833531466, 108.96969661097613)
    ));
    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorCompletionService<Ant> executorCompletionService = new ExecutorCompletionService<Ant>(executorService);
    private Route shortestRoute = null;
    private int activeAnts = 0;
    public static void main(String[] args) throws IOException {
        System.out.println("> "+NUMBER_OF_ANTS + "Artificial Ant ...");
        Driver driver = new Driver();
        driver.printHeading();
        AntColonyOptimization aco = new AntColonyOptimization();
        IntStream.range(1, NUMBER_OF_ANTS).forEach(x -> {
            executorCompletionService.submit(new Ant(aco, x));
            driver.activeAnts++;
            if (Math.random() > PROCESSING_CYCLE_PROBABILITY) driver.processAnts();
        });
        driver.processAnts();
        executorService.shutdownNow();
        System.out.println("\nOptimal Route : "+Arrays.toString(driver.shortestRoute.getCities().toArray()));
        System.out.println("Optimal Route:");
        for (City city : driver.shortestRoute.getCities()) {
            System.out.println(city.getName() + " (lat: " + Math.toDegrees(city.getLatitude()) + ", lng: " + Math.toDegrees(city.getLongitude()) + ")");
        }

        System.out.println("w/ Distance : " + driver.shortestRoute.getDistance());

        System.out.println("w/ Distance : " + driver.shortestRoute.getDistance());
    }
    private void processAnts() {
        while (activeAnts > 0) {
            try {
                Ant ant = executorCompletionService.take().get();
                Route currentRoute = ant.getRoute();
                if (shortestRoute == null || currentRoute.getDistance() < shortestRoute.getDistance()) {
                    shortestRoute = currentRoute;
                    StringBuffer distance = new StringBuffer("        "+String.format("%.2f", currentRoute.getDistance()));
                    IntStream.range(0, 21 - distance.length()).forEach(k-> distance.append(" "));
                    System.out.println(Arrays.toString(shortestRoute.getCities().toArray()) + " |" + distance + "| "+ ant.getAntNumb());
                }
            } catch (Exception e) {e.printStackTrace();}
            activeAnts--;
        }
    }
    private void printHeading() {
        String headingColumn1 = "Route";
        String remainingHeadingColumns = "Distance (in miles) | ant #";
        int cityNamesLength = 0;
        for (int x = 0; x < initialRoute.size(); x++) cityNamesLength += initialRoute.get(x).getName().length();
        int arrayLength = cityNamesLength + initialRoute.size()*2;
        int partialLength = (arrayLength - headingColumn1.length())/2;
        for (int x = 0; x < partialLength; x++)System.out.print(" ");
        System.out.print(headingColumn1);
        for (int x = 0; x < partialLength; x++)System.out.print(" ");
        if ((arrayLength % 2) == 0)System.out.print(" ");
        System.out.println(" | "+ remainingHeadingColumns);
        cityNamesLength += remainingHeadingColumns.length() + 3;
        for (int x = 0; x < cityNamesLength + initialRoute.size()*2; x++)System.out.print("-");
        System.out.println("");
    }
}