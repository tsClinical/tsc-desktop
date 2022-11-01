package com.fujitsu.tsc.desktop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import com.fujitsu.tsc.desktop.exporter.InvalidOidSyntaxException;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel;
import com.fujitsu.tsc.desktop.util.Config.DatasetType;

public class Utils {
	
	/**
	 * Split given string using the given delimiter and returns trimmed split values.
	 * @param s String to split
	 * @param delimiter Delimiter used for splitting
	 * @return Empty List, or List of trimmed split values. 
	 */
	public static List<String> split(String s, String delimiter) {
		List<String> rtn = new ArrayList<>();
		String[] values = StringUtils.splitByWholeSeparatorPreserveAllTokens(s, delimiter);
		if (values == null) {
			return rtn;
		} else {
			for (String value : values) {
				rtn.add(StringUtils.trim(value));
			}
			return rtn;
		}
	}

	/**
	 * Concatenate given list of strings into a string separated by a given delimiter.
	 * @param values strings to concatenate
	 * @param delimiter Delimiter to split return string;
	 * @return
	 */
	public static String join(List<String> values, String delimiter) {
		String rtn = "";
		if (values == null || values.isEmpty()) {
			return rtn;
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					rtn += delimiter;
				}
				rtn += values.get(i);
			}
			return rtn;
		}
	}

	/**
	 * This method converts natural text into coded text.
	 * (i.e. Remove spaces and symbols except for period(.) and underscore(_).)
	 * @param s
	 * @return
	 */
	public static String codedText(String s) {
		if (StringUtils.isEmpty(s)) {
			return "";
		}
		return RegExUtils.removeAll(s, Pattern.compile("[^a-zA-Z._0-9]"));
	}
	
	/**
	 * Derive domain from dataset name
	 * 
	 * @param type_id
	 * @param dataset_name
	 * @return
	 */
	public static String deriveDomain(DatasetType standard_type, String dataset_name) {
		String domain = "";
		if (StringUtils.isEmpty(dataset_name)) {
			return domain;
		}
		if (standard_type == DatasetType.ADaM) {
			return domain;
		} else {
			if (dataset_name.length() < 2) {
				return domain;
			} else if (dataset_name.startsWith("SUPP") && dataset_name.length() >= 6 && !"SUPPQUAL".equals(dataset_name)) {
				domain = StringUtils.substring(dataset_name, 4, 6);
			} else {
				domain = StringUtils.substring(dataset_name, 0, 2);
			}
		}
		return domain;
	}
	
	/**
	 * Returns if the dataset is a SDTM split dataset
	 * 
	 * @param type_id
	 * @param dataset_name
	 * @return
	 */
	public static boolean isSplitDataset(DatasetType standard_type, String dataset_name) {
		boolean rtn = false;
		if (StringUtils.isEmpty(dataset_name)) {
			return rtn;
		}
		if (standard_type == DatasetType.ADaM) {
			return rtn;
		} else {
			if (dataset_name.length() == 4) {
				rtn = true;
			} else if (dataset_name.startsWith("SUPP") && dataset_name.length() == 8 && !"SUPPQUAL".equals(dataset_name)) {
				rtn = true;
			} else {
				rtn = false;
			}
		}
		return rtn;
	}
}
