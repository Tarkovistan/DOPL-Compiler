import java.util.Scanner;

import javax.xml.transform.Templates;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Tokenizer {

  public String read(String fileName, HashMap<String, String> map) throws IOException {
    Boolean error = false;
    Boolean found = false;
    String finalLine = "";
    String tempLine = "";
   


    File file = new File(fileName);
    Scanner scan = new Scanner(file);
    while (scan.hasNextLine()) {

      tempLine = scan.nextLine().trim();

      // Check if the current string is a symbol
      while (!tempLine.isEmpty()) {
        found = false;
        for (String symbol : map.keySet()) {
          if (tempLine.startsWith(symbol)) {
            if (symbol == "integer" || symbol == "character" || symbol == "logical" || symbol == "then"|| symbol == "else"|| symbol == "start") { // needs a space
              if (tempLine.length() == symbol.length() || tempLine.charAt(symbol.length()) == ' ') {
               
                found = true;
                finalLine += symbol + " "; // FINAL line
                tempLine = tempLine.replaceFirst(symbol, "").trim() + " ";

                break;
              } 

            } else if (symbol == "if" || symbol == "loopif") {
              if(tempLine.length() == symbol.length()||tempLine.charAt(symbol.length()) == ' ' || tempLine.charAt(symbol.length()) == '('){
                found = true;
                finalLine += symbol + " "; // FINAL line
                tempLine = tempLine.replaceFirst(symbol, "").trim() + " ";

                break;

              }

            } else if (symbol == "endloop" || symbol == "endif") { // Needs a ;
              if (tempLine.length() == symbol.length() || tempLine.charAt(symbol.length()) == ' '
                  || tempLine.charAt(symbol.length()) == ';') {
                found = true;
                finalLine += symbol + " "; // FINAL line
                tempLine = tempLine.replaceFirst(symbol, "").trim();

                break;
              }
            }
            else if(symbol == "(" || symbol == ")"){
              found = true;
                finalLine += symbol + " "; // FINAL line
                tempLine = tempLine.replaceFirst("\\"+symbol, "").trim();

                break;

            }
            else if(symbol == "\""){
              found = true;
              finalLine += symbol + " "; // FINAL line
              
              tempLine = tempLine.replaceFirst("\\"+symbol, "").trim() + " ";

              break;
            }
            else if(symbol.contains(".") || symbol.equals(";")|| symbol.equals("<-")|| symbol.equals(",")){
              found = true;
              finalLine += symbol + " "; // FINAL line
              tempLine = tempLine.replaceFirst("\\"+symbol, "").trim();

              break;
            }
             else {
              if ((tempLine.length() == symbol.length()) || tempLine.charAt(symbol.length()) == ' '){
                found = true;
              finalLine += symbol + " "; // FINAL line
             
              tempLine = tempLine.replaceFirst(symbol, "").trim() + " ";

              break;
              }
              
            } 

          }
        }
        if (!found) { // if it's a variable
          if (Character.isLetter(tempLine.charAt(0))) {
            int i = 0;
            i++;
            while (i < tempLine.length() && (Character.isLetter(tempLine.charAt(i)) || tempLine.charAt(i) == '_'
                || Character.isDigit(tempLine.charAt(i)))) {
              i++;
            }
            
            String substr = tempLine.substring(0, i);
            tempLine = tempLine.replaceFirst(substr, "").trim() + " ";
            finalLine += substr + " ";
            found = true;
          }
        }
        if(!found){
        
          if(tempLine.charAt(0)=='!' && tempLine.length()>1 &&  tempLine.charAt(1) == '"'){
            String substr = tempLine.substring(0, 1);
            
            
            tempLine = tempLine.replaceFirst(substr, "").trim() + " ";
            finalLine += substr + " ";
            found = true;
          }
          
        }
        if (!found) { // If it's a digit
          int i = 0;
          if (Character.isDigit(tempLine.charAt(0))) {

            i++;
            while (i < tempLine.length() && Character.isDigit(tempLine.charAt(i))) {
              i++;
            }
   
            String substr = tempLine.substring(0, i);
            if (tempLine.length() == substr.length() ||tempLine.charAt(substr.length()) == ' ' || tempLine.charAt(substr.length()) == ';'|| tempLine.charAt(substr.length()) == '('|| tempLine.charAt(substr.length()) == ')' || tempLine.charAt(substr.length()) == '.') {
              tempLine = tempLine.replaceFirst(substr, "").trim() + " ";
              finalLine += substr + " ";
              found = true;
            } 

          }
        }
        if(tempLine.isBlank()){
          break;
        }
        if (!found) { // if it's not a variable nor a symbol
        
          error = true;
          break;
        }
      }

    }

    scan.close();
    if(error){
      return "ERROR";
    }

    return finalLine;
  }

}
