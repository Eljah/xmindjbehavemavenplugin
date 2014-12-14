package org.xmind.core;

import org.xmind.core.internal.Notes;
import org.xmind.core.internal.dom.NotesImpl;
import org.xmind.core.io.ByteArrayStorage;
import org.xmind.core.io.IStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Leon on 2/17/14.
 */
public class TestRun {
    public static void main(String[] args) throws IOException, CoreException {
        String workbookString = "parse.xmind";
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

        IWorkbook workbook = builder.loadFromPath(workbookString, ist, iench);

        for (ISheet isheet : workbook.getSheets()) {
            System.out.println(isheet.getId());
            ITopic root = isheet.getRootTopic();
            iterateOverTopic(root, "","src\\test\\resources");
        }
    }

    public static void iterateOverTopic(ITopic itop, String offset, String folderBase) throws IOException {
        System.out.println(offset + itop.getTitleText());
        boolean folderCreated = (new File(folderBase)).mkdirs();


        for (ITopic child : itop.getAllChildren()) {
            iterateOverTopic(child, offset + " ",folderBase+"\\"+itop.getTitleText());
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
                File newStoryCreated = new File(folderBase+"\\"+itop.getTitleText()+".story");
                BufferedWriter writer = new BufferedWriter(new FileWriter(newStoryCreated));
                writer.write(plainContent.getTextContent());
                writer.close();


            }
        }

    }
}
