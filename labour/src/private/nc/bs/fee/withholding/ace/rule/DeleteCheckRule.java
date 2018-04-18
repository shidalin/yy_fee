package nc.bs.fee.withholding.ace.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.util.mmf.framework.db.SqlInUtil;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.ArrayUtils;

/**
 * 删除校验类，已确认的单据不可删除
 * 
 * @author shidalin
 * 
 */
public class DeleteCheckRule implements IRule<AggWithholdingVO> {

	@Override
	public void process(AggWithholdingVO[] aggs) {
		checkNextWithholdingData(aggs);
		deleteCheck(aggs);

	}

	/**
	 * 检查是否存在下游预提单，若存在，不可取消
	 * 
	 * @throws BusinessException
	 */
	private void checkNextWithholdingData(AggWithholdingVO[] aggs) {
		if (!ArrayUtils.isEmpty(aggs)) {
			HashMap<String, Set<AggWithholdingVO>> srcpk2set = new HashMap<String, Set<AggWithholdingVO>>();
			for (AggWithholdingVO agg : aggs) {
				if (srcpk2set.keySet().contains(
						agg.getParentVO().getCsourceid())) {
					srcpk2set.get(agg.getParentVO().getCsourceid()).add(agg);
				} else {
					HashSet<AggWithholdingVO> setParam = new HashSet<AggWithholdingVO>();
					setParam.add(agg);
					srcpk2set.put(agg.getParentVO().getCsourceid(), setParam);
				}
			}
			SqlInUtil sqlInUtil = new SqlInUtil(srcpk2set.keySet().toArray(
					new String[0]));
			String querySql = "select t.csourceid,max(t.enddate) maxenddate from fee_withholding t where nvl(t.dr,0) = 0 group by t.csourceid having t.csourceid";
			try {
				querySql += sqlInUtil.getInSql();
				List list = (List) NCLocator.getInstance()
						.lookup(IUAPQueryBS.class)
						.executeQuery(querySql, new MapListProcessor());
				HashSet<String> pkset = new HashSet<String>();
				for (Object object : list) {
					Map map = (Map) object;
					String csourceid = (String) map.get("csourceid");
					UFDate maxenddate = new UFDate(
							(String) map.get("maxenddate"));
					HashSet<AggWithholdingVO> setParam = (HashSet<AggWithholdingVO>) srcpk2set
							.get(csourceid);
					for (AggWithholdingVO destvo : setParam) {
						if (maxenddate.after(destvo.getParentVO().getEnddate())) {
							pkset.add(destvo.getParentVO().getVbillcode());
						}
					}
				}
				if (pkset.size() > 0) {
					SqlInUtil sqlInUtilForpkset = new SqlInUtil(
							pkset.toArray(new String[0]));
					String insqlForpkset = sqlInUtilForpkset.getInSql();
					insqlForpkset = insqlForpkset.substring(
							insqlForpkset.indexOf("(") + 1,
							insqlForpkset.indexOf(")"));
					String message = "删除失败！存在靠后预提单不可删除，预提单单据号：" + insqlForpkset;
					throw new BusinessException(message);
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ExceptionUtils.wrappException(e);
			}
		}
	}

	/**
	 * 删除前校验，存在下游单据不可以删除
	 * 
	 * @param aggs
	 */
	private void deleteCheck(AggWithholdingVO[] aggs) {
		if (!ArrayUtils.isEmpty(aggs)) {
			ArrayList<String> arrayList = new ArrayList<String>();
			for (AggWithholdingVO agg : aggs) {
				String vbillcode = agg.getParentVO().getVbillcode();
				UFBoolean isaffirm = agg.getParentVO().getIsaffirm();
				if (isaffirm.booleanValue()) {
					arrayList.add(vbillcode);
				}
			}
			if (arrayList.size() > 0) {
				String message = "已经确认的单据不可删除，单据号为：\n";
				for (String vbillcode : arrayList) {
					message += vbillcode;
					if (arrayList.indexOf(vbillcode) == arrayList.size() - 1) {
						message += ",";
					}
				}
				ExceptionUtils.wrappBusinessException(message);
			}
		}
	}

}
