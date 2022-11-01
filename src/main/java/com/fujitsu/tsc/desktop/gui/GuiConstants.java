/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.gui;

import java.awt.Color;
import java.awt.Font;

import com.fujitsu.tsc.desktop.util.Config;

public class GuiConstants {

	/* Constants that help screen navigations */
	public static int GENERATION_CONFIG = 1;
	public static int VALIDATION_CONFIG = 2;
	public static int GENERATION_RESULT = 3;
	public static int VALIDATION_RESULT = 4;
	public static int IMPORTATION_CONFIG = 5;
	public static int IMPORTATION_RESULT = 6;
	public static int ODMGEN_CONFIG = 7;
	public static int ODMGEN_RESULT = 8;
	public static int ODMIMP_CONFIG = 9;
	public static int ODMIMP_RESULT = 10;

	/* Constants related to default colors */
	public static Color COLOR_BG = Color.decode("#f8f8f8");
	public static Color COLOR_THEME = Color.decode("#72a493");
	public static Color COLOR_BORDER = Color.decode("#72a493");
	public static Color COLOR_BTN_FG_SUBMIT = Color.decode("#0687f5");
	public static Color COLOR_BTN_BG_SUBMIT = Color.decode("#f8f8f8");

	/* Constants related to default fonts */
	public static String FONT_NAME_TITLE = "Arial";
	public static String FONT_NAME = "Arial";
	public static int FONT_STYLE_TITLE = Font.BOLD;
	public static int FONT_STYLE = Font.PLAIN;
	public static int FONT_SIZE_TITLE = 16;
	public static int FONT_SIZE = 12;
	public static int FONT_SIZE_SMALL = 10;
	public static Color FONT_COLOR_TITLE = COLOR_THEME;
	public static Color FONT_COLOR = Color.BLACK;
	public static Color FONT_COLOR_URL = Color.BLUE;
	public static Color FONT_COLOR_ON_WHITE = Color.DARK_GRAY;

	/* Constants related to JFrame */
	public static int FRAME_WIDTH = 1024;
	public static int FRAME_HEIGHT = 768;
	public static int BORDER_WIDTH = 1;
	public static int BORDER_WIDTH_THICK = 5;
	public static String INFINITY_IMAGE_PATH = "resources/images/Infinity-Mark-PANTONE-Red.jpg";

	/* Constants related to Menu */
	public static int MENU_HEIGHT = 80;
	public static String ICON_IMAGE_DIR = "resources/material-icons"; 

	/* Constants related to the content pane of JFrame */
	public static int MAIN_GAP_LEFT = 10;
	public static int MAIN_GAP_HORIZONTAL;
	public static int MAIN_GAP_RIGHT = 10;
	public static int MAIN_GAP_TOP = 10;
	public static int MAIN_GAP_VERTICAL = 10;
	public static int MAIN_GAP_BOTTOM = 10;

	/* Constants related to JList */
	public static int VISIBLE_ROWS = 5;

	/* Constants related to Selector panel*/
	public static int SELECTOR_GAP_LEFT = 18;
	public static int SELECTOR_GAP_HORIZONTAL = 40;
	public static int SELECTOR_GAP_RIGHT = 18;
	public static int SELECTOR_GAP_TOP = 0;
	public static int SELECTOR_GAP_VERTICAL;
	public static int SELECTOR_GAP_BOTTOM = 0;

	/* Constants related to Config panel */
	public static int BODY_GAP_LEFT = 16;
	public static int BODY_GAP_HORIZONTAL = 16;
	public static int BODY_GAP_RIGHT = 16;
	public static int BODY_GAP_TOP = 24;
	public static int BODY_GAP_VERTICAL = 18;
	public static int BODY_GAP_BOTTOM = 32;
	public static int CONFIG_GAP_LEFT = 10;
	public static int CONFIG_GAP_HORIZONTAL = 10;
	public static int CONFIG_GAP_RIGHT = 10;
	public static int CONFIG_GAP_TOP = 24;
	public static int CONFIG_GAP_VERTICAL = 18;
	public static int CONFIG_GAP_BOTTOM = 48;

	/* Constants related to Command panels*/
	public static int FOOTER_GAP_LEFT = 18;
	public static int COMMAND_GAP_HORIZONTAL = 18;
	public static int FOOTER_GAP_RIGHT = 18;
	public static int FOOTER_GAP_TOP = 0;
	public static int COMMAND_GAP_VERTICAL;
	public static int FOOTER_GAP_BOTTOM = 0;

	/* Constants related to Result panel*/
	public static int RESULT_GAP_LEFT = 10;
	public static int RESULT_GAP_HORIZONTAL;
	public static int RESULT_GAP_RIGHT = 10;
	public static int RESULT_GAP_TOP = 0;
	public static int RESULT_GAP_VERTICAL;
	public static int RESULT_GAP_BOTTOM = 23;
	public static int EDITOR_PANE_WIDTH = 830;
	public static int EDITOR_PANE_HEIGHT = 430;
	public static int TABLE_WIDTH = 830;
	public static int TABLE_HEIGHT = 430;
	public static int FIRST_COLUMN_WIDTH = 130;
	public static int SECOND_COLUMN_WIDTH = 700;
	public static int IMPORT_TABLE_WIDTH = 830;
	public static int IMPORT_TABLE_HEIGHT = 240;
	public static int IFIRST_COLUMN_WIDTH = 80;
	public static int ISECOND_COLUMN_WIDTH = 470;

	/* Constants related to About JDialog */
	public static int ABOUT_WIDTH = 512;
	public static int ABOUT_HEIGHT = 384;
	public static int ABOUT_GAP_LEFT = 10;
	public static int ABOUT_GAP_HORIZONTAL = 10;
	public static int ABOUT_GAP_RIGHT = 10;
	public static int ABOUT_GAP_TOP = 10;
	public static int ABOUT_GAP_VERTICAL = 10;
	public static int ABOUT_GAP_BOTTOM = 10;
	public static int SYMBOL_WIDTH = 155;
	public static int SYMBOL_HEIGHT = 96;
//	public static String SYMBOL_IMAGE_PATH = "resources/images/Symbol-Mark-PANTONE-Red.jpg";
//	public static String SOFTWARE_NAME = "tsClinical Metadata Desktop Tools";
//	public static String SOFTWARE_VERSION_LABEL = "V1.0.0";
	public static String WEBSITE_URL = "https://github.com/tsClinical/tsc-desktop";
	public static String COPYRIGHT = "Copyright 2020-2022 Fujitsu Limited. All Rights Reserved.";

	/* Other constants */
	public static String[] DEFINE_VERSIONS = new String[] { "2.0.0" };
	public static String[] ODM_VERSIONS = new String[] { "1.3.2" };
	public static boolean ENABLE_ODM = true;
	public static String ADAM_DISPLAY = "ADaM";
	public static String SDTM_DISPLAY = "SDTM";
	public static String SEND_DISPLAY = "SEND";
	public static String[] DATASET_TYPES = new String[] { ADAM_DISPLAY, SDTM_DISPLAY, SEND_DISPLAY };
	public static String getDatasetTypeDisplay(Config.DatasetType type) {
		if (type.toString().equals(Config.DatasetType.ADaM.toString())) {
			return ADAM_DISPLAY;
		} else if (type.toString().equals(Config.DatasetType.SDTM.toString())) {
			return SDTM_DISPLAY;
		} else if (type.toString().equals(Config.DatasetType.SEND.toString())) {
			return SEND_DISPLAY;
		} else {
			return null;
		}
	}
	public static Config.DatasetType getDatasetTypeValue(String strType) {
		if (strType.equals(ADAM_DISPLAY)) {
			return Config.DatasetType.ADaM;
		} else if (strType.equals(SDTM_DISPLAY)) {
			return Config.DatasetType.SDTM;
		} else if (strType.equals(SEND_DISPLAY)) {
			return Config.DatasetType.SEND;
		} else {
			return null;
		}
	}
	public static String[] ENCODINGS = new String[] { "UTF-8" };
	public static String DEFINE_FILE_NAME = "define.xml";
	public static String DEFINE_HTML_FILE_NAME = "define.html";
	public static String ODM_FILE_NAME = "odm.xml";
	public static String EXCEL_FILE_NAME = "define.xlsx";
	public static String ODM_EXCEL_FILE_NAME = "odm.xlsx";
	public static String CRF_EXCEL_FILE_NAME = "crf_spec.xlsx";
	public static String EDT_EXCEL_FILE_NAME = "edt_spec.xlsx";
}
