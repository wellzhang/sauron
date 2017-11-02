package com.feng.sauron.warning.service.base;

import com.feng.sauron.warning.dao.ContactMapper;
import com.feng.sauron.warning.domain.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Liuyb on 2015/12/9.
 */
@Service
public class ContactsService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ContactMapper contactMapper;
    @Transactional(readOnly = true)
    public Contact findContactById(long id){
        return contactMapper.selectByPrimaryKey(id);
    }
    @Transactional(readOnly = true)
    public List<Contact> getAllContactsInfo(){
        return contactMapper.selectAll();
    }
    @Transactional
    public void updateContact(Contact contact){
        contactMapper.updateByPrimaryKeySelective(contact);
    }
    @Transactional
    public int addContact(Contact contact){
        return contactMapper.insert(contact);
    }
    @Transactional
    public void deleteContact(long id){
        contactMapper.deleteByPrimaryKey(id);
    }

    public List<Contact> findContactByPager(int pageNo, int pageSize, long contactId, String name, String mobile) {
        pageNo = pageNo - 1 > 0 ? pageNo - 1 : 0;
        return contactMapper.selectByPager(pageNo * pageSize, pageSize, contactId, name, mobile);
    }

    public int findContactTotal(long contactId, String mobile, String wechat) {
        return contactMapper.selectCount(contactId, mobile, wechat);
    }
}
