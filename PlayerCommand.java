public class PlayerCommand
{
	private String[] words = {"", "", "", "", "", "", "", ""};
	private int w_count;

	public PlayerCommand (String input)
	{
		w_count = 0;
		int w_start = 0;
		boolean quotation = false;

		//Start of the for loop for checking specific characters in the string.
		for (int i = 0; i < input.length(); i++)
		{

			/*
			First Check
			This checks for quotation.
			If a quotation mark is found it toggles a boolean for whether to
			ignore spaces when checking for new words.
			*/

			if (input.charAt(i) == '\"' && quotation == false)
			{
				quotation = true;
			}else if (input.charAt(i) == '\"' && quotation == true)
			{
				quotation = false;
			}

			/*
			Second Check
			This checks for in order:
			If its the last character in the string.
			OR
			If it's a space AND
			If it's not the first character AND
			If the character before it wasn't also a space AND
			If there wasn't a previous quotation mark.
			*/
			if (i+1 == input.length() || (input.charAt(i) == ' ' && i > 0 && input.charAt(i-1) != ' ' && quotation == false))
			{
				for (int j = w_start; j < i; j++)
				{
					words[w_count] += input.charAt(j);
				}

				//If it is the last char in the string, this wil write it.
				if (i+1 == input.length())
				{
					words[w_count] += input.charAt(i);
				}

				//Word count and string position of next word.
				w_count++;
				w_start = i+1;
			}
		}

		//Sets all words to lowercase and removes quotation marks.
		for (int i = 0; i < w_count; i++)
		{
			words[i] = words[i].toLowerCase();
			words[i] = words[i].replaceAll("^\"|\"$", "");
		}

		//Removes prepositions
		for (int i = 1; i < words.length; i++)
		{
			if (words[i].equals("with")) //Any other reversal words go here.
			{
				String temp = words[i-1];

				words[i-1] = words[i+1];
				words[i+1] = temp;
				wordShift(i);
			}

			if (words[i].equals("on") || words[i].equals("at")) //Any other combining words go here.
			{
				wordShift(i);
			}
		}
	}

/**
Returns a string for the word number specified.
@param number The position of the word requested.
@return String The word at the specified position.
*/
	public String getWord(int number)
	{
		return words[number];
	}

	public void setWord(int number, String newWord)
	{
		words[number] = newWord;
	}

	public void wordShift(int start, String newWord)
	{
		words[start] = newWord;
		start++;

		for (int i = start; i < (words.length - 1); i++)
		{
			words[i] = words[i+1];
		}

		words[words.length-1] = "";
	}

	public void wordShift(int start)
	{
		for (int i = start; i < (words.length - 1); i++)
				{
					words[i] = words[i+1];
				}

		words[words.length-1] = "";
	}

	public void verbShift(String verb)
	{
		for (int i = 7; i > 0; i--)
		{
			words[i] = words[i-1];
		}

		words[0] = verb;
	}

/**
Outputs all words stored in the command for printing to the console
@return String Every word in the PlayerCommand object.
*/
	public String toString()
	{
		String output = "To String: ";

		for (int i = 0; i < w_count; i++)
		{
			output += words[i];

			if (i < w_count)
			{
				output += " ";
			}
		}

		return output;
	}

}