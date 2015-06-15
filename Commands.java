import java.io.*;
import java.util.Scanner;
import java.util.regex.*;
import java.lang.Math;

public class Commands extends TheAdventure
{

	public static final int ITEMOFFSET = STATS;
	public static final int SPECIALOFFSEET = ITEMOFFSET + ITEMS;

	/**
	Takes the first word given by the user and tries to mathc it
	to a valid command.
	@param PlayerCommand The processed player input.
	*/
	public static void lookup(PlayerCommand god)
	{
		String first = god.getWord(0);

		if (first.equals("exit"))
		{
			exit();
		}else if (first.equals("delirious"))
		{
			delirious(god);
		}else if (first.equals("look") || first.equals("examine") || first.equals("x") || first.equals("check"))
		{
			look(god);
		}else if (first.equals("use") || first.equals("bestow") || first.equals("apply") || first.equals("toggle") || first.equals("give"))
		{
			use(god);
		}else if (first.equals("get") || first.equals("take") || first.equals("steal") || first.equals("pocket") || first.equals("china") || first.equals("pickup"))
		{
			get(god);
		}else if (first.equals("help") || first.equals("?"))
		{
			help(god);
		}else if (first.equals("go") || first.equals("goto") || first.equals("walk") || first.equals("climb") || first.equals("jump"))
		{
			go(god);
		}else if (first.equals("attack") || first.equals("break") || first.equals("smash") || first.equals("fbdc") || first.equals("slay") || first.equals("destroy") || first.equals("annihilate"))
		{
			//If the item does not contain a custom attack message, this will display the default "break" actions.

			if (customVerb("attack", god))
			{
				attack(god);
			}
		}else if (first.equals("shoot") || first.equals("throw") || first.equals("toss") || first.equals("giveat"))
		{
			customVerb("throw", god);
		}else if (first.equals("drop"))
		{
			drop(god);
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

			use(god);
		}else if(first.equals("flip"))
		{
			flip(god);
		}else
		{
			says("Command unrecognized: " + first);
		}

	}

	/**
	DEBUG COMMAND. Command handler for when the first word from the
	PlayerCommand is "delirious".
	@param PlayerCommand The processed player input.
	*/
	public static void delirious(PlayerCommand god)
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
			debug = true;
		}else if(second.equals("locate"))
		{
			god.wordShift(0);
			int itemNumber = itemLookup(god);
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
			set(god);
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
			itemInfo(god);
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
		}else
		{
			badRead(first);
		}
	}

	/**
	The get command!
	@param PlayerCommand
	*/
	public static void get(PlayerCommand god)
	{
		ItemObject target;
		int itemNumber = itemLookup(god);
		int stateOffset = -1;

		if (itemNumber >= 0)
		{
			target = new ItemObject(itemNumber, player);
			int location = target.getLocation();

			if (location < 0)
			{
				says("You drop the " + target.getName() + " and pick it up again. Yep, you already had it in your inventory.");
			}else if (location == thisRoom.getNumber() || getState(itemNumber) != 5)
			{
				if (getState(itemNumber) == 3)
				{
					stateOffset = -3;
				}else if (getState(itemNumber) == 4)
				{
					stateOffset = -4;
				}

				if (getState(itemNumber) == 2)
				{
					says("You got deep pockets, but they aren't THAT deep.");
				}else
				{
					says("You pickup the " + target.getName() + ".");
					player[(player[10]+itemNumber)] = stateOffset;
				}
			}else
			{
				says("Try as you may, but I have no idea what you were trying to aquire there.");
			}
		}else
		{
			says("Try as you may, but I have no idea what you were trying to aquire there.");
		}
	}


	/**
	DEBUG COMMAND. Changes the player values to a specified value.
	Does not reprint the area, and can easy screw alot of things up.
	@param PlayerCommand The processed player input.
	*/
	public static void set(PlayerCommand god)
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
	@param PlayerCommand The processed player input.
	*/
	public static void use(PlayerCommand god)
	{
		ItemObject arrow;
		ItemObject target;
		int first;
		int second;

		says();

		first = itemLookup(god);

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
				second = itemLookup(god);
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
				}else if (state == 5)
				{
					says("I don't understand what " + god.getWord(1) + " is. ");
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
				says("I don't understand what " + god.getWord(1) + " is.");
			}




		}else if (getState(first) == 3)
		{
			says("You can't use that, it's broken. And probably by you no less!");
		}else
		{
			says("I don't understand what " + god.getWord(1) + " is.");
		}
	}

	/**
	The flip command
	@param PlayerCommand ""
	*/
	public static void flip(PlayerCommand god)
	{
		ItemObject arrow;
		int first;

		first = itemLookup(god);

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

	/**
	The drop command!
	@param PlayerCommand ""
	*/
	public static void drop(PlayerCommand god)
	{
		ItemObject target;
		int playerLocation = thisRoom.getNumber();
		int itemNumber = itemLookup(god);
		int stateOffset = 0;
		int location;

		if (itemNumber >= 0)
		{
			target = new ItemObject(itemNumber, player);
			location = target.getLocation();

			if (location < 0)
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

			}else
			{
				says("It's quite hard to drop something when you don't have it on your person.");
			}
		}
	}


	/**
	Displays a help message based upon the player input.
	@param PlayerCommand The processed player input.
	*/
	public static void help(PlayerCommand god)
	{
		says();
	}

	/**
	Processes and displays events for custom verbs (Anything besides use)
	for an item.
	@param String The verb name.
	@param PlayerCommand The processed player input.
	*/
	public static boolean customVerb(String verb, PlayerCommand god)
	{
		ItemObject arrow;
		ItemObject target;
		int firstItem = -1;
		int secondItem = -1;
		boolean invalid = true;

		says();

		for (int i = 0; i < 8; i++)
		{
			if (god.getWord(i).equals("with")) //Any other reversal words go here.
			{
				String temp = god.getWord(i-1);

				god.setWord(i-1, god.getWord(i+1));
				god.setWord(i+1, temp);
				god.wordShift(i);
			}

			if (god.getWord(i).equals("on") || god.getWord(i).equals("at")) //Any other combining words go here.
			{
				god.wordShift(i);
			}
		}

		firstItem = itemLookup(god);

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

					if (verb.equals("throw") && compareLocation(arrow) == 0)
					{
						int stateOffset = 0;
						if (getState(arrow.getLocation()) == 3 || getState(arrow.getLocation()) == 4)
						{
							stateOffset = (getState(arrow.getLocation())) * 100;
						}

						says("You throw the " + arrow.getName() + ". While it gets some good distance, it eventually comes to rest on the floor.");
						player[(player[10]+firstItem)] = thisRoom.getNumber()+stateOffset;
						secondItem = -1;
					}else
					{
						secondItem = 0;
					}

				}else
				{

					god.wordShift(0);
					secondItem = itemLookup(god);
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
					ItemEvents.itemVerbEvents(verb, arrow, target);
					invalid = false;
				}
			}else
			{
				says("What is a " + arrow.getName() + "?");
			}
		}else
		{
			says("You're angry, I get that. But no matter how hard you try you can't " + verb + " " + god.getWord(1) + " because it doesn't exist!");
		}

		return invalid;
	}

	/**

	*/
	public static void look(PlayerCommand god)
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
			int temp = itemLookup(god);

			if (temp >= 0 && getState(temp) != 5)
			{
				ItemObject thisItem = new ItemObject(temp, player);

				says("You inspect the " + thisItem.getName() + "...");
				says(thisItem.getDescription());

				switch (getState(thisItem))
				{
					case 1: says("You seem to have flipped it's shit. Good job keeping your cool.");
							break;
					case 3: says("It appears to be broken.");
							break;
					case 4: says("It appears to be locked.");
							break;
					case 5: says("You shouldn't be here...");
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
	public static void go(PlayerCommand god)
	{
		says();
		String second = god.getWord(1);
		int direction = -1;

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

			if (destination > -1)
			{
				says("You go " + second + ".");
				says();
				player[1] = destination;
				buildRoom();
			}else if(destination == -1)
			{
				says("It seems to be locked. Closer inspection reveals that it is, indeed, locked.");
			}else if(destination == -2)
			{
				says("Why don't you go " + second + "? Oh right, you can't. destination -2");
			}else
			{
				says("You've been messsing around with values haven't you?");
			}

		}else
		{
			says("Why don't you go " + second + "? Oh right, you can't. direction -1");
		}
	}

	public static void exit()
	{
		close_operation = -1;
	}

	/**
	Looks up and prints the toString method for a given item in a PlayerComand
	@param PlayerCommand ""
	*/
	public static void itemInfo(PlayerCommand god)
	{
		god.wordShift(0);
		int item = itemLookup(god);
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
	@param PlayerCommand The processed input from the user
	@return Int The item number that the command corresponds too.

	Version 2

	*/
	public static int itemLookup(PlayerCommand god)
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
			if (god.getWord(i).equals("") || god.getWord(i).equals("with") || god.getWord(i).equals("on"))
			{
				i = 5;
			}else
			{
				wordCount++;
			}
		}

		while (match < 1)
		{

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

			if (match > 1)
			{
				//If there are multiple matches...
				String output = "You'll have to be more specific, do you mean ";
				ItemObject temp;

				for (int i = 0; i < match; i++)
				{
					temp = new ItemObject(tempArray[i], player);
					output += temp.getName();

					if (i+2 < match)
					{
						output += ", ";
					}else if (i+1 < match)
					{
						output += "', or";
					}else
					{
						output += "?";
					}
				}

				says(output);

				output = keyboard.nextLine();

				PlayerCommand recursiveLoop = new PlayerCommand(output);
				recursiveLoop.verbShift(god.getWord(0));

				itemNumber = itemLookup(recursiveLoop);


			}else if (match == 1)
			{
				for (int i = 1; i < wordCount; i++)
				{
					god.setWord(1, itemName);
					god.wordShift(2);
				}
			}

			wordCount--;

		}

		return itemNumber;

	}
	/*

	Old itemLookup command.

	public static int itemLookup(PlayerCommand god)
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
	@param PlayerCommand blahblahblah
	*/
	public static void attack(PlayerCommand god)
	{
		ItemObject target;
		int firstItem = -1;

		firstItem = itemLookup(god);

		if(firstItem >= 0)
		{
			target = new ItemObject(firstItem, player);
			int loc = target.getLocation();

			if (loc < 500 && loc > -5)
			{
				says("While it takes a bit of effort on your end, you attack and break the " + target.getName() + ". Nice job hero.");
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