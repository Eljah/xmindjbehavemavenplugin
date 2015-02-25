package xmindjbehave.xmind;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import xmindjbehave.jbehave.JBehaveTextProcessor;
import xmindjbehave.jbehave.concatenate.Concatenator;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 18.12.14.
 */
@Mojo(name = "generateStoriesFromXMindGreenFlag", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateStoriesFromXMindGreenFlag extends AbstractXMindToSpecsMojo {
    /**
     * Location of the file.
     */
    @Override
    public void iterateOverTopic(ITopic itop, String offset, String folderBase, String textFromTheParentNode) throws IOException {
        System.out.println(offset);
        boolean folderCreated = (new File(folderBase)).mkdirs();
        for (ITopic child : itop.getAllChildren()) {
            String notes = "";
            if (!itop.getNotes().toString().equals("null")) {
                IPlainNotesContent plainContent = (IPlainNotesContent) itop.getNotes().getContent(INotes.PLAIN);
                notes = plainContent.getTextContent();
                if (notes == null) {
                    notes = "";
                }
                ;
                Concatenator c = new Concatenator(textFromTheParentNode, notes);
                notes = c.getResult();

            }
            ;
            iterateOverTopic(child, offset + " ", folderBase + "\\" + itop.getTitleText(), notes);

        }
        if (itop.getNotes() != null && this.topicOrParentHaveMarker(itop, "flag-green") && !itop.hasChildren(itop.getType())) {
            INotes nt = itop.getNotes();
            if (!nt.toString().equals("null")) {
                IPlainNotesContent plainContent = (IPlainNotesContent) nt.getContent(INotes.PLAIN);
                System.out.println("\r\n\r\nScenario: "
                        + itop.getTitleText()
                        + "\r\n\r\n"
                        + plainContent.getTextContent()
                        + "\r\n\r\n");
                File newStoryCreated = new File(folderBase + "\\" + itop.getTitleText() + ".story");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newStoryCreated));
                JBehaveTextProcessor jbehaveTextParser = new JBehaveTextProcessor(plainContent.getTextContent());
                writer.write(jbehaveTextParser.run());
                writer.close();
            }
        }
    }

    public void execute()
            throws MojoExecutionException {

        try {
            extractAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

}
