package clustering;

import java.util.Objects;

@Deprecated
public class Entry {
	private static final String format = "(%d, %d)";
	private int row;
	private int column;
	
	public Entry(int row, int col) {
		this.row = row;
		this.column = col;
	}
	public int row() {return row;}
	public int col() {return column;}
	
	public boolean equals(Object obj) {
		if(obj==null || !Entry.class.isAssignableFrom(obj.getClass()))
			return false;
		final Entry other = (Entry) obj;
		return row==other.row && column==other.column;
	}
	public int hashCode() {
		return Objects.hash(row,column);
	}
	public String toString() {
		return String.format(format, row, column);
	}
}
