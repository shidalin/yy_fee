package nc.ui.fee.wagesappro.ace.ref;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.pubapp.uif2app.query2.model.IRefQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArBVO;

import org.apache.commons.lang.ArrayUtils;

public class FE03RefZ5ReferQueryService implements IRefQueryService {

	public FE03RefZ5ReferQueryService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISubagreementMaintain queryService = (ISubagreementMaintain) NCLocator
				.getInstance().lookup(ISubagreementMaintain.class);
		AggCtArVO[] ctvos = queryService.queryRefVOS(queryScheme);
		if (!ArrayUtils.isEmpty(ctvos)) {
			for (AggCtArVO ctvo : ctvos) {
				if (ctvo.getCtArBVO() != null && ctvo.getCtArBVO().length > 1) {
					ctvo.setCtArBVO(new CtArBVO[] { ctvo.getCtArBVO()[0] });
				}
			}
		}
		return ctvos;
	}

	@Override
	public Object[] queryByWhereSql(String arg0) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}
}
