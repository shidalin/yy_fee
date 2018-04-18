package nc.ui.fee.workteam.model;

import nc.ui.bd.ref.AbstractRefModel;

/**
 * �б��²��� �����պ�ͬ����
 * 
 * @author shidalin
 * 
 */
public class ProjectStyleRefModel extends AbstractRefModel {

	@Override
	public String getWherePart() {
		// ��������
		String wherePart = super.getWherePart();
		// ����dr���˺��Ƿ����°汾��
		if (null == wherePart || "".equals(wherePart.trim())) {
			wherePart = " isnull(dr,0) = 0 and pk_defdoclist = ( select t.pk_defdoclist from bd_defdoclist t  where nvl(t.dr,0) =0 and  t.code = 'SY_XMLX' ) ";
		} else {
			wherePart += " and isnull(dr,0) = 0 and and pk_defdoclist = ( select t.pk_defdoclist from bd_defdoclist t  where nvl(t.dr,0) =0 and  t.code = 'SY_XMLX' ) ";
		}
		return wherePart;
	}

	@Override
	public String getRefCodeField() {
		// ���ر����ֶ�
		return "code";
	}

	@Override
	public String getRefNameField() {
		// ���������ֶ�
		return "name";
	}

	@Override
	public String[] getHiddenFieldCode() {
		// ���������ֶ�
		String[] fields = new String[] { "pk_defdoc" };
		return fields;
	}

	@Override
	public String[] getFieldCode() {
		// �����ֶ�
		String[] fields = new String[] { "code", "name" };
		return fields;
	}

	@Override
	public String[] getFieldName() {
		// �����ֶ�����
		String[] fields = new String[] { "����", "����"};
		return fields;
	}

	@Override
	public String getPkFieldCode() {
		// ���������ֶ�
		return "pk_defdoc";
	}

	@Override
	public String getTableName() {
		// ���ձ�����
		return "bd_defdoc";
	}

	@Override
	public String getRefTitle() {
		// ���ս�������
		return "��Ŀ�����Զ��嵵������";
	}

}
