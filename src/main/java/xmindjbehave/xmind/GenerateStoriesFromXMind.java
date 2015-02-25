package xmindjbehave.xmind;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import xmindjbehave.jbehave.JBehaveTextProcessor;
import xmindjbehave.jbehave.concatenate.Concatenator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which touches a timestamp file.
 *
 * @deprecated Don't use!
 */
@Mojo(name = "generateStoriesFromXMind", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateStoriesFromXMind
        extends AbstractXMindToSpecsMojo {

    public static void iterateOverTopic(ITopic itop, String offset, String folderBase) throws IOException {
        if (!itop.hasMarker("flag-red")) {
            System.out.println(offset);
            boolean folderCreated = (new File(folderBase)).mkdirs();


            for (ITopic child : itop.getAllChildren()) {
                iterateOverTopic(child, offset + " ", folderBase + "\\" + itop.getTitleText());
            }
            if (itop.getNotes() != null) {
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
    }


    public static void main(String[] args) {
        GenerateStoriesFromXMind gen = new GenerateStoriesFromXMind();
        try {
            gen.outputDirectory = new File("");
            gen.xmindpath = "C:\\pegas\\regression.xmind";

            gen.execute();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
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

    @Override
    public void iterateOverTopic(ITopic itop, String offset, String folderBase, String textFromTheParentNode) throws IOException {
        System.out.println(offset);
        //creating the folder to include the spec extracted from the topic note
        boolean folderCreated = (new File(folderBase)).mkdirs();
        //obtaining text from the node if it exists
        String valueforTheCurrentTopicNote = "";
        IPlainNotesContent plainContent = (IPlainNotesContent) itop.getNotes().getContent(INotes.PLAIN);
        if (plainContent != null) {
            valueforTheCurrentTopicNote = plainContent.getTextContent();
            Concatenator c = new Concatenator(textFromTheParentNode, valueforTheCurrentTopicNote);
            valueforTheCurrentTopicNote = c.getResult();
        }
        //if there are children we go deeper
        if (itop.getAllChildren().size() > 0) {
            for (ITopic child : itop.getAllChildren()) {
                iterateOverTopic(child, offset + " ", folderBase + "\\" + itop.getTitleText().replace(",","").replace("\r","").replace("\n","").replace("\"","").replace("\'","").replace(">","").replace("<","").replace("*","").replace(":","").replace(";","").replace("/","").trim(), valueforTheCurrentTopicNote);
            }
        }
        //else we are creating spec file
        else {
            //if only it is marked with the correct flag
            if (!this.topicOrParentHaveMarker(itop, "flag-red")&&!valueforTheCurrentTopicNote.trim().equals("")) {
                System.out.println("\r\n\r\nScenario: "
                        + itop.getTitleText()
                        + "\r\n\r\n"
                        + valueforTheCurrentTopicNote
                        + "\r\n\r\n");
                File newStoryCreated = new File(folderBase + "\\" + itop.getTitleText().replace(",","").replace("\r","").replace("\n","").replace("\"","").replace("\'","").replace(">","").replace("<","").replace("*","").replace(":","").replace(";","").replace("/","").trim() + ".story");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newStoryCreated));
                JBehaveTextProcessor jbehaveTextParser = new JBehaveTextProcessor(valueforTheCurrentTopicNote.trim());
                writer.write(jbehaveTextParser.run());
                writer.close();
            }

        }

    }
}


