package cn.bite.travel.web.servlet;

import cn.bite.travel.dao.impl.UserDaoImpl;
import cn.bite.travel.domain.ResultInfo;
import cn.bite.travel.domain.User;
import cn.bite.travel.service.UserService;
import cn.bite.travel.service.impl.UserServiceImpl;
import cn.bite.travel.util.JWriteBackUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created With IntelliJ IDEA.
 * Descriptions:
 * User:Mr.Du
 * Date:2019/7/14
 * Time:20:34
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    //注册
    public static void registUser(HttpServletRequest request, HttpServletResponse response) {
        //1.校验验证码是否正确
        //1.1从前台获取验证码
        String check_code = request.getParameter("check");
        //1.2从session域中获取验证码
        String code = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        //1.3判断两者是否相等
        if (code == null || !check_code.equalsIgnoreCase(code)) {
            //设置响应数
            JWriteBackUtils.getBack(response, false, "验证码不正确");
            //直接退出
            return;
        }
        //在后台验证填写信息是否正确
        //获取前台信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        //将其封装成User对象
        //2.封装User对象
        User user = new User();
        //使用commons-beanutils工具类 :封装Javabean
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用Service,查询是否存在该用户
        UserService userService = new UserServiceImpl();
        boolean flag = userService.regist(user);
        //判断用注册是否成功
        if (flag) {
            //注册成功了
            JWriteBackUtils.getBack(response, flag, "");
        } else {
            //注册失败
            JWriteBackUtils.getBack(response, flag, "注册失败");
        }

    }

    //登录
    public static void loginUser(HttpServletRequest request, HttpServletResponse response) {
        //1.校验验证码是否正确
        String check_code = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        String check = request.getParameter("check");
        if (!check.equalsIgnoreCase(check_code)) {
            JWriteBackUtils.getBack(response, false, "验证码不正确");
            return;
        }
        //2.从前端获取数据,封装成Map集合
        Map<String, String[]> parameterMap = request.getParameterMap();
        //利用BeanUtils将获取的键值对封装成User对象
        User user = new User();
        try {
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.在数据库中查找用户名和密码是否存在
        User u = new UserServiceImpl().findUser(user);
        //3.1如果不存在，提示对应错误
        if (u == null) {
            JWriteBackUtils.getBack(response, false, "账号或者密码不正确");
            return;
        }
        //3.2判断是否激活，没有激活提示激活
        if (!"Y".equals(u.getStatus())) {
            JWriteBackUtils.getBack(response, false, "账号尚未激活,请登录注册时的邮箱:" + u.getEmail() + "进行确认注册");
            return;
        }
        //4.登录成功，将信息保存到session域中去
        request.getSession().setAttribute("user", u);
        JWriteBackUtils.getBack(response, true, "");
    }

    //获取登录的用户，在登录成功的界面显示用户名
    public static void findUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.直接从session域中获取用户对象
        Object user = request.getSession().getAttribute("user");
        //判断用户是否为空
        //服务器将user对象响应给前台页面:header.html(通过index.html)
        ObjectMapper mapper = new ObjectMapper();
        //支持json格式的响应
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(), user);
    }

    //用户退出
    public static void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取session对象,手动删除sesison
        request.getSession().invalidate();
        //重定向到login.html
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    //激活邮件
    public static void active(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取code
        String code = request.getParameter("code");
        if (code != null) {
            UserService userService = new UserServiceImpl();
            //通过code查询用户
            User user = userService.active(code);
            String msg = "";
            if (user != null) {
                //激活成功了
                msg = "您已经激活成功了,请<a href='login.html'>登录</a>";
                //更新用户状态码
                new UserDaoImpl().updateStatus(user);
            } else {
                //激活失败
                msg = "激活失败,请联系管理人员";
            }
            //设置中文乱码
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }


    //test
    public static void test(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("test 方法执行了");
    }
}
