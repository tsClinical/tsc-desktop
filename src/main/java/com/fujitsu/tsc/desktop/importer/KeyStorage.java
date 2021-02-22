/* 
 * Copyright (c) 2020 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * The KeyStorage class stores an xml content.
 */
public class KeyStorage {
	private Hashtable<String, Oid> tableStorage;
	private ArrayList<String> arrayStorage;

	public KeyStorage() {
		tableStorage = new Hashtable<String, Oid>();
		arrayStorage = new ArrayList<String>();
	}
		
	public int storageCount() {
		return tableStorage.size();
	}

	public boolean storageExists(String storage) {
		return tableStorage.containsKey(storage);
	}

	public ArrayList<String> list() {
		return arrayStorage;
	}

	public Oid storage(String storage) {
		Oid oids;
		if(tableStorage.containsKey(storage)) {
			oids = tableStorage.get(storage);
		} else {
			oids = new Oid();
			tableStorage.put(storage, oids);
			arrayStorage.add(storage);
		}
		return oids;
	}

	public class Oid { 
		private Hashtable<String, Members> tableOid;
		private ArrayList<String> arrayOid;
		private String lastOid;

		public class Members {
			private Hashtable<String, LinkedHashMap<String, String>> tableMembers;
			private ArrayList<String> arrayMembers;

			private Members() {
				tableMembers = new Hashtable<String, LinkedHashMap<String, String>>();
				arrayMembers = new ArrayList<String>();
			}

			public int oidCount() {
				return tableMembers.size();
			}

			public ArrayList<String> memberList() {
				return arrayMembers;
			}

			public ArrayList<String> subkeyList(String member) {
				ArrayList<String> subkeyList = new ArrayList<String>();
				for(String subkey: tableMembers.get(member).keySet()) {
					subkeyList.add(subkey);
				}
				return subkeyList;
			}

			public int subkeyCount(String member) {
				return tableMembers.get(member).size();
			}

			public boolean memberExists(String member) {
				return tableMembers.containsKey(member);
			}

			public String get(String member) {
				return get(member, ""); 
			}

			public String get(String member, String subkey) {
				String value = "";
				if(tableMembers.get(member) != null) {
					if(tableMembers.get(member).get(subkey) != null) {
						value = tableMembers.get(member).get(subkey);
					}
				}
				return value; 
			}

			public void put(String key, String value) {
				put(key, "", value);
			}

			public void put(String member, String subkey, String value) {
				LinkedHashMap<String, String> valueTable = new LinkedHashMap<String, String>();
				if(tableMembers.containsKey(member)) { 
					valueTable = tableMembers.get(member);
				}
				valueTable.put(subkey, value);
				tableMembers.put(member, valueTable);
				if(!arrayMembers.contains(member)) {
					arrayMembers.add(member);
				}
			}

			public void remove(String key) {
				tableMembers.remove(key);
				arrayMembers.remove(key);
			}
		}

		public Oid() {
			tableOid = new Hashtable<String, Members>();
			arrayOid = new ArrayList<String>();
			clearLastOid();
		}

		public void clearLastOid() {
			lastOid = null;
		}

		public String getLastOid() {
			return lastOid;
		}

		public int oidCount() {
			return tableOid.size();
		}

		public boolean oidExists(String oid) {
			return tableOid.containsKey(oid);
		}

		public ArrayList<String> oidList() {
			return arrayOid;
		}

		public Members oid(String oid) {
			Members members;
			lastOid = oid;

			if(tableOid.containsKey(oid)) {
				members = tableOid.get(oid);
			} else {
				members = new Members();
				tableOid.put(oid, members);
				arrayOid.add(oid);
			}
			return members;
		}
	}
}
