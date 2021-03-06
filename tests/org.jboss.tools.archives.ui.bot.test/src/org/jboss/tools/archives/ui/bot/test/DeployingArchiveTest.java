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

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.archives.reddeer.archives.ui.ArchivePublishDialog;
import org.jboss.tools.archives.reddeer.archives.ui.ProjectArchivesExplorer;
import org.jboss.tools.archives.reddeer.component.Archive;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.view.ServersView;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests if deploying an archive via archives view and explorer is possible
 * 
 * @author jjankovi
 *
 */
@Require(clearProjects = true, perspective = "Java",
		 server = @Server(state = ServerState.NotRunning, 
		 version = "6.0", operator = ">="))
public class DeployingArchiveTest extends ArchivesTestBase {

	private static String project = "pr3";
	private final String ARCHIVE_NAME_1 = 
			project + "a.jar";
	private final String ARCHIVE_NAME_2 = 
			project + "b.jar";
	private final String PATH_SUFFIX = " [/" + project + "]"; 
	private final String PATH_ARCHIVE_1 = 
			ARCHIVE_NAME_1 + PATH_SUFFIX;
	private final String PATH_ARCHIVE_2 = 
			ARCHIVE_NAME_2 + PATH_SUFFIX;
	
	@BeforeClass
	public static void setup() {
		importArchiveProjectWithoutRuntime(project);
	}
	
	@Test
	public void testDeployingArchiveWithView() {
		
		/* prepare view for testing */
		view = viewForProject(project);
		
		/* publish into server with entered options */
		Archive archive	= view
			.getProject()
			.getArchive(PATH_ARCHIVE_1);
		fillPublishDialog(archive.publishToServer(), false, false);
				
		/* test archive is deployed */
		assertArchiveIsDeployed(project + "/" + ARCHIVE_NAME_1);
		
		/* remove archive from pre-configured server */
		removeArchiveFromServer(project + "/" + ARCHIVE_NAME_1);
		
		/* select the project again - workaround when switching views */
		view = viewForProject(project);
		
		/* edit publish setting - always publish option */
		fillPublishDialog(archive.editPublishSettings(), true, false);
		
		/* publish into server without dialog appears */
		archive.publishToServer();
		
		/* test archive is deployed */
		assertArchiveIsDeployed(project + "/" + ARCHIVE_NAME_1);
	}

	@Test
	public void testDeployingArchiveWithExplorer() {
		
		/* prepare explorer for testing */
		ProjectArchivesExplorer explorer = explorerForProject(project);
		
		/* publish into server with entered options */
		Archive archive = explorer
				.getArchive(PATH_ARCHIVE_2);
		fillPublishDialog(archive.publishToServer(), false, false);
		
		/* test archive is deployed */
		assertArchiveIsDeployed(project + "/" + ARCHIVE_NAME_2);
		
		/* remove archive from pre-configured server */
		removeArchiveFromServer(project + "/" + ARCHIVE_NAME_2);
		
		/* select the project again - workaround when switching views */
		explorer = explorerForProject(project);
		
		/* edit publish setting - always publish option */
		fillPublishDialog(archive.editPublishSettings(), true, false);
		
		/* publish into server without dialog appears */
		archive.publishToServer();
		
		/* test archive is deployed */
		assertArchiveIsDeployed(project + "/" + ARCHIVE_NAME_2);
	}
	
	private void fillPublishDialog(ArchivePublishDialog dialog, boolean alwaysPublish, boolean autodeploy) {
		if (!alwaysPublish && autodeploy) {
			throw new IllegalArgumentException(
					"Cannot autodeploy without always publish option checked");
		}
		dialog.selectServers(configuredState.getServer().name);
		if (alwaysPublish) dialog.checkAlwaysPublish();
		if (autodeploy) dialog.checkAutoDeploy();
		dialog.finish();
	}
	
	private void removeArchiveFromServer(String archive) {
		ServersView serversView = showServersView();
		serversView.removeProjectFromServers(archive);
	}
	
	private void assertArchiveIsDeployed(String archive) {
		ServersView serversView = showServersView();
		SWTBotTreeItem server = findConfiguredServer(serversView);
		server.collapse();
		server.expand();
		boolean found = false;
		for (String node : server.getNodes()) {
			if (node.contains(archive)) {
				found = true;
				break;
			}
		}
		assertTrue(archive + " was not deployed", found);
	}
	
	private SWTBotTreeItem findConfiguredServer(ServersView serversView) {
		SWTBotTreeItem server = null;
		try {
			server = serversView.findServerByName(serversView.bot().tree(), 
				configuredState.getServer().name);			
		} catch (WidgetNotFoundException exc) {
			fail("Server is not configured - missing in servers view");
		}
		return server;
	}


	private ServersView showServersView() {
		ServersView serversView = new ServersView();
		serversView.show();
		return serversView;
	}
}
