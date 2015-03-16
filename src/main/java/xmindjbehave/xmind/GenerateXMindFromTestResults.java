package xmindjbehave.xmind;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Element;
import org.xmind.core.*;
import org.xmind.core.internal.MarkerRef;
import org.xmind.core.internal.dom.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import org.xmind.core.marker.IMarkerRef;

import java.io.*;
import java.util.Date;

/**
 * Created by Leon on 30.06.14.
 */
@Mojo(name = "generateXMindFromTestResults", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class GenerateXMindFromTestResults extends AbstractToXmindMojo {

    //needed for testing and debugging
    @Deprecated
    public static void main(String[] args) {
        GenerateXMindFromTestResults gen = new GenerateXMindFromTestResults();
        try {
            gen.outputResultsDir = new File("C:\\pegas\\target\\jbehave");
            gen.xmindpath = "C:\\pegas\\example.xmind";
            gen.xmindprefix = "C:\\pegas\\example";
            gen.outputDirectory = new File("C:\\pegas\\src\\test\\resources\\");
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
                iterateOverTopicMarkAllGreen(root, "", outputResultsDir.getPath());
                //iterateOverTopic(root, "", outputResultsDir.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (useSingleOutput) {
                wb.save(xmindprefix + "-LAST.xmind");

            } else {
                wb.save(xmindprefix + (new Date()).toString().replace(" ", "").replace(":", "") + ".xmind");
            }
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
        if (itop.getNotes() != null) {
            INotes nt = itop.getNotes();


            //if (!nt.toString().equals("null")) {
                IPlainNotesContent plainContent = (IPlainNotesContent) nt.getContent(INotes.PLAIN);

                BufferedReader br = null;
                String sCurrentLine;
                String statsfilepath = outputResultsDir.getPath() + "\\" + folderBase.replace(outputResultsDir.getPath() + "\\", "").replace("\\", ".") + "." + itop.getTitleText() + ".stats";


                System.out.println("Setting marker of " + statsfilepath + "");

                try {

                    br = new BufferedReader(new FileReader(statsfilepath));

                    while ((sCurrentLine = br.readLine()) != null) {
                        System.out.println(sCurrentLine);
                        if (sCurrentLine.contains("stepsFailed=")) {
                            if (!sCurrentLine.contains("stepsFailed=0")) {
                                setMarkerToTopicAndParent(itop, "smiley-angry", "smiley-smile");

                                //itop.removeMarker("smiley-smile");
                                //itop.addMarker("smiley-angry");
                                System.out.println("Setting marker of " + statsfilepath + " to smiley-angry (RED)");
                            } else {
                                itop.removeMarker("smiley-angry");
                                itop.addMarker("smiley-smile");
                                System.out.println("Setting marker of " + statsfilepath + " to smiley-smile (GREEN)");
                            }
                        }
                    }


                } catch (java.io.FileNotFoundException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                //}

            }
        }

    }

    public void iterateOverTopicMarkAllGreen(ITopic itop, String offset, String folderBase) throws IOException {
        System.out.println(offset + itop.getTitleText());
        boolean folderCreated = (new File(folderBase)).mkdirs();
        String parentbase = folderBase;

        for (ITopic child : itop.getAllChildren()) {
            iterateOverTopicMarkAllGreen(child, offset + " ", folderBase + "\\" + itop.getTitleText());
        }
        if (itop.getNotes() != null) {
            INotes nt = itop.getNotes();


            if (!nt.toString().equals("null")) {
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

                        br = new BufferedReader(new FileReader(statsfilepath));

                        while ((sCurrentLine = br.readLine()) != null) {
                            System.out.println(sCurrentLine);
                            if (sCurrentLine.contains("stepsFailed=")) {
                                setMarkerToTopicAndParent(itop, "smiley-smile", "smiley-angry");
                            }
                        }
                    } catch (FileNotFoundException f) {
                        toBeWrittenToITop = "";
                    }

                    try {
                        br2 = new BufferedReader(new FileReader(specfilepath));
                        while ((sCurrentLine = br2.readLine()) != null) {
                            toBeWrittenToITop += sCurrentLine + "\n";
                        }
                    } catch (FileNotFoundException f) {
                        System.out.println("File "+specfilepath+" is not fount");
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


}
