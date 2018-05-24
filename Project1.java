//William Dahl
//ICSI 311
//Feb. 28th, 2018

import java.io.*;
import java.util.*;

//Regual Expression Processor
public class Project1{
	//Main function
	//Parama:
	//	String array intialized from inputs given in the command line 
	public static void main(String[] args) throws IOException {
		String regex = args[0]; // holds the regular expression
		//Checks if the correct number of inputs were given on the program call
		//Prints error message if there is not enouph or too many inours given and ends the program 
		if(args.length != 2){
			System.out.println("incorrect ussage: input should be \"java Project1 [regular expresion] [file]\"");
			System.exit(0);
		}

		//Intializes File object FileReader and BufferedReader to read from the file.
		File file = new File(args[1]);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		int lineNum = 0; //Keeps track of the line number currently on in the file
		readLine(regex, br, lineNum); //Calls the readLine function
	}

	//readLine function
	//reads a lne from the file pointed to by br
	//Parama:
	//	regex - String that holds the regular expression
	//	br - pointer to the BufferedReader object holding the pointer to the File.
	//	lineNum - the number of the line currently on in the file
	public static void readLine(String regex, BufferedReader br, int lineNum) throws IOException{
		String line; // holds the line read from the file
		int pos = -1; // holds the position currently on in the file
		line = br.readLine(); // reads line from file
		//Checks if the line read is null
		//If not increments the line number
		if(line != null){
			lineNum++;
		}

		//Else prints EOF messsage and ends the program
		else{
			System.out.println("End of file reached!");
			System.exit(0);
		}

		//Call to start function
		start(regex, line, pos, br, lineNum);
	}

	//Start function
	//First state in the state machine
	//Checks the first charecter in the regualr expression
	//Parama:
	//	regex - String that holds the regular expression
	//	line - String that holds the current line of the file
	//	pos - holds the current position in the line of the file
	//	br - pointer to BufferedReader object that reades the file
	// 	lineNum - number of line currently on in file
	public static void start(String regex, String line, int pos, BufferedReader br, int lineNum) throws IOException {
		int r = 0; // index for regex
		String output = ""; // string to hold the matched string
		pos++; //increments the position in the line to 0

		//checks for what the first charecter in the regular expresion is.
		if(regex.charAt(r) == '['){
			//if its a bracket calls bracket function
			bracket(regex, r, line, pos, output, br, lineNum);
		}

		else if(regex.charAt(r) == '\\'){
			// if its a backslash calls the slash function
			slash(regex, r, line, pos, output, br, lineNum);
		}

		else{
			//else calls the liteal function
			literal(regex, r, line, pos, output, br, lineNum);
		}
	}

	//literal function
	//treates the current charecter in the regular expression as a character
	//checks next charecter to find the nex state to go to
	//Parama:
	//	regex - holds the regular expression
	//	r - index for regex
	// 	line - holds the current line in the file
	//	pos - current position in the line in the file
	//  output - holds the matched charecters
	//  br - reader object to tread the file
	//	lineNum - number of current line in the file
	public static void literal(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		//checks if the position in still on the line and if there is a next character in the regular expression
		if(pos < line.length() && r+1 < regex.length()){
			//checks what the next charecter in the regular expression is and increments the index of regex
			//if star calls star function
			if(regex.charAt(r+1) == '*'){
				r++;
				star(regex, r, line, pos, output, br, lineNum);
			}

			//if plus calls plus function
			else if(regex.charAt(r+1) == '+'){
				r++;
				plus(regex, r, line, pos, output, br, lineNum);
			} 
 
			//if niether star or plus
			// checks if the current charecter in the regular expression is equal to the current charecter on the line in the file
			else if(regex.charAt(r) == line.charAt(pos)){
				//checks what the next charecter in the regualr expression is
				//appendes the charecter to output, increpemnets the index of regex
				//moves up a position on the line
				//if bracket calls the bracket function 
				if(regex.charAt(r+1) == '['){
					output += line.charAt(pos);
					pos++;
					r++;
					bracket(regex, r, line, pos, output, br, lineNum);
				}

				//if back slash calls the slash fucntion
				else if(regex.charAt(r+1) == '\\'){
					output += line.charAt(pos);
					pos++;
					r++;
					slash(regex, r, line, pos, output, br, lineNum);
				}

				//else calls the literal function
				else{
					output += line.charAt(pos);
					pos++;
					r++;
					literal(regex, r, line, pos, output, br, lineNum);
				}
			}

			//if charecters do not match go back to start
			else{
				start(regex, line, pos, br, lineNum);
			}
		}

		// if on last charecter in regualr expresion
		else if(pos < line.length() && r < regex.length()){
			// compares charecters
			//appends to output and calls print
			if(regex.charAt(r) == line.charAt(pos)){
				output += line.charAt(pos);
				print(regex, line, pos, output, br, lineNum);
			}

			//else goes back to start
			else{
				start(regex, line, pos, br, lineNum);
			}
		}

		//if position is out of line and printOrread returns true and the output length in no zero
		// output is printed
		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		// if postio in off of line and either printOrRead returns false or output is empty
		// reads next line in file
		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		//else print output
		else{
			print(regex, line, pos, output, br, lineNum);
		}
	}

	//slash function
	//makes next charecter in regualr expression treated as a literal
	//Parama:
	//	regex - regular expression
	//	r - index for regex
	//	line - current line in file
	//	pos - postion in the line
	//	output - mathced string
	//	br - file reader
	//	lineNum - line number
	public static void slash(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		//checks if the index in on the regular expression
		//if it is then increments the index for regex and calls literal
		if(r < regex.length()){
			r++;
			literal(regex, r, line, pos, output, br, lineNum);
		}

		//else prints output
		else{
			print(regex, line, pos, output, br, lineNum);
		}
	}

	//bracket function
	//gets all characters within the bracket and and put them into a string
	//then checks for the next charecter in the regular expression
	//Parama:
	//	regex - regualr expression
	//	r- index for regex
	// 	line - current line
	//	pos - position in line
	//  output - matched string
	//	br - file reader
	//	lineNum - number of line
	public static void bracket(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		String bracket = "";//string to hold charecters between the brackets in the regular expression
		r++; // incremnt index

		// loops throught the charecters inbetween the brackets
		while(regex.charAt(r) != ']'){
			bracket += regex.charAt(r); // adds the charecter to the bracket
			r++;
		}
		r++;
		if(r < regex.length() && pos < line.length()){
			if(regex.charAt(r) == '*'){
				//call to overloaded star function that uses the beracket string
				star(regex, r, line, pos, output, br, lineNum, bracket);
			}

			else if(regex.charAt(r) == '+'){
				//call to overloaded plus function that uses the bracket string
				plus(regex, r, line, pos, output, br, lineNum, bracket);
			}

			//calls inBracket with the bracet string and the charecter at the curren position in the line
			//if it is in the bracket then it is added to the output string
			//checks the next charecter in the regualr expression and calls the resulting function
			else if(regex.charAt(r) == '\\'){
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
					slash(regex, r, line, pos, output, br, lineNum);
				}

				else{
					start(regex, line, pos, br, lineNum);
				}
			}

			else if(regex.charAt(r) == '['){
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
					bracket(regex, r, line, pos, output, br, lineNum);
				}

				else{
					start(regex, line, pos, br, lineNum);
				}
			}

			else{
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
					literal(regex, r, line, pos, output, br, lineNum);
				}

				else{
					start(regex, line, pos, br, lineNum);
				}
			}
		}

		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		// if there is no next charecter in the regualr expression
		else{
			if(inBracket(bracket, line.charAt(pos))){
				output += line.charAt(pos);
				print(regex, line, pos, output, br, lineNum);
			}
			
			else{
				start(regex, line, pos, br, lineNum);
			}
		}
	}

	//inBracket function
	//checks if the charecter is within the brackets in the regular expression
	//Parama:
	//	bracket - string holding the charecters within the brackets in the regular expression
	//	c - charecter at the current position in the line
	//return:
	//	true - if the charecters is in the bracket string
	//	false - if the charecter is not in the bracket string
	public static boolean inBracket(String bracket, char c){
		int i = 0;//index for bracket
		//loops through all the charecter in the bracket string
		while(i < bracket.length()){
			//checks if the current charcter in bracket equals the specified charecter
			if(bracket.charAt(i) == c){
				return true;//returns true at any point the charecters equal each other
			}

			i++;// increments the index
		}

		return false;// return false if true is never returned
	}

	//star function
	//adds the charecter in line at the current position to output if it appers 0 or more times
	//Parama:
	//	regex - regualr expression
	//	r- index for regex
	// 	line - current line
	//	pos - position in line
	//  output - matched string
	//	br - file reader
	//	lineNum - number of line
	public static void star(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		if(r+1 < regex.length() && pos < line.length()){
			while(r+1 < regex.length() && pos < line.length()){
				//checks if the charecter prior to the star is the same as the charecter in the line
				//appends the charecter to the output string
				// moves to next position in the line
				if(regex.charAt(r-1) == line.charAt(pos)){
					output += line.charAt(pos);
					pos++;
				}

				//checks if the next charecter in regex is the same as the charecter in the line
				//goes to next charecter in regex and calls literal
				else if(regex.charAt(r+1) == line.charAt(pos)){
					r++;
					literal(regex, r, line, pos, output, br, lineNum);
					break;
				}

				//checks if the next charetcter is a special charecter and calls the respective function
				else if(regex.charAt(r+1) == '['){
					r++;
					bracket(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '\\'){
					r++;
					slash(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(r+2 < regex.length()){
					if(regex.charAt(r+2) == '*'){
						r += 2;
						star(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else if(regex.charAt(r+2) == '+'){
						r += 2;
						plus(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else{
						start(regex, line, pos, br, lineNum);
						break;
					}
				}

				//print
				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			//read line
			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		//no next charecter in regex
		else if(r < regex.length() && pos < line.length()){
			while(r < regex.length() && pos < line.length()){
				if(regex.charAt(r-1) == line.charAt(pos)){
					output += line.charAt(pos);
					pos++;
				}

				//prints if no match is found
				else{
					print(regex, line, pos, output, br, lineNum);
					break;
				}
			}

			//prints if end of line reached
			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		else if(r >= regex.length()){
			print(regex, line, pos, output, br, lineNum);
		}
	}

	//overloaded function of star
	//Uses inBracket to check if the charectes in the brackets appear zero ro more times in the line
	//Parama:
	//	bracket - string holding the charecters in the brackets in the regualr expression
	public static void star(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum, String bracket) throws IOException{
		//checks for next charecter in regex
		if(r+1 < regex.length() && pos < line.length()){
			//reads charecters until end of regex or the line in the file
			while(r+1 < regex.length() && pos < line.length()){
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
				}

				else if(regex.charAt(r+1) == line.charAt(pos)){
					r++;
					literal(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '['){
					r++;
					bracket(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '\\'){
					r++;
					slash(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(r+2 < regex.length()){
					if(regex.charAt(r+2) == '*'){
						r += 2;
						star(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else if(regex.charAt(r+2) == '+'){
						r += 2;
						plus(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else{
						start(regex, line, pos, br, lineNum);
						break;
					}
				}

				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		//if there is no next charecter in the regular expression
		else if(r < regex.length() && pos < line.length()){
			while(r < regex.length() && pos < line.length()){
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
				}

				else{
					print(regex, line, pos, output, br, lineNum);
				}
			}

			// prints output if end of line is reached
			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		//reads new line if end of line is reached
		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		// prints if end of regex is reached
		else if(r >= regex.length()){			
			print(regex, line, pos, output, br, lineNum);
		}
	}

	//plus function
	//matches charecters in the regular expression if the apprear in the file one or more times
	//Parama:
	//	regex - regualr expression
	//	r- index for regex
	// 	line - current line
	//	pos - position in line
	//  output - matched string
	//	br - file reader
	//	lineNum - number of line
	public static void plus(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		int count = 0; // counts the number of matches
		//checks if there is a next charecter in regex
		if(r+1 < regex.length() && pos < line.length()){
			//loops til the end of regex or line
			while(r+1 < regex.length() && pos < line.length()){
				//checks if prior charecter equals the current charecter in the line and if no ther match has been foung
				if(regex.charAt(r-1) == line.charAt(pos) && count == 0){
					output += line.charAt(pos); //appends the char to output
					pos++;//goes to next postion in the line
					count++; // shows that one match has been found
				}

				else if(regex.charAt(r-1) == line.charAt(pos) && count != 0){
					output += line.charAt(pos);
					pos++;
				}

				//checks if the next char in regex equals the current char in the line and count is more than zero
				else if(regex.charAt(r+1) == line.charAt(pos) && count != 0){
					r++;//goes to next char in regex
					literal(regex, r, line, pos, output, br, lineNum);
					break;// breaks out of loop
				}

				//checks if the next charetcter is a special charecter and calls the respective function and that count id more than zero
				else if(regex.charAt(r+1) == '[' && count != 0){
					r++;
					bracket(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '\\' && count != 0){
					r++;
					slash(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(r+2 < regex.length()){
					if(regex.charAt(r+2) == '*' && count != 0){
						r += 2;
						star(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else if(regex.charAt(r+2) == '+' && count != 0){
						r += 2;
						plus(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else{
						start(regex, line, pos, br, lineNum);
						break;
					}
				}

				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		// no next char in regex
		else if(r < regex.length() && pos < line.length()){
			while(r < regex.length() && pos < line.length()){
				if(regex.charAt(r-1) == line.charAt(pos) && count == 0){
					output += line.charAt(pos);
					pos++;
					count++;
				}

				else if(regex.charAt(r-1) == line.charAt(pos) && count != 0){
					output += line.charAt(pos);
					pos++;
				}

				else if(regex.charAt(r-1) != line.charAt(pos) && count != 0){
					print(regex, line, pos, output, br, lineNum);
					break;
				}

				// no match goes back to start
				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			//prints if end of line is reached
			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		// reads line
		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		else if(r >= regex.length()){
			print(regex, line, pos, output, br, lineNum);
		}
	} 

	//overloaded function of plus
	//Uses inBracket to check if the charectes in the brackets appear one or more times in the line
	//Parama:
	//	bracket - string holding the charecters in the brackets in the regualr expression
	public static void plus(String regex, int r, String line, int pos, String output, BufferedReader br, int lineNum, String bracket) throws IOException{
		int count = 0;
		if(r+1 < regex.length() && pos < line.length()){
			while(r+1 < regex.length() && pos < line.length()){
				// uses inBracket
				if(inBracket(bracket, line.charAt(pos))){
					output += line.charAt(pos);
					pos++;
					count++;
				}

				else if(regex.charAt(r+1) == line.charAt(pos) && count != 0){
					r++;
					literal(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '[' && count != 0){
					r++;
					bracket(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(regex.charAt(r+1) == '\\' && count != 0){
					r++;
					slash(regex, r, line, pos, output, br, lineNum);
					break;
				}

				else if(r+2 < regex.length()){
					if(regex.charAt(r+2) == '*' && count != 0){
						r += 2;
						star(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else if(regex.charAt(r+2) == '+' && count != 0){
						r += 2;
						plus(regex, r, line, pos, output, br, lineNum);
						break;
					}

					else{
						start(regex, line, pos, br, lineNum);
					}
				}

				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		else if(r < regex.length() && pos < line.length()){
			while(r < regex.length() && pos < line.length()){
				if(inBracket(bracket, line.charAt(pos)) && count == 0){
					output += line.charAt(pos);
					pos++;
					count++;
				}

				else if(inBracket(bracket, line.charAt(pos)) && count != 0){
					output += line.charAt(pos);
					pos++;
				}

				else if(!(inBracket(bracket, line.charAt(pos))) && count != 0){
					print(regex, line, pos, output, br, lineNum);
					break;
				}

				else{
					start(regex, line, pos, br, lineNum);
					break;
				}
			}

			if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
				print(regex, line, pos, output, br, lineNum);
			}

			else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
				readLine(regex, br, lineNum);
			}
		}

		else if(pos >= line.length() && printOrRead(regex, r) && output.length() != 0){
			print(regex, line, pos, output, br, lineNum);
		}

		else if(pos >= line.length() && (!(printOrRead(regex, r)) || output.length() == 0)){
			readLine(regex, br, lineNum);
		}

		else if(r >= regex.length()){
			print(regex, line, pos, output, br, lineNum);
		}
	} 

	//printOrRead function
	// Decides weather to print out put or read next line in file depending on the rest of the regualr expression
	// Parama:
	//	regex - regular expresion
	//	r - index for regualr expression
	//return:
	//	true - prints the output
	//	false - reads next line
	public static boolean printOrRead(String regex, int r){
		// checks if there is another charecter int he regualr expression
		if(r+1 < regex.length()){
			// loops through regex until one index from the end of regex 
			while(r+1 < regex.length()){
				// checks if current charecter in regex is a plus
				if(regex.charAt(r) == '+'){
					//checks if preovios charecter is a slash
					if(regex.charAt(r-1) == '\\'){
						//checks if next charecter is a star
						if(regex.charAt(r+1) == '*'){
							r++;// goes to next charecter
						}

						else{
							return false; // returns
						}
					}

					else{
						return false; // returns
					}
				}

				//checks if current hcarecter is a bracket
				else if(regex.charAt(r) == '['){
					// checks if previous is a slash
					if(regex.charAt(r-1) == '\\'){
						//checks if next charecter is a star
						if(regex.charAt(r+1) == '*'){
							r++; // goes to next chaerecter
						}

						else{
							return false; // returns
						}
					}

					else{
						// loops through to end bracket
						while(regex.charAt(r) != ']'){
							r++;// goes to next char
						}

						//checks if there is a next charecter
						if(r+1 < regex.length()){
							// checks if next charecter is a star
							if(regex.charAt(r+1) == '*'){
								r++;// goes to next charecter
							}

							else{
								return false; // return
							}
						}

						//no next char
						else{
							return false; // return
						}	
					}
				}

				//char is either a star or slash
				else if(regex.charAt(r) == '*' || regex.charAt(r) == '\\'){
					r++; // next char
				}

				// char is a literal
				else{
					// checks for star as next char
					if(regex.charAt(r+1) == '*'){
						r++;//goes to next char
					}

					else{
						return false;//returns
					}
				}
			}

			return true;//returns
		}	

		// current char is the last char in regular expression
		else if(r < regex.length()){
			//checks if current char is star
			if(regex.charAt(r) == '+'){
				if(regex.charAt(r-1) == '\\'){
					return false; //last char is literal and pos is past the line so reads new line
				}

				else{
					return true; // char is not literal and can be printed
				}
			}

			// if bracket and last char t is assumed that it is a literal and thus not printed
			else if(regex.charAt(r) == '['){
					return false;
			}

			// checks if char is star
			else if(regex.charAt(r) == '*'){
				//checks if literal and does not print
				if(regex.charAt(r-1) == '\\'){
					return false;
				}

				// else prints
				else{
					return true;
				}
			}

			// char is aliteral and can not be printed
			else{

				return false;
			}
		}

		// default is to print output
		return true;
	}

	//print function
	//prints the out put
	//Parama:
	//	regex - regualr expression
	//	line - current line in file
	//	pos - postion in line
	//	output - matched string
	//	br - file reader
	//	lineNum - line number
	public static void print(String regex, String line, int pos, String output, BufferedReader br, int lineNum) throws IOException{
		int startPos;//staring postion
		startPos = pos - output.length(); // subtracts the current postion from the length of the output string
		//checks if output is empty
		//goes back to start
		if(output == ""){
			start(regex, line, pos, br, lineNum);
		}

		//else prints the matched string
		else{
			System.out.println("Match found on line " + lineNum + ", starting at position " + (startPos+1) + " and ending at position " + pos + ": " + output);
			// if end of line reached reads new line
			if(pos >= line.length()){
				readLine(regex, br, lineNum);
			}

			//else just goes back straight to start
			else{
				start(regex, line, pos, br, lineNum);
			}
		}
	}
}