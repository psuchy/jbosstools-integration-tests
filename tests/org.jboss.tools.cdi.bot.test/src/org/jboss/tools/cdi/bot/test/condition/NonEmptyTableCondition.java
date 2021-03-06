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

package org.jboss.tools.cdi.bot.test.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

public class NonEmptyTableCondition implements ICondition {

	private SWTBotTable table;
	
	public NonEmptyTableCondition(SWTBotTable table) {
		super();
		this.table = table;
	}
	
	public boolean test() throws Exception {
		try {
			return table.rowCount() != 0;
		} catch (WidgetNotFoundException e) {
		}
		return false;
	}

	public String getFailureMessage() {
		return "Table is empty, it does not contain any table items";
	}

	public void init(SWTBot bot) {
		
	}

}
