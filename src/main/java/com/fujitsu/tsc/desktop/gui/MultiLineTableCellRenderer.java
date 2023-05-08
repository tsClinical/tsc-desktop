/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
	
	private static Logger logger = LogManager.getLogger();
	private Font defaultFont;

    public MultiLineTableCellRenderer() {
    	this.setLineWrap(true);
    	this.setWrapStyleWord(true);
    	this.setOpaque(true);
//    	this.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE));
    }
    
    public Component getTableCellRendererComponent(
    		JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	
    	/* Change the color of cells depending on the isSelected status */
    	if (isSelected) {
    		this.setForeground(table.getSelectionForeground());
    		this.setBackground(table.getSelectionBackground());
    	} else {
    		this.setForeground(GuiConstants.FONT_COLOR_ON_WHITE);
    		this.setBackground(table.getBackground());
    	}
    	
    	/*
    	 * Apply the table default font -
    	 * if you want to uncomment the below statements, make sure that non-English messages are displayed properly.
    	 */
		//defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE);
    	//this.setFont(defaultFont);
    	
    	if (value != null) {
    		this.setText(value.toString());
    	} else {
    		this.setText("");
    	}
    	
    	this.adjustRowHeight(table, row, column);
    	return this;
    }
    
    private void adjustRowHeight(JTable table, int row, int column) {
    	this.setSize(new Dimension(table.getTableHeader().getColumnModel().getColumn(column).getWidth(), this.getRowHeight()));
   		table.setRowHeight(row, this.getPreferredSize().height);	//getPreferredSize returns the adjusted size.
    }
}
