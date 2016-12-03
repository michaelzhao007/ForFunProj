import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Dongyang Zhao
 *
 */
public class PairArtist {
	
	//tuple class used as key in hashmap to compare equality
	static class Tuple {
		String t1;		
		String t2;
		
		public Tuple(String t1, String t2) {
			this.t1 = t1;
			this.t2 = t2;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t1 == null) ? 0 : t1.hashCode()) + ((t2 == null) ? 0 : t2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			Tuple cmp = (Tuple)obj;
			return (this.t1.equals(cmp.t1) && this.t2.equals(cmp.t2)) || (this.t2.equals(cmp.t1) && this.t1.equals(cmp.t2));
		}		
		
		@Override
		public String toString() {
			return "t1:" + t1 + ", t2:" + t2;
		}
	}
	
	//get all combinations of artists for one line
	public static List<Tuple> getAllCombinations(String[] list) {
		List<Tuple> result = new ArrayList<Tuple>();
		List<String> tuple = new ArrayList<String>();
		helper(list, result, tuple, 0);
		return result;
	}
	
	//dfs to generate the pair
	public static void helper(String[] list, List<Tuple> result, List<String> tuple, int idx) {
		if(tuple.size()>=2) {
			Tuple res = new Tuple(tuple.get(0), tuple.get(1));
			result.add(res);
		}
		else {
			for(int i = idx; i < list.length; i++) {
				tuple.add(list[idx]);
				helper(list, result, tuple, ++idx); 
				tuple.remove(tuple.size()-1);
			}
		}
	}
	
	//get final pair result from map if map value is larger or equal to 50
	public static List<Tuple> getResult(Map<Tuple, Integer> map) {
		List<Tuple> result = new ArrayList<Tuple>();
		for(Entry<Tuple, Integer> entry: map.entrySet()) {
			if(entry.getValue() >= 50)  {
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	//helper method to write to csv file
	public static void writeToCSV(List<Tuple> list) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File("src/result.csv"));
    	StringBuilder sb = new StringBuilder();
        for(Tuple tuple : list) {
        	sb.append(tuple.t1);
        	sb.append(",");
        	sb.append(tuple.t2);
        	sb.append("\n");
        }
        pw.write(sb.toString());
        pw.close();
	}
		
	public static void main(String[] args) {
		//1. read file
		File file = new File("src/Artist_lists_small.txt");
		BufferedReader br = null;
		Map<Tuple, Integer> map = new HashMap<Tuple, Integer>();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
	    //2. read line by line
			while ((line = br.readLine()) != null) {
	   //3. get combinations of pair and put in map
				String[] artists = line.split(",");
				List<Tuple> tuples = getAllCombinations(artists);
				for(Tuple tuple: tuples) {
					if(!map.containsKey(tuple)) {
						map.put(tuple, 1);
					}
					else {
						map.put(tuple, map.get(tuple)+1);
					}
				}
			}
	   	//4. get result and write to csv file	
			List<Tuple> result = getResult(map);
			writeToCSV(result);		 
		} 
		
		/*** my thoughts on suggested improvement on runtime. 
		 the key improvement to the runtime but sacrifice the 100% correctness:
		 1. we can go through the whole file, see how many time each artist has shown up on the file.
		 2. if the number of the showing on file for any two artist combination (in this algorithm
		    just one combination calculation, no previous for loop) is some big number, we can have 
		    some degree of confidence that the artist has a high probability of collision 
		    with another user. if the probability is high enough to make us thinking that two artists will collide
		    more than 50 times on the file, we are good to go. the number we need to think and reason to
		    refine this algorithm is the threshold number of the artist show up on the file.
		    more research on this is required
		***/
		
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
		catch(IOException e2) {
			e2.printStackTrace();
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
