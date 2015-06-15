/**
*/

import java.util.Scanner;
import java.io.*;
import java.util.regex.*;
import java.util.Random;

public class TheAdventure
{
	public static Scanner keyboard = new Scanner(System.in);
	public static int blank_count = 0;
	public static String saveFile = "save0.gg";
	public static int close_operation = 0;
	public static boolean debug = false;

	public static RoomObject thisRoom;

	public static int text_speed = 10;

	public static final int STATS = 20;
	public static final int ITEMS = 200;
	public static final int SPECIAL = 40;
	public static final int EXITS = 60;

	public static int FLAGS = STATS+ITEMS+SPECIAL+EXITS;

	public static int[] player = new int[FLAGS];
	public static String[] itemAliases = new String[ITEMS];

	public static NewGame newStart = new NewGame(FLAGS);

	public static void main (String[] args) throws IOException
	{
		PlayerCommand test;
		String input;
		int input_error = 0;

		player[10] = STATS;
		player[11] = STATS + ITEMS;
		player[12] = STATS + ITEMS + SPECIAL;

		says("1) New Game 2) New Game (No Intro) 3) Load 4) boatcat\n");
		System.out.print(">");

		input = keyboard.nextLine();

		do
		{
			switch(input)
			{
				case "1": input_error = 1;
						newStart.newPlayer(player);
						ItemEvents.displayEvent(11, 12, 13, 14);
						break;
				case "2": input_error = 1;
						newStart.newPlayer(player);
						break;
				case "3": input_error = 1;
						loadGame();
						break;
				case "4": debug = true;
						says("Debug mode activated");
						input = keyboard.nextLine();
						break;
				case "boatcat": input_error = 0;
						bogusSave();
						says("Bogus save file created. Good luck!");
						saveFile = "bogus.gg";
						says("");
						input = keyboard.nextLine();
						break;
				default: input = keyboard.nextLine();
			}
		} while (input_error == 0);

		input_error = 0;

		fillItems();

		buildRoom();

		//

		while ( close_operation != -1)
		{
			System.out.print(">>>");

			input = (keyboard.nextLine());

			input_error = checkCommand(input);

			if (input_error > 0)
			{
				test = new PlayerCommand(input);

				Commands.lookup(test);

				if (debug)
				{
					System.out.println("==== " + test.toString() + "====");
				}

			}else
			{
				switch (input_error)
				{
				case 0: says("Unspecified error. If you're getting this error, then I'm sorry to say that I have absolutely no idea what you did wrong, nor how you managed to get this error. So congradulations to you, and god speed.");
						break;
				case -1: switch (blank_count)
						{
						case 0: says("Invalid command. Because, you know, you didn't actually enter one");
								blank_count++;
								break;
						case 1: says("Invalid command. Yep, you still haven't typed anything.");
								blank_count++;
								break;
						case 2: says("Invalid command. Ok, so you hit enter. That doesn't count though.");
								blank_count++;
								break;
						case 3: says("\nValid command.                                                                    \n\nNah, just kidding. It's still an invalid command.");
								blank_count++;
								break;
						case 6: says("How do you like it when I type nothing?");
								blank_count++;
								break;
						case 9: says("Comgrabulations! You've won! As it turns out, the secret was just pressing enter!");
								blank_count++;
								break;
						case 20: says("...are you still there?");
								blank_count++;
								break;
						default: says(">");
								blank_count++;
								break;
						}
						break;
				case -2: says("Command too long. Whatever you're doing, you're doing it wrong. Last I checked there wasn't a single combination of words that is NEARLY as long as what you just tried. I mean, " + input.length() + " characters long. Really man?");
						break;
				case -3: says("Too many spaces. Stahp plz.");
						break;
				case -4: says("None of that, uhh, whatever that is!");
						break;
				case -5: says("Invalid command. Please type more than one space.");
						break;
				case -6: says("To many words! Whatever youre trying to say could probably be shortened a bit.");
						break;
				}
			}
		}

		/**/
	}

/**
Tests the input from the user and returns one of the many colorful
error messages if it's not valid.
@param input The input taken from the user.
@return int The number of the error.
*/
	private static int checkCommand (String input)
	{
		int error = 0;
		char test = ' ';

		if (input.length() == 0)
		{
			error = -1;
		}else if (input.length() >= 64)
		{
			error = -2;
		}else if (input.contains("  "))
		{
			error = -3;
		}else if (input.matches("!|@|#|$"))
		{
			error = -4;
		}else if (input.equals(" "))
		{
			error = -5;
		}else if (input.matches("(\\w+\\ ){8,}(.*)"))
		{
			error = -6;
		}else if (error == 0)
		{
			error = 1;
		}

		return error;
	}

	/**
	Outputs a string input to the user using word wrap and character
	delays to enhance the experience.
	@param String The string that the user wishes to print.
	*/
	public static void says(String text)
	{

		System.out.print("");
		int lastSpace = -1;
		int charCount = 0;
		char[] charText = text.toCharArray();

		for(int i = 0; i < charText.length; i++)
		{
			if (charCount < 78)
			{
				if (charText[i] == ' ')
				{
					lastSpace = i;
				}else if (charText[i] == '\n')
				{
					charCount = -1;
				}
				charCount++;
			}else if (lastSpace > 0)
			{
				i = lastSpace;
				charText[i] = '\n';
				charCount = 0;
				lastSpace = -1;
			}
		}

		text = String.valueOf(charText);

		try
		{
			for (int i = 0; i< text.length(); i++)
			{
				if(text.charAt(i) == '@')
				{
					Thread.sleep(text_speed*13);
				}else if (text.charAt(i) == '^')
				{
					pressAnyKeyToContinue();
				}else
				{
					Thread.sleep(text_speed);
					System.out.print(text.charAt(i));
				}
			}
		}catch (InterruptedException e)
		{
		}

		System.out.println("\n");
	}




	public static void says()
	{
		System.out.println();
	}


	/**
	Constructs a room object using the players location and prints the room information
	to the console.
	*/
	public static void buildRoom()
	{
		thisRoom = new RoomObject(player[1], player);
		says(thisRoom.toString());

		if (debug)
		{
			says("Room: " + thisRoom.getNumber() + " " + thisRoom.getName());
			says(thisRoom.getDescription());
			says("Exits " + thisRoom.readExits());
		}
	}
	/**
	Lists, verifies, and attempts to load player values from a file.
	*/
	public static void loadGame() throws IOException
	{
		File folder = new File(".");
		File[] fileList = folder.listFiles();
		String saveNames = "\t";
		int count = 0;
		int fileNum = -1;

		for (int i = 0; i < fileList.length; i++)
		{
			if (fileList[i].isFile() && fileList[i].toString().matches("^.*(\\.(gg|gG|GG|Gg)$)"))
			{
				saveNames += fileList[i].toString().replaceAll("^\\.", "");
				saveNames += "\n\t";
				count++;
				fileNum = i;
			}
		}

		if (count > 1)
		{
			says("The following saves were found:");
			says("Select a save number to load: \n" + saveNames);
			says(">");
			String input = keyboard.nextLine();

			if (input.equals("bogus.gg") || input.equals("bogus") || input.equals("b"))
			{
				buildCharacter("bogus.gg");
			}else
			{
				validNumber(input);
				buildCharacter("save" + input + ".gg");
			}
		}else if (count == 1)
		{
			says("Loading save file " + fileList[fileNum].toString() + ".");
			buildCharacter(fileList[fileNum].toString());
			//Actually load the file here at some point
		}else
		{
			says("No save data found. Starting a new game...");
		}
	}

	/**
	Outputs the player value array into a file to be loaded at a later time.
	*/

	public static void saveGame() throws IOException
	{
		PrintWriter output = new PrintWriter(saveFile);

		/*
		for (int i = 0; i < player.length; i++)
		{
			player[i]= i+1;
		}
		*/

		for (int i = 0; i < player.length; i++)
		{
			output.println(player[i]);
		}

		output.close();
	}

	/**
	Fills the player data array with values based upon a file supplied by loadGame()
	@param String The name of the file to load from.
	*/

	public static void buildCharacter(String filename) throws IOException
	{
		File file = new File(filename);
		boolean valid = validSave(filename);
		Scanner thisSave = new Scanner(file);

		//Pattern p = Pattern.compile("(\\d+\n){255,}");

		if (valid == true)
		{
			for (int i = 0; i < player.length; i++)
			{
				player[i] = thisSave.nextInt();
				thisSave.nextLine();
			}

			text_speed = player[13];
			says("Save successfully loaded.");

		}else
		{
			says("File is corrupt or invalid.");
		}

		thisSave.close();

	}

	/**
	Checks to see if the user input is a number.
	If it is not, it asks for a number until a valid number is provided.
	@param String The user input to test for a number.
	*/
	public static void validNumber(String input)
	{
		boolean valid = false;

		while (valid == false)
		{
			if (input.matches("\\d+"))
			{
				valid = true;
			}else
			{
				says("Invalid number. Valid numbers are between 0-9, and possibly even higher! It depends.");
				input = keyboard.nextLine();
			}

		}
	}

	/**
	Checks to see if a save file is valid based upon the expected value of FLAGS.
	@param String The name of the file to check.
	@ return Boolean Whether or not the file is valid.
	*/
	public static boolean validSave(String filename) throws IOException
	{
		File file = new File(filename);
		Scanner input = new Scanner(file);
		int count = 0;
		boolean valid = false;


		while (input.hasNextInt())
		{
			input.nextLine();
			count++;
		}


		if (count >= FLAGS)
		{
			valid = true;
		}

		input.close();
		return valid;
	}

	/**
	Creates a bogus save file. Used for testing or just to fuck with the game.
	*/
	public static void bogusSave()
	{
		Random random = new Random();

		for (int i = 0; i < player.length; i++)
		{
			player[i] = random.nextInt(2);
		}

		player[4] = 1500;
	}

	/**
	Fills an array with item aliases to be used when checking for valid commands.
	*/
	public static void fillItems()
	{
		File file;
		Scanner input;

		try
		{
			file = new File("ITEMS.txt");
			input = new Scanner(file);
			String lineParse;

			int i = 0;

			for (int j = 0; j < ITEMS; j++)
			{
				itemAliases[j] = "";
			}

			while (input.hasNext())
			{

				lineParse = input.nextLine();

				if (lineParse.contains(";"))
				{
					itemAliases[i] = lineParse;
					i++;
				}
			}

			if(debug)
			{
				says(i + " items read from ITEMS.TXT");

				for(int j = 0; j < i; j++)
				{
					says(itemAliases[j]);
				}
			}

			input.close();
		}catch (IOException e)
		{
			says("ITEMS.txt not found. IOException.");
		}
	}

	private static void pressAnyKeyToContinue()
	{
			System.out.println("Press enter to continue...");
	        keyboard.nextLine();
 	}
}