/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

public class XmlValidateResultPanel extends JPanel {
    
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
	private GuiMain parent;
    private Font titleFont;
    private Font defaultFont;
    
    /* Header Panel*/
	private JPanel headerPanel;
    private JLabel titleL;

    /* Body Panel*/
	private JPanel bodyPanel;
    private JLabel vResultMessage;
    protected JScrollPane vResultScrollPane;
    protected JTable vResultTable;
    protected JLabel outputLocationL;
    protected JLabel outputLocationUrl;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton backButton;

	public XmlValidateResultPanel(GuiMain parent) {
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
        titleL = new JLabel("Validate XML against XML Schema");
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

    private void initBodyPanel() {
    	bodyPanel = new JPanel();
    	bodyPanel.setBackground(GuiConstants.COLOR_BG);

		vResultScrollPane = new JScrollPane();
    	vResultTable = new JTable();
        vResultMessage = new JLabel();

        vResultTable.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        vResultTable.setModel(new DefaultTableModel(
            new String [][] {
                {null, null},
            },
            new String [] {
                "Line", "Message"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        vResultScrollPane.setViewportView(vResultTable);
        vResultTable.getColumnModel().getColumn(0).setPreferredWidth(GuiConstants.FIRST_COLUMN_WIDTH);
        vResultTable.getColumnModel().getColumn(1).setPreferredWidth(GuiConstants.SECOND_COLUMN_WIDTH);
        vResultTable.setFont(defaultFont);
        vResultTable.getTableHeader().setFont(defaultFont);
        vResultTable.setPreferredScrollableViewportSize(new Dimension(GuiConstants.TABLE_WIDTH, GuiConstants.TABLE_HEIGHT));
        vResultTable.setFillsViewportHeight(true);
        vResultTable.setCellSelectionEnabled(true);
        vResultTable.setShowVerticalLines(false);
        
		vResultMessage.setFont(defaultFont);
        vResultMessage.setText("The following validation errors have been found.");
//        this.setBorder(BorderFactory.createEtchedBorder());

        GroupLayout layout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GuiConstants.RESULT_GAP_LEFT, GuiConstants.RESULT_GAP_LEFT)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(vResultMessage)
                    .addComponent(vResultScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GuiConstants.RESULT_GAP_RIGHT, GuiConstants.RESULT_GAP_RIGHT))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GuiConstants.RESULT_GAP_TOP, GuiConstants.RESULT_GAP_TOP)
                .addComponent(vResultMessage)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vResultScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            	parent.showPanel(parent.xmlValidatePanel);
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
