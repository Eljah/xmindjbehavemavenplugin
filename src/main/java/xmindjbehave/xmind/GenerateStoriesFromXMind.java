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
        extends AbstractXMindMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;


    @Parameter(property = "generateStoriesFromXMind.xmindpath", defaultValue = "tests.xmind")
    private String xmindpath;


    public void main() throws IOException, CoreException {
        String workbookString = xmindpath;
        //String oldWorkbook = "C:`*`path`*`to`*`oldWorkbook.xmind";

        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        //IWorkbook Workbook = builder.createWorkbook(workbookString);
        IStorage ist = new ByteArrayStorage();
        IEncryptionHandler iench = new IEncryptionHandler() {
            @Override
            public String retrievePassword() throws CoreException {
                return "privet";
            }
        };

        wb = builder.loadFromPath(workbookString, ist, iench);

        for (ISheet isheet : wb.getSheets()) {
            System.out.println(isheet.getId());
            ITopic root = isheet.getRootTopic();
            iterateOverTopic(root, "", "src\\test\\resources");
        }
    }

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
                    writer.write(plainContent.getTextContent());
                    writer.close();
                }
            }
        }
    }


    public void execute()
            throws MojoExecutionException {
        /*
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        } */
        try {
            main();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
