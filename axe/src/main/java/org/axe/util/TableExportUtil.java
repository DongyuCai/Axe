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
package org.axe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.axe.bean.persistence.TableSchema;
import org.axe.bean.persistence.TableSchema.ColumnSchema;
import org.axe.helper.persistence.TableHelper;

/**
 * Rest接口导出工具类
 * @author CaiDongyu on 2018年2月3日 上午10:00:55.
 */
public class TableExportUtil {

	private TableExportUtil() {}
	
	public final static class Table{
		private String name;
		private String packageName;
		private String comment;
		private String idFields;
		private String uniqueFields;
		
		private List<Column> columnList;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public List<Column> getColumnList() {
			return columnList;
		}
		public void setColumnList(List<Column> columnList) {
			this.columnList = columnList;
		}
		public String getIdFields() {
			return idFields;
		}
		public void setIdFields(String idFields) {
			this.idFields = idFields;
		}
		public String getUniqueFields() {
			return uniqueFields;
		}
		public void setUniqueFields(String uniqueFields) {
			this.uniqueFields = uniqueFields;
		}
	}
	
	public final static class Column{
		private String name;
		private String type;
		private String comment;
		private boolean primary;
		private boolean autoIncreament;
		private boolean unique;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public boolean getPrimary() {
			return primary;
		}
		public void setPrimary(boolean primary) {
			this.primary = primary;
		}
		public boolean getAutoIncreament() {
			return autoIncreament;
		}
		public void setAutoIncreament(boolean autoIncreament) {
			this.autoIncreament = autoIncreament;
		}
		public boolean getUnique() {
			return unique;
		}
		public void setUnique(boolean unique) {
			this.unique = unique;
		}
	}
	
	public static List<Table> exportTableList(){
		Map<String, TableSchema> map = TableHelper.getEntityTableMap();
		List<Table> list = new ArrayList<>();
		for(String key:map.keySet()){
			TableSchema ts = map.get(key);
			
			Table table = new Table();
			table.setName(key);
			table.setPackageName(ts.getEntityClass().getPackage().getName());
			table.setComment(ts.getTableComment());
			table.setIdFields(ts.getIdFields());
			table.setUniqueFields(ts.getUniqueFields());
			
			List<ColumnSchema> csList = ts.getMappingColumnList();
			List<Column> cList = new ArrayList<>();
			for(ColumnSchema cs:csList){
				Column c = new Column();
				c.setName(cs.getFieldName());
				c.setType(cs.getFieldType());
				c.setComment(cs.getComment());
				c.setPrimary(cs.getPrimary());
				c.setAutoIncreament(cs.getPrimaryKeyAutoIncrement());
				c.setUnique(cs.getUnique());
				cList.add(c);
			}
			table.setColumnList(cList);
			
			list.add(table);
		}
		return list;
	}
	
}
