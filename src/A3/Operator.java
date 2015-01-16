
package A3;

/**
 * A simple enumerator that store the operations supported in our custom language. Each entry also stores the index used to access the
 * operation table within the {@code Cube}
 * 
 * @author Jan Christian Chavez-Kortlang
 * @see Cube
 */
public enum Operator
{
	$sum(0),
	$difference(1),
	$product(1),
	$division(1),
	$unaryMinus(2),
	$comparison(3),
	$logical(4),
	$equality(5),
	$negation(6),
	$assignment(7);

	private Operator(int index)
	{
		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	/**
	 * Iterates through the Operator.values() and checks if the parameter String {@code isEqual} to any of them. If true, it returns the
	 * corresponding Operator. <br/>
	 * Note: Returns <b>null</b> if no matching token is found.
	 * 
	 * @param word
	 * @return Operator
	 */
	public static Operator getOperatorFrom(String word)
	{
		for (Operator operator : Operator.values())
		{
			if (operator.isEqual(word))
			{
				return operator;
			}
		}
		return null;
	}

	public boolean isEqual(String operator)
	{
		switch (this)
		{
			case $sum:
				return operator.equals("+");
			case $difference:
				return operator.equals("-");
			case $product:
				return operator.equals("*");
			case $division:
				return operator.equals("/");
			case $unaryMinus:
				return operator.equals("-");
			case $comparison:
				return operator.equals("<") || operator.equals(">");
			case $logical:
				return operator.equals("&") || operator.equals("|");
			case $equality:
				return operator.equals("!=") || operator.equals("==");
			case $negation:
				return operator.equals("!");
			case $assignment:
				return operator.equals("=");
			default:
				return false;
		}
	}

	private int index;
}
