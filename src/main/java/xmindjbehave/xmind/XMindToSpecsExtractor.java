package xmindjbehave.xmind;

import org.xmind.core.CoreException;
import org.xmind.core.ITopic;

import java.io.IOException;

/**
 * Created by Ilya Evlampiev on 25.02.15.
 */
public interface XMindToSpecsExtractor {

    public void extractAll() throws IOException, CoreException;

    public void iterateOverTopic(ITopic itop, String offset, String folderBase, String textOfParentNode) throws IOException;

    public boolean topicOrParentHaveMarker(ITopic itopic, String markers) ;


    }
