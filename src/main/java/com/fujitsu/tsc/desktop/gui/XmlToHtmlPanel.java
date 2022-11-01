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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.xml.sax.SAXException;

import com.fujitsu.tsc.desktop.util.Config;

public class XmlToHtmlPanel extends JPanel implements ActionListener {
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
    private JLabel xslLocationL;
    public JTextField xslLocationTF;
    private JButton xslClearBT;
    private JButton xslBrowseBT;
    private JLabel outputLocationL;
    protected JTextField outputLocationTF;
    private JButton outputClearBT;
    private JButton outputBrowseBT;
    protected JFileChooser fileChooser1;
    private FileNameExtensionFilter filter1;
    protected JFileChooser fileChooser2;
    private FileNameExtensionFilter filter2;
    protected JFileChooser fileChooser3;

    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;
	
    public XmlToHtmlPanel(GuiMain parent, Config config) {
    	this.parent = parent;
        
        titleFont = new Font(GuiConstants.FONT_NAME_TITLE, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE_TITLE);
        defaultFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE);
        //smallFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE, GuiConstants.FONT_SIZE_SMALL);
        submitFont = new Font(GuiConstants.FONT_NAME, GuiConstants.FONT_STYLE_TITLE, GuiConstants.FONT_SIZE);
        this.setBackground(GuiConstants.COLOR_BORDER);
        
        initHeaderPanel();
        initBodyPanel(config);
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
        titleL = new JLabel("Convert from XML to HTML");
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
    
    private void initBodyPanel(Config config) {
    	bodyPanel = new JPanel();
    	bodyPanel.setBackground(GuiConstants.COLOR_BG);

        xmlLocationL = new JLabel("XML File Location (.xml):");
        xmlLocationL.setFont(defaultFont);
        xmlLocationTF = new JTextField("");
        //xmlLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        xmlLocationTF.setEditable(false);
        xslLocationL = new JLabel("Style Sheet Location (.xsl):");
        xslLocationL.setFont(defaultFont);
        xslLocationTF = new JTextField("");
        //xmlLocationTF.setFont(defaultFont);	//Non-english characters can be included in the path.
        xslLocationTF.setEditable(false);
        outputLocationL = new JLabel("Output Location:");
        outputLocationL.setFont(defaultFont);
        outputLocationTF = new JTextField("");

        fileChooser1 = new JFileChooser();
        filter1 = new FileNameExtensionFilter("XML(.xml)", "xml");
        fileChooser1.setFileFilter(filter1);
        xmlLocationTF.setTransferHandler(new FilePathTransferHandler(xmlLocationTF, fileChooser1, filter1));	//Add DnD support
        fileChooser2 = new JFileChooser();
        filter2 = new FileNameExtensionFilter("Style Sheet(.xsl)", "xsl");
        fileChooser2.setFileFilter(filter2);
        fileChooser2.setDialogTitle("Schema");
        xslLocationTF.setTransferHandler(new FilePathTransferHandler(xslLocationTF, fileChooser2, filter2));	//Add DnD support
        fileChooser3 = new JFileChooser();
        fileChooser3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputLocationTF.setTransferHandler(new FilePathTransferHandler(outputLocationTF, fileChooser3));	//Add DnD support

        xmlBrowseBT = new JButton("Browse");
        xmlBrowseBT.setFont(defaultFont);
        xmlBrowseBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(XmlToHtmlPanel.this);
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

        xslBrowseBT = new JButton("Browse");
        xslBrowseBT.setFont(defaultFont);
        xslBrowseBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int selected = fileChooser2.showOpenDialog(XmlToHtmlPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	xslLocationTF.setText(fileChooser2.getSelectedFile().getPath ());
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

        xslClearBT = new JButton("Clear");
        xslClearBT.setFont(defaultFont);
        xslClearBT.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			xslLocationTF.setText("");
    		}
    	});

        outputBrowseBT = new JButton("Browse");
        outputBrowseBT.setFont(defaultFont);
        outputBrowseBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser3.showOpenDialog(XmlToHtmlPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser3.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.DEFINE_HTML_FILE_NAME);
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });
    	
        outputClearBT = new JButton("Clear");
        outputClearBT.setFont(defaultFont);
    	outputClearBT.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			outputLocationTF.setText("");
    		}
    	});


        /* Populate values from config */
        xmlLocationTF.setText(config.x2hXmlLocation);
        xslLocationTF.setText(config.x2hXslLocation);
        outputLocationTF.setText(config.x2hOutputLocation);

//        this.setBorder(BorderFactory.createEtchedBorder());
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_LEFT, GuiConstants.CONFIG_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(xmlLocationL)
                    .addComponent(xslLocationL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(xmlLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(xslLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(outputLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                        .addComponent(xmlBrowseBT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xmlClearBT))
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                        .addComponent(xslBrowseBT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xslClearBT))
                	.addGroup(bodyPanelLayout.createSequentialGroup()
                        .addComponent(outputBrowseBT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputClearBT)))
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
                    .addComponent(xslLocationL)
                    .addComponent(xslLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(xslBrowseBT)
                    .addComponent(xslClearBT))
                .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(outputLocationL)
                    .addComponent(outputLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputBrowseBT)
                    .addComponent(outputClearBT))
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
    	if (StringUtils.isEmpty(xmlLocationTF.getText())) {
    		return "XML File Location cannot be blank.";
    	} else if (StringUtils.isEmpty(xslLocationTF.getText())) {
    		return "Style Sheet Location cannot be blank.";
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
	       		parent.showPanel(parent.xmlToHtmlResultPanel);
				parent.xmlToHtmlResultPanel.clearBodyPanel();
				Runnable runValidation = new Runnable() {
					public void run() {
						EditorPaneAppender epAppender = new EditorPaneAppender();
				        epAppender.setEditorPane(parent.xmlToHtmlResultPanel.gResultEditorPane);
				        epAppender.setLayout(new PatternLayout("%-5p %c{2} - %m%n"));
				        logger.addAppender(epAppender);
						try {
							logger.info("Converting XML to HTML...");
							TransformerFactory factory = TransformerFactory.newInstance();
							Transformer transformer = factory.newTransformer(new StreamSource(xslLocationTF.getText()));
							transformer.transform(new StreamSource(xmlLocationTF.getText()), new StreamResult(outputLocationTF.getText()));
							/* Display the output folder on the xmlToHtmlResultPanel. */
							logger.info("An HTML file has been created.");
							parent.xmlToHtmlResultPanel.outputLocationUrl.setText(
									new File(outputLocationTF.getText()).getCanonicalPath());
						} catch (Exception ex) {
							logger.error(ExceptionUtils.getStackTrace(ex));
						} finally {
							logger.removeAppender(epAppender);
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
