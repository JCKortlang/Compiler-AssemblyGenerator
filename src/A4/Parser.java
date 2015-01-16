
package A4;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import A3.DataType;
import A4.Gui;
import A3.Operator;
import A3.SemanticAnalyzer;
import A4.Token;

/**
 * Contains only static variables and functions.
 * 
 * @author Jan Christian Chavez-Kortlang
 */
public class Parser
{

	private static DefaultMutableTreeNode root;
	private static Vector<Token> tokens;
	private static int currentToken;
	private static Gui gui;

	public static DefaultMutableTreeNode run(Vector<Token> tokenVector, Gui gui)
	{
		SemanticAnalyzer.initialize();
		CodeGenerator.clear(gui);

		Parser.gui = gui;
		Parser.tokens = tokenVector;
		Parser.currentToken = 0;

		Parser.root = new DefaultMutableTreeNode("PROGRAM");

		if (!currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly);
			addNode(ParserError.OpeningCurly, root);
		}

		while (hasTokens() && !(currentTokenStarts(Rule.Program) || currentTokenFollows(Rule.Program)))
		{
			currentToken++;
		}

		if (currentTokenStarts(Rule.Program))
		{
			rule_PROGRAM(root);
		}
		
		
		// SEMANTIC ANALYSIS
		gui.writeSymbolTable(SemanticAnalyzer.getSymbolTable());

		// CODE GENERATOR
		CodeGenerator.addInstruction(Instruction.OPR, OpCode.Exit);
		CodeGenerator.writeCode(gui);

		return root;
	}

	private static boolean rule_PROGRAM(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		addNode(parent);
		currentToken++;

		if (hasTokens() && !(currentTokenStarts(Rule.Body) || currentTokenFollows(Rule.Body)))
		{
			error(ParserError.ID_Keyword);
			addNode(ParserError.ID_Keyword.getError(), parent);
		}

		while (hasTokens() && !(currentTokenStarts(Rule.Body) || currentTokenFollows(Rule.Body)))
		{
			currentToken++;
		}

		if (currentTokenStarts(Rule.Body))
		{
			node = addNode("BODY", parent);
			lineError = rule_BODY(node);
		}

		if (currentTokenFollows(Rule.Body))
		{
			addNode(parent);
			currentToken++;
		}
		else
		{
			error(ParserError.ClosingCurly);
			addNode(ParserError.ClosingCurly, parent);
		}

		return lineError;
	}

	private static boolean rule_BODY(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;
		
		//EXTRA CREDIT - SWITCH
		if (currentTokenStarts(Rule.Switch))
		{
			node = addNode("SWITCH", parent);
			lineError = rule_SWITCH(node);
		}
		else if (hasTokens() && currentTokenStarts(Rule.Assignment))
		{
			node = addNode("ASSIGNMENT", parent);
			lineError = rule_ASSIGNMENT(node);

			if (!lineError && currentTokenFollows(Rule.Assignment))
			{
				addNode(parent);
				currentToken++;
				// We do not update line error because line changes are valid at
				// this point.
			}
			else if (!lineError)
			{
				error(ParserError.SemiColon);
				addNode(ParserError.SemiColon, parent);
			}
			else if (lineError)
			{
				error(ParserError.SemiColon, currentToken - 1);
				addNode(ParserError.SemiColon, parent);
			}
		}
		else if (hasTokens() && currentTokenStarts(Rule.Variable))
		{
			node = addNode("VARIABLE", parent);
			lineError = rule_VARIABLE(node);

			if (!lineError && currentTokenFollows(Rule.Variable))
			{
				addNode(parent);
				currentToken++;
				// We do not update line error because line changes are valid at
				// this point.
			}
			else if (!lineError)
			{
				error(ParserError.SemiColon);
				addNode(ParserError.SemiColon, parent);
			}
			else if (lineError)
			{
				error(ParserError.SemiColon, currentToken - 1);
				addNode(ParserError.SemiColon, parent);
			}
		}
		else if (hasTokens() && currentTokenStarts(Rule.While))
		{
			node = addNode("WHILE", parent);
			lineError = rule_WHILE(node);
		}
		else if (hasTokens() && currentTokenStarts(Rule.If))
		{
			node = addNode("IF", parent);
			lineError = rule_IF(node);
		}
		else if (hasTokens() && currentTokenStarts(Rule.Return))
		{
			node = addNode("RETURN", parent);
			lineError = rule_RETURN(node);

			if (!lineError && currentTokenFollows(Rule.Return))
			{
				addNode(parent);
				currentToken++;
				// We do not update line error because line changes are valid at
				// this point.
			}
			else if (!lineError)
			{
				error(ParserError.SemiColon);
				addNode(ParserError.SemiColon, parent);
			}
			else if (lineError)
			{
				error(ParserError.SemiColon, currentToken - 1);
				addNode(ParserError.SemiColon, parent);
			}
		}
		else if (hasTokens() && currentTokenStarts(Rule.Print))
		{
			node = addNode("PRINT", parent);
			lineError = rule_PRINT(node);

			if (!lineError && currentTokenFollows(Rule.Print))
			{
				addNode(parent);
				currentToken++;
				// We do not update line error because line changes are valid at
				// this point.
			}
			else if (!lineError)
			{
				error(ParserError.SemiColon);
				addNode(ParserError.SemiColon, parent);
			}
			else if (lineError)
			{
				error(ParserError.SemiColon, currentToken - 1);
				addNode(ParserError.SemiColon, parent);
			}
		}

		if (hasTokens() && !(currentTokenStarts(Rule.Body) || currentTokenFollows(Rule.Body)))
		{
			error(ParserError.ID_Keyword);
			addNode(ParserError.ID_Keyword.getError(), parent);
		}

		while (hasTokens() && !(currentTokenStarts(Rule.Body) || currentTokenFollows(Rule.Body)))
		{
			currentToken++;
		}

		if (currentTokenStarts(Rule.Body))
		{
			node = addNode("BODY", parent);
			lineError = rule_BODY(node);
		}

		return lineError;
	}

	private static boolean rule_ASSIGNMENT(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		String variableID = tokens.get(currentToken).getWord();
		
		//SEMANTIC ANALYSIS
		SemanticAnalyzer.variableUsed(tokens.get(currentToken), gui);

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();

		if (!lineError && wordEquals("="))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}
		else if (!lineError)
		{
			error(ParserError.Equals);
			addNode(ParserError.Equals, parent);
		}
		else if (lineError)
		{
			error(ParserError.Equals, currentToken - 1);
			addNode(ParserError.Equals, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Expression))
		{
			error(ParserError.ID_Value_OpenParenthesis);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ID_Value_OpenParenthesis, currentToken - 1);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}

		while (!lineError && hasTokens() && !(currentTokenStarts(Rule.Expression) || currentTokenFollows(Rule.Assignment)))
		{
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}

		if (!lineError && currentTokenStarts(Rule.Expression))
		{
			int line = tokens.get(currentToken).getLine();

			node = addNode("EXPRESSION", parent);
			lineError = rule_EXPRESSION(node);
			
			//SEMANTIC ANALYSIS
			SemanticAnalyzer.variableAssigned(variableID, line, gui);

			CodeGenerator.addInstruction(Instruction.STO.toString(), variableID, "0");
		}

		return lineError;
	}

	private static boolean rule_VARIABLE(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();
		
		if (!lineError && tokenEquals("IDENTIFIER"))
		{
			//SEMANTIC ANALYSIS
			SemanticAnalyzer.variableDeclared(tokens.get(currentToken - 1).getWord(), tokens.get(currentToken), gui);

			addNode(parent);
			currentToken++;
			lineError = currentTokenIsOnDifferentLine();
		}
		else if (!lineError)
		{
			error(ParserError.ID);
			addNode(ParserError.ID, parent);
		}
		else if (lineError)
		{
			error(ParserError.ID, currentToken - 1);
			addNode(ParserError.ID, parent);
		}

		return lineError;
	}

	private static boolean rule_WHILE(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		String skipBodyLabel = null;
		int preConditionalPC = -1;

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();

		if (!lineError && wordEquals("("))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}
		else if (!lineError)
		{
			error(ParserError.OpeningParenthesis);
			addNode(ParserError.OpeningParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.OpeningParenthesis, currentToken - 1);
			addNode(ParserError.OpeningParenthesis, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Expression))
		{
			error(ParserError.ID_Value_OpenParenthesis);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ID_Value_OpenParenthesis, currentToken - 1);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}

		while (!lineError && hasTokens()
		      && !(currentTokenStarts(Rule.Expression) || currentTokenFollows(Rule.Expression) || currentTokenFollows(Rule.While)))
		{
			currentToken++;
			lineError = currentTokenIsOnDifferentLine();
		}

		if (!lineError && currentTokenStarts(Rule.Expression))
		{
			// CODE GENERATOR - We want to jump back here so we store the PC
			preConditionalPC = CodeGenerator.getPC();

			node = addNode("EXPRESSION", parent);
			lineError = rule_EXPRESSION(node);
			
			//SEMANTIC ANALYSIS
			SemanticAnalyzer.checkConditional(tokens.get(currentToken).getLine(), gui);

			// CODE GENERATOR - We want to jump over the following code if the above expression was false. Note that the PC value to jump to is
			// currently unknown.
			skipBodyLabel = CodeGenerator.createLabel();
			CodeGenerator.addInstruction(Instruction.JMC.toString(), skipBodyLabel, "false");
		}

		if (!lineError && wordEquals(")"))
		{
			node = addNode(parent);
			currentToken++;
			// We do not update lineError because curly braces can occur on different lines.
		}
		else if (!lineError)
		{
			error(ParserError.ClosingParenthesis);
			addNode(ParserError.ClosingParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ClosingParenthesis, currentToken - 1);
			addNode(ParserError.ClosingParenthesis, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly);
			addNode(ParserError.OpeningCurly, parent);
		}
		else if (lineError && !currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly, currentToken - 1);
			addNode(ParserError.OpeningCurly, parent);
		}

		while (!lineError && hasTokens()
		      && !(currentTokenStarts(Rule.Program) || currentTokenStarts(Rule.Program) || currentTokenFollows(Rule.While)))
		{
			currentToken++;
			// We do not update lineError because curly braces can occur on different lines.
		}

		if (!lineError && currentTokenStarts(Rule.Program))
		{
			node = addNode("PROGRAM", parent);
			lineError = rule_PROGRAM(node);

			// CODE GENERATOR - The program should jump back at this point.
			String jumpToPreConditionalLabel = CodeGenerator.createLabel();
			CodeGenerator.addLabel(jumpToPreConditionalLabel, preConditionalPC);
			CodeGenerator.addInstruction(Instruction.JMP.toString(), jumpToPreConditionalLabel, "0");
		}

		// CODE GENERATOR - We now know where the skipBodyLabel PC value so we update it.
		CodeGenerator.addLabel(skipBodyLabel, CodeGenerator.getPC());

		return lineError;
	}

	private static boolean rule_IF(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		// CODE GENERATOR
		String skipIfBodyLabel = null;
		String skipElseBodyLabel = null;

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();

		if (!lineError && wordEquals("("))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}
		else if (!lineError)
		{
			error(ParserError.OpeningParenthesis);
			addNode(ParserError.OpeningParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.OpeningParenthesis, currentToken - 1);
			addNode(ParserError.OpeningParenthesis, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Expression))
		{
			error(ParserError.ID_Value_OpenParenthesis);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ID_Value_OpenParenthesis, currentToken - 1);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}

		while (!lineError && hasTokens()
		      && !(currentTokenStarts(Rule.Expression) || currentTokenFollows(Rule.Expression) || currentTokenFollows(Rule.If)))
		{
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}

		if (!lineError && currentTokenStarts(Rule.Expression))
		{

			node = addNode("EXPRESSION", parent);
			lineError = rule_EXPRESSION(node);

			//SEMANTIC ANALYSIS
			SemanticAnalyzer.checkConditional(tokens.get(currentToken).getLine(), gui);

			// CODE GENERATOR - We create the skipIfBodyLabel but we do not know where it will jump to at this point.
			skipIfBodyLabel = CodeGenerator.createLabel();
			CodeGenerator.addInstruction(Instruction.JMC.toString(), skipIfBodyLabel, "false");

		}

		if (!lineError && wordEquals(")"))
		{
			node = addNode(parent);
			currentToken++;
			// We do not update lineError because curly braces can occur on different lines.
		}
		else if (!lineError)
		{
			error(ParserError.ClosingParenthesis);
			addNode(ParserError.ClosingParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ClosingParenthesis, currentToken - 1);
			addNode(ParserError.ClosingParenthesis, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly);
			addNode(ParserError.OpeningCurly, parent);
		}
		else if (lineError && !currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly, currentToken - 1);
			addNode(ParserError.OpeningCurly, parent);
		}

		while (!lineError && hasTokens()
		      && !(currentTokenStarts(Rule.Program) || currentTokenFollows(Rule.Program) || currentTokenFollows(Rule.If)))
		{
			currentToken++;
			// We do not update lineError because curly braces can occur on different lines.
		}

		if (!lineError && currentTokenStarts(Rule.Program))
		{
			node = addNode("PROGRAM", parent);
			lineError = rule_PROGRAM(node);

			// CODE GENERATOR - We create the exitLabel but we do not know the correct jump location.
			skipElseBodyLabel = CodeGenerator.createLabel();
			CodeGenerator.addInstruction(Instruction.JMP.toString(), skipElseBodyLabel, "0");
		}

		// CODE GENERATOR - We now know the correct PC value for skipIfBodyLabel so we update it.
		CodeGenerator.addLabel(skipIfBodyLabel, CodeGenerator.getPC());

		// "else" can occur on the same or different line and is optional.
		if (!lineError && wordEquals("else"))
		{
			addNode(parent);
			currentToken++;
			// We do not update lineError because curly braces can occur on
			// different lines.

			if (!currentTokenStarts(Rule.Program))
			{
				error(ParserError.OpeningCurly);
				addNode(ParserError.OpeningCurly, parent);
			}

			while (hasTokens() && !(currentTokenStarts(Rule.Program) || currentTokenFollows(Rule.Program) || currentTokenFollows(Rule.If)))
			{
				currentToken++;
				// We do not update lineError because curly braces can occur on different lines.
			}

			if (currentTokenStarts(Rule.Program))
			{
				node = addNode("PROGRAM", parent);
				lineError = rule_PROGRAM(node);
			}
		}

		// CODE GENERATOR - We now know the correct exit location of skipElseBodyLabel so we update it.
		CodeGenerator.addLabel(skipElseBodyLabel, CodeGenerator.getPC());

		return lineError;
	}

	private static boolean rule_PRINT(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();

		if (!lineError && wordEquals("("))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

		}
		else if (!lineError)
		{
			error(ParserError.OpeningParenthesis);
			addNode(ParserError.OpeningParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.OpeningParenthesis, currentToken - 1);
			addNode(ParserError.OpeningParenthesis, parent);
		}

		if (!lineError && !currentTokenStarts(Rule.Expression))
		{
			error(ParserError.ID_Value_OpenParenthesis);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ID_Value_OpenParenthesis, currentToken - 1);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}

		while (!lineError && hasTokens()
		      && !(currentTokenStarts(Rule.Expression) || currentTokenFollows(Rule.Expression) || currentTokenFollows(Rule.Print)))
		{
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}

		if (!lineError && currentTokenStarts(Rule.Expression))
		{
			node = addNode(parent);
			lineError = rule_EXPRESSION(node);
		}

		if (!lineError && wordEquals(")"))
		{
			CodeGenerator.addInstruction(Instruction.OPR, OpCode.PrintNewLineValue);

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}
		else if (!lineError)
		{
			error(ParserError.ClosingParenthesis);
			addNode(ParserError.ClosingParenthesis, parent);
		}
		else if (lineError)
		{
			error(ParserError.ClosingParenthesis, currentToken - 1);
			addNode(ParserError.ClosingParenthesis, parent);
		}

		return lineError;
	}

	private static boolean rule_RETURN(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;

		addNode(parent);
		currentToken++;
		//lineError = currentTokenIsOnDifferentLine();

		CodeGenerator.addInstruction(Instruction.OPR.toString(), OpCode.Return.getCode(), "0");
		return lineError;
	}

	private static boolean rule_EXPRESSION(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		node = addNode("X", parent);
		lineError = rule_X(node);

		while (!lineError && hasTokens() && wordEquals("|"))
		{
			addNode("|", parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("X", parent);
				lineError = rule_X(node);

				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(Operator.$logical);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, OpCode.Or);
			}
		}

		if (lineError)
		{
			error(ParserError.ID_Value_OpenParenthesis, currentToken - 1);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}

		return lineError;
	}

	private static boolean rule_X(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		node = addNode("Y", parent);
		lineError = rule_Y(node);

		while (!lineError && wordEquals("&"))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("Y", parent);
				lineError = rule_Y(node);
				
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(Operator.$logical);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, OpCode.And);
			}
		}

		return lineError;
	}

	private static boolean rule_Y(DefaultMutableTreeNode parent)
	{
		boolean operatorUsed = false;
		boolean lineError = false;
		DefaultMutableTreeNode node;

		if (!lineError && wordEquals("!"))
		{
			operatorUsed = true;

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
		}

		if (!lineError)
		{
			node = addNode("R", parent);
			lineError = rule_R(node);

			if (operatorUsed)
			{
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(Operator.$negation);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, OpCode.Not);
			}
		}

		return lineError;
	}

	private static boolean rule_R(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		node = addNode("E", parent);
		lineError = rule_E(node);

		while (!lineError && hasTokens() && (wordEquals("<") || wordEquals(">") || wordEquals("==") || wordEquals("!=")))
		{
			Operator operatorUsed;
			OpCode opCode;

			if (wordEquals("<") || wordEquals(">"))
			{
				operatorUsed = Operator.$comparison;

				opCode = wordEquals("<") ? OpCode.LessThan : OpCode.GreaterThan;
			}
			else
			{
				operatorUsed = Operator.$equality;

				opCode = wordEquals("==") ? OpCode.Equals : OpCode.NotEqual;
			}

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("E", parent);
				lineError = rule_E(node);
				
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(operatorUsed);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, opCode);
			}
		}
		return lineError;
	}

	private static boolean rule_E(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		node = addNode("A", parent);
		lineError = rule_A(node);

		while (!lineError && hasTokens() && (wordEquals("-") || wordEquals("+")))
		{
			Operator operatorUsed;
			OpCode opCode;

			if (wordEquals("+"))
			{
				operatorUsed = Operator.$sum;
				opCode = OpCode.Add;
			}
			else
			{
				operatorUsed = Operator.$difference;
				opCode = OpCode.Subtract;
			}

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("A", parent);
				lineError = rule_A(node);
				
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(operatorUsed);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, opCode);
			}
		}
		return lineError;
	}

	private static boolean rule_A(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		node = addNode("B", parent);
		lineError = rule_B(node);

		while (!lineError && hasTokens() && (wordEquals("*") || wordEquals("/")))
		{
			Operator operatorUsed;
			OpCode opCode;

			if (wordEquals("*"))
			{
				operatorUsed = Operator.$product;
				opCode = OpCode.Multiply;
			}
			else
			{
				operatorUsed = Operator.$division;
				opCode = OpCode.Divide;
			}

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("B", parent);
				lineError = rule_B(node);
				
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(operatorUsed);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, opCode);
			}
		}
		return lineError;
	}

	private static boolean rule_B(DefaultMutableTreeNode parent)
	{
		boolean operatorUsed = false;
		boolean lineError = false;
		DefaultMutableTreeNode node;

		if (hasTokens() && wordEquals("-"))
		{
			operatorUsed = true;

			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();
			
			//CODE GENERATOR
			CodeGenerator.addInstruction(Instruction.LIT.toString(), "0", "0");
		}

		if (!lineError)
		{
			node = addNode("C", parent);
			lineError = rule_C(node);

			if (operatorUsed)
			{
				//SEMANTIC ANALYSIS
				SemanticAnalyzer.operatorUsed(Operator.$unaryMinus);
				
				//CODE GENERATOR
				CodeGenerator.addInstruction(Instruction.OPR, OpCode.Subtract);
			}
		}

		return lineError;
	}

	private static boolean rule_C(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		if (currentTokenStarts(Rule.C) && !wordEquals("("))
		{
			// SEMANTIC ANALYSIS
			SemanticAnalyzer.variableUsed(tokens.get(currentToken), gui);

			// CODE GENERATOR
			if (tokens.get(currentToken).getToken().equalsIgnoreCase("identifier"))
			{
				CodeGenerator.addInstruction(Instruction.LOD.toString(), tokens.get(currentToken).getWord(), "0");
			}
			else
			{
				CodeGenerator.addInstruction(Instruction.LIT.toString(), tokens.get(currentToken).getWord(), "0");
			}

			// SYNTACTIC ANALYSIS
			addNode(tokens.get(currentToken).getToken().toLowerCase() + "(" + tokens.get(currentToken).getWord() + ")", parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

		}
		else if (wordEquals("("))
		{
			addNode(parent);
			currentToken++;
			//lineError = currentTokenIsOnDifferentLine();

			if (!lineError)
			{
				node = addNode("EXPRESSION", parent);
				lineError = rule_EXPRESSION(node);
			}

			if (!lineError && wordEquals(")"))
			{
				addNode(")", parent);
				currentToken++;
				//lineError = currentTokenIsOnDifferentLine();
			}
			else if (!lineError)
			{
				error(ParserError.ClosingParenthesis);
				addNode(ParserError.ClosingParenthesis, parent);
			}
			else if (lineError)
			{
				error(ParserError.ClosingParenthesis, currentToken - 1);
				addNode(ParserError.ClosingParenthesis, parent);
			}
		}
		else
		{
			error(ParserError.ID_Value_OpenParenthesis);
			addNode(ParserError.ID_Value_OpenParenthesis, parent);
		}
		return lineError;
	}

	private static boolean rule_SWITCH(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		addNode("switch", parent);
		currentToken++;

		// CODE GENERATOR - We create the label that the cases will jump to at the end of their body. Note, we do not yet know where to jump
		String exitSwitchLabel = CodeGenerator.createLabel();
		String switchIdentifier = "";

		if (wordEquals("("))
		{
			addNode("(", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.OpeningParenthesis);
			addNode(ParserError.OpeningParenthesis, parent);
		}

		if (tokenEquals("identifier"))
		{
			// CODE GENERATOR - We need to save the switchIdentifier so that we can load it within each Case of the body.
			switchIdentifier = tokens.get(currentToken).getWord();

			addNode("identifier", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.ID);
			addNode(ParserError.ID, parent);
		}

		if (wordEquals(")"))
		{
			addNode(")", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.ClosingParenthesis);
			addNode(ParserError.ClosingParenthesis, parent);
		}

		if (wordEquals("{"))
		{
			addNode("{", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.OpeningCurly);
			addNode(ParserError.OpeningCurly, parent);
		}

		if (!currentTokenStarts(Rule.Cases))
		{
			error(ParserError.Keyword);
			addNode(ParserError.Keyword, parent);
		}

		while (!currentTokenStarts(Rule.Cases) && !currentTokenFollows(Rule.Cases))
		{
			currentToken++;
		}

		if (currentTokenStarts(Rule.Cases))
		{
			node = addNode("CASES", parent);
			lineError = rule_CASES(node, switchIdentifier, exitSwitchLabel);
		}

		if (currentTokenStarts(Rule.Default))
		{
			node = addNode("DEFAULT", parent);
			lineError = rule_DEFAULT(node);
		}

		// CODE GENERATOR - This is where the exitSwitchLabel should jump to.
		CodeGenerator.addLabel(exitSwitchLabel, CodeGenerator.getPC());

		if (wordEquals("}"))
		{
			addNode("}", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.ClosingCurly);
			addNode(ParserError.ClosingCurly, parent);
		}

		return lineError;
	}

	private static boolean rule_CASES(DefaultMutableTreeNode parent, String switchIdentifier, String exitSwitchLabel)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;
		String skipCaseLabel;

		do
		{
			addNode("cases", parent);
			currentToken++;

			if (tokenEquals("integer") || tokenEquals("hexadecimal") || tokenEquals("octal") || tokenEquals("binary"))
			{
				// CODE GENERATOR - We must load the SWITCH IDENTIFIER and LIT value and compare them.
				CodeGenerator.addInstruction(Instruction.LOD.toString(), switchIdentifier, "0");
				CodeGenerator.addInstruction(Instruction.LIT.toString(), tokens.get(currentToken).getWord(), "0");
				CodeGenerator.addInstruction(Instruction.OPR, OpCode.Equals);

				addNode(tokens.get(currentToken).getToken() + "(" + tokens.get(currentToken).getWord() + ")", parent);
				currentToken++;
			}
			else
			{
				error(ParserError.Value);
				addNode(ParserError.Value, parent);
			}

			// CODE GENERATOR - If the Comparison above was false, then we will need to skip the case body.
			skipCaseLabel = CodeGenerator.createLabel();
			CodeGenerator.addInstruction(Instruction.JMC.toString(), skipCaseLabel, "false");

			if (wordEquals(":"))
			{
				addNode(":", parent);
				currentToken++;
			}
			else
			{
				error(ParserError.Colon);
				addNode(ParserError.Colon, parent);
			}

			if (!currentTokenStarts(Rule.Program))
			{
				error(ParserError.OpeningCurly);
				addNode(ParserError.OpeningCurly, parent);
			}

			while (!currentTokenStarts(Rule.Program) && !currentTokenStarts(Rule.Cases) && !currentTokenFollows(Rule.Cases))
			{
				currentToken++;
			}

			if (currentTokenStarts(Rule.Program))
			{
				node = addNode("PROGRAM", parent);
				lineError = rule_PROGRAM(node);
			}

			// CODE GENERATOR - Per the requirements, if the case of a body executes the program should exit the switch.
			CodeGenerator.addInstruction(Instruction.JMP.toString(), exitSwitchLabel, "0");

			// CODE GENERATOR - We now know the correct jump location for skipCaseLabel.
			CodeGenerator.addLabel(skipCaseLabel, CodeGenerator.getPC());

		} while (!currentTokenFollows(Rule.Cases) && currentTokenStarts(Rule.Cases) && !lineError);

		return lineError;
	}

	private static boolean rule_DEFAULT(DefaultMutableTreeNode parent)
	{
		boolean lineError = false;
		DefaultMutableTreeNode node;

		addNode("default", parent);
		currentToken++;

		if (wordEquals(":"))
		{
			addNode(":", parent);
			currentToken++;
		}
		else
		{
			error(ParserError.Colon);
			addNode(ParserError.Colon, parent);
		}

		if (!currentTokenStarts(Rule.Program))
		{
			error(ParserError.OpeningCurly);
			addNode(ParserError.OpeningCurly, parent);
		}

		while (!currentTokenStarts(Rule.Program) && !currentTokenFollows(Rule.Default))
		{
			currentToken++;
		}

		if (currentTokenStarts(Rule.Program))
		{
			node = addNode("PROGRAM", parent);
			lineError = rule_PROGRAM(node);
		}

		return lineError;
	}

	/**
	 * Adds a mutable tree node based on {@code tokens.get(currentToken).getWord()} to the {@code toParent} parameters and returns the
	 * {@code node} object that was added.
	 * 
	 * @param toParent
	 * @return DefaultMutableTreeNode
	 */
	private static DefaultMutableTreeNode addNode(DefaultMutableTreeNode toParent)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
		toParent.add(node);
		return node;
	}

	/**
	 * Adds a mutable tree node with the {@code nodeDescription} to the {@code toParent} parameter and returns the {@code node} object that
	 * was added.
	 * 
	 * @param nodeDescription
	 * @param toParent
	 * @return DefaultMutableTreeNode
	 */
	private static DefaultMutableTreeNode addNode(String nodeDescription, DefaultMutableTreeNode toParent)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeDescription);
		toParent.add(node);
		return node;
	}

	/**
	 * Adds a mutable tree node, which represents the {@code error} parameter, to the {@code toParent} parameter and returns the {@code node}
	 * object that was added.
	 * 
	 * @param error
	 * @param toParent
	 * @return DefaultMutableTreeNode
	 */
	private static void addNode(ParserError error, DefaultMutableTreeNode toParent)
	{
		DefaultMutableTreeNode node = null;

		if (hasTokens() && tokens.get(currentToken).getToken().toUpperCase().equals("ERROR"))
		{
			node = new DefaultMutableTreeNode("ERROR(" + tokens.get(currentToken).getWord() + ")");
			toParent.add(node);
		}
	}

	/**
	 * Determines if the currentWord that the parser is iterating over is equal to the parameter string.<br/>
	 * Note: This function IS case sensitive.
	 * 
	 * @param string
	 * @return boolean
	 */
	private static boolean wordEquals(String string)
	{
		return hasTokens() && tokens.get(currentToken).getWord().equals(string);
	}

	/**
	 * Determines if the currentToken that the parser is iterating over is equal to the parameter string.<br/>
	 * Note: This function is NOT case sensitive.
	 * 
	 * @param string
	 * @return boolean
	 */
	private static boolean tokenEquals(String string)
	{
		return hasTokens() && tokens.get(currentToken).getToken().equalsIgnoreCase(string);
	}

	/**
	 * Determines if there are tokens remaining in the {@code tokens} vector.
	 * 
	 * @return boolean
	 */
	private static boolean hasTokens()
	{
		return currentToken < tokens.size();
	}

	/**
	 * Determines if the currentToken is <strong>not</strong> on the same line as the last.<br/>
	 * Note: This should be called every time the tokenCount is incremented.
	 * 
	 * @return boolean
	 */
	private static boolean currentTokenIsOnDifferentLine()
	{
		return hasTokens() && !(tokens.get(currentToken).getLine() == tokens.get(currentToken - 1).getLine());
	}

	/**
	 * Determines if the currentToken that the parser is iterating over is one which contains a value.
	 * 
	 * @return boolean
	 */
	private static boolean isValue()
	{
		return tokenEquals("INTEGER") || tokenEquals("OCTAL") || tokenEquals("HEXADECIMAL") || tokenEquals("BINARY") || tokenEquals("STRING")
		      || tokenEquals("CHARACTER") || tokenEquals("FLOAT") || tokenEquals("IDENTIFIER") || wordEquals("true") || wordEquals("false");
	}

	/**
	 * Determines if the currentWord that the parser is iterating over is one which represents a data type.
	 * 
	 * @return boolean
	 */
	private static boolean isDataType()
	{
		return wordEquals("int") || wordEquals("float") || wordEquals("boolean") || wordEquals("char") || wordEquals("string")
		      || wordEquals("void");
	}

	/**
	 * Prints out an error message based on the parameter {@code error}.<br/>
	 * 
	 * @see {@link ParserError}
	 * @param error
	 */
	private static void error(ParserError error)
	{
		int token = hasTokens() ? currentToken : currentToken - 1;
		int currentLine = hasTokens() ? tokens.get(token).getLine() : tokens.get(token).getLine() + 1;
		String errorMessage = "Line " + currentLine + ": expected " + error.getError();
		System.out.println(errorMessage + "\n\tWord: " + tokens.get(token).getWord());
		Parser.gui.writeConsole(errorMessage);
	}

	/**
	 * Prints out an error message based on the parameter {@code error} of the specified {@code previousToken}.<br/>
	 * 
	 * @see {@link ParserError}
	 * @param error
	 */
	private static void error(ParserError error, int previousToken)
	{
		int previousLine = tokens.get(previousToken) != null ? tokens.get(previousToken).getLine() : -1;
		String errorMessage = "Line " + previousLine + ": expected " + error.getError();
		System.out.println(errorMessage + "\n\tWord: " + tokens.get(previousToken).getWord());
		Parser.gui.writeConsole(errorMessage);
	}

	/**
	 * Determines if the current token matches the expected value, as defined in the <strong>first set</strong>, of the parameter
	 * {@code rule}
	 * 
	 * @param rule
	 * @return boolean
	 */
	private static boolean currentTokenStarts(Rule rule)
	{
		if (hasTokens())
		{
			switch (rule)
			{
				case Program:
					return wordEquals("{");
				case Body:
					return currentTokenStarts(Rule.Print) || currentTokenStarts(Rule.Assignment) || currentTokenStarts(Rule.Variable)
					      || currentTokenStarts(Rule.While) || currentTokenStarts(Rule.If) || currentTokenStarts(Rule.Return) || currentTokenStarts(Rule.Switch);
				case Print:
					return wordEquals("print");
				case Assignment:
					return tokenEquals("IDENTIFIER");
				case Variable:
					return isDataType();
				case While:
					return wordEquals("while");
				case If:
					return wordEquals("if");
				case Return:
					return wordEquals("return");
				case Expression:
					return currentTokenStarts(Rule.X);
				case X:
					return currentTokenStarts(Rule.Y);
				case Y:
					return wordEquals("!") || currentTokenStarts(Rule.R);
				case R:
					return currentTokenStarts(Rule.E);
				case E:
					return currentTokenStarts(Rule.A);
				case A:
					return wordEquals("-") || currentTokenStarts(Rule.B);
				case B:
					return currentTokenStarts(Rule.C);
				case C:
					return isValue() || wordEquals("(");
				case Switch:
					return wordEquals("switch");
				case Cases:
					return wordEquals("case");
				case Default:
					return wordEquals("default");
				default:
					return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Determines if the current token matches the expected value, as defined in the <strong>follow set</strong>, of the parameter
	 * {@code rule}
	 * 
	 * @param rule
	 * @return boolean
	 */
	private static boolean currentTokenFollows(Rule rule)
	{
		if (hasTokens())
		{
			switch (rule)
			{
				case Program:
					return wordEquals(null);
				case Body:
					return wordEquals("}");
				case Print:
					return wordEquals(";");
				case Assignment:
					return wordEquals(";");
				case Variable:
					return wordEquals(";");
				case While:
					return currentTokenFollows(Rule.Body) || currentTokenStarts(Rule.Body);
				case If:
					return currentTokenFollows(Rule.Body) || currentTokenStarts(Rule.Body);
				case Return:
					return wordEquals(";");
				case Expression:
					return wordEquals(")") || wordEquals(";");
				case X:
					return wordEquals("|") || currentTokenFollows(Rule.Expression);
				case Y:
					return wordEquals("&") || currentTokenFollows(Rule.X);
				case R:
					return currentTokenFollows(Rule.Y);
				case E:
					return wordEquals("!=") || wordEquals("==") || wordEquals(">") || wordEquals("<") || currentTokenFollows(Rule.R);
				case A:
					return wordEquals("-") || wordEquals("+") || currentTokenFollows(Rule.E);
				case B:
					return wordEquals("*") || wordEquals("/") || currentTokenFollows(Rule.A);
				case C:
					return currentTokenFollows(Rule.B);
				case Switch:
					return currentTokenFollows(Rule.Body);
				case Cases:
					return currentTokenStarts(Rule.Default) || wordEquals("}");
				case Default:
					return wordEquals("}");
				default:
					return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * A basic enumerator class that lists all of the rules that are defined in the parser.
	 * 
	 * @see Parser
	 * @author Jan Christian Chavez-Kortlang
	 */
	private enum Rule
	{
		Program,
		Body,
		Assignment,
		Variable,
		While,
		If,
		Return,
		Print,
		Expression,
		X,
		Y,
		R,
		E,
		A,
		B,
		C,
		Switch,
		Cases,
		Default;
	}

	/**
	 * Enumerator class that contains all of the syntax errors that the parser may encounter. Every enumerator contains a string that
	 * describes the expected word(s) of each error.
	 * 
	 * @author Jan Christian Chavez-Kortlang
	 */
	private enum ParserError
	{
		OpeningCurly("{"),
		ClosingCurly("}"),
		OpeningParenthesis("("),
		ClosingParenthesis(")"),
		SemiColon(";"),
		Colon(":"),
		Equals("="),
		ID("identifer"),
		Keyword("keyword"),
		Value("value"),
		ID_Keyword("identifer or keyword"),
		ID_Value_OpenParenthesis("value, identifier, (");

		private ParserError(String expectedWord)
		{
			this.description = expectedWord;
		}

		/**
		 * Retrieves a String that represents the expected word(s).
		 * 
		 * @return String
		 */
		public String getError()
		{
			return this.description;
		}

		private String description;

	}
}
