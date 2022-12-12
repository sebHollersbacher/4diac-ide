/*******************************************************************************
 * Copyright (c) 2012, 2021 TU Wien ACIN, Profactor GmbH, fortiss GmbH,
 *                          Johannes Kepler University Linz,
 *                          Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alois Zoitl, Gerhard Ebenhofer, Monika Wenger
 *     - initial API and implementation and/or initial documentation
 *   Alois Zoitl - New Project Explorer layout
 *               - Fixed handing of project renameing
 *******************************************************************************/
package org.eclipse.fordiac.ide.systemmanagement.changelistener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.fordiac.ide.model.NameRepository;
import org.eclipse.fordiac.ide.model.libraryElement.AutomationSystem;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElement;
import org.eclipse.fordiac.ide.model.typelibrary.TypeEntry;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibrary;
import org.eclipse.fordiac.ide.model.typelibrary.TypeLibraryManager;
import org.eclipse.fordiac.ide.systemmanagement.ISystemEditor;
import org.eclipse.fordiac.ide.systemmanagement.Messages;
import org.eclipse.fordiac.ide.systemmanagement.SystemManager;
import org.eclipse.fordiac.ide.ui.FordiacLogHelper;
import org.eclipse.fordiac.ide.ui.editors.EditorUtils;
import org.eclipse.fordiac.ide.ui.editors.FordiacEditorMatchingStrategy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class FordiacResourceChangeListener implements IResourceChangeListener {

	private static class FileToRenameEntry {

		private final IFile filetoRename;
		private final TypeEntry existingTypeEntry;

		public FileToRenameEntry(final IFile filetoRename, final TypeEntry existingTypeEntry) {
			super();
			this.filetoRename = filetoRename;
			this.existingTypeEntry = existingTypeEntry;
		}

		public TypeEntry getTypeEntry() {
			return existingTypeEntry;
		}

		public IFile getFile() {
			return filetoRename;
		}
	}

	/** The instance. */
	private final SystemManager systemManager;
	private final Collection<TypeEntry> changedFiles;
	private final Collection<FileToRenameEntry> filesToRename;

	public FordiacResourceChangeListener(final SystemManager systemManager) {
		this.systemManager = systemManager;
		this.changedFiles = new HashSet<>();
		this.filesToRename = new HashSet<>();
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			// get the delta, if any, for the documentation directory
			final IResourceDelta rootDelta = event.getDelta();
			try {
				rootDelta.accept(visitor);
				if (!changedFiles.isEmpty()) {
					handleChangedFiles();
				}
				if (!filesToRename.isEmpty()) {
					handleFilesToRename();
				}

			} catch (final CoreException e) {
				FordiacLogHelper.logError("Couldn't process resource delta", e); //$NON-NLS-1$
			}
		}
	}

	private void handleChangedFiles() {
		Display.getDefault().syncExec(() -> {
			final List<IEditorPart> changedOpenedDirtyEditors = collectOpenedEditors();
			final List<IEditorFileChangeListener> editorListener = changedOpenedDirtyEditors.stream()
					.filter(IEditorFileChangeListener.class::isInstance).map(IEditorFileChangeListener.class::cast)
					.collect(Collectors.toList());
			changedFiles.clear();
			handleFileRefreshWIzards(editorListener);
		});

	}

	private void handleFilesToRename() {
		filesToRename.forEach(FordiacResourceChangeListener::autoRenameExistingType);
		filesToRename.clear();
	}

	private static void handleFileRefreshWIzards(final List<IEditorFileChangeListener> editorListener) {
		int code = IEditorFileChangeListener.INIT;
		for (final IEditorFileChangeListener editor : editorListener) {

			if (!((IEditorPart) editor).isDirty()) {
				editor.reloadFile();
				continue;
			}

			if (code == IEditorFileChangeListener.INIT) {
				code = openFileChangedDialog(editor);
			}

			switch (code) {
			case IEditorFileChangeListener.YES:
				editor.reloadFile();
				code = IEditorFileChangeListener.INIT;
				break;
			case IEditorFileChangeListener.YES_TO_ALL:
				editor.reloadFile();
				break;
			case IEditorFileChangeListener.NO:
				code = IEditorFileChangeListener.INIT;
				break;
			case IEditorFileChangeListener.NO_TO_ALL:
				return;
			default:
				FordiacLogHelper.logError(code + " is not a valid dialog output"); //$NON-NLS-1$
				break;
			}
		}
	}

	public static int openFileChangedDialog(final IEditorFileChangeListener editor) {
		final IFile file = editor.getFile();
		final String info = MessageFormat.format(Messages.AutomationSystemEditor_Info, file.getFullPath().toOSString());
		final MessageDialog dialog = new MessageDialog(((IEditorPart) editor).getSite().getShell(),
				Messages.AutomationSystemEditor_Title, null, info, MessageDialog.INFORMATION,
				new String[] { Messages.FordiacResourceChangeListener_0, Messages.FordiacResourceChangeListener_1,
						Messages.FordiacResourceChangeListener_2, Messages.FordiacResourceChangeListener_3 },
				0);
		return dialog.open();
	}

	private List<IEditorPart> collectOpenedEditors() {
		final List<IEditorPart> changedOpenedDirtyEditors = new ArrayList<>();
		IEditorPart activeEditor = null;

		for (final TypeEntry entry : changedFiles) {
			final IEditorPart findEditor = EditorUtils
					.findEditor((final IEditorPart editor) -> editor.getEditorInput() instanceof FileEditorInput
							&& ((FileEditorInput) editor.getEditorInput()).getFile().equals(entry.getFile()));

			if (findEditor != null) {
				if (findEditor == EditorUtils.getCurrentActiveEditor()) {
					activeEditor = findEditor;
				} else {
					changedOpenedDirtyEditors.add(findEditor);
				}
			} else {
				// no editor is currently open purge any editable model
				entry.setTypeEditable(null);
				SystemManager.INSTANCE.notifyListeners();
			}
		}

		if (activeEditor != null) {
			changedOpenedDirtyEditors.add(0, activeEditor); // we should add the active editor in front so that always
			// the current opened editor has the corresponding popup
		}
		return changedOpenedDirtyEditors;
	}

	IResourceDeltaVisitor visitor = delta -> {
		switch (delta.getKind()) {
		case IResourceDelta.CHANGED:
			return handleResourceChanged(delta);
		case IResourceDelta.REMOVED:
			return handleResourceRemoved(delta);
		case IResourceDelta.ADDED:
			if (IResourceDelta.MOVED_FROM == (delta.getFlags() & IResourceDelta.MOVED_FROM)) {
				return handleResourceMovedFrom(delta);
			}
			return handleResourceCopy(delta);
		default:
			break;
		}
		return true;
	};

	private static boolean isSystemFile(final IFile file) {
		return SystemManager.SYSTEM_FILE_ENDING.equalsIgnoreCase(file.getFileExtension());
	}

	private boolean handleResourceChanged(final IResourceDelta delta) {

		if (isFileChange(delta)) {
			collectTypeEntries(delta);
		} else if (IResourceDelta.OPEN == delta.getFlags()) {
			// project is opened oder closed
			if (0 != delta.getAffectedChildren(IResourceDelta.ADDED).length) {
				systemManager.notifyListeners();
			} else if (0 != delta.getAffectedChildren(IResourceDelta.REMOVED).length) {
				handleProjectRemove(delta);
				return false;
			}
		} else if (delta.getResource().getType() == IResource.PROJECT) {
			return checkForErrorMarkerChanges(delta);
		}



		return true;
	}

	public boolean checkForErrorMarkerChanges(final IResourceDelta delta) {
		for (final IResourceDelta d : delta.getAffectedChildren()) {
			if (IResourceDelta.MARKERS == (d.getFlags() & IResourceDelta.MARKERS)) {
				systemManager.notifyListeners();
				return false;
			}
		}
		return true;
	}

	private void collectTypeEntries(final IResourceDelta delta) {
		final IFile file = (IFile) delta.getResource();

		TypeEntry typeEntryForFile = TypeLibraryManager.INSTANCE.getTypeEntryForFile(file);
		if (typeEntryForFile == null) {
			typeEntryForFile = systemManager.getTypeEntry(file);
		}
		if (typeEntryForFile != null
				&& typeEntryForFile.getLastModificationTimestamp() != file.getModificationStamp()) {
			changedFiles.add(typeEntryForFile);
		}
	}

	private static boolean isFileChange(final IResourceDelta delta) {
		return delta.getResource().getType() == IResource.FILE && delta.getFlags() != IResourceDelta.MARKERS;
	}

	private boolean handleResourceRemoved(final IResourceDelta delta) {
		if (delta.getFlags() == IResourceDelta.MOVED_TO) {
			// we will handle movement only on the add side
			return false;
		}
		switch (delta.getResource().getType()) {
		case IResource.FILE:
			handleFileDelete(delta);
			break;
		case IResource.PROJECT:
			handleProjectRemove(delta);
			return false;
		default:
			// we don't need to do anything in the other cases
			break;
		}
		return true;
	}

	private boolean handleResourceMovedFrom(final IResourceDelta delta) {
		switch (delta.getResource().getType()) {
		case IResource.FILE:
			handleFileMove(delta);
			break;
		case IResource.PROJECT:
			handleProjectRename(delta);
			break;
		default:
			break;
		}
		return true;
	}

	private boolean handleResourceCopy(final IResourceDelta delta) {
		switch (delta.getResource().getType()) {
		case IResource.FILE:
			handleFileCopy(delta);
			break;
		case IResource.FOLDER:
			// if a folder has been moved we need to update the IFile of the children
			return true;
		case IResource.PROJECT:
			return true;
		default:
			break;
		}
		return false;
	}

	private void handleFileDelete(final IResourceDelta delta) {
		final TypeLibrary typeLib = TypeLibraryManager.INSTANCE.getTypeLibrary(delta.getResource().getProject());
		final IFile file = (IFile) delta.getResource();

		if (isSystemFile(file)) {
			systemManager.removeSystem(file);
		} else {
			final TypeEntry entry = TypeLibraryManager.INSTANCE.getTypeEntryForFile(file);
			if (null != entry) {
				closeAllEditorsForFile(file);
				final FileToRenameEntry rnEntry = getFileRenameEntry(entry);
				if(rnEntry != null) {
					// the file was moved to a new location update the type entry and do not remove it from the
					// rename list
					entry.setFile(rnEntry.getFile());
					filesToRename.remove(rnEntry);
				} else {
					typeLib.removeTypeEntry(entry);
				}
			}
		}
	}

	private FileToRenameEntry getFileRenameEntry(final TypeEntry palEntry) {
		return filesToRename.stream().filter(entry -> entry.getTypeEntry().equals(palEntry)).findAny().orElse(null);
	}

	private void handleFileCopy(final IResourceDelta delta) {
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(delta.getResource().getFullPath());
		if (file.getProject().isOpen() && delta.getFlags() != IResourceDelta.MARKERS) {

			if (isSystemFile(file)) {
				// in case of a copied system file we just need to fix the name in the root XML
				// node
				renameSystemFileCopy(file);
			} else {
				final TypeLibrary typeLib = TypeLibraryManager.INSTANCE
						.getTypeLibrary(delta.getResource().getProject());
				final TypeEntry typeEntryForFile = TypeLibraryManager.INSTANCE.getTypeEntryForFile(file);
				if (typeEntryForFile == null) {
					final TypeEntry entry = typeLib.createTypeEntry(file);
					if (null != entry) {
						updateTypeEntry(file, entry);
					}
				} else if (!file.equals(typeEntryForFile.getFile())) {
					// After a file has been copied and the copied file is not the same as the founded type entry
					// the file and the resulting type must be renamed with a unique name put it in the rename list
					filesToRename.add(new FileToRenameEntry(file, typeEntryForFile));
				}

			}
		}
	}

	protected static void autoRenameExistingType(final FileToRenameEntry entry) {

		final WorkspaceJob job = new WorkspaceJob(
				Messages.FordiacResourceChangeListener_4 + entry.getFile().getName()) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				final LibraryElement type = entry.getTypeEntry().getType();
				final String oldName = type.getName();
				final String newName = NameRepository.createUniqueTypeName(type);
				if (newName == null || newName.equals(oldName)) {
					return Status.CANCEL_STATUS;
				}
				final String oldPath = entry.getFile().getFullPath().toOSString();
				final String newPath = replaceLast(oldPath, oldName, newName);

				try {// this will trigger handleFileMove() and further handleFileRename(). file.move() is a workaround
					// to rename a file
					entry.getFile().move(new Path(newPath), true, new NullProgressMonitor());
				} catch (final CoreException e) {
					FordiacLogHelper.logError(e.getMessage(), e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(entry.getFile().getProject());
		job.schedule();
	}

	public static String replaceLast(final String text, final String regex, final String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement); //$NON-NLS-1$ //$NON-NLS-2$
	}

	static final Pattern systemNamePattern = Pattern
			.compile("\\<System\\p{javaWhitespace}+(Comment=\".*\"\\p{javaWhitespace}+)?Name=\"([^\"]*)"); //$NON-NLS-1$

	private void renameSystemFileCopy(final IFile file) {
		final WorkspaceJob job = new WorkspaceJob(Messages.FordiacResourceChangeListener_5 + file.getName()) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				boolean wrongName = false;
				final String newTypeName = TypeEntry.getTypeNameFromFile(file);
				try (Scanner scanner = new Scanner(file.getContents())) {
					final String name = scanner.findWithinHorizon(systemNamePattern, 0);
					wrongName = (null != name) && (!name.endsWith("\"" + newTypeName)); //$NON-NLS-1$
				} catch (final Exception e) {
					FordiacLogHelper.logError(e.getMessage(), e);
				}
				if (wrongName) {
					final AutomationSystem system = systemManager.getSystem(file);
					if ((null != system) && (!newTypeName.equals(system.getName()))) {
						system.setName(TypeEntry.getTypeNameFromFile(file));
						SystemManager.saveSystem(system);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(file.getProject());
		job.schedule();
	}

	private void handleProjectRename(final IResourceDelta delta) {
		final IProject oldProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(delta.getMovedFromPath().lastSegment());
		final IProject newProject = delta.getResource().getProject();
		TypeLibraryManager.INSTANCE.renameProject(oldProject, newProject);
		systemManager.renameProject(oldProject, newProject);
	}

	private void handleFileMove(final IResourceDelta delta) {
		final IFile src = ResourcesPlugin.getWorkspace().getRoot().getFile(delta.getMovedFromPath());
		final IFile dst = (IFile) delta.getResource();

		if (src.getParent().equals(dst.getParent())) {
			handleFileRename(dst, src);
		} else {
			if (!src.getProject().equals(dst.getProject())) {
				if (src.getProject().exists()) {
					handleFileMoveBetweenProjects(src, dst);
				} else {
					handleFileAfterProjectRename(src, dst);
				}
			} else {
				// file was moved update pallette entry
				final TypeEntry entry = TypeLibraryManager.INSTANCE.getTypeEntryForFile(src);
				if (null != entry) {
					entry.setFile(dst);
				}
			}
			updateEditorInput(src, dst);
		}
	}

	private void handleFileMoveBetweenProjects(final IFile src, final IFile dst) {
		if (isSystemFile(src)) {
			systemManager.moveSystemToNewProject(src, dst);
		} else {
			final TypeLibrary srcTypeLib = TypeLibraryManager.INSTANCE.getTypeLibrary(src.getProject());
			final TypeEntry entry = srcTypeLib.getTypeEntry(src);
			if (null != entry) {
				srcTypeLib.removeTypeEntry(entry);
				entry.setFile(dst);
				final TypeLibrary dstTypeLib = TypeLibraryManager.INSTANCE.getTypeLibrary(dst.getProject());
				dstTypeLib.addTypeEntry(entry);
			}
		}
	}

	private void handleFileAfterProjectRename(final IFile src, final IFile dst) {
		if (isSystemFile(src)) {
			systemManager.updateSystemFile(dst.getProject(), src, dst);  // if loaded the system should already be in
			// the list of the new project
		} else {
			final TypeLibrary typeLib = TypeLibraryManager.INSTANCE.getTypeLibrary(dst.getProject());
			final TypeEntry entry = typeLib.getTypeEntry(src);
			if (entry == null) {
				// we have to create the entry
				typeLib.createTypeEntry(dst);
			} else {
				entry.setFile(dst);
			}
		}

	}

	private void handleFileRename(final IFile dst, final IFile src) {
		if (isSystemFile(dst)) {
			renameSystemFile(dst);
		} else {
			handleTypeRename(src, dst);
		}
		systemManager.notifyListeners();
	}

	private static void handleTypeRename(final IFile src, final IFile file) {
		final TypeLibrary typeLibrary = TypeLibraryManager.INSTANCE.getTypeLibrary(file.getProject());
		final TypeEntry entry = TypeLibraryManager.INSTANCE.getTypeEntryForFile(src);
		if (entry != null && src.equals(entry.getFile())) {
			updateTypeEntry(file, entry);
		} else {
			updateTypeEntry(file, typeLibrary.createTypeEntry(file));
		}
	}

	private void renameSystemFile(final IFile file) {
		final WorkspaceJob job = new WorkspaceJob(Messages.FordiacResourceChangeListener_7 + file.getName()) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				final AutomationSystem system = systemManager.getSystem(file);
				if (null != system) {
					final String newTypeName = TypeEntry.getTypeNameFromFile(file);
					if (!newTypeName.equals(system.getName())) {
						system.setName(TypeEntry.getTypeNameFromFile(file));
						SystemManager.saveSystem(system);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(file.getProject());
		job.schedule();
	}

	private static void updateTypeEntry(final IFile newFile, final TypeEntry entry) {
		if (null != entry) {
			final String newTypeName = TypeEntry.getTypeNameFromFile(newFile);
			entry.getTypeLibrary().removeTypeEntry(entry);
			entry.setFile(newFile);
			entry.getTypeLibrary().addTypeEntry(entry);

			final WorkspaceJob job = new WorkspaceJob("Save Renamed type: " + entry.getTypeName()) { //$NON-NLS-1$
				@Override
				public IStatus runInWorkspace(final IProgressMonitor monitor) {
					// do the actual work in here
					final LibraryElement type = entry.getTypeEditable();
					if ((null != type) && // this means we couldn't load the type seems
							// like a problem in the type's XML file
							// TODO report on error
							(!newTypeName.equals(type.getName()))) {
						type.setName(newTypeName);
						entry.save();
					}
					return Status.OK_STATUS;
				}
			};
			job.setRule(newFile.getProject());
			job.schedule();
		}
	}

	private void handleProjectRemove(final IResourceDelta delta) {
		final IProject project = delta.getResource().getProject();
		closeAllProjectRelatedEditors(project);
		systemManager.removeProject(project);
	}

	private static void closeAllProjectRelatedEditors(final IProject project) {
		Display.getDefault().asyncExec(() -> EditorUtils.closeEditorsFiltered((final IEditorPart editor) -> {
			final IEditorInput input = editor.getEditorInput();
			if ((input instanceof FileEditorInput)
					&& (project.equals(((FileEditorInput) input).getFile().getProject()))) {
				return true;
			}
			if (editor instanceof ISystemEditor) {
				final AutomationSystem system = ((ISystemEditor) editor).getSystem();
				return project.equals(system.getSystemFile().getProject());
			}
			return false;
		}));
	}

	private static void closeAllEditorsForFile(final IFile file) {
		// display related stuff needs to run in a display thread
		Display.getDefault().asyncExec(() -> EditorUtils.closeEditorsFiltered((final IEditorPart editor) -> {
			final IEditorInput input = editor.getEditorInput();
			return (input instanceof FileEditorInput) && (file.equals(((FileEditorInput) input).getFile()));
		}));
	}

	private static FordiacEditorMatchingStrategy editorMatching = new FordiacEditorMatchingStrategy();

	private static void updateEditorInput(final IFile src, final IFile dst) {
		Display.getDefault().syncExec(() -> {
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IEditorReference[] editorReferences = activePage.getEditorReferences();

			for (final IEditorReference editorReference : editorReferences) {
				final IEditorPart editor = editorReference.getEditor(false);
				if (null != editor) {
					//the editor is loaded check if it is ours and if yes update it
					final IEditorInput input = editor.getEditorInput();
					if((src.equals(((FileEditorInput) input).getFile()) && editor instanceof IEditorFileChangeListener)) {
						((IEditorFileChangeListener)editor).updateEditorInput(new FileEditorInput(dst));
					}
				} else {
					// the editor is not yet loaded check if it may be ours. We can not load it as the file it is referring
					// to is not existing anymore, therefore we can only close it
					if (editorMatching.matches(editorReference, new FileEditorInput(src))) {
						activePage.closeEditors(new IEditorReference[] { editorReference }, false);

					}
				}
			}
		});
	}

}
