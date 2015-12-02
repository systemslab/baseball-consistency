package Proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class findRange {
	
	public static void main(String args[]) throws IOException{

		ArrayList<orderedPair> list = new ArrayList<orderedPair>();
		/*list.add(new orderedPair(1, 4));
		list.add(new orderedPair(30, 40));
		list.add(new orderedPair(20, 91));
		list.add(new orderedPair(8, 10));
		list.add(new orderedPair(6, 7));
		list.add(new orderedPair(3, 9));
		list.add(new orderedPair(9,12));
		list.add(new orderedPair(11, 14));
		list.add(new orderedPair(2, 10));
		list.add(new orderedPair(10, 11));
		list.add(new orderedPair(11, 13));
		
		list.add(new orderedPair(2, 10));
		list.add(new orderedPair(2, 12));
		list.add(new orderedPair(10, 11));
		list.add(new orderedPair(10,13));
		list.add(new orderedPair(4,13));
		list.add(new orderedPair(1,10));*/
		
		list.add(new orderedPair(1, 5));
		list.add(new orderedPair(2, 6));
		//list.add(new orderedPair(3,7));
		list.add(new orderedPair(4,8));
		list.add(new orderedPair(5,9));
		list.add(new orderedPair(6,10));
		list.add(new orderedPair(7,11));
		list.add(new orderedPair(8,12));
		list.add(new orderedPair(9,13));
		list.add(new orderedPair(10,13));
		list.add(new orderedPair(11,13));
		//list.add(new orderedPair(12,13));
		list.add(new orderedPair(1,12));
		
		
		
		Collections.sort(list, new Comparator<orderedPair>() {
	        @Override
	        public int compare(orderedPair  pair1, orderedPair  pair2)
	        {

	           if(pair1.low < pair2.low)
	        	   return -1;
	           return 1;
	        }
	    });
		
		int lowRange = 3;
		int highRange = 13;
		int currentlow = 3;
		int currMax = 0, currHighIndex = -1, counter = 0;
		for(int i = 0; i < list.size(); i++){
			
			if(list.get(i).low <= currentlow ){
				if(currMax < list.get(i).high - currentlow +1){
					currMax = list.get(i).high - currentlow +1;
					currHighIndex = i;
				}
			}else{
				currMax = 0;
				counter++;
				currentlow = list.get(currHighIndex).high;
				System.out.print("( " + list.get(currHighIndex).low + ", " + list.get(currHighIndex).high+")");
				if(currentlow >= highRange){
					break;
				}
				i--;
			}
			
			if( i== list.size() -1){
				currMax = 0;
				counter++;
				currentlow = list.get(currHighIndex).high;
				System.out.print("( " + list.get(currHighIndex).low + ", " + list.get(currHighIndex).high+")");
				if(currentlow >= highRange){
					break;
				}
				i--;
			}
			
			
		}
		
		System.out.println();
		System.out.println(counter);
	}
	
}

class orderedPair{
	int low, high;
	public orderedPair(int a, int b) {
		low = a;
		high = b;
	}
	
}

