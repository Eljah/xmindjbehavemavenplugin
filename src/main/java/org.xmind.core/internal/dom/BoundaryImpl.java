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

import static org.xmind.core.internal.dom.DOMConstants.ATTR_ID;
import static org.xmind.core.internal.dom.DOMConstants.ATTR_RANGE;
import static org.xmind.core.internal.dom.DOMConstants.ATTR_STYLE_ID;
import static org.xmind.core.internal.dom.DOMConstants.TAG_BOUNDARIES;
import static org.xmind.core.internal.dom.DOMConstants.TAG_TITLE;
import static org.xmind.core.internal.dom.DOMConstants.TAG_TOPIC;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmind.core.Core;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.event.ICoreEventListener;
import org.xmind.core.event.ICoreEventRegistration;
import org.xmind.core.event.ICoreEventSource;
import org.xmind.core.event.ICoreEventSupport;
import org.xmind.core.internal.Boundary;
import org.xmind.core.internal.event.NullCoreEventSupport;
import org.xmind.core.util.DOMUtils;

public class BoundaryImpl extends Boundary implements ICoreEventSource {

    private WorkbookImpl ownedWorkbook;

    private Element implementation;

    private ICoreEventSupport coreEventSupport;

    public BoundaryImpl(Element implementation, WorkbookImpl ownedWorkbook) {
        super();
        this.implementation = DOMUtils.addIdAttribute(implementation);
        this.ownedWorkbook = ownedWorkbook;
    }

    public Element getImplementation() {
        return implementation;
    }

    public Object getAdapter(Class adapter) {
        if (adapter == Node.class || adapter == Element.class)
            return implementation;
        return super.getAdapter(adapter);
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof BoundaryImpl))
            return false;
        BoundaryImpl that = (BoundaryImpl) obj;
        return this.implementation == that.implementation;
    }

    public int hashCode() {
        return implementation.hashCode();
    }

    public String toString() {
        return "BND#" + getId() + "(" + getTitleText() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public String getId() {
        return implementation.getAttribute(ATTR_ID);
    }

    protected String getLocalTitleText() {
        return DOMUtils.getTextContentByTag(implementation, TAG_TITLE);
    }

    public void setTitleText(String titleText) {
        String oldText = getLocalTitleText();
        DOMUtils.setText(implementation, TAG_TITLE, titleText);
        String newText = getLocalTitleText();
        fireValueChange(Core.TitleText, oldText, newText);
        updateModifiedTime();
    }

    public String getStyleId() {
        return DOMUtils.getAttribute(implementation, ATTR_STYLE_ID);
    }

    public void setStyleId(String styleId) {
        String oldValue = getStyleId();
        WorkbookImpl workbook = getRealizedWorkbook();
        WorkbookUtilsImpl.decreaseStyleRef(workbook, this);
        DOMUtils.setAttribute(implementation, ATTR_STYLE_ID, styleId);
        WorkbookUtilsImpl.increaseStyleRef(workbook, this);
        String newValue = getStyleId();
        fireValueChange(Core.Style, oldValue, newValue);
        updateModifiedTime();
    }

    public ISheet getOwnedSheet() {
        ITopic parent = getParent();
        return parent == null ? null : parent.getOwnedSheet();
    }

    public IWorkbook getOwnedWorkbook() {
        return ownedWorkbook;
    }

    public boolean isOrphan() {
        return DOMUtils.isOrphanNode(implementation);
    }

    public ITopic getParent() {
        Element t = getParentTopicElement();
        if (t != null)
            return (ITopic) ownedWorkbook.getAdaptableRegistry()
                    .getAdaptable(t);
        return null;
    }

    private Element getParentTopicElement() {
        Node p = implementation.getParentNode();
        if (DOMUtils.isElementByTag(p, TAG_BOUNDARIES)) {
            p = p.getParentNode();
            if (DOMUtils.isElementByTag(p, TAG_TOPIC))
                return (Element) p;
        }
        return null;
    }

    protected ITopic getTopic(int index) {
        if (index < 0)
            return null;

        Element p = getParentTopicElement();
        if (p == null)
            return null;

        Element ts = TopicImpl.findSubtopicsElement(p, ITopic.ATTACHED);
        if (ts == null)
            return null;

        Iterator<Element> it = DOMUtils.childElementIterByTag(ts, TAG_TOPIC);
        int i = 0;
        while (it.hasNext()) {
            Element t = it.next();
            if (i == index) {
                return (ITopic) ownedWorkbook.getAdaptableRegistry()
                        .getAdaptable(t);
            }
            i++;
        }
        return null;
    }

    public int getEndIndex() {
        return InternalDOMUtils.getEndIndex(DOMUtils.getAttribute(
                implementation, ATTR_RANGE));
//        return safeParseInt(getAttribute(implementation, ATTR_END_INDEX), -1);
    }

    public int getStartIndex() {
        return InternalDOMUtils.getStartIndex(DOMUtils.getAttribute(
                implementation, ATTR_RANGE));
//        return safeParseInt(getAttribute(implementation, ATTR_START_INDEX), -1);
    }

//    private Integer getIndex(String attrName) {
//        if (implementation.hasAttribute(attrName)) {
//            try {
//                return Integer.valueOf(implementation.getAttribute(attrName));
//            } catch (NumberFormatException e) {
//                return null;
//            }
//        }
//        return null;
//    }

    private Integer toIndexValue(int index) {
        return index < 0 ? null : Integer.valueOf(index);
    }

    public void setEndIndex(int index) {
        String oldValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        Integer oldIndexValue = toIndexValue(getEndIndex());
        DOMUtils.setAttribute(implementation, ATTR_RANGE,
                InternalDOMUtils.toRangeValue(getStartIndex(), index));
        Integer newIndexValue = toIndexValue(getEndIndex());
        String newValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        fireValueChange(Core.EndIndex, oldIndexValue, newIndexValue);
        fireValueChange(Core.Range, oldValue, newValue);
        updateModifiedTime();
    }

    public void setStartIndex(int index) {
        String oldValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        Integer oldIndexValue = toIndexValue(getStartIndex());
        DOMUtils.setAttribute(implementation, ATTR_RANGE,
                InternalDOMUtils.toRangeValue(index, getEndIndex()));
        Integer newIndexValue = toIndexValue(getStartIndex());
        String newValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        fireValueChange(Core.StartIndex, oldIndexValue, newIndexValue);
        fireValueChange(Core.Range, oldValue, newValue);
        updateModifiedTime();
    }

    public boolean isMasterBoundary() {
        return DOMConstants.VAL_MASTER.equals(DOMUtils.getAttribute(
                implementation, ATTR_RANGE));
    }

    public void setMasterBoundary(boolean overall) {
        String oldValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        String value = overall ? DOMConstants.VAL_MASTER : null;
        DOMUtils.setAttribute(implementation, ATTR_RANGE, value);
        String newValue = DOMUtils.getAttribute(implementation, ATTR_RANGE);
        fireValueChange(Core.Range, oldValue, newValue);
        updateModifiedTime();
    }

    protected WorkbookImpl getRealizedWorkbook() {
        ITopic parent = getParent();
        if (parent instanceof TopicImpl) {
            return ((TopicImpl) parent).getRealizedWorkbook();
        }
        return null;
    }

    protected void addNotify(WorkbookImpl workbook, TopicImpl parent) {
        getImplementation().setIdAttribute(DOMConstants.ATTR_ID, true);
        workbook.getAdaptableRegistry().registerById(this, getId(),
                getImplementation().getOwnerDocument());
        setCoreEventSupport(parent.getCoreEventSupport());
        WorkbookUtilsImpl.increaseStyleRef(workbook, this);
    }

    protected void removeNotify(WorkbookImpl workbook, TopicImpl parent) {
        WorkbookUtilsImpl.decreaseStyleRef(workbook, this);
        setCoreEventSupport(null);
        workbook.getAdaptableRegistry().unregisterById(this, getId(),
                getImplementation().getOwnerDocument());
        getImplementation().setIdAttribute(DOMConstants.ATTR_ID, false);
    }

    public ICoreEventRegistration registerCoreEventListener(String type,
            ICoreEventListener listener) {
        return getCoreEventSupport().registerCoreEventListener(this, type,
                listener);
    }

    public void setCoreEventSupport(ICoreEventSupport coreEventSupport) {
        this.coreEventSupport = coreEventSupport;
    }

    public ICoreEventSupport getCoreEventSupport() {
        if (coreEventSupport != null)
            return coreEventSupport;
        return NullCoreEventSupport.getInstance();
    }

    private void fireValueChange(String type, Object oldValue, Object newValue) {
        getCoreEventSupport().dispatchValueChange(this, type, oldValue,
                newValue);
    }

    public long getModifiedTime() {
        String time = DOMUtils.getAttribute(implementation,
                DOMConstants.ATTR_TIMESTAMP);
        return NumberUtils.safeParseLong(time, 0);
    }

    public void updateModifiedTime() {
        setModifiedTime(System.currentTimeMillis());
        ITopic parent = getParent();
        if (parent != null) {
            ((TopicImpl) parent).updateModifiedTime();
        }
    }

    public void setModifiedTime(long time) {
        long oldTime = getModifiedTime();
        DOMUtils.setAttribute(implementation, DOMConstants.ATTR_TIMESTAMP,
                Long.toString(time));
        long newTime = getModifiedTime();
        fireValueChange(Core.ModifyTime, oldTime, newTime);
    }

}