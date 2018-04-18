package nc.vo.fee.wagesappro;

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
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2017-11-18
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class WagesapproBVO extends SuperVO {

	/**
	 * 主键
	 */
	public java.lang.String pk_wagesappro_b;
	/**
	 * 集团
	 */
	public java.lang.String pk_group;
	/**
	 * 组织
	 */
	public java.lang.String pk_org;
	/**
	 * 组织多版本
	 */
	public java.lang.String pk_org_v;
	/**
	 * 行号
	 */
	public java.lang.String crowno;
	/**
	 * 姓名
	 */
	public java.lang.String name;
	/**
	 * 性别
	 */
	public java.lang.Integer sex;
	/**
	 * 年龄
	 */
	public java.lang.Integer age;
	/**
	 * 手机
	 */
	public java.lang.String telphone;
	/**
	 * 证件号
	 */
	public java.lang.String idnumber;
	/**
	 * 班组长
	 */
	public java.lang.String pleader;
	/**
	 * 工种
	 */
	public java.lang.String worktype;
	/**
	 * 工作天数
	 */
	public nc.vo.pub.lang.UFDouble workdays;
	/**
	 * 日工资
	 */
	public nc.vo.pub.lang.UFDouble daywages;
	/**
	 * 应发工资
	 */
	public nc.vo.pub.lang.UFDouble shouldwages;
	/**
	 * 扣缴个税
	 */
	public nc.vo.pub.lang.UFDouble psntax;
	/**
	 * 实发工资
	 */
	public nc.vo.pub.lang.UFDouble realwages;
	/**
	 * 签名
	 */
	public java.lang.String signature;
	/**
	 * 来源单据类型
	 */
	public java.lang.String csourcetypecode;
	/**
	 * 来源单据号
	 */
	public java.lang.String vsourcecode;
	/**
	 * 来源单据
	 */
	public java.lang.String csourceid;
	/**
	 * 来源单据明细
	 */
	public java.lang.String csourcebid;
	/**
	 * 来源单据行号
	 */
	public java.lang.String vsourcerowno;
	/**
	 * 来源交易类型
	 */
	public java.lang.String vsourcetrantype;
	/**
	 * 备注
	 */
	public java.lang.String vmemo;
	/**
	 * 自定义项1
	 */
	public java.lang.String vbdef1;
	/**
	 * 自定义项2
	 */
	public java.lang.String vbdef2;
	/**
	 * 自定义项3
	 */
	public java.lang.String vbdef3;
	/**
	 * 自定义项4
	 */
	public java.lang.String vbdef4;
	/**
	 * 自定义项5
	 */
	public java.lang.String vbdef5;
	/**
	 * 自定义项6
	 */
	public java.lang.String vbdef6;
	/**
	 * 自定义项7
	 */
	public java.lang.String vbdef7;
	/**
	 * 自定义项8
	 */
	public java.lang.String vbdef8;
	/**
	 * 自定义项9
	 */
	public java.lang.String vbdef9;
	/**
	 * 自定义项10
	 */
	public java.lang.String vbdef10;
	/**
	 * 自定义项11
	 */
	public java.lang.String vbdef11;
	/**
	 * 自定义项12
	 */
	public java.lang.String vbdef12;
	/**
	 * 自定义项13
	 */
	public java.lang.String vbdef13;
	/**
	 * 自定义项14
	 */
	public java.lang.String vbdef14;
	/**
	 * 自定义项15
	 */
	public java.lang.String vbdef15;
	/**
	 * 自定义项16
	 */
	public java.lang.String vbdef16;
	/**
	 * 自定义项17
	 */
	public java.lang.String vbdef17;
	/**
	 * 自定义项18
	 */
	public java.lang.String vbdef18;
	/**
	 * 自定义项19
	 */
	public java.lang.String vbdef19;
	/**
	 * 自定义项20
	 */
	public java.lang.String vbdef20;
	/**
	 * 上层单据主键
	 */
	public String pk_wagesappro;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 属性 pk_wagesappro_b的Getter方法.属性名：主键 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wagesappro_b() {
		return this.pk_wagesappro_b;
	}

	/**
	 * 属性pk_wagesappro_b的Setter方法.属性名：主键 创建日期:2017-11-18
	 * 
	 * @param newPk_wagesappro_b
	 *            java.lang.String
	 */
	public void setPk_wagesappro_b(java.lang.String pk_wagesappro_b) {
		this.pk_wagesappro_b = pk_wagesappro_b;
	}

	/**
	 * 属性 pk_group的Getter方法.属性名：集团 创建日期:2017-11-18
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public java.lang.String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集团 创建日期:2017-11-18
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(java.lang.String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2017-11-18
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public java.lang.String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2017-11-18
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(java.lang.String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 pk_org_v的Getter方法.属性名：组织多版本 创建日期:2017-11-18
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public java.lang.String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 属性pk_org_v的Setter方法.属性名：组织多版本 创建日期:2017-11-18
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(java.lang.String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 属性 crowno的Getter方法.属性名：行号 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCrowno() {
		return this.crowno;
	}

	/**
	 * 属性crowno的Setter方法.属性名：行号 创建日期:2017-11-18
	 * 
	 * @param newCrowno
	 *            java.lang.String
	 */
	public void setCrowno(java.lang.String crowno) {
		this.crowno = crowno;
	}

	/**
	 * 属性 name的Getter方法.属性名：姓名 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * 属性name的Setter方法.属性名：姓名 创建日期:2017-11-18
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 属性 sex的Getter方法.属性名：性别 创建日期:2017-11-18
	 * 
	 * @return nc.vo.fee.workteam.SexEnum
	 */
	public java.lang.Integer getSex() {
		return this.sex;
	}

	/**
	 * 属性sex的Setter方法.属性名：性别 创建日期:2017-11-18
	 * 
	 * @param newSex
	 *            nc.vo.fee.workteam.SexEnum
	 */
	public void setSex(java.lang.Integer sex) {
		this.sex = sex;
	}

	/**
	 * 属性 age的Getter方法.属性名：年龄 创建日期:2017-11-18
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getAge() {
		return this.age;
	}

	/**
	 * 属性age的Setter方法.属性名：年龄 创建日期:2017-11-18
	 * 
	 * @param newAge
	 *            java.lang.Integer
	 */
	public void setAge(java.lang.Integer age) {
		this.age = age;
	}

	/**
	 * 属性 telphone的Getter方法.属性名：手机 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTelphone() {
		return this.telphone;
	}

	/**
	 * 属性telphone的Setter方法.属性名：手机 创建日期:2017-11-18
	 * 
	 * @param newTelphone
	 *            java.lang.String
	 */
	public void setTelphone(java.lang.String telphone) {
		this.telphone = telphone;
	}

	/**
	 * 属性 idnumber的Getter方法.属性名：证件号 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getIdnumber() {
		return this.idnumber;
	}

	/**
	 * 属性idnumber的Setter方法.属性名：证件号 创建日期:2017-11-18
	 * 
	 * @param newIdnumber
	 *            java.lang.String
	 */
	public void setIdnumber(java.lang.String idnumber) {
		this.idnumber = idnumber;
	}

	/**
	 * 属性 pleader的Getter方法.属性名：班组长 创建日期:2017-11-18
	 * 
	 * @return nc.vo.fee.workteam.WorkteamVO
	 */
	public java.lang.String getPleader() {
		return this.pleader;
	}

	/**
	 * 属性pleader的Setter方法.属性名：班组长 创建日期:2017-11-18
	 * 
	 * @param newPleader
	 *            nc.vo.fee.workteam.WorkteamVO
	 */
	public void setPleader(java.lang.String pleader) {
		this.pleader = pleader;
	}

	/**
	 * 属性 worktype的Getter方法.属性名：工种 创建日期:2017-11-18
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public java.lang.String getWorktype() {
		return this.worktype;
	}

	/**
	 * 属性worktype的Setter方法.属性名：工种 创建日期:2017-11-18
	 * 
	 * @param newWorktype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setWorktype(java.lang.String worktype) {
		this.worktype = worktype;
	}

	/**
	 * 属性 workdays的Getter方法.属性名：工作天数 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getWorkdays() {
		return this.workdays;
	}

	/**
	 * 属性workdays的Setter方法.属性名：工作天数 创建日期:2017-11-18
	 * 
	 * @param newWorkdays
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setWorkdays(nc.vo.pub.lang.UFDouble workdays) {
		this.workdays = workdays;
	}

	/**
	 * 属性 daywages的Getter方法.属性名：日工资 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getDaywages() {
		return this.daywages;
	}

	/**
	 * 属性daywages的Setter方法.属性名：日工资 创建日期:2017-11-18
	 * 
	 * @param newDaywages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaywages(nc.vo.pub.lang.UFDouble daywages) {
		this.daywages = daywages;
	}

	/**
	 * 属性 shouldwages的Getter方法.属性名：应发工资 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getShouldwages() {
		return this.shouldwages;
	}

	/**
	 * 属性shouldwages的Setter方法.属性名：应发工资 创建日期:2017-11-18
	 * 
	 * @param newShouldwages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setShouldwages(nc.vo.pub.lang.UFDouble shouldwages) {
		this.shouldwages = shouldwages;
	}

	/**
	 * 属性 psntax的Getter方法.属性名：扣缴个税 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getPsntax() {
		return this.psntax;
	}

	/**
	 * 属性psntax的Setter方法.属性名：扣缴个税 创建日期:2017-11-18
	 * 
	 * @param newPsntax
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setPsntax(nc.vo.pub.lang.UFDouble psntax) {
		this.psntax = psntax;
	}

	/**
	 * 属性 realwages的Getter方法.属性名：实发工资 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRealwages() {
		return this.realwages;
	}

	/**
	 * 属性realwages的Setter方法.属性名：实发工资 创建日期:2017-11-18
	 * 
	 * @param newRealwages
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRealwages(nc.vo.pub.lang.UFDouble realwages) {
		this.realwages = realwages;
	}

	/**
	 * 属性 signature的Getter方法.属性名：签名 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSignature() {
		return this.signature;
	}

	/**
	 * 属性signature的Setter方法.属性名：签名 创建日期:2017-11-18
	 * 
	 * @param newSignature
	 *            java.lang.String
	 */
	public void setSignature(java.lang.String signature) {
		this.signature = signature;
	}

	/**
	 * 属性 csourcetypecode的Getter方法.属性名：来源单据类型 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcetypecode() {
		return this.csourcetypecode;
	}

	/**
	 * 属性csourcetypecode的Setter方法.属性名：来源单据类型 创建日期:2017-11-18
	 * 
	 * @param newCsourcetypecode
	 *            java.lang.String
	 */
	public void setCsourcetypecode(java.lang.String csourcetypecode) {
		this.csourcetypecode = csourcetypecode;
	}

	/**
	 * 属性 vsourcecode的Getter方法.属性名：来源单据号 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcecode() {
		return this.vsourcecode;
	}

	/**
	 * 属性vsourcecode的Setter方法.属性名：来源单据号 创建日期:2017-11-18
	 * 
	 * @param newVsourcecode
	 *            java.lang.String
	 */
	public void setVsourcecode(java.lang.String vsourcecode) {
		this.vsourcecode = vsourcecode;
	}

	/**
	 * 属性 csourceid的Getter方法.属性名：来源单据 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourceid() {
		return this.csourceid;
	}

	/**
	 * 属性csourceid的Setter方法.属性名：来源单据 创建日期:2017-11-18
	 * 
	 * @param newCsourceid
	 *            java.lang.String
	 */
	public void setCsourceid(java.lang.String csourceid) {
		this.csourceid = csourceid;
	}

	/**
	 * 属性 csourcebid的Getter方法.属性名：来源单据明细 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCsourcebid() {
		return this.csourcebid;
	}

	/**
	 * 属性csourcebid的Setter方法.属性名：来源单据明细 创建日期:2017-11-18
	 * 
	 * @param newCsourcebid
	 *            java.lang.String
	 */
	public void setCsourcebid(java.lang.String csourcebid) {
		this.csourcebid = csourcebid;
	}

	/**
	 * 属性 vsourcerowno的Getter方法.属性名：来源单据行号 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcerowno() {
		return this.vsourcerowno;
	}

	/**
	 * 属性vsourcerowno的Setter方法.属性名：来源单据行号 创建日期:2017-11-18
	 * 
	 * @param newVsourcerowno
	 *            java.lang.String
	 */
	public void setVsourcerowno(java.lang.String vsourcerowno) {
		this.vsourcerowno = vsourcerowno;
	}

	/**
	 * 属性 vsourcetrantype的Getter方法.属性名：来源交易类型 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVsourcetrantype() {
		return this.vsourcetrantype;
	}

	/**
	 * 属性vsourcetrantype的Setter方法.属性名：来源交易类型 创建日期:2017-11-18
	 * 
	 * @param newVsourcetrantype
	 *            java.lang.String
	 */
	public void setVsourcetrantype(java.lang.String vsourcetrantype) {
		this.vsourcetrantype = vsourcetrantype;
	}

	/**
	 * 属性 vmemo的Getter方法.属性名：备注 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVmemo() {
		return this.vmemo;
	}

	/**
	 * 属性vmemo的Setter方法.属性名：备注 创建日期:2017-11-18
	 * 
	 * @param newVmemo
	 *            java.lang.String
	 */
	public void setVmemo(java.lang.String vmemo) {
		this.vmemo = vmemo;
	}

	/**
	 * 属性 vbdef1的Getter方法.属性名：自定义项1 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef1() {
		return this.vbdef1;
	}

	/**
	 * 属性vbdef1的Setter方法.属性名：自定义项1 创建日期:2017-11-18
	 * 
	 * @param newVbdef1
	 *            java.lang.String
	 */
	public void setVbdef1(java.lang.String vbdef1) {
		this.vbdef1 = vbdef1;
	}

	/**
	 * 属性 vbdef2的Getter方法.属性名：自定义项2 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef2() {
		return this.vbdef2;
	}

	/**
	 * 属性vbdef2的Setter方法.属性名：自定义项2 创建日期:2017-11-18
	 * 
	 * @param newVbdef2
	 *            java.lang.String
	 */
	public void setVbdef2(java.lang.String vbdef2) {
		this.vbdef2 = vbdef2;
	}

	/**
	 * 属性 vbdef3的Getter方法.属性名：自定义项3 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef3() {
		return this.vbdef3;
	}

	/**
	 * 属性vbdef3的Setter方法.属性名：自定义项3 创建日期:2017-11-18
	 * 
	 * @param newVbdef3
	 *            java.lang.String
	 */
	public void setVbdef3(java.lang.String vbdef3) {
		this.vbdef3 = vbdef3;
	}

	/**
	 * 属性 vbdef4的Getter方法.属性名：自定义项4 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef4() {
		return this.vbdef4;
	}

	/**
	 * 属性vbdef4的Setter方法.属性名：自定义项4 创建日期:2017-11-18
	 * 
	 * @param newVbdef4
	 *            java.lang.String
	 */
	public void setVbdef4(java.lang.String vbdef4) {
		this.vbdef4 = vbdef4;
	}

	/**
	 * 属性 vbdef5的Getter方法.属性名：自定义项5 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef5() {
		return this.vbdef5;
	}

	/**
	 * 属性vbdef5的Setter方法.属性名：自定义项5 创建日期:2017-11-18
	 * 
	 * @param newVbdef5
	 *            java.lang.String
	 */
	public void setVbdef5(java.lang.String vbdef5) {
		this.vbdef5 = vbdef5;
	}

	/**
	 * 属性 vbdef6的Getter方法.属性名：自定义项6 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef6() {
		return this.vbdef6;
	}

	/**
	 * 属性vbdef6的Setter方法.属性名：自定义项6 创建日期:2017-11-18
	 * 
	 * @param newVbdef6
	 *            java.lang.String
	 */
	public void setVbdef6(java.lang.String vbdef6) {
		this.vbdef6 = vbdef6;
	}

	/**
	 * 属性 vbdef7的Getter方法.属性名：自定义项7 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef7() {
		return this.vbdef7;
	}

	/**
	 * 属性vbdef7的Setter方法.属性名：自定义项7 创建日期:2017-11-18
	 * 
	 * @param newVbdef7
	 *            java.lang.String
	 */
	public void setVbdef7(java.lang.String vbdef7) {
		this.vbdef7 = vbdef7;
	}

	/**
	 * 属性 vbdef8的Getter方法.属性名：自定义项8 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef8() {
		return this.vbdef8;
	}

	/**
	 * 属性vbdef8的Setter方法.属性名：自定义项8 创建日期:2017-11-18
	 * 
	 * @param newVbdef8
	 *            java.lang.String
	 */
	public void setVbdef8(java.lang.String vbdef8) {
		this.vbdef8 = vbdef8;
	}

	/**
	 * 属性 vbdef9的Getter方法.属性名：自定义项9 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef9() {
		return this.vbdef9;
	}

	/**
	 * 属性vbdef9的Setter方法.属性名：自定义项9 创建日期:2017-11-18
	 * 
	 * @param newVbdef9
	 *            java.lang.String
	 */
	public void setVbdef9(java.lang.String vbdef9) {
		this.vbdef9 = vbdef9;
	}

	/**
	 * 属性 vbdef10的Getter方法.属性名：自定义项10 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef10() {
		return this.vbdef10;
	}

	/**
	 * 属性vbdef10的Setter方法.属性名：自定义项10 创建日期:2017-11-18
	 * 
	 * @param newVbdef10
	 *            java.lang.String
	 */
	public void setVbdef10(java.lang.String vbdef10) {
		this.vbdef10 = vbdef10;
	}

	/**
	 * 属性 vbdef11的Getter方法.属性名：自定义项11 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef11() {
		return this.vbdef11;
	}

	/**
	 * 属性vbdef11的Setter方法.属性名：自定义项11 创建日期:2017-11-18
	 * 
	 * @param newVbdef11
	 *            java.lang.String
	 */
	public void setVbdef11(java.lang.String vbdef11) {
		this.vbdef11 = vbdef11;
	}

	/**
	 * 属性 vbdef12的Getter方法.属性名：自定义项12 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef12() {
		return this.vbdef12;
	}

	/**
	 * 属性vbdef12的Setter方法.属性名：自定义项12 创建日期:2017-11-18
	 * 
	 * @param newVbdef12
	 *            java.lang.String
	 */
	public void setVbdef12(java.lang.String vbdef12) {
		this.vbdef12 = vbdef12;
	}

	/**
	 * 属性 vbdef13的Getter方法.属性名：自定义项13 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef13() {
		return this.vbdef13;
	}

	/**
	 * 属性vbdef13的Setter方法.属性名：自定义项13 创建日期:2017-11-18
	 * 
	 * @param newVbdef13
	 *            java.lang.String
	 */
	public void setVbdef13(java.lang.String vbdef13) {
		this.vbdef13 = vbdef13;
	}

	/**
	 * 属性 vbdef14的Getter方法.属性名：自定义项14 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef14() {
		return this.vbdef14;
	}

	/**
	 * 属性vbdef14的Setter方法.属性名：自定义项14 创建日期:2017-11-18
	 * 
	 * @param newVbdef14
	 *            java.lang.String
	 */
	public void setVbdef14(java.lang.String vbdef14) {
		this.vbdef14 = vbdef14;
	}

	/**
	 * 属性 vbdef15的Getter方法.属性名：自定义项15 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef15() {
		return this.vbdef15;
	}

	/**
	 * 属性vbdef15的Setter方法.属性名：自定义项15 创建日期:2017-11-18
	 * 
	 * @param newVbdef15
	 *            java.lang.String
	 */
	public void setVbdef15(java.lang.String vbdef15) {
		this.vbdef15 = vbdef15;
	}

	/**
	 * 属性 vbdef16的Getter方法.属性名：自定义项16 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef16() {
		return this.vbdef16;
	}

	/**
	 * 属性vbdef16的Setter方法.属性名：自定义项16 创建日期:2017-11-18
	 * 
	 * @param newVbdef16
	 *            java.lang.String
	 */
	public void setVbdef16(java.lang.String vbdef16) {
		this.vbdef16 = vbdef16;
	}

	/**
	 * 属性 vbdef17的Getter方法.属性名：自定义项17 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef17() {
		return this.vbdef17;
	}

	/**
	 * 属性vbdef17的Setter方法.属性名：自定义项17 创建日期:2017-11-18
	 * 
	 * @param newVbdef17
	 *            java.lang.String
	 */
	public void setVbdef17(java.lang.String vbdef17) {
		this.vbdef17 = vbdef17;
	}

	/**
	 * 属性 vbdef18的Getter方法.属性名：自定义项18 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef18() {
		return this.vbdef18;
	}

	/**
	 * 属性vbdef18的Setter方法.属性名：自定义项18 创建日期:2017-11-18
	 * 
	 * @param newVbdef18
	 *            java.lang.String
	 */
	public void setVbdef18(java.lang.String vbdef18) {
		this.vbdef18 = vbdef18;
	}

	/**
	 * 属性 vbdef19的Getter方法.属性名：自定义项19 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef19() {
		return this.vbdef19;
	}

	/**
	 * 属性vbdef19的Setter方法.属性名：自定义项19 创建日期:2017-11-18
	 * 
	 * @param newVbdef19
	 *            java.lang.String
	 */
	public void setVbdef19(java.lang.String vbdef19) {
		this.vbdef19 = vbdef19;
	}

	/**
	 * 属性 vbdef20的Getter方法.属性名：自定义项20 创建日期:2017-11-18
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVbdef20() {
		return this.vbdef20;
	}

	/**
	 * 属性vbdef20的Setter方法.属性名：自定义项20 创建日期:2017-11-18
	 * 
	 * @param newVbdef20
	 *            java.lang.String
	 */
	public void setVbdef20(java.lang.String vbdef20) {
		this.vbdef20 = vbdef20;
	}

	/**
	 * 属性 生成上层主键的Getter方法.属性名：上层主键 创建日期:2017-11-18
	 * 
	 * @return String
	 */
	public String getPk_wagesappro() {
		return this.pk_wagesappro;
	}

	/**
	 * 属性生成上层主键的Setter方法.属性名：上层主键 创建日期:2017-11-18
	 * 
	 * @param newPk_wagesappro
	 *            String
	 */
	public void setPk_wagesappro(String pk_wagesappro) {
		this.pk_wagesappro = pk_wagesappro;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2017-11-18
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2017-11-18
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
