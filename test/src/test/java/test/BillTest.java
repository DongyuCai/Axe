package test;

import java.math.BigDecimal;

import org.axe.annotation.persistence.Id;
import org.axe.annotation.persistence.Table;

/**
 * 账单表
 * Created by CaiDongYu on 2016年7月18日 下午2:06:56.
 */
@Table("gas_charge_bill_test")
public class BillTest{
	private byte _byte1;
	private Byte _byte2;
	private short _short1;
	private Short _short2;
	private int _int1;
	private Integer _int2;
	@Id
	private long _long1;
	private Long _long2;
	private float _float1;
	private Float _float2;
	private double _double1;
	private Double _double2;
	private char _char1;
	private Character _char2;
	private boolean _boolean1;
	private Boolean _boolean2;
	
	private String string;
	private BigDecimal bigDecimal;
	private java.sql.Date sqlDate;
	private java.util.Date javaDate;
	
	private byte[] byteAry;

	public byte get_byte1() {
		return _byte1;
	}

	public void set_byte1(byte _byte1) {
		this._byte1 = _byte1;
	}

	public Byte get_byte2() {
		return _byte2;
	}

	public void set_byte2(Byte _byte2) {
		this._byte2 = _byte2;
	}

	public short get_short1() {
		return _short1;
	}

	public void set_short1(short _short1) {
		this._short1 = _short1;
	}

	public Short get_short2() {
		return _short2;
	}

	public void set_short2(Short _short2) {
		this._short2 = _short2;
	}

	public int get_int1() {
		return _int1;
	}

	public void set_int1(int _int1) {
		this._int1 = _int1;
	}

	public Integer get_int2() {
		return _int2;
	}

	public void set_int2(Integer _int2) {
		this._int2 = _int2;
	}

	public long get_long1() {
		return _long1;
	}

	public void set_long1(long _long1) {
		this._long1 = _long1;
	}

	public Long get_long2() {
		return _long2;
	}

	public void set_long2(Long _long2) {
		this._long2 = _long2;
	}

	public float get_float1() {
		return _float1;
	}

	public void set_float1(float _float1) {
		this._float1 = _float1;
	}

	public Float get_float2() {
		return _float2;
	}

	public void set_float2(Float _float2) {
		this._float2 = _float2;
	}

	public double get_double1() {
		return _double1;
	}

	public void set_double1(double _double1) {
		this._double1 = _double1;
	}

	public Double get_double2() {
		return _double2;
	}

	public void set_double2(Double _double2) {
		this._double2 = _double2;
	}

	public char get_char1() {
		return _char1;
	}

	public void set_char1(char _char1) {
		this._char1 = _char1;
	}

	public Character get_char2() {
		return _char2;
	}

	public void set_char2(Character _char2) {
		this._char2 = _char2;
	}

	public boolean is_boolean1() {
		return _boolean1;
	}

	public void set_boolean1(boolean _boolean1) {
		this._boolean1 = _boolean1;
	}

	public Boolean get_boolean2() {
		return _boolean2;
	}

	public void set_boolean2(Boolean _boolean2) {
		this._boolean2 = _boolean2;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

	public java.util.Date getJavaDate() {
		return javaDate;
	}

	public void setJavaDate(java.util.Date javaDate) {
		this.javaDate = javaDate;
	}

	public byte[] getByteAry() {
		return byteAry;
	}

	public void setByteAry(byte[] byteAry) {
		this.byteAry = byteAry;
	}
}
