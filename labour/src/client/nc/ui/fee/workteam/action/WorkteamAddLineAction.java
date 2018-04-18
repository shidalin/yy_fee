package nc.ui.fee.workteam.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.fee.workteam.WorkteamVO;
import nc.vo.pubapp.AppContext;

/**
 * batch addLine or insLine action autogen
 */
public class WorkteamAddLineAction extends BatchAddLineAction {

  private static final long serialVersionUID = 1L;

  private BatchBillTable editor = null;

  @Override
  protected void setDefaultData(Object obj) {
    super.setDefaultData(obj);
    WorkteamVO singleDocVO = (WorkteamVO) obj;
    // singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
    // singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
    singleDocVO.setAttributeValue("pk_group", this.getModel().getContext()
        .getPk_group());
    singleDocVO.setAttributeValue("pk_org", this.getModel().getContext()
        .getPk_org());
    singleDocVO.setAttributeValue("creator", AppContext.getInstance()
        .getPkUser());
    singleDocVO.setAttributeValue("creationtime", AppContext.getInstance()
        .getBusiDate());
    this.getEditor().getBillCardPanel().getBodyItem("pleader").setEdit(true);
  }

  @Override
  public void doAction(ActionEvent e) throws Exception {
    super.doAction(e);
    // 新增班组长必选
    this.getEditor().getBillCardPanel().getBodyItem("pleader").setEdit(true);
  }

  public BatchBillTable getEditor() {
    return this.editor;
  }

  public void setEditor(BatchBillTable editor) {
    this.editor = editor;
  }

}
