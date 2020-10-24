package lse;

import java.io.*;
import java.util.*;

public class driver 
{
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[])
	{
		LittleSearchEngine lse = new LittleSearchEngine();
		
		try
		{
			lse.makeIndex("docs.txt", "noisewords.txt");
		} 
		catch (FileNotFoundException e)
		{
		}		
		
	String input;
//
		for (String hi : lse.keywordsIndex.keySet())
			System.out.println(hi+" "+lse.keywordsIndex.get(hi));
//				
	while (!(input = getOption()).equals("q"))
	{
			System.out.println(lse.getKeyword(input));
	}
		
		System.out.println(lse.top5search("color", "strange"));
		System.out.println(lse.keywordsIndex.get("deep"));
	}
}

