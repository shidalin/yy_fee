package nc.impl.fee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.util.mmpub.dpub.db.SqlInUtil;
import nc.vo.fee.withholding.AggWithholdingVO;
import nc.vo.fee.withholding.WithholdingVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 会计平台查询实现
 * @author shidalin
 *
 */
public class FE02ReflectServiceIMpl implements IBillReflectorService {

	public FE02ReflectServiceIMpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> frvos) throws BusinessException {
		if (frvos != null && frvos.size() > 0) {
			ArrayList<String> arrayList = new ArrayList<String>();
			for (FipRelationInfoVO frvo : frvos) {
				String relationID = frvo.getRelationID();
				arrayList.add(relationID);
			}
			String querySql = "select  * from fee_withholding t where nvl(t.dr,0)=0 and t.pk_withholding";
			SqlInUtil sqlInUtil = new SqlInUtil(
					arrayList.toArray(new String[0]));
			String inSql = sqlInUtil.getInSql();
			querySql += inSql;
			List hvolist = (List) NCLocator
					.getInstance()
					.lookup(IUAPQueryBS.class)
					.executeQuery(querySql,
							new BeanListProcessor(WithholdingVO.class));
			if (hvolist != null && hvolist.size() > 0) {
				ArrayList<FipExtendAggVO> arrayList2 = new ArrayList<FipExtendAggVO>();
				for (Object obj : hvolist) {
					AggWithholdingVO aggWithholdingVO = new AggWithholdingVO();
					aggWithholdingVO
							.setParentVO((CircularlyAccessibleValueObject) obj);
					FipExtendAggVO vo = new FipExtendAggVO();
					vo.setBillVO(aggWithholdingVO);
					vo.setRelationID(((WithholdingVO) obj).getPk_withholding());
					arrayList2.add(vo);
				}
				return arrayList2;
			}
		}
		return null;
	}
}
