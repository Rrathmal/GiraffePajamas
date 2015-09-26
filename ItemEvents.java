import java.io.*;
import java.util.Scanner;
//import java.util.regex.*;

public class ItemEvents extends Commands
{
	/**
	Outputs and event to the console based upon the given event
	number.
	@param Int The event number
	*/
	public static void displayEvent(int event)
	{
		File file;
		Scanner input;
		String eventCheck;

		try
		{
			file = new File("FLUFF.txt");
			input = new Scanner(file, "UTF-8");


			for (int i = 0; i < ((event*4)+2); i++)
			{
				if (debug)
				{
					says("Skip line" + i);
				}

				input.nextLine();
			}

			eventCheck = input.nextLine();

			input.close();

			says(eventCheck.replaceAll("\\\\n", "\n"));

		}catch(IOException e)
		{
		}
	}

	/**
	Outputs and event to the console based upon the given event
	number.
	@param Int[] The events being played.
	*/
	public static void displayEvent(int... events)
	{
		File file;
		Scanner input;
		String eventCheck = "";

		try
		{
			file = new File("FLUFF.txt");

			for (int j = 0; j < events.length;j++)
			{
				input = new Scanner(file, "UTF-8");


				for (int i = 0; i < ((events[j]*4)+2); i++)
				{
					if (debug)
					{
						says("Skip line" + i + " " + input.nextLine());
					}else
					{
						input.nextLine();
					}
				}

				eventCheck += input.nextLine();

				input.close();

			}

			says(eventCheck);

		}catch(IOException e)
		{
		}
	}

	/**
	Takes an event string and a target item number and prossess
	any events associated with the two.
	SEE ITEMS.txt for command definitions.
	@param String The raw event string.
	@param Int The number of the item that's being used as a target.
	*/
	public static void itemEvent(String rawEvent, int item)
	{
		String[] events = readEvent(rawEvent);
		int eventCount = 0;
		boolean genericTarget = true;

		if (debug)
		{
			says("events length = " + events.length);
			says("itemEvent: Item number: " + item);
		}

		/*

		System.out.println("RAW EVENT: " + rawEvent);
		System.out.println("ITEM NUMB: " + item);

		*/

		if (debug)
		{
			String output = "Events: ";

			for (int i = 0; i < events.length; i++)
			{
				output += events[i] + " ";
			}

			says(output);
		}

		for (int i = 0; i < events.length; i++)
		{
			if (events[i].equals("i"))
			{
				int first = player[Integer.parseInt(events[i+1])];
				int second;

				if (events[i+2].equals("l"))
				{
					second = Integer.parseInt(events[i+3]);
					i++;
				}else
				{
					second = player[Integer.parseInt(events[i+2])];
				}


				if (first == second)
				{
					if (debug)
						says("match");

					i += 2;
				}else
				{
					while (i+1 < events.length && !( ( events[i+1].equals("i") ) || ( events[i+1].equals("T") ) ) )
					{
						if (debug)
							says("skip event " + i + ": " + events[i]);

						i++;
					}
				}

			}else if(events[i].equals("T"))
			{
				int targetItem;

				if (events[i+1].equals("-1") && genericTarget)
				{
					targetItem = item;
				}else
				{
					targetItem = (Integer.parseInt(events[i+1]));
				}


				if (!(targetItem == item))
				{
					while ((i+1 < events.length) && !(events[i+1].equals("T")))
					{
						i++;
					}
				}else
				{
					genericTarget = false;
				}

			}else if (events[i].equals("e"))
			{
				int eventNumber = Integer.parseInt(events[i+1]);

				if (debug)
				{
					says ("Playing event: " + eventNumber+ "\n");
				}

				displayEvent(eventNumber);
				i++;
				eventCount++;
			}else if (events[i].equals("p"))
			{
				player[Integer.parseInt(events[i+1])] = Integer.parseInt(events[i+2]);
				i += 2;
				eventCount++;
			}else if (events[i].equals("h") && item == Integer.parseInt(events[i-1]))
			{

				if(events[i+1].equals("+"))
				{
					player[3] += Integer.parseInt(events[i+2]);
				}else if(events[i+2].equals("-"))
				{
					player[3] -= Integer.parseInt(events[i+2]);
				}

				i += 2;
				eventCount++;
			}else if (events[i].equals("d"))
			{
				ItemObject tempItem = new ItemObject(item, player);
				int location = tempItem.getLocation();
				int statOffset = 0;

				if(location < 0)
				{
					switch (location)
					{
						case -3: statOffset = 300;
								break;
						case -4: statOffset = 400;
								break;
					}

					player[(player[10]+item)] = thisRoom.getNumber() + statOffset;

				}

				eventCount++;
			}else if (events[i].equals("b"))
			{
				if (player[player[10]+Integer.parseInt(events[i+1])] < 0)
				{
					player[player[10]+Integer.parseInt(events[i+1])] = thisRoom.getNumber();
				}else
				{

					//If the break command doesn't work, this is the reason why.
					//I'm pretty sure I need this, but honestly I forget.
					while (player[(player[10]+Integer.parseInt(events[i+1]))] >= 100)
					{
						player[(player[10]+Integer.parseInt(events[i+1]))] -= 100;
					}
				}

				player[(player[10]+Integer.parseInt(events[i+1]))]+= 300;

			}else if (events[i].equals("t"))
			{
				text_speed = player[Integer.parseInt(events[i+1])];
				i++;
			}else if (events[i].equals("c"))
			{
				player[Integer.parseInt(events[i+2])] = player[Integer.parseInt(events[i+1])];
				i+=2;
				eventCount++;
			}else if (events[i].equals("u"))
			{
				if (player[(player[10]+item)] < 0)
				{
					player[(player[10]+item)] = -1;
				}else
				{
					player[(player[10]+item)] = thisRoom.getNumber();
				}
				i++;
				eventCount++;
			}else if (events[i].equals("s"))
			{
				int offset = Integer.parseInt(events[i+2]);
				int itemLocation = player[player[10]+Integer.parseInt(events[i+1])];
				ItemObject temp = new ItemObject(Integer.parseInt(events[i+1]), player);

				if (itemLocation < 0)
				{
					offset *= -1;

					player[player[10]+Integer.parseInt(events[i+1])] = offset;

				}else
				{
					offset = offset*100;

					player[player[10]+Integer.parseInt(events[i+1])] = (itemLocation - (temp.getState()*100)) + offset;

				}

				i += 2;
			}
		}

		if (eventCount == 0)
		{
			says("I can't let you do that Dio.");
		}
	}

	/**
	Reads a raw event string from an ItemObject and converts it
	to a String[] to be processed by the itemEvent method
	@param String The event string taken from the ItemObject
	@return String[] The string converted into an array.
	*/
	public static String[] readEvent(String eventString)
	{
		String[] temp = new String[100];
		String[] events;
		int actions = 0;

		for (int i = 0; i < temp.length; i++)
		{
			temp[i] = "";
		}

		/*

		This is an old debug line used for file reads. Due to spam, it's been disabled.

		if (debug)
		{
			says("EVENT STRING: " + eventString + "\n");
		}

		*/

		for (int i = 0; i < eventString.length(); i++)
		{
			/*
			if (debug)
			{
			says("EVENTSTRING: " + eventString.charAt(i));
			}
			*/

			if (eventString.charAt(i) == ' ')
			{
				actions++;
			}else
			{
				temp[actions] += eventString.charAt(i);
			}

			/*
			if (debug)
			{
			says("STORED AT ACTION " + actions + " : " + temp[actions]);
			}
			*/
		}


		events = new String[actions+1];

		for (int i = 0; i < actions+1; i++)
		{
			events[i] = temp[i];
		}

		return events;
	}

	/**
	Checks an items custom verb list to see if the verb presented is present. Displays an error if it is not.
	@param String The verb to check for.
	@param ItemObject
	@param ItemObject
	*/
	public static boolean itemVerbEvents (String verb, ItemObject arrow, ItemObject target)
	{
		boolean valid = false;
		
		if (debug)
		{
			says("Debug Lookup:\n-----------------------\n");
			says("Arrow: \n\n" + arrow.toString());
			says("Target: \n\n" + target.toString());
		}

		String[][] verbEventArray = arrow.getVerbEvents();

		int verbLocation = -1;

		for (int i = 0; i < verbEventArray[0].length; i++)
		{
			if(verbEventArray[0][i].equals(verb))
			{
				verbLocation = i;
			}
		}

		if (verbLocation >= 0)
		{
			valid = true;
			
			String rawEvent = verbEventArray[1][verbLocation];

			itemEvent(rawEvent, target.getNumber());

		}
		
		return valid;
	}
}