/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class XmlDocument {
	public static final String INDENT_STRING = "  ";
	
	private XmlHeader xml_header;
	private List<ProcessingInstruction> processing_instructions;
	private XmlElement root_element;

	public XmlDocument(XmlHeader xml_header, XmlElement root_element) {
		this.xml_header = xml_header;
		this.processing_instructions = new ArrayList<>();
		this.root_element = root_element;
	}
	
	public void addProcessingInstruction(ProcessingInstruction processing_instruction) {
		this.processing_instructions.add(processing_instruction);
	}
	
	public XmlElement getRootElement() {
		return this.root_element;
	}
	
	public static class XmlHeader {
		private String encoding;
		
		public XmlHeader(String encoding) {
			this.encoding = encoding;
		}
		
		public String toString() {
			String str = "<?xml version=\"1.0\" encoding=\""
					+ this.encoding
					+ "\"?>" + System.lineSeparator();
			return str;
		}
	}
	
	public static class ProcessingInstruction {
		private String target;
		private LinkedHashMap<String, String> instructions;
		
		public ProcessingInstruction(String target) {
			this.target = target;
			this.instructions = new LinkedHashMap<>();
		}
		
		public void addInstruction(String name, String value) {
			this.instructions.put(name, value);
		}
		
		public String toString() {
			String str = "<?" + this.target;
			for (Entry<String, String> entry : this.instructions.entrySet()) {
				str += " " + entry.getKey() + "=\"" + escapeString(entry.getValue()) + "\"";
			}
			str += "?>" + System.lineSeparator();
			return str;
		}
	}
	
	public static class XmlElement {
		private int indent_level;
		private String name;
		private LinkedHashMap<String, String> attributes;
		private String text;
		private List<XmlElement> elements = new ArrayList<>();
		
		public XmlElement(String name) {
			this.indent_level = 0;
			this.name = name;
			this.attributes = new LinkedHashMap<>();
			this.text = "";
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getAttribute(String name) {
			if (StringUtils.isNotEmpty(name)) {
				return this.attributes.get(name);
			}
			return null;
		}
		
		public void addAttribute(String name, String value) {
			if (StringUtils.isNotEmpty(value)) {
				this.attributes.put(name, value);
			}
		}
		
		public void addAttribute(String name, Integer value) {
			if (value != null && value != 0) {
				this.attributes.put(name, String.valueOf(value));
			}
		}
		
		public void addText(String text) {
			this.text = text;
		}
		
		public void addElement(XmlElement element) {
			element.setIndentLevel(this.indent_level + 1);
			this.elements.add(element);
		}
		
		public XmlElement getElementByName(String name) {
			return this.elements.stream().filter(o -> StringUtils.equals(o.name, name)).findFirst().orElse(null);
		}
		
		public List<XmlElement> getElementsByName(String name) {
			return this.elements.stream().filter(o -> StringUtils.equals(o.name, name)).collect(Collectors.toList());
		}

		public int getElementIndex(XmlElement element) {
			return this.elements.indexOf(element);
		}
		
		public void sortElements(Comparator<XmlElement> comparator) {
			this.elements = this.elements.stream().sorted(comparator).collect(Collectors.toList());
		}
		
		public void setIndentLevel(int indent_level) {
			this.indent_level = indent_level;
		}
		
		public int getIndentLevel() {
			return indent_level;
		}
		
		public String toString() {
			String str = "";
			if (StringUtils.isEmpty(text) && elements.isEmpty()) {	//Abbreviated expression
				/* Print tag */
				for (int i = 0; i < this.indent_level; i++) {
					str += INDENT_STRING;
				}
				str += "<" + this.name;
				for (Entry<String, String> entry : this.attributes.entrySet()) {
					str += " " + entry.getKey() + "=\"" + escapeString(entry.getValue()) + "\"";
				}
				str += "/>" + System.lineSeparator();
			} else {
				/* Print beginning tag */
				for (int i = 0; i < this.indent_level; i++) {
					str += INDENT_STRING;
				}
				str += "<" + this.name;
				for (Entry<String, String> entry : this.attributes.entrySet()) {
					str += " " + entry.getKey() + "=\"" + escapeString(entry.getValue()) + "\"";
				}
				str += ">";
				
				/* Print text */
				str += escapeString(this.text);
	
				/* Print elements */
				if (!this.elements.isEmpty()) {
					str += System.lineSeparator();
				}
				for (XmlElement element : this.elements) {
					str += element.toString();
				}
	
				/* Print ending tag */
				if (!this.elements.isEmpty()) {
					for (int i = 0; i < this.indent_level; i++) {
						str += INDENT_STRING;
					}
				}
				str += "</" + this.name + ">" + System.lineSeparator();
			}
			return str;
		}
	}
	
	public String toString() {
		String str = "";
		str = xml_header.toString();
		for (ProcessingInstruction processing_instruction : processing_instructions) {
			str += processing_instruction.toString();
		}
		str += root_element.toString();
		return str;
	}
	
	/**
	 * This method escapes a string so that the string can be properly handled within XML.
	 * @param str A string value before HTML escape
	 * @return A string value before HTML escape
	 */
	public static String escapeString(String str) {
		StringBuffer buffer = new StringBuffer(str.length());
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
