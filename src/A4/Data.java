
package A4;

import A3.DataType;

public class Data
{
	public Data(DataType type)
	{
		this.type = type;
		this.value = null;
	}

	public Data(DataType type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public DataType getDataType()
	{
		return this.type;
	}

	public String getValue()
	{
		return this.value;
	}

	private DataType type;
	private String value;
}
