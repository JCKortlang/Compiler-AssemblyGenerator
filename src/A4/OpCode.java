package A4;

public enum OpCode
{
	Exit("0"),
	Return("1"),
	Add("2"),
	Subtract("3"),
	Multiply("4"),
	Divide("5"),
	Modulo("6"),
	Power("7"),
	Or("8"),
	And("9"),
	Not("10"),
	GreaterThan("11"),
	LessThan("12"),
	GreaterThanOrEqual("13"),
	LessThanOrEqual("14"),
	Equals("15"),
	NotEqual("16"),
	ClearScreen("18"),
	ReadValue("19"),
	PrintValue("20"),
	PrintNewLineValue("21");

	private OpCode(String code)
	{
		this.code = code;
	}
	
	public String getCode()
	{
		return this.code;
	}
	
	private String code;
}
