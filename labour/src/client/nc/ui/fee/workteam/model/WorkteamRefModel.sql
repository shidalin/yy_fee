insert into bd_refinfo
  (code,
   dr,
   isneedpara,
   isspecialref,
   metadatatypename,
   modulename,
   name,
   para1,
   para2,
   para3,
   pk_refinfo,
   refclass,
   refsystem,
   reftype,--{0，表型}，{1，树型}，{2，树表}
   reserv1,
   reserv2,
   reserv3,
   resid,
   residpath,
   ts,
   wherepart)
values
  ('fee_workteam',
   0,
   null,
   null,
   null,
   'fee',
   '班组基本信息',
   null,
   null,
   null,
   '0001Z010fee_workteam',
   'nc.ui.fee.workteam.model.WorkteamRefModel',
   null,
   0,
   null,
   null,
   null,
   '班组基本信息',
   'ref',
   '2017-11-19 00:36:48',
   null);
