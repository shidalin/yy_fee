package nc.pub.util;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.pubitf.initgroup.InitGroupQuery;
import nc.vo.fbm.pub.constant.FbmBusConstant;
import nc.vo.pub.BusinessException;

public class ModuleIsAbleUtil {

	/**
	 * �жϻ��ƽ̨�Ƿ�����
	 * 
	 * @param pk_group
	 * @throws BusinessException
	 */
	public static boolean isFIPEnable(String pk_group) throws BusinessException {
		return isEnable(pk_group, FbmBusConstant.OUTER_FUNCODE_FIP);
	}

	/**
	 * �ж������Ƿ�����
	 * 
	 * @param pk_group
	 * @throws BusinessException
	 */
	public static boolean isGLEnable(String pk_group) throws BusinessException {
		return isEnable(pk_group, FbmBusConstant.OUTER_FUNCODE_GL);
	}

	/**
	 * ��������ģ��API
	 * 
	 * @param pk_group
	 *            ����
	 * @param funcode
	 *            ���ܽڵ� ������Դ��dap_dapsystem
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
			throw new BusinessException("��ѯģ���Ƿ�����ʱ�쳣");
		}
	}

}
