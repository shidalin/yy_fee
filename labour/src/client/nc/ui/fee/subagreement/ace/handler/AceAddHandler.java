package nc.ui.fee.subagreement.ace.handler;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.billform.AddEvent;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class AceAddHandler implements IAppEventHandler<AddEvent> {

	@Override
	public void handleAppEvent(AddEvent e) {
		String pk_group = e.getContext().getPk_group();
		String pk_org = e.getContext().getPk_org();
		BillCardPanel panel = e.getBillForm().getBillCardPanel();
		// ��������֯Ĭ��ֵ
		panel.setHeadItem("pk_group", pk_group);
		panel.setHeadItem("pk_org", pk_org);
		// ����ҵ��Ա������Ա/������Ϣ
		String pk_user = e.getContext().getPk_loginUser();
		try {
			Map map = NCLocator.getInstance()
					.lookup(ISubagreementMaintain.class)
					.queryPsnDeptByUser(pk_user);
			if (map != null) {
				panel.setHeadItem("pk_psndoc", map.get("pk_psndoc"));
				panel.setHeadItem("pk_dept", map.get("pk_dept"));
			}
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			ExceptionUtils.wrappException(e1);
		}
		// ǩԼ����Ĭ��ֵ����
		UFDate busiDate = AppContext.getInstance().getBusiDate();
		panel.setHeadItem("signdate", busiDate);
		// ��������Ĭ��ֵ����
		panel.setHeadItem("dbilldate", busiDate);
	}
}
