package xmindjbehave.jbehave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

/**
 * Created by Ilya Evlampiev on 14.12.14.
 */
public class JbehaveTextParser {
    String theWholeText;
    String currentTableRow;

    public JbehaveTextParser(String wholeText) {
        this.theWholeText = wholeText;
    }

    public static void main(String[] args) {
        JbehaveTextParser jbehaveTextParser = new JbehaveTextParser("|Е642РА|116 RUS|170.0|666286|666287|666-286|666-287|\n" +
                "text\n|Е642РВ|116 RUS|IntRange:[170,180,20)|IntRange:[666286,666289,2)|666IntRange:[286,288,4]|666-286|666-287|\n");
        System.out.println(jbehaveTextParser.run());
    }

    public String run() {
        String newWholeText = "";

        newWholeText = handleRow(theWholeText);
        return newWholeText;
    }

    static String handleRow(String row) {
        String newWholeText = "";
        BufferedReader bufReader = new BufferedReader(new StringReader(row));
        String line = null;
        try {
            while ((line = bufReader.readLine()) != null) {
                if (line != "\n") {
                    MetaLanguageStatements metal = hasMetaLanguageInRow(line);
                    if (metal != null) {
                        line = parseMetaLanguageParameters(line, metal);
                        line = handleRow(line);
                    }
                    line=line.trim();
                    newWholeText += line + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newWholeText;
    }

    static String parseMetaLanguageParameters(String current_row, MetaLanguageStatements metal) {
        int replaceble_area_start = current_row.indexOf(metal.name(), 0);
        int first_position = replaceble_area_start + metal.name().length() + 1;
        char firstBr = current_row.charAt(first_position);
        int firstCom = current_row.indexOf(',', first_position + 2);
        int second_position_br = current_row.indexOf(']', firstCom + 1);
        int second_position_pr = current_row.indexOf(')', firstCom + 1);
        int second_position = -1;
        char second_br = ']';
        if (second_position_br != -1) {
            if (second_position_pr != -1) {
                if (second_position_br < second_position_pr) {
                    second_br = ']';
                    second_position = second_position_br;
                } else {
                    second_br = ')';
                    second_position = second_position_pr;
                }
            } else {
                second_br = ']';
                second_position = second_position_br;
            }

        } else {
            if (second_position_pr != -1) {

                second_br = ')';
                second_position = second_position_pr;
            }
        }

        int secondCom = -1;
        if (((String) (current_row.subSequence(firstCom + 1, second_position))).

                contains(",")

                )

        {    //todo
            secondCom = current_row.indexOf(",", firstCom + 1);
        }

        String value2 = null;
        int sampleNumber = 2;
        if (secondCom != -1)

        {
            sampleNumber = Integer.parseInt(current_row.substring(secondCom + 1, second_position));
            value2 = current_row.substring(firstCom + 1, secondCom);
        } else

        {
            value2 = current_row.substring(firstCom + 1, second_position);
        }

        String value1 = current_row.substring(first_position + 1, firstCom);

        String replaceble = current_row.substring(replaceble_area_start, second_position + 1);

        String[] samples = new String[sampleNumber];

        if (sampleNumber == 1)

        {
            if (firstBr == '[') {
                samples[0] = value1;
            } else if (second_br == ']') {
                samples[0] = value2;
            } else {
                samples[0] = calculateMiddleValue(metal, value1, value2);
            }
            return current_row.replace(replaceble, samples[0]);
        }

        if (sampleNumber == 2 || sampleNumber == -1)

        {
            if (firstBr == '[') {
                samples[0] = value1;
                if (second_br == ']') {
                    samples[1] = value2;
                } else {
                    samples[1] = calculateMiddleValue(metal, value1, value2);
                }
            } else if (second_br == ']') {
                samples[0] = value2;
                samples[1] = calculateMiddleValue(metal, value1, value2);
            } else {
                samples[0] = calculateMiddleValue(metal, value1, value2);
                samples[1] = calculateMiddleValue(metal, value1, value2);
            }
            String duplicate1 = current_row;
            String duplicate2 = current_row;
            return (duplicate1.replace(replaceble, samples[0]) + "\n" + duplicate2.replace(replaceble, samples[1]));
        }

        if (sampleNumber > 2)

        {
            if (firstBr == '[') {
                samples[0] = value1;
                if (second_br == ']') {
                    samples[1] = value2;
                    for (int i = 2; i < (sampleNumber); i++) {
                        samples[i] = calculateMiddleValue(metal, value1, value2);
                    }
                } else {
                    for (int i = 1; i < (sampleNumber); i++) {
                        samples[i] = calculateMiddleValue(metal, value1, value2);
                    }
                }
            } else if (second_br == ']') {
                samples[0] = value2;
                for (int i = 1; i < (sampleNumber); i++) {
                    samples[i] = calculateMiddleValue(metal, value1, value2);
                }
            } else {
                for (int i = 0; i < (sampleNumber); i++) {
                    samples[i] = calculateMiddleValue(metal, value1, value2);
                }
            }
            String duplicate1 = current_row;
            String toReturn = duplicate1.replace(replaceble, samples[0]);
            for (int i = 1; i < (sampleNumber); i++) {
                String duplicate = current_row;
                toReturn += "\n" + duplicate.replace(replaceble, samples[i]);
            }
            return toReturn;
        }

        return null;
    }

    static MetaLanguageStatements hasMetaLanguageInRow(String row) {
        for (MetaLanguageStatements metal : MetaLanguageStatements.values()) {
            if (row.contains(metal + ":[") || row.contains(metal + ":(")) {
                return metal;
            }
        }
        return null;
    }

    static String calculateMiddleValue(MetaLanguageStatements metal, String value1, String value2) {
        if (metal == MetaLanguageStatements.ByteRange) {
            Random r = new Random();
            byte Low = Byte.parseByte(value1);
            byte High = Byte.parseByte(value2);
            Low++;
            High--;
            int R = (r.nextInt(High - Low)) + Low;
            return R + "";
        }
        if (metal == MetaLanguageStatements.ShortRange) {
            Random r = new Random();
            short Low = Byte.parseByte(value1);
            short High = Byte.parseByte(value2);
            Low++;
            High--;
                      int R = (r.nextInt(High - Low)) + Low;
            return R + "";
        }
        if (metal == MetaLanguageStatements.IntRange) {
            Random r = new Random();
            int Low = Integer.parseInt(value1);
            int High = Integer.parseInt(value2);
            Low++;
            High--;
            int R;
            if ((High - Low)>0)
            {
            R= (r.nextInt(High - Low)) + Low;
            }
            else {R=Low;}
            return R + "";
        }
        if (metal == MetaLanguageStatements.LongRange) {
            Random r = new Random();
            long Low = Long.parseLong(value1);
            long High = Long.parseLong(value2);
            Low++;
            High--;
            long R;
            do {
                R = (r.nextLong());
            }
            while (Low < R && R < High);
            return R + "";
        }
        if (metal == MetaLanguageStatements.FloatRange) {
            Random r = new Random();
            float Low = Float.parseFloat(value1);
            float High = Float.parseFloat(value2);
            Low++;
            High--;
            float R;
            do {
                R = (r.nextFloat());
            }
            while (Low < R && R < High);
            return R + "";
        }
        if (metal == MetaLanguageStatements.DoubleRange) {
            Random r = new Random();
            double Low = Double.parseDouble(value1);
            double High = Double.parseDouble(value2);
            Low++;
            High--;
            double R;
            do {
                R = (r.nextDouble());
            }
            while (Low < R && R < High);
            return R + "";
        }
        if (metal == MetaLanguageStatements.BooleanRange) {
            Random r = new Random();
            boolean R = (r.nextBoolean());
            return R + "";
        }
        if (metal == MetaLanguageStatements.CharRange) {
            Random r = new Random();
            char Low = value1.charAt(0);
            char High = value2.charAt(0);
            Low++;
            High--;
            char R;
            R = (char) (r.nextInt(High - Low) + Low);
            return R + "";
        }

        return null;
    }
}
