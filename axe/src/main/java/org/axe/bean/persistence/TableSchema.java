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
package org.axe.bean.persistence;

import java.util.List;

/**
 * 封装 Dao Entity类的结构描述
 * @author CaiDongyu on 2019/8/5.
 */
public class TableSchema {
	
	//字段结构
	public static final class ColumnSchema{
		//变量名
		private String fieldName;
		//类型
		private String fieldType;
		//描述
		private String comment;
		
		//字段名，驼峰处理后
		private String columnName;
		
		//是否主键
		private boolean primary=false;
		//主键是否自增
		private boolean primaryKeyAutoIncrement=false;
		//是否唯一
		private boolean unique=false;
		
		//字段结构
		private EntityFieldMethod columnSchema;
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public String getColumnName() {
			return columnName;
		}
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
		public String getFieldType() {
			return fieldType;
		}
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
		public boolean getPrimary() {
			return primary;
		}
		public void setPrimary(boolean isPrimary) {
			this.primary = isPrimary;
		}
		public boolean getPrimaryKeyAutoIncrement() {
			return primaryKeyAutoIncrement;
		}
		public void setPrimaryKeyAutoIncrement(boolean isPprimaryKeyAutoIncrement) {
			this.primaryKeyAutoIncrement = isPprimaryKeyAutoIncrement;
		}
		public boolean getUnique() {
			return unique;
		}
		public void setUnique(boolean isUnique) {
			this.unique = isUnique;
		}
		public EntityFieldMethod getColumnSchema() {
			return columnSchema;
		}
		public void setColumnSchema(EntityFieldMethod columnSchema) {
			this.columnSchema = columnSchema;
		}
	}
	
	
	//表名，如果是分表，不能从这取，这只是初始定义
	private String tableName;
	//表备注
	private String tableComment;
	//是否自动创建
	private boolean autoCreate=false;
	//是否是分片存储
	private boolean sharding=false;
	//数据源名称
	private String dataSourceName;
	
	
	//对应数据类
	private Class<?> entityClass;
	//映射的字段
	private List<ColumnSchema> mappingColumnList;
	
	//主键约束
	private String idColumns;
	//唯一键约束
	private String uniqueColumns;
	
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableComment() {
		return tableComment;
	}
	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}
	public boolean getAutoCreate() {
		return autoCreate;
	}
	public void setAutoCreate(boolean isAutoCreate) {
		this.autoCreate = isAutoCreate;
	}
	public boolean getSharding() {
		return sharding;
	}
	public void setSharding(boolean isSharding) {
		this.sharding = isSharding;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public Class<?> getEntityClass() {
		return entityClass;
	}
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	public List<ColumnSchema> getMappingColumnList() {
		return mappingColumnList;
	}
	public void setMappingColumnList(List<ColumnSchema> mappingColumnList) {
		this.mappingColumnList = mappingColumnList;
	}
	public String getIdColumns() {
		return idColumns;
	}
	public void setIdColumns(String idColumns) {
		this.idColumns = idColumns;
	}
	public String getUniqueColumns() {
		return uniqueColumns;
	}
	public void setUniqueColumns(String uniqueColumns) {
		this.uniqueColumns = uniqueColumns;
	}
	
}
