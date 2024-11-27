package clover.servlet;

import clover.pojo.Poet;
import clover.util.BeanUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.util.Enumeration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PoetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            // 从请求中获取参数并存储到Map中
            Map<String, String> params = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                params.put(paramName, paramValue);
            }

            // 使用BeanUtil.convert方法将请求参数转换并填充到Poet对象中
            // 测试了convert方法
            Poet poetFromConvert = BeanUtil.convert(Poet.class, params);
            out.println("Poet object from convert: " + poetFromConvert.toString() + "<br>");

            // 使用BeanUtil.populate方法将Map集合中的数据注入到JavaBean的属性中去
            // 测试了populate方法
            Poet poetFromPopulate = new Poet();
            BeanUtil.populate(poetFromPopulate, params);
            out.println("Poet object from populate: " + poetFromPopulate.toString() + "<br>");

            // 使用BeanUtil.setProperty方法为指定bean实例的属性设值
            // 测试了setProperty方法
            Poet poetFromSetProperty = new Poet();
            BeanUtil.setProperty(poetFromSetProperty, "name", "Du Fu");
            out.println("Poet object from setProperty: " + poetFromSetProperty.toString() + "<br>");

            // 使用BeanUtil.copyProperties方法实现对象之间的拷贝
            // 测试了copyProperties方法
            Poet poetFromCopyProperties = new Poet();
            BeanUtil.copyProperties(poetFromCopyProperties, poetFromPopulate);
            out.println("Poet object from copyProperties: " + poetFromCopyProperties.toString() + "<br>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}