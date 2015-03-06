package xmindjbehave.xmind;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.xmind.core.*;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Ilya Evlampiev on 14.12.14.
 */

@Mojo(name = "generateXMindFromSelectedItemsOfXmind")
public class GenerateNewXMindForSelectedItemsOfXmind extends AbstractToXmindMojo {

    @Deprecated
    public static void main(String[] args) {
        GenerateNewXMindForSelectedItemsOfXmind gen = new GenerateNewXMindForSelectedItemsOfXmind();
        try {
            gen.outputResultsDir = new File("C:\\pegas\\target\\jbehave");
            gen.xmindpath = "C:\\pegas\\regression.xmind";
            gen.xmindprefix = "C:\\pegas\\regression";
            gen.outputDirectory = new File("");//("C:\\pegas\\src\\test\\resources");
            gen.useSingleOutput = true;
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

        if (!topicOrParentHaveMarker(itop,"symbol-right")&&itop.getParent()!=null&&!topicsChildrenHaveMarker(itop,"symbol-right"))
        {itop.getParent().remove(itop);}

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

    public boolean topicsChildrenHaveMarker(ITopic itopic, String markers) {
        List<ITopic> children = itopic.getAllChildren();
        boolean flag = true;
        System.out.println("checking topic " + itopic.getTitleText());
        if (children.size() == 0) {
            System.out.println("children are empty");
        } else {
            for (ITopic it : children) {
                if (it.hasMarker(markers)) return true;
                if (topicsChildrenHaveMarker(it, markers)) return true;
            }

        }


        return false;

    }


}
