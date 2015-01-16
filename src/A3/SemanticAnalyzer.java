
package A3;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import A4.Gui;

import A4.Token;

/**
 * Contains static methods that are called by the Parser to verify the semantics of code.
 * 
 * @author Jan Christian Chavez-Kortlang
 *
 */
public class SemanticAnalyzer
{
	/**
	 * Initializes the internal Symbol Table and Stack. This should always be called before analyzing a new segment of code.
	 */
	public static void initialize()
	{
		symbolTable = new Hashtable<String, Vector<SymbolTableItem>>();
		stack = new Stack<DataType>();
	}

	public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable()
	{
		return symbolTable;
	}

	/**
	 * Checks that the Token parameter has not been previously declared and adds it to the symbol table. If the Token has been declared, it
	 * reports an error.
	 * 
	 * @param type
	 * @param currentToken
	 * @param gui
	 */
	public static void variableDeclared(String type, Token currentToken, Gui gui)
	{
		String variableID = currentToken.getWord();
		boolean exists = symbolTable.containsKey(variableID);

		if (!exists)
		{
			Vector<SymbolTableItem> vector = new Vector<SymbolTableItem>();
			vector.add(new SymbolTableItem(type, "GLOBAL", "0"));
			symbolTable.put(variableID, vector);
		}
		else
		{
			error(gui, DUPLICATE_VARIABLE, variableID, currentToken.getLine());
		}
	}

	/**
	 * Verifies the type matching of the variables present within the internal stack. Returns DataType.$OK or DataType.$error.
	 * 
	 * @param variableID
	 * @param lineNumber
	 * @param gui
	 * @return DataType
	 */
	public static DataType variableAssigned(String variableID, int lineNumber, Gui gui)
	{
		// We don't use operatorUsed(Operator) because it implicitly pops from the stack and we need the stack values after the cube
		// calculation.
		DataType valueType = stack.pop();
		DataType variableType = stack.pop();
		DataType result = cube.getSemanticResultOf(Operator.$assignment, variableType, valueType);
		
		
		if (result != DataType.$OK)
		{
			error(gui, TYPE_MISMATCH, lineNumber);
		}
		
		return result;
	}

	/**
	 * Determines the type of currentToken parameter and pushes it onto the internal SemanticAnalyzer stack. This function should be called
	 * whenever a variable is used in an EXPRESSION or ASSIGNMENT.<br/>
	 * <b>Note:</b> Should not be used during VARIABLE declaration.
	 * 
	 * @param currentToken
	 * @param gui
	 * @see A2.Parser
	 */
	public static void variableUsed(Token currentToken, Gui gui)
	{
		DataType type = SemanticAnalyzer.getDataTypeOf(currentToken, gui);
		SemanticAnalyzer.stack.push(type);
	}

	public static void operatorUsed(Operator operator)
	{
		if (operator == Operator.$unaryMinus || operator == Operator.$negation)
		{
			stack.push(cube.getSemanticResultOf(operator, stack.pop()));
		}
		else
		{
			stack.push(cube.getSemanticResultOf(operator, stack.pop(), stack.pop()));
		}
	}

	/**
	 * Pops the internal stack and reports an error if the value is not DataType.$boolean. <br/>
	 * <b>Note: </b>Should only be called <b>after</b> the conditional EXPRESSION within IF and WHILE.
	 * 
	 * @param lineNumber
	 * @param gui
	 * @see DataType
	 */
	public static void checkConditional(int lineNumber, Gui gui)
	{
		boolean isConditional = stack.pop() == DataType.$boolean;

		if (!isConditional)
		{
			error(gui, EXPECTED_BOOLEAN, lineNumber);
		}
	}

	/**
	 * Retrieves the DataType of an identifier or literal value represented by the Token parameter.
	 * 
	 * @param currentToken
	 * @param gui
	 * @return DataType
	 * @see A2.Token
	 */
	private static DataType getDataTypeOf(Token currentToken, Gui gui)
	{
		if (currentToken.getWord().equals("true") || currentToken.getWord().equals("false"))
		{
			return DataType.$boolean;
		}
		else if (currentToken.getToken().equalsIgnoreCase("identifier"))
		{
			String id = currentToken.getWord();
			boolean exists = symbolTable.containsKey(id);

			if (exists)
			{
				String type = symbolTable.get(id).get(0).getType();
				return DataType.getDataTypeFrom(type);
			}
			else
			{
				error(gui, UNDEFINED_VARIABLE, currentToken.getWord(), currentToken.getLine());
				return DataType.$error;
			}
		}
		// Literal value
		else
		{
			return DataType.getDataTypeFromValue(currentToken.getToken());
		}
	}

	private static void error(Gui gui, int errorCode, int lineNumber)
	{
		switch (errorCode)
		{
			case TYPE_MISMATCH:
				gui.writeConsole("Line <" + lineNumber + ">: Incompatible types: type mismatch.");
				break;
			case EXPECTED_BOOLEAN:
				gui.writeConsole("Line <" + lineNumber + ">: incompatible types: expected boolean.");
				break;
		}
	}

	private static void error(Gui gui, int errorCode, String variableID, int lineNumber)
	{
		switch (errorCode)
		{
			case DUPLICATE_VARIABLE:
				gui.writeConsole("Line <" + lineNumber + ">: Variable <" + variableID + "> is already defined.");
				break;
			case UNDEFINED_VARIABLE:
				gui.writeConsole("Line <" + lineNumber + ">: Variable <" + variableID + "> not found.");
			default:
				error(gui, errorCode, lineNumber);
		}
	}

	private static final Cube cube = new Cube();
	private static Hashtable<String, Vector<SymbolTableItem>> symbolTable;
	private static Stack<DataType> stack;
	
	//ERROR CODES
	private static final int DUPLICATE_VARIABLE = 1;
	private static final int TYPE_MISMATCH = 2;
	private static final int EXPECTED_BOOLEAN = 3;
	private static final int UNDEFINED_VARIABLE = 4;

}
