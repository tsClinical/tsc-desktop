/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

public class WhereClause {
	private String variable;
	public enum Operator {EQ, NE}
	private Operator operator;
	private String value;
	
	public WhereClause(String variable, Operator operator, String value) {
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public Operator getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
}
