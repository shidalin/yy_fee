package nc.ui.fee.withholding.ace.ref;

import java.awt.Container;

import nc.ui.pubapp.billref.src.DefaultBillReferQuery;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.vo.querytemplate.TemplateInfo;

/**
 * 来源单据查询类 功能：来源单据查询模板初始化 设定过滤
 * 
 * @author shidl
 * 
 */

public class FE02RefFE01ReferQuery extends DefaultBillReferQuery {

	public FE02RefFE01ReferQuery(Container c, TemplateInfo info) {
		// TODO Auto-generated constructor stub
		super(c, info);
	}

	@Override
	public void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator) {

	}

}
