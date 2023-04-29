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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.fujitsu.tsc.desktop.importer.EdtSpecCreator;
import com.fujitsu.tsc.desktop.importer.ExcelWriter2;
import com.fujitsu.tsc.desktop.importer.EdtSpecCreator.StudyEdtCodelist;
import com.fujitsu.tsc.desktop.importer.EdtSpecCreator.StudyEdtColumn;
import com.fujitsu.tsc.desktop.importer.EdtSpecCreator.StudyEdtGeneral;
import com.fujitsu.tsc.desktop.util.Config;
import com.opencsv.exceptions.CsvException;
import com.fujitsu.tsc.desktop.gui.EditorPaneAppender;

public class EdtSpecCreatePanel extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = 1L;
    private static Logger logger = LogManager.getLogger();
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
    private JLabel edtTypeL;
    protected JComboBox<String> edtTypeCB;
    private JLabel headerCntL;
    protected JTextField headerCntTF;
    private JLabel headerRowL;
    protected JTextField headerRowTF;
    private JLabel encodingL;
    protected JComboBox<String> encodingCB;
    private JLabel delimitedOrFixedL;
    protected JComboBox<String> delimitedOrFixedCB;
    private JLabel delimiterL;
    protected JTextField delimiterTF;
    private JLabel textQualifierL;
    protected JComboBox<String> textQualifierCB;
    private JLabel dataSourceLocationL;
    protected JTextField dataSourceLocationTF;
    private JLabel outputLocationL;
    protected JTextField outputLocationTF;
    private JButton browseButton1;
    private JButton clearButton1;
    private JButton browseButton2;
    private JButton clearButton2;
    protected JFileChooser fileChooser1;
    protected JFileChooser fileChooser2;
    
    /* Footer Panel*/
	private JPanel footerPanel;
	private JButton runButton;

    public EdtSpecCreatePanel(GuiMain parent, Config config){
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
        titleL = new JLabel("Create eDT Spec from Datasets");
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

        edtTypeL = new JLabel("Type:");
        edtTypeCB = new JComboBox<String>(Config.EDT_TYPE);
        headerCntL = new JLabel("# of Header Lines:");
        headerCntTF = new JTextField("");
        headerRowL = new JLabel("Header Row Number:");
        headerRowL.setVisible(false);
        headerRowTF = new JTextField("");
        headerRowTF.setVisible(false);
        
        headerCntTF.getDocument().addDocumentListener(new DocumentListener() {
        	public void changedUpdate(DocumentEvent e) {
        		//Do nothing - simply ignore the operation.
        	}
        	public void insertUpdate(DocumentEvent e) {
        	    check();
        	}
        	public void removeUpdate(DocumentEvent e) {
        		check();
        	}
            public void check() {
            	int headerCnt = NumberUtils.toInt(headerCntTF.getText(), -1);
            	if (headerCnt > 0) {
            		headerRowL.setVisible(true);
            		headerRowTF.setVisible(true);
            	} else {
            		headerRowL.setVisible(false);
            		headerRowTF.setVisible(false);
            		headerRowTF.setText("");
            	}
            }
        });

        encodingL = new JLabel("Character Encoding:");
        encodingCB = new JComboBox<String>(Config.ENCODING);
        delimitedOrFixedL = new JLabel("Delimited/Fixed Width:");
        delimitedOrFixedCB = new JComboBox<String>(Config.DELIMITED_FIXED);
        delimiterL = new JLabel("Delimiter:");
        delimiterTF = new JTextField("");
        textQualifierL = new JLabel("Text Qualifier:");
        textQualifierCB = new JComboBox<String>(Config.TEXT_QUALIFIER);
        dataSourceLocationL = new JLabel("Data Source Location:");
        dataSourceLocationTF = new JTextField("");
        dataSourceLocationTF.setEditable(false);
        outputLocationL = new JLabel("Output Location:");
        outputLocationTF = new JTextField("");
        browseButton1 = new JButton("Browse");
        clearButton1 = new JButton("Clear");
        browseButton2 = new JButton("Browse");
        clearButton2 = new JButton("Clear");
        
    	fileChooser1 = new JFileChooser();
        dataSourceLocationTF.setTransferHandler(new FilePathTransferHandler(dataSourceLocationTF, fileChooser1));	//Add DnD support
        fileChooser2 = new JFileChooser();
        fileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputLocationTF.setTransferHandler(new FilePathTransferHandler(outputLocationTF, fileChooser2));	//Add DnD support
        
    	browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selected = fileChooser1.showOpenDialog(EdtSpecCreatePanel.this);
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
                int selected = fileChooser2.showOpenDialog(EdtSpecCreatePanel.this);
                if (selected == JFileChooser.APPROVE_OPTION){
                	outputLocationTF.setText(fileChooser2.getSelectedFile().getPath()
                			+ System.getProperty("file.separator") + GuiConstants.EDT_EXCEL_FILE_NAME);
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

        edtTypeL.setFont(defaultFont);
        edtTypeCB.setFont(defaultFont);
        headerCntL.setFont(defaultFont);
        headerCntTF.setFont(defaultFont);
        headerRowL.setFont(defaultFont);
        headerRowTF.setFont(defaultFont);
        encodingL.setFont(defaultFont);
        encodingCB.setFont(defaultFont);
        delimitedOrFixedL.setFont(defaultFont);
        delimitedOrFixedCB.setFont(defaultFont);
        delimiterL.setFont(defaultFont);
        delimiterTF.setFont(defaultFont);
        textQualifierL.setFont(defaultFont);
        textQualifierCB.setFont(defaultFont);
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
        edtTypeCB.setSelectedItem(config.edtType);
        headerCntTF.setText(config.edtHeaderCnt);
        headerRowTF.setText(config.edtHeaderRow);
        encodingCB.setSelectedItem(config.edtEncoding);
        delimitedOrFixedCB.setSelectedItem(config.edtDelimitedOrFixed);
        delimiterTF.setText(config.edtDelimiter);
        textQualifierCB.setSelectedItem(config.edtTextQualifier);
        dataSourceLocationTF.setText(config.edtDataSourceLocation);
        outputLocationTF.setText(config.edtOutputLocation);
        
        GroupLayout bodyPanelLayout = new GroupLayout(bodyPanel);
        bodyPanel.setLayout(bodyPanelLayout);
        bodyPanelLayout.setHorizontalGroup(
        	bodyPanelLayout.createSequentialGroup()
                .addContainerGap(GuiConstants.BODY_GAP_LEFT, GuiConstants.BODY_GAP_LEFT)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(edtTypeL)
                    .addComponent(headerCntL)
                    .addComponent(encodingL)
                    .addComponent(delimitedOrFixedL)
                    .addComponent(delimiterL)
                    .addComponent(textQualifierL)
                    .addComponent(dataSourceLocationL)
                    .addComponent(outputLocationL))
                .addGap(GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL, GuiConstants.BODY_GAP_HORIZONTAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(edtTypeCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addGroup(bodyPanelLayout.createSequentialGroup()
                    	.addComponent(headerCntTF, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    	.addGap(GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL, GuiConstants.CONFIG_GAP_VERTICAL)
                		.addComponent(headerRowL)
                    	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    	.addComponent(headerRowTF, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))
                    .addComponent(encodingCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimitedOrFixedCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimiterTF, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
                    .addComponent(textQualifierCB, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(edtTypeL)
                    .addComponent(edtTypeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(headerCntL)
                    .addComponent(headerCntTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                	.addComponent(headerRowL)
                	.addComponent(headerRowTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(encodingL)
                    .addComponent(encodingCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(delimitedOrFixedL)
                    .addComponent(delimitedOrFixedCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(delimiterL)
                    .addComponent(delimiterTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL, GuiConstants.BODY_GAP_VERTICAL)
                .addGroup(bodyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(textQualifierL)
                    .addComponent(textQualifierCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
    	runButton = new JButton("Create");
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
        if (edtTypeCB.getSelectedItem() == null) {
        	return "Type cannot be blank.";
    	} else if (NumberUtils.toInt(headerCntTF.getText(), -1) < 0) {
    		return "# of Header Lines must be 0 or a positive number.";
    	} else if (NumberUtils.toInt(headerRowTF.getText(), -1) <= 0 || NumberUtils.toInt(headerRowTF.getText(), -1) > NumberUtils.toInt(headerCntTF.getText(), -1)) {
    		return "Header Row Number must be a positive number smaller than or equal to # of Header Lines.";
        } else if (encodingCB.getSelectedItem() == null) {
            return "Character Encoding cannot be blank.";
        } else if (delimitedOrFixedCB.getSelectedItem() == null) {
            return "Delimited/Fixed Width cannot be blank.";
    	} else if ( !(StringUtils.length(delimiterTF.getText()) == 1 || StringUtils.equals(delimiterTF.getText(), "\\t")) ) {
    		return "Delimiter must be a single character or a tab (\\t).";
        } else if (textQualifierCB.getSelectedItem() == null) {
            return "Text Qualifier cannot be blank.";
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
				parent.showPanel(parent.edtSpecCreateResultPanel);
				parent.edtSpecCreateResultPanel.clearBodyPanel();
				Runnable createEdtSpec = new Runnable() {
					public void run() {
						final LoggerContext loggerContext = (LoggerContext)LogManager.getContext(false);
						final Configuration loggerConfig = loggerContext.getConfiguration();
						final PatternLayout patternLayout = PatternLayout.newBuilder().withPattern("[%p] %m%n").build();
				        EditorPaneAppender epAppender = new EditorPaneAppender(patternLayout);
				        epAppender.setEditorPane(parent.edtSpecCreateResultPanel.gResultEditorPane);
				        epAppender.start();
				        loggerConfig.getRootLogger().addAppender(epAppender, Level.INFO, null);
				        
						logger.info("Creating eDT Spec...");
						StudyEdtGeneral param = new StudyEdtGeneral();
						param.type_id = edtTypeCB.getSelectedItem().toString();
						param.header_line = NumberUtils.toInt(headerCntTF.getText());
						param.header_row_num = NumberUtils.toInt(headerRowTF.getText());
						param.encoding = encodingCB.getSelectedItem().toString();
						param.separating_method = delimitedOrFixedCB.getSelectedItem().toString();
						param.delimiter = delimiterTF.getText();
						param.text_qualifier = textQualifierCB.getSelectedItem().toString();
						File srcFile = new File(dataSourceLocationTF.getText());
						File outFile = new File(outputLocationTF.getText());
						EdtSpecCreator creator = new EdtSpecCreator(param, srcFile);
						try {
							Pair<List<StudyEdtColumn>, List<StudyEdtCodelist>> rtn = creator.create();
							ExcelWriter2 excelWriter = new ExcelWriter2();
							excelWriter.addData("GENERAL", param, StudyEdtGeneral.class);
							excelWriter.addData("COLUMN", rtn.getLeft(), StudyEdtColumn.class);
							excelWriter.addData("CODELIST", rtn.getRight(), StudyEdtCodelist.class);
							excelWriter.writeout(outFile);
							logger.info("Done.");
							parent.edtSpecCreateResultPanel.outputLocationUrl.setText(
									new File(outputLocationTF.getText()).getCanonicalPath());
						} catch (Exception ex) {
							logger.error(ExceptionUtils.getStackTrace(ex));
						} finally {
							epAppender.stop();
							loggerConfig.getRootLogger().removeAppender(EditorPaneAppender.APPENDER_NAME);
						}
				    }
				};
				new Thread(createEdtSpec).start();
			} else {
				String[] options = { "OK" };
				JLabel errMessageL = new JLabel(errMessage);
				errMessageL.setFont(defaultFont);
				JOptionPane.showOptionDialog(parent, errMessageL, "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, "OK");
			}
		}
	}
}
