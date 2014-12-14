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
package org.xmind.core.internal.dom;

import static org.xmind.core.internal.dom.DOMConstants.TAG_STYLE_SHEET;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.internal.StyleSheetBuilder;
import org.xmind.core.internal.zip.ArchiveConstants;
import org.xmind.core.io.IInputSource;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.core.util.DOMUtils;
import org.xmind.core.util.IXMLLoader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class StyleSheetBuilderImpl extends StyleSheetBuilder implements
        ErrorHandler {

    private DocumentBuilder documentCreator = null;

    private DocumentBuilder documentLoader = null;

    private DocumentBuilder getDocumentCreator() {
        if (documentCreator == null) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                documentCreator = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException(e);
            }
        }
        return documentCreator;
    }

    private DocumentBuilder getDocumentLoader() throws CoreException {
        if (documentLoader == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true);
            factory
                    .setAttribute(
                            "http://apache.org/xml/features/continue-after-fatal-error", //$NON-NLS-1$
                            Boolean.TRUE);
            try {
                documentLoader = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new CoreException(Core.ERROR_FAIL_ACCESS_XML_PARSER, e);
            }
            documentLoader.setErrorHandler(this);
        }
        return documentLoader;
    }

    private Document createDocument() {
        return getDocumentCreator().newDocument();
    }

    public IStyleSheet createStyleSheet() {
        Document impl = createDocument();
        DOMUtils.createElement(impl, TAG_STYLE_SHEET);
        StyleSheetImpl sheet = new StyleSheetImpl(impl);
        return sheet;
    }

    public IStyleSheet loadFromStream(InputStream stream) throws IOException,
            CoreException {
        DocumentBuilder loader = getDocumentLoader();
        Document doc = parse(loader, stream);
        return createStyleSheet(doc);
    }

    public IStyleSheet loadFromInputSource(IInputSource source,
            IXMLLoader xmlLoader) throws IOException, CoreException {
        Document doc = xmlLoader.loadXMLFile(source,
                ArchiveConstants.STYLES_XML);
        return createStyleSheet(doc);
    }

    private IStyleSheet createStyleSheet(Document doc) {
        fixbug(doc);
        StyleSheetImpl sheet = new StyleSheetImpl(doc);
        init(sheet);
        return sheet;
    }

    private void init(StyleSheetImpl sheet) {
        for (IStyle style : sheet.getAllStyles()) {
            init(style);
        }
//        for (IStyle style : sheet.getMasterStyles()) {
//            init(style);
//        }
//        for (IStyle style : sheet.getNormalStyles()) {
//            init(style);
//        }
    }

    private void init(IStyle style) {
    }

    private Document parse(DocumentBuilder loader, InputStream stream)
            throws IOException, CoreException {
        try {
            return loader.parse(stream);
        } catch (SAXException e) {
            throw new CoreException(Core.ERROR_FAIL_PARSING_XML);
        } catch (IOException e) {
            throw e;
        }
    }

    public void loadProperties(InputStream stream, IStyleSheet styleSheet)
            throws IOException, CoreException {
        Properties p = new Properties();
        p.load(stream);
        ((StyleSheetImpl) styleSheet).setProperties(p);
    }

    public void error(SAXParseException exception) throws SAXException {
    }

    public void fatalError(SAXParseException exception) throws SAXException {
    }

    public void warning(SAXParseException exception) throws SAXException {
    }

    /**
     * This is to fix a bug generated by version 3.0.0.
     * 
     * @param doc
     */
    private void fixbug(Document doc) {
        Element element = doc.getDocumentElement();
        if (element != null) {
            fixbug(element);
        }
    }

    /**
     * This is to fix a bug generated by version 3.0.0.
     * 
     * @param element
     */
    private void fixbug(Element element) {
        String value = element.getAttribute(DOMConstants.ATTR_SHAPE_CLASS);
        if ("org.xmind.topicShape.rectangle".equals(value)) { //$NON-NLS-1$
            element.setAttribute(DOMConstants.ATTR_SHAPE_CLASS,
                    "org.xmind.topicShape.rect"); //$NON-NLS-1$
        }
        Iterator<Element> it = DOMUtils.childElementIter(element);
        while (it.hasNext()) {
            fixbug(it.next());
        }
    }

}