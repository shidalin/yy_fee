package nc.pub.util;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.pubitf.initgroup.InitGroupQuery;
import nc.vo.fbm.pub.constant.FbmBusConstant;
import nc.vo.pub.BusinessException;

public class ModuleIsAbleUtil {

	/**
	 * 判断会计平台是否启用
	 * 
	 * @param pk_group
	 * @throws BusinessException
	 */
	public static boolean isFIPEnable(String pk_group) throws BusinessException {
		return isEnable(pk_group, FbmBusConstant.OUTER_FUNCODE_FIP);
	}

	/**
	 * 判断总账是否启用
	 * 
	 * @param pk_group
	 * @throws BusinessException
	 */
	public static boolean isGLEnable(String pk_group) throws BusinessException {
		return isEnable(pk_group, FbmBusConstant.OUTER_FUNCODE_GL);
	}

	/**
	 * 调用启用模块API
	 * 
	 * @param pk_group
	 *            集团
	 * @param funcode
	 *            功能节点 数据来源于dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isEnable(String pk_group, String funcode)
			throws BusinessException {
		try {
			return InitGroupQuery.isEnabled(
					pk_group == null ? InvocationInfoProxy.getInstance()
							.getGroupId() : pk_group, funcode);
		} catch (BusinessException be) {
			Logger.error(be.getMessage(), be);
			throw new BusinessException("查询模块是否启用时异常");
		}
	}

}
