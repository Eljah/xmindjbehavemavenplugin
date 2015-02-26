package xmindjbehave.xmind;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 27.02.15.
 */
@Mojo(name = "cleanStories", defaultPhase = LifecyclePhase.CLEAN)
public class CleanStoriesAll extends AbstractMojo {
    @Parameter(defaultValue = "src\\test\\resources", property = "outputDir", required = true)
    protected String outputDirectory;

    @Deprecated
    public static void main(String[] args) {
        CleanStoriesAll gen = new CleanStoriesAll();
        try {
            gen.outputDirectory = new String("Тест-кейсы");

            gen.execute();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        } catch (MojoFailureException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            cleanupFolders(outputDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void cleanupFolders(String startingFolder) throws FileNotFoundException {
        File aStartingDir = new File(startingFolder);
        List<File> emptyFolders = new ArrayList<File>();
        File[] filesAndDirs = aStartingDir.listFiles();
        if (filesAndDirs != null) {
            emptyFolders = Arrays.asList(filesAndDirs);
            List<String> fileNames = new ArrayList<String>();
            for (File f : emptyFolders) {
                String s = f.getAbsolutePath();
                cleanupFolders(s);
                fileNames.add(s);
                boolean isDeleted = f.delete();
                if (isDeleted) {
                    System.out.println(f.getPath() + " deleted");
                }
            }
        }
    }
}
