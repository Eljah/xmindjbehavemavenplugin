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

import org.xmind.core.marker.IMarker;
import org.xmind.core.marker.IMarkerGroup;

public abstract class MarkerGroup implements IMarkerGroup {

    public IMarker getMarker(String markerId) {
        if (markerId == null)
            return null;

        for (IMarker marker : getMarkers()) {
            if (markerId.equals(marker.getId()))
                return marker;
        }
        return null;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

}