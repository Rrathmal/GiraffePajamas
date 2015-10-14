import java.io.*;
import java.util.Scanner;
//import java.util.regex.*;
import java.lang.Math;

public class Commands extends TheAdventure
{

	public static final int ITEMOFFSET = STATS;
	public static final int SPECIALOFFSEET = ITEMOFFSET + ITEMS;

	/**
	Takes the first word given by the user and tries to match it
	to a valid command.
	*/
	public static void lookup()
	{
		String first = god.getWord(0);

		if (first.equals("exit"))
		{
			exit();
		}else if (first.equals("delirious"))
		{
			delirious();
		}else if (first.equals("look") || first.equals("examine") || first.equals("x") || first.equals("check"))
		{
			look();
		}else if (first.equals("inventory") || first.equals("inv") || first.equals("bag") || first.equals("penis"))
		{
			inventory();
		}else if (first.equals("use") || first.equals("bestow") || first.equals("apply") || first.equals("toggle") || first.equals("give"))
		{
			use();
		}else if (first.equals("get") || first.equals("take") || first.equals("steal") || first.equals("pocket") || first.equals("china") || first.equals("pickup"))
		{
			get();
		}else if (first.equals("help") || first.equals("?"))
		{
			help();
		}else if (first.equals("go") || first.equals("goto") || first.equals("walk") || first.equals("climb") || first.equals("jump"))
		{
			go();
		}else if (first.equals("attack") || first.equals("break") || first.equals("smash") || first.equals("fbdc") || first.equals("slay") || first.equals("destroy") || first.equals("annihilate"))
		{
			//If the item does not contain a custom attack message, this will display the default "break" actions.

			if (!(customVerb("attack")))
			{
				attack();
			}
		}else if (first.equals("shoot") || first.equals("throw") || first.equals("toss") || first.equals("giveat"))
		{
			toss();
		}else if (first.equals("drop"))
		{
			drop();
		}else if (first.equals("sit"))
		{
			if (god.getWord(1).equals("on") || god.getWord(1).equals("in"))
			{
				god.setWord(3, god.getWord(2));
				god.setWord(2, "on");
				god.setWord(1, "me");
			}else
			{
				god.setWord(3, god.getWord(1));
				god.setWord(2, "on");
				god.setWord(1, "me");
			}

			use();
		}else if(first.equals("flip"))
		{
			if (!(customVerb("flip")))
				{
					flip();
				}
		}else if(first.equals("2+2"))
		{
			says("Nope.");
		}else if(first.equals("achievements") || first.equals("endings") || first.equals("ending"))
		{
			displaySession();
		}else if(first.equals("save"))
		{
			saveGame();
		}else
		{
			says("Command unrecognized: " + first);
		}

	}

	/**
	DEBUG COMMAND. Command handler for when the first word from the
	PlayerCommand is "delirious".
	*/
	public static void delirious()
	{
		String first = "delirious";

		String second = god.getWord(1);
		String third = god.getWord(2);

		says();

		if (second.equals("saveprint"))
		{
			for (int i = 0; i < player.length; i++)
			{
				says(String.valueOf(i) + ": " + player[i]);
			}
		}else if (second.equals("biznasty") || second.equals("biznasty.gif"))
		{
			says("Hella.");
		}else if (second.equals("debug"))
		{
			debug = !debug;
		}else if(second.equals("locate"))
		{
			god.wordShift(0);
			int itemNumber = itemLookup();
			if (itemNumber > 0)
			{
				String output = "";
				ItemObject temp = new ItemObject(itemNumber, player);
				RoomObject tempRoom;
				switch (temp.getLocation())
				{
					case -1: output += ("The item's in your inventory man. You should know that!");
							break;
					case 98: output +=("The item is in spaaaaaaaaaaccccceeeeeeeeee");
							break;
					default: tempRoom = new RoomObject(temp.getLocation(), player);
						output +=("The item is in room " + temp.getLocation() + " : " + tempRoom.getName());
				}

				if (getState(temp.getLocation()) == 1)
				{
					output +=(" AND you flipped it over.");
				}else if(getState(temp.getLocation()) == 2)
				{
					output += " AND it's stationary.";
				}else if(getState(temp.getLocation()) == 3)
				{
					output += " AND it's broken.";
				}else if(getState(temp.getLocation()) == 4)
				{
					output += " AND it's locked.";
				}else if(getState(temp.getLocation()) == 5)
				{
					output += " AND it's also not. How mysterious.";
				}

				says(output);
			}else
			{
				says("Item " + third + " does not exist. Sorry bro.");
			}
		}else if (second.equals("settextspeed") || second.equals("text-speed") || second.equals("textspeed"))
		{
			if (third.equals("default") || third.equals(""))
			{
				text_speed = 12;
				says("Text speed reset to default(" + text_speed + ").");
			}
			else
			{
				try
				{
					text_speed = Integer.parseInt(third);
					player[13] = text_speed;
					says("XXX Text speed set to " + text_speed + ".");
				}catch (NumberFormatException e)
				{
					says("Invalid speed \"" + third + "\". Please enter a number 0-1000. Or not, I mean you could go even higher. Who's going to stop you?");
				}
			}
		}else if (second.equals("set"))
		{
			set();
		}else if (second.equals("read"))
		{
			try
			{
				says("Player[" + god.getWord(2) + "] has a value of " + player[Integer.parseInt(god.getWord(2))] + ".");
			}catch(NumberFormatException e)
			{
				says("Invalid value.");
			}
		}else if(second.equals("iteminfo"))
		{
			itemInfo();
		}else if(second.equals("go"))
		{
			try
			{
				player[1] = Integer.parseInt(god.getWord(2));
				buildRoom();
			}catch(NumberFormatException e)
			{
				says("Invalid value.");
			}
		}else if (second.equals("fillitems"))
		{
			fillItems();
		}else if(second.equals("itemlist"))
		{
			itemList();
		}else
		{
			badRead(first);
		}
	}

	/**
	Command used for checking the players inventory. Currently, no seperate array is kept, so the whole player array is
	read every time. This is only balanced by the fact that this command is only called when the player wishes.
	*/
	public static void inventory()
	{
		ItemObject item;
		String output = "";
		int items = 0;

		says("You are currently carrying...\n");

		for (int i = 1; i < ITEMS; i++) //Set to 1 to avoid cataloging the player.
		{
			if (player[player[10]+i] < 0 && player[player[10]+i] > -5)
			{
				item = new ItemObject(i, player);
				output += "\t\t" + item.getNameState() + "\n";
				items++;
			}
		}

		if (items > 0)
		{
			says(output);
		}else
		{
			says("Nothing.\n\nOh wow, really? Well, I guess that's not true...");
			player[player[10]+7] = -1;
		}
	}

	/**
	The get command!

	*/
	private static void get()
	{
		
		if (god.getWord(1).equals("shirt") || god.getWord(1).equals("hat") || god.getWord(1).equals("dress") || god.getWord(1).equals("belt") || god.getWord(1).equals("boot") || god.getWord(1).equals("panda"))
		{
			getCollectable();
		}else
		{
			ItemObject target;
			int itemNumber = itemLookup();
			int stateOffset = -1;
			

			if (itemNumber >= 0)
			{
				target = new ItemObject(itemNumber, player);
				int location = target.getLocation();

				if (location < 0)
				{
					says("You drop the " + target.getName() + " and pick it up again. Yep, you already had it in your inventory.");
				}else if (compareLocation(target) == 0 && getState(itemNumber) != 5 && (target.isType("i") || (target.isType("l"))))
				{
					if (getState(itemNumber) == 3)
					{
						stateOffset = -3;
					}else if (getState(itemNumber) == 4)
					{
						stateOffset = -4;
					}

					says("You pickup the " + target.getNameState() + ".");
					player[(player[10]+itemNumber)] = stateOffset;

				}else
				{
					says("Try as you may, but I have no idea what you were trying to aquire there.");
				}
			}else
			{
				says("Try as you may, but I have no idea what you were trying to aquire there.");
			}
		}
	}


	private static void getCollectable() 
	{
		int type = -1;
		String output = "";
		
		switch(god.getWord(1))
		{
			case "shirt": type = 1;
				break;
			case "dress": type = 2;
				break;
			case "hat": type = 5;
				break;
			case "belt": type = 3;
				break;
			case "boot": type = 4;
				break;
		}
		
		int address = thisRoom.findCollectable(type);
		
		if (address > 0)
		{
			player[address] = 0;
			player[(3+type)]+=1;
			output+= "You find a " + god.getWord(1) + " and put it on. ";
			
			switch(god.getWord(1))
			{
				case "shirt": output+= "You feel much cooler now. Figuratively of course.@@@@@@@@ In truth this thing breeths like a loaf of bread.";
					break;
				case "dress": output+= "Your ability to block bullets has increased!";
					break;
				case "hat": output+= "Your gentlemanly rating has increased!@@@@@ You're still in the negatives regarding that however.";
					break;
				case "belt": output+= "Your heroics have increased by further tightening your waist line!";
					break;
				case "boot": output+= "You put on another boot?@@@@ Must have been a pretty big boot.";
					break;
			}
		}else
		{
			output+="Scour and search as you may, you can't find one lying around.";
		}
		
		says(output);
	}

	/**
	DEBUG COMMAND. Changes the player values to a specified value.
	Does not reprint the area, and can easy screw alot of things up.
	*/
	public static void set()
	{
		says();

		god.wordShift(0);

		if (god.getWord(2).equals("to"))
		{
			god.wordShift(2);
		}

		try
		{
			player[Integer.parseInt(god.getWord(1))] = Integer.parseInt(god.getWord(2));
			says("Player value " + god.getWord(1) + " set to " + god.getWord(2) + ".");
		}catch (NumberFormatException e)
		{
			says("Invalid set command.");
		}

	}

	/**
	Processes and handles any events stored in a ItemObject's
	use field. Defaults to being used on 0 (The Player) if no
	target is specified.
	*/
	public static void use()
	{
		ItemObject arrow;
		ItemObject target;
		int first;
		int second;

		says();

		first = itemLookup();

		if (first >= 0 && getState(first) < 5)
		{
			arrow = new ItemObject(first, player);

			if (god.getWord(2).equals("on"))
			{
				god.wordShift(2);
			}

			god.wordShift(1);

			//Changes the target to 'me' if nothing was specified.

			if (god.getWord(1).equals(""))
			{
				god.setWord(1, "me");
				second = 0;
			}else
			{
				second = itemLookup();
			}

			// says("DEBUG: Second: " + second);

			if (second >= 0)
			{
				target = new ItemObject(second, player);
				String[] useEvents = arrow.getUseEvents();
				int state = getState(arrow);

				if (state == 3)
				{
					says("Although you may be able to divine it's original purpose," +
							" using it in its current state would be impossible.");
				}else if (state == 4)
				{
					says("It's locked. You need a key (or a \"KEY\").");
				}else
				{
					if (state == 1)
					{
						says("You would, but, you already flipped the shit out of it. You'll have to flip it back over first.\n\nYes, it's silly, but so was flipping it over in the first place.");
					}else
					{
						for(int i = 0; i < useEvents.length; i++)
						{
							ItemEvents.itemEvent(useEvents[i], target.getNumber());
						}
					}
				}

			}else
			{
				says("I don't understand what " + god.getWord(2) + " is.");
			}




		}else if (getState(first) == 3)
		{
			says("You can't use that, it's broken. And probably by you no less!");
		}else
		{
			says("I don't understand what " + god.getWord(1) + " is.");
			if (debug)
			{
				says(god.getWord(1) + " is in state: Inaccessable.");
			}
		}
	}

	/**
	The flip command
	 ""
	*/
	
	private static void flip()
	{
		int itemNumber = itemLookup();
		
		
		if (itemNumber > 0)
		{
			ItemObject table = new ItemObject(itemNumber, player);
			if (table.isHere())
			{
				switch (getState(itemNumber))
				{
					case 0: says("You take a deep breath and --HHHGGGGRRRRAAAAAAGGGGGAAAHHHAHGAHGAGAHAAGAHGAAHAAAAAA!!!! You flip the living (or possibly not) beejesus's out of the " + table.getName() + ".\nYour RAGE has decreased by 1.");
							player[player[10]+itemNumber] = TheAdventure.thisRoom.getNumber()+100;
							player[9]+=1;
							player[15]-=1;
							break;
					case 1: says("You take a deep breath and --H@H@H@@@@@@@@@@@@@@ Oh wait a minute, @@@@@you already flipped this over.@@@@@@@ Thinking calmly for a second, you take the " + table.getName() + " and set it back up.@@@@ Or @@@@set it to whatever a \"non-flipped\" position would be.");
							unflip(table);
							break;
					case 2: says("You take a deep breath and --HHHGGGGRRRRAAAAAAGGGGGAAAHHHAHGAHGAGAHAAGAHGAAHAAAAAA!!!! You flip the living (or possibly not) beejesus's out of the " + table.getName() + ".\nYour RAGE has decreased by 1.");
							player[player[10]+itemNumber] = TheAdventure.thisRoom.getNumber()+100;
							player[9]+=1;
							player[15]-=1;
							break;
					case 3:	says("You take a deep breath and --H@H@AAAAAA!!!!!!!@@@@@@@@@@@\n\nYou flip one of the broken peices into the air!@@@@\n\nIt does one\n\n@@No two@@@@,\n\nHoly moley!@@@ \n\n17 consecutive flips before stciking the landing perfectly!@@@@@@\nThe crowd goes wild!!@@@@@@@@\nLadies and gentlemen@@ what you've seen here will be remembered for generations!@@@@@@@@\n\n(Not really,@@@ also it's still broken)");
							break;
					case 4: says("You take a deep breath and --HHHGGGGRRRRAAAAAAGGGGGAAAHHHAHGAHGAGAHAAGAHGAAHAAAAAA!!!! You flip the living (or possibly not) beejesus's out of the " + table.getName() + ".\nYour RAGE has decreased by 1. Also it's still locked.");
							player[player[10]+itemNumber] = TheAdventure.thisRoom.getNumber()+100;
							break;
					default: says("Wait, what?");
				}
			}
		}
		
	}
	
	private static void unflip(ItemObject table)
	{
		if(table.isType("i") || table.isType("p"))
		{
			player[table.p()] = thisRoom.getNumber();
		}else if (table.isType("f"))
		{
			player[table.p()] = thisRoom.getNumber() + 200;
		}else if (table.isType("l"))
		{
			player[table.p()] = thisRoom.getNumber() + 400;
		}
	}
	
	/* Old code. Archived becasue I'm an idiot
	public static void flip()
	{
		ItemObject arrow;
		int first;

		first = itemLookup();

		if (first >= 0)
		{
			arrow = new ItemObject(first, player);

			if (arrow.getState() != 1)
			{
				if (arrow.getState() == 3)
				{
					says("In \"another\" fit of anger, you flip the " + arrow.getName() + " over. Although it made little difference, as it still lays in pieces.");
				}else if(arrow.getState() != 5)
				{
					says("You grab the " + arrow.getName() + " and flip it's shit. YYYEEEEEEEAAAAAAAHHHHHH!!!!!");
					player[arrow.p()] = thisRoom.getNumber() + 100;
					player[9]++;
				}
			}else
			{
				if(arrow.isType("i") || arrow.isType("p"))
				{
					player[arrow.p()] = thisRoom.getNumber();
				}else if (arrow.isType("f"))
				{
					player[arrow.p()] = thisRoom.getNumber() + 200;
				}else if (arrow.isType("l"))
				{
					player[arrow.p()] = thisRoom.getNumber() + 400;
				}

				says("You calmly pick the " + arrow.getName() + " back up, and place it into it's original location. Looking back: the whole ordeal was very therapudic.");
			}
		}
	}
	
	*/

	public static void toss()
	{
		ItemObject arrow;
		//ItemObject target;
		int itemNumber = itemLookup();


		if (itemNumber >= 0)
		{
			arrow = new ItemObject(itemNumber, player);

			if (arrow.hasVerb("throw"))
			{
				customVerb("throw");
			}else
			{
				says("Oh you're strong. But you're not THAT strong.");
			}
		}
	}

	/**
	The drop command!
	*/
	public static void drop()
	{
		ItemObject target;
		int playerLocation = thisRoom.getNumber();
		int itemNumber = itemLookup();
		int stateOffset = 0;
		int location;

		if (itemNumber >= 0)
		{
			target = new ItemObject(itemNumber, player);
			location = target.getLocation();

			if (location < 0 && !(target.isType("s")))
			{
				if(location == -3)
				{
					stateOffset = 300;
				}else if (location == -4)
				{
					stateOffset = 400;
				}

				says("You drop the " + target.getName() + ".");
				player[(player[10]+itemNumber)] = playerLocation + stateOffset;

			}else if (target.isType("s"))
			{
				says("Unlike you, that's already quite grounded.");
			}else
			{
				says("It's quite hard to drop something when you don't have it on your person.");
			}
		}
	}


	/**
	Displays a help message based upon the player input.
	 The processed player input.
	*/
	public static void help()
	{
		File file;
		Scanner input;
		String readLine;
		boolean read = false;

		try
		{
			file = new File ("HALP.txt");
			input = new Scanner(file, "UTF-8");

			while(input.hasNext() && !read)
			{
				readLine = input.nextLine();
				if (readLine.equals("h-" + god.getWord(1)))
				{
					says(input.nextLine());
					read = true;
				}else
				{
					input.nextLine();
				}

			}

		}catch(IOException e)
		{
			says("Help! The help file is gone! I can't trust anyone anymore!");
		}
	}

	/**
	Processes and displays events for custom verbs (Anything besides use)
	for an item.
	@param String The verb name.
	 The processed player input.
	*/
	
	/*Error message reporting due to the verb not being present on the item presented is up to the command
		for handling what needs to be said. AKA if I don't write a tailored message to the command, the 
		console will just report nothing.*/
	
	public static boolean customVerb(String verb)
	{
		ItemObject arrow;
		ItemObject target;
		int firstItem = -1;
		int secondItem = -1;
		boolean valid = false;

		says();

		firstItem = itemLookup();

		if(firstItem >= 0)
		{

			arrow = new ItemObject(firstItem, player);

			if (arrow.getLocation() > -5)
			{
				if (god.getWord(2).equals("me"))
				{
					System.out.println("WORD 3 : ");
					target = new ItemObject(0, player);
					secondItem = 0;
				}else if (god.getWord(2).equals(""))
				{
					target = new ItemObject(0, player);

					secondItem = 0;

				}else
				{

					god.wordShift(0);
					secondItem = itemLookup();
					if(secondItem >= 0)
					{
						target = new ItemObject(secondItem, player);
					}else
					{
						target = new ItemObject(0, player);
						says("I understood that you wanted to use the " + arrow.getName() + " to " + verb + ", but I have no idea what a " + god.getWord(1) + " is.");
						secondItem = -1;
					}
				}

				if (secondItem >= 0)
				{
					valid = ItemEvents.itemVerbEvents(verb, arrow, target);
				}
			}else
			{
				says("What is a " + arrow.getName() + "?");
			}
		}else
		{
		}

		return valid;
	}

	/**
	The look command. Examines the surroundings unless an item parameter is specified by the user.
	*/
	public static void look()
	{
		if (god.getWord(1).equals("at"))
		{
			god.wordShift(1);
		}

		says();

		String second = god.getWord(1);

		if (second.equals(""))
		{
			says("You look to your surroundings...");
			buildRoom();
		}else
		{
			int temp = itemLookup();

			if (temp >= 0 && getState(temp) != 5)
			{
				ItemObject thisItem = new ItemObject(temp, player);

				says("You inspect the " + thisItem.getName() + "...");
				says(thisItem.getDescription());

				switch (getState(thisItem))
				{
					case 1: says("You seem to have flipped it's shit. Good job keeping your cool.");
							break;
					case 2: says("It would be hard to move it from where it is.");
							break;
					case 3: says("It appears to be broken.");
							break;
					case 4: says("It appears to be locked.");
							break;
				}

			}else
			{
				badRead(second);
			}
		}
	}
	/**

	*/
	public static void go()
	{
		says();
		String second = god.getWord(1);
		int direction = -1;

		//Space ending. Currently does not end the game
		if (second.equals("space"))
		{
			ItemEvents.displayEvent(0);
		}

		switch (second)
		{
			case "north": direction = 0;
						break;
			case "south": direction = 1;
						break;
			case "east": direction = 2;
						break;
			case "west": direction = 3;
						break;
			case "up": direction = 4;
						break;
			case "down": direction = 5;
						break;
			case "in": direction = 6;
						break;
			case "out": direction = 7;
						break;
		}
		if (direction > -1)
		{
			int destination = thisRoom.getExit(direction);

			if (destination > -1 && player[destination] >= 0)
			{
				says("You go " + second + ".");
				
				//Stall printing in the console by 4 letters
				says("@@@@");
				
				//Change the location of the player
				player[1] = player[destination];
				
				//Increase the time by 1 minute
				timeTravel(); 
				
				//Rebuild room
				buildRoom();
			}else if(destination < 0 || player[destination] == -1)
			{
				says("It seems to be locked. Closer inspection reveals that it is, indeed, locked.");
			}else
			{
				says("You find no way to proceed in that direction. At least not on this plane of existence.");
			}

		}else
		{
			says("You find no way to proceed in that direction. At least not on this plane of existence.");
		}
	}

	public static void exit()
	{
		close_operation = -1;
	}
	

	/**
	Looks up and prints the toString method for a given item in a PlayerCommand
	*/
	public static void itemInfo()
	{
		god.wordShift(0);
		int item = itemLookup();
		ItemObject temp;

		if (item >= 0)
		{
			temp = new ItemObject(item, player);
			says(temp.toString());
			says(temp.getValidVerbs());
		}else
		{
			says("Invalid item name.");
		}
	}

	/**
	Finds an item number based upon the given alias by the user.
	 The processed input from the user
	@return Int The item number that the command corresponds too.

	Version 2

	*/
	public static int itemLookup()
	{
		int itemNumber = -1;
		int wordCount = 1;
		int match = 0;
		int tempArray[] = new int[200];
		String itemName = "";

		if (god.getWord(1).equals(""))
		{
			return 0;
		}

		for (int i = 2; i < 5; i++)
		{
			if (god.getWord(i).equals(""))
			{
				i = 5;
			}else
			{
				wordCount++;
			}
		}

		while (match < 1 && wordCount > 0)
		{
			itemName = "";

			if (debug)
			says ("match " + match + " wordcount " + wordCount);

			for (int i = 0; i < wordCount; i++)
			{
				itemName += god.getWord(i+1);
				if (i+1 < wordCount)
				{
					itemName += " ";
				}
			}

			if (debug)
			{
				says("Beginning lookup for: " + itemName);
			}

			for (int i = 0; i < itemAliases.length; i++)
			{
				if (itemAliases[i].contains(itemName))
				{
					if (match == 0)
					{
						itemNumber = i;
						tempArray[match] = i;
					}else
					{
						tempArray[match] = i;
					}
					match++;
				}
			}

			if (debug)
			{
				says(match + " matches found on the original search.");
			}

			if (match > 1)
			{

				if (debug)
				{
					says("Refining search...");
				}

				ItemObject temp;
				int tempMatch = 0;

				for (int i = 0; i < match; i++)
				{
					temp = new ItemObject(tempArray[i], player);

					if(compareLocation(temp) == 0 && temp.getState() != 5)
					{
						tempArray[tempMatch] = tempArray[i];
						tempMatch++;
					}else
					{
						match--;
					}

				}

				if (debug)
				{
					says(match + " matches found in your location.");
				}


				if (match > 1)
				{
					//If there are multiple matches after checking location...
					String output = "You'll have to be more specific, do you mean ";

					for (int i = 0; i < match; i++)
					{
						temp = new ItemObject(tempArray[i], player);
						output += temp.getName();

						if (i+2 < match)
						{
							output += ", ";
						}else if (i+1 < match)
						{
							output += ", or ";
						}else
						{
							output += "? ('cancel' to cancel.)";
						}
					}

					says(output);

					output = keyboard.nextLine();

					if (!(output.equals("cancel")))
					{
						itemNumber = -1;
						wordCount = 0;
						match = 1;
						String verb = god.getWord(0);

						says(god.toString());

						god = new PlayerCommand(verb + " " + output);

						says(god.toString());

						if (debug)
							says("Recursion! New PlayerCommand " + god.toString());

						itemNumber = itemLookup();

					}else
					{
						itemNumber = -2;
					}

				}


			}else if (match == 1)
			{

				if (debug)
					says("Single match item number: " + itemNumber);

				for (int i = 1; i < wordCount; i++)
				{
					god.setWord(1, itemName);
					god.wordShift(2);
				}

				return itemNumber;

			}else
			{
				if (debug)
					says("Match: " + match + " is < 1");
			}

			wordCount--;


		}

		return itemNumber;

	}
	/*

	Old itemLookup command.

	public static int itemLookup()
	{
		int itemNumber = -1;
		int aliasMatch = 0;
		int[] matches = new int[ITEMS];
		String second = god.getWord(1);
		String third = god.getWord(2);
		String fourth = god.getWord(3);

		for (int i = 0; i < itemAliases.length; i++)
		{
			if (itemAliases[i].contains(second))
			{
				if (aliasMatch < 1)
				{
					itemNumber = i;
					matches[aliasMatch] = i;
				}else
				{
					matches[aliasMatch] = i;
				}
				aliasMatch++;
			}
		}

		if (aliasMatch > 1)
		{
			int total_matches = aliasMatch;
			aliasMatch = 0;
			ItemObject temp;

			for (int i = 0; i < total_matches; i++)
			{
				if (player[player[10]+matches[i]] == thisRoom.getNumber() || (player[player[10]+matches[i]] < 0 && player[player[10]+matches[i]] > -5))
				{
					matches[aliasMatch] = matches[i];
					aliasMatch++;
				}
			}

			if (aliasMatch == 1)
			{
				itemNumber = matches[0];
			}else if (aliasMatch > 1)
			{
				String output = "You'll have to be more specific, do you mean ";

				for (int i = 0; i < aliasMatch; i++)
				{
					temp = new ItemObject(matches[i], player);
					output += temp.getName();
					if (i+2 < aliasMatch)
					{
						output += ", ";
					}else if (i+1 < aliasMatch)
					{
						output += "', or";
					}else
					{
						output += ".";
					}
				}

				aliasMatch = -1;
			}
		}

		if (!(third.equals("")))
		{
			second += " " + third;

			for (int i = 0; i < itemAliases.length; i++)
			{
				if (itemAliases[i].contains(second))
				{
					god.wordShift(1);
					god.setWord(1, second);
					itemNumber = i;
					matches[aliasMatch] = i;
				}else
				{
					matches[aliasMatch] = i;
				}
				aliasMatch++;
				}
			}
		if (aliasMatch > 1)
		{
			int total_matches = aliasMatch;
			aliasMatch = 0;
			ItemObject temp;

			for (int i = 0; i < total_matches; i++)
			{
				if (player[player[10]+matches[i]] == thisRoom.getNumber() || (player[player[10]+matches[i]] < 0 && player[player[10]+matches[i]] > -5))
				{
					matches[aliasMatch] = matches[i];
					aliasMatch++;
				}
			}

			if (aliasMatch == 1)
			{
				itemNumber = matches[0];
			}else if (aliasMatch > 1)
			{
				String output = "You'll have to be more specific, do you mean ";

				for (int i = 0; i < aliasMatch; i++)
				{
					temp = new ItemObject(matches[i], player);
					output += temp.getName();
					if (i+2 < aliasMatch)
					{
						output += ", ";
					}else if (i+1 < aliasMatch)
					{
						output += "', or";
					}else
					{
						output += ".";
					}
				}
			}
		}


		}
		if (!(fourth.equals("")))
		{
			second += " " + fourth;

			for (int i = 0; i < itemAliases.length; i++)
			{
				if (itemAliases[i].contains(second))
				{
					god.wordShift(1);
					god.wordShift(1);
					god.setWord(1, second);
					return i;
				}
			}
		}else
		{
		}

		return itemNumber;
	}

	*/


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
	public static int getState(int itemFlag)
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

	/**
	Returns the itemstate for the specified item.
	@param ItemObject The ItemObject for the item you want to check.
	@return Int The item state.
	*/
	public static int getState(ItemObject item)
	{
		int itemFlag;

		if (player[player[10]+item.getNumber()] >= 0)
		{
			itemFlag = player[player[10]+item.getNumber()] / 100;
		}else
		{
			itemFlag = player[player[10]+item.getNumber()];
		}

		if (itemFlag == -1)
		{
			itemFlag = 0;
		}

		return Math.abs(itemFlag);
	}
	
	/**
	 * Debug command which, against my better judgment, creates an item object for every available item and prints it's location.
	 */
	private static void itemList()
	{
		String output = "";
		ItemObject objectItem;
		
		for (int i = player[10]; i < player[11] && player[i] != 98; i++)
		{
			objectItem = new ItemObject(i-player[10], player);
			output+=objectItem.toString()+"\n\n";
		}
		
		says(output);
	}


	/**
	Displays a generic error message for when a word cannot be read
	properly. It's debatable if this will get used that ofted,
	as I have a disposition towards custom messages.
	@param String The word that could not be read.
	*/
	public static void badRead(String thisWord)
	{
		says("I understood what you were saying, up until " + thisWord + ". Other than that though, I got nothing.");
	}

	/**
	The default attack command. Sets an items state to broken and displays a message if a custom command wasn't already found.
	*/
	public static void attack()
	{
		ItemObject target;
		int firstItem = -1;

		firstItem = itemLookup();

		if(firstItem >= 0)
		{
			target = new ItemObject(firstItem, player);
			int loc = target.getLocation();

			if (loc < 500 && loc > -5)
			{
				says("While it takes a bit of effort on your end, you attack and break the " + target.getName() + ". You feel satisfied. Rage -1.");
				player[target.p()] = thisRoom.getNumber()+300;
			}
		}
	}

	/**
	Checks to see if an item is in the current room, or the players inventory.
	@param ItemObject The ItemObject for the item being checked.
	@return int The locational difference between the two.
	*/
	public static int compareLocation(ItemObject item)
	{
		int location = item.getLocation();
		int playerLocation = player[1];
		int stateOffset = getState(item);
		int comparison = -1;

		if (location < 0 && location > -5)
		{
			comparison = 0;
		}else
		{
			location -= stateOffset * 100;

			comparison = location - playerLocation;
		}

		return comparison;

	}


}