package mavenplugintest;

import org.apache.maven.plugin.AbstractMojo;
import org.xmind.core.IWorkbook;
import org.xmind.core.internal.Workbook;
import org.xmind.core.internal.dom.WorkbookImpl;

/**
 * Created by Leon on 30.06.14.
 */
public abstract class AbstractXMindMojo extends AbstractMojo {
    IWorkbook wb;
}
