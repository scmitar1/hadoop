package writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class CountByIPTableWritable implements Writable, DBWritable{
	private String ip;
	private String MaxSrc;
	private String MaxDst;
	private int MaxSrcCount;
	private int MaxDstCount;
	
	public CountByIPTableWritable (){}
	
	public CountByIPTableWritable (String ip, String MaxSrc, String MaxDst,
			int MaxSrcCount, int MaxDstCount) {
		this.ip = ip;
		this.MaxSrc = MaxSrc;
		this.MaxDst = MaxDst;
		this.MaxSrcCount = MaxSrcCount;
		this.MaxDstCount = MaxDstCount;
	}
	
	@Override
	public void readFields(ResultSet re) throws SQLException {
		ip = re.getString("ip");
		MaxSrc = re.getString("MaxSrc");
		MaxDst = re.getString("MaxDst");
		MaxSrcCount = re.getInt("MaxSrcCount");
		MaxDstCount = re.getInt("MaxDstCount");
	}

	@Override
	public void write(PreparedStatement ps) throws SQLException {
		ps.setString(1, ip);
		ps.setString(2, MaxSrc);
		ps.setString(3, MaxDst);
		ps.setInt(4, MaxSrcCount);
		ps.setInt(5, MaxDstCount);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		ip = Text.readString(in);
		MaxSrc = Text.readString(in);
		MaxDst = Text.readString(in);
		MaxSrcCount = in.readInt();
		MaxDstCount = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Text.writeString(out, ip);
		Text.writeString(out, MaxSrc);
		Text.writeString(out, MaxDst);
		out.writeInt(MaxSrcCount);
		out.writeInt(MaxDstCount);
	}
}
