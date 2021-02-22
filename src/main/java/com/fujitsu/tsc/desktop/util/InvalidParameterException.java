/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

public class InvalidParameterException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidParameterException(String param, String value) {
		super("The parameter " + param + " = " + value + " is not a valid parameter.");
	}
}
