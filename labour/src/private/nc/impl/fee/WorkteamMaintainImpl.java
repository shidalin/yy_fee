package nc.impl.fee;

import org.apache.commons.lang.ArrayUtils;

import nc.impl.pub.ace.AceWorkteamPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.fee.workteam.WorkteamVO;
import nc.itf.fee.IWorkteamMaintain;

public class WorkteamMaintainImpl extends AceWorkteamPubServiceImpl implements
		IWorkteamMaintain {

	@Override
	public WorkteamVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		BatchSaveAction<WorkteamVO> saveAction = new BatchSaveAction<WorkteamVO>();
		Object[] updObjs = batchVO.getUpdObjs();
		if (!ArrayUtils.isEmpty(updObjs)) {
			for (Object obj : updObjs) {
				((WorkteamVO) obj).setModifiedtime(AppContext.getInstance()
						.getServerTime());
				((WorkteamVO) obj).setModifier(AppContext.getInstance()
						.getPkUser());
			}
		}
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public void queryReferencingInfo(WorkteamVO[] vos) throws BusinessException {
		// TODO Auto-generated method stub
		try {
			super.queryReferencingInfo(vos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ExceptionUtils.marsh(e);
		}
	}
}
