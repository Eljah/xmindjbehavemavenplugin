package xmindjbehave.jbehave.concatenate;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Ilya Evlampiev on 25.02.15.
 */
public class Concatenator {
    String concatenateparent;
    String concatenatechild;
    ArrayList<String> parentfragments;
    ArrayList<String> childfragments;
    ArrayList<String> allfragments;
    String result = "";


    public Concatenator(String concatenateparent, String concatenatechild) {
        this.concatenateparent = concatenateparent.trim().replaceAll("\r","")+"\n";
        this.concatenatechild = concatenatechild.trim().replaceAll("\r","")+"\n";
        parentfragments = new ArrayList<String>();
        childfragments = new ArrayList<String>();
        allfragments = new ArrayList<String>();
        fragmentateParent(this.concatenateparent);
        fragmentateChildren(this.concatenatechild);
        result = concatenateTexts();
    }

    public void fragmentate(String text, String separator, ArrayList toUpdate) {
        text=text.replaceAll("\r","");
        if (text != null && separator != null) {
            String[] textSeparated = text.split(separator);

            for (String token : textSeparated) {
                toUpdate.add(token);
            }
        }
    }

    //>>>>>>
    public void fragmentateParent(String text) {
        fragmentate(text, ">>>>>>\n", parentfragments);
    }

    //<<<<<<
    public void fragmentateChildren(String text) {
        fragmentate(text, "<<<<<<\n", childfragments);
    }

    public String concatenateTexts() {
        for (String token : parentfragments) {
            allfragments.add(token);
            int index = parentfragments.indexOf(token);
            if (index <= childfragments.size() - 1) {
                allfragments.add(childfragments.get(parentfragments.indexOf(token)));
            }
            ;
        }
        String toReturn = "";
        for (String token : allfragments) {
            toReturn += token.toString();
        }
        System.out.println("CONCATENATED RETURNED: "+toReturn);
        return toReturn+"\n";
    }

    public String getResult() {
        return result;
    }

    public static void main(String args[]) {
        Concatenator n = new Concatenator("1asdsadasdasdasd\r\n" +
                "2asdasdasdasdasd\r\n" +
                "\n" +
                "3asdasdasd\r\n" +
                ">>>>>>\r\n" +
                "4asdasdasda\n" +
                "5asdasdasdas\n" +
                ">>>>>>\r\n" +
                "6ccccc\n", "7bbbbbbb\n" +
                ">>>>>>\r\n" +
                "8bbbbbbbbb\n" +
                "<<<<<<\r\n" +
                "9bbbbbbbb\n");
        Concatenator n2 = new Concatenator(n.getResult(), "FFFFF\n<<<<<<\r\nGGGGGG\n");
        System.out.println(n2.getResult());
    }

}
