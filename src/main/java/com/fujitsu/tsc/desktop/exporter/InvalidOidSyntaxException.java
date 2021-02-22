/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.exporter;

public class InvalidOidSyntaxException extends Exception {

	public InvalidOidSyntaxException(String str) {
		super("The OID syntax is invalid - Tag Name: " + str);
	}

}
