
package A3;

public class SymbolTableItem
{
	private String type;
	private String scope;
	private String value;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public SymbolTableItem(String type, String scope, String value)
	{
		this.type = type;
		this.scope = scope;
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		String output = "";
		output += this.type.isEmpty() ? "" : this.type + ", ";
		output += this.scope.isEmpty() ? "" : this.scope + ", ";
		output += this.value.isEmpty() ? "" : this.value;
		return output;
	}

}
