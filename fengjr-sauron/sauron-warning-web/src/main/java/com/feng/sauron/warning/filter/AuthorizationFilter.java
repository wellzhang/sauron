package com.feng.sauron.warning.filter;

import com.fengjr.upm.filter.util.UserUtils;
import com.fengjr.upm.tools.Menu;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jian.zhang on 2016/6/8.
 */
public class AuthorizationFilter extends HandlerInterceptorAdapter {

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        try{
            if(
                //                    UserUtils.getUser().getId().equals("f4cd698d-d9bf-4797-97d5-91501d09f83d")
//                    || UserUtils.getUser().getId().equals("44bdd8be-d429-41a7-a185-98d70aa92aaa") ||
                    UserUtils.hasPermission("admin")){

                request.setAttribute("anthor","1");

            }
        }catch (Exception e){

        }
    }

}
