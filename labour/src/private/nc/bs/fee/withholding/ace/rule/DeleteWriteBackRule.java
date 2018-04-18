package nc.bs.fee.withholding.ace.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.fee.ISubagreementMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.util.mmf.framework.db.SqlInUtil;
import nc.vo.fee.subagreement.SubagreementVO;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * 删除后规则，回写上游已预提金额，剩余可预提金额，已预提天数，剩余可预提天数
 * 
 * @author Administrator
 * 
 */
public class DeleteWriteBackRule implements IRule<AggWithholdingVO> {

	@Override
	public void process(AggWithholdingVO[] aggs) {
		if (!ArrayUtils.isEmpty(aggs)) {
			HashSet<String> csourcePKSet = new HashSet<String>();
			HashMap<String, Set<AggWithholdingVO>> srcpk2set = new HashMap<String, Set<AggWithholdingVO>>();
			for (AggWithholdingVO agg : aggs) {
				csourcePKSet.add(agg.getParentVO().getCsourceid());
				if (srcpk2set.keySet().contains(
						agg.getParentVO().getCsourceid())) {
					srcpk2set.get(agg.getParentVO().getCsourceid()).add(agg);
				} else {
					HashSet<AggWithholdingVO> setParam = new HashSet<AggWithholdingVO>();
					setParam.add(agg);
					srcpk2set.put(agg.getParentVO().getCsourceid(), setParam);
				}

			}
			String querySql = "select * from fee_subagreement t where nvl(t.dr,0)=0 and  t.pk_subagreement";
			SqlInUtil sqlInUtil = new SqlInUtil(
					csourcePKSet.toArray(new String[0]));
			try {
				String inSql = sqlInUtil.getInSql();
				querySql += inSql;
				BaseDAO baseDao = new BaseDAO();
				List<SubagreementVO> srcList = (List) baseDao.executeQuery(
						querySql, new BeanListProcessor(SubagreementVO.class));
				if (srcList.size() > 0) {
					ArrayList<SubagreementVO> writeBackList = new ArrayList<SubagreementVO>();
					HashMap<String, SubagreementVO> srcpk2srcvo = new HashMap<String, SubagreementVO>();
					for (SubagreementVO srcvo : srcList) {
						srcpk2srcvo.put(srcvo.getPk_subagreement(), srcvo);
					}
					for (String key : srcpk2set.keySet()) {
						SubagreementVO writeBackVO = srcpk2srcvo.get(key);
						Set<AggWithholdingVO> destset = srcpk2set.get(key);
						Integer withholdingdays = 0;
						UFDouble withholdingmny = UFDouble.ZERO_DBL;
						for (AggWithholdingVO destvo : destset) {
							withholdingdays += destvo.getParentVO()
									.getWithholdingdays();
							withholdingmny = withholdingmny.add(destvo
									.getParentVO().getWithholdingmny());
						}

						// 已预提天数
						writeBackVO.setUseddays(writeBackVO.getUseddays()
								- withholdingdays);
						// 剩余可预提天数
						writeBackVO.setActivedays(writeBackVO.getActivedays()
								+ withholdingdays);
						// 已预提金额
						writeBackVO.setUsedmny(writeBackVO.getUsedmny().sub(
								withholdingmny));
						// 剩余可预提金额
						writeBackVO.setActivemny(writeBackVO.getActivemny()
								.add(withholdingmny));
						writeBackList.add(writeBackVO);
					}
					if (writeBackList.size() > 0) {
						String[] fieldNames = new String[] { "useddays",
								"activedays", "usedmny", "activemny" };
						NCLocator
								.getInstance()
								.lookup(ISubagreementMaintain.class)
								.writeBackForWithholding(writeBackList,
										fieldNames);
					}
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ExceptionUtils.wrapException(e);
			}

		}

	}
}
