/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fujitsu.tsc.desktop.exporter.XmlGenerator;
import com.fujitsu.tsc.desktop.exporter.InvalidOidSyntaxException;
import com.fujitsu.tsc.desktop.exporter.RequiredValueMissingException;
import com.fujitsu.tsc.desktop.exporter.TableNotFoundException;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.InvalidParameterException;

public class OdmExportPanel extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
    private Config config;
    private GuiMain parent;	//Root window
    private Font titleFont;
    private Font defaultFont;
    //private Font smallFont;
    private Font submitFont;
    
    /* Header Panel*/
	private JPanel headerPanel;
    private JLabel titleL;
    
    /* Body Panel*/
	private JPanel bodyPanel;
    private JLabel odmVersionL;
    protected JComboBox odmVersionCB;
    private JLabel xmlEncodingL;
    protected JComboBox xmlEncodingCB;
    private JLabel stylesheetLocationL;
    protected JTextField stylesheetLocationTF;
    private JLabel dataSourceLocationL;
    protected JTextField dataSourceLocationTF;
    private JLabel outputLocationL;
    protected JTextField outputLocationTF;
    private JButton browseButton1;
    private JButton clearButton1;
    private JButton browseButton2;
    private JButton clearButton2;
    protected JFileChooser fileChooser1;
    private FileNameExtensionFilter filter1;
    protected JFileChooser fileChooser2;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;

    public OdmExportPanel(GuiMain parent, Config config){
    	this.config = config;
    	this.parent = parent;
        titleFont = new Font(GuiConstants.FONT_NAME_TITLE, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE_TITLE);
        defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE);
        //smallFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE_SMALL);
        submitFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE);
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
    
    private void initBodyPanel() {
    	bodyPanel = new JPanel();
    	bodyPanel.setBackground(GuiConstants.COLOR_BG);

        odmVersionL = new JLabel("ODM Version:");
        odmVersionCB = new JComboBox<String>(Config.ODM_VERSIONS);
        xmlEncodingL = new JLabel("XML Encoding:");
        xmlEncodingCB = new JComboBox<String>(Config.ENCODING);
        stylesheetLocationL = new JLabel("Stylesheet Location (.xsl):");
        stylesheetLocationTF = new JTextField("");
        dataSourceLocationL = new JLabel("Data Source Location:");
        dataSourceLocationTF = new JTextField("");
        outputLocationL = new JLabel("Output Location:");
        outputLocationTF = new JTextField("");
        browseButton1 = new JButton("Browse");
        clearButton1 = new JButton("Clear");
        browseButton2 = new JButton("Browse");
        clearButton2 = new JButton("Clear");
        
    	fileChooser1 = new JFileChooser();
        filter1 = new FileNameExtensionFilter("EXCEL (.xlsx)", "xlsx");
        fileChooser1.setFileFilter(filter1);
        dataSourceLocationTF.setTransferHandler(new FilePathTransferHandler(dataSourceLocationTF, fileChooser1, filter1));	//Add DnD support
        fileChooser2 = new JFileChooser();
        fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputLocationTF.setTransferHandler(new FilePathTransferHandler(outputLocationTF, fileChooser2));	//Add DnD support
    	
    	browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(OdmExportPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	dataSourceLocationTF.setText(fileChooser1.getSelectedFile().getPath());
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			dataSourceLocationTF.setText("");
    		}
    	});

    	browseButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser2.showOpenDialog(OdmExportPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser2.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.ODM_FILE_NAME);
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });
    	
    	clearButton2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			outputLocationTF.setText("");
    		}
    	});

    	odmVersionL.setFont(defaultFont);
    	odmVersionCB.setFont(defaultFont);
        xmlEncodingL.setFont(defaultFont);
        xmlEncodingCB.setFont(defaultFont);
        stylesheetLocationL.setFont(defaultFont);
        //stylesheetLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        stylesheetLocationTF.setForeground(GuiConstants.FONT_COLOR_ON_WHITE);
        dataSourceLocationL.setFont(defaultFont);
        //dataSourceLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        dataSourceLocationTF.setEditable(false);
        outputLocationL.setFont(defaultFont);
        //outputLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        outputLocationTF.setForeground(GuiConstants.FONT_COLOR_ON_WHITE);
        browseButton1.setFont(defaultFont);
        clearButton1.setFont(defaultFont);
        browseButton2.setFont(defaultFont);
        clearButton2.setFont(defaultFont);
        
        /* Populate values from config */
        odmVersionCB.setSelectedItem(config.e2oOdmVersion);
        xmlEncodingCB.setSelectedItem(config.e2oXmlEncoding);
        stylesheetLocationTF.setText(config.e2oStylesheetLocation);
        dataSourceLocationTF.setText(config.e2oDataSourceLocation);
        outputLocationTF.setText(config.e2oOutputLocation);
        
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_LEFT, GuiConstants.CONFIG_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(odmVersionL)
                    .addComponent(xmlEncodingL)
                    .addComponent(stylesheetLocationL)
                    .addComponent(dataSourceLocationL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(odmVersionCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addComponent(xmlEncodingCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addComponent(stylesheetLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(dataSourceLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(outputLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(browseButton1)
                		.addComponent(browseButton2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(clearButton1)
                		.addComponent(clearButton2))
                .addContainerGap(GuiConstants.CONFIG_GAP_RIGHT, GuiConstants.CONFIG_GAP_RIGHT))
        );
        bodyPanelLayout.setVerticalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_TOP, GuiConstants.CONFIG_GAP_TOP)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(odmVersionL)
                        .addComponent(odmVersionCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(xmlEncodingL)
                        .addComponent(xmlEncodingCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(stylesheetLocationL)
                        .addComponent(stylesheetLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dataSourceLocationL)
                        .addComponent(dataSourceLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton1)
                        .addComponent(clearButton1))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(outputLocationL)
                        .addComponent(outputLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton2)
                        .addComponent(clearButton2))
                .addContainerGap(GuiConstants.CONFIG_GAP_BOTTOM, GuiConstants.CONFIG_GAP_BOTTOM))
        );
    }

    private void initFooterPanel() {
    	footerPanel = new JPanel();
    	footerPanel.setBackground(GuiConstants.COLOR_BG);
    	runButton = new JButton("Generate");
    	runButton.setFont(submitFont);
    	runButton.setForeground(GuiConstants.COLOR_BTN_FG_SUBMIT);
    	runButton.setBackground(GuiConstants.COLOR_BTN_BG_SUBMIT);
    	runButton.setActionCommand("Generate");
    	runButton.addActionListener(this);
        
        GroupLayout footerPanelLayout = new GroupLayout(footerPanel);
        footerPanelLayout.setAutoCreateContainerGaps(true);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
        	footerPanelLayout.createSequentialGroup()
	      		.addContainerGap(GuiConstants.FOOTER_GAP_LEFT, Short.MAX_VALUE)
	      		.addComponent(runButton)
        );
        footerPanelLayout.setVerticalGroup(
        	footerPanelLayout.createSequentialGroup()
      			.addComponent(runButton)
      			.addContainerGap(Short.MAX_VALUE, Short.MAX_VALUE)
        );
    }
    
    /**
     * Check entry of this panel
     * @return String An error message 
     */
    public String validateEntry() {
    	if (odmVersionCB.getSelectedItem() == null) {
    		return "ODM Version cannot be blank.";
    	} else if (xmlEncodingCB.getSelectedItem() == null) {
    		return "XML Encoding cannot be blank.";
    	} else if (StringUtils.isEmpty(dataSourceLocationTF.getText())) {
    		return "Data Source Location cannot be blank.";
    	} else if (StringUtils.isEmpty(outputLocationTF.getText())) {
    		return "Output Location cannot be blank.";
    	} else {
    		return "";
    	}
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Generate".equals(command)) {
        	String errMessage = this.validateEntry();
	       	if (StringUtils.isEmpty(errMessage)) {
	       		parent.showPanel(parent.odmExportResultPanel);
				parent.odmExportResultPanel.clearBodyPanel();
				Runnable exportOdm = new Runnable() {
					public void run() {
				        EditorPaneAppender epAppender = new EditorPaneAppender();
				        epAppender.setEditorPane(parent.odmExportResultPanel.gResultEditorPane);
				        epAppender.setLayout(new PatternLayout("%-5p %c{2} - %m%n"));
				        logger.addAppender(epAppender);
						try {
							/* Configure Define-XML Generator */
							config.e2oOdmVersion = odmVersionCB.getSelectedItem().toString();
							config.e2oXmlEncoding = xmlEncodingCB.getSelectedItem().toString();
							config.e2oStylesheetLocation = stylesheetLocationTF.getText();
							config.e2oDataSourceLocation = dataSourceLocationTF.getText();
							config.e2oOutputLocation = outputLocationTF.getText();
							
							XmlGenerator generator = new XmlGenerator(config, Config.RunMode.GUI);
							generator.generateOdmXml();
							parent.odmExportResultPanel.outputLocationUrl.setText(
									new File(outputLocationTF.getText()).getCanonicalPath());
						} catch (InvalidParameterException | TableNotFoundException | IOException
							| InvalidOidSyntaxException | RequiredValueMissingException | InvalidFormatException ex) {
							logger.error(ex.getMessage());
						} finally {
							logger.removeAppender(epAppender);
						}
				    }
				};
				new Thread(exportOdm).start();
	       	} else {
	   			String[] options = {"OK"};
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
	        }
        }
	}
}
