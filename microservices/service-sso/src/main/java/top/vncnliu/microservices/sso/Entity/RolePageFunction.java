package top.vncnliu.microservices.sso.Entity;

import lombok.Data;

/**
 * User: liuyq
 * Date: 2018/6/20
 * Description:角色 菜单 映射
 */

@Data
public class RolePageFunction extends BaseEntity {

	private Long role_id;
	
	private Long page_function_id;

	/**
	 * 是否显示，控制用户拥有菜单权限，但前端一定展示
	 */
	private boolean show;

}