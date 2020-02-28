package greedyModularity;

import java.util.Objects;

public class MatrixEntry implements Comparable<MatrixEntry>{
	private static final String format = "(%.16e, r:%d, c:%d)";
	private double value;
	private int row;
	private int column;
	
	public MatrixEntry(double val, int row, int col) {
		this.value = val;
		this.row = row;
		this.column = col;
	}
	public double value() {return value;}
	public int row() {return row;}
	public int col() {return column;}
	public int compareTo(MatrixEntry t) {
		return value>t.value?1:value<t.value?-1:0;
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !MatrixEntry.class.isAssignableFrom(obj.getClass())) 
			return false;
		
		final MatrixEntry me = (MatrixEntry) obj;
		return compareTo(me)==0 && row==me.row && column==me.column;
	}
	public int hashCode() {
		return Objects.hash(value, row, column);
	}
	public String toString() {
		return String.format(format, value, row, column);
	}
}