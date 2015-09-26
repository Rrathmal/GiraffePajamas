import java.util.Scanner;
import java.io.*;
import java.util.regex.*;

public class RoomObject
{
	private int number;
	private String name;
	private String description;
//	private int[] npcs;
	private int[] items;
	private int numItems = 0;
	private final int FIELDS = 6;
	private final int DIRECTIONS = 8;
	private final int[] player;
	
	private int visibleItems;

	private int[] exits = new int[DIRECTIONS];
	private int[] collectables;

	public RoomObject(int roomNumber, int[] playerInput)
	{
		Scanner input;
		File file;
		player =  playerInput;
		int tempValue;

		try
		{
			file = new File("LISA.txt");
			input = new Scanner(file, "UTF-8");
			exits = new int[DIRECTIONS];

			//Skips to the room entry
			for (int i = 0; i < roomNumber * FIELDS; i++)
			{
				input.nextLine();
			}

			//int number
			number = roomNumber;
			input.nextLine();

			//String name
			name = input.nextLine();

			//String description
			description = input.nextLine();


			//int[] exits

			String exitString = input.nextLine();

			readExits(exitString);
			
			String collectString = input.nextLine();
			
			readCollectables(collectString);

			input.close();

			//int[] items
			int[] tempItems = new int[200];

			for (int i = player[10]; i < player[11]; i++)
			{
				tempValue = player[i];

				if (tempValue != -2 && !(tempValue >=200 && tempValue < 300))
				{
					while (tempValue >= 100)
					{
						tempValue-=100;
					}


					if (tempValue == number)
					{
						tempItems[numItems] = i - player[10];
						numItems++;
						ItemObject thisItem = new ItemObject(i-player[10], player);
						
						if (thisItem.getType() == "i")
						{
							visibleItems++;
						}
					}
				}
			}

			items = new int[numItems];

			for (int i = 0; i < numItems; i++)
			{
				items[i] = tempItems[i];
			}

			//int[] npcs

			//npcs = new int[0];


		}catch(IOException e)
		{
			System.out.println("FILE NOT FOUND: LISA.TXT");
		}
	}

	public RoomObject (int roomNumber)
	{
		Scanner input;
		File file;
		player = new int[0];

		try
		{
			file = new File("LISA.txt");
			input = new Scanner(file, "UTF-8");

			//Skips to the room entry
			for (int i = 0; i < roomNumber * FIELDS; i++)
			{
				input.nextLine();
			}

			//int number
			number = roomNumber;
			input.nextLine();

			//String name
			name = input.nextLine();

			//String description
			description = input.nextLine();


		}catch (IOException e)
		{
		}
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

	public String getItems()
	{
		int totalItems = visibleItems;
		String output = "\n\nYou see a ";
		if (totalItems >= 2)
		{

			for (int i = 0; i < totalItems; i++)
			{
				ItemObject temp = new ItemObject(items[i], player);

				if(getState(items[i]) < 5)
				{
					output += temp.getName();
					if(getState(temp.getNumber()) == 1)
					{
						output += " (Flipped)";
					}else if(getState(temp.getNumber()) == 3)
					{
						output += " (Broken)";
					}

					if (i+2 < totalItems)
					{
						output += ", ";
					}else if (i+2 == totalItems)
					{
						output += " and a ";
					}else if (i+2 > totalItems)
					{
						output += ".";
					}
				}else if (i+2 > totalItems)
				{
					output += ".";
				}
			}
		}else if (totalItems == 1)
		{
			ItemObject temp = new ItemObject(items[0], player);

			if (temp.getState() != 2 && temp.getState() != 5)
			{
				output += temp.getName();
			}else
			{
				output = "";
			}
		}else
		{
			output = "";
		}
		return output;
	}

/*
	public String getNpcs()
	{
		String output = "";
		String properEnglish = "is";

		if (npcs.length > 1)
		{
			properEnglish = "are";
		}

		return output;
	}
*/
	public int getExit(int exitDirection)
	{
		return exits[exitDirection];
	}

	public String readExits()
	{
		String output = "\n\nObvious exits are ";
		String direction = "";
		int totalExits = 0;
		int[] validExits = new int[DIRECTIONS];

		for (int i = 0; i < exits.length; i++)
		{
			if(exits[i] >= 0 && player[exits[i]] >= 0)
			{
				validExits[totalExits] = i;
				totalExits++;
			}
		}
		
		if(totalExits > 1)
		{
			for (int i = 0; i < totalExits-1; i++)
			{
				direction = getDirection(validExits[i]);
				output+= direction;
				
			}
			
			output+= "and ";
			
			direction = getDirection(validExits[totalExits-1]);
			
			output+= direction + ".";
			
		}else if (totalExits == 1)
		{
			output+=(getDirection(validExits[0]) + "." );
		}else
		{
			output = "\n\nThere is no escape...";
		}

		return output;
	}

	private String getDirection(int value) {
		
		String direction = "";
		
		switch (value)
		{
			case 0: direction = "North ";
					break;
			case 1: direction = "South ";
					break;
			case 2: direction = "East ";
					break;
			case 3: direction = "West ";
					break;
			case 4: direction = "Up ";
					break;
			case 5: direction = "Down ";
					break;
			case 6: direction = "In ";
					break;
			case 7: direction = "Out ";
					break;
		}
		return direction;
	}

	public void readExits(String exitString)
	{
		String[] rawExits = new String[DIRECTIONS];
		Pattern p = Pattern.compile(":");
		rawExits = p.split(exitString, 0);

		for (int i = 0; i < DIRECTIONS; i++)
		{
			exits[i] = Integer.parseInt(rawExits[i]);
		}
	}

	public void readCollectables(String collectableString)
	{
		String[] rawCol = new String[100];
		Pattern p = Pattern.compile(":");
		rawCol = p.split(collectableString, 0);

		
		if (Integer.parseInt(rawCol[0]) != 0)
		{
			
			collectables = new int[Integer.parseInt(rawCol[0])];
			
			for (int i = 0; i < collectables.length; i++)
			{
				collectables[i] = Integer.parseInt(rawCol[i]);
			}
		}else
		{
			collectables = new int[1];
			collectables[0] = -1;
		}
	}
	
	public int getState(int itemFlag)
	{
		if (player[player[10]+itemFlag] >= 0)
		{
			itemFlag = player[player[10]+itemFlag] / 100;
		}else
		{
			itemFlag = player[player[10]+itemFlag];
		}

		if (itemFlag == -1)
		{
			itemFlag = 0;
		}

		return Math.abs(itemFlag);
	}
	
	public int findCollectable (int type)
	{
		int address = -1;
		
		if(collectables[0] > 0)
		{
			for (int i = 0; i < collectables.length; i++)
			{
				if (player[collectables[i]] == type)
				{
					address = collectables[i];
					i = collectables.length;
				}
			}
		}
		
		return address;
	}


	public String toString()
	{
		String output = ("You are in " + name + ".\n\n" + description + "." +
						getItems() + readExits());

		return output;
	}
}