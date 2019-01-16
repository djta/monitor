package com.ccms.core.formengine;

import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormOutput;
import com.ccms.core.foctory.TemplateBean;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

public class FormOutputEngine extends FormOutput {

	/**
	 * 每行多少列
	 */
	public static final int totalColOneRow = 12;

	public FormOutputEngine(Db db) {
		this.db = db;
	}

	/**
	 * @param db
	 * @param form
	 * @param tmp
	 *            模板
	 * @param flag
	 *            1：form 2：filter
	 * @param locale
	 *            1：cn 2：en
	 * @throws Throwable
	 */
	public String printPage(FormBean form, TemplateBean tmp, int flag, int locale) throws Throwable {

		// 加载模版,替换掉item标签
		String tCheckbox = StringUtil.replace(flag == 1 ? tmp.gettCheckbox() : tmp.gettCheckboxFilter(), "_${fld:form_item_id}", "");
		String tRadiobox = StringUtil.replace(flag == 1 ? tmp.gettRadiobox() : tmp.gettRadioboxFilter(), "_${fld:form_item_id}", "");
		String tText = StringUtil.replace(flag == 1 ? tmp.gettText() : tmp.gettTextFilter(), "_${fld:form_item_id}", "");
		String tCombo = StringUtil.replace(flag == 1 ? tmp.gettCombo() : tmp.gettComboFilter(), "_${fld:form_item_id}", "");
		String tDate = StringUtil.replace(flag == 1 ? tmp.gettDate() : tmp.gettDateFilter(), "_${fld:form_item_id}", "");
		String tDateTime = StringUtil.replace(flag == 1 ? tmp.gettDateTime() : tmp.gettDateTimeFilter(), "_${fld:form_item_id}", "");
		String tHidden = StringUtil.replace(flag == 1 ? tmp.gettHidden() : tmp.gettHiddenFilter(), "_${fld:form_item_id}", "");
		String tTextReadonly = StringUtil.replace(flag == 1 ? tmp.gettTextReadonly() : tmp.gettTextReadonlyFilter(), "_${fld:form_item_id}", "");
		String tTextShowonly = StringUtil.replace(flag == 1 ? tmp.gettTextShowonly() : tmp.gettTextShowonlyFilter(), "_${fld:form_item_id}", "");
		String tTextarea = StringUtil.replace(flag == 1 ? tmp.gettTextarea() : tmp.gettTextareaFilter(), "_${fld:form_item_id}", "");
		String tPickup = StringUtil.replace(flag == 1 ? tmp.gettPickup() : tmp.gettPickupFilter(), "_${fld:form_item_id}", "");
		String tPlugin = StringUtil.replace(flag == 1 ? tmp.gettPlugin() : tmp.gettPluginFilter(), "_${fld:form_item_id}", "");

		String tRow = flag == 1 ? tmp.gettRow() : tmp.gettRowFilter();
		String tItem = flag == 1 ? tmp.gettItem() : tmp.gettItemFilter();
		String tItemNone = flag == 1 ? tmp.gettItemNone() : tmp.gettItemNoneFilter();

		String strDomain = locale == 1 ? tmp.getStrDomainCn() : tmp.getStrDomainEn();
		String strFkField = tmp.getStrFkField();
		String strFkData = locale == 1 ? tmp.getStrFkDataCn() : tmp.getStrFkDataEn();

		strDomain = StringUtil.replace(strDomain, "${subject_id}", form.getSubject_id().toString());
		strFkData = StringUtil.replace(strFkData, "${subject_id}", form.getSubject_id().toString());

		String queryItem = tmp.getQueryItem();
		String queryField = flag == 1 ? tmp.getFormQueryFieldSql() : tmp.getFilterQueryFieldSql();

		TemplateEngine teItem = new TemplateEngine(null, null, queryItem);
		teItem.replace(form.getRsForm(), "");
		queryItem = teItem.toString();

		TemplateEngine teField = new TemplateEngine(null, null, queryField);
		teField.replace(form.getRsForm(), "");
		queryField = teField.toString();

		Recordset rsItem = db.get(queryItem);

		StringBuffer hgrid_item = new StringBuffer();
		rsItem.top();
		while (rsItem.next()) {
			// 判断中英文
			rsItem.setValue("item_name", locale == 1 ? rsItem.getValue("item_name_cn") : rsItem.getValue("item_name_en"));

			TemplateEngine tField = new TemplateEngine(null, null, queryField);
			tField.replace(rsItem, "");
			Recordset rsField = db.get(tField.toString());

			if (rsField.getRecordCount() == 0 && (rsItem.getString("linkage_document_id") == null || flag == 2))// 处理同显页面,flag!=1则为查询界面
				continue;

			// hgrid body
			StringBuffer hgrid = new StringBuffer(4096);
			StringBuilder colsBuf = new StringBuilder(256);
			// 单独处理hidden
			StringBuffer hiddenBuf = new StringBuffer(1024);
			// 是否全是hidden元素
			boolean isAllHidden = true;
			Integer iShowType = 0; // 字段类型
			int colTotal = 0; // 当前列总数
			rsField.top();
			while (rsField.next()) {
				if (rsField.getString("show_type") != null)
					iShowType = rsField.getInteger("show_type");
				else
					iShowType = 0; /* 默认文本框 */

				if (iShowType == 9) {
					hiddenBuf.append(getText(rsField, tHidden));
					continue;
				} else {
					isAllHidden = false;
				}

				int col = rsField.getInt("colspan");
				colTotal += col;

				// 判断中英文
				rsField.setValue("field_name", locale == 1 ? rsField.getValue("field_name_cn") : rsField.getValue("field_name_en"));
				switch (iShowType.intValue()) {
				case 0: // 文本框
					colsBuf.append(getText(rsField, tText));
					break;
				case 1: // 下接框
					colsBuf.append(getCombo(rsField, tCombo, strDomain, strFkField, strFkData));
					break;
				case 2: // 复选框
					colsBuf.append(getCheckbox(rsField, tCheckbox, strDomain));
					break;
				case 3: // 多选一
					colsBuf.append(getRadio(rsField, tRadiobox, strDomain, strFkField, strFkData));
					break;
				case 4: // 只读
					colsBuf.append(getText(rsField, tTextReadonly));
					break;
				case 5: // 日期
					colsBuf.append(getText(rsField, tDate));
					break;
				case 6: // 文本域
					colsBuf.append(getText(rsField, tTextarea));
					break;
				case 7: // 选取框
					colsBuf.append(getText(rsField, tPickup));
					break;
				case 8: // 插件(在字段一级指定)
					colsBuf.append(getText(rsField, getText(rsField, tPlugin)));
					break;
				case 10: // 日期时间
					colsBuf.append(getText(rsField, tDateTime));
					break;
				case 11: // 仅显示
					colsBuf.append(getText(rsField, getText(rsField, tTextShowonly)));
					break;
				default:
					// 未指定控件类型
					colsBuf.append(getText(rsField, tText));
					break;
				}

				if (colTotal >= totalColOneRow) {
					hgrid.append(StringUtil.replace(tRow, "${cols}", colsBuf.toString()));
					colsBuf.delete(0, colsBuf.length());
					colTotal = 0;
				}
			}
			if(colTotal > 0 && colTotal <= totalColOneRow){
				hgrid.append(StringUtil.replace(tRow, "${cols}", colsBuf.toString()));
				colsBuf.delete(0, colsBuf.length());
				colTotal = 0;
			}
			if (isAllHidden == true) { // 全部为hidden
				TemplateEngine t1 = new TemplateEngine(null, null, rsItem.getInt("item_id") == 0 ? tItemNone : tItem);
				t1.replace(rsItem, "");
				hgrid_item.append(StringUtil.replace(t1.toString(), "${items}", hiddenBuf.toString()));
				continue;
			}
			// 如果没有选择分类则不需要分类
			TemplateEngine t1 = new TemplateEngine(null, null, rsItem.getInt("item_id") == 0 ? tItemNone : tItem);
			t1.replace(rsItem, "");
			if (hgrid.length() > 0) {
				hgrid_item.append(StringUtil.replace(t1.toString(), "${items}", hgrid.toString()));
			}
			// 把hidden拼上来
			hgrid_item.append(hiddenBuf.toString());
		}
		return StringUtil.replace(hgrid_item.toString(), "${DEF:", "${def:");
	}

}
