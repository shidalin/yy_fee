package nc.ui.fee.workteam.model;

import nc.ui.bd.ref.AbstractRefModel;

/**
 * 列表新参照 班组参照
 * 
 * @author shidalin
 * 
 */
public class WorkteamRefModel extends AbstractRefModel {

	@Override
	public String getWherePart() {
		// 过滤条件
		String wherePart = super.getWherePart();
		// 增加dr过滤和是否班组长过滤
		if (null == wherePart || "".equals(wherePart.trim())) {
			wherePart = " isnull(dr,0) = 0 and isleader = 'Y' ";
		} else {
			wherePart += " and isnull(dr,0) = 0 and isleader = 'Y' ";
		}
		return wherePart;
	}

	@Override
	public String[] getHiddenFieldCode() {
		// 参照隐藏字段
		String[] fields = new String[] { "pk_workteam" ,"pk_org"};
		return fields;
	}

	@Override
	public String[] getFieldCode() {
		// 参照字段
		String[] fields = new String[] { "name", "decode(sex,1,'男',2,'女')", "age", "isleader",
				"pleader" };
		return fields;
	}

	@Override
	public String getRefCodeField() {
		// 返回编码字段
		return "name";
	}

	@Override
	public String getRefNameField() {
		// 返回名称字段
		return "name";
	}

	@Override
	public String[] getFieldName() {
		// 参照字段名称
		String[] fields = new String[] { "姓名", "性别", "年龄", "是否班组长", "班组长" };
		return fields;
	}

	@Override
	public String getPkFieldCode() {
		// 参照主键字段
		return "pk_workteam";
	}

	@Override
	public String getTableName() {
		// 参照表名字
		return "fee_workteam";
	}

	@Override
	public String getRefTitle() {
		// 参照界面名称
		return "班组基本信息";
	}

}
