package nc.ui.fee.subagreement.ace.ref;

import java.awt.Container;

import nc.ui.pub.pf.BillSourceVar;
import nc.ui.pubapp.billref.src.view.SourceRefDlg;

/**
 ** ��Դ������ʾ�� ���ܣ���ѯ���ѡ������ ��д����getRefBillInfoBeanPath(), �����������������ļ�
 * 
 * @author shidl
 * 
 */

public class FE01RefZ5SourceRefDlg extends SourceRefDlg {

	public FE01RefZ5SourceRefDlg(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);
	}

	/**
	 * ���������ļ�
	 */
	@Override
	public String getRefBillInfoBeanPath() {
		return "nc/ui/fee/subagreement/ace/ref/FE01RefZ5.xml";
	}
}
