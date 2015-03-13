package slak;

public enum Token {
	INPUT("read"), OUTPUT("write"),
	ASSIGNMENT(true, false, "="),
	TRUE(false, true, "true"), FALSE(false, true, "false"), AND(false, true, "&&"), OR(false, true, "||"), NOT(false, true, "!"),
	IF("if"), ELSE("else"), WHILE("while"), UNTIL("until"),
	DO(true, false, "do"), THEN("{"), EXECUTE("{"), END(true, true, "}"),
	PLUS(true, true, "+"), MINUS(true, true, "-"), MULTIPLY(true, true, "*"), DIVIDE(true, true, "/"), REMAINDER(true, true, "%"),
	MORE_EQUALS(false, true, ">="), LESS_EQUALS(false, true, "<="), EQUALS(false, true, "=="), NOT_EQUALS(false, true, "!="), MORE_THAN(true, true, ">"), LESS_THAN(true, true, "<"), 
	NUMBER(false, true, "number"), STRING(false, true, "string"), VARIABLE_NAME(false, true, "variable name"), LINE("newline"),
	COMMA(true, true, ","), LPAR(true, true, "("), RPAR(true, true, ")"), LSQPAR(true, true, "["), RSQPAR(true, true, "]");
	
	private String value;
	private boolean isOneChar = false, outputable = false;
	
	private Token(String value) {
		this.value = value;
	}
	private Token(boolean isOneChar, boolean outputable, String value) {
		this.isOneChar = isOneChar;
		this.outputable = outputable;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String newVal) {
		this.value = newVal;
	}
	
	public boolean isOneChar() {
		return isOneChar;
	}
	public void setOneChar(boolean isOneChar) {
		this.isOneChar = isOneChar;
	}
	
	public boolean isOutputable() {
		return outputable;
	}
	public void setOutputable(boolean outputable) {
		this.outputable = outputable;
	}
}
