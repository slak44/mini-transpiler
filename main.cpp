#include <string>
#include <iostream>
#include <vector>
#define PRINT(x) std::cout<<x<<std::endl
typedef unsigned int uint;

enum TokenType {
	SemiColumn,
	Comma,
	Plus,
	Minus,
	Asterix,
	Slash,
	LPar,
	RPar,
	If,
	Var,
	True,
	False,
	Or,
	And,
	LogOr,
	LogAnd,
	Not,
	NotEquals,
	Assignment,
	GreaterThan,
	GreaterThanEquals,
	Equals,
	SmallerThanEquals,
	SmallerThan,
	Word,
	LCurlyBrace,
	RCurlyBrace,
	LSquareBrace,
	RSquareBrace,
	DQuote,
	SQuote,
	Input,
	Output
};

class Token {
public:
	Token(TokenType type, std::string str) {
		t = type;
		str_val = str;
	}

	TokenType getType() {
		return t;
	}

	std::string getString() {
		return str_val;
	}

private:
	TokenType t;
	/*If the string is empty, the data is contained in the TokenType itself.*/
	std::string str_val = "";
};

//Stop on these chars
bool isFollowedBy(std::string in, uint pos) {
	switch (in.at(pos)) {
		case ' ': return true;
		case ';': return true;
		case ',': return true;
		case '+': return true;
		case '-': return true;
		case '*': return true;
		case '/': return true;
		case '|': return true;
		case '&': return true;
		case '>': return true;
		case '<': return true;
		case '=': return true;
		case '{': return true;
		case '}': return true;
		case '[': return true;
		case ']': return true;
		case ')': return true;
		case '(': return true;
		case '\"': return true;
		case '\'': return true;
		default: return false;
	}
}

Token getWord(std::string in, uint &newPos, uint pos = 0) {
	std::string res = "";
	res += in.at(pos);
	newPos = pos;
	for (uint i = pos+1; i < in.length(); i++) {
		if (isFollowedBy(in, i)) break;
		newPos = i;
		res += in.at(i);
	}
	PRINT("Word: " << res);
	return Token(Word, res);
}

std::vector<Token> tokenize(std::string input) {
	std::vector<Token> v;
	for (uint i = 0; i < input.size(); i++) {
		char a = input.at(i);
		switch (a) {
			case ' ':
				break;
			case ';':
				PRINT(';');
				v.push_back(Token(SemiColumn, ""));
				break;
			case ',':
				PRINT(',');
				v.push_back(Token(Comma, ""));
				break;
			case '+':
				PRINT('+');
				v.push_back(Token(Plus, ""));
				break;
			case '-':
				PRINT('-');
				v.push_back(Token(Minus, ""));
				break;
			case '*':
				PRINT('*');
				v.push_back(Token(Asterix, ""));
				break;
			case '/':
				PRINT('/');
				v.push_back(Token(Slash, ""));
				break;
			case '|':
				if (input.at(i+1) == '|') {
					PRINT("Logical OR");
					v.push_back(Token(LogOr, ""));
					i++;
				} else {
					PRINT("Binary OR");
					v.push_back(Token(Or, ""));
				}
				break;
			case '&':
				if (input.at(i+1) == '&') {
					PRINT("Logical AND");
					v.push_back(Token(LogAnd, ""));
					i++;
				} else {
					PRINT("Binary AND");
					v.push_back(Token(And, ""));
				}
				break;
			case '=':
				if (input.at(i+1) == '=') {
					PRINT("Equals");
					v.push_back(Token(Equals, ""));
					i++;
				} else {
					PRINT("Assignment");
					v.push_back(Token(Assignment, ""));
				}
				break;
			case '!':
				if (input.at(i+1) == '=') {
					PRINT("Not Equals");
					v.push_back(Token(NotEquals, ""));
					i++;
				} else {
					PRINT("Binary NOT");
					v.push_back(Token(Not, ""));
				}
				break;
			case '<':
				if (input.at(i+1) == '=') {
					PRINT("Inclusive smaller than");
					v.push_back(Token(SmallerThanEquals, ""));
					i++;
				} else {
					PRINT("Exclusive smaller than");
					v.push_back(Token(SmallerThan, ""));
				}
				break;
			case '>':
				if (input.at(i+1) == '=') {
					PRINT("Inclusive greater than");
					v.push_back(Token(GreaterThanEquals, ""));
					i++;
				} else {
					PRINT("Exclusive greater than");
					v.push_back(Token(GreaterThan, ""));
				}
				break;
			case 'v':
				if (input.at(i+1) == 'a' && input.at(i+2) == 'r' && input.at(i+3) == ' ') {
					PRINT("Var");
					v.push_back(Token(Var, ""));
					i += 2;
				} else v.push_back(getWord(input, i, i));
				break;
			case 'i':
				if (input.at(i+1) == 'f') {
					for (uint j = 2;; j++) {
						if (input.at(i+j) == '(') {
							PRINT("If");
							v.push_back(Token(If, ""));
							i += j-1;
							goto done;
						} else if (input.at(i+j) == ' ') {
							continue;
						} else break;
					}
				}
				v.push_back(getWord(input, i, i));
				done:;
				break;
			case 't'://TODO if a word starts with true or false it bugs
				if (input.substr(i, 4) == "true") {
					PRINT("True");
					v.push_back(Token(True, ""));
					i += 4;
				} else v.push_back(getWord(input, i, i));
				break;
			case 'f':
				if (input.substr(i, 5) == "false") {
					PRINT("False");
					v.push_back(Token(False, ""));
					i += 5;
				} else v.push_back(getWord(input, i, i));
				break;
			case 'a':
				if (input.substr(i, 5) == "alert") {
					PRINT("Output");
					v.push_back(Token(Output, ""));
					i += 5;
				} else v.push_back(getWord(input, i, i));
				break;
			case 'p':
				if (input.substr(i, 7) == "prompt(") {
					PRINT("Input");
					v.push_back(Token(Input, ""));
					i += 7;
				} else v.push_back(getWord(input, i, i));
				break;
			case '(':
				PRINT('(');
				v.push_back(Token(LPar, ""));
				break;
			case ')':
				PRINT(')');
				v.push_back(Token(RPar, ""));
				break;
			case '{':
				PRINT('{');
				v.push_back(Token(LCurlyBrace, ""));
				break;
			case '}':
				PRINT('}');
				v.push_back(Token(RCurlyBrace, ""));
				break;
			case '[':
				PRINT('[');
				v.push_back(Token(LSquareBrace, ""));
				break;
			case ']':
				PRINT(']');
				v.push_back(Token(RSquareBrace, ""));
				break;
			case '\"':
				PRINT('\"');
				v.push_back(Token(DQuote, ""));
				break;
			case '\'':
				PRINT('\'');
				v.push_back(Token(SQuote, ""));
				break;
			default: v.push_back(getWord(input, i, i));
		}
	}
	return v;
}

int main() {
	std::string input = "var i = 10; var j = 12; var k = i + j; alert k;";
	std::vector<Token> v = tokenize(input);
	return 0;
}
