package nc.ui.fee.wagesappro.ace.ref;

import nc.ui.pubapp.billref.dest.DefaultBillDataLogic;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.pubapp.AppContext;

public class FE03RefZ5TransferBillDataLogic extends DefaultBillDataLogic {
	@Override
	public void doTransferAddLogic(Object selectedData) {
		// 1.填充单据类型信息
		AggWagesapproVO agg = (AggWagesapproVO) selectedData;
		agg.getParentVO().setPk_billtypecode("FE03");
		agg.getParentVO().setPk_billtypeid(
				nc.vo.am.common.util.BillTypeUtils.getPKByCode("FE03"));
		agg.getParentVO().setDbilldate(AppContext.getInstance().getBusiDate());
		// 把数据设置在界面上
		super.doTransferAddLogic(selectedData);
	}
}
