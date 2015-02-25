package xmindjbehave.xmind;

import org.apache.maven.plugins.annotations.Parameter;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 25.02.15.
 */
public abstract class AbstractXMindToSpecsMojo extends AbstractXMindMojo implements XMindToSpecsExtractor{

    String selector;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirRoot", required = true)
    protected File outputDirectoryRoot;

    @Parameter(defaultValue = "src\\test\\resources", property = "outputDir", required = true)
    protected File outputDirectory;

    @Parameter(property = "generateStoriesFromXMind.xmindpath", defaultValue = "tests.xmind")
    protected String xmindpath;

    public void extractAll() throws IOException, CoreException {
        String workbookString = xmindpath;

        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        IStorage ist = new ByteArrayStorage();
        IEncryptionHandler iench = new IEncryptionHandler() {
            @Override
            public String retrievePassword() throws CoreException {
                return "privet";
            }
        };
        wb = builder.loadFromPath(workbookString, ist, iench);
        //wb = builder.loadFromPath(workbookString);

        for (ISheet isheet : wb.getSheets()) {
            System.out.println(isheet.getId());
            ITopic root = isheet.getRootTopic();
            this.iterateOverTopic(root, "", outputDirectory.getAbsolutePath(),"");
            cleanupFolders(outputDirectory.getAbsolutePath());
        }
    }

    public boolean topicOrParentHaveMarker(ITopic itopic, String markers) {
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

}
