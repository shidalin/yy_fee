package nc.vo.fee.wagesappro;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 * �˴�����۵�������Ϣ
 * </p>
 * ��������:2017-11-18
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class WagesapproVO extends SuperVO {

	/**
	 * ����
	 */
	public java.lang.String pk_wagesappro;
	/**
	 * ����
	 */
	public java.lang.String pk_group;
	/**
	 * ��֯
	 */
	public java.lang.String pk_org;
	/**
	 * ��֯��汾
	 */
	public java.lang.String pk_org_v;
	/**
	 * ���ݺ�
	 */
	public java.lang.String vbillcode;
	/**
	 * ��ͬ���
	 */
	public java.lang.String contratcno;
	/**
	 * ���񿨺�
	 */
	public java.lang.String serviceno;
	/**
	 * ��������
	 */
	public UFDate workstartdate;
	/**
	 * ��������
	 */
	public UFDate workenddate;
	/**
	 * ���ʷ���ʱ��
	 */
	public java.lang.String wageperiod;
	/**
	 * �˹��Ѵ���
	 */
	public java.lang.Integer feecount;
	/**
	 * �ͻ�
	 */
	public java.lang.String pk_customer;
	/**
	 * ��ַ
	 */
	public java.lang.String address;
	/**
	 * ��������
	 */
	public UFDate dbilldate;
	/**
	 * ����״̬
	 */
	public java.lang.Integer vbillstatus;
	/**
	 * �������ͱ���
	 */
	public java.lang.String pk_billtypecode;
	/**
	 * ��������
	 */
	public java.lang.String pk_billtypeid;
	/**
	 * ������
	 */
	public java.lang.String creator;
	/**
	 * ����ʱ��
	 */
	public UFDateTime creationtime;
	/**
	 * ����޸���
	 */
	public java.lang.String modifier;
	/**
	 * ����޸�ʱ��
	 */
	public UFDateTime modifiedtime;
	/**
	 * �����
	 */
	public java.lang.String vapproveid;
	/**
	 * ���ʱ��
	 */
	public UFDateTime tapprovetime;
	/**
	 * ��������
	 */
	public java.lang.String vapprovenote;
	/**
	 * ��ע
	 */
	
	
	public java.lang.String vmemo;
	
	/**
	*��Դ���ݺ�
	*/
	public java.lang.String vsourcecode;
	/**
	*��Դ����
	*/
	public java.lang.String csourceid;
	/**
	*��Դ��������
	*/
	public java.lang.String csourcetypecode;
	/**
	 * �Զ�����1
	 */
	public java.lang.String vdef1;
	/**
	 * �Զ�����2
	 */
	public java.lang.String vdef2;
	/**
	 * �Զ�����3
	 */
	public java.lang.String vdef3;
	/**
	 * �Զ�����4
	 */
	public java.lang.String vdef4;
	/**
	 * �Զ�����5
	 */
	public java.lang.String vdef5;
	/**
	 * �Զ�����6
	 */
	public java.lang.String vdef6;
	/**
	 * �Զ�����7
	 */
	public java.lang.String vdef7;
	/**
	 * �Զ�����8
	 */
	public java.lang.String vdef8;
	/**
	 * �Զ�����9
	 */
	public java.lang.String vdef9;
	/**
	 * �Զ�����10
	 */
	public java.lang.String vdef10;
	/**
	 * �Զ�����11
	 */
	public java.lang.String vdef11;
	/**
	 * �Զ�����12
	 */
	public java.lang.String vdef12;
	/**
	 * �Զ�����13
	 */
	public java.lang.String vdef13;
	/**
	 * �Զ�����14
	 */
	public java.lang.String vdef14;
	/**
	 * �Զ�����15
	 */
	public java.lang.String vdef15;
	/**
	 * �Զ�����16
	 */
	public java.lang.String vdef16;
	/**
	 * �Զ�����17
	 */
	public java.lang.String vdef17;
	/**
	 * �Զ�����18
	 */
	public java.lang.String vdef18;
	/**
	 * �Զ�����19
	 */
	public java.lang.String vdef19;
	/**
	 * �Զ�����20
	 */
	public java.lang.String vdef20;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_wagesappro��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wagesappro() {
		return this.pk_wagesappro;
	}

	/**
	 * ����pk_wagesappro��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newPk_wagesappro
	 *            java.lang.String
	 */
	public void setPk_wagesappro(java.lang.String pk_wagesappro) {
		this.pk_wagesappro = pk_wagesappro;
	}

	/**
	 * ���� pk_group��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public java.lang.String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(java.lang.String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.����������֯ ��������:2017-11-18
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public java.lang.String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.����������֯ ��������:2017-11-18
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(java.lang.String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ���� pk_org_v��Getter����.����������֯��汾 ��������:2017-11-18
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public java.lang.String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ����pk_org_v��Setter����.����������֯��汾 ��������:2017-11-18
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(java.lang.String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * ���� contratcno��Getter����.����������ͬ��� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getContratcno() {
		return this.contratcno;
	}

	/**
	 * ����contratcno��Setter����.����������ͬ��� ��������:2017-11-18
	 * 
	 * @param newContratcno
	 *            java.lang.String
	 */
	public void setContratcno(java.lang.String contratcno) {
		this.contratcno = contratcno;
	}

	/**
	 * ���� serviceno��Getter����.�����������񿨺� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getServiceno() {
		return this.serviceno;
	}

	/**
	 * ����serviceno��Setter����.�����������񿨺� ��������:2017-11-18
	 * 
	 * @param newServiceno
	 *            java.lang.String
	 */
	public void setServiceno(java.lang.String serviceno) {
		this.serviceno = serviceno;
	}

	/**
	 * ���� workstartdate��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getWorkstartdate() {
		return this.workstartdate;
	}

	/**
	 * ����workstartdate��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newWorkstartdate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setWorkstartdate(UFDate workstartdate) {
		this.workstartdate = workstartdate;
	}

	/**
	 * ���� workenddate��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getWorkenddate() {
		return this.workenddate;
	}

	/**
	 * ����workenddate��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newWorkenddate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setWorkenddate(UFDate workenddate) {
		this.workenddate = workenddate;
	}

	/**
	 * ���� wageperiod��Getter����.�����������ʷ���ʱ�� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getWageperiod() {
		return this.wageperiod;
	}

	/**
	 * ����wageperiod��Setter����.�����������ʷ���ʱ�� ��������:2017-11-18
	 * 
	 * @param newWageperiod
	 *            java.lang.String
	 */
	public void setWageperiod(java.lang.String wageperiod) {
		this.wageperiod = wageperiod;
	}

	/**
	 * ���� feecount��Getter����.���������˹��Ѵ��� ��������:2017-11-18
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getFeecount() {
		return this.feecount;
	}

	/**
	 * ����feecount��Setter����.���������˹��Ѵ��� ��������:2017-11-18
	 * 
	 * @param newFeecount
	 *            java.lang.Integer
	 */
	public void setFeecount(java.lang.Integer feecount) {
		this.feecount = feecount;
	}

	/**
	 * ���� pk_customer��Getter����.���������ͻ� ��������:2017-11-18
	 * 
	 * @return nc.vo.bd.cust.CustomerVO
	 */
	public java.lang.String getPk_customer() {
		return this.pk_customer;
	}

	/**
	 * ����pk_customer��Setter����.���������ͻ� ��������:2017-11-18
	 * 
	 * @param newPk_customer
	 *            nc.vo.bd.cust.CustomerVO
	 */
	public void setPk_customer(java.lang.String pk_customer) {
		this.pk_customer = pk_customer;
	}

	/**
	 * ���� address��Getter����.����������ַ ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getAddress() {
		return this.address;
	}

	/**
	 * ����address��Setter����.����������ַ ��������:2017-11-18
	 * 
	 * @param newAddress
	 *            java.lang.String
	 */
	public void setAddress(java.lang.String address) {
		this.address = address;
	}

	/**
	 * ���� dbilldate��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getDbilldate() {
		return this.dbilldate;
	}

	/**
	 * ����dbilldate��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newDbilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setDbilldate(UFDate dbilldate) {
		this.dbilldate = dbilldate;
	}

	/**
	 * ���� vbillstatus��Getter����.������������״̬ ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.pf.BillStatusEnum
	 */
	public java.lang.Integer getVbillstatus() {
		return this.vbillstatus;
	}

	/**
	 * ����vbillstatus��Setter����.������������״̬ ��������:2017-11-18
	 * 
	 * @param newVbillstatus
	 *            nc.vo.pub.pf.BillStatusEnum
	 */
	public void setVbillstatus(java.lang.Integer vbillstatus) {
		this.vbillstatus = vbillstatus;
	}

	/**
	 * ���� pk_billtypecode��Getter����.���������������ͱ��� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billtypecode() {
		return this.pk_billtypecode;
	}

	/**
	 * ����pk_billtypecode��Setter����.���������������ͱ��� ��������:2017-11-18
	 * 
	 * @param newPk_billtypecode
	 *            java.lang.String
	 */
	public void setPk_billtypecode(java.lang.String pk_billtypecode) {
		this.pk_billtypecode = pk_billtypecode;
	}

	/**
	 * ���� pk_billtypeid��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.billtype.BilltypeVO
	 */
	public java.lang.String getPk_billtypeid() {
		return this.pk_billtypeid;
	}

	/**
	 * ����pk_billtypeid��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newPk_billtypeid
	 *            nc.vo.pub.billtype.BilltypeVO
	 */
	public void setPk_billtypeid(java.lang.String pk_billtypeid) {
		this.pk_billtypeid = pk_billtypeid;
	}

	/**
	 * ���� creator��Getter����.�������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public java.lang.String getCreator() {
		return this.creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:2017-11-18
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(java.lang.String creator) {
		this.creator = creator;
	}

	/**
	 * ���� creationtime��Getter����.������������ʱ�� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ����creationtime��Setter����.������������ʱ�� ��������:2017-11-18
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ���� modifier��Getter����.������������޸��� ��������:2017-11-18
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public java.lang.String getModifier() {
		return this.modifier;
	}

	/**
	 * ����modifier��Setter����.������������޸��� ��������:2017-11-18
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(java.lang.String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ���� modifiedtime��Getter����.������������޸�ʱ�� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.������������޸�ʱ�� ��������:2017-11-18
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ���� vapproveid��Getter����.������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public java.lang.String getVapproveid() {
		return this.vapproveid;
	}

	/**
	 * ����vapproveid��Setter����.������������� ��������:2017-11-18
	 * 
	 * @param newVapproveid
	 *            nc.vo.sm.UserVO
	 */
	public void setVapproveid(java.lang.String vapproveid) {
		this.vapproveid = vapproveid;
	}

	/**
	 * ���� tapprovetime��Getter����.�����������ʱ�� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTapprovetime() {
		return this.tapprovetime;
	}

	/**
	 * ����tapprovetime��Setter����.�����������ʱ�� ��������:2017-11-18
	 * 
	 * @param newTapprovetime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTapprovetime(UFDateTime tapprovetime) {
		this.tapprovetime = tapprovetime;
	}

	/**
	 * ���� vapprovenote��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVapprovenote() {
		return this.vapprovenote;
	}

	/**
	 * ����vapprovenote��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newVapprovenote
	 *            java.lang.String
	 */
	public void setVapprovenote(java.lang.String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	/**
	 * ���� vmemo��Getter����.����������ע ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVmemo() {
		return this.vmemo;
	}

	/**
	 * ����vmemo��Setter����.����������ע ��������:2017-11-18
	 * 
	 * @param newVmemo
	 *            java.lang.String
	 */
	public void setVmemo(java.lang.String vmemo) {
		this.vmemo = vmemo;
	}

	/**
	 * ���� vdef1��Getter����.���������Զ�����1 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef1() {
		return this.vdef1;
	}

	/**
	 * ����vdef1��Setter����.���������Զ�����1 ��������:2017-11-18
	 * 
	 * @param newVdef1
	 *            java.lang.String
	 */
	public void setVdef1(java.lang.String vdef1) {
		this.vdef1 = vdef1;
	}

	/**
	 * ���� vdef2��Getter����.���������Զ�����2 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef2() {
		return this.vdef2;
	}

	/**
	 * ����vdef2��Setter����.���������Զ�����2 ��������:2017-11-18
	 * 
	 * @param newVdef2
	 *            java.lang.String
	 */
	public void setVdef2(java.lang.String vdef2) {
		this.vdef2 = vdef2;
	}

	/**
	 * ���� vdef3��Getter����.���������Զ�����3 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef3() {
		return this.vdef3;
	}

	/**
	 * ����vdef3��Setter����.���������Զ�����3 ��������:2017-11-18
	 * 
	 * @param newVdef3
	 *            java.lang.String
	 */
	public void setVdef3(java.lang.String vdef3) {
		this.vdef3 = vdef3;
	}

	/**
	 * ���� vdef4��Getter����.���������Զ�����4 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef4() {
		return this.vdef4;
	}

	/**
	 * ����vdef4��Setter����.���������Զ�����4 ��������:2017-11-18
	 * 
	 * @param newVdef4
	 *            java.lang.String
	 */
	public void setVdef4(java.lang.String vdef4) {
		this.vdef4 = vdef4;
	}

	/**
	 * ���� vdef5��Getter����.���������Զ�����5 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef5() {
		return this.vdef5;
	}

	/**
	 * ����vdef5��Setter����.���������Զ�����5 ��������:2017-11-18
	 * 
	 * @param newVdef5
	 *            java.lang.String
	 */
	public void setVdef5(java.lang.String vdef5) {
		this.vdef5 = vdef5;
	}

	/**
	 * ���� vdef6��Getter����.���������Զ�����6 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef6() {
		return this.vdef6;
	}

	/**
	 * ����vdef6��Setter����.���������Զ�����6 ��������:2017-11-18
	 * 
	 * @param newVdef6
	 *            java.lang.String
	 */
	public void setVdef6(java.lang.String vdef6) {
		this.vdef6 = vdef6;
	}

	/**
	 * ���� vdef7��Getter����.���������Զ�����7 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef7() {
		return this.vdef7;
	}

	/**
	 * ����vdef7��Setter����.���������Զ�����7 ��������:2017-11-18
	 * 
	 * @param newVdef7
	 *            java.lang.String
	 */
	public void setVdef7(java.lang.String vdef7) {
		this.vdef7 = vdef7;
	}

	/**
	 * ���� vdef8��Getter����.���������Զ�����8 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef8() {
		return this.vdef8;
	}

	/**
	 * ����vdef8��Setter����.���������Զ�����8 ��������:2017-11-18
	 * 
	 * @param newVdef8
	 *            java.lang.String
	 */
	public void setVdef8(java.lang.String vdef8) {
		this.vdef8 = vdef8;
	}

	/**
	 * ���� vdef9��Getter����.���������Զ�����9 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef9() {
		return this.vdef9;
	}

	/**
	 * ����vdef9��Setter����.���������Զ�����9 ��������:2017-11-18
	 * 
	 * @param newVdef9
	 *            java.lang.String
	 */
	public void setVdef9(java.lang.String vdef9) {
		this.vdef9 = vdef9;
	}

	/**
	 * ���� vdef10��Getter����.���������Զ�����10 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef10() {
		return this.vdef10;
	}

	/**
	 * ����vdef10��Setter����.���������Զ�����10 ��������:2017-11-18
	 * 
	 * @param newVdef10
	 *            java.lang.String
	 */
	public void setVdef10(java.lang.String vdef10) {
		this.vdef10 = vdef10;
	}

	/**
	 * ���� vdef11��Getter����.���������Զ�����11 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef11() {
		return this.vdef11;
	}

	/**
	 * ����vdef11��Setter����.���������Զ�����11 ��������:2017-11-18
	 * 
	 * @param newVdef11
	 *            java.lang.String
	 */
	public void setVdef11(java.lang.String vdef11) {
		this.vdef11 = vdef11;
	}

	/**
	 * ���� vdef12��Getter����.���������Զ�����12 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef12() {
		return this.vdef12;
	}

	/**
	 * ����vdef12��Setter����.���������Զ�����12 ��������:2017-11-18
	 * 
	 * @param newVdef12
	 *            java.lang.String
	 */
	public void setVdef12(java.lang.String vdef12) {
		this.vdef12 = vdef12;
	}

	/**
	 * ���� vdef13��Getter����.���������Զ�����13 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef13() {
		return this.vdef13;
	}

	/**
	 * ����vdef13��Setter����.���������Զ�����13 ��������:2017-11-18
	 * 
	 * @param newVdef13
	 *            java.lang.String
	 */
	public void setVdef13(java.lang.String vdef13) {
		this.vdef13 = vdef13;
	}

	/**
	 * ���� vdef14��Getter����.���������Զ�����14 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef14() {
		return this.vdef14;
	}

	/**
	 * ����vdef14��Setter����.���������Զ�����14 ��������:2017-11-18
	 * 
	 * @param newVdef14
	 *            java.lang.String
	 */
	public void setVdef14(java.lang.String vdef14) {
		this.vdef14 = vdef14;
	}

	/**
	 * ���� vdef15��Getter����.���������Զ�����15 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef15() {
		return this.vdef15;
	}

	/**
	 * ����vdef15��Setter����.���������Զ�����15 ��������:2017-11-18
	 * 
	 * @param newVdef15
	 *            java.lang.String
	 */
	public void setVdef15(java.lang.String vdef15) {
		this.vdef15 = vdef15;
	}

	/**
	 * ���� vdef16��Getter����.���������Զ�����16 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef16() {
		return this.vdef16;
	}

	/**
	 * ����vdef16��Setter����.���������Զ�����16 ��������:2017-11-18
	 * 
	 * @param newVdef16
	 *            java.lang.String
	 */
	public void setVdef16(java.lang.String vdef16) {
		this.vdef16 = vdef16;
	}

	/**
	 * ���� vdef17��Getter����.���������Զ�����17 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef17() {
		return this.vdef17;
	}

	/**
	 * ����vdef17��Setter����.���������Զ�����17 ��������:2017-11-18
	 * 
	 * @param newVdef17
	 *            java.lang.String
	 */
	public void setVdef17(java.lang.String vdef17) {
		this.vdef17 = vdef17;
	}

	/**
	 * ���� vdef18��Getter����.���������Զ�����18 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef18() {
		return this.vdef18;
	}

	/**
	 * ����vdef18��Setter����.���������Զ�����18 ��������:2017-11-18
	 * 
	 * @param newVdef18
	 *            java.lang.String
	 */
	public void setVdef18(java.lang.String vdef18) {
		this.vdef18 = vdef18;
	}

	/**
	 * ���� vdef19��Getter����.���������Զ�����19 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef19() {
		return this.vdef19;
	}

	/**
	 * ����vdef19��Setter����.���������Զ�����19 ��������:2017-11-18
	 * 
	 * @param newVdef19
	 *            java.lang.String
	 */
	public void setVdef19(java.lang.String vdef19) {
		this.vdef19 = vdef19;
	}

	/**
	 * ���� vdef20��Getter����.���������Զ�����20 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVdef20() {
		return this.vdef20;
	}

	/**
	 * ����vdef20��Setter����.���������Զ�����20 ��������:2017-11-18
	 * 
	 * @param newVdef20
	 *            java.lang.String
	 */
	public void setVdef20(java.lang.String vdef20) {
		this.vdef20 = vdef20;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2017-11-18
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}
	
	

	public java.lang.String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(java.lang.String vbillcode) {
		this.vbillcode = vbillcode;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("fee.wagesappro");
	}

	public java.lang.String getVsourcecode() {
		return vsourcecode;
	}

	public void setVsourcecode(java.lang.String vsourcecode) {
		this.vsourcecode = vsourcecode;
	}

	public java.lang.String getCsourceid() {
		return csourceid;
	}

	public void setCsourceid(java.lang.String csourceid) {
		this.csourceid = csourceid;
	}

	public java.lang.String getCsourcetypecode() {
		return csourcetypecode;
	}

	public void setCsourcetypecode(java.lang.String csourcetypecode) {
		this.csourcetypecode = csourcetypecode;
	}
	
	
}
