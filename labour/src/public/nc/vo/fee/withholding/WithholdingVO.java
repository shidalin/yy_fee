package nc.vo.fee.withholding;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2017-12-5
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class WithholdingVO extends SuperVO {
	
/**
*主键
*/
public java.lang.String pk_withholding;
/**
*集团
*/
public java.lang.String pk_group;
/**
*组织
*/
public java.lang.String pk_org;
/**
*组织多版本
*/
public java.lang.String pk_org_v;
/**
*单据类型编码
*/
public java.lang.String pk_billtypecode;
/**
*单据类型
*/
public java.lang.String pk_billtypeid;
/**
*单据号
*/
public java.lang.String vbillcode;
/**
*合同编号
*/
public java.lang.String contratcno;
/**
*服务卡号
*/
public java.lang.String serviceno;
/**
*开始日期
*/
public UFDate startdate;
/**
*结束日期
*/
public UFDate enddate;
/**
*合同金额
*/
public nc.vo.pub.lang.UFDouble contractmny;
/**
*预提金额
*/
public nc.vo.pub.lang.UFDouble withholdingmny;
/**
*预提日期
*/
public UFDate withholdingdate;
/**
*预提天数
*/
public java.lang.Integer withholdingdays;
/**
*项目经理
*/
public java.lang.String promanager;
/**
*设计师
*/
public java.lang.String stylist;
/**
*项目类型
*/
public java.lang.String prostyle;
/**
*概述
*/
public java.lang.String overview;
/**
*单据日期
*/
public UFDate dbilldate;
/**
*是否确认
*/
public UFBoolean isaffirm;
/**
*创建人
*/
public java.lang.String creator;
/**
*创建时间
*/
public UFDateTime creationtime;
/**
*最后修改人
*/
public java.lang.String modifier;
/**
*最后修改时间
*/
public UFDateTime modifiedtime;
/**
*备注
*/
public java.lang.String vmemo;
/**
*来源单据类型
*/
public java.lang.String csourcetypecode;
/**
*来源单据号
*/
public java.lang.String vsourcecode;
/**
*来源单据
*/
public java.lang.String csourceid;
/**
*来源单据明细
*/
public java.lang.String csourcebid;
/**
*来源单据行号
*/
public java.lang.String vsourcerowno;
/**
*来源交易类型
*/
public java.lang.String vsourcetrantype;
/**
*自定义项1
*/
public java.lang.String vdef1;
/**
*自定义项2
*/
public java.lang.String vdef2;
/**
*自定义项3
*/
public java.lang.String vdef3;
/**
*自定义项4
*/
public java.lang.String vdef4;
/**
*自定义项5
*/
public java.lang.String vdef5;
/**
*自定义项6
*/
public java.lang.String vdef6;
/**
*自定义项7
*/
public java.lang.String vdef7;
/**
*自定义项8
*/
public java.lang.String vdef8;
/**
*自定义项9
*/
public java.lang.String vdef9;
/**
*自定义项10
*/
public java.lang.String vdef10;
/**
*自定义项11
*/
public java.lang.String vdef11;
/**
*自定义项12
*/
public java.lang.String vdef12;
/**
*自定义项13
*/
public java.lang.String vdef13;
/**
*自定义项14
*/
public java.lang.String vdef14;
/**
*自定义项15
*/
public java.lang.String vdef15;
/**
*自定义项16
*/
public java.lang.String vdef16;
/**
*自定义项17
*/
public java.lang.String vdef17;
/**
*自定义项18
*/
public java.lang.String vdef18;
/**
*自定义项19
*/
public java.lang.String vdef19;
/**
*自定义项20
*/
public java.lang.String vdef20;
/**
*时间戳
*/
public UFDateTime ts;
    
    
/**
* 属性 pk_withholding的Getter方法.属性名：主键
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getPk_withholding() {
return this.pk_withholding;
} 

/**
* 属性pk_withholding的Setter方法.属性名：主键
* 创建日期:2017-12-5
* @param newPk_withholding java.lang.String
*/
public void setPk_withholding ( java.lang.String pk_withholding) {
this.pk_withholding=pk_withholding;
} 
 
/**
* 属性 pk_group的Getter方法.属性名：集团
*  创建日期:2017-12-5
* @return nc.vo.org.GroupVO
*/
public java.lang.String getPk_group() {
return this.pk_group;
} 

/**
* 属性pk_group的Setter方法.属性名：集团
* 创建日期:2017-12-5
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( java.lang.String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* 属性 pk_org的Getter方法.属性名：组织
*  创建日期:2017-12-5
* @return nc.vo.org.OrgVO
*/
public java.lang.String getPk_org() {
return this.pk_org;
} 

/**
* 属性pk_org的Setter方法.属性名：组织
* 创建日期:2017-12-5
* @param newPk_org nc.vo.org.OrgVO
*/
public void setPk_org ( java.lang.String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* 属性 pk_org_v的Getter方法.属性名：组织多版本
*  创建日期:2017-12-5
* @return nc.vo.vorg.OrgVersionVO
*/
public java.lang.String getPk_org_v() {
return this.pk_org_v;
} 

/**
* 属性pk_org_v的Setter方法.属性名：组织多版本
* 创建日期:2017-12-5
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( java.lang.String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* 属性 pk_billtypecode的Getter方法.属性名：单据类型编码
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getPk_billtypecode() {
return this.pk_billtypecode;
} 

/**
* 属性pk_billtypecode的Setter方法.属性名：单据类型编码
* 创建日期:2017-12-5
* @param newPk_billtypecode java.lang.String
*/
public void setPk_billtypecode ( java.lang.String pk_billtypecode) {
this.pk_billtypecode=pk_billtypecode;
} 
 
/**
* 属性 pk_billtypeid的Getter方法.属性名：单据类型
*  创建日期:2017-12-5
* @return nc.vo.pub.billtype.BilltypeVO
*/
public java.lang.String getPk_billtypeid() {
return this.pk_billtypeid;
} 

/**
* 属性pk_billtypeid的Setter方法.属性名：单据类型
* 创建日期:2017-12-5
* @param newPk_billtypeid nc.vo.pub.billtype.BilltypeVO
*/
public void setPk_billtypeid ( java.lang.String pk_billtypeid) {
this.pk_billtypeid=pk_billtypeid;
} 
 
/**
* 属性 vbillcode的Getter方法.属性名：单据号
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVbillcode() {
return this.vbillcode;
} 

/**
* 属性vbillcode的Setter方法.属性名：单据号
* 创建日期:2017-12-5
* @param newVbillcode java.lang.String
*/
public void setVbillcode ( java.lang.String vbillcode) {
this.vbillcode=vbillcode;
} 
 
/**
* 属性 contratcno的Getter方法.属性名：合同编号
*  创建日期:2017-12-5
* @return nc.vo.pmpub.project.ProjectHeadVO
*/
public java.lang.String getContratcno() {
return this.contratcno;
} 

/**
* 属性contratcno的Setter方法.属性名：合同编号
* 创建日期:2017-12-5
* @param newContratcno nc.vo.pmpub.project.ProjectHeadVO
*/
public void setContratcno ( java.lang.String contratcno) {
this.contratcno=contratcno;
} 
 
/**
* 属性 serviceno的Getter方法.属性名：服务卡号
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getServiceno() {
return this.serviceno;
} 

/**
* 属性serviceno的Setter方法.属性名：服务卡号
* 创建日期:2017-12-5
* @param newServiceno java.lang.String
*/
public void setServiceno ( java.lang.String serviceno) {
this.serviceno=serviceno;
} 
 
/**
* 属性 startdate的Getter方法.属性名：开始日期
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getStartdate() {
return this.startdate;
} 

/**
* 属性startdate的Setter方法.属性名：开始日期
* 创建日期:2017-12-5
* @param newStartdate nc.vo.pub.lang.UFDate
*/
public void setStartdate ( UFDate startdate) {
this.startdate=startdate;
} 
 
/**
* 属性 enddate的Getter方法.属性名：结束日期
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getEnddate() {
return this.enddate;
} 

/**
* 属性enddate的Setter方法.属性名：结束日期
* 创建日期:2017-12-5
* @param newEnddate nc.vo.pub.lang.UFDate
*/
public void setEnddate ( UFDate enddate) {
this.enddate=enddate;
} 
 
/**
* 属性 contractmny的Getter方法.属性名：合同金额
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getContractmny() {
return this.contractmny;
} 

/**
* 属性contractmny的Setter方法.属性名：合同金额
* 创建日期:2017-12-5
* @param newContractmny nc.vo.pub.lang.UFDouble
*/
public void setContractmny ( nc.vo.pub.lang.UFDouble contractmny) {
this.contractmny=contractmny;
} 
 
/**
* 属性 withholdingmny的Getter方法.属性名：预提金额
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDouble
*/
public nc.vo.pub.lang.UFDouble getWithholdingmny() {
return this.withholdingmny;
} 

/**
* 属性withholdingmny的Setter方法.属性名：预提金额
* 创建日期:2017-12-5
* @param newWithholdingmny nc.vo.pub.lang.UFDouble
*/
public void setWithholdingmny ( nc.vo.pub.lang.UFDouble withholdingmny) {
this.withholdingmny=withholdingmny;
} 
 
/**
* 属性 withholdingdate的Getter方法.属性名：预提日期
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getWithholdingdate() {
return this.withholdingdate;
} 

/**
* 属性withholdingdate的Setter方法.属性名：预提日期
* 创建日期:2017-12-5
* @param newWithholdingdate nc.vo.pub.lang.UFDate
*/
public void setWithholdingdate ( UFDate withholdingdate) {
this.withholdingdate=withholdingdate;
} 
 
/**
* 属性 withholdingdays的Getter方法.属性名：预提天数
*  创建日期:2017-12-5
* @return java.lang.Integer
*/
public java.lang.Integer getWithholdingdays() {
return this.withholdingdays;
} 

/**
* 属性withholdingdays的Setter方法.属性名：预提天数
* 创建日期:2017-12-5
* @param newWithholdingdays java.lang.Integer
*/
public void setWithholdingdays ( java.lang.Integer withholdingdays) {
this.withholdingdays=withholdingdays;
} 
 
/**
* 属性 promanager的Getter方法.属性名：项目经理
*  创建日期:2017-12-5
* @return nc.vo.bd.psn.PsndocVO
*/
public java.lang.String getPromanager() {
return this.promanager;
} 

/**
* 属性promanager的Setter方法.属性名：项目经理
* 创建日期:2017-12-5
* @param newPromanager nc.vo.bd.psn.PsndocVO
*/
public void setPromanager ( java.lang.String promanager) {
this.promanager=promanager;
} 
 
/**
* 属性 stylist的Getter方法.属性名：设计师
*  创建日期:2017-12-5
* @return nc.vo.bd.psn.PsndocVO
*/
public java.lang.String getStylist() {
return this.stylist;
} 

/**
* 属性stylist的Setter方法.属性名：设计师
* 创建日期:2017-12-5
* @param newStylist nc.vo.bd.psn.PsndocVO
*/
public void setStylist ( java.lang.String stylist) {
this.stylist=stylist;
} 
 
/**
* 属性 prostyle的Getter方法.属性名：项目类型
*  创建日期:2017-12-5
* @return nc.vo.bd.defdoc.DefdocVO
*/
public java.lang.String getProstyle() {
return this.prostyle;
} 

/**
* 属性prostyle的Setter方法.属性名：项目类型
* 创建日期:2017-12-5
* @param newProstyle nc.vo.bd.defdoc.DefdocVO
*/
public void setProstyle ( java.lang.String prostyle) {
this.prostyle=prostyle;
} 
 
/**
* 属性 overview的Getter方法.属性名：概述
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getOverview() {
return this.overview;
} 

/**
* 属性overview的Setter方法.属性名：概述
* 创建日期:2017-12-5
* @param newOverview java.lang.String
*/
public void setOverview ( java.lang.String overview) {
this.overview=overview;
} 
 
/**
* 属性 dbilldate的Getter方法.属性名：单据日期
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getDbilldate() {
return this.dbilldate;
} 

/**
* 属性dbilldate的Setter方法.属性名：单据日期
* 创建日期:2017-12-5
* @param newDbilldate nc.vo.pub.lang.UFDate
*/
public void setDbilldate ( UFDate dbilldate) {
this.dbilldate=dbilldate;
} 
 
/**
* 属性 isaffirm的Getter方法.属性名：是否确认
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIsaffirm() {
return this.isaffirm;
} 

/**
* 属性isaffirm的Setter方法.属性名：是否确认
* 创建日期:2017-12-5
* @param newIsaffirm nc.vo.pub.lang.UFBoolean
*/
public void setIsaffirm ( UFBoolean isaffirm) {
this.isaffirm=isaffirm;
} 
 
/**
* 属性 creator的Getter方法.属性名：创建人
*  创建日期:2017-12-5
* @return nc.vo.sm.UserVO
*/
public java.lang.String getCreator() {
return this.creator;
} 

/**
* 属性creator的Setter方法.属性名：创建人
* 创建日期:2017-12-5
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( java.lang.String creator) {
this.creator=creator;
} 
 
/**
* 属性 creationtime的Getter方法.属性名：创建时间
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* 属性creationtime的Setter方法.属性名：创建时间
* 创建日期:2017-12-5
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* 属性 modifier的Getter方法.属性名：最后修改人
*  创建日期:2017-12-5
* @return nc.vo.sm.UserVO
*/
public java.lang.String getModifier() {
return this.modifier;
} 

/**
* 属性modifier的Setter方法.属性名：最后修改人
* 创建日期:2017-12-5
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( java.lang.String modifier) {
this.modifier=modifier;
} 
 
/**
* 属性 modifiedtime的Getter方法.属性名：最后修改时间
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* 属性modifiedtime的Setter方法.属性名：最后修改时间
* 创建日期:2017-12-5
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* 属性 vmemo的Getter方法.属性名：备注
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVmemo() {
return this.vmemo;
} 

/**
* 属性vmemo的Setter方法.属性名：备注
* 创建日期:2017-12-5
* @param newVmemo java.lang.String
*/
public void setVmemo ( java.lang.String vmemo) {
this.vmemo=vmemo;
} 
 
/**
* 属性 csourcetypecode的Getter方法.属性名：来源单据类型
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourcetypecode() {
return this.csourcetypecode;
} 

/**
* 属性csourcetypecode的Setter方法.属性名：来源单据类型
* 创建日期:2017-12-5
* @param newCsourcetypecode java.lang.String
*/
public void setCsourcetypecode ( java.lang.String csourcetypecode) {
this.csourcetypecode=csourcetypecode;
} 
 
/**
* 属性 vsourcecode的Getter方法.属性名：来源单据号
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcecode() {
return this.vsourcecode;
} 

/**
* 属性vsourcecode的Setter方法.属性名：来源单据号
* 创建日期:2017-12-5
* @param newVsourcecode java.lang.String
*/
public void setVsourcecode ( java.lang.String vsourcecode) {
this.vsourcecode=vsourcecode;
} 
 
/**
* 属性 csourceid的Getter方法.属性名：来源单据
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourceid() {
return this.csourceid;
} 

/**
* 属性csourceid的Setter方法.属性名：来源单据
* 创建日期:2017-12-5
* @param newCsourceid java.lang.String
*/
public void setCsourceid ( java.lang.String csourceid) {
this.csourceid=csourceid;
} 
 
/**
* 属性 csourcebid的Getter方法.属性名：来源单据明细
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getCsourcebid() {
return this.csourcebid;
} 

/**
* 属性csourcebid的Setter方法.属性名：来源单据明细
* 创建日期:2017-12-5
* @param newCsourcebid java.lang.String
*/
public void setCsourcebid ( java.lang.String csourcebid) {
this.csourcebid=csourcebid;
} 
 
/**
* 属性 vsourcerowno的Getter方法.属性名：来源单据行号
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcerowno() {
return this.vsourcerowno;
} 

/**
* 属性vsourcerowno的Setter方法.属性名：来源单据行号
* 创建日期:2017-12-5
* @param newVsourcerowno java.lang.String
*/
public void setVsourcerowno ( java.lang.String vsourcerowno) {
this.vsourcerowno=vsourcerowno;
} 
 
/**
* 属性 vsourcetrantype的Getter方法.属性名：来源交易类型
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVsourcetrantype() {
return this.vsourcetrantype;
} 

/**
* 属性vsourcetrantype的Setter方法.属性名：来源交易类型
* 创建日期:2017-12-5
* @param newVsourcetrantype java.lang.String
*/
public void setVsourcetrantype ( java.lang.String vsourcetrantype) {
this.vsourcetrantype=vsourcetrantype;
} 
 
/**
* 属性 vdef1的Getter方法.属性名：自定义项1
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef1() {
return this.vdef1;
} 

/**
* 属性vdef1的Setter方法.属性名：自定义项1
* 创建日期:2017-12-5
* @param newVdef1 java.lang.String
*/
public void setVdef1 ( java.lang.String vdef1) {
this.vdef1=vdef1;
} 
 
/**
* 属性 vdef2的Getter方法.属性名：自定义项2
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef2() {
return this.vdef2;
} 

/**
* 属性vdef2的Setter方法.属性名：自定义项2
* 创建日期:2017-12-5
* @param newVdef2 java.lang.String
*/
public void setVdef2 ( java.lang.String vdef2) {
this.vdef2=vdef2;
} 
 
/**
* 属性 vdef3的Getter方法.属性名：自定义项3
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef3() {
return this.vdef3;
} 

/**
* 属性vdef3的Setter方法.属性名：自定义项3
* 创建日期:2017-12-5
* @param newVdef3 java.lang.String
*/
public void setVdef3 ( java.lang.String vdef3) {
this.vdef3=vdef3;
} 
 
/**
* 属性 vdef4的Getter方法.属性名：自定义项4
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef4() {
return this.vdef4;
} 

/**
* 属性vdef4的Setter方法.属性名：自定义项4
* 创建日期:2017-12-5
* @param newVdef4 java.lang.String
*/
public void setVdef4 ( java.lang.String vdef4) {
this.vdef4=vdef4;
} 
 
/**
* 属性 vdef5的Getter方法.属性名：自定义项5
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef5() {
return this.vdef5;
} 

/**
* 属性vdef5的Setter方法.属性名：自定义项5
* 创建日期:2017-12-5
* @param newVdef5 java.lang.String
*/
public void setVdef5 ( java.lang.String vdef5) {
this.vdef5=vdef5;
} 
 
/**
* 属性 vdef6的Getter方法.属性名：自定义项6
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef6() {
return this.vdef6;
} 

/**
* 属性vdef6的Setter方法.属性名：自定义项6
* 创建日期:2017-12-5
* @param newVdef6 java.lang.String
*/
public void setVdef6 ( java.lang.String vdef6) {
this.vdef6=vdef6;
} 
 
/**
* 属性 vdef7的Getter方法.属性名：自定义项7
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef7() {
return this.vdef7;
} 

/**
* 属性vdef7的Setter方法.属性名：自定义项7
* 创建日期:2017-12-5
* @param newVdef7 java.lang.String
*/
public void setVdef7 ( java.lang.String vdef7) {
this.vdef7=vdef7;
} 
 
/**
* 属性 vdef8的Getter方法.属性名：自定义项8
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef8() {
return this.vdef8;
} 

/**
* 属性vdef8的Setter方法.属性名：自定义项8
* 创建日期:2017-12-5
* @param newVdef8 java.lang.String
*/
public void setVdef8 ( java.lang.String vdef8) {
this.vdef8=vdef8;
} 
 
/**
* 属性 vdef9的Getter方法.属性名：自定义项9
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef9() {
return this.vdef9;
} 

/**
* 属性vdef9的Setter方法.属性名：自定义项9
* 创建日期:2017-12-5
* @param newVdef9 java.lang.String
*/
public void setVdef9 ( java.lang.String vdef9) {
this.vdef9=vdef9;
} 
 
/**
* 属性 vdef10的Getter方法.属性名：自定义项10
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef10() {
return this.vdef10;
} 

/**
* 属性vdef10的Setter方法.属性名：自定义项10
* 创建日期:2017-12-5
* @param newVdef10 java.lang.String
*/
public void setVdef10 ( java.lang.String vdef10) {
this.vdef10=vdef10;
} 
 
/**
* 属性 vdef11的Getter方法.属性名：自定义项11
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef11() {
return this.vdef11;
} 

/**
* 属性vdef11的Setter方法.属性名：自定义项11
* 创建日期:2017-12-5
* @param newVdef11 java.lang.String
*/
public void setVdef11 ( java.lang.String vdef11) {
this.vdef11=vdef11;
} 
 
/**
* 属性 vdef12的Getter方法.属性名：自定义项12
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef12() {
return this.vdef12;
} 

/**
* 属性vdef12的Setter方法.属性名：自定义项12
* 创建日期:2017-12-5
* @param newVdef12 java.lang.String
*/
public void setVdef12 ( java.lang.String vdef12) {
this.vdef12=vdef12;
} 
 
/**
* 属性 vdef13的Getter方法.属性名：自定义项13
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef13() {
return this.vdef13;
} 

/**
* 属性vdef13的Setter方法.属性名：自定义项13
* 创建日期:2017-12-5
* @param newVdef13 java.lang.String
*/
public void setVdef13 ( java.lang.String vdef13) {
this.vdef13=vdef13;
} 
 
/**
* 属性 vdef14的Getter方法.属性名：自定义项14
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef14() {
return this.vdef14;
} 

/**
* 属性vdef14的Setter方法.属性名：自定义项14
* 创建日期:2017-12-5
* @param newVdef14 java.lang.String
*/
public void setVdef14 ( java.lang.String vdef14) {
this.vdef14=vdef14;
} 
 
/**
* 属性 vdef15的Getter方法.属性名：自定义项15
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef15() {
return this.vdef15;
} 

/**
* 属性vdef15的Setter方法.属性名：自定义项15
* 创建日期:2017-12-5
* @param newVdef15 java.lang.String
*/
public void setVdef15 ( java.lang.String vdef15) {
this.vdef15=vdef15;
} 
 
/**
* 属性 vdef16的Getter方法.属性名：自定义项16
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef16() {
return this.vdef16;
} 

/**
* 属性vdef16的Setter方法.属性名：自定义项16
* 创建日期:2017-12-5
* @param newVdef16 java.lang.String
*/
public void setVdef16 ( java.lang.String vdef16) {
this.vdef16=vdef16;
} 
 
/**
* 属性 vdef17的Getter方法.属性名：自定义项17
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef17() {
return this.vdef17;
} 

/**
* 属性vdef17的Setter方法.属性名：自定义项17
* 创建日期:2017-12-5
* @param newVdef17 java.lang.String
*/
public void setVdef17 ( java.lang.String vdef17) {
this.vdef17=vdef17;
} 
 
/**
* 属性 vdef18的Getter方法.属性名：自定义项18
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef18() {
return this.vdef18;
} 

/**
* 属性vdef18的Setter方法.属性名：自定义项18
* 创建日期:2017-12-5
* @param newVdef18 java.lang.String
*/
public void setVdef18 ( java.lang.String vdef18) {
this.vdef18=vdef18;
} 
 
/**
* 属性 vdef19的Getter方法.属性名：自定义项19
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef19() {
return this.vdef19;
} 

/**
* 属性vdef19的Setter方法.属性名：自定义项19
* 创建日期:2017-12-5
* @param newVdef19 java.lang.String
*/
public void setVdef19 ( java.lang.String vdef19) {
this.vdef19=vdef19;
} 
 
/**
* 属性 vdef20的Getter方法.属性名：自定义项20
*  创建日期:2017-12-5
* @return java.lang.String
*/
public java.lang.String getVdef20() {
return this.vdef20;
} 

/**
* 属性vdef20的Setter方法.属性名：自定义项20
* 创建日期:2017-12-5
* @param newVdef20 java.lang.String
*/
public void setVdef20 ( java.lang.String vdef20) {
this.vdef20=vdef20;
} 
 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2017-12-5
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2017-12-5
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
    