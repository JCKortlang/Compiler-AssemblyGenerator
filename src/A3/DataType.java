
package A3;

/**
 * A simple enumerator that lists the data types available in our custom language. Each entry stores the index that can be used to reference
 * it in a semantic table.
 * 
 * @author Jan Christian Chavez-Kortlang
 *
 */
public enum DataType
{
	$int(0, "int"),
	$float(1, "float"),
	$char(2, "char"),
	$string(3, "string"),
	$boolean(4, "boolean"),
	$void(5, "void"),
	$error(6, "ERROR"),
	$OK(-1, "OK");

	private DataType(int index, String type)
	{
		this.index = index;
		this.type = type;
	}

	public int getIndex()
	{
		return index;
	}

	public String getType()
	{
		return this.type;
	}

	public boolean isEqual(String type)
	{
		if (this != $error)
		{
			return type.equalsIgnoreCase(this.type);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the DataType represented by the type string used during variable declaration. E.g. "int" returns DataType.$int.<br/>
	 * If no match is found it returns $error.
	 * 
	 * @param word
	 * @return DataType
	 */
	public static DataType getDataTypeFrom(String word)
	{
		word = word.toLowerCase();

		if (word.equals("int"))
		{
			return $int;
		}
		else if (word.equals("float"))
		{
			return $float;
		}
		else if (word.equals("boolean"))
		{
			return $boolean;
		}
		else if (word.equals("char"))
		{
			return $char;
		}
		else if (word.equals("string"))
		{
			return $string;
		}
		else if (word.equals("void"))
		{
			return $void;
		}
		else
		{
			return $error;
		}
	}

	/**
	 * Returns the DataType represented by the token string of an arbitrary value. E.g. "OCTAL" returns DataType.$int <br/>
	 * <b>Note:</b> This function is NOT case sensitive.
	 * 
	 * @param token
	 * @return DataType
	 * @see A2.Token
	 */
	public static DataType getDataTypeFromValue(String token)
	{
		token = token.toUpperCase();

		if (token.equals("INTEGER") || token.equals("OCTAL") || token.equals("HEXADECIMAL") || token.equals("BINARY"))
		{
			return $int;
		}
		else if (token.equals("FLOAT"))
		{
			return $float;
		}
		else if (token.equals("CHARACTER"))
		{
			return $char;
		}
		else if (token.equals("STRING"))
		{
			return $string;
		}
		else if (token.equals("TRUE") || token.equals("FALSE"))
		{
			return $boolean;
		}
		else
		{
			return $error;
		}
	}

	private int index;
	private String type;
}
