package nc.ui.fee.withholding.ace.ref;

import java.awt.Container;

import nc.ui.pub.pf.BillSourceVar;
import nc.ui.pubapp.billref.src.view.SourceRefDlg;

/**
 ** ��Դ������ʾ�� ���ܣ���ѯ���ѡ������ ��д����getRefBillInfoBeanPath(), �����������������ļ�
 * 
 * @author shidl
 * 
 */

public class FE02RefFE01SourceRefDlg extends SourceRefDlg {

	public FE02RefFE01SourceRefDlg(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);
	}

	/**
	 * ���������ļ�
	 */
	@Override
	public String getRefBillInfoBeanPath() {
		return "nc/ui/fee/withholding/ace/ref/FE02RefFE01.xml";
	}
}
