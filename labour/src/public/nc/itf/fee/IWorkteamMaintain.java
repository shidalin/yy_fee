package nc.itf.fee;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.fee.workteam.WorkteamVO;

public interface IWorkteamMaintain extends ISmartService {

	public WorkteamVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public void queryReferencingInfo(WorkteamVO[] vos) throws BusinessException;
}