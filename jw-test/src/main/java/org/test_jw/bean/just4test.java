package org.test_jw.bean;

import org.jw.annotation.persistence.Table;

/**
 * Created by CaiDongYu on 2016/4/19.
 */
@Table("just4test")
public class just4test {
    private long id;
    private String name;

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }
}
