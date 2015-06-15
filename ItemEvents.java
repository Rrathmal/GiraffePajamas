import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

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
		String[] longEvent;
		String temp;

		try
		{
			file = new File("FLUFF.txt");
			input = new Scanner(file);


			for (int i = 0; i < ((event*3)+2); i++)
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
	@param Int The event number
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


				for (int i = 0; i < ((events[j]*3)+2); i++)
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

			says(eventCheck.replaceAll("\\\\n", "\n"));

		}catch(IOException e)
		{
		}
	}

	/**
	Takes an event string and a target item number and prossess
	any events associated with the two.
	@param String The raw event string.
	@param Int The number of the item that's being used as a target.
	*/
	public static void itemEvent(String rawEvent, int item)
	{
		String[] events = readEvent(rawEvent);
		int eventCount = 0;

		/*

		System.out.println("RAW EVENT: " + rawEvent);
		System.out.println("ITEM NUMB: " + item);

		*/

		if (debug)
		{
			for (int i = 0; i < events.length; i++)
			{
				says("Events " + i + ": " + events[i]);
			}
		}

		for (int i = 0; i < events.length; i++)
		{
			if (events[i].equals("i"))
			{
				if (player[Integer.parseInt(events[i+1])] == player[Integer.parseInt(events[i+2])])
				{
					i += 2;
				}else
				{
					//i = events.length;
					while (!(events[i].equals("i")) && i < events.length)
					{
						i++;
					}
				}

			}else if (events[i].equals("e") && item == Integer.parseInt(events[i-1]))
			{
				int eventNumber = Integer.parseInt(events[i+1]);

				if (debug)
				{
					says ("Playing event: " + eventNumber+ "\n");
				}

				displayEvent(eventNumber);
				i++;
				eventCount++;
			}else if (events[i].equals("p") && item == Integer.parseInt(events[i-1]))
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
			}else if (events[i].equals("d") && item == Integer.parseInt(events[i-1]))
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
			}else if (events[i].equals("b") && item == Integer.parseInt(events[i-1]))
			{
				if (player[player[10]+Integer.parseInt(events[i+1])] < 0)
				{
					player[player[10]+Integer.parseInt(events[i+1])] = thisRoom.getNumber();
				}

				player[(player[10]+Integer.parseInt(events[i+1]))]+= 300;

			}else if (events[i].equals("t") && item == Integer.parseInt(events[i-1]))
			{
				text_speed = player[Integer.parseInt(events[i+1])];
				i++;
			}else if (events[i].equals("c") && item == Integer.parseInt(events[i-1]))
			{
				player[Integer.parseInt(events[i+2])] = player[Integer.parseInt(events[i+1])];
				i+=2;
				eventCount++;
			}else if (events[i].equals("u") && item == Integer.parseInt(events[i-1]))
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

		if (debug)
		{
			says("EVENT STRING: " + eventString + "\n");
		}

		for (int i = 0; i < eventString.length(); i++)
		{
			if (debug)
			{
			says("EVENTSTRING: " + eventString.charAt(i));
			}

			if (eventString.charAt(i) == ' ')
			{
				actions++;
			}else
			{
				temp[actions] += eventString.charAt(i);
			}

			if (debug)
			{
			says("STORED AT ACTION " + actions + " : " + temp[actions]);
			}
		}


		events = new String[actions+1];

		for (int i = 0; i < actions+1; i++)
		{
			events[i] = temp[i];
		}

		return events;
	}

	/**

	*/
	public static void itemVerbEvents (String verb, ItemObject arrow, ItemObject target)
	{
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
			String rawEvent = verbEventArray[1][verbLocation];

			itemEvent(rawEvent, target.getNumber());

		}else
		{
			says("While in your mind a " + arrow.getName() + " might be great to " + verb +
					" with; this is earth, so it has no effect.");
		}
	}
}