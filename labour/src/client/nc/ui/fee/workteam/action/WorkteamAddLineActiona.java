package nc.ui.fee.workteam.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pubapp.AppContext;
/**
  batch addLine or insLine action autogen
*/
public class WorkteamAddLineActiona extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		WorkteamVO singleDocVO = (WorkteamVO) obj;
		//singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
		//singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
		singleDocVO.setAttributeValue("pk_group", this.getModel().getContext().getPk_group());
		singleDocVO.setAttributeValue("pk_org", this.getModel().getContext().getPk_org());
    singleDocVO.setAttributeValue("creator", AppContext.getInstance().getPkUser());
    singleDocVO.setAttributeValue("creationtime", AppContext.getInstance().getBusiDate());
	}

}