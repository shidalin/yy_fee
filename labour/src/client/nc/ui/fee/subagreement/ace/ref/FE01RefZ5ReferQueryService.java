package nc.ui.fee.subagreement.ace.ref;

import nc.bs.framework.common.NCLocator;
import nc.itf.fee.ISubagreementMaintain;
import nc.ui.pubapp.uif2app.query2.model.IRefQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ct.ar.entity.AggCtArVO;
import nc.vo.ct.ar.entity.CtArBVO;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;

import org.apache.commons.lang.ArrayUtils;

/**
 * 来源单据查询服务 功能：调用来源单据查询服务接口，实现来源单据查询
 * 
 * @author shidl
 * 
 */

public class FE01RefZ5ReferQueryService implements IRefQueryService {

	public FE01RefZ5ReferQueryService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		// 默认过滤条件
		// 1.价税合计>累计应收金额
		// 2.最新版本
		// 3.生效
		ISubagreementMaintain queryService = (ISubagreementMaintain) NCLocator
				.getInstance().lookup(ISubagreementMaintain.class);
		QuerySchemeProcessor querySchemeProcessor = new QuerySchemeProcessor(
				queryScheme);
		// 已经生成的不再重复生成
		String mainTableAlias = querySchemeProcessor.getMainTableAlias();
		// 表体已没有关联数据，改用表头服务号进行关联
		String whereSql = " and "
				+ " not exists (select t.serviceno from fee_subagreement t where nvl(t.dr,0)=0 and t.serviceno = "+mainTableAlias
				+ ".vbillcode)";
		querySchemeProcessor.appendWhere(whereSql);
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
