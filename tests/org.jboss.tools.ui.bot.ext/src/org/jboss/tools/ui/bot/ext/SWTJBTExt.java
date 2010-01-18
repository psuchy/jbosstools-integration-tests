 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.ui.bot.ext;

import static org.jboss.tools.ui.bot.ext.SWTTestExt.eclipse;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.JobName;
import org.jboss.tools.ui.bot.ext.types.ViewType;
/**
 * Provides JBoss Tools common operations based on SWTBot element operations
 * @author Vladimir Pakan
 *
 */
public class SWTJBTExt {

  SWTWorkbenchBot bot;
	Logger log = Logger.getLogger(SWTJBTExt.class);
	
	public SWTJBTExt(SWTWorkbenchBot bot) {
		this.bot = bot;
	}
	
	/**
	 * Check if JBoss Developer Studio Is Running
	 * Dynamic version of isJBDSRun Method
	 * @return
	 */
  public boolean isJBDSRun (){
    return SWTJBTExt.isJBDSRun(bot);
  }
  /**
   * Check if JBoss Developer Studio Is Running
   * @param bot
   * @return
   */
	public static boolean isJBDSRun (SWTWorkbenchBot bot){
	  boolean jbdsIsRunning = false;
	  try{
	    bot.menu(IDELabel.Menu.HELP).menu(IDELabel.Menu.ABOUT_JBOSS_DEVELOPER_STUDIO);
	    jbdsIsRunning = true;
	  }catch (WidgetNotFoundException wnfe){
	    // do nothing
	  }
	  
	  return jbdsIsRunning;
	  
	}
	/**
	 * Returns true when in Web Page of Wizard is defined at least one Server Runtime Instance
	 * @param bot
	 * @return
	 */
  public static boolean isServerDefinedInWebWizardPage(SWTWorkbenchBot bot){
    boolean isServerDefined = false;
    try{
      bot.label(IDELabel.ImportJSFProjectDialog.CHOICE_LIST_IS_EMPTY);
    } catch (WidgetNotFoundException wnfe){
      isServerDefined = true;
    }
    return isServerDefined;
  }
  /**
   * Return true when in Web Page of Wizard is defined at least one Server Runtime Instance
   * Dynamic version of isServerDefinedInWebWizardPage
   * @return
   */
  public boolean isServerDefinedInWebWizardPage(){
    return SWTJBTExt.isServerDefinedInWebWizardPage(bot);
  }
  /**
   * Starts Application Server in Server View on position specified by index
   * Dynamic version of startApplicationServer
   * @param index - zero based Position of Server within Server Tree
   */
  public void startApplicationServer(int index){
    SWTJBTExt.startApplicationServer(bot, index);
  }
  /**
   * Starts Application Server in Server View on position specified by index
   * @param bot
   * @param index - zero based Position of Server within Server Tree
   */
  public static void startApplicationServer(SWTWorkbenchBot bot , int index){
    SWTJBTExt.chooseServerPopupMenu(bot,index, IDELabel.Menu.START,45*1000L);
    bot.sleep(10*1000L);
  }
  /**
   * Stops Application Server in Server View on position specified by index
   * Dynamic version of stopApplicationServer
   * @param index - zero based Position of Server within Server Tree
   */
  public void stopApplicationServer(int index){
    SWTJBTExt.stopApplicationServer(bot, index);
  }
  /**
   * Stops Application Server in Server View on position specified by index
   * @param bot
   * @param index - zero based Position of Server within Server Tree
   */
  public static void stopApplicationServer(SWTWorkbenchBot bot , int index){
    SWTJBTExt.chooseServerPopupMenu(bot,index, IDELabel.Menu.STOP,10*1000L);
  }
  /**
   * Choose Server Popup Menu with specified label on Server with position specified by index 
   * @param bot
   * @param index
   * @param menuLabel
   * @param timeOut
   */
  public static void chooseServerPopupMenu(SWTWorkbenchBot bot , int index, String menuLabel, long timeOut){
    SWTEclipseExt swtEclipseExt = new SWTEclipseExt(bot);
    SWTBot servers = swtEclipseExt.showView(ViewType.SERVERS);
    SWTBotTree serverTree = servers.tree();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(serverTree, index);
    
    new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree,
        menuLabel, false)).click();
    swtEclipseExt.getSwtUtilExt().waitForAll(timeOut);
    
  }
  /**
   * Deletes Application Server in Server View on position specified by index
   * Dynamic version of deleteApplicationServer
   * @param index - zero based Position of Server within Server Tree
   */
  public void deleteApplicationServer(int index){
    SWTJBTExt.deleteApplicationServer(bot, index);
  }
  /**
   * Deletes Application Server in Server View on position specified by index
   * @param bot
   * @param index - zero based Position of Server within Server Tree
   */
  public static void deleteApplicationServer(SWTWorkbenchBot bot , int index){
    SWTJBTExt.chooseServerPopupMenu(bot,index, IDELabel.Menu.DELETE,10*1000L);
    bot.shell(IDELabel.Shell.DELETE_SERVER).activate();
    bot.button(IDELabel.Button.OK).click();
  }
  /**
   * Remove Project from all Servers
   * @param projectName
   */
  public void removeProjectFromServers(String projectName){
    
    eclipse.showView(ViewType.SERVERS);
    
    delay();
    
    SWTBotTree serverTree = bot.viewByTitle(IDELabel.View.SERVERS).bot().tree();
    
    // Expand All
    for (SWTBotTreeItem serverTreeItem : serverTree.getAllItems()){
      serverTreeItem.expand();
      // if JSF Test Project is deployed to server remove it
      int itemIndex = 0;
      SWTBotTreeItem[] serverTreeItemChildren = serverTreeItem.getItems(); 
      while (itemIndex < serverTreeItemChildren.length 
        && !serverTreeItemChildren[itemIndex].getText().startsWith(projectName)){
        itemIndex++;
      }  
      // Server Tree Item has Child with Text equal to JSF TEst Project
      if (itemIndex < serverTreeItemChildren.length){
        ContextMenuHelper.prepareTreeItemForContextMenu(serverTree,serverTreeItemChildren[itemIndex]);
        new SWTBotMenu(ContextMenuHelper.getContextMenu(serverTree, IDELabel.Menu.REMOVE, false)).click();
        bot.shell("Server").activate();
        bot.button(IDELabel.Button.OK).click();
      }  
    }
    delay();
  }
  
  public void delay() {
    bot.sleep(500);
  }
  /**
   * Delete Project from workspace
   * @param projectName
   */
  public void deleteProject(String projectName) {

    removeProjectFromServers(projectName);
    
    SWTBot packageExplorer = eclipse.showView(ViewType.PACKAGE_EXPLORER);
    delay();
    SWTBotTree tree = packageExplorer.tree();
    delay();
    
    ContextMenuHelper.prepareTreeItemForContextMenu(tree,
      tree.getTreeItem(projectName));
    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,
      IDELabel.Menu.DELETE, false)).click();
    bot.shell(IDELabel.Shell.DELETE_RESOURCES).activate();
    bot.button(IDELabel.Button.OK).click();
    
    new SWTUtilExt(bot).waitForNonIgnoredJobs();
    
  }
  /**
   * Choose Run On Server menu for specified project
   * @param bot
   * @param projectName
   */
  public static void runProjectOnServer(SWTWorkbenchBot bot, String projectName){
 
    SWTBotTree packageExplorerTree = eclipse.showView(ViewType.PACKAGE_EXPLORER).tree();

    packageExplorerTree.setFocus();
    SWTBotTreeItem packageExplorerTreeItem = packageExplorerTree
        .getTreeItem(projectName);
    
    packageExplorerTreeItem.select();
    packageExplorerTreeItem.click();
    // Search for Menu Item with Run on Server substring within label
    final SWTBotMenu menuRunAs = bot.menu(IDELabel.Menu.RUN).menu(IDELabel.Menu.RUN_AS);
    final MenuItem menuItem = UIThreadRunnable
      .syncExec(new WidgetResult<MenuItem>() {
        @SuppressWarnings("unchecked")
        public MenuItem run() {
          int menuItemIndex = 0;
          MenuItem menuItem = null;
          final MenuItem[] menuItems = menuRunAs.widget.getMenu().getItems();
          while (menuItem == null && menuItemIndex < menuItems.length){
            if (menuItems[menuItemIndex].getText().indexOf("Run on Server") > - 1){
              menuItem = menuItems[menuItemIndex];
            }
            else{
              menuItemIndex++;
            }
          }
        return menuItem;
        }
      });
    if (menuItem != null){
      new SWTBotMenu(menuItem).click();
      bot.shell(IDELabel.Shell.RUN_ON_SERVER).activate();
      bot.button(IDELabel.Button.FINISH).click();
      SWTUtilExt swtUtil = new SWTUtilExt(bot);      
      swtUtil.waitForJobs(10*1000L,JobName.BUILDING_WS);
      swtUtil.waitForJobs(10*1000L,JobName.UPDATING_INDEXES);
    }
    else{
      throw new WidgetNotFoundException("Unable to find Menu Item with Label 'Run on Server'");
    }
  }
  /**
   * Choose Run On Server menu for specified project
   * @param projectName
   */
  public void runProjectOnServer(String projectName){
    runProjectOnServer(bot,projectName);
  }
  /**
   * Creates new Server within Server View when Wizard for new Project is called
   * @param bot
   * @param serverGroup
   * @param serverType
   */
  public static void addServerToServerViewOnWizardPage (SWTWorkbenchBot bot,String serverGroup , String serverType){
    // Check if there is defined Application Server if not create one
    if (!SWTJBTExt.isServerDefinedInWebWizardPage(bot)){
      // Specify Application Server for Deployment
      bot.button(IDELabel.Button.NEW, 1).click();
      bot.shell(IDELabel.Shell.NEW_SERVER).activate();
      bot.tree().select(serverGroup);
      bot.tree().expandNode(serverGroup)
        .select(serverType);
      bot.button(IDELabel.Button.FINISH).click();
    }  
    
  }
  /**
   * Creates new Server within Server View when Wizard for new Project is called
   * @param serverGroup
   * @param serverType
   */
  public void addServerToServerViewOnWizardPage (String serverGroup , String serverType){
    addServerToServerViewOnWizardPage (bot,serverGroup , serverType);
  }
  
}
