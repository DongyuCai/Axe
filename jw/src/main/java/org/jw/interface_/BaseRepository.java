package org.jw.interface_;

/**
 * 基础Dao 接口，使用时候可选
 * 使用这个接口内的方法，Entity类必须要有@Id标注的字段
 * 可以没有@Table注解，但是类名和表名要一只，有@Table注解则无所谓
 * Dao可以不继承 这个接口，只需要有@Dao注解，DaoAspect也会加载
 * 继承此接口可以让Dao拥有一些基本的面向对象的Entity操作
 * Created by CaiDongYu on 2016/5/5.
 */
public interface BaseRepository {
	public int insertEntity(Object entity);

	/**
	 * 需要entity类有@Id字段
	 */
	public int deleteEntity(Object entity);

	/**
	 * 需要entity类有@Id字段
	 */
	public int updateEntity(Object entity);
	
	/**
	 * 需要entity类有@Id字段
	 */
	public <T> T getEntity(T entity);
	
	/**
	 * 保存entity
	 * 这是个 INSERT ... ON DUPLICATE KEY UPDATE 操作
	 * 如果entity有主键，并且主键已存在，执行update
	 * TODO:返回Entity，并且是查询出来值塞满的，包括Id
	 */
	public int saveEntity(Object entity);
	
}
