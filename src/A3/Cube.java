
package A3;

import java.util.Vector;

/**
 * The Cube is a vector of two dimensional arrays corresponding to the {@code Operator} enumerator. The tables are used to calculate the
 * semantic result between two {@code DataType}s corresponding to an operation represented by an {@code Operator} enumerator.
 * 
 * @author Jan Christian Chavez-Kortlang
 * @see Operator
 * @see DataType
 *
 */
public class Cube
{
	public Cube()
	{
		vector = new Vector<DataType[][]>(8);

		vector.add(Operator.$sum.getIndex(), SUM);
		vector.add(Operator.$difference.getIndex(), DIFFERENCE_PRODUCT_DIVISION);
		vector.add(Operator.$unaryMinus.getIndex(), UNARY_MINUS);
		vector.add(Operator.$comparison.getIndex(), COMPARISON);
		vector.add(Operator.$logical.getIndex(), LOGICAL);
		vector.add(Operator.$equality.getIndex(), EQUALITY);		
		vector.add(Operator.$negation.getIndex(), NEGATION);
		vector.add(Operator.$assignment.getIndex(), ASSIGNMENT);
	}

	/**
	 * Returns the semantic result of the operation between type1 and type2 parameters.
	 * 
	 * @param operationOn
	 * @param type1
	 * @param type2
	 * @return DataType
	 */
	public DataType getSemanticResultOf(Operator operationOn, DataType type1, DataType type2)
	{
		if (operationOn != Operator.$unaryMinus && operationOn != Operator.$negation)
		{
			return vector.get(operationOn.getIndex())[type1.getIndex()][type2.getIndex()];
		}
		else
		{
			return getSemanticResultOf(operationOn, type2);
		}
	}

	/**
	 * This function should be used to retrieve the semantic results of a Operator.$unaryMinus or Operator.$negation on the DataType
	 * parameter. Returns DataType.$error if used incorrectly.
	 * 
	 * @param operationOn
	 * @param type
	 * @return DataType
	 * @see DataType
	 */
	public DataType getSemanticResultOf(Operator operationOn, DataType type)
	{
		if (operationOn == Operator.$unaryMinus || operationOn == Operator.$negation)
		{
			return vector.get(operationOn.getIndex())[0][type.getIndex()];
		}
		else
		{
			return DataType.$error;
		}

	}

	private Vector<DataType[][]> vector;

	private static final DataType[][] SUM = {
	      { DataType.$int, DataType.$float, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$float, DataType.$float, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$string, DataType.$string, DataType.$string, DataType.$string, DataType.$string, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error } };

	private static final DataType[][] DIFFERENCE_PRODUCT_DIVISION = {
	      { DataType.$int, DataType.$float, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$float, DataType.$float, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$string, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error } };

	private static final DataType[][] UNARY_MINUS = { { DataType.$int, DataType.$float, DataType.$error, DataType.$error, DataType.$error,
	      DataType.$error, DataType.$error } };

	private static final DataType[][] COMPARISON = {
	      { DataType.$boolean, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$boolean, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error } };

	private static final DataType[][] EQUALITY = {
	      { DataType.$boolean, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$boolean, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$boolean, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$boolean, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error } };

	private static final DataType[][] LOGICAL = {
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$boolean, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error } };

	private static final DataType[][] NEGATION = { { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$boolean,
	      DataType.$error, DataType.$error } };

	private static final DataType[][] ASSIGNMENT = {
	      { DataType.$OK, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$OK, DataType.$OK, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$OK, DataType.$error, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$OK, DataType.$error, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$OK, DataType.$error, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$OK, DataType.$error },
	      { DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error, DataType.$error } };
}
