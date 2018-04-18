package nc.ui.fee.subagreement.ace.handler;

import java.util.Arrays;
import java.util.List;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;

/**
 * 表头表尾编辑前事件
 * 
 * @author shidalin
 * 
 */
public class AceHeadTailBeforeEditEventHandler implements
    IAppEventHandler<CardHeadTailBeforeEditEvent> {

  public AceHeadTailBeforeEditEventHandler() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
    // 注意，编辑前事件需要返回值
    e.setReturnValue(Boolean.TRUE);
    String key = e.getKey();
    BillCardPanel cardPanel = e.getBillCardPanel();
    String pk_org = cardPanel.getHeadItem("pk_org").getValue();
    List<String> list = Arrays.asList("pk_teamwork");
    // 参照前加组织过滤
    if (list.contains(key)) {
      UIRefPane ref = (UIRefPane) cardPanel.getHeadItem(key).getComponent();
      ref.getRefModel().setWherePart(" pk_org = '" + pk_org + "'");
      ref.setPk_org(pk_org);
    }

  }
}
