package questions;
import java.io.*;
import java.util.Scanner;
public class FileMerger {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Please provide two valid sorted input filenames and one output file name as command line parameter!");
            System.exit(-1);
        }
        String inputFileName1 = args[0];
        String inputFileName2 = args[1];
        String outputFileName = args[2];
        FileMerger merger = new FileMerger();
        try {
            merger.mergeSortedFiles(inputFileName1, inputFileName2, outputFileName);
            System.out.println("Merging of sorted input files completed. Output file available at "+outputFileName);
        } catch (IOException e) {
            System.out.println("Error while merging sorted files!" + e.toString());
            e.printStackTrace();
        }
    }

    public void mergeSortedFiles(String file1, String file2, String outFileName) throws IOException {

        //Auto closable-Java 8
        try (FileInputStream inputStream1 = new FileInputStream(file1);
             Scanner sc1 = new Scanner(inputStream1, "UTF-8");

             FileInputStream inputStream2 = new FileInputStream(file2);
             Scanner sc2 = new Scanner(inputStream2, "UTF-8");

             Writer output = new BufferedWriter(new FileWriter(outFileName));
             PrintWriter out = new PrintWriter(output)
        ) {

            if (sc1.hasNextLine() && sc2.hasNextLine()) {
                String line1 = sc1.nextLine();
                String line2 = sc2.nextLine();
                do {
                    if (line1 != null && line2 != null) {
                        if (line1.compareTo(line2) < 0) {
                            line1 = writeAndMoveOnToNextLine(sc1, out, line1);
                        } else if (line1.compareTo(line2) > 0) {
                            line2 = writeAndMoveOnToNextLine(sc2, out, line2);
                        } else {
                            line1 = writeAndMoveOnToNextLine(sc1, out, line1);
                            line2 = writeAndMoveOnToNextLine(sc2, out, line2);
                        }
                    }
                } while (line1 != null && line2 != null);
                if (line1 != null)
                    out.println(line1);
                if (line2 != null)
                    out.println(line2);
            }
            while (sc1.hasNextLine()) {
                String line = sc1.nextLine();
                out.println(line);
            }
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();
                out.println(line);
            }
            // note that Scanner suppresses exceptions
            if (sc1.ioException() != null) {
                throw sc1.ioException();
            }
            if (sc2.ioException() != null) {
                throw sc2.ioException();
            }
        }
    }

    private String writeAndMoveOnToNextLine(Scanner sc, PrintWriter out, String line) {
        out.println(line);
        if (sc.hasNextLine()) {
            line = sc.nextLine();
        } else
            line = null;
        return line;
    }
}