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

import org.xmind.core.INamed;
import org.xmind.core.marker.IMarker;
import org.xmind.core.marker.IMarkerRef;

public abstract class MarkerRef implements IMarkerRef {

    public Object getAdapter(Class adapter) {
        if (adapter == INamed.class)
            return getMarker();
        return null;
    }

    public String getDescription() {
        String description = getOwnedSheet().getLegend().getMarkerDescription(
                getMarkerId());
        if (description != null)
            return description;
        IMarker marker = getMarker();
        if (marker != null) {
            String name = marker.getName();
            if (name != null)
                return name;
        }
        return ""; //$NON-NLS-1$
    }

}