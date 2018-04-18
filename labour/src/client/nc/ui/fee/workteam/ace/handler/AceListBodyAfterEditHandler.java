package nc.ui.fee.workteam.ace.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.pub.lang.UFBoolean;

/**
 * 班组列表表体编辑前事件
 * 
 * @author shidalin
 * 
 */
public class AceListBodyAfterEditHandler implements
		IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		String key = e.getKey();
		int row = e.getRow();
	}

}
