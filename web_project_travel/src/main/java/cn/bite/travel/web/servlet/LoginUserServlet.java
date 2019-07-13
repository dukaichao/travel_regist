package cn.bite.travel.web.servlet;

import cn.bite.travel.dao.UserDao;
import cn.bite.travel.dao.impl.UserDaoImpl;
import cn.bite.travel.domain.ResultInfo;
import cn.bite.travel.domain.User;
import cn.bite.travel.service.impl.UserServiceImpl;
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
 * Date:2019/7/13
 * Time:12:02
 */
@WebServlet("/loginUserServlet")
public class LoginUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        //封装成User对象
        User user = new User();
        try {
            BeanUtils.populate(user,parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        User u = new UserServiceImpl().findUser(user);
        if(u == null){
            getBack(response,false,"账号或者密码不正确");
            return;
        }
        String check_code = (String) request.getSession().getAttribute("CHECKCODE_SERVER");




    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }

    public static void getBack(HttpServletResponse response,boolean flag,String s){
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setFlag(flag);
        resultInfo.setErrorMsg(s);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(resultInfo);
            //设置服务器响应格式 :json类型
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
