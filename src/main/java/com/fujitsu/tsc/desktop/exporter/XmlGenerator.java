/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.InvalidParameterException;

public class XmlGenerator {

	private static Logger logger = Logger.getLogger("com.fujitsu.tsc.desktop");
	private Properties prop;
	private Config config;
	private Config.RunMode mode;

	/**
	 * This constructor creates a new Generator object.
	 */
	public XmlGenerator() {
		/* Configure the Generator object with a default config and run mode. */
		this.config = new Config();
		this.mode = config.runMode;
	}

	public XmlGenerator(Config config) {
		this.config = config;
		this.mode = Config.RunMode.API;
	}

	public XmlGenerator(Config config, Config.RunMode mode) {
		this.config = config;
		this.mode = mode;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * This method creates a DefineXmlWriter object using parameters defined in main.properties,
	 * and calls its methods in the right order to create a new Define.xml.
	 * @throws InvalidParameterException 
	 * @throws TableNotFoundException 
	 * @throws IOException 
	 * @throws InvalidOidSyntaxException 
	 * @throws RequiredValueMissingException 
	 * @throws InvalidFormatException 
	 */
	public void generateDefineXml() throws InvalidParameterException, TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException, InvalidFormatException {

		try {
			logger.info("Launching the program...");
			logger.info("Creating define.xml for " + config.e2dDatasetType + "...");
			DefineXmlWriter writer = new DefineXmlWriter(this.config);
			logger.info("Writing XML Header...");
			writer.writeXMLHeader();
			logger.info("Done.");
			logger.info("Writing Study Section...");
			writer.writeStudySection();
			logger.info("Done.");
			logger.info("Writing Document Section...");
			if (config.e2dDatasetType.name().equals("SDTM")) {
				writer.writeDocumentSection("AnnotatedCRF");
			}
			writer.writeDocumentSection("SupplementalDoc");
			logger.info("Done.");
			logger.info("Writing ValueListDef Section...");
			writer.writeValueListDefSection();
			logger.info("Done.");
			logger.info("Writing WhereClauseDef Section...");
			writer.writeWhereClauseDefSection();
			logger.info("Done.");
			logger.info("Writing ItemGroupDef Section...");
			writer.writeItemGroupDefSection();
			logger.info("Done.");
			logger.info("Writing ItemDef Section...");
			writer.writeItemDefSection();
			logger.info("Done.");
			logger.info("Writing Codelist Section...");
			writer.writeCodelistSection();
			logger.info("Done.");
			logger.info("Writing MethodDef Section...");
			writer.writeMethodDefSection();
			logger.info("Done.");
			logger.info("Writing CommentDef Section...");
			writer.writeCommentDefSection();
			logger.info("Done.");
			logger.info("Writing Leaf Section...");
			writer.writeLeafSection();
			logger.info("Done.");
			if (config.e2dDatasetType.name().equals("ADaM") && config.e2dIncludeResultMetadata == true) {
				logger.info("Writing AnalysisResult Section...");
				writer.writeAnalysisResultSection();
				logger.info("Done.");
			}
			writer.writeEndTag("MetaDataVersion");
			writer.writeEndTag("Study");
			writer.writeEndTag("ODM");
			logger.info("Define.xml has been created.");

			writer.close();
		} catch (InvalidParameterException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(1);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (TableNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(2);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(3);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(4);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (InvalidOidSyntaxException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(5);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (RequiredValueMissingException ex) {
			logger.error(ex.getMessage());
			logger.error(ex.getSourceClass());
			for (int i = 0; i < ex.getStackTrace().length; i++) {
				logger.error("    " + ex.getStackTrace()[i]);
			}
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(6);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		}
	}

	public void generateOdmXml() throws InvalidParameterException, TableNotFoundException, IOException, InvalidOidSyntaxException, RequiredValueMissingException, InvalidFormatException {

		try {
			logger.info("Launching the program...");
			logger.info("Creating ODM ...");
			OdmXmlWriter writer = new OdmXmlWriter(this.config);
			logger.info("Writing XML Header...");
			writer.writeXMLHeader();
			logger.info("Done.");
			logger.info("Writing Study Section...");
			writer.writeStudySection();
			logger.info("Done.");
			logger.info("Writing Unit Section...");
			writer.writeUnitSection();
			logger.info("Done.");
			logger.info("Writing Protocol Section...");
			writer.writeProtocolSection();
			logger.info("Done.");
			logger.info("Writing EventDef Section...");
			writer.writeEventDefSection();
			logger.info("Done.");
			logger.info("Writing FormDef Section...");
			writer.writeFormDefSection();
			logger.info("Done.");
			logger.info("Writing ItemGroup Section...");
			writer.writeItemGroupDefSection();
			logger.info("Done.");
			logger.info("Writing ItemDef Section...");
			writer.writeItemDefSection();
			logger.info("Done.");
			logger.info("Writing Codelist Section...");
			writer.writeCodelistSection();
			logger.info("Done.");
			logger.info("Writing ConditionDef Section...");
			writer.writeConditionDefSection();
			logger.info("Done.");
			logger.info("Writing MethodDef Section...");
			writer.writeMethodDefSection();
			logger.info("Done.");

			writer.writeEndTag("MetaDataVersion");
			writer.writeEndTag("Study");
			writer.writeEndTag("ODM");
			logger.info("ODM has been created.");

			writer.close();
		} catch (InvalidParameterException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(1);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (TableNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(2);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(4);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (InvalidOidSyntaxException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(5);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		} catch (RequiredValueMissingException ex) {
			logger.error(ex.getMessage());
			logger.error("Exiting the program...");
			if (mode.equals(Config.RunMode.CLI)) {
				System.exit(6);
			} else if (mode.equals(Config.RunMode.GUI)) {
				try {
					Thread.currentThread().join();
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				throw ex;
			}
		}
	}
	
	public static void main(String[] args) throws InvalidFormatException {
		PropertyConfigurator.configure("./properties/log4j.properties");
		String[] strConfig = null;

		try {
			/*
			 * Parse command-line arguments
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-config")) {
					String s = args[++i];
					strConfig = s.split(";");
					if (strConfig != null && strConfig.length > 0) {
						break;
					} else {
						logger.error("'-config' must be followed by PARAMETER=VALUE pair(s) separated by ';'.");
						logger.error("Argument: " + args[i]);
						System.exit(-1);
					}
				} else {
					logger.error("Undefined command-line argument(s): " + args[i]);
					System.exit(-1);
				}
			}
			 */

			/*
			 * Create a GeneratorConfig object, then create a generator using the config.
			 */
			Properties prop = new Properties();
			FileReader reader = new FileReader(Config.PROPERTIES_PATH);
			prop.load(reader);
			reader.close();
			Config config = new Config(prop);

			/*
			 * Command-line parameters precede Properties.
			if (strConfig != null) {
				for (int i = 0; i < strConfig.length; i++) {
					try {
						config.setProperty(Config.Parameter.valueOf(strConfig[i].substring(0, strConfig[i].indexOf('='))),
								strConfig[i].substring(strConfig[i].indexOf('=') + 1));
					} catch (IllegalArgumentException ex) {
						logger.error("An illegal argument has been found: " + strConfig[i].substring(0, strConfig[i].indexOf('=')));
						System.exit(-1);
					}
				}
			}
			 */

			XmlGenerator generator = new XmlGenerator(config, Config.RunMode.CLI);
			generator.generateDefineXml();

		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		} catch (InvalidParameterException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		} catch (TableNotFoundException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		} catch (InvalidOidSyntaxException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		} catch (RequiredValueMissingException ex) {
			logger.error(ex.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * This method escapes a string so that the string can be properly handled within XML.
	 * i.e.
	 * 	& -> &amp;
	 * 	< -> &lt;
	 * 	> -> &gt;
	 *  " -> &quot;
	 *  ' -> &apos;
	 */
	public static String escapeString(String str) {
		StringBuffer buffer = new StringBuffer(1000);	//initial capacity
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '&') {
				buffer.append("&amp;");
			} else if (str.charAt(i) == '<') {
				buffer.append("&lt;");
			} else if (str.charAt(i) == '>') {
				buffer.append("&gt;");
			} else if (str.charAt(i) == '"') {
				buffer.append("&quot;");
			} else if (str.charAt(i) == '\'') {
				buffer.append("&apos;");
			} else {
				buffer.append(str.charAt(i));
			}
		}
		return buffer.toString();
	}
}
