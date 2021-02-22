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
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.ErrorInfo;
import com.opencsv.exceptions.CsvException;
//import com.fujitsu.tsc.desktop.importer.ArchitectImporter;
import com.fujitsu.tsc.desktop.importer.CrfSpecCreator;
import com.fujitsu.tsc.desktop.importer.ExcelWriter2;
import com.fujitsu.tsc.desktop.importer.models.EdcKeysModel;
import com.fujitsu.tsc.desktop.importer.models.OdmCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmEventModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFieldModel;
import com.fujitsu.tsc.desktop.importer.models.OdmFormModel;
import com.fujitsu.tsc.desktop.importer.models.OdmModel;
import com.fujitsu.tsc.desktop.importer.models.OdmStudyModel;
import com.fujitsu.tsc.desktop.importer.models.OdmUnitModel;

public class CrfSpecCreatePanel extends JPanel implements ActionListener {

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
    private JLabel architectLocationL;
    protected JTextField architectLocationTF;
    private JScrollPane sourceFilesScrollPane;
    private JLabel sourceFilesL;
    protected JList<String> sourceFilesLI;
    private JLabel headerCntL;
    protected JTextField headerCntTF;
    private JLabel encodingL;
    protected JComboBox<String> encodingCB;
    private JLabel delimiterL;
    protected JTextField delimiterTF;
    private JLabel textQualifierL;
    protected JComboBox<String> textQualifierCB;
    private JLabel outputLocationL;
    protected JTextField outputLocationTF;
    private JButton browseButton1;
    private JButton clearButton1;
    private JButton browseButton2;
    private JButton clearButton2;
    private JButton browseButton3;
    private JButton clearButton3;
    protected JFileChooser fileChooser1;
    private FileNameExtensionFilter filter1;
    protected JFileChooser fileChooser2;
    protected JFileChooser fileChooser3;
    
    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;
	
    public CrfSpecCreatePanel(GuiMain parent, Config config) {
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
        titleL = new JLabel("Create CRF Spec from Datasets");
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

        architectLocationL = new JLabel("Architect CRF Location (.xlsx):");
        architectLocationL.setEnabled(false);
        architectLocationTF = new JTextField("");
        architectLocationTF.setEditable(false);
        sourceFilesL = new JLabel("Datasets Text Files:");
        sourceFilesLI = new JList<String>();
        sourceFilesLI.setVisibleRowCount(GuiConstants.VISIBLE_ROWS);
        sourceFilesScrollPane = new JScrollPane(sourceFilesLI);
        headerCntL = new JLabel("# of Header Lines:");
        headerCntTF = new JTextField("");
        encodingL = new JLabel("Character Encoding:");
        encodingCB = new JComboBox<String>(Config.ENCODING);
        delimiterL = new JLabel("Delimiter:");
        delimiterTF = new JTextField("");
        textQualifierL = new JLabel("Text Qualifier:");
        textQualifierCB = new JComboBox<String>(Config.TEXT_QUALIFIER);

        outputLocationL = new JLabel("Output Location:");
        outputLocationTF = new JTextField("");
        browseButton1 = new JButton("Browse");
        browseButton1.setEnabled(false);
        clearButton1 = new JButton("Clear");
        clearButton1.setEnabled(false);
        browseButton2 = new JButton("Browse");
        clearButton2 = new JButton("Clear");
        browseButton3 = new JButton("Browse");
        clearButton3 = new JButton("Clear");

    	fileChooser1 = new JFileChooser();
        filter1 = new FileNameExtensionFilter("Excel Book (.xlsx)", "xlsx");
        fileChooser1.setFileFilter(filter1);
        architectLocationTF.setTransferHandler(new FilePathTransferHandler(architectLocationTF, fileChooser1, filter1));	//Add DnD support
    	fileChooser2 = new JFileChooser();
    	fileChooser2.setMultiSelectionEnabled(true);
    	sourceFilesLI.setTransferHandler(new FilePathTransferHandler(sourceFilesLI, fileChooser2));	//Add DnD support
        fileChooser3 = new JFileChooser();
        fileChooser3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputLocationTF.setTransferHandler(new FilePathTransferHandler(outputLocationTF, fileChooser3));	//Add DnD support

    	browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(CrfSpecCreatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	architectLocationTF.setText(fileChooser1.getSelectedFile().getPath());
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			architectLocationTF.setText("");
    		}
    	});

    	browseButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser2.showOpenDialog(CrfSpecCreatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
					Vector<String> paths = new Vector<String>();
					for (File f : fileChooser2.getSelectedFiles()) {
						paths.add(f.getAbsolutePath());
					}
					sourceFilesLI.setListData(paths);
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			sourceFilesLI.setModel(new DefaultListModel<String>());
    		}
    	});

    	browseButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser3.showOpenDialog(CrfSpecCreatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser3.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.CRF_EXCEL_FILE_NAME);
                } else if (selected == JFileChooser.CANCEL_OPTION){
                	//Do nothing - simply ignore the operation.
                } else if (selected == JFileChooser.ERROR_OPTION){
                	//Do nothing - simply ignore the operation.
                }
            }
        });

    	clearButton3.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			outputLocationTF.setText("");
    		}
    	});
    	
        architectLocationL.setFont(defaultFont);
        sourceFilesL.setFont(defaultFont);
        headerCntL.setFont(defaultFont);
        headerCntTF.setFont(defaultFont);
        encodingL.setFont(defaultFont);
        encodingCB.setFont(defaultFont);
        delimiterL.setFont(defaultFont);
        delimiterTF.setFont(defaultFont);
        textQualifierL.setFont(defaultFont);
        textQualifierCB.setFont(defaultFont);
        outputLocationL.setFont(defaultFont);
        browseButton1.setFont(defaultFont);
        clearButton1.setFont(defaultFont);
        browseButton2.setFont(defaultFont);
        clearButton2.setFont(defaultFont);
        browseButton3.setFont(defaultFont);
        clearButton3.setFont(defaultFont);

        /* Populate values from config */
//        architectLocationTF.setText(config.crfArchitectLocation);
        if (!ArrayUtils.isEmpty(config.crfSourceFiles))
        	sourceFilesLI.setListData(config.crfSourceFiles);
        headerCntTF.setText(config.crfHeaderCnt);
        encodingCB.setSelectedItem(config.crfEncoding);
        delimiterTF.setText(config.crfDelimiter);
        textQualifierCB.setSelectedItem(config.crfTextQualifier);
        outputLocationTF.setText(config.crfOutputLocation);
        
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_LEFT, GuiConstants.CONFIG_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(architectLocationL)
                    .addComponent(sourceFilesL)
                    .addComponent(headerCntL)
                    .addComponent(encodingL)
                    .addComponent(delimiterL)
                    .addComponent(textQualifierL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(architectLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(sourceFilesScrollPane, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addComponent(headerCntTF, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(encodingCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimiterTF, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(textQualifierCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputLocationTF, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                .addGap(GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL, GuiConstants.CONFIG_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(browseButton1)
                		.addComponent(browseButton2)
                		.addComponent(browseButton3))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                		.addComponent(clearButton1)
                		.addComponent(clearButton2)
                		.addComponent(clearButton3))
                .addContainerGap(GuiConstants.CONFIG_GAP_RIGHT, GuiConstants.CONFIG_GAP_RIGHT))
        );
        bodyPanelLayout.setVerticalGroup(
        		bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.CONFIG_GAP_TOP, GuiConstants.CONFIG_GAP_TOP)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(architectLocationL)
                        .addComponent(architectLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                		.addComponent(browseButton1)
                		.addComponent(clearButton1))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(sourceFilesL)
                        .addComponent(sourceFilesScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                		.addComponent(browseButton2)
                		.addComponent(clearButton2))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(headerCntL)
                        .addComponent(headerCntTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(encodingL)
                        .addComponent(encodingCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(delimiterL)
                        .addComponent(delimiterTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(textQualifierL)
                        .addComponent(textQualifierCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                    .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(outputLocationL)
                        .addComponent(outputLocationTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton3)
                        .addComponent(clearButton3))
                .addContainerGap(GuiConstants.CONFIG_GAP_BOTTOM, GuiConstants.CONFIG_GAP_BOTTOM))
        );
    }
    
    private void initFooterPanel() {
    	footerPanel = new JPanel();
    	footerPanel.setBackground(GuiConstants.COLOR_BG);
    	runButton = new JButton("Create");
    	runButton.setFont(submitFont);
    	runButton.setForeground(GuiConstants.COLOR_BTN_FG_SUBMIT);
    	runButton.setBackground(GuiConstants.COLOR_BTN_BG_SUBMIT);
    	runButton.setActionCommand("Create");
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
    	if (StringUtils.isEmpty(architectLocationTF.getText())) {
    		if (sourceFilesLI.getModel() == null || sourceFilesLI.getModel().getSize() < 1) {
    			return "Either \"Architect CRF Location (.xlsx)\" or \"Datasets Text Files\" must be entered.";
    		}
    	}
    	if (sourceFilesLI.getModel() != null && sourceFilesLI.getModel().getSize() > 0) {
    		if (StringUtils.isEmpty(headerCntTF.getText()) || NumberUtils.toInt(headerCntTF.getText(), 0) < 1) {
    			return "\"# of Header Lines\" cannot be blank and must be a positive integer when \"Datasets Text Files\" is entered.";
        	} else if (NumberUtils.toInt(headerCntTF.getText(), -1) < 0) {
        		return "# of Header Lines must be 0 or a positive number.";
    		} else if (encodingCB.getModel() == null) {
    			return "\"Character Encoding\" cannot be blank when \"Datasets Text Files\" is entered.";
    		} else if (StringUtils.isEmpty(delimiterTF.getText())) {
    			return "\"Delimiter\" cannot be blank when \"Datasets Text Files\" is entered.";
        	} else if ( !(StringUtils.length(delimiterTF.getText()) == 1 || StringUtils.equals(delimiterTF.getText(), "\\t")) ) {
        		return "Delimiter must be a single character or a tab (\\t).";
    		} else if (textQualifierCB.getModel() == null ) {
    			return "\"Text Qualifier\" cannot be blank when \"Datasets Text Files\" is entered.";
    		}
    	}
    	if (StringUtils.isEmpty(outputLocationTF.getText())) {
    		return "Output Location cannot be blank.";
    	}
   		return "";
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Create".equals(command)) {
        	String errMessage = this.validateEntry();
	       	if (StringUtils.isEmpty(errMessage)) {
	       		parent.showPanel(parent.crfSpecCreateResultPanel);
	       		parent.crfSpecCreateResultPanel.clearBodyPanel();
				Runnable createCrfSpec = new Runnable() {
					public void run() {
				        EditorPaneAppender epAppender = new EditorPaneAppender();
				        epAppender.setEditorPane(parent.crfSpecCreateResultPanel.gResultEditorPane);
				        epAppender.setLayout(new PatternLayout("%-5p %c{2} - %m%n"));
				        logger.addAppender(epAppender);
						OdmModel crf = null;
						File outFile = new File(outputLocationTF.getText());
						try {
							if (sourceFilesLI.getModel() != null && sourceFilesLI.getModel().getSize() > 0) {
								OdmStudyModel params = new OdmStudyModel();
								params.edc_dataset_type = "Text";
								params.header_line = NumberUtils.toInt(headerCntTF.getText());
								params.encoding = encodingCB.getSelectedItem().toString();
								params.delimiter = delimiterTF.getText();
								params.text_qualifier = textQualifierCB.getSelectedItem().toString();
								ListModel<String> list = sourceFilesLI.getModel();
								List<File> srcFiles = new Vector<File>();
								for (int i = 0; i < list.getSize(); i++) {
									srcFiles.add(new File(list.getElementAt(i)));
								}
								CrfSpecCreator creator = new CrfSpecCreator(config, params, srcFiles.toArray(new File[srcFiles.size()]));
								logger.info("Creating CRF Spec from Datasets...");
								crf = creator.create();
								logger.info("Created CRF Spec from Datasets successfully.");
							}
//							if (StringUtils.isNotEmpty(architectLocationTF.getText())) {
//								logger.info("Importing Architect CRF ...");
//								config.crfArchitectLocation = architectLocationTF.getText();
//								ArchitectImporter architectImporter = null;
//								if (crf != null) {
//									architectImporter = new ArchitectImporter(config, crf);
//								} else {
//									architectImporter = new ArchitectImporter(config);
//								}
//								List<ErrorInfo> errors = architectImporter.parse();
//								if (!errors.isEmpty()) {
//									for (ErrorInfo error : errors) {
//										logger.error(error);
//									}
//									throw new IllegalArchitectFormat();
//								} else {
//									logger.info("Architect CRF has been loaded successfully.");
//								}
//								logger.info("Converting Architect to CRF Spec...");
//								crf = architectImporter.bindArchitectToOdm();
//								logger.info("Converted Architect to CRF successfully.");
//							}
							if (crf == null) {
								throw new IllegalGuiParameterException();
							}
							
							/* Finally */
							crf.updateEdcKeys(config.valueDelimiter);
							crf.updateFieldFormName();
							crf.updateFieldId(config.valueDelimiter);
							crf.updateFieldDerivedFrom();
							
							logger.info("Writing to Excel...");
							ExcelWriter2 excelWriter = new ExcelWriter2();
							logger.info("Writing STUDY Sheet...");
							excelWriter.addData("STUDY", crf.getStudy(), OdmStudyModel.class);
							logger.info("Writing UNIT Sheet...");
							excelWriter.addData("UNIT", crf.listUnit(), OdmUnitModel.class);
							logger.info("Writing EVENT Sheet...");
							excelWriter.addData("EVENT", crf.listEvent(), OdmEventModel.class);
							logger.info("Writing EVENTxFORM Sheet...");
							excelWriter.addData("EVENTxFORM", crf.listEventForm(), OdmEventFormModel.class);
							logger.info("Writing FORM Sheet...");
							excelWriter.addData("FORM", crf.listForm(), OdmFormModel.class);
							logger.info("Writing FIELD Sheet...");
							excelWriter.addData("FIELD", crf.listField(), OdmFieldModel.class);
							logger.info("Writing CODELIST Sheet. This could take a few minutes...");
							excelWriter.addData("CODELIST", crf.listCodelist(), OdmCodelistModel.class);
							logger.info("Writing EDC_KEYS Sheet...");
							excelWriter.addData("EDC_KEYS", crf.getEdcKeys(), EdcKeysModel.class);
							excelWriter.writeout(outFile);
							logger.info("Done.");
							parent.crfSpecCreateResultPanel.outputLocationUrl.setText(
									new File(outputLocationTF.getText()).getCanonicalPath());
						} catch (IOException | IllegalArgumentException | IllegalAccessException
								| IllegalGuiParameterException | CsvException ex) {
//								| IllegalGuiParameterException | IllegalArchitectFormat | InvalidFormatException | CsvException ex) {
							logger.error(ex.getMessage());
						} finally {
							logger.removeAppender(epAppender);
						}
				    }
				};
				new Thread(createCrfSpec).start();
	       	} else {
	   			String[] options = {"OK"};
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
	        }
        }
	}
	
	private class IllegalGuiParameterException extends Exception {
		private static final long serialVersionUID = 1L;

		public IllegalGuiParameterException() {
			super("Configuration settings on the GUI is illegal.");
		}
	}
	
	private class IllegalArchitectFormat extends Exception {
		private static final long serialVersionUID = 1L;

		public IllegalArchitectFormat() {
			super("Loading Architect CRF failed due to illegal format.");
		}
	}
}
