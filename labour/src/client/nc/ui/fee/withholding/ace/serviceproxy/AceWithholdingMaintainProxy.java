package nc.ui.fee.withholding.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWithholdingMaintain;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.fee.withholding.AggWithholdingVO;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceWithholdingMaintainProxy implements IQueryService,
		ISingleBillService<AggWithholdingVO> {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IWithholdingMaintain query = NCLocator.getInstance().lookup(
				IWithholdingMaintain.class);
		return query.query(queryScheme);
	}

	@Override
	public AggWithholdingVO operateBill(AggWithholdingVO arg0) throws Exception {
		NCLocator
				.getInstance()
				.lookup(IWithholdingMaintain.class)
				.delete(new AggWithholdingVO[] { arg0 },
						new AggWithholdingVO[] { arg0 });
		return arg0;
	}

}