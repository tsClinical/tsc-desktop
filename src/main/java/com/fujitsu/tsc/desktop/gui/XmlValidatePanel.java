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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.fujitsu.tsc.desktop.util.Config;

public class XmlValidatePanel extends JPanel implements ActionListener {
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
    private JLabel xmlLocationL;
    public JTextField xmlLocationTF;
    private JButton xmlClearBT;
    private JButton xmlBrowseBT;
    private JLabel schemaLocationL;
    public JTextField schemaLocationTF;
    private JButton schemaClearBT;
    private JButton schemaBrowseBT;
    protected JFileChooser fileChooser1;
    private FileNameExtensionFilter filter1;
    protected JFileChooser fileChooser2;
    private FileNameExtensionFilter filter2;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;
	
    public XmlValidatePanel(GuiMain parent, Config config) {
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

        xmlLocationL = new JLabel("XML File Location (.xml):");
        xmlLocationL.setFont(defaultFont);
        xmlLocationTF = new JTextField("");
        //defineXmlLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        xmlLocationTF.setEditable(false);
        schemaLocationL = new JLabel("Schema Location (.xsd):");
        schemaLocationL.setFont(defaultFont);
        schemaLocationTF = new JTextField("");
        //schemaLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        schemaLocationTF.setEditable(false);

        fileChooser1 = new JFileChooser();
        filter1 = new FileNameExtensionFilter("XML(.xml)", "xml");
        fileChooser1.setFileFilter(filter1);
        xmlLocationTF.setTransferHandler(new FilePathTransferHandler(xmlLocationTF, fileChooser1, filter1));	//Add DnD support
        fileChooser2 = new JFileChooser();
        filter2 = new FileNameExtensionFilter("XML Schema Document(.xsd)", "xsd");
        fileChooser2.setFileFilter(filter2);
        fileChooser2.setDialogTitle("Schema");
        schemaLocationTF.setTransferHandler(new FilePathTransferHandler(schemaLocationTF, fileChooser2, filter2));	//Add DnD support

        xmlBrowseBT = new JButton("Browse");
        xmlBrowseBT.setFont(defaultFont);
        xmlBrowseBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(XmlValidatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                    xmlLocationTF.setText(fileChooser1.getSelectedFile().getPath ());
                }else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                }else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });
        
        xmlClearBT = new JButton("Clear");
        xmlClearBT.setFont(defaultFont);
        xmlClearBT.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			xmlLocationTF.setText("");
    		}
    	});

        schemaBrowseBT = new JButton("Browse");
        schemaBrowseBT.setFont(defaultFont);
        schemaBrowseBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int selected = fileChooser2.showOpenDialog(XmlValidatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                    schemaLocationTF.setText(fileChooser2.getSelectedFile().getPath ());
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

        schemaClearBT = new JButton("Clear");
        schemaClearBT.setFont(defaultFont);
        schemaClearBT.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			schemaLocationTF.setText("");
    		}
    	});

        /* Populate values from config */
        xmlLocationTF.setText(config.validateXmlLocation);
        schemaLocationTF.setText(config.validateSchemaLocation);

//        this.setBorder(BorderFactory.createEtchedBorder());
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_LEFT, GuiConstants.CONFIG_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(xmlLocationL)
                    .addComponent(schemaLocationL))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(xmlLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(schemaLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                        .addComponent(xmlBrowseBT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xmlClearBT))
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                        .addComponent(schemaBrowseBT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(schemaClearBT)))
                .addContainerGap())
        );
        bodyPanelLayout.setVerticalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_TOP, GuiConstants.CONFIG_GAP_TOP)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xmlLocationL)
                    .addComponent(xmlLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(xmlBrowseBT)
                    .addComponent(xmlClearBT))
                .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(schemaLocationL)
                    .addComponent(schemaLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(schemaBrowseBT)
                    .addComponent(schemaClearBT))
                .addContainerGap(GuiConstants.CONFIG_GAP_BOTTOM, GuiConstants.CONFIG_GAP_BOTTOM))
        );

    }
    
    private void initFooterPanel() {
    	footerPanel = new JPanel();
    	footerPanel.setBackground(GuiConstants.COLOR_BG);
    	runButton = new JButton("Validate");
    	runButton.setFont(submitFont);
    	runButton.setForeground(GuiConstants.COLOR_BTN_FG_SUBMIT);
    	runButton.setBackground(GuiConstants.COLOR_BTN_BG_SUBMIT);
    	runButton.setActionCommand("Validate");
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
    	if (StringUtils.isEmpty(xmlLocationTF.getText())) {
    		return "XML File Location cannot be blank.";
    	} else if (StringUtils.isEmpty(schemaLocationTF.getText())) {
    		return "Schema Location cannot be blank.";
    	} else {
    		return "";
    	}
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Validate".equals(command)) {
        	String errMessage = this.validateEntry();
	       	if (StringUtils.isEmpty(errMessage)) {
	       		parent.showPanel(parent.xmlValidateResultPanel);
				Runnable runValidation = new Runnable() {
					public void run() {
						XmlValidationAppender appender = new XmlValidationAppender(parent.xmlValidateResultPanel.vResultTable);
						appender.clear();
						String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
						SchemaFactory factory = SchemaFactory.newInstance(language);
						XmlValidationHandler handler = new XmlValidationHandler(appender);
						try {
							Schema schema = factory.newSchema(new File(schemaLocationTF.getText()));
							Validator validator = schema.newValidator();
							validator.setErrorHandler(handler);
							validator.validate(new StreamSource(new File(parent.xmlValidatePanel.xmlLocationTF.getText())));
				    		if (handler.getErrorCount() == 0) {
				    			appender.writeSuccessMessage();
				    		}
						} catch (SAXException | IOException ex) {
				    		logger.error(ex.getMessage());
		                    appender.writeErrorMessage(ex.getMessage());
						}
					}
				};
				new Thread(runValidation).start();
	       	} else {
	   			String[] options = {"OK"};
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
	        }
        }
	}
}
