package org.smart4j.chapter3;

import org.smart4j.framework.annotation.Service;
import org.smart4j.framework.annotation.Tns;
import org.smart4j.framework.helper.DataBaseHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/8.
 */
@Service
public class TestService {

    @Tns
    public void testTns(){
        Map<String,Object> fieldMap = new HashMap<String,Object>();
        fieldMap.put("name","test-tns");
        DataBaseHelper.insertEntity(just4test.class,fieldMap);
    }

    public void testNoTns(){
        Map<String,Object> fieldMap = new HashMap<String,Object>();
        fieldMap.put("name","test-no-tns");
        DataBaseHelper.insertEntity(just4test.class,fieldMap);
    }
}
