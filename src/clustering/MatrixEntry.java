package clustering;

import java.util.Objects;

public class MatrixEntry<N> implements Comparable<MatrixEntry<N>>{
	private static final String format = "(%.16e, r:%s, c:%s)";
	private double value;
	private N row;
	private N column;
	
	public MatrixEntry(double val, N row, N col) {
		this.value = val;
		this.row = row;
		this.column = col;
	}
	public double value() {return value;}
	public N row() {return row;}
	public N col() {return column;}
	public int compareTo(MatrixEntry<N> t) {
		return value>t.value?1:value<t.value?-1:0;
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !MatrixEntry.class.isAssignableFrom(obj.getClass())) 
			return false;
		final MatrixEntry<?> me = (MatrixEntry<?>) obj;
		return value==me.value && row.equals(me.row) && column.equals(me.column);
	}
	public int hashCode() {
		return Objects.hash(value, row, column);
	}
	public String toString() {
		return String.format(format, value, row, column);
	}
}