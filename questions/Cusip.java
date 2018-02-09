package questions;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;

public class Cusip {
    static private char localeMinusSign;
    static private char localeDecimalSeparator;

    static {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        localeMinusSign = currentLocaleSymbols.getMinusSign();
        localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide valid input filename as command line parameter!");
            System.exit(-1);
        }
        String inputFileName = args[0];
        Cusip c = new Cusip();
        try {
            c.printCusipClosingPrice(inputFileName);
        } catch (IOException e) {
            System.out.println("Exception while processing the file:" + inputFileName);
            e.printStackTrace();
        }

    }

    public void printCusipClosingPrice(String inputFilePath) throws IOException {
        DecimalFormat df = new DecimalFormat("#,###.00");
        //File & Scanner Auto closable in  Java 8
        try (FileInputStream inputStream = new FileInputStream(inputFilePath);
             Scanner sc = new Scanner(inputStream, "UTF-8")
        ) {
            String cusip = null;
            BigDecimal price = null;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (isNumeric(line)) {
                    price = new BigDecimal(line);
                    if (cusip == null) {
                        System.err.println("Incorrect format, cusip must be available before the price!:" + price);
                    }
                } else {
                    if (isValidCusip(line)) {
                        //print the previous cusip closing price
                        if (cusip != null && price != null) {
                            System.out.println("Cusip:" + cusip + " Closing price:" + df.format(price));
                        }
                        if (cusip != null && price == null) {
                            System.err.println("Cusip:" + cusip + " doesn't have closing price");
                        }
                        cusip = line;
                        price=null;
                    } else {
                        System.err.println("Invalid cusip found:" + line);
                        System.err.println("Skipping prices to until next cusip!");
                        do {
                            if (sc.hasNextLine())
                                line = sc.nextLine();
                        } while (isNumeric(line));
                        cusip = line;
                        price=null;
                    }
                }
            }
            //Last matched cusip and closing price
            if (cusip != null && price != null) {
                System.out.println("Cusip:" + cusip + " Closing price:" + df.format(price));
            }
            if (cusip != null && price == null) {
                System.err.println("Cusip:" + cusip + " doesn't have closing price");
            }

            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        }
    }

    private static boolean isValidCusip(String s) {
        if (s == null)
            return false;
        String trimmedStr = s.trim();
        if (trimmedStr.length() != 8)
            return false;
        if (isAlphanumeric(trimmedStr))
            return true;
        else
            return false;
    }

    private static boolean isAlphanumeric(String str) {
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (!Character.isLetterOrDigit(c))
                return false;
        }
        return true;
    }

    //For additional number format check use Apache Commons Lang utility function: NumberUtils.isCreatable, NumberUtils.isNumber or StringUtils.isNumeric.
    private static boolean isNumeric(String s) {
        if (s == null)
            return false;

        if (!Character.isDigit(s.charAt(0)) && s.charAt(0) != localeMinusSign) return false;
        boolean isDecimalSeparatorFound = false;

        for (char c : s.substring(1).toCharArray()) {
            if (!Character.isDigit(c)) {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound) {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }
}
