/* 
 * Copyright (c) 2022 Fujitsu Limited. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0(the "License").
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.fujitsu.tsc.desktop.importer;

import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDatasetModel.DefineARMDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMDisplayModel.DefineARMDisplayPk;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel;
import com.fujitsu.tsc.desktop.importer.models.DefineARMResultModel.DefineARMResultPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCodelistModel.DefineCodelistPk;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineCommentModel.DefineCommentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDatasetModel.DefineDatasetPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDictionaryModel.DefineDictionaryPk;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel;
import com.fujitsu.tsc.desktop.importer.models.DefineDocumentModel.DefineDocumentPk;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel;
import com.fujitsu.tsc.desktop.importer.models.DefineMethodModel.DefineMethodPk;
import com.fujitsu.tsc.desktop.importer.models.DefineModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStudyModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel;
import com.fujitsu.tsc.desktop.importer.models.DefineValueModel.DefineValuePk;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel;
import com.fujitsu.tsc.desktop.importer.models.DefineVariableModel.DefineVariablePk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.DefineWCPk;
import com.fujitsu.tsc.desktop.importer.models.DefineWCModel.WCCondition;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocType;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.DocumentRef;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorN;
import com.fujitsu.tsc.desktop.importer.models.DefineModel.YorNull;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.DefineStandardPk;
import com.fujitsu.tsc.desktop.importer.models.DefineStandardModel.StandardType;
import com.fujitsu.tsc.desktop.util.Config;
import com.fujitsu.tsc.desktop.util.Config.DatasetType;
import com.fujitsu.tsc.desktop.util.ErrorLog;
import com.fujitsu.tsc.desktop.util.ErrorLog.ErrorLevel;
import com.fujitsu.tsc.desktop.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * This is a class to validate ODM-XML, bind ODM-XML to {@link OdmModel}, and write the {@link OdmModel} to Excel.
 * To use this class, you must call a constructor first, validateSoft() second to bind, and then generateExcel().
 */
public class SdtmAdamSpecImporter {

	private Config config;
	private DefineModel define;
	private Workbook workbook;

	public SdtmAdamSpecImporter(Config config, Workbook workbook) {
		this.config = config;
		this.workbook = workbook;
		this.define = new DefineModel();
	}

	public List<ErrorLog> parse() {
		List<ErrorLog> rtn = new ArrayList<>();
		List<ErrorLog> errors = importDocument(config.defineDocumentTableName);
		ErrorLog error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importMethod(config.defineMethodTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importComment(config.defineCommentTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importStudy(config.defineStudyTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importStandard(config.defineStandardTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importDataset(config.defineDatasetTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importCodelist(config.defineCodelistTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			errors.addAll(checkCodelistDecode());
			rtn.addAll(errors);
		}
		
		errors = importDictionary(config.defineDictionaryTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importVariable(config.defineVariableTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		errors = importValue(config.defineValueTableName);
		error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
		if (error != null) {
			return errors;
		} else {
			rtn.addAll(errors);
		}
		if (config.e2dDatasetType.equals(Config.DatasetType.ADaM) && this.config.e2dIncludeResultMetadata == true) {
			errors = importArmDisplay(config.defineResult1TableName);
			error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
			if (error != null) {
				return errors;
			} else {
				rtn.addAll(errors);
			}
			errors = importArmDataset(config.defineResult2TableName);
			error = errors.stream().filter(o -> o.getErrorLevel()==ErrorLevel.ERROR).findAny().orElse(null);
			if (error != null) {
				return errors;
			} else {
				rtn.addAll(errors);
			}
		}
		/* Update properties in DefineModel that have not been updated from Excel. */
		define.updateHasSupp();
		define.processRepeatN(config.e2dDatasetType);
		define.updateVariableOrdinal();
		
		return rtn;
	}
	
	List<ErrorLog> importDocument(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("ID", true), //
				new ExcelColumn("Type", true), //
				new ExcelColumn("href", true), //
				new ExcelColumn("Title", true), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String document_id = ExcelCell.getAsString(cells.get("ID"));
				if (StringUtils.isEmpty(document_id)) {
					String message = "The 'ID' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("ID");
					rtn.add(error);
					return rtn;
				}
				DefineDocumentPk pk = new DefineDocumentPk(document_id);
				/* Check if the new object is unique. */
				DefineDocumentModel document2 = define.get(pk);
				if (document2 != null) {
					String message = "The row (" + document_id + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				
				DefineDocumentModel document = new DefineDocumentModel(pk);
				document.document_type = DocType.parse(ExcelCell.getAsString(cells.get("Type")));
				document.document_href = ExcelCell.getAsString(cells.get("href"));
				document.document_title = ExcelCell.getAsString(cells.get("Title"));
				document.ordinal = ordinal++;
				document.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				document.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, document);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importMethod(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("OID", true), //
				new ExcelColumn("Name", true), //
				new ExcelColumn("Type", true), //
				new ExcelColumn("Description", true), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("FormalExpression Context", false), //
				new ExcelColumn("FormalExpression Text", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		if (!excel_sheet.exists()) {
			return new ArrayList<>();	//Optional Sheet
		}
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String method_oid = ExcelCell.getAsString(cells.get("OID"));
				if (StringUtils.isEmpty(method_oid)) {
					String message = "The 'OID' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("OID");
					rtn.add(error);
					return rtn;
				}
				DefineMethodPk pk = new DefineMethodPk(method_oid);
				/* Check if the new object is unique. */
				DefineMethodModel method2 = define.get(pk);
				if (method2 != null) {
					String message = "The row (" + method_oid + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				
				DefineMethodModel method = new DefineMethodModel(pk);
				method.ordinal = ordinal++;
				method.method_name = ExcelCell.getAsString(cells.get("Name"));
				method.method_type = ExcelCell.getAsString(cells.get("Type"));
				method.description = ExcelCell.getAsString(cells.get("Description"));
				String description_lang = ExcelCell.getAsString(cells.get("Language"));
				if (StringUtils.isEmpty(description_lang)) {
					description_lang = ExcelCell.getAsString(cells.get("xml:lang"));
				}
				method.description_lang = description_lang;
				List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
				List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
				List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
				List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
				List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
				List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
				List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
				method.document_refs.addAll(document_refs);
				method.formal_expression_context = ExcelCell.getAsString(cells.get("FormalExpression Context"));
				method.formal_expression = ExcelCell.getAsString(cells.get("FormalExpression Text"));
				if ((StringUtils.isEmpty(method.formal_expression_context) && StringUtils.isNotEmpty(method.formal_expression))
						|| (StringUtils.isNotEmpty(method.formal_expression_context) && StringUtils.isEmpty(method.formal_expression))) {
					String message = "'FormalExpression Context' and 'FormalExpression Text' columns must be both empty or both entered. They are ignored.";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("FormalExpression Context, FormalExpression Text");
					rtn.add(error);
				}
				method.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				method.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, method);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importComment(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("OID", true), //
				new ExcelColumn("Comment", true), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		if (!excel_sheet.exists()) {
			return new ArrayList<>();	//Optional Sheet
		}
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String comment_oid = ExcelCell.getAsString(cells.get("OID"));
				if (StringUtils.isEmpty(comment_oid)) {
					String message = "The 'OID' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("OID");
					rtn.add(error);
					return rtn;
				}
				DefineCommentPk pk = new DefineCommentPk(comment_oid);
				/* Check if the new object is unique. */
				DefineCommentModel comment2 = define.get(pk);
				if (comment2 != null) {
					String message = "The row (" + comment_oid + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				
				DefineCommentModel comment = new DefineCommentModel(pk);
				comment.ordinal = ordinal++;
				comment.comment_text = ExcelCell.getAsString(cells.get("Comment"));
				comment.comment_lang = ExcelCell.getAsString(cells.get("Language"));
				List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
				List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
				List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
				List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
				List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
				List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
				List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
				comment.document_refs.addAll(document_refs);
				comment.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				comment.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, comment);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}
	
	List<ErrorLog> importStudy(String sheet_name) {
		
		VerticalExcelSheet excel_sheet = new VerticalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("ODMVersion", false), //Ignored
				new ExcelColumn("FileType", false), //Ignored
				new ExcelColumn("FileOID", false), //
				new ExcelColumn("AsOfDateTime", false), //
				new ExcelColumn("Originator", false), //
				new ExcelColumn("Context", false), //
				new ExcelColumn("StudyOID", false), //
				new ExcelColumn("StudyName", true), //
				new ExcelColumn("StudyDescription", true), //
				new ExcelColumn("ProtocolName", true), //
				new ExcelColumn("MetaDataOID", false), //
				new ExcelColumn("MetaDataName", false), //
				new ExcelColumn("MetaDataDescription", false), //
				new ExcelColumn("DefineVersion", true), //
				new ExcelColumn("StandardName", false), //Ignored in Define-XML 2.0. Deprecated in Define-XML 2.1
				new ExcelColumn("StandardVersion", false), //Deprecated in Define-XML 2.1
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("xml:lang", false), //For backward compatibility
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				DefineStudyModel study = new DefineStudyModel();
//				study.odm_version = ExcelCell.getAsString(cells.get("ODMVersion"));
//				study.file_type = ExcelCell.getAsString(cells.get("FileType"));
				study.file_oid = ExcelCell.getAsString(cells.get("FileOID"));
				study.as_of_date_time = ExcelCell.getAsString(cells.get("AsOfDateTime"));
				study.originator = ExcelCell.getAsString(cells.get("Originator"));
				String context = ExcelCell.getAsString(cells.get("Context"));
				study.context = (StringUtils.isEmpty(context) ? "Submission" : context);
				study.study_oid = ExcelCell.getAsString(cells.get("StudyOID"));
				study.study_name = ExcelCell.getAsString(cells.get("StudyName"));
				study.study_description = ExcelCell.getAsString(cells.get("StudyDescription"));
				study.protocol_name = ExcelCell.getAsString(cells.get("ProtocolName"));
				study.metadata_oid = ExcelCell.getAsString(cells.get("MetaDataOID"));
				study.metadata_name = ExcelCell.getAsString(cells.get("MetaDataName"));
				study.metadata_description = ExcelCell.getAsString(cells.get("MetaDataDescription"));
				study.define_version = ExcelCell.getAsString(cells.get("DefineVersion"));
				study.standard_name = ExcelCell.getAsString(cells.get("StandardName"));
				study.standard_version = ExcelCell.getAsString(cells.get("StandardVersion"));
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (!"2.0.0".equals(config.e2dDefineVersion)) {
					if (StringUtils.isNotEmpty(comment_oid)) {
						study.comment_oid = comment_oid;
						if (StringUtils.isNotEmpty(str_comment)) {
							String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Comment");
							rtn.add(error);
						}
					} else if (StringUtils.isNotEmpty(str_comment)) {
						study.comment_oid = DefineCommentModel.createCommentOID(study);
						/* Create DefineCommentModel object */
						DefineCommentPk comment_pk = new DefineCommentPk(study.comment_oid);
						DefineCommentModel comment = new DefineCommentModel(comment_pk);
						comment.comment_text = str_comment;
						String comment_lang = ExcelCell.getAsString(cells.get("Language"));
						if (StringUtils.isEmpty(comment_lang)) {
							comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
						}
						comment.comment_lang = comment_lang;
						List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
						List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
						List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
						List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
						List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
						List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
						List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
						comment.document_refs.addAll(document_refs);
						define.put(comment_pk, comment);
					} else {
						String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
						if (StringUtils.isNotEmpty(document_id)) {
							String message = "The 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Comment");
							rtn.add(error);
						}
					}
				}
				/* --> End Comment */
				study.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				study.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));
				define.put(study);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importStandard(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Name", true), //
				new ExcelColumn("Type", true), //
				new ExcelColumn("Publishing Set", true), //
				new ExcelColumn("Version", true), //
				new ExcelColumn("Status", true), //
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("xml:lang", false), //For backward compatibility
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		if (!excel_sheet.exists()) {
			return new ArrayList<>();	//Optional Sheet
		}
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}
		
		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String standard_name = ExcelCell.getAsString(cells.get("Name"));
				String standard_version = ExcelCell.getAsString(cells.get("Version"));
				String publishing_set = ExcelCell.getAsString(cells.get("Publishing Set"));
				if (StringUtils.isEmpty(standard_name) || StringUtils.isEmpty(standard_version)) {
					String message = "The 'Name' and 'Version' columns are required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				DefineStandardPk pk = new DefineStandardPk(DefineStandardModel.createOid(standard_name, standard_version, publishing_set));
				/* Check if the new object is unique. */
				DefineStandardModel standard2 = define.get(pk);
				if (standard2 != null) {
					String message = "The row (" + standard_name + "/" + standard_version + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}

				DefineStandardModel standard = new DefineStandardModel(pk);
				standard.standard_name = standard_name;
				standard.standard_version = standard_version;
				standard.publishing_set = publishing_set;
				standard.standard_type = StandardType.parse(ExcelCell.getAsString(cells.get("Type")));
				standard.standard_status = ExcelCell.getAsString(cells.get("Status"));
				standard.ordinal = ordinal++;
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("CommentOID");
						rtn.add(error);
					} else {
						standard.comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					standard.comment_oid = DefineCommentModel.createCommentOID(standard);
					/* Create DefineCommentModel object */
					DefineCommentPk comment_pk = new DefineCommentPk(standard.comment_oid);
					DefineCommentModel comment = new DefineCommentModel(comment_pk);
					comment.comment_text = str_comment;
					String comment_lang = ExcelCell.getAsString(cells.get("Language"));
					if (StringUtils.isEmpty(comment_lang)) {
						comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
					}
					comment.comment_lang = comment_lang;
					List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
					List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
					List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
					List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
					List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
					List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
					List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
					comment.document_refs.addAll(document_refs);
					define.put(comment_pk, comment);
				} else {
					String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					if (StringUtils.isNotEmpty(document_id)) {
						String message = "The 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */
				standard.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				standard.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));
				
				define.put(pk, standard);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importDataset(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Domain", false), //
				new ExcelColumn("Dataset Name", true), //
				new ExcelColumn("Description", true), //
				new ExcelColumn("TranslatedText", false), //For backward compatibility
				new ExcelColumn("No Data", false), //
				new ExcelColumn("SASDatasetName", false), //
				new ExcelColumn("Repeating", true), //
				new ExcelColumn("IsReferenceData", true), //
				new ExcelColumn("Purpose", true), //
				new ExcelColumn("Standard", false), //
				new ExcelColumn("Structure", true), //
				new ExcelColumn("Class", true), //
				new ExcelColumn("Subclass", false), //
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("xml:lang", false), //For backward compatibility
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("Alias", false), //
				new ExcelColumn("Leaf href", false), //
				new ExcelColumn("href", false), //For backward compatibility
				new ExcelColumn("Leaf Title", false), //
				new ExcelColumn("Title", false), //For backward compatibility
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String dataset_name = ExcelCell.getAsString(cells.get("Dataset Name"));
				if (StringUtils.isEmpty(dataset_name)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Dataset Name' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("Dataset Name");
					rtn.add(error);
					return rtn;
				}
				DefineDatasetPk pk = new DefineDatasetPk(dataset_name);
				/* Check if the new object is unique. */
				DefineDatasetModel dataset2 = define.get(pk);
				if (dataset2 != null) {
					String message = "The row (" + dataset_name + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}

				DefineDatasetModel dataset = new DefineDatasetModel(pk);
				String domain = ExcelCell.getAsString(cells.get("Domain"));
				if (!(config.e2dDatasetType == Config.DatasetType.ADaM)) {
					if (StringUtils.isEmpty(domain)) {
						dataset.domain = Utils.deriveDomain(config.e2dDatasetType, dataset_name);
					} else {
						dataset.domain = domain;
					}
				}
				dataset.ordinal = ordinal++;
				dataset.description = ExcelCell.getAsString(cells.get("Description"));
				if (StringUtils.isEmpty(dataset.description)) {
					dataset.description = ExcelCell.getAsString(cells.get("TranslatedText"));
				}
				dataset.has_no_data = YorNull.parse(ExcelCell.getAsString(cells.get("No Data")));	//
				String sas_dataset_name = ExcelCell.getAsString(cells.get("SASDatasetName"));
				if (StringUtils.isEmpty(sas_dataset_name)) {
					sas_dataset_name = dataset.dataset_name;
				}
				dataset.sas_dataset_name = sas_dataset_name;
				dataset.repeating = YorN.parse(ExcelCell.getAsString(cells.get("Repeating")));
				dataset.is_reference_data = YorN.parse(ExcelCell.getAsString(cells.get("IsReferenceData")));
				dataset.purpose = ExcelCell.getAsString(cells.get("Purpose"));
				if (!"2.0.0".equals(config.e2dDefineVersion)) {
					String str_standard = ExcelCell.getAsString(cells.get("Standard"));
					if (StringUtils.isNotEmpty(str_standard)) {
						/* If Standard is populated, then try to find its OID based on standard name and version. */
						String standard_oid = "";
						List<DefineStandardModel> standards = define.listSortedStandard();
						for (DefineStandardModel standard : standards) {
							if (standard.standard_type == StandardType.IG) {
								String standard_id1 = Utils.codedText(standard.standard_name + standard.standard_version).toLowerCase();
								String standard_id2 = Utils.codedText(str_standard).toLowerCase();
								if (standard_id1.equals(standard_id2)) {
									standard_oid = standard.toOid();
									break;
								}
							}
						}
						if (StringUtils.isEmpty(standard_oid)) {
							String message = "The value in the 'Standard' column is ignored because it is not found in the " + config.defineStandardTableName + " sheet. Check if both Standard Name and Standard Version are entered.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Standard");
							rtn.add(error);
						} else {
							dataset.standard_oid = standard_oid;
						}
					}
				}
				dataset.structure = ExcelCell.getAsString(cells.get("Structure"));
				dataset.dataset_class = ExcelCell.getAsString(cells.get("Class"));
				dataset.dataset_subclass = ExcelCell.getAsString(cells.get("Subclass"));
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("CommentOID");
						rtn.add(error);
					} else {
						dataset.comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					dataset.comment_oid = DefineCommentModel.createCommentOID(dataset);
					/* Create DefineCommentModel object */
					DefineCommentPk comment_pk = new DefineCommentPk(dataset.comment_oid);
					DefineCommentModel comment = new DefineCommentModel(comment_pk);
					comment.comment_text = str_comment;
					String comment_lang = ExcelCell.getAsString(cells.get("Language"));
					if (StringUtils.isEmpty(comment_lang)) {
						comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
					}
					comment.comment_lang = comment_lang;
					List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
					List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
					List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
					List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
					List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
					List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
					List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
					comment.document_refs.addAll(document_refs);
					define.put(comment_pk, comment);
				} else {
					String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					if (StringUtils.isNotEmpty(document_id)) {
						String message = "The 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */
				dataset.alias_name = ExcelCell.getAsString(cells.get("Alias"));
				/* Leaf href */
				String leaf_href = ExcelCell.getAsString(cells.get("Leaf href"));
				if (StringUtils.isEmpty(leaf_href)) {
					leaf_href = ExcelCell.getAsString(cells.get("href"));
				}
				if (StringUtils.isEmpty(leaf_href)) {
					leaf_href = StringUtils.lowerCase(dataset.dataset_name) + ".xpt";
				}
				dataset.leaf_href = leaf_href;
				/* Leaf Title */
				String leaf_title = ExcelCell.getAsString(cells.get("Leaf Title"));
				if (StringUtils.isEmpty(leaf_title)) {
					leaf_title = ExcelCell.getAsString(cells.get("Title"));
				}
				if (StringUtils.isEmpty(leaf_title)) {
					leaf_title = leaf_href;
				}
				dataset.leaf_title = leaf_title;
				dataset.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				dataset.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, dataset);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importCodelist(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Codelist ID", true), //
				new ExcelColumn("Codelist Code", true), //
				new ExcelColumn("Codelist Label", true), //
				new ExcelColumn("DataType", true), //
				new ExcelColumn("SASFormatName", true), //
				new ExcelColumn("Standard", false), //
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("Code", true), //
				new ExcelColumn("User Code", false), //
				new ExcelColumn("Order Number", true), //
				new ExcelColumn("Rank", true), //
				new ExcelColumn("ExtendedValue", true), //
				new ExcelColumn("Submission Value", true), //
				new ExcelColumn("Decode", true), //
				new ExcelColumn("Translated Text", false), //
				new ExcelColumn("Decode Language", false), //
				new ExcelColumn("xml:lang", false), //For backward compatibility
				new ExcelColumn("Alias Context", false), //
				new ExcelColumn("Alias Name", false), //
				new ExcelColumn("User Note 1", true), //
				new ExcelColumn("User Note 2", true) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			String last_codelist_id = "";
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String codelist_id = ExcelCell.getAsString(cells.get("Codelist ID"));
				if (StringUtils.isEmpty(codelist_id)) {
					String message = "The 'Codelist ID' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("Codelist ID");
					rtn.add(error);
					return rtn;
				}
				String submission_value = ExcelCell.getAsString(cells.get("Submission Value"));
				if (StringUtils.isEmpty(submission_value)) {
					String message = "The 'Submission Value' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("Submission Value");
					rtn.add(error);
					return rtn;
				}
				DefineCodelistPk pk = new DefineCodelistPk(codelist_id, submission_value);
				/* Check if the new object is unique. */
				DefineCodelistModel codelist_item2 = define.get(pk);
				if (codelist_item2 != null) {
					String message = "The row (" + codelist_id + "/" + submission_value + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				
				DefineCodelistModel codelist_item = new DefineCodelistModel(pk);
				if (StringUtils.equals(codelist_item.codelist_id, last_codelist_id)) {
					codelist_item.ordinal = ordinal++;
				} else {
					codelist_item.ordinal = 1;
					last_codelist_id = codelist_item.codelist_id;
				}
				
				codelist_item.codelist_code = ExcelCell.getAsString(cells.get("Codelist Code"));
				codelist_item.codelist_label = ExcelCell.getAsString(cells.get("Codelist Label"));
				codelist_item.data_type = ExcelCell.getAsString(cells.get("DataType"));
				codelist_item.sas_format_name = ExcelCell.getAsString(cells.get("SASFormatName"));
				if (!"2.0.0".equals(config.e2dDefineVersion)) {
				String str_standard = ExcelCell.getAsString(cells.get("Standard"));
					if (StringUtils.isEmpty(codelist_item.codelist_code)) {
						if (StringUtils.isNotEmpty(str_standard)) {
							String message = "The 'Standard' column is ignored because 'Codelist Code' is empty (indicating Non Standard).";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Standard");
							rtn.add(error);
						}
					} else {
						if (StringUtils.isEmpty(str_standard)) {
							/* If Standard is empty, then try to populate automatically if only one CT (except for Define-XML) is found in the standards. */
							List<DefineStandardModel> standards = define.listSortedStandard().stream()
									.filter(o -> (o.standard_type == StandardType.CT && !"DEFINE-XML".equals(o.publishing_set)))
									.collect(Collectors.toList());
							if (standards.size() == 1) {
								codelist_item.standard_oid = standards.get(0).toOid();
							} else {
								if (!"2.0.0".equals(config.e2dDefineVersion)) {
									String message = "Failed to identify a Controled Terminology to be assigned to the codelist. Make sure to fill out the 'Standard' column.";
									ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
									error.setColumnName("Standard");
									rtn.add(error);
								}
							}
						} else {
							/* If Standard is populated, then try to find its OID based on publishing set and version. */
							String standard_oid = "";
							List<DefineStandardModel> standards = define.listSortedStandard();
							for (DefineStandardModel standard : standards) {
								if (standard.standard_type == StandardType.CT) {
									String ct_id1_short = Utils.codedText(standard.publishing_set + standard.standard_version).toLowerCase();
									String ct_id1_long = Utils.codedText(standard.standard_name).toLowerCase() + ct_id1_short;
									String ct_id2 = Utils.codedText(str_standard).toLowerCase();
									if (ct_id1_short.equals(ct_id2) || ct_id1_long.equals(ct_id2)) {
										standard_oid = standard.toOid();
										break;
									}
								}
							}
							if (StringUtils.isEmpty(standard_oid)) {
								String message = "The value in the 'Standard' column is ignored because it is not found in the " + config.defineStandardTableName + " sheet. Check if both Publishing Set and Standard Version are entered.";
								ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
								error.setColumnName("Standard");
								rtn.add(error);
							} else {
								codelist_item.standard_oid = standard_oid;
							}
						}
					}
				}
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("CommentOID");
						rtn.add(error);
					} else {
						codelist_item.comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					codelist_item.comment_oid = DefineCommentModel.createCommentOID(codelist_item);
					/* Create DefineCommentModel object */
					DefineCommentPk comment_pk = new DefineCommentPk(codelist_item.comment_oid);
					DefineCommentModel comment = new DefineCommentModel(comment_pk);
					comment.comment_text = str_comment;
					String comment_lang = ExcelCell.getAsString(cells.get("Language"));
					if (StringUtils.isEmpty(comment_lang)) {
						comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
					}
					comment.comment_lang = comment_lang;
					List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
					List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
					List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
					List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
					List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
					List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
					List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
					comment.document_refs.addAll(document_refs);
					define.put(comment_pk, comment);
				} else {
					String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					if (StringUtils.isNotEmpty(document_id)) {
						String message = "The 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */
				codelist_item.code = ExcelCell.getAsString(cells.get("Code"));
				codelist_item.order_number = ExcelCell.getAsInteger(cells.get("Order Number"));
				codelist_item.rank = ExcelCell.getAsInteger(cells.get("Rank"));
				codelist_item.extended_value = YorNull.parse(ExcelCell.getAsString(cells.get("ExtendedValue")));
				String str_decode = ExcelCell.getAsString(cells.get("Decode"));
				if (StringUtils.isEmpty(str_decode)) {
					str_decode = ExcelCell.getAsString(cells.get("Translated Text"));
				}
				codelist_item.decode = str_decode;
				String xml_lang = ExcelCell.getAsString(cells.get("Decode Language"));
				if (StringUtils.isEmpty(xml_lang)) {
					xml_lang = ExcelCell.getAsString(cells.get("xml:lang"));
				}
				codelist_item.xml_lang = xml_lang;
				codelist_item.alias_context = ExcelCell.getAsString(cells.get("Alias Context"));
				codelist_item.alias_name = ExcelCell.getAsString(cells.get("Alias Name"));
				codelist_item.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				codelist_item.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, codelist_item);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}
	
	/* Check if all items in a codelist is either all enumerated or coded. Remove decode if mixed. */
	List<ErrorLog> checkCodelistDecode() {
		List<ErrorLog> rtn = new ArrayList<>();
		List<DefineCodelistModel> codelist_items = define.listSortedCodelist();
		List<String> codelist_ids = new ArrayList<>();
		for (DefineCodelistModel codelist_item : codelist_items) {
			if (!codelist_ids.contains(codelist_item.codelist_id)) {
				codelist_ids.add(codelist_item.codelist_id);
			}
		}
		for (String codelist_id : codelist_ids) {
			List<DefineCodelistModel> filtered_codelist_items = codelist_items.stream().filter(o -> StringUtils.equals(o.codelist_id, codelist_id)).collect(Collectors.toList());
			List<DefineCodelistModel> coded_codelist_items = filtered_codelist_items.stream().filter(o -> StringUtils.isNotEmpty(o.codelist_id)).collect(Collectors.toList());
			if (coded_codelist_items.size() > 0 && coded_codelist_items.size() != filtered_codelist_items.size()) {
				String message = "The 'Decode' must be all empty or all filled out for each Codelist ID. 'Decode' for the " + codelist_id + " codelist is ignored.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("Decode");
				rtn.add(error);
				for (int i = 0; i < filtered_codelist_items.size(); i++) {
					DefineCodelistModel filtered_codelist_item = filtered_codelist_items.get(i);
					filtered_codelist_item.decode = "";
				}
			}
		}
		return rtn;
	}

	List<ErrorLog> importVariable(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Domain", false), //Ignored
				new ExcelColumn("Dataset Name", true), //
				new ExcelColumn("Variable Name", true), //
				new ExcelColumn("Is SUPP", false), //
				new ExcelColumn("Repeat N", false), //
				new ExcelColumn("Label", true), //
				new ExcelColumn("No Data", false), //
				new ExcelColumn("Non Standard", false), //
				new ExcelColumn("Mandatory", true), //
				new ExcelColumn("Key Sequence", true), //
				new ExcelColumn("Sort Order", false), //
				new ExcelColumn("DataType", true), //
				new ExcelColumn("Length", true), //
				new ExcelColumn("SignificantDigits", true), //
				new ExcelColumn("SASFieldName", true), //
				new ExcelColumn("DisplayFormat", true), //
				new ExcelColumn("Codelist", true), //
				new ExcelColumn("Origin", true), //
				new ExcelColumn("Source", false), //
				new ExcelColumn("Evaluator", false), //
				new ExcelColumn("CRF ID", false), //
				new ExcelColumn("CRF Page Type", false), //
				new ExcelColumn("CRF Page Reference", false), //
				new ExcelColumn("CRF First Page", false), //
				new ExcelColumn("CRF Last Page", false), //
				new ExcelColumn("CRF Page Title", false), //
				new ExcelColumn("MethodOID", false), //
				new ExcelColumn("Derivation Type", false), //
				new ExcelColumn("Predecessor", false), //
				new ExcelColumn("Derivation", false), //
				new ExcelColumn("Predecessor/Derivation", false), //For backward compatibility
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("xml:lang", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("Role", false), //
				new ExcelColumn("Role codelist", false), //
				new ExcelColumn("FormalExpression Context", false), //
				new ExcelColumn("FormalExpression Text", false), //
				new ExcelColumn("FormalExpression", false), //For backward compatibility
				new ExcelColumn("Alias Context", false), //
				new ExcelColumn("Alias Name", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			String last_dataset_name = "";
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String dataset_name = ExcelCell.getAsString(cells.get("Dataset Name"));
				if (StringUtils.isEmpty(dataset_name)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Dataset Name' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("Dataset Name");
					rtn.add(error);
					return rtn;
				}
				DefineDatasetModel dataset = define.get(new DefineDatasetPk(dataset_name));
				if (dataset == null) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Dataset Name' is not found in the " + config.defineDatasetTableName + " sheet. Skipping the row...");
					error.setColumnName("Dataset Name");
					rtn.add(error);
					return rtn;
				}
				String variable_name = ExcelCell.getAsString(cells.get("Variable Name"));
				if (StringUtils.isEmpty(variable_name)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Variable Name' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("Variable Name");
					rtn.add(error);
					return rtn;
				}
				DefineVariablePk pk = new DefineVariablePk(dataset_name, DefineVariableModel.createOid(dataset_name, variable_name));
				/* Check if the new object is unique. */
				DefineVariableModel variable2 = define.get(pk);
				if (variable2 != null) {
					String message = "The row (" + dataset_name + "/" + variable_name + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}

				DefineVariableModel variable = new DefineVariableModel(pk);
				variable.variable_name = variable_name;
				if (StringUtils.equals(last_dataset_name, dataset_name)) {
					variable.ordinal = ordinal++;
				} else {
					ordinal = 1;
					variable.ordinal = ordinal++;
					last_dataset_name = dataset_name;
				}
				variable.is_supp = YorN.parse(ExcelCell.getAsString(cells.get("Is SUPP")));	//YorN.No if null
				int repeat_n = ExcelCell.getAsInteger(cells.get("Repeat N"));
				if (repeat_n > 99) {
					String message = "The 'Repeat N' value must be less than 100. The 'Repeat N' is ignored.";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("Repeat N");
					rtn.add(error);
					repeat_n = 0;
				}
				variable.repeat_n = repeat_n;
				variable.variable_label = ExcelCell.getAsString(cells.get("Label"));
				variable.has_no_data = YorNull.parse(ExcelCell.getAsString(cells.get("No Data")));
				variable.has_no_data_derived = variable.deriveHasNoData(define);
				variable.is_non_standard = YorNull.parse(ExcelCell.getAsString(cells.get("Non Standard")));
				variable.mandatory = YorN.parse(ExcelCell.getAsString(cells.get("Mandatory")));
				variable.key_sequence = ExcelCell.getAsString(cells.get("Key Sequence"));
				variable.sort_order = ExcelCell.getAsString(cells.get("Sort Order"));	//Blank if null
				variable.data_type = ExcelCell.getAsString(cells.get("DataType"));
				variable.length = ExcelCell.getAsString(cells.get("Length"));
				if (variable.repeat_n > 0) {
					List<String> length_0_n = Utils.split(variable.length, config.valueDelimiter);
					if (length_0_n.size() == 1) {
						variable.repeat_n_length = new ArrayList<>();
					} else if (variable.repeat_n + 1 == length_0_n.size()) {
						variable.repeat_n_length = length_0_n;
					} else {
						String message = "The 'Length' should be a list of Length split by \"" + config.valueDelimiter + "\". The 'Length' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Length");
						rtn.add(error);
					}
				}
				variable.significant_digits = ExcelCell.getAsString(cells.get("SignificantDigits"));
				variable.sas_field_name = ExcelCell.getAsString(cells.get("SASFieldName"));
				variable.display_format = ExcelCell.getAsString(cells.get("DisplayFormat"));
				String str_codelist = ExcelCell.getAsString(cells.get("Codelist"));
				if (StringUtils.isNotEmpty(str_codelist)) {
					DefineCodelistModel codelist = define.listSortedCodelist().stream().filter(o -> StringUtils.equals(o.codelist_id, str_codelist)).findAny().orElse(null);
					DefineDictionaryModel dictionary = define.listSortedDictionary().stream().filter(o -> StringUtils.equals(o.dictionary_id, str_codelist)).findAny().orElse(null);
					if (codelist == null && dictionary == null) {
						String message = "The '" + str_codelist + "' is not found in the " + config.defineCodelistTableName + " or " + config.defineDictionaryTableName + " sheet. The Codelist is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Codelist");
						rtn.add(error);
					} else {
						variable.codelist = str_codelist;
					}
				}
				variable.origin = ExcelCell.getAsString(cells.get("Origin"));
				variable.source = ExcelCell.getAsString(cells.get("Source"));
				variable.evaluator = ExcelCell.getAsString(cells.get("Evaluator"));
				variable.crf_id = ExcelCell.getAsString(cells.get("CRF ID"));
				variable.crf_page_type = ExcelCell.getAsString(cells.get("CRF Page Type"));
				variable.crf_page_reference = ExcelCell.getAsString(cells.get("CRF Page Reference"));
				variable.crf_first_page = ExcelCell.getAsString(cells.get("CRF First Page"));
				variable.crf_last_page = ExcelCell.getAsString(cells.get("CRF Last Page"));
				variable.crf_page_title = ExcelCell.getAsString(cells.get("CRF Page Title"));
				variable.predecessor = ExcelCell.getAsString(cells.get("Predecessor"));	//Blank if null
				String predecessor_derivation = ExcelCell.getAsString(cells.get("Predecessor/Derivation"));
				if (StringUtils.isEmpty(variable.predecessor) && !"Derived".equals(variable.origin)) {
					variable.predecessor = predecessor_derivation;
				}
				/* Begin Method --> */
				String method_oid = ExcelCell.getAsString(cells.get("MethodOID"));
				String derivation = ExcelCell.getAsString(cells.get("Derivation"));
				DefineMethodModel method = null;
				if (StringUtils.isNotEmpty(method_oid)) {
					method = define.listSortedMethod().stream().filter(o -> StringUtils.equals(o.oid, method_oid)).findFirst().orElse(null);
					if (method == null) {
						String message = "The 'MethodOID' is not found in the " + config.defineMethodTableName + " sheet. The 'MethodOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("MethodOID");
						rtn.add(error);
					} else {
						variable.method_oid = method_oid;
					}
					if (StringUtils.isNotEmpty(derivation)) {
						String message = "Either of the 'MethodOID' or 'Derivation' column can be entered. The 'Derivation' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Derivation");
						rtn.add(error);
					} else if (StringUtils.isNotEmpty(predecessor_derivation)) {
						String message = "Either of the 'MethodOID' or 'Predecessor/Derivation' column can be entered. The 'Predecessor/Derivation' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Predecessor/Derivation");
						rtn.add(error);
					}
				} else {
					if (StringUtils.isNotEmpty(derivation) || ("Derived".equals(variable.origin) && StringUtils.isNotEmpty(predecessor_derivation))) {
						variable.method_oid = DefineMethodModel.createMethodOID(variable);
						/* Create DefineMethodModel object */
						DefineMethodPk method_pk = new DefineMethodPk(variable.method_oid);
						method = new DefineMethodModel(method_pk);
						method.method_name = "Algorithm to derive " + variable_name;
						method.method_type = ExcelCell.getAsString(cells.get("Derivation Type"));
						method.description = (StringUtils.isNotEmpty(derivation) ? derivation : predecessor_derivation);
						method.description_lang = ExcelCell.getAsString(cells.get("Language"));
						if (StringUtils.isEmpty(method.description_lang)) {
							method.description_lang = ExcelCell.getAsString(cells.get("xml:lang"));
						}
						List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
						List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
						List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
						List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
						List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
						List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
						List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
						method.document_refs.addAll(document_refs);
						method.formal_expression_context = ExcelCell.getAsString(cells.get("FormalExpression Context"));
						method.formal_expression = ExcelCell.getAsString(cells.get("FormalExpression Text"));
						if (StringUtils.isEmpty(method.formal_expression)) {
							method.formal_expression = ExcelCell.getAsString(cells.get("Formal expression"));
						}
						define.put(method_pk, method);
					}
				}
				/* --> End Method */
				/* Begin Comment: Note that Comment for Variable/Value is derived differently from the other sections. --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("CommentOID");
						rtn.add(error);
					} else {
						variable.comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					if (method == null) {
						variable.comment_oid = DefineCommentModel.createCommentOID(variable);
						/* Create DefineCommentModel object */
						DefineCommentPk comment_pk = new DefineCommentPk(variable.comment_oid);
						DefineCommentModel comment = new DefineCommentModel(comment_pk);
						comment.comment_text = str_comment;
						String comment_lang = ExcelCell.getAsString(cells.get("Language"));
						if (StringUtils.isEmpty(comment_lang)) {
							comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
						}
						comment.comment_lang = comment_lang;
						List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
						List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
						List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
						List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
						List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
						List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
						List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
						comment.document_refs.addAll(document_refs);
						define.put(comment_pk, comment);
					} else {
						String message = "Either of the 'Derivation' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else {	//Comment is empty.
					String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					if (StringUtils.isNotEmpty(document_id) && method == null) {	//Predecessor is also empty.
						String message = "The 'Derivation' or 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */
				
				variable.role = ExcelCell.getAsString(cells.get("Role"));
				String str_role_codelist = ExcelCell.getAsString(cells.get("Role codelist"));
				if (StringUtils.isNotEmpty(str_role_codelist)) {
					DefineCodelistModel role_codelist = define.listSortedCodelist().stream().filter(o -> StringUtils.equals(o.codelist_id, str_role_codelist)).findAny().orElse(null);
					if (role_codelist == null) {
						String message = "The '" + str_role_codelist + "' is not found in the " + config.defineCodelistTableName + " sheet. The Role Codelist is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Role codelist");
						rtn.add(error);
					} else {
						variable.role_codelist = str_role_codelist;
					}
				}
				variable.alias_context = ExcelCell.getAsString(cells.get("Alias Context"));
				variable.alias_name = ExcelCell.getAsString(cells.get("Alias Name"));
				variable.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				variable.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, variable);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	List<ErrorLog> importValue(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Domain", false), //Ignored
				new ExcelColumn("Dataset Name", true), //
				new ExcelColumn("Variable Name", true), //
				new ExcelColumn("Value Name", true), //
				new ExcelColumn("Value Key", false), //
				new ExcelColumn("Label", true), //
				new ExcelColumn("No Data", false), //
				new ExcelColumn("Mandatory", false), //
				new ExcelColumn("Key Sequence", false), //Ignored
				new ExcelColumn("DataType", true), //
				new ExcelColumn("Length", true), //
				new ExcelColumn("SignificantDigits", true), //
				new ExcelColumn("SASFieldName", true), //
				new ExcelColumn("DisplayFormat", true), //
				new ExcelColumn("Codelist", true), //
				new ExcelColumn("Origin", true), //
				new ExcelColumn("Source", false), //
				new ExcelColumn("CRF ID", false), //
				new ExcelColumn("CRF Page Type", false), //
				new ExcelColumn("CRF Page Reference", false), //
				new ExcelColumn("CRF First Page", false), //
				new ExcelColumn("CRF Last Page", false), //
				new ExcelColumn("CRF Page Title", false), //
				new ExcelColumn("MethodOID", false), //
				new ExcelColumn("Derivation Type", true), //
				new ExcelColumn("Predecessor", false), //
				new ExcelColumn("Derivation", false), //
				new ExcelColumn("Predecessor/Derivation", false), //For backward compatibility
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("xml:lang", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("FormalExpression Context", false), //
				new ExcelColumn("FormalExpression Text", false), //
				new ExcelColumn("FormalExpression", false), //For backward compatibility
				new ExcelColumn("Alias Context", false), //
				new ExcelColumn("Alias Name", false), //
				new ExcelColumn("User Note 1", false), //
				new ExcelColumn("User Note 2", false), //
				new ExcelColumn("W Domain", false), //Deprecated and ignored
				new ExcelColumn("W Dataset Name", false), //Deprecated and ignored
				new ExcelColumn("W Variable Name", false), //Deprecated and ignored
				new ExcelColumn("W Value Key", false), //Deprecated and ignored
				new ExcelColumn("WhereClauseGroupID", false), //
				new ExcelColumn("WhereClauseDataset", true), //
				new ExcelColumn("WhereClauseVariable", true), //
				new ExcelColumn("WhereClauseOperator", true), //
				new ExcelColumn("WhereClauseValue", true), //
				new ExcelColumn("WhereClause CommentOID", false), //
				new ExcelColumn("WhereClause Comment", false), //
				new ExcelColumn("WhereClause Language", false), //
				new ExcelColumn("W xml:lang",false) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			String last_dataset_name = "";
			DefineValuePk last_value_pk;
			DefineWCPk last_wc_pk;
			String last_wc_group_id = "";
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				/* WhereClause is required for each row. */
				String wc_dataset = ExcelCell.getAsString(cells.get("WhereClauseDataset"));
				if (StringUtils.isEmpty(wc_dataset)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseDataset' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("WhereClauseDataset");
					rtn.add(error);
					return rtn;
				}
				String wc_variable = ExcelCell.getAsString(cells.get("WhereClauseVariable"));
				if (StringUtils.isEmpty(wc_variable)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseVariable' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("WhereClauseVariable");
					rtn.add(error);
					return rtn;
				}
				String wc_operator = ExcelCell.getAsString(cells.get("WhereClauseOperator"));
				if (StringUtils.isEmpty(wc_operator)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseOperator' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("WhereClauseOperator");
					rtn.add(error);
					return rtn;
				}
				String wc_value = ExcelCell.getAsString(cells.get("WhereClauseValue"));
				if (StringUtils.isEmpty(wc_value)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseValue' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("WhereClauseValue");
					rtn.add(error);
					return rtn;
				}
				
				/* Value could be empty except for the first row. */
				String dataset_name = ExcelCell.getAsString(cells.get("Dataset Name"));
				String variable_name = ExcelCell.getAsString(cells.get("Variable Name"));
				String value_name = ExcelCell.getAsString(cells.get("Value Name"));
				if (last_value_pk == null) {
					if (StringUtils.isEmpty(dataset_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Dataset Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Dataset Name");
						rtn.add(error);
						return rtn;
					}
					if (StringUtils.isEmpty(variable_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Variable Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Variable Name");
						rtn.add(error);
						return rtn;
					}
					if (StringUtils.isEmpty(value_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Value Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Value Name");
						rtn.add(error);
						return rtn;
					}
				}
				String value_key = ExcelCell.getAsString(cells.get("Value Key"));
				String value_oid = DefineValueModel.createOid(dataset_name, variable_name, value_name, value_key);
				DefineValuePk pk = new DefineValuePk(value_oid);
				/*
				 * Create DefineValueModel
				 */
				if (StringUtils.isNotEmpty(value_oid) && !pk.equals(last_value_pk)) {
					DefineValueModel value2 = define.get(pk);
					/* Check if the new object is unique. The pk is allowed to be the same as the last pk. */
					if (value2 != null) {
						String message = "The row (" + dataset_name + "/" + variable_name + "/" + (StringUtils.isEmpty(value_key) ? value_name : value_key) + ") is duplicated. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					DefineVariableModel variable = define.get(new DefineVariablePk(dataset_name, DefineVariableModel.createOid(dataset_name, variable_name)));
					if (variable == null) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The pair of 'Dataset Name' and 'Variable Name' is not found in the " + config.defineVariableTableName + " sheet. Skipping the row...");
						error.setColumnName("Dataset Name/Variable Name");
						rtn.add(error);
						return rtn;
					}
					
					DefineValueModel value = new DefineValueModel(pk);
					value.dataset_name = dataset_name;
					value.variable_name = variable_name;
					value.value_name = value_name;
					value.value_key = value_key;
					if (StringUtils.equals(last_dataset_name, dataset_name)) {
						value.ordinal = ordinal++;
					} else {
						ordinal = 1;
						value.ordinal = ordinal++;
						last_dataset_name = dataset_name;
					}
					value.value_label = ExcelCell.getAsString(cells.get("Label"));
					value.has_no_data = YorNull.parse(ExcelCell.getAsString(cells.get("No Data")));
					value.has_no_data_derived = value.deriveHasNoData(define);
					value.mandatory = YorN.parse(ExcelCell.getAsString(cells.get("Mandatory")));
					value.data_type = ExcelCell.getAsString(cells.get("DataType"));
					value.length = ExcelCell.getAsString(cells.get("Length"));
					value.significant_digits = ExcelCell.getAsString(cells.get("SignificantDigits"));
					value.sas_field_name = ExcelCell.getAsString(cells.get("SASFieldName"));
					value.display_format = ExcelCell.getAsString(cells.get("DisplayFormat"));
					String str_codelist = ExcelCell.getAsString(cells.get("Codelist"));
					if (StringUtils.isNotEmpty(str_codelist)) {
						DefineCodelistModel codelist = define.listSortedCodelist().stream().filter(o -> StringUtils.equals(o.codelist_id, str_codelist)).findAny().orElse(null);
						DefineDictionaryModel dictionary = define.listSortedDictionary().stream().filter(o -> StringUtils.equals(o.dictionary_id, str_codelist)).findAny().orElse(null);
						if (codelist == null && dictionary == null) {
							String message = "The '" + str_codelist + "' is not found in the " + config.defineCodelistTableName + " or " + config.defineDictionaryTableName + " sheet. The Codelist is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Codelist");
							rtn.add(error);
						} else {
							value.codelist = str_codelist;
						}
					}
					value.origin = ExcelCell.getAsString(cells.get("Origin"));
					value.source = ExcelCell.getAsString(cells.get("Source"));
					value.crf_id = ExcelCell.getAsString(cells.get("CRF ID"));
					value.crf_page_type = ExcelCell.getAsString(cells.get("CRF Page Type"));
					value.crf_page_reference = ExcelCell.getAsString(cells.get("CRF Page Reference"));
					value.crf_first_page = ExcelCell.getAsString(cells.get("CRF First Page"));
					value.crf_last_page = ExcelCell.getAsString(cells.get("CRF Last Page"));
					value.crf_page_title = ExcelCell.getAsString(cells.get("CRF Page Title"));
					value.predecessor = ExcelCell.getAsString(cells.get("Predecessor"));	//Blank if null
					String predecessor_derivation = ExcelCell.getAsString(cells.get("Predecessor/Derivation"));
					if (StringUtils.isEmpty(value.predecessor) && !"Derived".equals(value.origin)) {
						value.predecessor = predecessor_derivation;
					}
					/* Begin Method --> */
					String method_oid = ExcelCell.getAsString(cells.get("MethodOID"));
					String derivation = ExcelCell.getAsString(cells.get("Derivation"));
					DefineMethodModel method = null;
					if (StringUtils.isNotEmpty(method_oid)) {
						method = define.listSortedMethod().stream().filter(o -> StringUtils.equals(o.oid, method_oid)).findFirst().orElse(null);
						if (method == null) {
							String message = "The 'MethodOID' is not found in the " + config.defineMethodTableName + " sheet. The 'MethodOID' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("MethodOID");
							rtn.add(error);
						} else {
							value.method_oid = method_oid;
						}
						if (StringUtils.isNotEmpty(derivation)) {
							String message = "Either of the 'MethodOID' or 'Derivation' column can be entered. The 'Derivation' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Derivation");
							rtn.add(error);
						} else if (StringUtils.isNotEmpty(predecessor_derivation)) {
							String message = "Either of the 'MethodOID' or 'Predecessor/Derivation' column can be entered. The 'Predecessor/Derivation' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Predecessor/Derivation");
							rtn.add(error);
						}
					} else {
						if (StringUtils.isNotEmpty(derivation) || ("Derived".equals(value.origin) && StringUtils.isNotEmpty(predecessor_derivation))) {
							value.method_oid = DefineMethodModel.createMethodOID(value);
							/* Create DefineMethodModel object */
							DefineMethodPk method_pk = new DefineMethodPk(value.method_oid);
							method = new DefineMethodModel(method_pk);
							method.method_name = "Algorithm to derive " + variable_name + "." + (StringUtils.isEmpty(value_key) ? value_name : value_key);
							method.method_type = ExcelCell.getAsString(cells.get("Derivation Type"));
							method.description = (StringUtils.isNotEmpty(derivation) ? derivation : predecessor_derivation);
							String description_lang = ExcelCell.getAsString(cells.get("Language"));
							if (StringUtils.isEmpty(description_lang)) {
								method.description_lang = ExcelCell.getAsString(cells.get("xml:lang"));
							}
							List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
							List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
							List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
							List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
							List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
							List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
							List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
							method.document_refs.addAll(document_refs);
							method.formal_expression_context = ExcelCell.getAsString(cells.get("FormalExpression Context"));
							method.formal_expression = ExcelCell.getAsString(cells.get("FormalExpression Text"));
							if (StringUtils.isEmpty(method.formal_expression)) {
								method.formal_expression = ExcelCell.getAsString(cells.get("Formal expression"));
							}
							define.put(method_pk, method);
						}
					}
					/* --> End Method */
					/* Begin Comment: Note that Comment for Variable/Value is derived differently from the other sections. --> */
					String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
					String str_comment = ExcelCell.getAsString(cells.get("Comment"));
					if (StringUtils.isNotEmpty(comment_oid)) {
						DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
						if (comment == null) {
							String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("CommentOID");
							rtn.add(error);
						} else {
							value.comment_oid = comment_oid;
						}
						if (StringUtils.isNotEmpty(str_comment)) {
							String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Comment");
							rtn.add(error);
						}
					} else if (StringUtils.isNotEmpty(str_comment)) {
						if (method == null) {
							value.comment_oid = DefineCommentModel.createCommentOID(value);
							/* Create DefineCommentModel object */
							DefineCommentPk comment_pk = new DefineCommentPk(value.comment_oid);
							DefineCommentModel comment = new DefineCommentModel(comment_pk);
							comment.comment_text = str_comment;
							String comment_lang = ExcelCell.getAsString(cells.get("Language"));
							if (StringUtils.isEmpty(comment_lang)) {
								comment_lang = ExcelCell.getAsString(cells.get("xml:lang"));
							}
							comment.comment_lang = comment_lang;
							List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
							List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
							List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
							List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
							List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
							List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
							List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
							comment.document_refs.addAll(document_refs);
							define.put(comment_pk, comment);
						} else {
							String message = "Either of the 'Predecessor' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Comment");
							rtn.add(error);
						}
					} else {	//Comment is empty.
						String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
						if (StringUtils.isNotEmpty(document_id) && method == null) {	//Derivation is also empty
							String message = "The 'Derivation' or 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Comment");
							rtn.add(error);
						}
					}
					/* --> End Comment */
					value.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
					value.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));
	
					define.put(pk, value);
					last_value_pk = pk;
				}
				
				/*
				 * Create WCCondition for each row.
				 * Create DefineWCModel if it is a new entry (i.e. new Value or new WhereClauseGroupID), or use the last DefineWCModel
				 */
				DefineValueModel last_value = define.get(last_value_pk);
				String wc_group_id = ExcelCell.getAsString(cells.get("WhereClauseGroupID"));
				String wc_oid = DefineWCModel.createOid(last_value.dataset_name, last_value.variable_name, last_value.value_name, last_value.value_key, wc_group_id);
				DefineWCModel wc = null;
				DefineWCPk wc_pk = new DefineWCPk(wc_oid);
				
				if (wc_pk.equals(last_wc_pk) && StringUtils.equals(wc_group_id, last_wc_group_id)) {
					/* Use the last WC */
					wc = define.get(last_wc_pk);
					WCCondition wc_condition = new WCCondition();
					wc_condition.dataset_name = wc_dataset;
					wc_condition.variable_name = wc_variable;
					wc_condition.operator = wc_operator;
					wc_condition.values.addAll(Utils.split(wc_value, config.valueDelimiter));
					wc.wc_conditions.add(wc_condition);
				} else {
					DefineWCModel wc2 = define.get(wc_pk);
					/* Check if the new object is unique. The pk is allowed to be the same as the last pk.  */
					if (wc2 != null) {
						String message = "The row (" + (StringUtils.isEmpty(wc_group_id) ? "" : wc_group_id + "/") + wc_dataset + "/" + wc_variable + "/" + wc_operator + "/" + wc_value + ") is duplicated. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					wc = new DefineWCModel(wc_pk);	//Create new WC
					last_value.where_clause_pks.add(wc_pk);
					/* Begin Comment --> */
					String comment_oid = ExcelCell.getAsString(cells.get("WhereClause CommentOID"));
					String str_comment = ExcelCell.getAsString(cells.get("WhereClause Comment"));
					if (StringUtils.isNotEmpty(comment_oid)) {
						DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
						if (comment == null) {
							String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("CommentOID");
							rtn.add(error);
						} else {
							wc.comment_oid = comment_oid;
						}
						if (StringUtils.isNotEmpty(str_comment)) {
							String message = "Either of the 'WhereClause CommentOID' or 'WhereClause Comment' column can be entered. The 'WhereClause Comment' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("WhereClause Comment");
							rtn.add(error);
						}
					} else if (StringUtils.isNotEmpty(str_comment)) {
						wc.comment_oid = DefineCommentModel.createCommentOID(wc);
						/* Create DefineCommentModel object */
						DefineCommentPk comment_pk = new DefineCommentPk(wc.comment_oid);
						DefineCommentModel comment = new DefineCommentModel(comment_pk);
						comment.comment_text = str_comment;
						String comment_lang = ExcelCell.getAsString(cells.get("WhereClause Language"));
						if (StringUtils.isEmpty(comment_lang)) {
							comment_lang = ExcelCell.getAsString(cells.get("W xml:lang"));
						}
						comment.comment_lang = comment_lang;
						define.put(comment_pk, comment);
					}
					/* --> End Comment */
					WCCondition wc_condition = new WCCondition();
					wc_condition.dataset_name = wc_dataset;
					wc_condition.variable_name = wc_variable;
					wc_condition.operator = wc_operator;
					wc_condition.values.addAll(Utils.split(wc_value, config.valueDelimiter));
					wc.wc_conditions.add(wc_condition);
					define.put(wc_pk, wc);
				}
				last_wc_group_id = wc_group_id;
				last_wc_pk = wc_pk;
				
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}
	
	List<ErrorLog> importDictionary(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Dictionary ID", true), //
				new ExcelColumn("Name", true), //
				new ExcelColumn("DataType", true), //
				new ExcelColumn("Version", true), //
				new ExcelColumn("ref", true), //
				new ExcelColumn("href", true), //
				new ExcelColumn("CommentOID", false), //
				new ExcelColumn("Comment", false), //
				new ExcelColumn("Language", false), //
				new ExcelColumn("DocumentID", false), //
				new ExcelColumn("Document Page Type", false), //
				new ExcelColumn("Document Page Reference", false), //
				new ExcelColumn("Document First Page", false), //
				new ExcelColumn("Document Last Page", false), //
				new ExcelColumn("Document Page Title", false), //
				new ExcelColumn("User Note 1", true), //
				new ExcelColumn("User Note 2", true) //
		});
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}

		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				String dictionary_id = ExcelCell.getAsString(cells.get("Dictionary ID"));
				if (StringUtils.isEmpty(dictionary_id)) {
					String message = "The 'Dictionary ID' column is required and cannot be empty. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					error.setColumnName("Dictionary ID");
					rtn.add(error);
					return rtn;
				}
				DefineDictionaryPk pk = new DefineDictionaryPk(DefineDictionaryModel.createOid(dictionary_id));
				/* Check if the new object is unique. */
				DefineDictionaryModel dictionary2 = define.get(pk);
				if (dictionary2 != null) {
					String message = "The row (" + dictionary_id + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
				}
				
				DefineDictionaryModel dictionary = new DefineDictionaryModel(pk);
				dictionary.dictionary_id = dictionary_id;
				dictionary.ordinal = ordinal++;
				dictionary.dictionary_name = ExcelCell.getAsString(cells.get("Name"));
				dictionary.data_type = ExcelCell.getAsString(cells.get("DataType"));
				dictionary.dictionary_version = ExcelCell.getAsString(cells.get("Version"));
				dictionary.dictionary_ref = ExcelCell.getAsString(cells.get("ref"));
				dictionary.dictionary_href = ExcelCell.getAsString(cells.get("href"));
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("CommentOID");
						rtn.add(error);
					} else {
						dictionary.comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'CommentOID' or 'Comment' column can be entered. The 'Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					dictionary.comment_oid = DefineCommentModel.createCommentOID(dictionary);
					/* Create DefineCommentModel object */
					DefineCommentPk comment_pk = new DefineCommentPk(dictionary.comment_oid);
					DefineCommentModel comment = new DefineCommentModel(comment_pk);
					comment.comment_text = str_comment;
					comment.comment_lang = ExcelCell.getAsString(cells.get("Language"));
					List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
					List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
					List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
					List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Document First Page")), config.valueDelimiter);
					List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Document Last Page")), config.valueDelimiter);
					List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Document Page Title")), config.valueDelimiter);
					List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
					comment.document_refs.addAll(document_refs);
					define.put(comment_pk, comment);
				} else {
					String document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					if (StringUtils.isNotEmpty(document_id)) {
						String message = "The 'Comment' column is required when 'DocumentID' is entered. The 'DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */
				dictionary.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
				dictionary.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));

				define.put(pk, dictionary);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}
	
	List<ErrorLog> importArmDisplay(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Display Name", true), //
				new ExcelColumn("Display Description", true), //
				new ExcelColumn("Display Language", false), //
				new ExcelColumn("Display xml:lang", false), //For backward compatibility
				new ExcelColumn("Leaf ID", false), //
				new ExcelColumn("Leaf Page Type", false), //
				new ExcelColumn("Leaf Page Reference", false), //
				new ExcelColumn("Leaf First Page", false), //
				new ExcelColumn("Leaf Last Page", false), //
				new ExcelColumn("Leaf Page Title", false), //
				new ExcelColumn("User Note1", false), //
				new ExcelColumn("User Note2", false), //
				new ExcelColumn("W Display Name", false), //Deprecated and ignored
				new ExcelColumn("Result Key", true), //
				new ExcelColumn("Result Description", true), //
				new ExcelColumn("Result Language", false), //
				new ExcelColumn("Result xml:lang", false), //For backward compatibility
				new ExcelColumn("ParameterOID Dataset", true), //
				new ExcelColumn("Analysis Reason", true), //
				new ExcelColumn("Analysis Purpose", true), //
				new ExcelColumn("Documentation ID", false), //
				new ExcelColumn("Documentation Page Type", false), //
				new ExcelColumn("Documentation Page Reference", false), //
				new ExcelColumn("Documentation First Page", false), //
				new ExcelColumn("Documentation Last Page", false), //
				new ExcelColumn("Documentation Page Title", false), //
				new ExcelColumn("Documentation Text", false), //
				new ExcelColumn("Documentation Language", false), //
				new ExcelColumn("Documentation xml:lang", false), //For backward compatibility
				new ExcelColumn("Programming Code Context", false), //
				new ExcelColumn("Programming Code Text", false), //
				new ExcelColumn("Programming Code Document ID", false), //
				new ExcelColumn("Programming Code Document Page Type", false), //
				new ExcelColumn("Programming Code Document Page Reference", false), //
				new ExcelColumn("Programming Code Document First Page", false), //
				new ExcelColumn("Programming Code Document Last Page", false), //
				new ExcelColumn("Datasets CommentOID", false), //
				new ExcelColumn("Datasets Comment", false), //
				new ExcelColumn("Datasets Language", false), //
				new ExcelColumn("Datasets xml:lang", false), //For backward compatibility
				new ExcelColumn("Datasets DocumentID", false), //
				new ExcelColumn("DocumentID", false), //For backward compatibility
				new ExcelColumn("Datasets Document Page Type", false), //
				new ExcelColumn("Document Page Type", false), //For backward compatibility
				new ExcelColumn("Datasets Document Page Reference", false), //
				new ExcelColumn("Document Page Reference", false), //For backward compatibility
				new ExcelColumn("Datasets Document First Page", false), //
				new ExcelColumn("Datasets Document Last Page", false), //
				new ExcelColumn("Datasets Document Page Title", false), //
		});
		if (!excel_sheet.exists()) {
			return new ArrayList<>();	//Optional Sheet
		}
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}
		
		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			DefineARMDisplayPk last_display_pk;
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				/* Result Key is required for each row. */
				String result_key = ExcelCell.getAsString(cells.get("Result Key"));
				if (StringUtils.isEmpty(result_key)) {
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Result Key' column is required and cannot be empty. Skipping the row...");
					error.setColumnName("Result Key");
					rtn.add(error);
					return rtn;
				}
				/* ARM Display could be empty except for the first row. */
				String display_name = ExcelCell.getAsString(cells.get("Display Name"));
				if (last_display_pk == null) {
					if (StringUtils.isEmpty(display_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Display Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Display Name");
						rtn.add(error);
						return rtn;
					}
				}
				
				DefineARMDisplayPk pk = new DefineARMDisplayPk(display_name);
				/*
				 * Create DefineARMDisplayModel
				 */
				if (StringUtils.isNotEmpty(display_name) && !pk.equals(last_display_pk)) {
					DefineARMDisplayModel display2 = define.get(pk);
					/* Check if the new object is unique. The pk is allowed to be the same as the last pk. */
					if (display2 != null) {
						String message = "The row (" + display_name + ") is duplicated. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					DefineARMDisplayModel display = new DefineARMDisplayModel(pk);
					display.ordinal = ordinal++;
					display.display_desc = ExcelCell.getAsString(cells.get("Display Description"));
					String display_lang = ExcelCell.getAsString(cells.get("Display Language"));
					if (StringUtils.isEmpty(display_lang)) {
						display_lang = ExcelCell.getAsString(cells.get("Display xml:lang"));
					}
					display.display_lang = display_lang;
					List<String> leaf_ids = Utils.split(ExcelCell.getAsString(cells.get("Leaf ID")), config.valueDelimiter);
					List<String> leaf_page_types = Utils.split(ExcelCell.getAsString(cells.get("Leaf Page Type")), config.valueDelimiter);
					List<String> leaf_page_references = Utils.split(ExcelCell.getAsString(cells.get("Leaf Page Reference")), config.valueDelimiter);
					List<String> leaf_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Leaf First Page")), config.valueDelimiter);
					List<String> leaf_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Leaf Last Page")), config.valueDelimiter);
					List<String> leaf_page_titles = new ArrayList<>();	//Leaf Page Title is not supported in ARM for Define-XML 2.0
					List<DocumentRef> leaf_document_refs = createDocumentRefs(rtn, leaf_ids, leaf_page_types, leaf_page_references, leaf_first_pages, leaf_last_pages, leaf_page_titles);
					display.document_refs.addAll(leaf_document_refs);
					display.user_note1 = ExcelCell.getAsString(cells.get("User Note 1"));
					display.user_note2 = ExcelCell.getAsString(cells.get("User Note 2"));
					define.put(pk, display);
					last_display_pk = pk;
				}
				
				/*
				 * Create ARMResult for each row and add it to the last ARM Display
				 */
				DefineARMDisplayModel last_display = define.get(last_display_pk);
				DefineARMResultPk result_pk = new DefineARMResultPk(display_name, result_key);
				/* Check if the new object is unique. */
				DefineARMResultModel result2 = define.get(result_pk);
				if (result2 != null) {
					String message = "The row (" + display_name + "/" + result_key + ") is duplicated. Skipping the row...";
					ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
					rtn.add(error);
					return rtn;
				}
				DefineARMResultModel result = new DefineARMResultModel(result_pk);
				result.result_desc = ExcelCell.getAsString(cells.get("Result Description"));
				String result_lang = ExcelCell.getAsString(cells.get("Result Language"));
				if (StringUtils.isEmpty(result_lang)) {
					result_lang = ExcelCell.getAsString(cells.get("Result xml:lang"));
				}
				result.result_lang = result_lang;
				String param_dataset = ExcelCell.getAsString(cells.get("ParameterOID Dataset"));
				DefineVariableModel variable = define.listSortedVariable().stream().filter(o -> StringUtils.equals(o.variable_oid, DefineVariableModel.createOid(param_dataset, "PARAMCD"))).findFirst().orElse(null);
				if (StringUtils.isNotEmpty(param_dataset)) {
					if (StringUtils.isNotEmpty(param_dataset) && variable == null) {
						String message = "The PARAMCD variable of the " + param_dataset + " dataset is not found in the " + config.defineDatasetTableName + " sheet. The 'ParameterOID Dataset' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("ParameterOID Dataset");
						rtn.add(error);
					} else {
						result.param_dataset = param_dataset;
						result.param_oid = variable.toOid();
					}
				}
				result.analysis_reason = ExcelCell.getAsString(cells.get("Analysis Reason"));
				result.analysis_purpose = ExcelCell.getAsString(cells.get("Analysis Purpose"));
				result.docm_text = ExcelCell.getAsString(cells.get("Documentation Text"));
				String docm_lang = ExcelCell.getAsString(cells.get("Documentation Language"));
				if (StringUtils.isEmpty(docm_lang)) {
					docm_lang = ExcelCell.getAsString(cells.get("Documentation xml:lang"));
				}
				result.docm_lang = docm_lang;
				List<String> docm_ids = Utils.split(ExcelCell.getAsString(cells.get("Documentation ID")), config.valueDelimiter);
				List<String> docm_page_types = Utils.split(ExcelCell.getAsString(cells.get("Documentation Page Type")), config.valueDelimiter);
				List<String> docm_page_references = Utils.split(ExcelCell.getAsString(cells.get("Documentation Page Reference")), config.valueDelimiter);
				List<String> docm_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Documentation First Page")), config.valueDelimiter);
				List<String> docm_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Documentation Last Page")), config.valueDelimiter);
				List<String> docm_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Documentation Page Title")), config.valueDelimiter);
				List<DocumentRef> docm_document_refs = createDocumentRefs(rtn, docm_ids, docm_page_types, docm_page_references, docm_first_pages, docm_last_pages, docm_page_titles);
				result.docm_document_refs.addAll(docm_document_refs);
				result.prog_code_context = ExcelCell.getAsString(cells.get("Programming Code Context"));
				result.prog_code_text = ExcelCell.getAsString(cells.get("Programming Code Text"));
				List<String> code_ids = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document ID")), config.valueDelimiter);
				List<String> code_page_types = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document Page Type")), config.valueDelimiter);
				List<String> code_page_references = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document Page Reference")), config.valueDelimiter);
				List<String> code_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document First Page")), config.valueDelimiter);
				List<String> code_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document Last Page")), config.valueDelimiter);
				List<String> code_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Programming Code Document Page Title")), config.valueDelimiter);
				List<DocumentRef> code_document_refs = createDocumentRefs(rtn, code_ids, code_page_types, code_page_references, code_first_pages, code_last_pages, code_page_titles);
				result.prog_code_document_refs.addAll(code_document_refs);
				/* Begin Comment --> */
				String comment_oid = ExcelCell.getAsString(cells.get("Datasets CommentOID"));
				String str_comment = ExcelCell.getAsString(cells.get("Datasets Comment"));
				if (StringUtils.isNotEmpty(comment_oid)) {
					DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
					if (comment == null) {
						String message = "The 'Datasets CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'Datasets CommentOID' is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Datasets CommentOID");
						rtn.add(error);
					} else {
						result.dataset_comment_oid = comment_oid;
					}
					if (StringUtils.isNotEmpty(str_comment)) {
						String message = "Either of the 'Datasets CommentOID' or 'Datasets Comment' column can be entered. The 'Datasets Comment' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Datasets Comment");
						rtn.add(error);
					}
				} else if (StringUtils.isNotEmpty(str_comment)) {
					result.dataset_comment_oid = DefineCommentModel.createCommentOID(result);
					/* Create DefineCommentModel object */
					DefineCommentPk comment_pk = new DefineCommentPk(result.dataset_comment_oid);
					DefineCommentModel comment = new DefineCommentModel(comment_pk);
					comment.comment_text = str_comment;
					String comment_lang = ExcelCell.getAsString(cells.get("Datasets Language"));
					if (StringUtils.isEmpty(comment_lang)) {
						comment_lang = ExcelCell.getAsString(cells.get("Datasets xml:lang"));
					}
					comment.comment_lang = comment_lang;
					List<String> document_ids = Utils.split(ExcelCell.getAsString(cells.get("Datasets DocumentID")), config.valueDelimiter);
					if (document_ids.isEmpty()) {
						document_ids = Utils.split(ExcelCell.getAsString(cells.get("DocumentID")), config.valueDelimiter);
					}
					List<String> document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Datasets Document Page Type")), config.valueDelimiter);
					if (document_page_types.isEmpty()) {
						document_page_types = Utils.split(ExcelCell.getAsString(cells.get("Document Page Type")), config.valueDelimiter);
					}
					List<String> document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Datasets Document Page Reference")), config.valueDelimiter);
					if (document_page_references.isEmpty()) {
						document_page_references = Utils.split(ExcelCell.getAsString(cells.get("Document Page Reference")), config.valueDelimiter);
					}
					List<String> document_first_pages = Utils.split(ExcelCell.getAsString(cells.get("Datasets Document First Page")), config.valueDelimiter);
					List<String> document_last_pages = Utils.split(ExcelCell.getAsString(cells.get("Datasets Document Last Page")), config.valueDelimiter);
					List<String> document_page_titles = Utils.split(ExcelCell.getAsString(cells.get("Datasets Document Page Title")), config.valueDelimiter);
					List<DocumentRef> document_refs = createDocumentRefs(rtn, document_ids, document_page_types, document_page_references, document_first_pages, document_last_pages, document_page_titles);
					comment.document_refs.addAll(document_refs);
					define.put(comment_pk, comment);
				} else {
					String document_id = ExcelCell.getAsString(cells.get("Datasets DocumentID"));
					if (StringUtils.isEmpty(document_id)) {
						document_id = ExcelCell.getAsString(cells.get("DocumentID"));
					}
					if (StringUtils.isNotEmpty(document_id)) {
						String message = "The 'Datasets Comment' column is required when 'Datasets DocumentID' is entered. The 'Datasets DocumentID' and associated information is ignored.";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						error.setColumnName("Datasets Comment");
						rtn.add(error);
					}
				}
				/* --> End Comment */

				last_display.arm_result_pks.add(result_pk);
				define.put(result_pk, result);
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}
	
	List<ErrorLog> importArmDataset(String sheet_name) {
		
		HorizontalExcelSheet excel_sheet = new HorizontalExcelSheet(sheet_name, true, new ExcelColumn[] {
				new ExcelColumn("Display Name", true), //
				new ExcelColumn("Result Key", true), //
				new ExcelColumn("Dataset Name", true), //
				new ExcelColumn("Analysis Variable", true), //
				new ExcelColumn("User Note1", false), //
				new ExcelColumn("User Note2", false), //
				new ExcelColumn("W Display Name", false), //Deprecated and ignored
				new ExcelColumn("W Result Key", false), //Deprecated and ignored
				new ExcelColumn("W Dataset Name", false), //Deprecated and ignored
				new ExcelColumn("WhereClauseDataset", true), //
				new ExcelColumn("WhereClauseVariable", true), //
				new ExcelColumn("WhereClauseOperator", true), //
				new ExcelColumn("WhereClauseValue", true), //
				new ExcelColumn("WhereClause CommentOID", false), //
				new ExcelColumn("WhereClause Comment", false), //
				new ExcelColumn("WhereClause Language", false), //
				new ExcelColumn("W xml:lang", false) //For backward compatibility
		});
		if (!excel_sheet.exists()) {
			return new ArrayList<>();	//Optional Sheet
		}
		List<ErrorLog> errors = excel_sheet.validate();
		if (!errors.isEmpty()) {
			return errors;
		}
		
		Function<ExcelRow, List<ErrorLog>> binder = new Function<ExcelRow, List<ErrorLog>>() {
			DefineARMDatasetPk last_arm_dataset_pk;
			DefineWCPk last_wc_pk;
			int ordinal = 1;
			
			@Override
			public List<ErrorLog> apply(ExcelRow cells) {
				List<ErrorLog> rtn = new ArrayList<>();
				/* WhereClause is required for each row. */
				String wc_dataset = ExcelCell.getAsString(cells.get("WhereClauseDataset"));
//				if (StringUtils.isEmpty(wc_dataset)) {
//					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseDataset' column is required and cannot be empty. Skipping the row...");
//					error.setColumnName("WhereClauseDataset");
//					rtn.add(error);
//					return rtn;
//				}
				String wc_variable = ExcelCell.getAsString(cells.get("WhereClauseVariable"));
//				if (StringUtils.isEmpty(wc_variable)) {
//					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseVariable' column is required and cannot be empty. Skipping the row...");
//					error.setColumnName("WhereClauseVariable");
//					rtn.add(error);
//					return rtn;
//				}
				String wc_operator = ExcelCell.getAsString(cells.get("WhereClauseOperator"));
//				if (StringUtils.isEmpty(wc_operator)) {
//					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseOperator' column is required and cannot be empty. Skipping the row...");
//					error.setColumnName("WhereClauseOperator");
//					rtn.add(error);
//					return rtn;
//				}
				String wc_value = ExcelCell.getAsString(cells.get("WhereClauseValue"));
//				if (StringUtils.isEmpty(wc_value)) {
//					ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'WhereClauseValue' column is required and cannot be empty. Skipping the row...");
//					error.setColumnName("WhereClauseValue");
//					rtn.add(error);
//					return rtn;
//				}
				/* ARM Dataset could be empty except for the first row. */
				String display_name = ExcelCell.getAsString(cells.get("Display Name"));
				String result_key = ExcelCell.getAsString(cells.get("Result Key"));
				String dataset_name = ExcelCell.getAsString(cells.get("Dataset Name"));
				if (last_arm_dataset_pk == null) {
					if (StringUtils.isEmpty(display_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Display Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Display Name");
						rtn.add(error);
						return rtn;
					}
					if (StringUtils.isEmpty(result_key)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Result Key' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Result Key");
						rtn.add(error);
						return rtn;
					}
					if (StringUtils.isEmpty(dataset_name)) {
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, "The 'Dataset Name' column is required and cannot be empty. Skipping the row...");
						error.setColumnName("Dataset Name");
						rtn.add(error);
						return rtn;
					}
				}
				
				DefineARMDatasetPk pk = new DefineARMDatasetPk(display_name, result_key, DefineDatasetModel.createOid(dataset_name));
				/*
				 * Create DefineValueModel
				 */
				if (StringUtils.isNotEmpty(display_name) && StringUtils.isNotEmpty(result_key) && StringUtils.isNotEmpty(dataset_name) && !pk.equals(last_arm_dataset_pk)) {
					DefineARMDatasetModel arm_dataset2 = define.get(pk);
					/* Check if the new object is unique. The pk is allowed to be the same as the last pk. */
					if (arm_dataset2 != null) {
						String message = "The row (" + display_name + "/" + result_key + "/" + dataset_name + ") is duplicated. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					/* Check if the dataset exists */
					DefineDatasetModel dataset = define.get(new DefineDatasetPk(dataset_name));
					if (dataset == null) {
						String message = "The dataset " + dataset_name + "is not found in the " + config.defineDatasetTableName + " sheet. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					
					DefineARMDatasetModel arm_dataset = new DefineARMDatasetModel(pk);
					arm_dataset.dataset_name = dataset_name;
					arm_dataset.dataset_oid = dataset.toOid();
					arm_dataset.ordinal = ordinal++;
					List<String> analysis_variables = Utils.split(ExcelCell.getAsString(cells.get("Analysis Variable")), config.valueDelimiter);
					for (String analysis_variable : analysis_variables) {
						String analysis_variable_oid = DefineVariableModel.createOid(dataset_name, analysis_variable);
						DefineVariableModel variable = define.listSortedVariable().stream().filter(o -> StringUtils.equals(o.variable_oid, analysis_variable_oid)).findFirst().orElse(null);
						if (variable == null) {
							String message = "The variable " + analysis_variable + " is not found in the " + config.defineVariableTableName + " sheet. The 'Analysis Variable' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("Analysis Variable");
							rtn.add(error);
						} else {
							arm_dataset.analysis_variable_oids.add(analysis_variable_oid);
						}
					}

					arm_dataset.user_note1 = ExcelCell.getAsString(cells.get("User Note1"));
					arm_dataset.user_note2 = ExcelCell.getAsString(cells.get("User Note2"));

					define.put(pk, arm_dataset);
					last_arm_dataset_pk = pk;
				}
				
				/*
				 * Create WCCondition for each row.
				 * Create DefineWCModel if it is a new entry (i.e. new ARM Dataset), or use the last DefineWCModel
				 */
				DefineARMDatasetModel last_arm_dataset = define.get(last_arm_dataset_pk);
				String wc_oid = DefineWCModel.createOid(last_arm_dataset);
				DefineWCModel wc = null;
				DefineWCPk wc_pk = new DefineWCPk(wc_oid);
				if (wc_pk.equals(last_wc_pk)) {
					/* Use the last WC */
					wc = define.get(last_wc_pk);
					WCCondition wc_condition = new WCCondition();
					wc_condition.dataset_name = wc_dataset;
					wc_condition.variable_name = wc_variable;
					wc_condition.operator = wc_operator;
					wc_condition.values.addAll(Utils.split(wc_value, config.valueDelimiter));
					wc.wc_conditions.add(wc_condition);
				} else {
					DefineWCModel wc2 = define.get(wc_pk);
					/* Check if the new object is unique. The pk is allowed to be the same as the last pk.  */
					if (wc2 != null) {
						String message = "The row (" + wc_dataset + "/" + wc_variable + "/" + wc_operator + "/" + wc_value + ") is duplicated. Skipping the row...";
						ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
						rtn.add(error);
						return rtn;
					}
					wc = new DefineWCModel(wc_pk);	//Create new WC
					/* Begin Comment --> */
					String comment_oid = ExcelCell.getAsString(cells.get("WhereClause CommentOID"));
					String str_comment = ExcelCell.getAsString(cells.get("WhereClause Comment"));
					if (StringUtils.isNotEmpty(comment_oid)) {
						DefineCommentModel comment = define.listSortedComment().stream().filter(o -> StringUtils.equals(o.oid, comment_oid)).findFirst().orElse(null);
						if (comment == null) {
							String message = "The 'CommentOID' is not found in the " + config.defineCommentTableName + " sheet. The 'CommentOID' is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("CommentOID");
							rtn.add(error);
						} else {
							wc.comment_oid = comment_oid;
						}
						if (StringUtils.isNotEmpty(str_comment)) {
							String message = "Either of the 'WhereClause CommentOID' or 'WhereClause Comment' column can be entered. The 'WhereClause Comment' and associated information is ignored.";
							ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
							error.setColumnName("WhereClause Comment");
							rtn.add(error);
						}
					} else if (StringUtils.isNotEmpty(str_comment)) {
						wc.comment_oid = DefineCommentModel.createCommentOID(wc);
						/* Create DefineCommentModel object */
						DefineCommentPk comment_pk = new DefineCommentPk(wc.comment_oid);
						DefineCommentModel comment = new DefineCommentModel(comment_pk);
						comment.comment_text = str_comment;
						String comment_lang = ExcelCell.getAsString(cells.get("WhereClause Language"));
						if (StringUtils.isEmpty(comment_lang)) {
							comment_lang = ExcelCell.getAsString(cells.get("W xml:lang"));
						}
						comment.comment_lang = comment_lang;
						define.put(comment_pk, comment);
					}
					/* --> End Comment */
					WCCondition wc_condition = new WCCondition();
					wc_condition.dataset_name = wc_dataset;
					wc_condition.variable_name = wc_variable;
					wc_condition.operator = wc_operator;
					wc_condition.values.addAll(Utils.split(wc_value, config.valueDelimiter));
					wc.wc_conditions.add(wc_condition);
					last_arm_dataset.where_clause_pk = wc_pk;
					define.put(wc_pk, wc);
				}
				last_wc_pk = wc_pk;
				return rtn;
			}
		};
		
		return excel_sheet.doImport(binder);
	}

	private List<DocumentRef> createDocumentRefs(List<ErrorLog> error_logs, List<String> document_ids, List<String> document_page_types, List<String> document_page_references, List<String> document_first_pages, List<String> document_last_pages, List<String> document_page_titles) {
		List<DocumentRef> rtn = new ArrayList<>();
		if (document_ids.isEmpty()) {
			return rtn;
		}
		if (document_page_types.size() > 0 && document_page_types.size() != document_ids.size()) {
			String message = "The count of 'Document Page Type' is different from the count of 'Document ID'. The 'Document Page Type' and associated information is ignored.";
			ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
			error.setColumnName("Document Page Type");
			error_logs.add(error);
			document_page_types = new ArrayList<>();
			document_page_references = new ArrayList<>();
			document_first_pages = new ArrayList<>();
			document_last_pages = new ArrayList<>();
		} else {
			if (document_page_references.size() > 0 && document_page_references.size() != document_ids.size()) {
				String message = "The count of 'Document Page Reference' is different from the count of 'Document ID'. The 'Document Page Reference' and associated information is ignored.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("Document Page Reference");
				error_logs.add(error);
				document_page_references = new ArrayList<>();
			}
			if (document_page_references.size() > 0 && (document_first_pages.size() > 0 || document_last_pages.size() > 0)) {
				String message = "The 'Document First Page' and 'Document Last Page' columns are ignored when 'Document Page Reference' is filled out.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("Document First Page, Document Last Page");
				error_logs.add(error);
				document_first_pages = new ArrayList<>();
				document_last_pages = new ArrayList<>();
			}
			if ((document_first_pages.size() > 0 && document_first_pages.size() != document_ids.size())
					|| (document_last_pages.size() > 0 && document_last_pages.size() != document_ids.size())) {
				String message = "The count of 'Document First Page' or 'Document Last Page' is different from the count of 'Document ID'. The columns are ignored.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("Document First Page, Document Last Page");
				error_logs.add(error);
				document_first_pages = new ArrayList<>();
				document_last_pages = new ArrayList<>();
			}
			if (document_page_titles.size() > 0 && document_page_titles.size() != document_ids.size()) {
				String message = "The count of 'Document Page Title' is different from the count of 'Document ID'. The column is ignored.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("Document Page Title");
				error_logs.add(error);
				document_page_titles = new ArrayList<>();
			}
		}
		for (int i = 0; i < document_ids.size(); i++) {
			String document_id = document_ids.get(i);
			DefineDocumentModel document = define.listSortedDocument().stream().filter(o -> StringUtils.equals(o.document_id, document_id)).findFirst().orElse(null);
			if (document == null) {
				String message = "The 'DocumentID' is not found in the " + config.defineDocumentTableName + " sheet. The 'DocumentID' and associated information is ignored.";
				ErrorLog error = new ErrorLog(ErrorLevel.WARN, message);
				error.setColumnName("DocumentID");
				error_logs.add(error);
			} else {
				DocumentRef document_ref = new DocumentRef(document_id);
				if (!document_page_types.isEmpty()) {
					document_ref.document_page_type = document_page_types.get(i);
				}
				if (!document_page_references.isEmpty()) {
					document_ref.document_page_reference = document_page_references.get(i);
				}
				if (!document_first_pages.isEmpty()) {
					document_ref.document_first_page = document_first_pages.get(i);
				}
				if (!document_last_pages.isEmpty()) {
					document_ref.document_last_page = document_last_pages.get(i);
				}
				if (!document_page_titles.isEmpty()) {
					document_ref.document_page_title = document_page_titles.get(i);
				}
				rtn.add(document_ref);
			}
		}
		return rtn;
	}
	
	/**
	 * This method must be called after the Study and Standard sheets are imported.
	 * @return Dataset Type (e.g. SDTM, ADaM). ADaM if the type is not identifiable.
	 */
	private DatasetType findDatasetType() {
		DefineStudyModel study = this.define.getStudy();
		if (study != null && StringUtils.isNotEmpty(study.standard_name)) {
			if (StringUtils.startsWith(study.standard_name, "SDTM")) {
				return DatasetType.SDTM;
			} else if (StringUtils.startsWith(study.standard_name, "SEND")) {
				return DatasetType.SEND;
			} else if (StringUtils.startsWith(study.standard_name, "ADaM")) {
				return DatasetType.ADaM;
			}
		}
		List<DefineStandardModel> standards = this.define.listSortedStandard();
		/* Check ADaM first because ADaM might include SDTM standards. */
		DefineStandardModel standard = standards.stream().filter(o -> StringUtils.startsWith(o.standard_name, "ADaM")).findFirst().orElse(null);
		if (standard != null) {
			return DatasetType.ADaM;
		}
		standard = standards.stream().filter(o -> StringUtils.startsWith(o.standard_name, "SDTM")).findFirst().orElse(null);
		if (standard != null) {
			return DatasetType.SDTM;
		}
		standard = standards.stream().filter(o -> StringUtils.startsWith(o.standard_name, "SEND")).findFirst().orElse(null);
		if (standard != null) {
			return DatasetType.SEND;
		}
		
		return DatasetType.ADaM;	//Default
	}
	
	private enum SheetType {
		Vertical, Horizontal;
	}
	
	private abstract class ExcelSheet {
		String name;
		boolean is_required;
		SheetType sheet_type;
		ExcelColumn[] columns = new ExcelColumn[]{};
	}
	
	private class VerticalExcelSheet extends ExcelSheet {
		final int KEY_COLUMN_NUM = 0;
		final int VALUE_COLUMN_NUM = 1; 
		final int FIRST_ROW_NUM = 1;
		
		public VerticalExcelSheet(String name, boolean is_required, ExcelColumn[] columns) {
			this.name = name;
			this.is_required = is_required;
			this.sheet_type = SheetType.Vertical;
			this.columns = columns;
		}
		
		public int getKeyColumnNum() {
			return KEY_COLUMN_NUM;
		}
		
		public int getValueColumnNum() {
			return VALUE_COLUMN_NUM;
		}
		
		public int getFirstRowNum() {
			return FIRST_ROW_NUM;
		}
		
		public List<ErrorLog> validate() {
			List<ErrorLog> rtn = new ArrayList<>();
			Sheet sheet = workbook.getSheet(this.name);
			if (sheet == null) {
				ErrorLog error = new ErrorLog(ErrorLevel.ERROR, "The sheet '" + this.name +"' is not found in the given spreadsheet.");
				rtn.add(error);
				return rtn;
			}
			List<String> actual_column_names = new ArrayList<>();
			/* Get actual column names */
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(this.getKeyColumnNum());
				if (cell == null) {
					continue;
				} else {
					actual_column_names.add(ExcelCell.getAsString(cell));
				}
			}
			/* Check if required columns exist in actual columns */
			for (ExcelColumn column : this.columns) {
				if (column.is_required) {
					if (actual_column_names.indexOf(column.name) == -1) {
						String message = "A required property '" + column.name + "' is not found.";
						ErrorLog error = new ErrorLog(ErrorLevel.ERROR, message);
						error.setTabName(this.name);
						error.setColumnNum(this.getKeyColumnNum() + 1);
						rtn.add(error);
					}
				}
			}
			return rtn;
		}
		
		public List<ErrorLog> doImport(Function<ExcelRow, List<ErrorLog>> binder) {
			Sheet sheet = workbook.getSheet(this.name);
			ExcelRow cells = new ExcelRow();
			for (int rowNum = this.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				String key = ExcelCell.getAsString(row.getCell(this.getKeyColumnNum()));
				if (StringUtils.isEmpty(key)) {
					continue;
				}
				cells.put(key, row.getCell(this.getValueColumnNum()));
			}
			return binder.apply(cells);
		}
	}
	
	private class HorizontalExcelSheet extends ExcelSheet {
		final int HEADER_ROW_NUM = 0;
		final int FIRST_ROW_NUM = 1; 
		final int FIRST_COLUMN_NUM = 0;
		
		public HorizontalExcelSheet(String name, boolean is_required, ExcelColumn[] columns) {
			this.name = name;
			this.is_required = is_required;
			this.sheet_type = SheetType.Horizontal;
			this.columns = columns;
		}
		
		/**
		 * Return true if the sheet name exists in the workbook
		 * @return
		 */
		public boolean exists() {
			Sheet sheet = workbook.getSheet(this.name);
			if (sheet == null) {
				return false;
			}
			return true;
		}
		
		public int getHeaderRowNum() {
			return HEADER_ROW_NUM;
		}
		
		public int getFirstRowNum() {
			return FIRST_ROW_NUM;
		}
		
		public int getFirstColumnNum() {
			return FIRST_COLUMN_NUM;
		}
		
		/**
		 * Validate if the sheet and required columns exist.
		 * The column name ignores cases and white spaces.
		 * @return Successful validation returns empty list
		 */
		public List<ErrorLog> validate() {
			List<ErrorLog> rtn = new ArrayList<>();
			Sheet sheet = workbook.getSheet(this.name);
			if (sheet == null) {
				ErrorLog error = new ErrorLog(ErrorLevel.ERROR, "The sheet '" + this.name +"' is not found in the given spreadsheet.");
				rtn.add(error);
				return rtn;
			}
			List<String> actual_column_names = new ArrayList<>();
			Row header_row = sheet.getRow(this.getHeaderRowNum());
			if (header_row == null) {
				String message = "A header row is not found.";
				ErrorLog error = new ErrorLog(this.name, this.getHeaderRowNum() + 1, "", ErrorLevel.ERROR, message);
				rtn.add(error);
			} else {
				/* Get actual column names */
				for (int i = 0; i <= header_row.getLastCellNum(); i++) {
					Cell cell = header_row.getCell(i);
					if (cell == null) {
						continue;
					} else {
						String column_name = ExcelCell.getAsString(cell);
						actual_column_names.add(StringUtils.deleteWhitespace(column_name).toUpperCase());
					}
				}
				/* Check if required columns exist in actual columns */
				for (ExcelColumn column : this.columns) {
					if (column.is_required) {
						String column_name = StringUtils.deleteWhitespace(column.name).toUpperCase();
						if (actual_column_names.indexOf(column_name) == -1) {
							String message = "A required column '" + column.name + "' is not found.";
							ErrorLog error = new ErrorLog(this.name, this.getHeaderRowNum() + 1, "", ErrorLevel.ERROR, message);
							rtn.add(error);
						}
					}
				}
			}
			return rtn;
		}

		/**
		 * Load the sheet data to objects using a given binder.
		 * @param binder A function that defines binding rule between an Excel row and an object
		 * @return
		 */
		public List<ErrorLog> doImport(Function<ExcelRow, List<ErrorLog>> binder) {
			List<ErrorLog> rtn = new ArrayList<>();
			Sheet sheet = workbook.getSheet(this.name);
			Row header_row = sheet.getRow(this.HEADER_ROW_NUM);
			for (int rowNum = this.getFirstRowNum(); rowNum <= sheet.getLastRowNum(); rowNum++) {
				Row row = sheet.getRow(rowNum);
				ExcelRow cells = new ExcelRow();
				for (int cellNum = getFirstColumnNum(); cellNum <= row.getLastCellNum(); cellNum++) {
					String key = ExcelCell.getAsString(header_row.getCell(cellNum));
					if (StringUtils.isEmpty(key)) {
						continue;
					}
					cells.put(key, row.getCell(cellNum));
				}
				List<ErrorLog> errors = binder.apply(cells);
				/* Update tab name and line number */
				for (ErrorLog error : errors) {
					error.setTabName(this.name);
					error.setLineNum(rowNum + 1);
				}
				rtn.addAll(errors);
			}
			return rtn;
		}
	}

	/**
	 * This class implements Map (i.e. put/get methods) that ignores cases and white spaces from column names.
	 * Null for keys and cells is not supported.
	 */
	private class ExcelRow {
		private Map<String, Cell> cells = new HashMap<>();
		
		public void put(String key, Cell cell) {
			cells.put(StringUtils.deleteWhitespace(key).toUpperCase(), cell);
		}
		
		public Cell get(String key) {
			return cells.get(StringUtils.deleteWhitespace(key).toUpperCase());
		}
	}
	
	private class ExcelColumn {
		String name = "";
		boolean is_required = false;
		
		public ExcelColumn(String name, boolean is_required) {
			this.name = name;
			this.is_required = is_required;
		}
	}
	
	private static class ExcelCell {
		public static String getAsString(Cell cell) {
			if (cell == null) {
				return "";
			}
			CellType cell_type = cell.getCellTypeEnum();
			switch(cell_type) {
			case STRING:
				/* All cells in the SDTM/ADaM Spec is expected to be formatted as String. */
				return cell.getStringCellValue();
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			case NUMERIC:
				/* 0 will be returned for a blank cell. */
				return String.valueOf(cell.getNumericCellValue());
			case ERROR:
				return String.valueOf(cell.getErrorCellValue());
			default:
				break;
			}
			return "";
		}
		
		/**/
		public static int getAsInteger(Cell cell) {
			if (cell == null) {
				return 0;
			}
			String val = getAsString(cell);
			try {
				val = StringUtils.removeEnd(val, ".0");
				return Integer.parseInt(val);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}
	}
	
	public DefineModel getDefineModel() {
		return this.define;
	}
	
	public void setDefineModel(DefineModel define) {
		this.define = define;
	}
}
