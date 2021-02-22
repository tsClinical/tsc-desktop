/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class EditorPaneAppender extends AppenderSkeleton {

	private JEditorPane logPane = null;
	
	public EditorPaneAppender() {
	}

	public void setEditorPane(JEditorPane logPane) {
		this.logPane = logPane;
	}

	@Override
	protected void append(LoggingEvent arg0) {
        Document doc = logPane.getDocument();
        try {
        	doc.insertString(doc.getLength(), this.layout.format(arg0), null);
        } catch (BadLocationException ble) {
        	System.err.println(ble.getMessage());
        }
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}
}
