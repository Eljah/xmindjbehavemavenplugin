/* ******************************************************************************
 * Copyright (c) 2006-2012 XMind Ltd. and others.
 * 
 * This file is a part of XMind 3. XMind releases 3 and
 * above are dual-licensed under the Eclipse Public License (EPL),
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 * and the GNU Lesser General Public License (LGPL), 
 * which is available at http://www.gnu.org/licenses/lgpl.html
 * See http://www.xmind.net/license.html for details.
 * 
 * Contributors:
 *     XMind Ltd. - initial API and implementation
 *******************************************************************************/
package org.xmind.core.internal;

import java.util.Comparator;

import org.xmind.core.IIdFactory;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbookBuilder;
import org.xmind.core.IWorkspace;
import org.xmind.core.internal.dom.MarkerSheetBuilderImpl;
import org.xmind.core.internal.dom.StyleSheetBuilderImpl;
import org.xmind.core.internal.dom.WorkbookBuilderImpl;
import org.xmind.core.marker.IMarkerSheetBuilder;
import org.xmind.core.style.IStyleSheetBuilder;
import org.xmind.core.util.ILogger;

public class InternalCore {

    public static final boolean DEBUG_WORKBOOK_SAVE = Boolean
            .getBoolean("org.xmind.debug.core.workbookSave"); //$NON-NLS-1$

    /**
     * @author MANGOSOFT
     * 
     */
    private final class DefaultLogger implements ILogger {
        public void log(Throwable e) {
            e.printStackTrace();
        }

        public void log(Throwable e, String message) {
            System.err.println(message);
            e.printStackTrace();
        }

        public void log(String message) {
            System.err.println(message);
        }
    }

    private static InternalCore instance = null;

    private IWorkbookBuilder workbookBuilder = null;

    private IWorkspace workspace = null;

    private IMarkerSheetBuilder markerSheetBuilder = null;

    private Comparator<ITopic> topicComparator;

    private IIdFactory idFactory;

    private IStyleSheetBuilder styleSheetBuilder;

    private ILogger logger;

    private InternalCore() {
    }

    public String getCurrentVersion() {
        return "2.0"; //$NON-NLS-1$
    }

    public IWorkbookBuilder getWorkbookBuilder() {
        if (workbookBuilder == null)
            workbookBuilder = new WorkbookBuilderImpl();
        return workbookBuilder;
    }

    public IWorkspace getWorkspace() {
        if (workspace == null) {
            workspace = new Workspace();
        }
        return workspace;
    }

    public IMarkerSheetBuilder getMarkerSheetBuilder() {
        if (markerSheetBuilder == null) {
            markerSheetBuilder = new MarkerSheetBuilderImpl();
        }
        return markerSheetBuilder;
    }

    public Comparator<ITopic> getTopicComparator() {
        if (topicComparator == null) {
            topicComparator = new TopicCompartor();
        }
        return topicComparator;
    }

    public IIdFactory getIdFactory() {
        if (idFactory == null) {
            idFactory = new IDFactory();
        }
        return idFactory;
    }

    public IStyleSheetBuilder getStyleSheetBuilder() {
        if (styleSheetBuilder == null) {
            styleSheetBuilder = new StyleSheetBuilderImpl();
        }
        return styleSheetBuilder;
    }

    public static InternalCore getInstance() {
        if (instance == null)
            instance = new InternalCore();
        return instance;
    }

    /**
     * @return
     */
    public ILogger getLogger() {
        if (logger == null) {
            logger = new DefaultLogger();
        }
        return logger;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

}