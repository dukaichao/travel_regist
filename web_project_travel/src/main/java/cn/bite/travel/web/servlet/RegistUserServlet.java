package cn.bite.travel.web.servlet;

import cn.bite.travel.domain.ResultInfo;
import cn.bite.travel.domain.User;
import cn.bite.travel.service.UserService;
import cn.bite.travel.service.impl.UserServiceImpl;
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

@WebServlet("/registUserServlet")
public class RegistUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //1.校验验证码是否正确
        //1.1从前台获取验证码
        String check_code = request.getParameter("check");
        System.out.println(check_code);
        //1.2从session域中获取验证码
        String code = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        //1.3判断两者是否相等
        if(code == null ||  !check_code.equalsIgnoreCase(code)){
            //设置响应数
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码不正确");
            ObjectMapper objectMapper = new ObjectMapper();
            //创建json的解析对象 (jackson的jar包核心类)
            String json = objectMapper.writeValueAsString(resultInfo);
            //设置服务器响应格式 :json类型
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            //直接退出
            return;
        }
        //在后台验证填写信息是否正确
        //获取前台信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        //将其封装成User对象
        //2.封装User对象
        User user = new User() ;
        //使用commons-beanutils工具类 :封装Javabean
        try {
            BeanUtils.populate(user,parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用Service,查询是否存在该用户
        UserService userService = new UserServiceImpl() ;
        boolean flag = userService.regist(user) ;
        //创建响应结果对象:ResultInfo
        ResultInfo resultInfo = new ResultInfo() ;
        //判断用注册是否成功
        if(flag){
            //注册成功了
            resultInfo.setFlag(true);
        }else{
            //注册失败
            resultInfo.setFlag(false);
            //提示信息
            resultInfo.setErrorMsg("注册失败!!!");
        }
        //向前台返回信息
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(resultInfo);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request,response);

    }
}
