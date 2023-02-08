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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorLog;
import com.fujitsu.tsc.desktop.util.InvalidParameterException;
import com.fujitsu.tsc.desktop.util.ErrorLog.ErrorLevel;
import com.fujitsu.tsc.desktop.exporter.XmlGenerator;
import com.fujitsu.tsc.desktop.exporter.model.XmlDocument;
import com.fujitsu.tsc.desktop.importer.SdtmAdamSpecImporter;
import com.fujitsu.tsc.desktop.importer.models.DefineModel;
import com.fujitsu.tsc.desktop.exporter.DefineXmlWriter2;
import com.fujitsu.tsc.desktop.exporter.InvalidOidSyntaxException;
import com.fujitsu.tsc.desktop.exporter.RequiredValueMissingException;
import com.fujitsu.tsc.desktop.exporter.TableNotFoundException;

public class DefineExportPanel extends JPanel implements ActionListener {
    
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
    private JLabel defineVersionL;
    protected JComboBox<String> defineVersionCB;
    private JLabel datasetTypeL;
    protected JComboBox<String> datasetTypeCB;
    protected JCheckBox includeResultMetadataCB;
    private JLabel xmlEncodingL;
    protected JComboBox<String> xmlEncodingCB;
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

    public DefineExportPanel(GuiMain parent, Config config){
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
        titleL = new JLabel("Convert from Excel to Define-XML");
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

        defineVersionL = new JLabel("Define-XML Version:");
        defineVersionCB = new JComboBox<String>(Config.DEFINE_VERSIONS);
        datasetTypeL = new JLabel("Dataset Type:");
        datasetTypeCB = new JComboBox<String>(Config.DatasetType.stringValues());
        includeResultMetadataCB = new JCheckBox("Include Result Metadata");
        includeResultMetadataCB.setOpaque(false);
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
        
        datasetTypeCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String strType = (String)((JComboBox<String>)e.getSource()).getSelectedItem();
            	if (GuiConstants.getDatasetTypeValue(strType) == Config.DatasetType.ADaM) {
            		includeResultMetadataCB.setVisible(true);
            	} else {
            		includeResultMetadataCB.setVisible(false);
            	}
            }
        });
        
    	browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(DefineExportPanel.this);
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
                int selected = fileChooser2.showOpenDialog(DefineExportPanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser2.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.DEFINE_FILE_NAME);
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

    	defineVersionL.setFont(defaultFont);
    	defineVersionCB.setFont(defaultFont);
        datasetTypeL.setFont(defaultFont);
        datasetTypeCB.setFont(defaultFont);
        includeResultMetadataCB.setFont(defaultFont);
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
        defineVersionCB.setSelectedItem(config.e2dDefineVersion);
        datasetTypeCB.setSelectedItem(config.e2dDatasetType.name());
        if (config.e2dIncludeResultMetadata)
        	includeResultMetadataCB.setSelected(true);
        xmlEncodingCB.setSelectedItem(config.e2dXmlEncoding);
        stylesheetLocationTF.setText(config.e2dStylesheetLocation);
        dataSourceLocationTF.setText(config.e2dDataSourceLocation);
        outputLocationTF.setText(config.e2dOutputLocation);
        
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        	bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.BODY_GAP_LEFT, GuiConstants.BODY_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(defineVersionL)
                    .addComponent(datasetTypeL)
                    .addComponent(xmlEncodingL)
                    .addComponent(stylesheetLocationL)
                    .addComponent(dataSourceLocationL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(defineVersionCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                    	.addComponent(datasetTypeCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    	.addComponent(includeResultMetadataCB))
                    .addComponent(xmlEncodingCB, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addComponent(stylesheetLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(dataSourceLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(outputLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(browseButton1)
               		.addComponent(browseButton2))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
               		.addComponent(clearButton1)
               		.addComponent(clearButton2))
                .addContainerGap(GuiConstants.BODY_GAP_RIGHT, GuiConstants.BODY_GAP_RIGHT)
        );
        bodyPanelLayout.setVerticalGroup(
        	bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.BODY_GAP_TOP, GuiConstants.BODY_GAP_TOP)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(defineVersionL)
                    .addComponent(defineVersionCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(datasetTypeL)
                    .addComponent(datasetTypeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(includeResultMetadataCB))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xmlEncodingL)
                    .addComponent(xmlEncodingCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(stylesheetLocationL)
                    .addComponent(stylesheetLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(dataSourceLocationL)
                    .addComponent(dataSourceLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton1)
                    .addComponent(clearButton1))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(outputLocationL)
                    .addComponent(outputLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton2)
                    .addComponent(clearButton2))
                .addContainerGap(GuiConstants.BODY_GAP_BOTTOM, GuiConstants.BODY_GAP_BOTTOM)
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
    	if (defineVersionCB.getSelectedItem() == null) {
    		return "Define-XML Version cannot be blank.";
    	} else if (datasetTypeCB.getSelectedItem() == null) {
    		return "Dataset Type cannot be blank.";
    	} else if (xmlEncodingCB.getSelectedItem() == null) {
    		return "XML Encoding cannot be blank.";
    	} else if (StringUtils.isEmpty(stylesheetLocationTF.getText())) {
    		return "Stylesheet Location cannot be blank.";
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
	       		parent.showPanel(parent.defineExportResultPanel);
				parent.defineExportResultPanel.clearBodyPanel();
				Runnable exportDefine = new Runnable() {
					public void run() {
				        EditorPaneAppender epAppender = new EditorPaneAppender();
				        epAppender.setEditorPane(parent.defineExportResultPanel.gResultEditorPane);
				        epAppender.setLayout(new PatternLayout("%-5p %c{2} - %m%n"));
				        logger.addAppender(epAppender);
						/* Configure Define-XML Generator */
						config.e2dDefineVersion = defineVersionCB.getSelectedItem().toString();
						config.e2dDatasetType = Config.DatasetType.valueOf(datasetTypeCB.getSelectedItem().toString());
						config.e2dIncludeResultMetadata = includeResultMetadataCB.isSelected();
						config.e2dXmlEncoding = xmlEncodingCB.getSelectedItem().toString();
						config.e2dStylesheetLocation = stylesheetLocationTF.getText();
						config.e2dDataSourceLocation = dataSourceLocationTF.getText();
						config.e2dOutputLocation = outputLocationTF.getText();
						Workbook workbook = null;
						logger.info("Opening the source Excel file...");
						try {
							File sourceFile = new File(config.e2dDataSourceLocation);
							workbook = new XSSFWorkbook(sourceFile);
							logger.info("Loading...");
							SdtmAdamSpecImporter importer = new SdtmAdamSpecImporter(config, workbook);
							List<ErrorLog> error_logs = importer.parse();
							if (error_logs.isEmpty()) {
								logger.info("Loading completed.");
							} else {
								List<ErrorLog> error_log_errors = error_logs.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).collect(Collectors.toList());
								if (error_log_errors.isEmpty()) {
									logger.warn("The following warning(s) found during loading:");
									for (ErrorLog error_log : error_logs) {
										logger.warn(error_log.print());
									}
									logger.info("Continue processing...");
								} else {
									logger.error("The following error(s) found during loading:");
									for (ErrorLog error_log_error : error_log_errors) {
										logger.error(error_log_error.print());
									}
									logger.error("Processing suspended.");
									workbook.close();
									return;
								}
							}
							if ("2.0.0".equals(config.e2dDefineVersion)) {
								logger.info("Writing Define-XML 2.0 to the target file...");
							} else {
								logger.info("Writing Define-XML 2.1 to the target file...");
							}
							DefineXmlWriter2 writer;
							writer = new DefineXmlWriter2(config);
							DefineModel define = importer.getDefineModel();
							XmlDocument xml_document = writer.bind(define);
							writer.writeout(xml_document);
							logger.info("Define-XML has been successfully created.");
							parent.defineExportResultPanel.outputLocationUrl.setText(
									new File(outputLocationTF.getText()).getCanonicalPath());
							logger.removeAppender(epAppender);
							workbook.close();
						} catch (Exception ex) {
							logger.error(ExceptionUtils.getStackTrace(ex));
							logger.removeAppender(epAppender);
						}
				    }
				};
				new Thread(exportDefine).start();
	       	} else {
	   			String[] options = {"OK"};
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
	        }
        }
	}
}
