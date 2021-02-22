/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import com.fujitsu.tsc.desktop.exporter.TableNotFoundException;
import com.fujitsu.tsc.desktop.exporter.WhereClause;

public interface MetaDataReader {
	
	Hashtable<String, String> read();
	Hashtable<String, String> read(String tableName);
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
// MetaDataReader connects multiple WhereClauses as logical AND.
//	void setTable(String tableName, WhereClause clause) throws TableNotFoundException;
	void setTable(String tableName, WhereClause clauseArray[]) throws TableNotFoundException;
	void setTable(String str) throws TableNotFoundException;
	void close() throws IOException;
	String getTableName();
// Changed for v1.1.0 - now MetaDataReader allows a WhereClause array.
//	WhereClause getWhereClause();
	WhereClause[] getWhereClause();
	void setUniqueKeys(String tableName, HashSet keys);
	void clearUniqueKeys(String tableName);

}
