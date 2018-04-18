package nc.ui.fee.wagesappro.ace.ref;

import java.awt.Container;

import nc.ui.pub.pf.BillSourceVar;
import nc.ui.pubapp.billref.src.view.SourceRefDlg;

/**
 ** 来源单据显示类 功能：查询后的选择框界面 复写父类getRefBillInfoBeanPath(), 加载拉单参照配置文件
 * 
 * @author shidl
 * 
 */

public class FE03RefZ5SourceRefDlg extends SourceRefDlg {

	public FE03RefZ5SourceRefDlg(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);
	}

	/**
	 * 加载配置文件
	 */
	@Override
	public String getRefBillInfoBeanPath() {
		return "nc/ui/fee/wagesappro/ace/ref/FE03RefZ5.xml";
	}
}
