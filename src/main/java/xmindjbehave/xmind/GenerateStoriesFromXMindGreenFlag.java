package xmindjbehave.xmind;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;
import xmindjbehave.jbehave.JbehaveTextParser;

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
public class GenerateStoriesFromXMindGreenFlag extends AbstractXMindMojo {
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
            cleanupFolders("src\\test\\resources");
        }
    }

    public static boolean topicOrParentHaveMarker(ITopic itopic, String markers) {
        //List<ITopic> allCheildren = itopic.getAllChildren();
        ITopic parent = itopic.getParent();
        boolean flag = true;
        System.out.println("checking topic " + itopic.getTitleText());
        if (parent == null) {
            System.out.println("parent is null");
            System.out.println("current topic has " + markers + " : " + itopic.hasMarker(markers));
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

    public static void iterateOverTopic(ITopic itop, String offset, String folderBase) throws IOException {
        System.out.println(offset);
        boolean folderCreated = (new File(folderBase)).mkdirs();
        for (ITopic child : itop.getAllChildren()) {
            iterateOverTopic(child, offset + " ", folderBase + "\\" + itop.getTitleText());

        }
        if (itop.getNotes() != null && topicOrParentHaveMarker(itop, "flag-green")) {
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
                JbehaveTextParser jbehaveTextParser = new JbehaveTextParser(plainContent.getTextContent());
                writer.write(jbehaveTextParser.run());
                writer.close();
            }

        }

    }

    public static void cleanupFolders(String startingFolder) throws FileNotFoundException {
        File aStartingDir = new File(startingFolder);
        List<File> emptyFolders = new ArrayList<File>();
        findEmptyFoldersInDir(aStartingDir, emptyFolders);
        List<String> fileNames = new ArrayList<String>();
        for (File f : emptyFolders) {
            String s = f.getAbsolutePath();
            fileNames.add(s);
        }
        for (File f : emptyFolders) {
            boolean isDeleted = f.delete();
            if (isDeleted) {
                System.out.println(f.getPath() + " deleted");
            }
        }
    }

    public static boolean findEmptyFoldersInDir(File folder, List<File> emptyFolders) {
        boolean isEmpty = false;
        File[] filesAndDirs = folder.listFiles();
        List<File> filesDirs = Arrays.asList(filesAndDirs);
        if (filesDirs.size() == 0) {
            isEmpty = true;
        }
        if (filesDirs.size() > 0) {
            boolean allDirsEmpty = true;
            boolean noFiles = true;
            for (File file : filesDirs) {
                if (!file.isFile()) {
                    boolean isEmptyChild = findEmptyFoldersInDir(file, emptyFolders);
                    if (!isEmptyChild) {
                        allDirsEmpty = false;
                    }
                }
                if (file.isFile()) {
                    noFiles = false;
                }
            }
            if (noFiles == true && allDirsEmpty == true) {
                isEmpty = true;
            }
        }
        if (isEmpty) {
            emptyFolders.add(folder);
        }
        return isEmpty;
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
