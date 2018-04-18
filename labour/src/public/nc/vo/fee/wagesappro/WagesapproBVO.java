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

public class WagesapproBVO extends SuperVO {

	/**
	 * ����
	 */
	public java.lang.String pk_wagesappro_b;
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
	 * �к�
	 */
	public java.lang.String crowno;
	/**
	 * ����
	 */
	public java.lang.String name;
	/**
	 * �Ա�
	 */
	public java.lang.Integer sex;
	/**
	 * ����
	 */
	public java.lang.Integer age;
	/**
	 * �ֻ�
	 */
	public java.lang.String telphone;
	/**
	 * ֤����
	 */
	public java.lang.String idnumber;
	/**
	 * ���鳤
	 */
	public java.lang.String pleader;
	/**
	 * ����
	 */
	public java.lang.String worktype;
	/**
	 * ��������
	 */
	public nc.vo.pub.lang.UFDouble workdays;
	/**
	 * �չ���
	 */
	public nc.vo.pub.lang.UFDouble daywages;
	/**
	 * Ӧ������
	 */
	public nc.vo.pub.lang.UFDouble shouldwages;
	/**
	 * �۽ɸ�˰
	 */
	public nc.vo.pub.lang.UFDouble psntax;
	/**
	 * ʵ������
	 */
	public nc.vo.pub.lang.UFDouble realwages;
	/**
	 * ǩ��
	 */
	public java.lang.String signature;
	/**
	 * ��Դ��������
	 */
	public java.lang.String csourcetypecode;
	/**
	 * ��Դ���ݺ�
	 */
	public java.lang.String vsourcecode;
	/**
	 * ��Դ����
	 */
	public java.lang.String csourceid;
	/**
	 * ��Դ������ϸ
	 */
	public java.lang.String csourcebid;
	/**
	 * ��Դ�����к�
	 */
	public java.lang.String vsourcerowno;
	/**
	 * ��Դ��������
	 */
	public java.lang.String vsourcetrantype;
	/**
	 * ��ע
	 */
	public java.lang.String vmemo;
	/**
	 * �Զ�����1
	 */
	public java.lang.String vbdef1;
	/**
	 * �Զ�����2
	 */
	public java.lang.String vbdef2;
	/**
	 * �Զ�����3
	 */
	public java.lang.String vbdef3;
	/**
	 * �Զ�����4
	 */
	public java.lang.String vbdef4;
	/**
	 * �Զ�����5
	 */
	public java.lang.String vbdef5;
	/**
	 * �Զ�����6
	 */
	public java.lang.String vbdef6;
	/**
	 * �Զ�����7
	 */
	public java.lang.String vbdef7;
	/**
	 * �Զ�����8
	 */
	public java.lang.String vbdef8;
	/**
	 * �Զ�����9
	 */
	public java.lang.String vbdef9;
	/**
	 * �Զ�����10
	 */
	public java.lang.String vbdef10;
	/**
	 * �Զ�����11
	 */
	public java.lang.String vbdef11;
	/**
	 * �Զ�����12
	 */
	public java.lang.String vbdef12;
	/**
	 * �Զ�����13
	 */
	public java.lang.String vbdef13;
	/**
	 * �Զ�����14
	 */
	public java.lang.String vbdef14;
	/**
	 * �Զ�����15
	 */
	public java.lang.String vbdef15;
	/**
	 * �Զ�����16
	 */
	public java.lang.String vbdef16;
	/**
	 * �Զ�����17
	 */
	public java.lang.String vbdef17;
	/**
	 * �Զ�����18
	 */
	public java.lang.String vbdef18;
	/**
	 * �Զ�����19
	 */
	public java.lang.String vbdef19;
	/**
	 * �Զ�����20
	 */
	public java.lang.String vbdef20;
	/**
	 * �ϲ㵥������
	 */
	public String pk_wagesappro;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_wagesappro_b��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wagesappro_b() {
		return this.pk_wagesappro_b;
	}

	/**
	 * ����pk_wagesappro_b��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newPk_wagesappro_b
	 *            java.lang.String
	 */
	public void setPk_wagesappro_b(java.lang.String pk_wagesappro_b) {
		this.pk_wagesappro_b = pk_wagesappro_b;
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
	 * ���� crowno��Getter����.���������к� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCrowno() {
		return this.crowno;
	}

	/**
	 * ����crowno��Setter����.���������к� ��������:2017-11-18
	 * 
	 * @param newCrowno
	 *            java.lang.String
	 */
	public void setCrowno(java.lang.String crowno) {
		this.crowno = crowno;
	}

	/**
	 * ���� name��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * ����name��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * ���� sex��Getter����.���������Ա� ��������:2017-11-18
	 * 
	 * @return nc.vo.fee.workteam.SexEnum
	 */
	public java.lang.Integer getSex() {
		return this.sex;
	}

	/**
	 * ����sex��Setter����.���������Ա� ��������:2017-11-18
	 * 
	 * @param newSex
	 *            nc.vo.fee.workteam.SexEnum
	 */
	public void setSex(java.lang.Integer sex) {
		this.sex = sex;
	}

	/**
	 * ���� age��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getAge() {
		return this.age;
	}

	/**
	 * ����age��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newAge
	 *            java.lang.Integer
	 */
	public void setAge(java.lang.Integer age) {
		this.age = age;
	}

	/**
	 * ���� telphone��Getter����.���������ֻ� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTelphone() {
		return this.telphone;
	}

	/**
	 * ����telphone��Setter����.���������ֻ� ��������:2017-11-18
	 * 
	 * @param newTelphone
	 *            java.lang.String
	 */
	public void setTelphone(java.lang.String telphone) {
		this.telphone = telphone;
	}

	/**
	 * ���� idnumber��Getter����.��������֤���� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getIdnumber() {
		return this.idnumber;
	}

	/**
	 * ����idnumber��Setter����.��������֤���� ��������:2017-11-18
	 * 
	 * @param newIdnumber
	 *            java.lang.String
	 */
	public void setIdnumber(java.lang.String idnumber) {
		this.idnumber = idnumber;
	}

	/**
	 * ���� pleader��Getter����.�����������鳤 ��������:2017-11-18
	 * 
	 * @return nc.vo.fee.workteam.WorkteamVO
	 */
	public java.lang.String getPleader() {
		return this.pleader;
	}

	/**
	 * ����pleader��Setter����.�����������鳤 ��������:2017-11-18
	 * 
	 * @param newPleader
	 *            nc.vo.fee.workteam.WorkteamVO
	 */
	public void setPleader(java.lang.String pleader) {
		this.pleader = pleader;
	}

	/**
	 * ���� worktype��Getter����.������������ ��������:2017-11-18
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public java.lang.String getWorktype() {
		return this.worktype;
	}

	/**
	 * ����worktype��Setter����.������������ ��������:2017-11-18
	 * 
	 * @param newWorktype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setWorktype(java.lang.String worktype) {
		this.worktype = worktype;
	}

	/**
	 * ���� workdays��Getter����.���������������� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getWorkdays() {
		return this.workdays;
	}

	/**
	 * ����workdays��Setter����.���������������� ��������:2017-11-18
	 * 
	 * @param newWorkdays
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setWorkdays(nc.vo.pub.lang.UFDouble workdays) {
		this.workdays = workdays;
	}

	/**
	 * ���� daywages��Getter����.���������չ��� ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getDaywages() {
		return this.daywages;
	}

	/**
	 * ����daywages��Setter����.���������չ��� ��������:2017-11-18
	 * 
	 * @param newDaywages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaywages(nc.vo.pub.lang.UFDouble daywages) {
		this.daywages = daywages;
	}

	/**
	 * ���� shouldwages��Getter����.��������Ӧ������ ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getShouldwages() {
		return this.shouldwages;
	}

	/**
	 * ����shouldwages��Setter����.��������Ӧ������ ��������:2017-11-18
	 * 
	 * @param newShouldwages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setShouldwages(nc.vo.pub.lang.UFDouble shouldwages) {
		this.shouldwages = shouldwages;
	}

	/**
	 * ���� psntax��Getter����.���������۽ɸ�˰ ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getPsntax() {
		return this.psntax;
	}

	/**
	 * ����psntax��Setter����.���������۽ɸ�˰ ��������:2017-11-18
	 * 
	 * @param newPsntax
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setPsntax(nc.vo.pub.lang.UFDouble psntax) {
		this.psntax = psntax;
	}

	/**
	 * ���� realwages��Getter����.��������ʵ������ ��������:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRealwages() {
		return this.realwages;
	}

	/**
	 * ����realwages��Setter����.��������ʵ������ ��������:2017-11-18
	 * 
	 * @param newRealwages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRealwages(nc.vo.pub.lang.UFDouble realwages) {
		this.realwages = realwages;
	}

	/**
	 * ���� signature��Getter����.��������ǩ�� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSignature() {
		return this.signature;
	}

	/**
	 * ����signature��Setter����.��������ǩ�� ��������:2017-11-18
	 * 
	 * @param newSignature
	 *            java.lang.String
	 */
	public void setSignature(java.lang.String signature) {
		this.signature = signature;
	}

	/**
	 * ���� csourcetypecode��Getter����.����������Դ�������� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcetypecode() {
		return this.csourcetypecode;
	}

	/**
	 * ����csourcetypecode��Setter����.����������Դ�������� ��������:2017-11-18
	 * 
	 * @param newCsourcetypecode
	 *            java.lang.String
	 */
	public void setCsourcetypecode(java.lang.String csourcetypecode) {
		this.csourcetypecode = csourcetypecode;
	}

	/**
	 * ���� vsourcecode��Getter����.����������Դ���ݺ� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcecode() {
		return this.vsourcecode;
	}

	/**
	 * ����vsourcecode��Setter����.����������Դ���ݺ� ��������:2017-11-18
	 * 
	 * @param newVsourcecode
	 *            java.lang.String
	 */
	public void setVsourcecode(java.lang.String vsourcecode) {
		this.vsourcecode = vsourcecode;
	}

	/**
	 * ���� csourceid��Getter����.����������Դ���� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourceid() {
		return this.csourceid;
	}

	/**
	 * ����csourceid��Setter����.����������Դ���� ��������:2017-11-18
	 * 
	 * @param newCsourceid
	 *            java.lang.String
	 */
	public void setCsourceid(java.lang.String csourceid) {
		this.csourceid = csourceid;
	}

	/**
	 * ���� csourcebid��Getter����.����������Դ������ϸ ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebid() {
		return this.csourcebid;
	}

	/**
	 * ����csourcebid��Setter����.����������Դ������ϸ ��������:2017-11-18
	 * 
	 * @param newCsourcebid
	 *            java.lang.String
	 */
	public void setCsourcebid(java.lang.String csourcebid) {
		this.csourcebid = csourcebid;
	}

	/**
	 * ���� vsourcerowno��Getter����.����������Դ�����к� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcerowno() {
		return this.vsourcerowno;
	}

	/**
	 * ����vsourcerowno��Setter����.����������Դ�����к� ��������:2017-11-18
	 * 
	 * @param newVsourcerowno
	 *            java.lang.String
	 */
	public void setVsourcerowno(java.lang.String vsourcerowno) {
		this.vsourcerowno = vsourcerowno;
	}

	/**
	 * ���� vsourcetrantype��Getter����.����������Դ�������� ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcetrantype() {
		return this.vsourcetrantype;
	}

	/**
	 * ����vsourcetrantype��Setter����.����������Դ�������� ��������:2017-11-18
	 * 
	 * @param newVsourcetrantype
	 *            java.lang.String
	 */
	public void setVsourcetrantype(java.lang.String vsourcetrantype) {
		this.vsourcetrantype = vsourcetrantype;
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
	 * ���� vbdef1��Getter����.���������Զ�����1 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef1() {
		return this.vbdef1;
	}

	/**
	 * ����vbdef1��Setter����.���������Զ�����1 ��������:2017-11-18
	 * 
	 * @param newVbdef1
	 *            java.lang.String
	 */
	public void setVbdef1(java.lang.String vbdef1) {
		this.vbdef1 = vbdef1;
	}

	/**
	 * ���� vbdef2��Getter����.���������Զ�����2 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef2() {
		return this.vbdef2;
	}

	/**
	 * ����vbdef2��Setter����.���������Զ�����2 ��������:2017-11-18
	 * 
	 * @param newVbdef2
	 *            java.lang.String
	 */
	public void setVbdef2(java.lang.String vbdef2) {
		this.vbdef2 = vbdef2;
	}

	/**
	 * ���� vbdef3��Getter����.���������Զ�����3 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef3() {
		return this.vbdef3;
	}

	/**
	 * ����vbdef3��Setter����.���������Զ�����3 ��������:2017-11-18
	 * 
	 * @param newVbdef3
	 *            java.lang.String
	 */
	public void setVbdef3(java.lang.String vbdef3) {
		this.vbdef3 = vbdef3;
	}

	/**
	 * ���� vbdef4��Getter����.���������Զ�����4 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef4() {
		return this.vbdef4;
	}

	/**
	 * ����vbdef4��Setter����.���������Զ�����4 ��������:2017-11-18
	 * 
	 * @param newVbdef4
	 *            java.lang.String
	 */
	public void setVbdef4(java.lang.String vbdef4) {
		this.vbdef4 = vbdef4;
	}

	/**
	 * ���� vbdef5��Getter����.���������Զ�����5 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef5() {
		return this.vbdef5;
	}

	/**
	 * ����vbdef5��Setter����.���������Զ�����5 ��������:2017-11-18
	 * 
	 * @param newVbdef5
	 *            java.lang.String
	 */
	public void setVbdef5(java.lang.String vbdef5) {
		this.vbdef5 = vbdef5;
	}

	/**
	 * ���� vbdef6��Getter����.���������Զ�����6 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef6() {
		return this.vbdef6;
	}

	/**
	 * ����vbdef6��Setter����.���������Զ�����6 ��������:2017-11-18
	 * 
	 * @param newVbdef6
	 *            java.lang.String
	 */
	public void setVbdef6(java.lang.String vbdef6) {
		this.vbdef6 = vbdef6;
	}

	/**
	 * ���� vbdef7��Getter����.���������Զ�����7 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef7() {
		return this.vbdef7;
	}

	/**
	 * ����vbdef7��Setter����.���������Զ�����7 ��������:2017-11-18
	 * 
	 * @param newVbdef7
	 *            java.lang.String
	 */
	public void setVbdef7(java.lang.String vbdef7) {
		this.vbdef7 = vbdef7;
	}

	/**
	 * ���� vbdef8��Getter����.���������Զ�����8 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef8() {
		return this.vbdef8;
	}

	/**
	 * ����vbdef8��Setter����.���������Զ�����8 ��������:2017-11-18
	 * 
	 * @param newVbdef8
	 *            java.lang.String
	 */
	public void setVbdef8(java.lang.String vbdef8) {
		this.vbdef8 = vbdef8;
	}

	/**
	 * ���� vbdef9��Getter����.���������Զ�����9 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef9() {
		return this.vbdef9;
	}

	/**
	 * ����vbdef9��Setter����.���������Զ�����9 ��������:2017-11-18
	 * 
	 * @param newVbdef9
	 *            java.lang.String
	 */
	public void setVbdef9(java.lang.String vbdef9) {
		this.vbdef9 = vbdef9;
	}

	/**
	 * ���� vbdef10��Getter����.���������Զ�����10 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef10() {
		return this.vbdef10;
	}

	/**
	 * ����vbdef10��Setter����.���������Զ�����10 ��������:2017-11-18
	 * 
	 * @param newVbdef10
	 *            java.lang.String
	 */
	public void setVbdef10(java.lang.String vbdef10) {
		this.vbdef10 = vbdef10;
	}

	/**
	 * ���� vbdef11��Getter����.���������Զ�����11 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef11() {
		return this.vbdef11;
	}

	/**
	 * ����vbdef11��Setter����.���������Զ�����11 ��������:2017-11-18
	 * 
	 * @param newVbdef11
	 *            java.lang.String
	 */
	public void setVbdef11(java.lang.String vbdef11) {
		this.vbdef11 = vbdef11;
	}

	/**
	 * ���� vbdef12��Getter����.���������Զ�����12 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef12() {
		return this.vbdef12;
	}

	/**
	 * ����vbdef12��Setter����.���������Զ�����12 ��������:2017-11-18
	 * 
	 * @param newVbdef12
	 *            java.lang.String
	 */
	public void setVbdef12(java.lang.String vbdef12) {
		this.vbdef12 = vbdef12;
	}

	/**
	 * ���� vbdef13��Getter����.���������Զ�����13 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef13() {
		return this.vbdef13;
	}

	/**
	 * ����vbdef13��Setter����.���������Զ�����13 ��������:2017-11-18
	 * 
	 * @param newVbdef13
	 *            java.lang.String
	 */
	public void setVbdef13(java.lang.String vbdef13) {
		this.vbdef13 = vbdef13;
	}

	/**
	 * ���� vbdef14��Getter����.���������Զ�����14 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef14() {
		return this.vbdef14;
	}

	/**
	 * ����vbdef14��Setter����.���������Զ�����14 ��������:2017-11-18
	 * 
	 * @param newVbdef14
	 *            java.lang.String
	 */
	public void setVbdef14(java.lang.String vbdef14) {
		this.vbdef14 = vbdef14;
	}

	/**
	 * ���� vbdef15��Getter����.���������Զ�����15 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef15() {
		return this.vbdef15;
	}

	/**
	 * ����vbdef15��Setter����.���������Զ�����15 ��������:2017-11-18
	 * 
	 * @param newVbdef15
	 *            java.lang.String
	 */
	public void setVbdef15(java.lang.String vbdef15) {
		this.vbdef15 = vbdef15;
	}

	/**
	 * ���� vbdef16��Getter����.���������Զ�����16 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef16() {
		return this.vbdef16;
	}

	/**
	 * ����vbdef16��Setter����.���������Զ�����16 ��������:2017-11-18
	 * 
	 * @param newVbdef16
	 *            java.lang.String
	 */
	public void setVbdef16(java.lang.String vbdef16) {
		this.vbdef16 = vbdef16;
	}

	/**
	 * ���� vbdef17��Getter����.���������Զ�����17 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef17() {
		return this.vbdef17;
	}

	/**
	 * ����vbdef17��Setter����.���������Զ�����17 ��������:2017-11-18
	 * 
	 * @param newVbdef17
	 *            java.lang.String
	 */
	public void setVbdef17(java.lang.String vbdef17) {
		this.vbdef17 = vbdef17;
	}

	/**
	 * ���� vbdef18��Getter����.���������Զ�����18 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef18() {
		return this.vbdef18;
	}

	/**
	 * ����vbdef18��Setter����.���������Զ�����18 ��������:2017-11-18
	 * 
	 * @param newVbdef18
	 *            java.lang.String
	 */
	public void setVbdef18(java.lang.String vbdef18) {
		this.vbdef18 = vbdef18;
	}

	/**
	 * ���� vbdef19��Getter����.���������Զ�����19 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef19() {
		return this.vbdef19;
	}

	/**
	 * ����vbdef19��Setter����.���������Զ�����19 ��������:2017-11-18
	 * 
	 * @param newVbdef19
	 *            java.lang.String
	 */
	public void setVbdef19(java.lang.String vbdef19) {
		this.vbdef19 = vbdef19;
	}

	/**
	 * ���� vbdef20��Getter����.���������Զ�����20 ��������:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef20() {
		return this.vbdef20;
	}

	/**
	 * ����vbdef20��Setter����.���������Զ�����20 ��������:2017-11-18
	 * 
	 * @param newVbdef20
	 *            java.lang.String
	 */
	public void setVbdef20(java.lang.String vbdef20) {
		this.vbdef20 = vbdef20;
	}

	/**
	 * ���� �����ϲ�������Getter����.���������ϲ����� ��������:2017-11-18
	 * 
	 * @return String
	 */
	public String getPk_wagesappro() {
		return this.pk_wagesappro;
	}

	/**
	 * ���������ϲ�������Setter����.���������ϲ����� ��������:2017-11-18
	 * 
	 * @param newPk_wagesappro
	 *            String
	 */
	public void setPk_wagesappro(String pk_wagesappro) {
		this.pk_wagesappro = pk_wagesappro;
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

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("fee.wagesapproItem");
	}
}
