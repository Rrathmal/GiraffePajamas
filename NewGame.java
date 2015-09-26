public class NewGame
{
	public static int[] player;

	public NewGame(int arraysize)
	{
		player = new int[arraysize];
		//Not all values need to be initialized, and in fact, it's much more efficient
		//to save the headache of writing them all out and only initialize the few that
		//are necessary (ie: Starting items, Location, Locked doors, NPC status)
		
		/*
		 * 
		 * Player values
		 * 
		 * CURRENTLY 0-19
		 * 
		 * Various stored values pertaining to player actions. Mostly synonymous with special.
		 * 
		 * 
		 */
		

		player[0] = 0; 	//No idea what this is going to be for yet. It probably won't change ever.
		player[1] = 0; 	//Player location.
		player[2] = 0; 	//Stuck. Prevents the player using move commands. // 1 yes, 0 no.
		player[3] = 34;	//Player HP. Whether or not this matters, I have no idea.
		player[4] = 1; 	//Number of shirts worn. //I seriously lost my shit laughing thinking of ways to use this.
		player[5] = 0; 	//Number of dresses worn.
		player[6] = 1; 	//Number of belts worn.
		player[7] = 2; 	//Number of boots worn.
		player[8] = 0; 	//Number of hats worn.
		player[9] = 0;	//Items flipped

		l(player[10]);  //ITEM OFFSET
		l(player[11]); 	//SPECIAL OFFSET
		l(player[12]);	//EXITS OFFSET

		player[13] = 15;//Text speed
		player[14] = 22;//Event text speed
		player[15] = 0;	//RAGE
		
		l(player[16]);	//COLLECTABLE OFFSET //Off set itself because I'm lazy

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
		500+ Map Location (Non-accessible)

		*/

		//REMOVE THIS WHEN ALL ITEMS ARE SET
		for (int i = 20; i < 220; i++)
		{
			player[i] = 98;
		}

		player[20] = -1;	//Player. If this isn't set to -1 or , half of the use commands break.
		player[21] = 200;	//This Chair 	: Test Room
		player[22] = 2; 	//Leather Belt	: Test Room
		player[23] = -1;	//Dagger		: Inv
		player[24] = -1;	//Lockpick		: Inv
		player[25] = 400;	//Locked box	: Test Room
		player[26] = 0;		//Mystery		: Test Room
		player[27] = -5;	//"Longsword"	: N/A
		player[28] = 201;	//GR Dresser	: Guest Room
		player[29] = 201;	//GR Wardrobe	: Guest Room
		player[30] = 201;	//GR Nightstand	: Guest Room
		player[31] = 201;	//GR Bed		: Guest Room
		player[32] = 201;	//GR Table		: Guest Room
		player[33] = 501;	//Maid			: Guest Room
		player[34] = 501;	//Dead maid D:  : Guest Room
		player[35] = 201;	//GR Chair		: Guest Room
		player[36] = 202;	//Candlestick	: U manor foyer

		/*

		Special

		CURRENTLY 220-259

		Flag values for various actions. No order to them at all.
		Refer to comments for use.

		*/

		player[220] = 0;	//Number of Commands entered.

		/*

		Exits

		CURRENTLY 260-359

		Location values for room exits.
		With the exception of:

		-1 : Locked
		-2 : Invisible
		-3 : God help you what did you do
		
		OK. I think I remembered how this worked. The value stored here refers to the location to travel to, whereas the value
		in the LISA file just refers to the address here.

		*/

		player[260] = -2;	//Room 0 exit up: 	Room 0	: Universal null exit
		player[261] = 2;	//GuestBedroom1 : West exit
		player[262] = -2;	//MFUE	up		: Locked (Attic)
		player[263] = 3;	//MFUE	down	: manor foyer D
		player[264] = 4;	//MFUE	north	: UEC
		player[265] = 13;	//MFUE	west	: UFW
		player[266] = 1;	//MFUE	east	: GB1
		player[267] = 2;	//MFD	up		: MFUE
		player[268] = 2;	//UEC	south	: MFUE
		player[269] = 6;	//MFUE 	in		: study
		player[270] = 13;	//study	out		: MFUW
		player[271] = 1;
		player[272] = 1;
		
		
		/*
		 
		 Collectables
		 
		 Currently 360-409
		 
		 Status and determining number for what specific collectables are.
		 Unlike items, they're value is not for a location but for which item it is.
		 
		 -1: Destroyed
		 0 : Aquired
		 1 : Shirt
		 2 : Dress
		 3 : Belt
		 4 : Boots
		 5 : Hat
		 6 : Small furry animal
		 
		 These values are to be referenced by rooms in order to display in look statements when a collectable is available.
		 
		 
		 */
		
		player[360] = 1;
		player[361] = 2;
		player[362] = 3;

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