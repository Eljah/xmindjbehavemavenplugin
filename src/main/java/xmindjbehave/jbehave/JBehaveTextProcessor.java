package xmindjbehave.jbehave;

import xmindjbehave.jbehave.meta.MetaCombinatoricsTemplateImpl;
import xmindjbehave.jbehave.meta.MetaCopyPasteTemplateImpl;
import xmindjbehave.jbehave.meta.MetaSubstituteTemplateImpl;
import xmindjbehave.jbehave.meta.MetaTemplate;
import xmindjbehave.xmind.AbstractXMindToSpecsMojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Ilya Evlampiev on 23.02.15.
 */
public class JBehaveTextProcessor {
   public String theWholeText;
    public String hostPort;

    public JBehaveTextProcessor(String wholeText, String hostPort) {
        this.theWholeText = wholeText;
        this.hostPort=hostPort;
    }

    public static void main(String[] args) {
        //JBehaveTextProcessor jbehaveTextProcessor = new JBehaveTextProcessor("|Е642РА|116 RUS|170.0|666286|666287|666-286|666-287|\n" +
        //        "text\n|Е642РВ|116 RUS|IntCountRange:(170,180,200)|IntCountRange:[666288,666289,2)|666IntCountRange:[286,288,4]|666-286|666-287|\n");

        JBehaveTextProcessor jbehaveTextProcessor = new JBehaveTextProcessor("${A:|Е642РА|116 RUS|170.0|666286|666287|666-286|666-287|}\n" +
                "text\n${A}\n${A}\n${B:Example\n" +
                "|Е642РВ|116 RUS|IntIncrementRange:[170,180,3]|IntIncrementRange:(666288,666290,1]|666${:IntIncrementRange:[286,288,2]}|666-${:}|666-287|\n}\n${B}","22");

        jbehaveTextProcessor.setHostPort("22");
        System.out.println(jbehaveTextProcessor.run());
    }

    public void setHostPort(String hostPort)
    {
        this.hostPort=hostPort;
    }

    public String run() {

        //todo to move replacemetn to the separate Meta class


        theWholeText=theWholeText.replace("${MACRO:IP}",hostPort);

        String newWholeText = "";
        MetaTemplate meta=new MetaCombinatoricsTemplateImpl();
        BufferedReader bufReader = new BufferedReader(new StringReader(theWholeText));
        String line="";
        try {
            while ((line = bufReader.readLine()) != null) {
                if (line != "\n") {
                    newWholeText = newWholeText+"\r\n"+meta.parseMeta(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        meta=new MetaCopyPasteTemplateImpl();
        String newWholeText2 = "";
        BufferedReader bufReader2 = new BufferedReader(new StringReader(newWholeText));
        String line2="";
        try {
            while ((line2 = bufReader2.readLine()) != null) {
                if (line2 != "\n") {
                    newWholeText2 = newWholeText2+"\r\n"+meta.parseMeta(line2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        meta=new MetaSubstituteTemplateImpl();
        newWholeText2=meta.parseMeta(newWholeText2);


        return newWholeText2.trim();
    }
}
