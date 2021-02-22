/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

import java.util.ArrayList;
import java.util.List;

public class TscStack {
	private List<String> stack;

	public TscStack() {
 		stack = new ArrayList<String>();
	}

	public void push(String qName) {
		stack.add(qName);
	}

	private String popData(Boolean canRemove) {
		return popData(canRemove, 0);
	}

	private String popData(Boolean canRemove, int generationAgo) {
		int pos = stack.size() - generationAgo - 1;
		String result;

		if(pos < 0) {
			result = null;
		} else {
			result = stack.get(pos);
			if(canRemove) {
				stack.remove(pos);
			}
		}

		return result;
	}

	public String pop() {
		return popData(true);
	}

	public String getCurrent() {
		return popData(false);
	}

	public String getParent() {
		return popData(false, 1);
	}

	public String getPath() {
		String separator = "/";
		String result = separator;

		for(String qName: stack) {
			result += qName + separator;
		}
		return result;
	}

	public int getSize() {
		return stack.size();
	}

	public int getPosition() {
		return this.stack.size() - 1;
	}
}
