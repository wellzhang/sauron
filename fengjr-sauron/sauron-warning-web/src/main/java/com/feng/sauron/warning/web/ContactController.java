package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.Contact;
import com.feng.sauron.warning.service.base.ContactsService;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.web.base.BaseController;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Liuyb on 2015/12/9.
 */
@RequestMapping("/contact")
public class ContactController extends BaseController {
    @Autowired
    ContactsService service;

    @RequestMapping(value = "/list/{pageNo}/{pageSize}")
    public String listContact(HttpServletRequest request,@PathVariable("pageNo") int pageNo,
                              @PathVariable("pageSize") int pageSize,
                              Model model) {


        Page<Contact> pageData = new Page<Contact>();
        String mobile = request.getParameter("mobile");
        String wechat = request.getParameter("wechat");
        String _id = request.getParameter("id");
        if (StringUtils.isEmpty(_id)) {
            _id = "0";
        }
        long id = Long.parseLong(_id);
        pageData.setDataList(service.findContactByPager(pageNo, pageSize, id, mobile, wechat));
        pageData.setPageSize(pageSize);
        pageData.setPageNO(pageNo);
        pageData.setTotal(service.findContactTotal(id, mobile, wechat));
        model.addAttribute("pageData", pageData);
        return "contact/contactListNew";
    }

    @RequestMapping(value = "/query")
    public String queryContact(HttpServletRequest request,
                               Model model) {

        String _id = request.getParameter("id");
        if (StringUtils.isEmpty(_id)) {
            _id = "0";
        }
        long id = Long.parseLong(_id);
        String mobile = request.getParameter("mobile");
        String wechat = request.getParameter("wechat");
        Page<Contact> pageData = new Page<Contact>();
        pageData.setDataList(service.findContactByPager(1, 10, id, mobile, wechat));
        pageData.setPageSize(10);
        pageData.setPageNO(1);
        pageData.setTotal(service.findContactTotal(id, mobile, wechat));
        model.addAttribute("pageData", pageData);
        return "contact/contactListNew";
    }

    @ResponseBody
    @RequestMapping(value = "/add/{name}/{mobile}/{wechat}/{role}/{status}/{email}")
    public ResponseDTO<Contact> addContact(@PathVariable("name") String name,
                                           @PathVariable("mobile") String mobile,
                                           @PathVariable("wechat") String wechat,
                                           @PathVariable("role") int role,
                                           @PathVariable("status") int status,
                                           @PathVariable("email") String email) {
        ResponseDTO<Contact> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        Contact contact = new Contact();
        contact.setName(name);
        contact.setMobile(mobile);
        contact.setWechat(wechat);
        contact.setRole(role);
        contact.setStatus(status);
        contact.setEmail(email);
        try {
            service.addContact(contact);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;

    }

    @ResponseBody
    @RequestMapping(value = "/del/{id}")
    public ResponseDTO<Contact> delContact(@PathVariable("id") long id) {
        ResponseDTO<Contact> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            service.deleteContact(id);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
        }
        return responseDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
    public ResponseDTO<Contact> modifyContact(@PathVariable("id") long id) {
        ResponseDTO<Contact> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            responseDTO.setAttach(service.findContactById(id));
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/update/{id}/{name}/{mobile}/{wechat}/{role}/{email}", method = RequestMethod.GET)
    public ResponseDTO<Contact> modifyContact(@PathVariable("id") long id,
                                              @PathVariable("name") String name,
                                              @PathVariable("mobile") String mobile,
                                              @PathVariable("wechat") String wechat,
                                              @PathVariable("role") int role,
                                              @PathVariable("email") String email) {
        ResponseDTO<Contact> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            Contact contact = new Contact();
            contact.setId(id);
            contact.setName(name);
            contact.setMobile(mobile);
            contact.setWechat(wechat);
            contact.setRole(role);
            contact.setEmail(email);
            service.updateContact(contact);
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @Override
    protected UserService getUserService() {
        return null;
    }
}
