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
package org.xmind.core.util;

import java.io.IOException;

import org.w3c.dom.Document;
import org.xmind.core.CoreException;
import org.xmind.core.io.IInputSource;

public interface IXMLLoader {

    Document loadXMLFile(IInputSource source, String entryName)
            throws IOException, CoreException;

    Document createDocument();

}