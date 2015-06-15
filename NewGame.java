public class NewGame
{
	public static int[] player;

	public NewGame(int arraysize)
	{
		player = new int[arraysize];
		//Not all values need to be initialiased, and in fact, it's much more efficient
		//to save the headache of writing them all out and only initialize the few that
		//are necessary (ie: Starting itmes, Location, Locked doors, NPC status)

		player[0] = 0; 	//No idea what this is going to be for yet. It probably won't change ever.
		player[1] = 0; 	//Player location.
		player[2] = 0; 	//Stuck. Prevents the player using move commands. // 1 yes, 0 no.
		player[3] = 34;	//Player HP. Whether or not this matters, I have no idea.
		player[4] = 1; 	//Number of shirts worn. //I seriously lost my shit laughing thinking of ways to use this.
		player[5] = 0; 	//Number of dresses worn.
		player[6] = 1; 	//Number of belts worn.
		player[7] = 2; 	//Number of boots worn.
		player[8] = 0; 	//Number of hats worn.
		player[9] = 0;	//

		l(player[10]);  //ITEM OFFSET
		l(player[11]); 	//SPECIAL OFFSET
		l(player[12]);	//EXITS OFFSET

		player[13] = 15;//Text speed
		player[14] = 22;//Event text speed

		/*

		Items

		CURRENTLY 20-219

		Location corresponds directly to the order in ITEMS.txt

		Item locations read as such:
		-1 inventory,
		0-97 Map location,
		98 space,
		99 inventory (broken),
		100+ Map Location (Flipped)
		200+ Map Location (Stationary)
		300+ Map Location (Broken)
		400+ Map Location (Locked)
		500+ Map Location (Non-accessable)

		*/

		player[20] = -1;	//Player. If this isn't set to -1 or , half of the use commands break.
		player[21] = 0;		//This Chair 	: Test Room
		player[22] = 3; 	//Leather Belt	: Test Room
		player[23] = -1;	//Dagger
		player[24] = 2;		//Lockpick
		player[25] = 400;	//Locked box	: Test Room

		//REMOVE THIS WHEN ALL ITEMS ARE SET
		for (int i = 26; i < 220; i++)
		{
			player[i] = 98;
		}

		/*

		Special

		CURRENTLY 220-259

		Flag values for various actions. No order to them at all.
		Refer to comments for use.

		*/

		/*

		Exits

		CURRENTLY 260-319

		Location values for room exits.
		With the exception of:

		-1 : Locked
		-2 : Invisible
		-3 : God help you what did you do

		*/

		player[260] = -2;	//Room 0 exit up: 	Room 0
		player[261] = -2;	//
		player[262] = -2;	//
		player[263] = -2;	//
		player[264] = -2;	//

	}

	public static int[] newPlayer(int[] array)
	{

		for (int i = 0; i < 10; i++)
		{
			array[i] = player[i];
		}

		for (int i = 13; i < player.length; i++)
		{
			array[i] = player[i];
		}


		System.out.println("\n\n");

		return array;
	}

	public static void l(int i)
	{
	}
}