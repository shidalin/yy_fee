INSERT INTO bd_billtype (ts, iseditableproperty, pk_billtypecode, ncbrcode, parentbilltype, canextendtransaction, istransaction, isbizflowbill, datafinderclz, isaccount, referclassname, pk_org, component, isroot, billtypename, billcoderule, emendenumclass, dr, nodecode, isenablebutton, pk_billtypeid, classname, systemcode, checkclassname, accountclass, islock, forwardbilltype, billtypename2, billtypename3, billtypename4, transtype_class, billtypename5, pk_group, billtypename6, webnodecode, billstyle, def3, isapprovebill, def2, isenabletranstypebcr, wherestring, def1 ) VALUES ('2017-11-19 00:04:36', null, 'FE03', '~', '~', 'Y', 'N', null, null, null, null, 'GLOBLE00000000000000', 'wagesappro', null, '人工工资审批', '~', null, null, 'FEH10203', null, '0001ZZ10000000008N72', null, 'FEE', null, null, null, null, null, null, null, null, null, '~', null, '~', null, null, 'Y', null, null, null, null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'N', 'N', '0001ZZ10000000008N73', null, 'SAVE', null, null, '送审', null, null, 10, 'N', '1', null, 'FE03', null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'N', 'N', '0001ZZ10000000008N74', null, 'APPROVE', null, null, '审核', null, null, 11, 'N', '2', null, 'FE03', null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'Y', 'Y', '0001ZZ10000000008N75', null, 'UNSAVEBILL', null, null, '收回', null, null, 13, 'N', '3', null, 'FE03', null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'N', 'Y', '0001ZZ10000000008N76', null, 'UNAPPROVE', null, null, '弃审', null, null, 12, 'N', '3', null, 'FE03', null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'N', 'N', '0001ZZ10000000008N77', null, 'DELETE', null, null, '删除', null, null, 30, 'N', '3', null, 'FE03', null );
INSERT INTO pub_billaction (ts, actionstyleremark, pushflag, pk_billtypeid, controlflag, finishflag, pk_billaction, actionnote6, actiontype, actionnote4, actionnote5, actionnote, actionnote2, actionnote3, action_type, constrictflag, actionstyle, showhint, pk_billtype, dr ) VALUES ('2017-11-19 00:04:44', null, null, '0001ZZ10000000008N72', 'N', 'N', '0001ZZ10000000008N78', null, 'SAVEBASE', null, null, '保存', null, null, 31, 'Y', '1', null, 'FE03', null );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_SAVE', 'N', 'SAVE', '~', 0, 'FE03', '0001ZZ10000000008N79' );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_APPROVE', 'N', 'APPROVE', '~', 0, 'FE03', '0001ZZ10000000008N7A' );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_UNSAVEBILL', 'N', 'UNSAVEBILL', '~', 0, 'FE03', '0001ZZ10000000008N7B' );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_UNAPPROVE', 'N', 'UNAPPROVE', '~', 0, 'FE03', '0001ZZ10000000008N7C' );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_DELETE', 'N', 'DELETE', '~', 0, 'FE03', '0001ZZ10000000008N7D' );
INSERT INTO pub_busiclass (ts, pk_billtypeid, pk_businesstype, classname, isbefore, actiontype, pk_group, dr, pk_billtype, pk_busiclass ) VALUES ('2017-11-19 00:04:44', '0001ZZ10000000008N72', '~', 'N_FE03_SAVEBASE', 'N', 'SAVEBASE', '~', 0, 'FE03', '0001ZZ10000000008N7E' );
