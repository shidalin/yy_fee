package nc.bs.fee.subagreement.ace.rule;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.util.mmpub.dpub.db.SqlInUtil;
import nc.vo.fee.subagreement.AggSubagreementVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class FE01DeleteBeforeRule implements IRule<AggSubagreementVO> {

	public FE01DeleteBeforeRule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(AggSubagreementVO[] aggs) {
		try {
			this.checkUsedRecord(aggs);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.wrappException(e);
		}
	}

	/**
	 * ���ʹ�ü�¼
	 * 
	 * @throws BusinessException
	 */
	public void checkUsedRecord(AggSubagreementVO[] aggs)
			throws BusinessException {
		// ������ε����Ƿ��������
		if (org.apache.commons.lang.ArrayUtils.isEmpty(aggs)) {
			return;
		}
		ArrayList<String> arrayList = new ArrayList<String>();
		for (AggSubagreementVO agg : aggs) {
			arrayList.add(agg.getParentVO().getPk_subagreement());
		}
		String querySql = "select count(*) from fee_withholding t where nvl(t.dr,0)=0 and t.csourceid ";
		SqlInUtil sqlInUtil = new SqlInUtil(arrayList.toArray(new String[0]));
		String inSql = sqlInUtil.getInSql();
		querySql += inSql;
		// ���ú�̨У��
		Object result = NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(querySql, new ColumnProcessor());
		if (result != null && Integer.parseInt(result.toString()) > 0) {
			throw new BusinessException("��ǰ���ݴ������ε��ݣ�����ɾ��");
		}
	}

}
