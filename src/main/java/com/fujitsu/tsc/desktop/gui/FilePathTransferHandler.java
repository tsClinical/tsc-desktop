/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

public class FilePathTransferHandler extends TransferHandler {
	
    private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
    private JTextComponent textComponent;
    private JList<String> listComponent;
    private FileNameExtensionFilter filter;
    private JFileChooser chooser;
    private int fileSelectionMode;
    private boolean multiSelectionEnabled;
    private Transferable t;
    private List<File> fileList;
	
	public FilePathTransferHandler(JTextComponent component, JFileChooser chooser) {
		super();
		this.textComponent = component;
		this.chooser = chooser;
		this.fileSelectionMode = chooser.getFileSelectionMode();
	}

	public FilePathTransferHandler(JTextComponent component, JFileChooser chooser, FileNameExtensionFilter filter) {
		super();
		this.textComponent = component;
		this.chooser = chooser;
		this.fileSelectionMode = chooser.getFileSelectionMode();
		this.filter = filter;
	}

	public FilePathTransferHandler(JList component, JFileChooser chooser) {
		super();
		this.listComponent = component;
		this.chooser = chooser;
		this.multiSelectionEnabled = chooser.isMultiSelectionEnabled();
	}

	public boolean canImport(TransferHandler.TransferSupport info) {
        if (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        	info.setDropAction(TransferHandler.COPY);
        	return true;
        } else {
        	return false;
        }
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		try {
    		if (!info.isDrop()) {
                return false;
            } else {
				/*
				 * Check if the dragged file has a supported file extension.
				 * This check cannot be made in the canImport method - see
				 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6759788
				 */
	    		t = info.getTransferable();
				fileList = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
				// Do not use the FileNameExtensionFilter.accept() method since it accepts directory.
				if (textComponent != null) {
					if (fileSelectionMode == JFileChooser.DIRECTORIES_ONLY) {
						if (fileList.get(0).isDirectory()) {
							textComponent.setText(fileList.get(0).getAbsolutePath() + System.getProperty("file.separator") + GuiConstants.DEFINE_FILE_NAME);
							chooser.setCurrentDirectory(fileList.get(0).getParentFile());
							return true;
						} else {
							return false;
						}
					} else {
						if (Arrays.asList(this.filter.getExtensions()).contains(
								fileList.get(0).getName().substring(fileList.get(0).getName().lastIndexOf('.') + 1))) {
							textComponent.setText(fileList.get(0).getAbsolutePath());
							chooser.setCurrentDirectory(fileList.get(0).getParentFile());
							return true;
						} else {
							return false;
						}
					}
				} else if (listComponent != null) {
					Vector<String> paths = new Vector<String>();
					for (File f : fileList) {
						paths.add(f.getAbsolutePath());
					}
					listComponent.setListData(paths);
					return true;
				} else {
					return false;
				}
            }
		} catch (UnsupportedFlavorException ex) {
			logger.error(ex.getMessage());
			return false;
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}
}
