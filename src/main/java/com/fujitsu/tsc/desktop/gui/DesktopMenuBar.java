/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

/**
 * A class for a menu bar of the tsc-desktop tool extending JMenuBar
 */
public class DesktopMenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 5574971135909932382L;
	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
	
	private GuiMain parent;	//Root window
	private JMenu fileMenu;
	private JMenuItem closeMI;	//Close the application window
	
	private JMenu defineMenu;
	private JMenuItem exportDefineMI;	//Convert from Excel to Define-XML
	private JMenuItem importDefineMI;	//Convert from Define-XML to Excel
	private JMenuItem xmlToHtmlMI;		//Convert from Define-XML to HTML
	
	private JMenu crfMenu;
	private JMenuItem exportOdmMI;	//Convert from Excel to ODM-XML
	private JMenuItem importOdmMI;	//Convert from ODM-XML to Excel
	private JMenuItem importCrfMI;	//Create CRF Spec based on Rave Architect and Datasets
	
	private JMenu edtMenu;
	private JMenuItem importEdtMI;	//Create eDT Spec based on Datasets
	
	//Validator menu
	private JMenu validationMenu;
	private JMenuItem validateXmlMI;	//Validate XML against XML Schema
	
	//Help menu
	private JMenu helpMenu;
	private JMenuItem aboutMI;	//Displays application version info
	
    /**
     * Creates a new menu bar for tsc-desktop
     * @param parent The GuiMain object
     */
	public DesktopMenuBar(GuiMain parent) {
    	this.parent = parent;
		initComponents();
	}

    private void initComponents() {
        
    	/*
    	 * Initialize menu labels and icons
    	 */
        this.setPreferredSize(new Dimension(GuiConstants.FRAME_WIDTH, GuiConstants.MENU_HEIGHT));
        this.setBackground(GuiConstants.COLOR_BG);
        this.setBorderPainted(false);
        
        fileMenu = createMenu("File", "custom_folder_open_black_48dp.png");
        closeMI = new JMenuItem("Close");
        
    	defineMenu = createMenu("Define-XML", "custom_integration_instructions_black_48dp.png");
    	exportDefineMI = new JMenuItem("Convert from Excel to Define-XML");
    	importDefineMI = new JMenuItem("Convert from Define-XML to Excel");
    	xmlToHtmlMI = new JMenuItem("Convert from XML to HTML");
    	
    	crfMenu = createMenu("CRF", "custom_ballot_black_48dp.png");
    	exportOdmMI = new JMenuItem("Convert from Excel to ODM-XML");
    	importOdmMI = new JMenuItem("Convert from ODM-XML to Excel");
    	importCrfMI = new JMenuItem("Create CRF Spec from Datasets");
    	
    	edtMenu = createMenu("eDT", "custom_table_view_black_48dp.png");
    	importEdtMI = new JMenuItem("Create eDT Spec from Datasets");
    	
    	validationMenu = createMenu("Validation", "custom_fact_check_black_48dp.png");
    	validateXmlMI = new JMenuItem("Validate XML against XML Schema");
        
        helpMenu = createMenu("Help", "custom_info_black_48dp.png");
        aboutMI = new JMenuItem("About tsClinical Desktop");

    	/*
    	 * Construct menu structure
    	 */
        this.add(Box.createRigidArea(new Dimension(10, 1)));

        this.add(fileMenu);
        fileMenu.add(closeMI);
        
        this.add(Box.createRigidArea(new Dimension(10, 1)));
        
        this.add(defineMenu);
        defineMenu.add(exportDefineMI);
        defineMenu.add(importDefineMI);
        defineMenu.add(xmlToHtmlMI);
        
        this.add(Box.createRigidArea(new Dimension(10, 1)));

        this.add(crfMenu);
        crfMenu.add(exportOdmMI);
        crfMenu.add(importOdmMI);
        crfMenu.add(importCrfMI);
        
        this.add(Box.createRigidArea(new Dimension(10, 1)));

        this.add(edtMenu);
        edtMenu.add(importEdtMI);
        
        this.add(Box.createRigidArea(new Dimension(10, 1)));

        this.add(validationMenu);
        validationMenu.add(validateXmlMI);
        
        this.add(Box.createRigidArea(new Dimension(10, 1)));

        this.add(helpMenu);
        helpMenu.add(aboutMI);
        
    	/*
    	 * Set Action Command
    	 */
        closeMI.setActionCommand("CLOSE");
        exportDefineMI.setActionCommand("EXPORT_DEFINE");
        importDefineMI.setActionCommand("IMPORT_DEFINE");
        xmlToHtmlMI.setActionCommand("XML_TO_HTML");
        exportOdmMI.setActionCommand("EXPORT_ODM");
        importOdmMI.setActionCommand("IMPORT_ODM");
        importCrfMI.setActionCommand("CREATE_CRF");
        importEdtMI.setActionCommand("CREATE_EDT");
        validateXmlMI.setActionCommand("VALIDATE_XML");
        aboutMI.setActionCommand("ABOUT");
        
    	/*
    	 * Add ActionListener
    	 */
        closeMI.addActionListener(this);
        exportDefineMI.addActionListener(this);
        importDefineMI.addActionListener(this);
        xmlToHtmlMI.addActionListener(this);
        exportOdmMI.addActionListener(this);
        importOdmMI.addActionListener(this);
        importCrfMI.addActionListener(this);
        importEdtMI.addActionListener(this);
        validateXmlMI.addActionListener(this);
        aboutMI.addActionListener(this);
    }
    
    /*
     * Create new JMenu with a label and an icon
     */
    private JMenu createMenu(String label, String icon_filename) {
        JMenu menu = new JMenu(label);
        menu.setIcon(new ImageIcon(GuiConstants.ICON_IMAGE_DIR + "/" + icon_filename));
        menu.setHorizontalTextPosition(JMenu.CENTER);
        menu.setVerticalTextPosition(JMenu.BOTTOM);
        return menu;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
       	logger.info(command);
        if ("EXPORT_DEFINE".equals(command)) {
           	parent.showPanel(parent.defineExportPanel);
        } else if ("IMPORT_DEFINE".equals(command)) {
           	parent.showPanel(parent.defineImportPanel);
        } else if ("XML_TO_HTML".equals(command)) {
        	parent.showPanel(parent.xmlToHtmlPanel);
        } else if ("EXPORT_ODM".equals(command)) {
        	parent.showPanel(parent.odmExportPanel);
        } else if ("IMPORT_ODM".equals(command)) {
        	parent.showPanel(parent.odmImportPanel);
        } else if ("CREATE_CRF".equals(command)) {
        	parent.showPanel(parent.crfSpecCreatePanel);
        } else if ("CREATE_EDT".equals(command)) {
           	parent.showPanel(parent.edtSpecCreatePanel);
        } else if ("VALIDATE_XML".equals(command)) {
           	parent.showPanel(parent.xmlValidatePanel);
        } else if ("ABOUT".equals(command)) {
        	parent.aboutDialog.setVisible(true);
        } else if ("CLOSE".equals(command)) {
            parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
        }
	}
}
