package shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple Quickstart application showing how to use Shiro's API.
 *
 * @since 0.9 RC2
 */
public class Quickstart {

    private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);


    public static void main(String[] args) {

        // The easiest way to create a Shiro SecurityManager with configured
        // realms, users, roles and permissions is to use the simple INI config.
        // We'll do that by using a factory that can ingest a .ini file and
        // return a SecurityManager instance:

        // Use the shiro.ini file at the root of the classpath
        // (file: and url: prefixes load from files and urls respectively):
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        // for this simple example quickstart, make the SecurityManager
        // accessible as a JVM singleton.  Most applications wouldn't do this
        // and instead rely on their container configuration or web.xml for
        // webapps.  That is outside the scope of this simple quickstart, so
        // we'll just do the bare minimum so you can continue to get a feel
        // for things.
        SecurityUtils.setSecurityManager(securityManager);

        // Now that a simple Shiro environment is set up, let's see what you can do:

        // get the currently executing user:
        // 获取当前的subject
        Subject currentUser = SecurityUtils.getSubject();
        log.info("=============当前用户：" + currentUser);

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        // 测试使用session
        // 获取session
        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if (value.equals("aValue")) {
            log.info("==============================Retrieved the correct value! [" + value + "]===========================");
        }

        // let's login the current user so we can check against roles and permissions:
        // 测试当前用户是否已经被认证，即是否已经登录
        if (!currentUser.isAuthenticated()) {
        	// 把用户名和密码封装为UsernamePasswordToken对象
            UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
            // rememberme
            token.setRememberMe(true);
            try {
            	// ִ 执行登录
                currentUser.login(token);
            } 
            // 用户不存在异常 
            catch (UnknownAccountException uae) {
                log.info("========================There is no user with username of " + token.getPrincipal());
                return; 
            } 
            // 账户正确，密码不正确异常
            catch (IncorrectCredentialsException ice) {
                log.info("==========================Password for account " + token.getPrincipal() + " was incorrect!");
                return; 
            } 
            // 用户被锁定异常
            catch (LockedAccountException lae) {
                log.info("=======================The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            // 所有认证时异常
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
            }
        }

        //say who they are:
        //  打印登录成功
        log.info("----> User [" + currentUser.getPrincipal() + "] logged in successfully.");

        //test a role:
        // 验证是否有这个角色
        if (currentUser.hasRole("schwartz")) {
            log.info("========================= 有这个角色");
        } else {
            log.info("============================== 没有这个角色");
            return; 
        }

        //test a typed permission (not instance-level)
        // 测试用户是否具备某一个行为
        if (currentUser.isPermitted("lightsaber:weild")) {
            log.info("----> You may use a lightsaber ring.  Use it wisely.");
        } else {
            log.info("Sorry, lightsaber rings are for schwartz masters only.");
        }

        //a (very powerful) Instance Level permission:
        // 测试用户是否具备某一个行为
        if (currentUser.isPermitted("user:delete:zhangsan")) {
            log.info("----> You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  " +
                    "Here are the keys - have fun!");
        } else {
            log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }

        //all done - log out!
        // 查看是否是登录状态
        System.out.println("---->" + currentUser.isAuthenticated());
        // 执行登出
        currentUser.logout();
        // 验证是否登出
        System.out.println("---->" + currentUser.isAuthenticated());

        System.exit(0);
    }
}
