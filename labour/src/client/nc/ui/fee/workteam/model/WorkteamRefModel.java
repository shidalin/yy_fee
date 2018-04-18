package nc.ui.fee.workteam.model;

import nc.ui.bd.ref.AbstractRefModel;

/**
 * �б��²��� �������
 * 
 * @author shidalin
 * 
 */
public class WorkteamRefModel extends AbstractRefModel {

	@Override
	public String getWherePart() {
		// ��������
		String wherePart = super.getWherePart();
		// ����dr���˺��Ƿ���鳤����
		if (null == wherePart || "".equals(wherePart.trim())) {
			wherePart = " isnull(dr,0) = 0 and isleader = 'Y' ";
		} else {
			wherePart += " and isnull(dr,0) = 0 and isleader = 'Y' ";
		}
		return wherePart;
	}

	@Override
	public String[] getHiddenFieldCode() {
		// ���������ֶ�
		String[] fields = new String[] { "pk_workteam" ,"pk_org"};
		return fields;
	}

	@Override
	public String[] getFieldCode() {
		// �����ֶ�
		String[] fields = new String[] { "name", "decode(sex,1,'��',2,'Ů')", "age", "isleader",
				"pleader" };
		return fields;
	}

	@Override
	public String getRefCodeField() {
		// ���ر����ֶ�
		return "name";
	}

	@Override
	public String getRefNameField() {
		// ���������ֶ�
		return "name";
	}

	@Override
	public String[] getFieldName() {
		// �����ֶ�����
		String[] fields = new String[] { "����", "�Ա�", "����", "�Ƿ���鳤", "���鳤" };
		return fields;
	}

	@Override
	public String getPkFieldCode() {
		// ���������ֶ�
		return "pk_workteam";
	}

	@Override
	public String getTableName() {
		// ���ձ�����
		return "fee_workteam";
	}

	@Override
	public String getRefTitle() {
		// ���ս�������
		return "���������Ϣ";
	}

}
