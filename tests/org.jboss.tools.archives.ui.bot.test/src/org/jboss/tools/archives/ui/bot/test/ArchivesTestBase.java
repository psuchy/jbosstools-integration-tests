/*******************************************************************************
 * Copyright (c) 2010-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.archives.ui.bot.test;

import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.jdt.ui.ide.NewJavaProjectWizardDialog;
import org.jboss.reddeer.eclipse.jdt.ui.ide.NewJavaProjectWizardPage;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.archives.reddeer.archives.ui.ProjectArchivesExplorer;
import org.jboss.tools.archives.reddeer.archives.ui.ProjectArchivesView;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ErrorLogView;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author jjankovi
 *
 */
@Require(clearProjects = true, perspective = "Java")
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ ArchivesAllBotTests.class })
public class ArchivesTestBase extends SWTTestExt {

	protected ProjectExplorer projectExplorer = new ProjectExplorer();
	protected ProjectArchivesView view = new ProjectArchivesView();
	
	protected ProjectArchivesView openProjectArchivesView() {
		view.open();
		return view;
	}
	
	protected ProjectArchivesView viewForProject(String projectName) {
		view.open();
		projectExplorer.open();
		projectExplorer.getProject(projectName).select();
		view.open();
		return view;
	}
	
	protected ProjectArchivesExplorer explorerForProject(String projectName) {
		return new ProjectArchivesExplorer(projectName);
	}
	
	protected void assertArchiveIsInView(ProjectArchivesView view, 
			String archiveName) {
		try {
			view.open();
			view.getProject().getArchive(archiveName);
		} catch (Exception sle) {
			fail("'" + archiveName + "' is not in archives view but it should!");
		}
	}
	
	protected void assertArchiveIsNotInView(ProjectArchivesView view, 
			String archiveName) {
		try {
			view.open();
			view.getProject().getArchive(archiveName);
			fail("'" + archiveName + "' is in archives view but it should not!");
		} catch (Exception sle) {
			
		}
	}
	
	protected void assertArchiveIsInExplorer(ProjectArchivesExplorer explorer, 
			String archiveName) {
		try {
			explorer.getArchive(archiveName);
		} catch (Exception sle) {
			fail("'" + archiveName + "' is not in archives explorer but it should!");
		}
	}
	
	protected void assertArchiveIsNotInExplorer(ProjectArchivesExplorer explorer, 
			String archiveName) {
		try {
			explorer.getArchive(archiveName);
			fail("'" + archiveName + "' is in archives explorer but it should not!");
		} catch (Exception sle) {
		}
	}
	
	protected static void clearErrorView() {
		new ErrorLogView().clear();
	}
	
	protected static void createJavaProject(String projectName) {
		NewJavaProjectWizardDialog javaProject = new NewJavaProjectWizardDialog();
		javaProject.open();
		
		NewJavaProjectWizardPage javaWizardPage = javaProject.getFirstPage();
		javaWizardPage.setProjectName(projectName);
		
		javaProject.finish(false);
	}
	
	protected static void importArchiveProjectWithoutRuntime(String projectName) {
		
		String location = "/resources/prj/" + projectName;
		importArchiveProjectWithoutRuntime(projectName, location, projectName);
	}
	
	protected static void importArchiveProjectWithoutRuntime(String projectName, 
			String projectLocation, String dir) {
		ImportHelper.importProject(projectLocation, dir, Activator.PLUGIN_ID);
	}
	
	protected void addArchivesSupport(String projectName) {
		addRemoveArchivesSupport(projectName, true);
	}
	
	protected void removeArchivesSupport(String projectName) {
		addRemoveArchivesSupport(projectName, false);
	}
	
	private void addRemoveArchivesSupport(String projectName, boolean add) {
		projectExplorer.getProject(projectName).select();
		if (add) {
			new ContextMenu(IDELabel.Menu.PACKAGE_EXPLORER_CONFIGURE, 
					IDELabel.Menu.ADD_ARCHIVE_SUPPORT).select();
		} else {
			new ContextMenu(IDELabel.Menu.PACKAGE_EXPLORER_CONFIGURE, 
					IDELabel.Menu.REMOVE_ARCHIVE_SUPPORT).select();
		}
		new WaitWhile(new JobIsRunning());
	}
	
}
