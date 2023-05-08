/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OdmExportResultPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger();
	private GuiMain parent;
    private Font titleFont;
    private Font defaultFont;
    
    /* Header Panel*/
	private JPanel headerPanel;
    private JLabel titleL;

    /* Body Panel*/
	private JPanel bodyPanel;
    private JLabel gResultMessage;
    protected JScrollPane gResultScrollPane;
    protected JEditorPane gResultEditorPane;
    protected JLabel outputLocationL;
    protected JLabel outputLocationUrl;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton backButton;
	
	public OdmExportResultPanel(GuiMain parent) {
		this.parent = parent;
        titleFont = new Font(GuiConstants.FONT_NAME_TITLE, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE_TITLE);
        defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE);
        this.setBackground(GuiConstants.COLOR_BORDER);
        
        initHeaderPanel();
        initBodyPanel();
        initFooterPanel();
        
        GroupLayout panelLayout = new GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
        		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
               	.addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               	.addComponent(bodyPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(footerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
        		panelLayout.createSequentialGroup()
           		.addGap(GuiConstants.BORDER_WIDTH_THICK, GuiConstants.BORDER_WIDTH_THICK, GuiConstants.BORDER_WIDTH_THICK)
               	.addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               	.addGap(GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH)
                .addComponent(bodyPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH, GuiConstants.BORDER_WIDTH)
               	.addComponent(footerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
	}
	
    private void initHeaderPanel() {
    	headerPanel = new JPanel();
    	headerPanel.setBackground(GuiConstants.COLOR_BG);
        titleL = new JLabel("Convert from Excel to ODM-XML");
    	titleL.setFont(titleFont);
        titleL.setForeground(GuiConstants.FONT_COLOR_TITLE);
        
        GroupLayout headerPanelLayout = new GroupLayout(headerPanel);
        headerPanelLayout.setAutoCreateContainerGaps(true);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
        	headerPanelLayout.createSequentialGroup()
        		.addComponent(titleL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createSequentialGroup()
            	.addComponent(titleL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }
    
	public void clearBodyPanel() {
		gResultEditorPane.setText("");
		outputLocationUrl.setText("");
	}
	
	private void initBodyPanel() {
    	bodyPanel = new JPanel();
    	bodyPanel.setBackground(GuiConstants.COLOR_BG);
		
		gResultScrollPane = new JScrollPane();
	    gResultEditorPane = new JEditorPane();
	    gResultScrollPane.setViewportView(gResultEditorPane);
	    gResultScrollPane.setPreferredSize(new Dimension(GuiConstants.EDITOR_PANE_WIDTH, GuiConstants.EDITOR_PANE_HEIGHT));
	    
		gResultMessage = new JLabel();
		gResultMessage.setFont(defaultFont);
		gResultMessage.setText("ODM Generator Log:");
		outputLocationL = new JLabel();
		outputLocationL.setFont(defaultFont);
		outputLocationL.setText("Output Folder: ");
		outputLocationUrl = new JLabel();
		//outputLocationUrl.setFont(defaultFont);	//Commented out to avoid non-English letters are collapsed.
		outputLocationUrl.setForeground(Color.BLUE);
		outputLocationUrl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported() && outputLocationUrl.getText() != null) {
				    try {
						Desktop.getDesktop().open(new File(outputLocationUrl.getText()));
					} catch (IOException ex) {
						logger.error(ex.getMessage());
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				outputLocationUrl.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
        //this.setBorder(BorderFactory.createEtchedBorder());
        
		gResultEditorPane.setForeground(GuiConstants.FONT_COLOR_ON_WHITE);
        gResultEditorPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gResultScrollPane.getVerticalScrollBar().setValue(gResultScrollPane.getVerticalScrollBar().getMaximum());
			}
        });
        
        GroupLayout layout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GuiConstants.RESULT_GAP_LEFT, GuiConstants.RESULT_GAP_LEFT)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(gResultMessage)
                    .addComponent(gResultScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                    	.addComponent(outputLocationL)
                    	.addComponent(outputLocationUrl, GroupLayout.PREFERRED_SIZE,
                    		gResultScrollPane.getSize().width - outputLocationL.getSize().width, Short.MAX_VALUE)))
                .addContainerGap(GuiConstants.RESULT_GAP_RIGHT, GuiConstants.RESULT_GAP_RIGHT))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GuiConstants.RESULT_GAP_TOP, GuiConstants.RESULT_GAP_TOP)
                .addComponent(gResultMessage)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gResultScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                		.addComponent(outputLocationL)
                		.addComponent(outputLocationUrl))
                .addContainerGap(GuiConstants.RESULT_GAP_BOTTOM, GuiConstants.RESULT_GAP_BOTTOM))
        );
    }
	
    private void initFooterPanel() {
    	footerPanel = new JPanel();
    	footerPanel.setBackground(GuiConstants.COLOR_BG);
        backButton = new JButton("Back");
        backButton.setFont(defaultFont);
        backButton.setForeground(GuiConstants.FONT_COLOR);
        backButton.setBackground(GuiConstants.COLOR_BG);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	parent.showPanel(parent.odmExportPanel);
            }
        });
        
        GroupLayout footerPanelLayout = new GroupLayout(footerPanel);
        footerPanelLayout.setAutoCreateContainerGaps(true);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
        	footerPanelLayout.createSequentialGroup()
	      		.addContainerGap(GuiConstants.FOOTER_GAP_LEFT, Short.MAX_VALUE)
	      		.addComponent(backButton)
        );
        footerPanelLayout.setVerticalGroup(
        	footerPanelLayout.createSequentialGroup()
      			.addComponent(backButton)
      			.addContainerGap(Short.MAX_VALUE, Short.MAX_VALUE)
        );
    }
}
