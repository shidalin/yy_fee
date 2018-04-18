package nc.bs.fee.wagesappro.ace.rule;

import nc.bs.framework.common.NCLocator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.pubitf.fip.service.IFipMessageService;
import nc.vo.fee.wagesappro.AggWagesapproVO;
import nc.vo.fee.wagesappro.WagesapproVO;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.ArrayUtils;

/**
 * 审批后规则，发送会计平台
 * 
 * @author shidalin
 * 
 */
public class ApprovedAfterRule implements IRule<AggWagesapproVO> {

	@Override
	public void process(AggWagesapproVO[] aggs) {
		if (!ArrayUtils.isEmpty(aggs)) {
			IFipMessageService itf = NCLocator.getInstance().lookup(
					IFipMessageService.class);
			for (AggWagesapproVO agg : aggs) {
				WagesapproVO hvo = agg.getParentVO();
				// 只有审批通过才发送会计平台
				if (hvo.getVbillstatus() != 1) {
					continue;
				}
				// 构造数据1
				FipRelationInfoVO reVO = new FipRelationInfoVO();
				reVO.setPk_group(hvo.getPk_group());
				reVO.setPk_org(hvo.getPk_org());
				reVO.setRelationID(hvo.getPk_wagesappro());
				reVO.setPk_system("FEE");
				reVO.setBusidate(hvo.getDbilldate() == null ? AppContext
						.getInstance().getBusiDate() : hvo.getDbilldate());
				reVO.setPk_billtype(hvo.getPk_billtypecode() == null ? "FE03"
						: hvo.getPk_billtypecode());
				reVO.setPk_operator(hvo.getCreator());
				// 构造数据2
				FipMessageVO fipMessageVO = new FipMessageVO();
				fipMessageVO.setMessagetype(FipMessageVO.MESSAGETYPE_ADD);
				fipMessageVO.setBillVO(agg);
				fipMessageVO.setMessageinfo(reVO);
				try {

					itf.sendMessage(fipMessageVO);
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ExceptionUtils.wrappException(e);
				}
			}
		}

	}
}
