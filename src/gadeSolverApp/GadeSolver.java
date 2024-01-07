package gadeSolverApp;

import java.util.HashSet;
import java.util.Iterator;


// search for gade d(0)... d(k-1) d(k) ... d(m)
// where k = number of digits obtained from the problem
// d(k) .. d(m) represent the digits not contained in d(0) ... d(k-1). These digits are unique and in ascending order.

// d(0) = a, d(1) = b, etc.

// To search for a gade, typically you want to check the result against one or more reducedDigitalRoot based on a,b,c,....
// This is specificed in argument(s) "-d={letters},sum",   e.g.    -d abgj 6 -d abbd 8

// For searching, we need to make assumptions about the value of k, i.e. the number of digits what are resulting from the problem.
// this is specified by argument k (range of known digits to search), e.g. -k 4 6    -k 5 5

// We also allow constraining the values to be found by giving certain possible values 
// This is done using argument -v,    e.g.       -v a 0123    -v b 123456

// The resulting values may be inserted into a template. Set using -t 
// The default template is abcdefghijklmnopqrstuvxyz

// Set list of digits (including rep) that MUST be before k, e.g.  -y  1138 
// Set list of digits that MUST NOT be before k             ,e.g.  -n 67

// Option -nn suppressed the default printing of "sol# :" before each solution.

// Option -ng to print number of possible gades for each k

// -c "Nxx xx.yyy Exxx xx.yyy"
// -cd 600

// -kml

// TBD: Need to be able to describe a minimum and maximum instances of a given digit (before k)
// -in, e.g. -in 3 2 4   (3 must be present between 2 and 4 times before k)


// examples

// barndommens gade
// -t "N55 4j.e3k E012 2d.2fb" -k 11 11 -c "N55 46.500 E012 22.250" -cd 500 -v a 0 -v b 1 -kml
// -t "N55 4j.e3k E012 2d.2fb" -k 11 11 -c "N55 46.500 E012 22.250" -cd 500 -v a 0 -v b 1 -v j 6 -v d 2

// Anytime Gade
// -d 554ajfh 6 -d 123bbcg 7 -k 1 15 -t "N55 4a.jfh E012 3b.bcg" -v a 0123 -v b 123456 -u abcfghj
// do med distance
// -d 554ajfh 6 -d 123bbcg 7 -k 1 15 -t "N55 4a.jfh E012 3b.bcg" -c "N55 42.111 E012 34.111" -cd 3000 

// Kan du cracke en gade, step 1:
// -k 4 4 -t "N cc ce.gjc, E fg fh.hcf    A=a, B=b" -d cccegjc 4 -d fgfhhcf 4 -v c 5 -v f 1 -v g 2

// step 2
// -k 7 7 -t "N dd jg.gcl, E ac bm.lfi   E=e, H=h" -v d 5 -v a 1 -v c 2 -v b 1 -v j 4 -v g 789 -v m 678

// step 3
// -k 8 8 -t "N 55 5b.cen, E 12 1f.ahi    H=h, J=j" -v b 0123 -v f 45678 -v n 9
// -k 8 8 -t "N 55 5b.cen, E 12 1f.ahi" -v b 0123 -v f 456789 -v n 9 -nn
// -k 8 8 -t "N 55 5b.cen, E 12 1f.ahi    H=h, J=j" -v b 0 -v f 7 -v n 9 -v c 6 -v e 7  

// step 4
// -k 1 10 -t "..." -ng

// Cyklen
// -k 6 6 -t "N55 4a.ifi E012 3d.hha" -v a 0 -v d 45 
// do, med brug af distance funktion
// -k 6 6 -t "N55 4a.ifi E012 3d.hha" -c "N55 40.741 E012 35.108" -cd 300    
// do, output kml fil som kan ses i f.eks. GE eller google maps.
//-k 6 6 -t "N55 4a.ifi E012 3d.hha" -c "N55 40.741 E012 35.108" -cd 300 -kml

// Example med positiv, negativ lister
// -k 6 6 -t "N55 4a.ifi E012 3d.hha" -v a 0 -v d 45 -y 33 -n 4 -t abcdef 

// Ligefrem gade
//  -t "N ab cd.efg E hij kl.mno" -k 1 16 -v a 5 -v h 0 -v c 012345  

// Bænken
// -k 12 12 -t "N55 4m.dla E012 2l.jjl"  -c "N55 49.072 E012 29.122" -cd 3000
// -k 12 12 -t "N55 4m.dla E012 2l.jjl"  -c "N55 49.072 E012 29.122" -cd 3000 -kml

// 1855.639001 m per N minut
// 1044.796791 m per E minut
// Derefter sqrt(diff^2 + diff^2)

// (M3+) Visiting Eiffel & Jet-d'eau
// -t "N55 4e.jfc E012 2f.eih" -k 2 4 -c "N55 42.951 E012 24.167" -cd 3000
// -t "N55 4e.jfc E012 2f.eih" -k 2 4 -c "N55 42.951 E012 24.167" -cd 3000 -kml

// Veje til Hillerød
// -t "N55 oo.dth E012 bs.pql" -k 20 20 -c "N55 53.668 E012 22.157" -cd 5000 -v a 0 -v b 1 -v i 3 -v t 9 -v s 9 -v j 3 -v c 1 -v r 9 -in 3 5 9 -in 9 2 5 -in 4 2 5 -in 8 2 5 -in 2 2 5 -in 1 1 5
// baseret på at følgende cifre var indsamlet og at der manglede et 4 cifret årstal: 334 28 29 3 4 30 389 31

// Asterix 10
// -t "N55 4s.nkq E012 2d.obr" -k 10 11 -c "N55 49.520 E012 21.320" -cd 3000 -nn
// N55° 49.247 E12° 21.102
// -t "N55 4s.nkq E012 2d.obr" -k 13 25 -v s 9 -v n 2 -v n 2 -v e 1 -v o 0
// N55 48.345 E12 20.040  - not possible
// -t "N55 4s.nkq E012 2d.obr" -k 13 25 -v s 8 -v n 3 -v e 0 -v 
// N55 48.915 E12 21.363
// -t "N55 4s.nkq E012 2d.obr" -k 10 25 -v s 8 -v n 9 -v e 1 -v o 3
// -t "N55 4s.nkq E012 2d.obr" -k 10 25 -c "N55 49.520 E012 21.320" -cd 1000
// Hvis vi nu antager 24 album sammen, så kan 2 eller 4 ikke optræde sent. 
// -t "N55 4s.nkq E012 2d.obr" -k 13 25 -c "N55 49.520 E012 21.320" -cd 3000 -y 737324 -nn


public class GadeSolver implements Runnable {
	int k_min=0, k_max=0, k;
	boolean print_sol_num = true;
	boolean print_no_gades = false;
	boolean search_gades = false;
	int d_checks = 0;
	String[] d_check_str = new String[10];
	int[] d_check_value = new int[10];
	int v_checks = 0;
	char[] v_check = new char[10];
	String[] v_check_str = new String[10];
	int u_checks = 0;
	String[] u_check_str = new String[10];
	int in_checks = 0;
	int[] in_check = new int[10];
	int[] in_check_min = new int[10];
	int[] in_check_max = new int[10];
	String coor = "N55 47.214 E012 21.301";
	String temp_result = "";
	String d_present = "";
	String d_not_present = "";
	String temp_result_input = "";
	int coor_dist = 0;
	int min_dist = 1;
	String template = "abcefghijklmnopqrstuvxyz";
	boolean kml = false;
	
    // Search gades structures
    char[] gade_t = new char[100];
    int no_tr_digits = 0;
    int[] tr_digits = new int[100];
    HashSet<String> gades_found = new HashSet<String>();
    HashSet<String> gades_search_result = new HashSet<String>();
    int max_gade_search = -1;
    int min_gade_search = 99999999;
    String max_gade_string = "";
    String min_gade_string = "";
    boolean first_run = true;
    		
	static GadeSolverApp appRef = null;

	Iterator<String> gade_iterator;
	
	// Constructor. Will do just the decoding of arguments.
	public GadeSolver(String[] args) {
		String arg;
		for (int i=0; i < args.length && args[i].startsWith("-");) {
		
			arg = args[i++];

			if (arg.equals("-k")) {
				k_min = Integer.parseInt(args[i++]);
				k_max = Integer.parseInt(args[i++]);

			} else if (arg.equals("-d")) {
				d_check_str[d_checks] = args[i++];
				d_check_value[d_checks++] = Integer.parseInt(args[i++]);
			} else if (arg.equals("-v")) {
				v_check[v_checks] = args[i++].charAt(0);
				v_check_str[v_checks++] = args[i++];
			} else if (arg.equals("-u")) {
				u_check_str[u_checks++] = args[i++];
			} else if (arg.equals("-t")) {
				template = args[i++];
			} else if (arg.equals("-tr")) {
				temp_result_input = args[i++];
			} else if (arg.equals("-y")) {
				d_present = args[i++];
			} else if (arg.equals("-n")) {
				d_not_present = args[i++];
			} else if (arg.equals("-c")) {
				coor = args[i++];
			} else if (arg.equals("-cd")) {
				coor_dist = Integer.parseInt(args[i++]);
			} else if (arg.equals("-md")) {
				min_dist = Integer.parseInt(args[i++]);
			} else if (arg.equals("-nn")) {
				print_sol_num = false;
			} else if (arg.equals("-ng")) {
				print_no_gades = true;
			} else if (arg.equals("-kml")) {
				kml = true;
			} else if (arg.equals("-sg")) {
				search_gades = true;
			} else if (arg.equals("-in")) {
				in_check[in_checks] = Integer.parseInt(args[i++]);
				in_check_min[in_checks] = Integer.parseInt(args[i++]);
				in_check_max[in_checks] = Integer.parseInt(args[i++]);
				in_checks++;
			} else if (arg.equals("-nogui")) {
				// ignore
			} else {
				println("Illegal arguments");
				return;
			}
		}
		
        // Analyse -tr Assume we use %<digit> to indicate a variable digits. A digit without %
        // is fixed. We build normal temp_result at the same time.
        for (int tri=0; tri<temp_result_input.length(); tri++) {
        	if (temp_result_input.charAt(tri) == '@') { 
        		tr_digits[no_tr_digits++] = temp_result_input.charAt(++tri) - '0';
        	}
        	temp_result += temp_result_input.charAt(tri);
        }
	}   

	public static void setRef(GadeSolverApp ref) {
		appRef = ref;			
	}

	private synchronized void println(String arg) {
		// Local variant. Dump to console and/or to output window
		System.out.println(arg);
		if (appRef != null)
			appRef.Output(arg + "\n");
	}

	private synchronized void print(String arg) {
		// Local variant. Dump to console and/or to output window
		System.out.print(arg);
		if (appRef != null)
			appRef.Output(arg);
	}

	
	private static double getDegrees(String c, int s1, int e1, int s2, int e2) {
		double d = Float.parseFloat(c.substring(s1, e1)) +
				Float.parseFloat(c.substring(s2, e2)) / 60.0;
		return Math.toRadians(d);
	}
	
	/*
	 * Haversine Formula (from R.W. Sinnott, "Virtues of the Haversine", Sky
	 * and Telescope, vol. 68, no. 2, 1984, p. 159):
	 * 
	 * See the following URL for more info on calculating distances:
	 * http://www.census.gov/cgi-bin/geo/gisfaq?Q5.1
	 */
	private static int calc_dist(String c1, String c2) {
		double earthRadius = 6371; //km
		//           1         2
		// 0123456789012345678901
		// N55 12.123 E012 12.123
		try {
			double lat1 = getDegrees(c1, 1, 3, 4, 10);
			double lon1 = getDegrees(c1, 12, 15, 16, 22);
			double lat2 = getDegrees(c2, 1, 3, 4, 10);
			double lon2 = getDegrees(c2, 12, 15, 16, 22);

			double dlon = (lon2 - lon1);
			double dlat = (lat2 - lat1);

			double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
					+ Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2)
					* Math.sin(dlon / 2);

			double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));
			double km = earthRadius * c;
			return (int) (km * 1000);
		} 
		catch (Exception e){
			return 10000; // return 10KM, assume too much.
		}

	
	}
	
	private static char highest_char(String s, char higher_than) {
		char maxChar=higher_than;
		for (int i=0; i<s.length(); i++) {
			char c=s.charAt(i);
			if ((c>='a') && (c<='z') && (c>maxChar))
				maxChar = c;
		}
		return maxChar;
	}
	
	private static int complete(int []d_value, int k) {
		int digits=k, i, digit_to_add;
		
		// check present values, add values not there..
		for (i=0, digit_to_add=0; digit_to_add<10;) {
			if ((i<k) && (d_value[i] < digit_to_add)) {
				i++;  // checking smaller numbers right now...
				continue;
			}
			
			if ((i<k) && (d_value[i] == digit_to_add)) { 
				digit_to_add++; // digit already there
				i++;
				continue;
			} 
			
			// add digit
			d_value[digits++] = digit_to_add;
			digit_to_add++;
		}
		
		return digits;
	}
	
	private static int rDigitSum(int n) {
		if (n<10)
			return n;
		else
			return rDigitSum((n % 10) + rDigitSum(n / 10));
	}

	private boolean check(int k, int[] d_value, int digits) {
		// First let's count the number of digit instances 0...k-1...
		int[] digit_count = new int[10];
		for (int i=0; i<10; i++)
			digit_count[i] = 0;
		for (int i=0; i<k; i++)
			digit_count[d_value[i]]++;
		
		// check for not_present
		for (int i=0; i<d_not_present.length(); i++) {
			if (digit_count[d_not_present.charAt(i) - '0']>0)
				return false;
		}
		
		// check for present
		for (int i=0; i<d_present.length(); i++) 
			digit_count[d_present.charAt(i) - '0']--;
		for (int i=0; i<10; i++) {
			if (digit_count[i] < 0)
				return false;
		}
		
		// Check any value constraints
		for (int vc = 0; vc < v_checks; vc++) {
			int value = d_value[v_check[vc]-'a'];
			int i;
			for (i=0; i<v_check_str[vc].length(); i++) {
				if (value == v_check_str[vc].charAt(i) - '0') // ok
					break;
			}
			if (i == v_check_str[vc].length())  // we failed to find the value 
				return false;
		}
		
		
		// Check reduced digital root
		for (int dc = 0; dc<d_checks; dc++) {
			int n=0;
			// first extract values given by string
			for (int i=0; i<d_check_str[dc].length(); i++) {
				char c = d_check_str[dc].charAt(i);
				if ((c>='a') && (c<='z'))
					n += d_value[c - 'a'];
				else if ((c>='0') & (c<='9'))
					n += c - '0';
			}
			
			//println("check. " + d_check_str[dc] + " " + d_check_value[dc] + " " + n + " " + rDigitSum(n));
			
			if (rDigitSum(n) != d_check_value[dc])
				return false;
		}
		
		// Check uniqueness
		for (int uc = 0; uc<u_checks; uc++) {
			int[] u_value = new int[10];
			int n;
			for (int i=0; i<10; i++)
				u_value[i] = -1;
			
			for (int i=0; i<u_check_str[uc].length(); i++) {
				char c = u_check_str[uc].charAt(i);
				n = d_value[c - 'a'];
				if (n>9)
					return false;
				//println(n);
				if (u_value[n] != -1)
					return false;
				u_value[n] = 1;
			}
		}
		
		// Check digit instance ranges
		for (int ic = 0; ic<in_checks; ic++) {
			int v = in_check[ic];
			if ((in_check_min[ic] > digit_count[v]) ||
				(in_check_max[ic] < digit_count[v]))
				return false;

		}
				
		return true;
	}

	private static String output(int []d_value, int digits, String template, int k) {
		String result ="";
		
		for (int i=0; i<template.length(); i++) {
			char c = template.charAt(i);
			if ((c>='a') && (c<='z') && (c-'a' < digits)) { 
				// substitute
				result += d_value[c-'a'];
			} else {
				switch (c) {
					case '[': // need to evaluate expression until ]. An expression must have the format [<expression>, # digits, minimum, maximum]
						String expr="", noDigitStr="", minValStr="", maxValStr="";
						for (i++; i<template.length() && (c=template.charAt(i)) != ','; i++)
							expr += c;
						for (i++; i<template.length() && (c=template.charAt(i)) != ','; i++)
							noDigitStr += c; 
						for (i++; i<template.length() && (c=template.charAt(i)) != ','; i++)
							minValStr += c; 
						for (i++; i<template.length() && (c=template.charAt(i)) != ']'; i++)
							maxValStr += c;
						
						// Step 1. evaluate values.
						String expr1="";
						for (int j=0; j<expr.length(); j++) {
							char c1 = expr.charAt(j);
							if ((c1>='a') && (c1<='z') && (c1-'a' < digits))  
								expr1 += d_value[c1-'a'];
							else
								expr1 += c1;
						}
						
						int value=0;

						// Now we must evaluate expr1, and check minimum and maximum conditions. If ok, 
						// format according to noDigits.
						int tmp=0;
						char op = ' ';
						for (int j=0; j<expr1.length(); j++) {
							//System.out.println("expr1=" + expr1 + ", j=" + j + ", value=" + value + ", tmp=" + tmp + ", op=" + op);
							char c2 = expr1.charAt(j);
							if ((c2>='0') && (c2<='9')) {
								tmp = tmp * 10 + (c2-'0');
							} else {
								switch (op) {
									case ' ': value = tmp; break; // push
									case '+': value += tmp; break;
									case '-': value -= tmp; break;
									case '*': value *= tmp; break;
									case '/': value /= tmp; break;
									default: break;
								}
								tmp = 0;
								op = c2;
							}
						}
						switch (op) {
							case '+': value += tmp; break;
							case '*': value *= tmp; break;
							case '-': value -= tmp; break;
							case '/': value /= tmp; break;
							default: break;
						}
						// Check value
						if (value < Integer.valueOf(minValStr) || value > Integer.valueOf(maxValStr))
							return "";
						
						 result += String.format("%0" + noDigitStr + "d", value);
						break;
					case ']': // ignore;
						break;
					case '$': // value of k
						result += Integer.toString(k);
						break;
					case '#': // Input digits string
						String t = "";
						for (int j=0; j<k; j++)
							t += Integer.toString(d_value[j]);
						result += t;
						break;
					case '%': // All gade chars formatted
						String gade = "";
						for (int g=0; g<digits; g++) 
							gade += (g>0?",":"") + Character.toString((char) ('a' + g)) + "=" + Integer.toString(d_value[g]);
						result += gade;
						break;
					default:
						result += c;
						break;
				}
			}
		}
		return result;
	}
	
	private void kml_output_start()  {
		println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		println("  <kml xmlns=\"http://www.opengis.net/kml/2.2\">");
		println("  <Document>");
	}
	
	private void kml_output_solution(String coor, int sol) {
		// N55 55.555 E012 23.234
		// 0123456789012345678901
		//           1         2
		double n, e;
		
		n = Float.parseFloat(coor.substring(1,3)) +
				Float.parseFloat(coor.substring(4,10)) / 60.0;
		e = Float.parseFloat(coor.substring(13,15)) +
				Float.parseFloat(coor.substring(16,22)) / 60.0;
		
		println(" <Placemark><name>" + coor + "</name>");
		println("   <Point><coordinates>" + e + "," + n + "</coordinates></Point>");
		println(" </Placemark>");
	}
	
	private void kml_output_final() {
		println("</Document>");
		println("</kml>");
	}
		
	public static int search_gade(int x, int k, char[] gade_t, int[] d_value, int[] tr_digits, int no_tr_digits, HashSet<String> gades_found, String gade_tr) {
//		System.out.println(x);
		
		// For each position we will try to different possible letters, given
		// the previous letters selected and the coordinate in -tr
		// The digit to match against is tr_digits[x];
		int highest_pre_k = -1;  // k is 'a' .. 'a'+k-1
		int highest_post_k = -1;
		int lowest_post_k = -1;
		int digit_ref = -1;
		@SuppressWarnings("unused")
		int no_pre_k = 0;
		int no_post_k = 0;
		int last_index = -1;
		int[] d_used = new int[10];
		for (int b = 0; b <10; b++)
			d_used[b] = 0;
		int no_d_used = 0;
		// Let's analyse the gade right now
		for (int z = 0; z < k+9; z++) {
			if (d_value[z] != -1 && d_used[d_value[z]] == 0) {
				d_used[d_value[z]] = 1;
				no_d_used++;
			}
			if (d_value[z] == tr_digits[x])
				digit_ref = z;
			if (z < k && d_value[z] > highest_pre_k)
				highest_pre_k = d_value[z];
			if (z >= k && d_value[z] > highest_post_k)
				highest_post_k = d_value[z];
			if (z >= k && (d_value[z] < lowest_post_k || lowest_post_k == -1))
				lowest_post_k = d_value[z];
			if (z < k && d_value[z] != -1)
				no_pre_k++;
			if (z >= k && d_value[z] != -1)
				last_index = z;
			if (z >= k && d_value[z] != -1)
				no_post_k++;
		}

		// Enough digits left to fill gade (up to last known letter)
		if (last_index - k > (10 - no_d_used) + no_post_k) {
			return 0;
		}

		if (x == no_tr_digits) {
			// Done, create gade
			String gade = "";
			int tr_index = 0;
			for (int tri = 0; tri < gade_tr.length(); tri++) {
				if (gade_tr.charAt(tri) == '@') {
					gade += gade_t[tr_index++];
					tri++;
				} else {
					gade += gade_tr.charAt(tri);
				}
			}
			gades_found.add(gade);
			return 0;
		}
		
		// Lets be smart about possible values for a letter at position x
		char start_c = 'a'; 
		char end_c = (char) ('a' + k + 9);
		if (digit_ref != -1) {
			// So, we already have a letter for this digit.
			if (digit_ref >= k) {
				start_c = end_c = (char) ('a' + digit_ref);
			} else {
				// the digit was before k, so range could be diff
				int dr_s = digit_ref;
				while (dr_s > 0 && 
						(d_value[dr_s - 1] == tr_digits[x] ||
						d_value[dr_s - 1] == -1)) 
					dr_s--;
				start_c= (char) ('a' + dr_s);
				int dr_e = digit_ref;
				while (dr_e < k &&
						(d_value[dr_e + 1] == tr_digits[x] ||
						d_value[dr_e + 1] == -1))
					dr_e++;
				end_c = (char) ('a' + dr_e);
			}
		} else {
			// We don't have the value yet, but what may we learn about the possible
			// letter values. It could be before k or after k...

			// TODO: More logic can surely be added here... 
		}

		for (char c= start_c; c <= end_c; c++) {
			boolean set_ok = true;
			boolean i_set_d = false;
			int index = c - 'a';
			// Quick check. Is digit already set to other value?
			if ((d_value[index] != -1) && (d_value[index] != tr_digits[x])) {
				continue;
			}
				
				// Ok, we have the intention to put the digit here. Let's examine the basics
			if (index < k) {
				for (int h = 0; h < index; h++)
					if (d_value[h] != -1 && d_value[h] > tr_digits[x]) {
						set_ok = false;
						break;
					}
				for (int h = index + 1; h < k; h++)
					if (d_value[h] != -1 && d_value[h] < tr_digits[x]) {
						set_ok = false;
						break;
					}
				for (int h = k; h < k + 9; h++)
					if (d_value[h] == tr_digits[x]) { 
						set_ok = false;
						break;
					}
			} else {
				for (int h = k; h < index; h++) 
					if (d_value[h] != -1 && d_value[h] >= tr_digits[x]) {
						set_ok = false;
						break;
					}
				for (int h = index + 1; h < k + 9; h++)
					if (d_value[h] != -1 && d_value[h] <= tr_digits[x]) {
						set_ok = false;
						break;
					}
				for (int h = 0; h < k; h++)
					if (d_value[h] == tr_digits[x]) { 
						set_ok = false;
						break;
					}
			}
			
			if (set_ok) {
				if (d_value[index] == -1) {
					i_set_d = true;
					d_value[index] = tr_digits[x];
				}
				gade_t[x] = c;
				// Call recursively
				search_gade(x + 1, k, gade_t, d_value, tr_digits, no_tr_digits, gades_found, gade_tr);

				// reset?
				if (i_set_d)
					d_value[index] = -1;
			}
		}
		return 0;
	}

	public void solve() {
		
		// Do initial logic. Then call run 
		
		
		
	}
	
	public void run() {
		// Let's go do it.
		// Local solver related variables are assigned here.
        int i;
        HashSet<String> solutions = new HashSet<String>();
        int[] d_value = new int[100];
        Thread[] threads = null;
        boolean i_was_first = first_run;
        int min_digits;
        String template_local = template;
        
        // main k loop
        int no_gades;
        
        int k_min_local = k_min;
        int k_max_local = k_max;
        if (!first_run) {
        	k_min_local = k;
        	k_max_local = k;
        }
      	  
        for (k=k_min_local; k<=k_max_local; k++) {
            if (search_gades && first_run) {
            	gades_found.clear();
            	// We will use d_value to store resulting gade values
            	for (i=0; i<100; i++) 
            		d_value[i] = -1;
            	
            	search_gade(0, k, gade_t, d_value, tr_digits, no_tr_digits, gades_found, temp_result_input);
            	
            	System.out.println("Found " + gades_found.size() + " possible gades. Time to check them..");
            	
            	// Here, we will spawn initial threads!!!
            	first_run = false;
            	gade_iterator = gades_found.iterator(); 
            	threads = new Thread[8];
            	for (int t = 0; t < 7; t++) {
            		threads[t] = new Thread(this);
            		threads[t].start();
            	}
            }
            no_gades = 0;
            for (;;) { // Loop for gade 
            	if (search_gades) {
            		synchronized(this) {
            			if (!gade_iterator.hasNext())
            				break;
            			template_local = gade_iterator.next().toString();  
            			// Needs to be carefully considered... need synchronized section for this...
            		}
            	}
                // Find highest char in template and d_checks... We need to get at least this many digits from our gade. 
                char maxChar = highest_char(template_local, 'a');
                for (i=0; i<d_checks; i++)
                	maxChar = highest_char(d_check_str[i], maxChar);
                min_digits = (maxChar - 'a') +1 ;
            	

            	//	println("k=" + k);

            	// Now, given k, we need to try all the possible values.
            	// First we iterate the known values
            	no_gades = 0;
            	for (i=0; i<100; i++) 
            		d_value[i] =0;
            	int digits = 0;
            	if (search_gades)
            		solutions.clear();
            	for (i=0;;) {

            		if (i==k) {
            			// ok, all values from the problem have been set. Time to complete with the missing digits 
            			// (in order) and check the solution ....
            			digits = complete(d_value, k);
            			no_gades++;
            			
            			if ((digits >= min_digits) && check(k, d_value, digits)) {
            				String solution = new String(output(d_value, digits, template_local, k));
            				if (!search_gades && temp_result != "" && !solution.startsWith(temp_result) && solution != "") {
            					// skip
            				} else {
            					solutions.add(solution);
            				}
            			}

            			// now, backtrack, and restart 
            			for (i--; i>=0 && d_value[i]==9; i--);
            			if (i<0) 
            				break; // done
            			d_value[i]++;
            			continue;
            		}

            		i++;
            		d_value[i] = d_value[i-1];
            	}	      		
            	if (search_gades) {
            		// Add to rsult if solutions are found. We need however to ensure that one of the coordinates is the one
            		// we are looking for, i.e. temp_result...
            		synchronized(this) {
            			if (solutions.size() > 0 && solutions.contains(temp_result)) {
            				gades_search_result.add(new String(solutions.size() + "," + template_local));
            				if (solutions.size() >= max_gade_search) {
            					max_gade_search = solutions.size();
            					max_gade_string = solutions.size() + "," + template_local;
            					System.out.println("New max: " + max_gade_string);
            				}
            				if (solutions.size() <= min_gade_search) {
            					min_gade_search = solutions.size();
            					min_gade_string = solutions.size() + "," + template_local;
            					System.out.println("New min: " + min_gade_string);
            				}
            			}
            		}
            	} else {
            		break; // No in search gade iterator loop, really. 
            	}
            }  
            	
            	
        	// done with k
        	if (print_no_gades)
        		println("For k=" + k + " there are " + no_gades + " possible gades.");
        	
        	// Wait for other threads if spawned
        	if (search_gades && threads != null) {
                for (int t = 0; t < 7; t++) {
            		try {
    					threads[t].join();
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            	}
    		
        	}
        }
        
        // Search gades?
        if (search_gades && i_was_first) {
            
        	println("Total number of possible gades: " + gades_search_result.size() + ". Max: " + max_gade_string + ". Min: " + min_gade_string);
         	for (Iterator<String> iterator = gades_search_result.iterator(); iterator.hasNext();) {
         		println(iterator.next().toString());
         	}
        }
        
     
        int sol=1;
        int dist=0;
        String sol_string;
        
        if (!search_gades) {
        	if (kml)
        		kml_output_start();

        	for (Iterator<String> iterator = solutions.iterator(); iterator.hasNext();) {

        		sol_string = iterator.next().toString();
        		if (coor_dist >0) {
        			// Distance check
        			dist = calc_dist(sol_string, coor);
        			if (dist > coor_dist || dist < min_dist)
        				continue;
        		}
        		if (print_sol_num && !kml)
        			print(sol + ": ");

        		if (kml)
        			kml_output_solution(sol_string, sol);
        		else
        			println(sol_string + " ");
        		sol++;
        	}

        	if (kml)
        		kml_output_final();
        }
	}
}
     
