DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ10000000008U3R';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'FE02' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'FE02';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ10000000008U3R';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ10000000008U3S';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ10000000008U3S';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000008U3T';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000008U3U';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000008U3V';