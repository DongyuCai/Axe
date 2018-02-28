/**
 * MIT License
 * 
 * Copyright (c) 2017 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.axe.interface_.persistence;

/**
 * 基础Dao 接口，使用时候可选
 * 使用这个接口内的方法，Entity类必须要有@Id标注的字段
 * 可以没有@Table注解，但是类名和表名要一只，有@Table注解则无所谓
 * Dao可以不继承 这个接口，只需要有@Dao注解，DaoAspect也会加载
 * 继承此接口可以让Dao拥有一些基本的面向对象的Entity操作
 * @author CaiDongyu on 2016/5/5.
 */
public interface BaseRepository {
	public <T> T insertEntity(T entity);

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
	 * TODO(OK):返回Entity，并且是查询出来值塞满的，包括Id
	 */
	public <T> T saveEntity(T entity);
	
}
