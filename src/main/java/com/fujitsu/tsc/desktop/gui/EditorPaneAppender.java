/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.LogEvent;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class EditorPaneAppender extends AbstractAppender {

	public static final String APPENDER_NAME = "EditorPane";
	private JEditorPane logPane = null;
	private PatternLayout patternLayout;
	
	public EditorPaneAppender(PatternLayout patternLayout) {
		super(APPENDER_NAME, null, patternLayout, false, null);
		this.patternLayout = patternLayout;
	}

	public void setEditorPane(JEditorPane logPane) {
		this.logPane = logPane;
	}

	@Override
	public void append(LogEvent arg0) {
		if (arg0 == null) {
			return;
		}
        Document doc = logPane.getDocument();
        try {
        	StringBuilder builder = new StringBuilder(100);
        	this.patternLayout.serialize(arg0, builder);
        	doc.insertString(doc.getLength(), builder.toString(), null);
        } catch (BadLocationException ble) {
        	System.err.println(ble.getMessage());
        }
	}
}
