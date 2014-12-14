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
package org.xmind.core.marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IMarkerResource {

    String getPath();

    InputStream getInputStream();

    OutputStream getOutputStream();

    InputStream openInputStream() throws IOException;

    OutputStream openOutputStream() throws IOException;

    List<IMarkerVariation> getVariations();

    InputStream getInputStream(IMarkerVariation variation);

    OutputStream getOutputStream(IMarkerVariation variation);

}