import java.util.Scanner;
import java.io.*;
import java.util.regex.*;
import java.util.InputMismatchException;

public class ItemObject
{
	private int number;
	private String name;
	private String description;
	private String[] useEvents;
	private String[][] verbs;
	private String type;
	private int location;
	private final int FIELDS = 9;
	private int[] player;
	//private String verbActions;

	public ItemObject(int objectNumber, int[] playerInput)
	{
		Scanner input;
		File file;
		String verbNames;
		Pattern p = Pattern.compile(":");
		player = playerInput;

		location = player[objectNumber+player[10]];

		try
		{
			file = new File("ITEMS.txt");
			input = new Scanner(file, "UTF-8");

			for (int i = 0; i < objectNumber * FIELDS; i++)
			{
				input.nextLine();
			}

			number = objectNumber;
			input.nextLine();
			name = input.nextLine();
			input.nextLine(); //Skips the Aliases line
			description = input.nextLine();
			type = input.nextLine();
			useEvents = p.split(input.nextLine(), 0);

			verbNames = input.nextLine();

			String[] vNames = p.split(verbNames, 0);

/*

This was for the old (STATES) system. I don't know if I need it anymore.
			switch (player[objectNumber+player[10]])
			{
				case 3:input.nextLine();
				case 2:input.nextLine();
				case 1:input.nextLine();
						break;
			}
*/

			verbNames = input.nextLine();

			String[] vActions = p.split(verbNames, 0);

			verbs = new String[2][vNames.length];

			for (int i = 0; i < vNames.length; i++)
			{
				verbs[0][i] = vNames[i];
			}

			for (int i = 0; i < vActions.length; i++)
			{
				verbs[1][i] = vActions[i];
			}

		}catch(IOException e)
		{
		}
	}
	public ItemObject(int objectNumber)
	{
		Scanner input;
		File file;

		try
		{
			file = new File("ITEMS.txt");
			input = new Scanner(file, "UTF-8");

			for (int i = 0; i < objectNumber * FIELDS; i++)
			{
				input.nextLine();
			}

			number = objectNumber;
			input.nextLine();
			name = input.nextLine();
			description = input.nextLine();

		}catch (IOException | InputMismatchException e)
		{
			System.out.println("WARNING! The items.txt file is fucked!\n");
		}
	}

	public String verbsToString()
	{
		String output = "";

		for (int i = 0; i < verbs.length; i++)
		{
			for (int j = 0; j < verbs[0].length; j++)
			{
				output += verbs[i][j] + "&&";
			}
			output += "\n";
		}

		return output;
	}

	public int getNumber()
	{
		return number;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public int getLocation()
	{
		return location;
	}

	public String[] getUseEvents()
	{
		return useEvents;
	}

	public String[][] getVerbEvents()
	{
		return verbs;
	}

	public String getValidVerbs()
	{
		String output = "Valid verbs:\n";

		for (int i = 1; i < verbs[0].length; i++)
		{
			output += "\t" +verbs[0][i] + " : EVENT : " + verbs[1][i] + "\n";
		}

		return output;
	}

	public boolean hasVerb(String verb)
	{
		boolean valid = false;

		for (int i = 0; i < verbs[0].length; i++)
		{
			if (verbs[0][i].equals(verb))
			{
				valid = true;
			}
		}

		return valid;
	}

	public String getType()
	{
		return type;
	}

	public boolean isType(String that)
	{
		boolean state = false;

		if (this.type.equals(that))
		{
			state = true;
		}

		return state;
	}

	/**
	Returns the itemstate for the specified item.
	@param Int The item number.
	@return Int The item state. States are as follows:
				0 - Normal
				1 - Flipped
				2 - Stationary
				3 - Broken
				4 - Locked
				5 - Innaccessable
	*/
	public int getState()
	{
		int itemFlag = -6;

		if (player[player[10]+number] >= 0)
		{
			itemFlag = player[player[10]+number] / 100;
		}else
		{
			itemFlag = player[player[10]+number];
		}

		if (itemFlag == -1)
		{
			itemFlag = 0;
		}

		return Math.abs(itemFlag);
	}

	/**
	Returns the location of the item in the player array as an int.
	@return int The player array location value.
	*/
	public int p()
	{
		return player[10]+number;
	}

	public String toString()
	{
		String output = "";

		output += "Item Number: " + number + "\nItem Name: " + name +
					"\nItem Type: " + type +
					"\nItem Desciption: " + description +
					"\n\nCurrent Location: ";

		if (location >= 0)
		{
			while (location >= 100)
			{
				location -= 100;
			}
			RoomObject tempRoom = new RoomObject(location);

			output +=  tempRoom.getName() + "\nRoom number: " +
						location;
		}else
		{
			output += "Inventory.";
		}

		output += "\nState: ";

		switch (getState())
		{
			case 1: output += "Flipped. (If not in inventory)";
					break;
			case 2: output += "Stationary.";
					break;
			case 3: output += "Broken.";
					break;
			case 4: output += "Locked.";
					break;
			case 5: output += "Innaccessable.";
					break;
			default: output += "Normal.";
		}

		return output;
	}
}


/*
verbActions:
;verb e int (etc)
;verb p int int (etc)

eg

;look e 12 p 1 1
;use

Object states:
-1 Stationary, 0 Untouched, 1 flipped, 2 broken, 3 gone

VERB LIST for items:

- -> Non-unique (ie Covered by other fields or generic)
+ -> Unique (Will always produce a unique message)
* -> liminal (May for some, not for others)

-use, apply
-flip, ragequit, fuck
*break, attack,
-ron
*get, take, pocket, steal, sean wade
-shoot, 360noscope
+throw, match start


*/