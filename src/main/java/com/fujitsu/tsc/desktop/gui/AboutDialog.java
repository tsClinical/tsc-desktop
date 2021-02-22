/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.GroupLayout.Alignment;

import org.apache.log4j.Logger;

import com.fujitsu.tsc.desktop.util.Config;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = -1976452718671539689L;
	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");

	private JLabel aboutSw;
    private JLabel swVersion;
    private JLabel url;
    private JLabel copyright;
    private JSeparator separator1;
    private JButton closeButton;

    public AboutDialog(JFrame frame) {
    	super(frame);
    	initComponents();
    }
    
    private void initComponents() {
    	
        /* Overall JDialog settings */
    	this.getContentPane().setBackground(GuiConstants.COLOR_BG);
    	this.setPreferredSize(new Dimension(GuiConstants.ABOUT_WIDTH, GuiConstants.ABOUT_HEIGHT));
    	this.setResizable(false);

    	aboutSw = new JLabel("About " + Config.SOFTWARE_NAME); 
    	aboutSw.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE_TITLE));
    	aboutSw.setForeground(GuiConstants.FONT_COLOR_TITLE);
        swVersion = new JLabel("Version: " + Config.SOFTWARE_VERSION);
        swVersion.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE));
        swVersion.setForeground(GuiConstants.FONT_COLOR);

        /* Add hyperlink to the web site url. */
        url = new JLabel(GuiConstants.WEBSITE_URL);
		url.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE));
        url.setForeground(Color.BLUE);
		url.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported() && url.getText() != null) {
				    try {
						Desktop.getDesktop().browse(new URI(url.getText()));
					} catch (IOException ex) {
						logger.error(ex.getMessage());
					} catch (URISyntaxException ex) {
						logger.error(ex.getMessage());
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				url.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});

		separator1 = new JSeparator();
		
        copyright = new JLabel(GuiConstants.COPYRIGHT);
		copyright.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE));
        copyright.setForeground(GuiConstants.FONT_COLOR);
		
        /* Add close button. */
        closeButton = new JButton("Close");
		closeButton.setFont(new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        this.getContentPane().setLayout(groupLayout);
//        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.setHorizontalGroup(
        	groupLayout.createSequentialGroup()
        		.addGap(GuiConstants.ABOUT_GAP_LEFT, GuiConstants.ABOUT_GAP_LEFT, Short.MAX_VALUE)
        		.addGroup(groupLayout.createParallelGroup()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(aboutSw, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(swVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(url, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(copyright, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        				.addComponent(separator1)
        			.addGroup(groupLayout.createSequentialGroup()
        				.addGap(GuiConstants.ABOUT_GAP_LEFT, GuiConstants.ABOUT_GAP_LEFT, Short.MAX_VALUE)
        				.addComponent(closeButton)))
            	.addGap(GuiConstants.ABOUT_GAP_LEFT, GuiConstants.ABOUT_GAP_LEFT, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
            	.addGap(GuiConstants.ABOUT_GAP_TOP, GuiConstants.ABOUT_GAP_TOP, Short.MAX_VALUE)
           		.addComponent(aboutSw, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
           		.addGap(GuiConstants.ABOUT_GAP_VERTICAL)
           		.addComponent(swVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
           		.addGap(GuiConstants.ABOUT_GAP_VERTICAL)
                .addComponent(url, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
           		.addGap(GuiConstants.ABOUT_GAP_VERTICAL)
                .addComponent(copyright, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
           		.addGap(GuiConstants.ABOUT_GAP_VERTICAL)
           		.addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
           		.addGap(GuiConstants.ABOUT_GAP_VERTICAL)
           		.addComponent(closeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
               	.addGap(GuiConstants.ABOUT_GAP_BOTTOM, GuiConstants.ABOUT_GAP_BOTTOM, Short.MAX_VALUE)
        );
        
        pack();
    }
}
