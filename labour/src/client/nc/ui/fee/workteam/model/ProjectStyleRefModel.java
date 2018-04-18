package nc.ui.fee.workteam.model;

import nc.ui.bd.ref.AbstractRefModel;

/**
 * 列表新参照 其他收合同参照
 * 
 * @author shidalin
 * 
 */
public class ProjectStyleRefModel extends AbstractRefModel {

	@Override
	public String getWherePart() {
		// 过滤条件
		String wherePart = super.getWherePart();
		// 增加dr过滤和是否最新版本滤
		if (null == wherePart || "".equals(wherePart.trim())) {
			wherePart = " isnull(dr,0) = 0 and pk_defdoclist = ( select t.pk_defdoclist from bd_defdoclist t  where nvl(t.dr,0) =0 and  t.code = 'SY_XMLX' ) ";
		} else {
			wherePart += " and isnull(dr,0) = 0 and and pk_defdoclist = ( select t.pk_defdoclist from bd_defdoclist t  where nvl(t.dr,0) =0 and  t.code = 'SY_XMLX' ) ";
		}
		return wherePart;
	}

	@Override
	public String getRefCodeField() {
		// 返回编码字段
		return "code";
	}

	@Override
	public String getRefNameField() {
		// 返回名称字段
		return "name";
	}

	@Override
	public String[] getHiddenFieldCode() {
		// 参照隐藏字段
		String[] fields = new String[] { "pk_defdoc" };
		return fields;
	}

	@Override
	public String[] getFieldCode() {
		// 参照字段
		String[] fields = new String[] { "code", "name" };
		return fields;
	}

	@Override
	public String[] getFieldName() {
		// 参照字段名称
		String[] fields = new String[] { "编码", "名称"};
		return fields;
	}

	@Override
	public String getPkFieldCode() {
		// 参照主键字段
		return "pk_defdoc";
	}

	@Override
	public String getTableName() {
		// 参照表名字
		return "bd_defdoc";
	}

	@Override
	public String getRefTitle() {
		// 参照界面名称
		return "项目类型自定义档案参照";
	}

}
