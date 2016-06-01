package org.axe.bean.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 封装 Dao Entity类的字段和Get Set方法
 * Created by CaiDongYu on 2016/5/5.
 */
public class EntityFieldMethod {
	private Field field;
	private Method method;
	public EntityFieldMethod(Field field, Method method) {
		this.field = field;
		this.method = method;
	}
	public Field getField() {
		return field;
	}
	public Method getMethod() {
		return method;
	}
}
