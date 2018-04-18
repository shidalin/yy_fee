package nc.ui.fee.withholding.ace.handler;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.defdoc.IDefdoclistQryService;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 表头表尾编辑前事件
 * 
 * @author shidalin
 * 
 */
public class AceHeadTailBeforeEditEventHandler implements
		IAppEventHandler<CardHeadTailBeforeEditEvent> {

	public AceHeadTailBeforeEditEventHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		// 注意，编辑前事件需要返回值
		e.setReturnValue(Boolean.TRUE);
		String key = e.getKey();
		BillCardPanel cardPanel = e.getBillCardPanel();
		// 项目类型添加参照过滤，用户定义档案编码-SY_XMLX
		// if ("prostyle".equals(key)) {
		// String condition = " code = 'SY_XMLX' ";
		// String[] pk_orgs = new String[] { e.getContext().getPk_org() };
		// try {
		// String[] pk_defdoclists = NCLocator.getInstance()
		// .lookup(IDefdoclistQryService.class)
		// .queryDefdocPKByCondition(pk_orgs, condition);
		// if (ArrayUtils.isEmpty(pk_defdoclists)) {
		// ExceptionUtils.wrappBusinessException("查询项目类型（自定义档案）没有明细值");
		// }
		// // 设置 para1 = pk_deflist
		// UIRefPane ref = (UIRefPane) cardPanel.getHeadItem(key)
		// .getComponent();
		// ref.getRefModel().setPara1(pk_defdoclists[0]);
		// String querySql =
		// " select code,name,memo,mnecode from bd_defdoc where nvl(dr,0)=0 and pk_defdoclist = '"
		// + pk_defdoclists[0] + "'";
		// ref.getRefModel().setQuerySql(querySql);
		// } catch (BusinessException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// ExceptionUtils.wrappBusinessException("查询项目类型（自定义档案）出错");
		// }
		// }
	}

}
