package xmindjbehave.xmind;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import org.xmind.core.marker.IMarkerRef;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 14.12.14.
 */
@Mojo(name = "updateXMindFromJBehaveStories")
public class UpdateXMindFromJBehaveStories extends AbstractToXmindMojo {

    @Deprecated
    public static void main(String[] args) {
        UpdateXMindFromJBehaveStories gen = new  UpdateXMindFromJBehaveStories();
        try {
            gen.outputResultsDir = new File("C:\\pegas\\target\\jbehave");
            gen.xmindpath = "C:\\pegas\\example.xmind";
            //gen.xmindpath = "C:\\pegas\\regression.xmind";
            gen.xmindprefix = "C:\\example\\regression";
            gen.outputDirectory = new File("C:\\pegas\\src\\test\\resources");
            gen.useSingleOutput=true;
            gen.execute();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        } catch (MojoFailureException e) {
            e.printStackTrace();
        }
    }


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
                iterateOverTopic(root, "", outputResultsDir.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
                wb.save(xmindpath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public void iterateOverTopic(ITopic itop, String offset, String folderBase) throws IOException {
        System.out.println(offset + itop.getTitleText());
        boolean folderCreated = (new File(folderBase)).mkdirs();
        String parentbase = folderBase;

        for (ITopic child : itop.getAllChildren()) {
            iterateOverTopic(child, offset + " ", folderBase + "\\" + itop.getTitleText());
        }
        if (topicOrParentHaveMarker(itop,"symbol-exclam")) {
            INotes nt = itop.getNotes();


            if (true) {
                IPlainNotesContent plainContent = (IPlainNotesContent) nt.getContent(INotes.PLAIN);

                BufferedReader br = null;
                BufferedReader br2 = null;

                String sCurrentLine;
                String statsfilepath = outputResultsDir.getPath() + "\\" + folderBase.replace(outputResultsDir.getPath() + "\\", "").replace("\\", ".") + "." + itop.getTitleText() + ".stats";
                String specfilepath = outputDirectory.getPath() + "\\" + folderBase.replace(outputResultsDir.getPath() + "\\", "") + "\\" + itop.getTitleText() + ".story";


                System.out.println("Setting marker of " + statsfilepath + "");

                try {
                    String toBeWrittenToITop = "";

                    try {
                        br2 = new BufferedReader(new FileReader(specfilepath));
                        while ((sCurrentLine = br2.readLine()) != null) {
                            toBeWrittenToITop += sCurrentLine + "\n";
                        }
                    } catch (FileNotFoundException f) {
                        toBeWrittenToITop = "";
                    }
                    ITopic newitop = wb.createTopic();
                    if (!toBeWrittenToITop.equals("")) {
                        IPlainNotesContent plainContent2 = (IPlainNotesContent) wb.createNotesContent(INotes.PLAIN);
                        plainContent2.setTextContent(toBeWrittenToITop);
                        INotes notes = newitop.getNotes();
                        notes.setContent(INotes.PLAIN, plainContent2);
                    }
                    for (IMarkerRef mr : itop.getMarkerRefs()) {
                        newitop.addMarker(mr.getMarkerId());
                    }
                    for (ITopic tp : itop.getAllChildren()) {
                        newitop.add(tp);
                    }

                    newitop.setTitleText(itop.getTitleText());
                    newitop.setFolded(itop.isFolded());

                    itop.getParent().add(newitop);
                    itop.getParent().remove(itop);

                    //NotesImpl ni=(NotesImpl)itop.getNotes();
                    //ni.setContent(INotes.PLAIN, plainContent);
                    //((TopicImpl)itop).setNotes(ni);
                    //plainContent = (IPlainNotesContent) nt.getContent(INotes.PLAIN);
                    //System.out.println(plainContent2.getTextContent());

                    //System.out.println();
                    //itop.

                } catch (java.io.FileNotFoundException e) {
                    itop.setFolded(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } else {
                itop.setFolded(true);
            }
        }

    }


    public void setMarkerToTopicAndParent(ITopic itopic, String markersToAdd, String markersToRemove) {
        ITopic parent = itopic.getParent();
        itopic.removeMarker(markersToRemove);
        itopic.addMarker(markersToAdd);
        itopic.setFolded(false);

        boolean flag = true;
        System.out.println("checking topic " + itopic.getTitleText());
        if (parent == null) {
        } else {
            setMarkerToTopicAndParent(parent, markersToAdd, markersToRemove);

        }
    }

    public boolean topicOrParentHaveMarker(ITopic itopic, String markers) {
        ITopic parent = itopic.getParent();
        boolean flag = true;
        System.out.println("checking topic " + itopic.getTitleText());
        if (parent == null) {
            System.out.println("parent is null");
            System.out.println("current topic has " + markers + " : " + itopic.hasMarker(markers));
            for (ITopic it: itopic.getAllChildren())
            {
            System.out.println(it.getMarkerRefs());
            }
            if (itopic.hasMarker(markers)) {
                return itopic.hasMarker(markers);
            }
        } else {
            if (itopic.hasMarker(markers)) {
                return true;
            } else {
                return topicOrParentHaveMarker(parent, markers);
            }
        }
        return itopic.hasMarker(markers);

    }

}
