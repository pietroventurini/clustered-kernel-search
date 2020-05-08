package clustering;

import java.util.Objects;

public class OptimizedMatrixEntry<N> implements Comparable<OptimizedMatrixEntry<N>>{
	private static final String format = "(%.16e, r:%d, c:%d)";
	private double value;
	private N row;
	private N column;
	
	public OptimizedMatrixEntry(double val, N row, N col) {
		this.value = val;
		this.row = row;
		this.column = col;
	}
	public double value() {return value;}
	public N row() {return row;}
	public N col() {return column;}
	public int compareTo(OptimizedMatrixEntry<N> t) {
		return value>t.value?1:value<t.value?-1:0;
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !OptimizedMatrixEntry.class.isAssignableFrom(obj.getClass())) 
			return false;
		final OptimizedMatrixEntry<?> me = (OptimizedMatrixEntry<?>) obj;
		return value==me.value && row.equals(me.row) && column.equals(me.column);
	}
	public int hashCode() {
		return Objects.hash(value, row, column);
	}
	public String toString() {
		return String.format(format, value, row, column);
	}
}