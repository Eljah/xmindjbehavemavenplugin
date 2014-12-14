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
package org.xmind.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.xmind.core.io.IOutputTarget;
import org.xmind.core.io.IStorage;
import org.xmind.core.marker.IMarkerSheet;
import org.xmind.core.style.IStyleSheet;

/**
 * @author briansun
 * 
 */
public interface IWorkbook extends IAdaptable, IModifiable {

    /**
     * @return
     */
    ITopic createTopic();

    /**
     * @return
     */

    ISheet createSheet();

    /**
     * @param end1
     * @param end2
     * @return
     */
    IRelationship createRelationship(IRelationshipEnd end1,
                                     IRelationshipEnd end2);

    /**
     * @return
     */
    IRelationship createRelationship();

    /**
     * 
     * @return
     */
    IBoundary createBoundary();

    /**
     * 
     * @return
     */
    ISummary createSummary();

    /**
     * 
     */
    List<ISheet> getSheets();

    /**
     * 
     * @return
     */
    ISheet getPrimarySheet();

    /**
     * 
     * @param sheet
     */
    void addSheet(ISheet sheet);

    /**
     * 
     * @param sheet
     * @param index
     */
    void addSheet(ISheet sheet, int index);

    /**
     * 
     * @param sheet
     */
    void removeSheet(ISheet sheet);

    /**
     * 
     * @param sourceIndex
     * @param targetIndex
     */
    void moveSheet(int sourceIndex, int targetIndex);

    /**
     * Gets an element with the given identifier string.
     * 
     * @param id
     *            The identifier string of the desired element
     * @return The element with the given identifier string.
     */
    Object getElementById(String id);

    /**
     * Finds an element with the given identifier string requested starting from
     * the source object.
     * 
     * @param id
     * @param source
     * @return
     */
    Object findElement(String id, IAdaptable source);

    /**
     * Gets a Topic element with the given id. The topic returned is the same
     * as:
     * 
     * <pre>
     * Object element = getElementById(id);
     * return element instanceof ITopic ? (ITopic) element : null;
     * </pre>
     * 
     * @see #getElementById(String)
     * @param id
     * @return
     */
    ITopic findTopic(String id);

    /**
     * Finds the topic element with the given ID starting from the source
     * object.
     * 
     * @param id
     * @param source
     * @return
     */
    ITopic findTopic(String id, IAdaptable source);

    /**
     * 
     * @return
     */
    IStyleSheet getStyleSheet();

    /**
     * 
     * @return
     */
    IManifest getManifest();

    /**
     * 
     * @return
     */
    IMeta getMeta();

    /**
     * 
     * @return
     */
    IMarkerSheet getMarkerSheet();

    /**
     * @see org.xmind.core.INotes#PLAIN
     * @see org.xmind.core.INotes#HTML
     * @see org.xmind.core.IPlainNotesContent
     * @see org.xmind.core.IHtmlNotesContent
     * @param format
     * @return
     */
    INotesContent createNotesContent(String format);

    /**
     * 
     * @return
     */
    String getVersion();

    /**
     * Clones the specified elements into this workbook and returns a session
     * object mapping source resources to their clones.
     * 
     * <p>
     * Note that the elements cloned will have newly assigned identifying
     * attributes, so that it's safe to keep the cloned elements together with
     * their sources within one workbook.
     * </p>
     * 
     * @param sources
     * @return
     */
    ICloneData clone(Collection<? extends Object> sources);

    /**
     * 
     * @param topic
     * @return
     */
    ITopic cloneTopic(ITopic topic);

    /**
     * Imports the specified element into this workbook. The imported element
     * will have the same identifying attributes with the source element, so
     * this method is primarily used to replace the existing element that has
     * the same identifying attributes.
     * 
     * <p>
     * Note that adding imported elements into this workbook without first
     * removing the source element may cause unknown behavior when invocating
     * <code>getElementById()</code> or <code>findElement()</code>.
     * </p>
     * 
     * @param source
     *            the element to import
     * @return the imported element
     */
    IAdaptable importElement(IAdaptable source);

    /**
     * 
     * @param resourceType
     * @param resourceId
     * @return
     */
    IResourceRef createResourceRef(String resourceType, String resourceId);

    /**
     * Saves this workbook to the last saved location.
     * 
     * @throws java.io.IOException
     * @throws CoreException
     * @deprecated Workbook instances should NOT cache output target any more,
     *             as the output target may not be ready if called multiple
     *             times. Use other save(xxx) methods instead and prepare a
     *             proper output target in prior.
     */
    void save() throws IOException, CoreException;

    /**
     * Saves all contents of this workbook to a target local file. The default
     * implementation uses the ZIP file format to store multiple entries into
     * one file.
     * 
     * <p>
     * As a legacy behaviour, the file path passed to this method is remembered
     * and reused for the {@link #save()} method. However, this behaviour is not
     * recommended any more and may be removed in future. So clients should not
     * rely on the deprecated <code>save()</code> method any more.
     * </p>
     * 
     * <p>
     * Another legacy behaviour is that the file path passed to this method will
     * override the file path set by <code>setFile()</code>. So curently clients
     * need to call <code>setFile()</code> after calling this method if they
     * want the workbook refers to a different file path. However, this
     * behaviour may be removed in future to reduce the reliability on local
     * files.
     * </p>
     * 
     * @param file
     *            the absolute path of the target file in the local file system
     * @throws java.io.IOException
     *             if I/O error occurs
     * @throws CoreException
     *             if some logic or execution error occurs, e.g. missing some
     *             required components, or operation canceled by user, etc.
     */
    void save(String file) throws IOException, CoreException;

    /**
     * Saves all contents of this workbook to a target output stream. The
     * default implementation uses the ZIP file format to store multiple entries
     * into one output stream.
     * 
     * <p>
     * Note that it's not gauranteed that the output stream will be closed when
     * the process finishes, so clients should explictly call
     * <code>close()</code> on the output stream in a <code>finally</code>
     * block.
     * </p>
     * 
     * @param output
     *            the output stream to write workbook contents to
     * @throws java.io.IOException
     *             if I/O error occurs
     * @throws CoreException
     *             if some logic or execution error occurs, e.g. missing some
     *             required components, or operation canceled by user, etc.
     */
    void save(OutputStream output) throws IOException, CoreException;

    /**
     * Saves all contents of this workbook to a multi-entry output target.
     * 
     * <p>
     * Note that clients should make sure the output target is ready before
     * calling this method.
     * </p>
     * 
     * <p>
     * As a legacy behaviour, the output target passed in this method is
     * remembered and reused for the {@link #save()} method. However, this
     * behaviour is not recommended any more and may be removed in future. So
     * clients should not rely on the deprecated <code>save()</code> method any
     * more.
     * </p>
     * 
     * @param target
     *            the output target to write all entries of this workbook to
     * @throws java.io.IOException
     *             if I/O error occurs
     * @throws CoreException
     *             if some logic or execution error occurs, e.g. missing some
     *             required components, or operation canceled by user, etc.
     */
    void save(IOutputTarget target) throws IOException, CoreException;

    /**
     * Returns the path of the local file that this workbook refers to.
     * 
     * <p>
     * The path is typically set by {@link #setFile(String)} or
     * {@link #save(String)}.
     * </p>
     * 
     * <p>
     * Developer Guide: As a legacy concept, the reliability on local files will
     * be gradually reduced in future so that a workbook performs as a pure
     * in-memory model and support more kinds of output targets to save to. So
     * this method may one day be deprecated, and thus it's recommended that
     * clients use external mechanisms to remember which output target a
     * workbook refers to and is referred to.
     * <p>
     * 
     * @return the file path that this workbook refers to
     * @see #setFile(String)
     * @see #save(String)
     */
    String getFile();

    /**
     * Sets the path of a local file that this workbook will refer to.
     * 
     * <p>
     * Developer Guide: As a legacy concept, the reliability on local files will
     * be gradually reduced in future so that a workbook performs as a pure
     * in-memory model and support more kinds of output targets to save to. So
     * this method may one day be deprecated, and thus it's recommended that
     * clients use external mechanisms to remember which output target a
     * workbook refers to and is referred to.
     * <p>
     * 
     * @param file
     *            the path of a new local file that this workbook will refer to
     * @see #getFile()
     */
    void setFile(String file);

    /**
     * 
     * @param storage
     */
    void setTempStorage(IStorage storage);

    /**
     * 
     * @return
     */
    IStorage getTempStorage();

    /**
     * 
     * @param tempLocation
     */
    void setTempLocation(String tempLocation);

    /**
     * 
     * @return
     */
    String getTempLocation();

    /**
     * 
     * @throws java.io.IOException
     * @throws CoreException
     */
    void saveTemp() throws IOException, CoreException;

    /**
     * @param password
     */
    void setPassword(String password);

    /**
     * @return
     */
    String getPassword();

    /**
     * 
     * @return
     */
    IRevisionRepository getRevisionRepository();

}