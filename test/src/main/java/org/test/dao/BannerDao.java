package org.test.dao;

import org.axe.annotation.persistence.Dao;
import org.axe.annotation.persistence.Sql;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.test.bean.Banner;

@Dao
public interface BannerDao {
	
	@Sql("select * from Banner")
	public Page<Banner> page(PageConfig pageConfig);
}
