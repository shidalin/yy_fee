DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ10000000008U3M';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ10000000008U3M';
DELETE FROM pub_function WHERE pk_billtype = 'FE02';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ10000000008U3M';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'FE02';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ10000000008U3M';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000008U3N';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000008U3O';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000008U3P';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000008U3Q';