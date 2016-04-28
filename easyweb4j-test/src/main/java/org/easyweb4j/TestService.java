package org.easyweb4j;

import org.easyweb4j.annotation.Service;
import org.easyweb4j.annotation.Tns;
import org.easyweb4j.helper.DataBaseHelper;

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
