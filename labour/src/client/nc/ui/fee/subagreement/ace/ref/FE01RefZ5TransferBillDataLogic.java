package nc.ui.fee.subagreement.ace.ref;

import nc.ui.pubapp.billref.dest.DefaultBillDataLogic;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.pubapp.AppContext;

public class FE01RefZ5TransferBillDataLogic extends DefaultBillDataLogic {
	@Override
	public void doTransferAddLogic(Object selectedData) {
		// 1.填充单据类型信息
		AggSubagreementVO agg = (AggSubagreementVO) selectedData;
		agg.getParentVO().setPk_billtypecode("FE01");
		agg.getParentVO().setPk_billtypeid(
				nc.vo.am.common.util.BillTypeUtils.getPKByCode("FE01"));
		agg.getParentVO().setDbilldate(AppContext.getInstance().getBusiDate());
		// 把数据设置在界面上
		super.doTransferAddLogic(selectedData);
	}
}
