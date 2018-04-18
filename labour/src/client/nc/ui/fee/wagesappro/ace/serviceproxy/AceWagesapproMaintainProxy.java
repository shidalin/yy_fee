package nc.ui.fee.wagesappro.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.IWagesapproMaintain;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceWagesapproMaintainProxy implements IQueryService {
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		IWagesapproMaintain query = NCLocator.getInstance().lookup(
				IWagesapproMaintain.class);
		return query.query(queryScheme);
	}

}