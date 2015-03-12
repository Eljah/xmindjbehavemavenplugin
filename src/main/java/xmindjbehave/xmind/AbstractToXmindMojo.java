package xmindjbehave.xmind;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by Ilya Evlampiev on 02.03.15.
 */
public abstract class AbstractToXmindMojo extends AbstractXMindMojo{
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirRoot", required = true)
    protected File outputDirectoryRoot;

    @Parameter(defaultValue = "src\\test\\resources", property = "outputDir", required = true)
    protected File outputDirectory;

    @Parameter(defaultValue = "\\target\\jbehave", property = "testResultsDir", required = true)
    protected File outputResultsDir;

    @Parameter(property = "generateXMindFromTestResults.xmindprefix", defaultValue = "tests")
    protected String xmindprefix;

    @Parameter(property = "generateStoriesFromXMind.xmindpath", defaultValue = "tests.xmind")
    protected String xmindpath;

    @Parameter(property = "generateStoriesFromXMind.useSingleOutput", defaultValue = "tests.xmind")
    protected boolean useSingleOutput;


}
