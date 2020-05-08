package clustering;

import java.util.Objects;

public class OptimizedEntry<N> {
	private static final String format = "(%d, %d)";
	private N row;
	private N column;
	
	public OptimizedEntry(N row, N col) {
		this.row = row;
		this.column = col;
	}
	public N row() {return row;}
	public N col() {return column;}
	
	public boolean equals(Object obj) {
		if(obj==null || !OptimizedEntry.class.isAssignableFrom(obj.getClass()))
			return false;
		final OptimizedEntry<N> other = (OptimizedEntry<N>) obj;
		return row.equals(other.row) && column.equals(other.column);
	}
	public int hashCode() {
		return Objects.hash(row,column);
	}
	public String toString() {
		return String.format(format, row, column);
	}
}