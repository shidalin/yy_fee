package nc.bs.fee.subagreement.ace.rule;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.impl.fee.Z5WriteBackParaForFE01Impl;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.fee.IZ5WriteBackParaForFE01;
import nc.itf.fee.IZ5WriteBackService;
import nc.itf.fee.RewriteRaraForZ5;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.fee.subagreement.SubagreementItemVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 删除后规则：1.校验是否被下游单据引用；2.更新上游其他收合同状态为“待进场”
 * 
 * @author shidalin
 * 
 */
public class FE01DeleteAfterRule implements IRule<AggSubagreementVO> {

	public FE01DeleteAfterRule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(AggSubagreementVO[] aggs) {
		IZ5WriteBackService writeBackService = NCLocator.getInstance().lookup(
				nc.itf.fee.IZ5WriteBackService.class);
		try {
			// 调用其他收合同回写服务
			writeBackService.writeBackForFE01(getWBPara(aggs));
		} catch (BusinessException e) {
			e.printStackTrace();
			ExceptionUtils.wrappException(e);
		}
	}

	/**
	 * 回写参数转换
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	public IZ5WriteBackParaForFE01[] getWBPara(AggSubagreementVO[] aggs) throws BusinessException {
		if (aggs != null && aggs.length > 0) {
			ArrayList<IZ5WriteBackParaForFE01> wbParaList = new ArrayList<IZ5WriteBackParaForFE01>();
			String projectStatus = this.getProjectStatus("SY_XMZT", "SY_XMZT02");
			for (AggSubagreementVO agg : aggs) {
				CircularlyAccessibleValueObject[] childrenVOs = agg
						.getChildrenVO();
				for (CircularlyAccessibleValueObject childrenVO : childrenVOs) {
					SubagreementItemVO itemVO = (SubagreementItemVO) childrenVO;
					RewriteRaraForZ5 rewriteRaraForZ5 = new RewriteRaraForZ5();
					rewriteRaraForZ5.setCsrcbid(itemVO.getCsourcebid());
					rewriteRaraForZ5.setCsrcid(itemVO.getCsourceid());
					// 自定义档案 SY_XMZT
					// 待进场 SY_XMZT02
					rewriteRaraForZ5.setProjectStatus(projectStatus);
					rewriteRaraForZ5.setVsrctype(itemVO.getCsourcetypecode());
					IZ5WriteBackParaForFE01 z5WriteBackParaForFE01 = new Z5WriteBackParaForFE01Impl(
							rewriteRaraForZ5);
					wbParaList.add(z5WriteBackParaForFE01);
				}
			}
			return wbParaList.toArray(new IZ5WriteBackParaForFE01[0]);
		}
		return null;
	}

	public String getProjectStatus(String docListCode, String docCode)
			throws BusinessException {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("	select t1.pk_defdoc	");
		stringBuffer.append("	  from bd_defdoc t1	");
		stringBuffer.append("	 inner join bd_defdoclist t2	");
		stringBuffer.append("	    on t1.pk_defdoclist = t2.pk_defdoclist	");
		stringBuffer.append("	 where nvl(t1.dr, 0) = 0	");
		stringBuffer.append("	   and nvl(t2.dr, 0) = 0	");
		stringBuffer.append("	   and t2.code = ?	");
		stringBuffer.append("	   and t1.code = ?	");
		String querySql = stringBuffer.toString();
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(docListCode);
		sqlParameter.addParam(docCode);
		Object result = NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(querySql, sqlParameter, new ColumnProcessor());
		return result == null ? "" : result.toString();

	}
}
