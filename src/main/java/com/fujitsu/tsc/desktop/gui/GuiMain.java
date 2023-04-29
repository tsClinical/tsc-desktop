/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fujitsu.tsc.desktop.util.Config;

public class GuiMain extends JFrame implements WindowListener {
	private static final long serialVersionUID = 5574971135909932382L;
	private static Logger logger;

	private float ratio;
	private Properties prop;
    private Config config;
    private Font defaultFont;

    /* Place holder of config/result panel and command panel - used to switch panels to display */
    private JPanel bodyPanel;
    private JPanel commandPanel;

    /* The selector pane contains radio buttons that switch the config screen by selecting. */
    private JPanel selectorPanel;

    /* The command panel contains buttons that execute a command by clicking. */
    
    private HashMap<String, JPanel[]> screen = new HashMap<String, JPanel[]>();
    protected DefineExportPanel defineExportPanel;
    protected DefineExportResultPanel defineExportResultPanel;
    protected DefineImportPanel defineImportPanel;
    protected DefineImportResultPanel defineImportResultPanel;
    protected XmlToHtmlPanel xmlToHtmlPanel;
    protected XmlToHtmlResultPanel xmlToHtmlResultPanel;
    protected OdmExportPanel odmExportPanel;
    protected OdmExportResultPanel odmExportResultPanel;
    protected OdmImportPanel odmImportPanel;
    protected OdmImportResultPanel odmImportResultPanel;
    protected CrfSpecCreatePanel crfSpecCreatePanel;
    protected CrfSpecCreateResultPanel crfSpecCreateResultPanel;
    protected EdtSpecCreatePanel edtSpecCreatePanel;
    protected EdtSpecCreateResultPanel edtSpecCreateResultPanel;
    protected XmlValidatePanel xmlValidatePanel;
    protected XmlValidateResultPanel xmlValidateResultPanel;
    protected AboutDialog aboutDialog;

    /**
     * Creates new form GeneratorGui
     */
    public GuiMain() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

    	/*
    	 * JFrame settings
    	 */
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);	//Closing will be handled by WindowAdapter
        this.addWindowListener(this);
        
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        DisplayMode mode = env.getDefaultScreenDevice().getDisplayMode();
        //ratio = mode.getWidth() / GuiConstants.FRAME_WIDTH;	//Indicates ratio of screen resolution compared to default (1024 x 768)
                
        this.setResizable(false);
        this.setPreferredSize(new Dimension(Float.valueOf(GuiConstants.FRAME_WIDTH).intValue(), Float.valueOf(GuiConstants.FRAME_HEIGHT).intValue()));
        //this.setPreferredSize(new Dimension(Float.valueOf(GuiConstants.FRAME_WIDTH * ratio).intValue(), Float.valueOf(GuiConstants.FRAME_HEIGHT * ratio).intValue()));
        //this.setLocationRelativeTo(null);	//Show the window in the center of the screen

        /*
         * Create a default font of GUI components - the scope should be limited to named components
         * in order to avoid adverse impact to other components (e.g. FileChooser)
         */
        defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, Float.valueOf(GuiConstants.FONT_SIZE * ratio).intValue());
        
        this.setJMenuBar(new DesktopMenuBar(this));

        ImageIcon icon = new ImageIcon(GuiConstants.INFINITY_IMAGE_PATH);
        this.setIconImage(icon.getImage());
        this.setTitle(Config.SOFTWARE_NAME + " " + Config.SOFTWARE_VERSION);

        /* Open the Generator/Validator configuration file and set it as default parameters. */
    	try {
			logger.info("loading property...");
    		prop = new Properties();
    		InputStreamReader reader = new InputStreamReader(new FileInputStream(Config.PROPERTIES_PATH), Config.DEFAULT_ENCODING);
    		prop.load(reader);
    		reader.close();
    	} catch (IllegalArgumentException ex) {
			logger.error(ex.getMessage());
    	} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}

		/* Create a GeneratorConfig object based on a properties file. */
		config = new Config(prop);
		
        /* Instantiate all panels and initialize with config */
        defineExportPanel = new DefineExportPanel(this, config);
        defineExportResultPanel = new DefineExportResultPanel(this);
        defineImportPanel = new DefineImportPanel(this, config);
        defineImportResultPanel = new DefineImportResultPanel(this);
        xmlToHtmlPanel = new XmlToHtmlPanel(this, config);
        xmlToHtmlResultPanel = new XmlToHtmlResultPanel(this);
        odmExportPanel = new OdmExportPanel(this, config);
        odmExportResultPanel = new OdmExportResultPanel(this);
        odmImportPanel = new OdmImportPanel(this, config);
        odmImportResultPanel = new OdmImportResultPanel(this);
        crfSpecCreatePanel = new CrfSpecCreatePanel(this, config);
        crfSpecCreateResultPanel = new CrfSpecCreateResultPanel(this);
        edtSpecCreatePanel = new EdtSpecCreatePanel(this, config);
        edtSpecCreateResultPanel = new EdtSpecCreateResultPanel(this);
        xmlValidatePanel = new XmlValidatePanel(this, config);
        xmlValidateResultPanel = new XmlValidateResultPanel(this);
        aboutDialog = new AboutDialog(this);

        /* Show the Define-XML Export screen by default. */
        this.showPanel(defineExportPanel);
    }

    public void showPanel(JPanel panel) {
        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        this.getContentPane().repaint();
        this.pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	logger = LogManager.getLogger();
		logger.info(Config.SOFTWARE_NAME + " " + Config.SOFTWARE_VERSION);
		logger.info(GuiConstants.COPYRIGHT);

		/*
    	 * Set the preferred look and feel
    	 */
    	try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//            	logger.info(info.getName());
            	if ("Windows".equals(info.getName())) {
//            	if ("Nimbus".equals(info.getName())) {
//            	if ("CDE/Motif".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (InstantiationException ex) {
            logger.error(ex.getMessage());
        } catch (IllegalAccessException ex) {
            logger.error(ex.getMessage());
        } catch (UnsupportedLookAndFeelException ex) {
            logger.error(ex.getMessage());
        }

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GuiMain().setVisible(true);
            }
        });
    }

	@Override
	public void windowClosing(WindowEvent e) {
		String[] options = {"Yes", "No"};
		switch (JOptionPane.showOptionDialog(GuiMain.this, "Are you sure to close the window?", "Select an option",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			case JOptionPane.YES_OPTION:
				/* Save parameters to main.properties before closing the window */
				logger.info("saving property...");
				try {
		    		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Config.PROPERTIES_PATH), Config.DEFAULT_ENCODING);

		    		// Generate Define.xml
		    		prop.setProperty("e2dDefineVersion", (String)defineExportPanel.defineVersionCB.getSelectedItem());
		    		prop.setProperty("e2dDatasetType", (String)defineExportPanel.datasetTypeCB.getSelectedItem());
		    		prop.setProperty("e2dIncludeResultMetadata", Boolean.toString(defineExportPanel.includeResultMetadataCB.isSelected()));
		    		prop.setProperty("e2dXmlEncoding", (String)defineExportPanel.xmlEncodingCB.getSelectedItem());
		    		prop.setProperty("e2dStylesheetLocation", defineExportPanel.stylesheetLocationTF.getText());
		    		prop.setProperty("e2dDataSourceLocation", defineExportPanel.dataSourceLocationTF.getText());
		    		prop.setProperty("e2dOutputLocation", defineExportPanel.outputLocationTF.getText());
		    		prop.setProperty("defineStudyTableName", config.defineStudyTableName);
		    		prop.setProperty("defineDocumentTableName", config.defineDocumentTableName);
		    		prop.setProperty("defineDatasetTableName", config.defineDatasetTableName);
		    		prop.setProperty("defineVariableTableName", config.defineVariableTableName);
		    		prop.setProperty("defineValueTableName", config.defineValueTableName);
		    		prop.setProperty("defineResult1TableName", config.defineResult1TableName);
		    		prop.setProperty("defineResult2TableName", config.defineResult2TableName);
		    		prop.setProperty("defineDictionaryTableName", config.defineDictionaryTableName);
		    		prop.setProperty("defineCodelistTableName", config.defineCodelistTableName);
		    		prop.setProperty("valueDelimiter", config.valueDelimiter);
		    		prop.setProperty("oidMode", config.oidMode.name());
		    		
		    		// Import Define-XML
		    		prop.setProperty("d2eDefineVersion", (String)defineImportPanel.defineVersionCB.getSelectedItem());
		    		prop.setProperty("d2eDatasetType", (String)defineImportPanel.datasetTypeCB.getSelectedItem());
		    		prop.setProperty("d2eSeparateSheet", Boolean.toString(defineImportPanel.separateSheetCB.isSelected()));
		    		prop.setProperty("d2eMergeNSVtoParent", Boolean.toString(defineImportPanel.mergeNSVtoParentCB.isSelected()));
		    		prop.setProperty("d2eDataSourceLocation", defineImportPanel.dataSourceLocationTF.getText());
		    		prop.setProperty("d2eOutputLocation", defineImportPanel.outputLocationTF.getText());

		    		// Convert from Define-XML to HTML
		    		prop.setProperty("x2hXmlLocation", xmlToHtmlPanel.xmlLocationTF.getText());
		    		prop.setProperty("x2hXslLocation", xmlToHtmlPanel.xslLocationTF.getText());
		    		prop.setProperty("x2hOutputLocation", xmlToHtmlPanel.outputLocationTF.getText());
		    		
		    		// Export ODM-XML
		    		prop.setProperty("o2eOdmVersion", (String)odmExportPanel.odmVersionCB.getSelectedItem());
		    		prop.setProperty("e2oXmlEncoding", (String)odmExportPanel.xmlEncodingCB.getSelectedItem());
		    		prop.setProperty("e2oStylesheetLocation", odmExportPanel.stylesheetLocationTF.getText());
		    		prop.setProperty("e2oDataSourceLocation", odmExportPanel.dataSourceLocationTF.getText());
		    		prop.setProperty("e2oOutputLocation", odmExportPanel.outputLocationTF.getText());
		    		prop.setProperty("odmStudyTableName", config.odmStudyTableName);
		    		prop.setProperty("odmUnitTableName", config.odmUnitTableName);
		    		prop.setProperty("odmEventTableName", config.odmEventTableName);
		    		prop.setProperty("odmEventFormTableName", config.odmEventFormTableName);
		    		prop.setProperty("odmFormTableName", config.odmFormTableName);
		    		prop.setProperty("odmFieldTableName", config.odmFieldTableName);
		    		prop.setProperty("odmCodelistTableName", config.odmCodelistTableName);
		    		prop.setProperty("odmMethodTableName", config.odmMethodTableName);
		    		prop.setProperty("odmConditionTableName", config.odmConditionTableName);
		    		
		    		// Import ODM-XML
		    		prop.setProperty("o2eOdmVersion", (String)odmImportPanel.odmVersionCB.getSelectedItem());
		    		prop.setProperty("o2eOdmLocation", odmImportPanel.dataSourceLocationTF.getText());
		    		prop.setProperty("o2eOutputLocation", odmImportPanel.outputLocationTF.getText());

		    		// Create CRF Spec from Datasets
		    		prop.setProperty("crfArchitectLocation", crfSpecCreatePanel.architectLocationTF.getText());
		    		prop.setProperty("crfSourceFiles", getSingleString(crfSpecCreatePanel.sourceFilesLI.getModel()));
		    		prop.setProperty("crfHeaderCnt", crfSpecCreatePanel.headerCntTF.getText());
		    		prop.setProperty("crfHeaderRow", crfSpecCreatePanel.headerRowTF.getText());
		    		prop.setProperty("crfEncoding", (String)crfSpecCreatePanel.encodingCB.getSelectedItem());
		    		prop.setProperty("crfDelimiter", crfSpecCreatePanel.delimiterTF.getText());
		    		prop.setProperty("crfTextQualifier", (String)crfSpecCreatePanel.textQualifierCB.getSelectedItem());
		    		prop.setProperty("crfOutputLocation", crfSpecCreatePanel.outputLocationTF.getText());

		    		// Create eDT Spec
		    		prop.setProperty("edtType", (String)edtSpecCreatePanel.edtTypeCB.getSelectedItem());
		    		prop.setProperty("edtHeaderCnt", edtSpecCreatePanel.headerCntTF.getText());
		    		prop.setProperty("edtHeaderRow", edtSpecCreatePanel.headerRowTF.getText());
		    		prop.setProperty("edtEncoding", (String)edtSpecCreatePanel.encodingCB.getSelectedItem());
		    		prop.setProperty("edtDelimitedOrFixed", (String)edtSpecCreatePanel.delimitedOrFixedCB.getSelectedItem());
		    		prop.setProperty("edtDelimiter", edtSpecCreatePanel.delimiterTF.getText());
		    		prop.setProperty("edtTextQualifier", (String)edtSpecCreatePanel.textQualifierCB.getSelectedItem());
		    		prop.setProperty("edtDataSourceLocation", edtSpecCreatePanel.dataSourceLocationTF.getText());
		    		prop.setProperty("edtOutputLocation", edtSpecCreatePanel.outputLocationTF.getText());

		    		/* Parameters of Validate against XML Schema */
		    		prop.setProperty("validateXmlLocation", xmlValidatePanel.xmlLocationTF.getText());
		    		prop.setProperty("validateSchemaLocation", xmlValidatePanel.schemaLocationTF.getText());
		    		
		    		prop.store(writer, "This file is automatically updated when the GUI window is closed.");

		    		writer.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}

				logger.info("Terminated.");
				System.exit(0);
			case JOptionPane.NO_OPTION: break;
			default: break;
		}
	}

	private String getSingleString(ListModel<String> list) {
		String singleString = "";
		for (int i = 0; i < list.getSize(); i++) {
			if (i == 0) {
				singleString += list.getElementAt(i);
			} else {
				singleString += "|" + list.getElementAt(i);
			}
		}
		return singleString;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}
