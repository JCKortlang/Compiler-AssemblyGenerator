
package A4;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import A3.SemanticAnalyzer;
import A3.SymbolTableItem;

/**
 * Generates a vector that contains strings of assembly code that represents compiled code.
 * 
 * @author Jan Christian
 *
 */
public class CodeGenerator
{
	/**
	 * Retrieves the Program Counter value corresponding to the <b>most recent</b> {@code Instruction} created.<br/>
	 * 
	 * @return int
	 */
	protected static int getPC()
	{
		return programCounter;
	}

	/**
	 * Generates a label for JMC and JMP instructions.
	 * 
	 * @return String
	 */
	protected static String createLabel()
	{
		String label = "#e" + ++labelCounter;
		return label;
	}

	/**
	 * Inserts a label into the inner Hashtable.
	 * 
	 * @param labelName
	 * @param value
	 */
	protected static void addLabel(String labelName, int value)
	{
		labels.put(labelName, value);
	}

	/**
	 * Adds an instruction to the appropriate vector using the enumerators defined in the A4 package.
	 * 
	 * @param instruction
	 * @param opCode
	 */
	protected static void addInstruction(Instruction instruction, OpCode opCode)
	{
		instructions.add(instruction.toString() + " " + opCode.getCode() + ", 0");
		programCounter++;
	}

	/**
	 * Adds an instruction to appropriate vector using the parameter strings. Used for JMP and JMC.
	 * 
	 * @param instruction
	 * @param p1
	 * @param p2
	 */
	protected static void addInstruction(String instruction, String p1, String p2)
	{
		instructions.add(instruction + " " + p1 + ", " + p2);
		programCounter++;
	}

	static void writeCode(Gui gui)
	{
		// Writes the SymbolTable
		Enumeration<String> symbolTableKeys = SemanticAnalyzer.getSymbolTable().keys();
		Enumeration<Vector<SymbolTableItem>> symbolTableElements = SemanticAnalyzer.getSymbolTable().elements();

		while (symbolTableKeys.hasMoreElements() && symbolTableElements.hasMoreElements())
		{
			String toWrite = symbolTableKeys.nextElement() + ", " + symbolTableElements.nextElement().get(0).toString();
			gui.writeCode(toWrite);
		}

		// Writes the Labels.
		Enumeration<String> labelKeys = labels.keys();
		Enumeration<Integer> labelValues = labels.elements();

		while (labelKeys.hasMoreElements() && labelValues.hasMoreElements())
		{
			String toWrite = labelKeys.nextElement() + ", LABEL, " + labelValues.nextElement();
			gui.writeCode(toWrite);
		}

		gui.writeCode("@");

		// Writes the Assembly Instructions.
		for (String instruction : instructions)
		{
			gui.writeCode(instruction);
		}

	}

	static void clear(Gui gui)
	{
		programCounter = 1;
		labelCounter = 0;

		SemanticAnalyzer.getSymbolTable().clear();
		instructions.clear();
		labels.clear();
	}

	private static final Vector<String> instructions = new Vector<>();
	private static final Hashtable<String, Integer> labels = new Hashtable<String, Integer>();
	private static int programCounter = 1;
	private static int labelCounter = 0;
}
