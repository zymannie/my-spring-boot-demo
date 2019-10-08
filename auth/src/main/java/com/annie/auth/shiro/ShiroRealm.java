package com.annie.auth.shiro;

import com.annie.db.dao.User;
import com.annie.db.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ShiroRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    @Autowired
    private UserService userService;

    /**
     * 登录认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        logger.info("-------登录认证-------");
        // 获取用户输入的用户名和密码
        String userName = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());

        // 通过用户名到数据库查询用户信息
        User user = userService.getUserInfo(userName);

        if (user == null)
            throw new UnknownAccountException("用户名或密码错误！");

        if (!password.equals(user.getPassword()))
            throw new IncorrectCredentialsException("用户名或密码错误！");

        return new SimpleAuthenticationInfo(user, password, getName());
    }


    /**
     * 权限认证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("-------权限认证-------");

        User user = (User) SecurityUtils.getSubject().getPrincipal();
//        String userName = user.getUsername();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户角色集
//        List<UserWithRole> roleList = userWithRoleService.selectRoleByUser(user);
//        Set<String> roleSet = roleList.stream().map(UserWithRole::getStrRoleId).collect(Collectors.toSet());
//        simpleAuthorizationInfo.setRoles(roleSet);

        // 获取用户权限集
//        List<Menu> permissionList = new ArrayList<>();
//        Iterator<String> iter = roleSet.iterator();
//        while(iter.hasNext()){
//            permissionList.addAll(roleWithMenuService.selectRoleWithMenus(Long.valueOf(iter.next())));
//        }
//
//        Set<String> permissionSet = permissionList.stream().map(Menu::getStrId).collect(Collectors.toSet());
//        simpleAuthorizationInfo.setStringPermissions(permissionSet);

        return simpleAuthorizationInfo;
    }


    /**
     * 清除权限缓存
     * 使用方法：在需要清除用户权限的地方注入 ShiroRealm,
     * 然后调用其clearCache方法。
     */
    public void clearCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }
}