package nc.vo.fee.withholding;

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
 *   �˴�����۵�������Ϣ
 * </p>
 *  ��������:2017-12-5
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class WithholdingVO extends SuperVO {
	
/**
*����
*/
public java.lang.String pk_withholding;
/**
*����
*/
public java.lang.String pk_group;
/**
*��֯
*/
public java.lang.String pk_org;
/**
*��֯��汾
*/
public java.lang.String pk_org_v;
/**
*�������ͱ���
*/
public java.lang.String pk_billtypecode;
/**
*��������
*/
public java.lang.String pk_billtypeid;
/**
*���ݺ�
*/
public java.lang.String vbillcode;
/**
*��ͬ���
*/
public java.lang.String contratcno;
/**
*���񿨺�
*/
public java.lang.String serviceno;
/**
*��ʼ����
*/
public UFDate startdate;
/**
*��������
*/
public UFDate enddate;
/**
*��ͬ���
*/
public nc.vo.pub.lang.UFDouble contractmny;
/**
*Ԥ����
*/
public nc.vo.pub.lang.UFDouble withholdingmny;
/**
*Ԥ������
*/
public UFDate withholdingdate;
/**
*Ԥ������
*/
public java.lang.Integer withholdingdays;
/**
*��Ŀ����
*/
public java.lang.String promanager;
/**
*���ʦ
*/
public java.lang.String stylist;
/**
*��Ŀ����
*/
public java.lang.String prostyle;
/**
*����
*/
public java.lang.String overview;
/**
*��������
*/
public UFDate dbilldate;
/**
*�Ƿ�ȷ��
*/
public UFBoolean isaffirm;
/**
*������
*/
public java.lang.String creator;
/**
*����ʱ��
*/
public UFDateTime creationtime;
/**
*����޸���
*/
public java.lang.String modifier;
/**
*����޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*��ע
*/
public java.lang.String vmemo;
/**
*��Դ��������
*/
public java.lang.String csourcetypecode;
/**
*��Դ���ݺ�
*/
public java.lang.String vsourcecode;
/**
*��Դ����
*/
public java.lang.String csourceid;
/**
*��Դ������ϸ
*/
public java.lang.String csourcebid;
/**
*��Դ�����к�
*/
public java.lang.String vsourcerowno;
/**
*��Դ��������
*/
public java.lang.String vsourcetrantype;
/**
*�Զ�����1
*/
public java.lang.String vdef1;
/**
*�Զ�����2
*/
public java.lang.String vdef2;
/**
*�Զ�����3
*/
public java.lang.String vdef3;
/**
*�Զ�����4
*/
public java.lang.String vdef4;
/**
*�Զ�����5
*/
public java.lang.String vdef5;
/**
*�Զ�����6
*/
public java.lang.String vdef6;
/**
*�Զ�����7
*/
public java.lang.String vdef7;
/**
*�Զ�����8
*/
public java.lang.String vdef8;
/**
*�Զ�����9
*/
public java.lang.String vdef9;
/**
*�Զ�����10
*/
public java.lang.String vdef10;
/**
*�Զ�����11
*/
public java.lang.String vdef11;
/**
*�Զ�����12
*/
public java.lang.String vdef12;
/**
*�Զ�����13
*/
public java.lang.String vdef13;
/**
*�Զ�����14
*/
public java.lang.String vdef14;
/**
*�Զ�����15
*/
public java.lang.String vdef15;
/**
*�Զ�����16
*/
public java.lang.String vdef16;
/**
*�Զ�����17
*/
public java.lang.String vdef17;
/**
*�Զ�����18
*/
public java.lang.String vdef18;
/**
*�Զ�����19
*/
public java.lang.String vdef19;
/**
*�Զ�����20
*/
public java.lang.String vdef20;
/**
*ʱ���
*/
public UFDateTime ts;
    
    
/**
* ���� pk_withholding��Getter����.������������
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getPk_withholding() {
return this.pk_withholding;
} 

/**
* ����pk_withholding��Setter����.������������
* ��������:2017-12-5
* @param newPk_withholding java.lang.String
*/
public void setPk_withholding ( java.lang.String pk_withholding) {
this.pk_withholding=pk_withholding;
} 
 
/**
* ���� pk_group��Getter����.������������
*  ��������:2017-12-5
* @return nc.vo.org.GroupVO
*/
public java.lang.String getPk_group() {
return this.pk_group;
} 

/**
* ����pk_group��Setter����.������������
* ��������:2017-12-5
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( java.lang.String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* ���� pk_org��Getter����.����������֯
*  ��������:2017-12-5
* @return nc.vo.org.OrgVO
*/
public java.lang.String getPk_org() {
return this.pk_org;
} 

/**
* ����pk_org��Setter����.����������֯
* ��������:2017-12-5
* @param newPk_org nc.vo.org.OrgVO
*/
public void setPk_org ( java.lang.String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* ���� pk_org_v��Getter����.����������֯��汾
*  ��������:2017-12-5
* @return nc.vo.vorg.OrgVersionVO
*/
public java.lang.String getPk_org_v() {
return this.pk_org_v;
} 

/**
* ����pk_org_v��Setter����.����������֯��汾
* ��������:2017-12-5
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( java.lang.String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* ���� pk_billtypecode��Getter����.���������������ͱ���
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getPk_billtypecode() {
return this.pk_billtypecode;
} 

/**
* ����pk_billtypecode��Setter����.���������������ͱ���
* ��������:2017-12-5
* @param newPk_billtypecode java.lang.String
*/
public void setPk_billtypecode ( java.lang.String pk_billtypecode) {
this.pk_billtypecode=pk_billtypecode;
} 
 
/**
* ���� pk_billtypeid��Getter����.����������������
*  ��������:2017-12-5
* @return nc.vo.pub.billtype.BilltypeVO
*/
public java.lang.String getPk_billtypeid() {
return this.pk_billtypeid;
} 

/**
* ����pk_billtypeid��Setter����.����������������
* ��������:2017-12-5
* @param newPk_billtypeid nc.vo.pub.billtype.BilltypeVO
*/
public void setPk_billtypeid ( java.lang.String pk_billtypeid) {
this.pk_billtypeid=pk_billtypeid;
} 
 
/**
* ���� vbillcode��Getter����.�����������ݺ�
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVbillcode() {
return this.vbillcode;
} 

/**
* ����vbillcode��Setter����.�����������ݺ�
* ��������:2017-12-5
* @param newVbillcode java.lang.String
*/
public void setVbillcode ( java.lang.String vbillcode) {
this.vbillcode=vbillcode;
} 
 
/**
* ���� contratcno��Getter����.����������ͬ���
*  ��������:2017-12-5
* @return nc.vo.pmpub.project.ProjectHeadVO
*/
public java.lang.String getContratcno() {
return this.contratcno;
} 

/**
* ����contratcno��Setter����.����������ͬ���
* ��������:2017-12-5
* @param newContratcno nc.vo.pmpub.project.ProjectHeadVO
*/
public void setContratcno ( java.lang.String contratcno) {
this.contratcno=contratcno;
} 
 
/**
* ���� serviceno��Getter����.�����������񿨺�
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getServiceno() {
return this.serviceno;
} 

/**
* ����serviceno��Setter����.�����������񿨺�
* ��������:2017-12-5
* @param newServiceno java.lang.String
*/
public void setServiceno ( java.lang.String serviceno) {
this.serviceno=serviceno;
} 
 
/**
* ���� startdate��Getter����.����������ʼ����
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getStartdate() {
return this.startdate;
} 

/**
* ����startdate��Setter����.����������ʼ����
* ��������:2017-12-5
* @param newStartdate nc.vo.pub.lang.UFDate
*/
public void setStartdate ( UFDate startdate) {
this.startdate=startdate;
} 
 
/**
* ���� enddate��Getter����.����������������
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getEnddate() {
return this.enddate;
} 

/**
* ����enddate��Setter����.����������������
* ��������:2017-12-5
* @param newEnddate nc.vo.pub.lang.UFDate
*/
public void setEnddate ( UFDate enddate) {
this.enddate=enddate;
} 
 
/**
* ���� contractmny��Getter����.����������ͬ���
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getContractmny() {
return this.contractmny;
} 

/**
* ����contractmny��Setter����.����������ͬ���
* ��������:2017-12-5
* @param newContractmny nc.vo.pub.lang.UFDouble
*/
public void setContractmny ( nc.vo.pub.lang.UFDouble contractmny) {
this.contractmny=contractmny;
} 
 
/**
* ���� withholdingmny��Getter����.��������Ԥ����
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getWithholdingmny() {
return this.withholdingmny;
} 

/**
* ����withholdingmny��Setter����.��������Ԥ����
* ��������:2017-12-5
* @param newWithholdingmny nc.vo.pub.lang.UFDouble
*/
public void setWithholdingmny ( nc.vo.pub.lang.UFDouble withholdingmny) {
this.withholdingmny=withholdingmny;
} 
 
/**
* ���� withholdingdate��Getter����.��������Ԥ������
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getWithholdingdate() {
return this.withholdingdate;
} 

/**
* ����withholdingdate��Setter����.��������Ԥ������
* ��������:2017-12-5
* @param newWithholdingdate nc.vo.pub.lang.UFDate
*/
public void setWithholdingdate ( UFDate withholdingdate) {
this.withholdingdate=withholdingdate;
} 
 
/**
* ���� withholdingdays��Getter����.��������Ԥ������
*  ��������:2017-12-5
* @return java.lang.Integer
*/
public java.lang.Integer getWithholdingdays() {
return this.withholdingdays;
} 

/**
* ����withholdingdays��Setter����.��������Ԥ������
* ��������:2017-12-5
* @param newWithholdingdays java.lang.Integer
*/
public void setWithholdingdays ( java.lang.Integer withholdingdays) {
this.withholdingdays=withholdingdays;
} 
 
/**
* ���� promanager��Getter����.����������Ŀ����
*  ��������:2017-12-5
* @return nc.vo.bd.psn.PsndocVO
*/
public java.lang.String getPromanager() {
return this.promanager;
} 

/**
* ����promanager��Setter����.����������Ŀ����
* ��������:2017-12-5
* @param newPromanager nc.vo.bd.psn.PsndocVO
*/
public void setPromanager ( java.lang.String promanager) {
this.promanager=promanager;
} 
 
/**
* ���� stylist��Getter����.�����������ʦ
*  ��������:2017-12-5
* @return nc.vo.bd.psn.PsndocVO
*/
public java.lang.String getStylist() {
return this.stylist;
} 

/**
* ����stylist��Setter����.�����������ʦ
* ��������:2017-12-5
* @param newStylist nc.vo.bd.psn.PsndocVO
*/
public void setStylist ( java.lang.String stylist) {
this.stylist=stylist;
} 
 
/**
* ���� prostyle��Getter����.����������Ŀ����
*  ��������:2017-12-5
* @return nc.vo.bd.defdoc.DefdocVO
*/
public java.lang.String getProstyle() {
return this.prostyle;
} 

/**
* ����prostyle��Setter����.����������Ŀ����
* ��������:2017-12-5
* @param newProstyle nc.vo.bd.defdoc.DefdocVO
*/
public void setProstyle ( java.lang.String prostyle) {
this.prostyle=prostyle;
} 
 
/**
* ���� overview��Getter����.������������
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getOverview() {
return this.overview;
} 

/**
* ����overview��Setter����.������������
* ��������:2017-12-5
* @param newOverview java.lang.String
*/
public void setOverview ( java.lang.String overview) {
this.overview=overview;
} 
 
/**
* ���� dbilldate��Getter����.����������������
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getDbilldate() {
return this.dbilldate;
} 

/**
* ����dbilldate��Setter����.����������������
* ��������:2017-12-5
* @param newDbilldate nc.vo.pub.lang.UFDate
*/
public void setDbilldate ( UFDate dbilldate) {
this.dbilldate=dbilldate;
} 
 
/**
* ���� isaffirm��Getter����.���������Ƿ�ȷ��
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIsaffirm() {
return this.isaffirm;
} 

/**
* ����isaffirm��Setter����.���������Ƿ�ȷ��
* ��������:2017-12-5
* @param newIsaffirm nc.vo.pub.lang.UFBoolean
*/
public void setIsaffirm ( UFBoolean isaffirm) {
this.isaffirm=isaffirm;
} 
 
/**
* ���� creator��Getter����.��������������
*  ��������:2017-12-5
* @return nc.vo.sm.UserVO
*/
public java.lang.String getCreator() {
return this.creator;
} 

/**
* ����creator��Setter����.��������������
* ��������:2017-12-5
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( java.lang.String creator) {
this.creator=creator;
} 
 
/**
* ���� creationtime��Getter����.������������ʱ��
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* ����creationtime��Setter����.������������ʱ��
* ��������:2017-12-5
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* ���� modifier��Getter����.������������޸���
*  ��������:2017-12-5
* @return nc.vo.sm.UserVO
*/
public java.lang.String getModifier() {
return this.modifier;
} 

/**
* ����modifier��Setter����.������������޸���
* ��������:2017-12-5
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( java.lang.String modifier) {
this.modifier=modifier;
} 
 
/**
* ���� modifiedtime��Getter����.������������޸�ʱ��
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* ����modifiedtime��Setter����.������������޸�ʱ��
* ��������:2017-12-5
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* ���� vmemo��Getter����.����������ע
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVmemo() {
return this.vmemo;
} 

/**
* ����vmemo��Setter����.����������ע
* ��������:2017-12-5
* @param newVmemo java.lang.String
*/
public void setVmemo ( java.lang.String vmemo) {
this.vmemo=vmemo;
} 
 
/**
* ���� csourcetypecode��Getter����.����������Դ��������
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourcetypecode() {
return this.csourcetypecode;
} 

/**
* ����csourcetypecode��Setter����.����������Դ��������
* ��������:2017-12-5
* @param newCsourcetypecode java.lang.String
*/
public void setCsourcetypecode ( java.lang.String csourcetypecode) {
this.csourcetypecode=csourcetypecode;
} 
 
/**
* ���� vsourcecode��Getter����.����������Դ���ݺ�
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcecode() {
return this.vsourcecode;
} 

/**
* ����vsourcecode��Setter����.����������Դ���ݺ�
* ��������:2017-12-5
* @param newVsourcecode java.lang.String
*/
public void setVsourcecode ( java.lang.String vsourcecode) {
this.vsourcecode=vsourcecode;
} 
 
/**
* ���� csourceid��Getter����.����������Դ����
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourceid() {
return this.csourceid;
} 

/**
* ����csourceid��Setter����.����������Դ����
* ��������:2017-12-5
* @param newCsourceid java.lang.String
*/
public void setCsourceid ( java.lang.String csourceid) {
this.csourceid=csourceid;
} 
 
/**
* ���� csourcebid��Getter����.����������Դ������ϸ
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourcebid() {
return this.csourcebid;
} 

/**
* ����csourcebid��Setter����.����������Դ������ϸ
* ��������:2017-12-5
* @param newCsourcebid java.lang.String
*/
public void setCsourcebid ( java.lang.String csourcebid) {
this.csourcebid=csourcebid;
} 
 
/**
* ���� vsourcerowno��Getter����.����������Դ�����к�
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcerowno() {
return this.vsourcerowno;
} 

/**
* ����vsourcerowno��Setter����.����������Դ�����к�
* ��������:2017-12-5
* @param newVsourcerowno java.lang.String
*/
public void setVsourcerowno ( java.lang.String vsourcerowno) {
this.vsourcerowno=vsourcerowno;
} 
 
/**
* ���� vsourcetrantype��Getter����.����������Դ��������
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcetrantype() {
return this.vsourcetrantype;
} 

/**
* ����vsourcetrantype��Setter����.����������Դ��������
* ��������:2017-12-5
* @param newVsourcetrantype java.lang.String
*/
public void setVsourcetrantype ( java.lang.String vsourcetrantype) {
this.vsourcetrantype=vsourcetrantype;
} 
 
/**
* ���� vdef1��Getter����.���������Զ�����1
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef1() {
return this.vdef1;
} 

/**
* ����vdef1��Setter����.���������Զ�����1
* ��������:2017-12-5
* @param newVdef1 java.lang.String
*/
public void setVdef1 ( java.lang.String vdef1) {
this.vdef1=vdef1;
} 
 
/**
* ���� vdef2��Getter����.���������Զ�����2
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef2() {
return this.vdef2;
} 

/**
* ����vdef2��Setter����.���������Զ�����2
* ��������:2017-12-5
* @param newVdef2 java.lang.String
*/
public void setVdef2 ( java.lang.String vdef2) {
this.vdef2=vdef2;
} 
 
/**
* ���� vdef3��Getter����.���������Զ�����3
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef3() {
return this.vdef3;
} 

/**
* ����vdef3��Setter����.���������Զ�����3
* ��������:2017-12-5
* @param newVdef3 java.lang.String
*/
public void setVdef3 ( java.lang.String vdef3) {
this.vdef3=vdef3;
} 
 
/**
* ���� vdef4��Getter����.���������Զ�����4
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef4() {
return this.vdef4;
} 

/**
* ����vdef4��Setter����.���������Զ�����4
* ��������:2017-12-5
* @param newVdef4 java.lang.String
*/
public void setVdef4 ( java.lang.String vdef4) {
this.vdef4=vdef4;
} 
 
/**
* ���� vdef5��Getter����.���������Զ�����5
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef5() {
return this.vdef5;
} 

/**
* ����vdef5��Setter����.���������Զ�����5
* ��������:2017-12-5
* @param newVdef5 java.lang.String
*/
public void setVdef5 ( java.lang.String vdef5) {
this.vdef5=vdef5;
} 
 
/**
* ���� vdef6��Getter����.���������Զ�����6
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef6() {
return this.vdef6;
} 

/**
* ����vdef6��Setter����.���������Զ�����6
* ��������:2017-12-5
* @param newVdef6 java.lang.String
*/
public void setVdef6 ( java.lang.String vdef6) {
this.vdef6=vdef6;
} 
 
/**
* ���� vdef7��Getter����.���������Զ�����7
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef7() {
return this.vdef7;
} 

/**
* ����vdef7��Setter����.���������Զ�����7
* ��������:2017-12-5
* @param newVdef7 java.lang.String
*/
public void setVdef7 ( java.lang.String vdef7) {
this.vdef7=vdef7;
} 
 
/**
* ���� vdef8��Getter����.���������Զ�����8
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef8() {
return this.vdef8;
} 

/**
* ����vdef8��Setter����.���������Զ�����8
* ��������:2017-12-5
* @param newVdef8 java.lang.String
*/
public void setVdef8 ( java.lang.String vdef8) {
this.vdef8=vdef8;
} 
 
/**
* ���� vdef9��Getter����.���������Զ�����9
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef9() {
return this.vdef9;
} 

/**
* ����vdef9��Setter����.���������Զ�����9
* ��������:2017-12-5
* @param newVdef9 java.lang.String
*/
public void setVdef9 ( java.lang.String vdef9) {
this.vdef9=vdef9;
} 
 
/**
* ���� vdef10��Getter����.���������Զ�����10
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef10() {
return this.vdef10;
} 

/**
* ����vdef10��Setter����.���������Զ�����10
* ��������:2017-12-5
* @param newVdef10 java.lang.String
*/
public void setVdef10 ( java.lang.String vdef10) {
this.vdef10=vdef10;
} 
 
/**
* ���� vdef11��Getter����.���������Զ�����11
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef11() {
return this.vdef11;
} 

/**
* ����vdef11��Setter����.���������Զ�����11
* ��������:2017-12-5
* @param newVdef11 java.lang.String
*/
public void setVdef11 ( java.lang.String vdef11) {
this.vdef11=vdef11;
} 
 
/**
* ���� vdef12��Getter����.���������Զ�����12
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef12() {
return this.vdef12;
} 

/**
* ����vdef12��Setter����.���������Զ�����12
* ��������:2017-12-5
* @param newVdef12 java.lang.String
*/
public void setVdef12 ( java.lang.String vdef12) {
this.vdef12=vdef12;
} 
 
/**
* ���� vdef13��Getter����.���������Զ�����13
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef13() {
return this.vdef13;
} 

/**
* ����vdef13��Setter����.���������Զ�����13
* ��������:2017-12-5
* @param newVdef13 java.lang.String
*/
public void setVdef13 ( java.lang.String vdef13) {
this.vdef13=vdef13;
} 
 
/**
* ���� vdef14��Getter����.���������Զ�����14
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef14() {
return this.vdef14;
} 

/**
* ����vdef14��Setter����.���������Զ�����14
* ��������:2017-12-5
* @param newVdef14 java.lang.String
*/
public void setVdef14 ( java.lang.String vdef14) {
this.vdef14=vdef14;
} 
 
/**
* ���� vdef15��Getter����.���������Զ�����15
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef15() {
return this.vdef15;
} 

/**
* ����vdef15��Setter����.���������Զ�����15
* ��������:2017-12-5
* @param newVdef15 java.lang.String
*/
public void setVdef15 ( java.lang.String vdef15) {
this.vdef15=vdef15;
} 
 
/**
* ���� vdef16��Getter����.���������Զ�����16
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef16() {
return this.vdef16;
} 

/**
* ����vdef16��Setter����.���������Զ�����16
* ��������:2017-12-5
* @param newVdef16 java.lang.String
*/
public void setVdef16 ( java.lang.String vdef16) {
this.vdef16=vdef16;
} 
 
/**
* ���� vdef17��Getter����.���������Զ�����17
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef17() {
return this.vdef17;
} 

/**
* ����vdef17��Setter����.���������Զ�����17
* ��������:2017-12-5
* @param newVdef17 java.lang.String
*/
public void setVdef17 ( java.lang.String vdef17) {
this.vdef17=vdef17;
} 
 
/**
* ���� vdef18��Getter����.���������Զ�����18
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef18() {
return this.vdef18;
} 

/**
* ����vdef18��Setter����.���������Զ�����18
* ��������:2017-12-5
* @param newVdef18 java.lang.String
*/
public void setVdef18 ( java.lang.String vdef18) {
this.vdef18=vdef18;
} 
 
/**
* ���� vdef19��Getter����.���������Զ�����19
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef19() {
return this.vdef19;
} 

/**
* ����vdef19��Setter����.���������Զ�����19
* ��������:2017-12-5
* @param newVdef19 java.lang.String
*/
public void setVdef19 ( java.lang.String vdef19) {
this.vdef19=vdef19;
} 
 
/**
* ���� vdef20��Getter����.���������Զ�����20
*  ��������:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef20() {
return this.vdef20;
} 

/**
* ����vdef20��Setter����.���������Զ�����20
* ��������:2017-12-5
* @param newVdef20 java.lang.String
*/
public void setVdef20 ( java.lang.String vdef20) {
this.vdef20=vdef20;
} 
 
/**
* ���� ����ʱ�����Getter����.��������ʱ���
*  ��������:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* ��������ʱ�����Setter����.��������ʱ���
* ��������:2017-12-5
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("fee.withholding");
    }
   }
    