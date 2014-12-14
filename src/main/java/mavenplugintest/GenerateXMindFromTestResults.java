package mavenplugintest;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xmind.core.*;
import org.xmind.core.internal.dom.MarkerRefImpl;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import org.xmind.core.marker.IMarkerRef;

import java.io.*;
import java.util.Date;

/**
 * Created by Leon on 30.06.14.
 */
@Mojo(name = "generateXMindFromTestResults", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class GenerateXMindFromTestResults extends AbstractXMindMojo {

    @Parameter(property = "generateStoriesFromXMind.xmindpath", defaultValue = "tests.xmind")
    private String xmindpath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
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

        try {
            wb = builder.loadFromPath(workbookString, ist, iench);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        for (ISheet isheet : wb.getSheets()) {
            System.out.println(isheet.getId());
            ITopic root = isheet.getRootTopic();
            try {
                iterateOverTopic(root, "", "\\target\\jbehave");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            wb.save(xmindpath.replace(".xmind", "") + (new Date()).toString().replace(" ", "").replace(":", "") + ".xmind");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }


    public static void iterateOverTopic(ITopic itop, String offset, String folderBase) throws IOException {
        System.out.println(offset + itop.getTitleText());
        boolean folderCreated = (new File(folderBase)).mkdirs();
        String parentbase = folderBase;

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

                /*
                File newStoryCreated = new File(folderBase+"\\"+itop.getTitleText()+".story");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newStoryCreated));
                writer.write(plainContent.getTextContent());
                writer.close();
                 */
                BufferedReader br = null;
                String sCurrentLine;
                String statsfilepath = folderBase.replace("\\", ".").replace(".test.resources", "target\\jbehave\\").replace(".target.jbehave.", "target\\jbehave\\") + "." + itop.getTitleText() + ".stats";

                System.out.println("Setting marker of " + statsfilepath + "");

                try {

                    br = new BufferedReader(new FileReader(statsfilepath));

                    while ((sCurrentLine = br.readLine()) != null) {
                        System.out.println(sCurrentLine);
                        if (sCurrentLine.contains("stepsFailed=")) {
                            if (!sCurrentLine.contains("stepsFailed=0")) {
                                itop.removeMarker("smiley-smile");
                                itop.addMarker("smiley-angry");
                                System.out.println("Setting marker of " + statsfilepath + " to smiley-angry (RED)");

                            } else {
                                itop.removeMarker("smiley-angry");
                                itop.addMarker("smiley-smile");
                                System.out.println("Setting marker of " + statsfilepath + " to smiley-smile (GREEN)");
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }

    }
}
