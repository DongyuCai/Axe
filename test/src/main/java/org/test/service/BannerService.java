package org.test.service;

import org.axe.annotation.ioc.Autowired;
import org.axe.annotation.ioc.Service;
import org.axe.bean.persistence.Page;
import org.axe.bean.persistence.PageConfig;
import org.axe.hoke.annotation.Hoke;
import org.test.bean.Banner;
import org.test.dao.BannerDao;
@Hoke
@Service
public class BannerService {

	@Autowired
	private BannerDao bannerDao;
	
//	@HokeConfig
	public Page<Banner> page(int pageNum){
		return bannerDao.page(new PageConfig(pageNum, 1));
	}
}
