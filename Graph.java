import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

class Graph{
    public class Vertex 
		{
			HashMap<String, Integer> nbrs = new HashMap<>();
		}

		static HashMap<String, Vertex> vtces;

		public Graph() 
		{
			vtces = new HashMap<>();
		}

		public int numVetex() 
		{
			return this.vtces.size();
		}

		public boolean containsVertex(String vname) 
		{
			return this.vtces.containsKey(vname);
		}

		public void addVertex(String vname) 
		{
			Vertex vtx = new Vertex();
			vtces.put(vname, vtx);
		}

		public void removeVertex(String vname) 
		{
			Vertex vtx = vtces.get(vname);
			ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

			for (String key : keys) 
			{
				Vertex nbrVtx = vtces.get(key);
				nbrVtx.nbrs.remove(vname);
			}

			vtces.remove(vname);
		}

		public int numEdges() 
		{
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int count = 0;

			for (String key : keys) 
			{
				Vertex vtx = vtces.get(key);
				count = count + vtx.nbrs.size();
			}

			return count / 2;
		}

		public boolean containsEdge(String vname1, String vname2) 
		{
			Vertex vtx1 = vtces.get(vname1);
			Vertex vtx2 = vtces.get(vname2);
			
			if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
				return false;
			}

			return true;
		}

		public void addEdge(String vname1, String vname2, int value) 
		{
			Vertex vtx1 = vtces.get(vname1); 
			Vertex vtx2 = vtces.get(vname2); 

			if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
				return;
			}

			vtx1.nbrs.put(vname2, value);
			vtx2.nbrs.put(vname1, value);
		}

		public void removeEdge(String vname1, String vname2) 
		{
			Vertex vtx1 = vtces.get(vname1);
			Vertex vtx2 = vtces.get(vname2);
			
			//check if the vertices given or the edge between these vertices exist or not
			if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
				return;
			}

			vtx1.nbrs.remove(vname2);
			vtx2.nbrs.remove(vname1);
		}
        public void display_Map() 
		{
			System.out.println("\t Bhubaneswar Map");
			System.out.println("\t------------------");
			System.out.println("----------------------------------------------------\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());

			for (String key : keys) 
			{
				String str = key + " =>\n";
				Vertex vtx = vtces.get(key);
				ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());
				
				for (String nbr : vtxnbrs)
				{
					str = str + "\t" + nbr + "\t";
                    			if (nbr.length()<16)
                    			str = str + "\t";
                    			if (nbr.length()<8)
                    			str = str + "\t";
                    			str = str + vtx.nbrs.get(nbr) + "\n";
				}
				System.out.println(str);
			}
			System.out.println("\t------------------");
			System.out.println("---------------------------------------------------\n");

		}
        public void display_Stations() 
		{
			System.out.println("\n***********************************************************************\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int i=1;
			for(String key : keys) 
			{
				System.out.println(i + ". " + key);
				i++;
			}
			System.out.println("\n***********************************************************************\n");
		}
        public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) 
		{
			// DIR EDGE
			if (containsEdge(vname1, vname2)) {
				return true;
			}

			//MARK AS DONE
			processed.put(vname1, true);

			Vertex vtx = vtces.get(vname1);
			ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

			//TRAVERSE THE NBRS OF THE VERTEX
			for (String nbr : nbrs) 
			{

				if (!processed.containsKey(nbr))
					if (hasPath(nbr, vname2, processed))
						return true;
			}

			return false;
		}

        private class DijkstraPair implements Comparable<DijkstraPair> 
		{
			String vname;
			String psf;
			int cost;

			@Override
			public int compareTo(DijkstraPair o) 
			{
				return o.cost - this.cost;
			}
		}
		
		public int dijkstra(String src, String des, boolean nan) 
		{
			int val = 0;
			ArrayList<String> ans = new ArrayList<>();
			HashMap<String, DijkstraPair> map = new HashMap<>();

			Heap<DijkstraPair> heap = new Heap<>();

			for (String key : vtces.keySet()) 
			{
				DijkstraPair np = new DijkstraPair();
				np.vname = key;
				//np.psf = "";
				np.cost = Integer.MAX_VALUE;

				if (key.equals(src)) 
				{
					np.cost = 0;
					np.psf = key;
				}

				heap.add(np);
				map.put(key, np);
			}

			//keep removing the pairs while heap is not empty
			while (!heap.isEmpty()) 
			{
				DijkstraPair rp = heap.remove();
				
				if(rp.vname.equals(des))
				{
					val = rp.cost;
					break;
				}
				
				map.remove(rp.vname);

				ans.add(rp.vname);
				
				Vertex v = vtces.get(rp.vname);
				for (String nbr : v.nbrs.keySet()) 
				{
					if (map.containsKey(nbr)) 
					{
						int oc = map.get(nbr).cost;
						Vertex k = vtces.get(rp.vname);
						int nc;
						if(nan)
							nc = rp.cost + 120 + 40*k.nbrs.get(nbr);
						else
							nc = rp.cost + k.nbrs.get(nbr);

						if (nc < oc) 
						{
							DijkstraPair gp = map.get(nbr);
							gp.psf = rp.psf + nbr;
							gp.cost = nc;

							heap.updatePriority(gp);
						}
					}
				}
			}
			return val;
		}

        public static void Create_Map(Graph g)
		{
			// Adding vertices
			g.addVertex("Acharya Vihar"); // 0
			g.addVertex("AG Square"); // 1
			g.addVertex("AIIMS"); // 2
			g.addVertex("AMRI Hospital"); // 3
			g.addVertex("Apollo Hospital"); // 4
			g.addVertex("Baramunda"); // 5
			g.addVertex("Biju Pattnaik Airport"); // 6
			g.addVertex("C.V. Raman Global University"); // 7
			g.addVertex("CARE Hospital"); // 8
			g.addVertex("Collage of Engineering & Technology"); // 9
			g.addVertex("CUTM"); // 10
			g.addVertex("Fire Station"); // 11
			g.addVertex("GITA"); // 12
			g.addVertex("Gohiria Square"); // 13
			g.addVertex("Hi-Tech"); // 14
			g.addVertex("IIIT"); // 15
			g.addVertex("IIT"); // 16
			g.addVertex("IMMT"); // 17
			g.addVertex("Infocity Square"); // 18
			g.addVertex("ITER"); // 19
			g.addVertex("Jagamara"); // 20
			g.addVertex("Jaydev Vihar"); // 21
			g.addVertex("Kalinga Hospital Square"); // 22
			g.addVertex("Kalpana Square"); // 23
			g.addVertex("Khandagiri"); // 24
			g.addVertex("KIIMS"); // 25
			g.addVertex("KIIT"); // 26
			g.addVertex("KIIT Square"); // 27
			g.addVertex("Lingaraj Mandir"); // 28
			g.addVertex("Lingraj Station"); // 29
			g.addVertex("Master Canteen"); // 30
			g.addVertex("OUAT Square"); // 31
			g.addVertex("Patia Square"); // 32
			g.addVertex("Rajmahal Square"); // 33
			g.addVertex("Rasulgarh"); // 34
			g.addVertex("Silicon Institute of Technology"); // 35
			g.addVertex("SUM Hospital"); // 36
			g.addVertex("Tomando"); // 37
			g.addVertex("Trident Academy Of Technology"); // 38
			g.addVertex("Utkal Hospital"); // 39
			g.addVertex("Vani Vihar"); // 40

			
			// Adding edges with rounded integer distances
			g.addEdge("KIIMS", "KIIT", 1);
			g.addEdge("KIIT", "KIIT Square", 1);
			g.addEdge("KIIMS", "Silicon Institute of Technology", 2);
			g.addEdge("Silicon Institute of Technology", "Infocity Square", 1);
			g.addEdge("Infocity Square", "Trident Academy Of Technology", 0);
			g.addEdge("Trident Academy Of Technology", "Utkal Hospital", 3);
			g.addEdge("Utkal Hospital", "CARE Hospital", 4);
			g.addEdge("KIIT Square", "Patia Square", 2);
			g.addEdge("Patia Square", "Kalinga Hospital Square", 3);
			g.addEdge("CARE Hospital", "Kalinga Hospital Square", 1);
			g.addEdge("Infocity Square", "Patia Square", 2);
			g.addEdge("KIIMS", "Patia Square", 3);
			g.addEdge("Kalinga Hospital Square", "Jaydev Vihar", 3);
			g.addEdge("Kalinga Hospital Square", "Apollo Hospital", 4);
			g.addEdge("Apollo Hospital", "Acharya Vihar", 2);
			g.addEdge("Jaydev Vihar", "IMMT", 3);
			g.addEdge("IMMT", "Acharya Vihar", 1);
			g.addEdge("Acharya Vihar", "Vani Vihar", 2);
			g.addEdge("Vani Vihar", "Rasulgarh", 5);
			g.addEdge("Rasulgarh", "Hi-Tech", 2);
			g.addEdge("Master Canteen", "Vani Vihar", 4);
			g.addEdge("Master Canteen", "Acharya Vihar", 4);
			g.addEdge("Master Canteen", "Jaydev Vihar", 6);
			g.addEdge("Rasulgarh", "Kalpana Square", 5);
			g.addEdge("Kalpana Square", "Lingaraj Mandir", 35);
			g.addEdge("Master Canteen", "Rajmahal Square", 3);
			g.addEdge("Rajmahal Square", "Kalpana Square", 1);
			g.addEdge("Rajmahal Square", "AG Square", 1);
			g.addEdge("AG Square", "Jaydev Vihar", 5);
			g.addEdge("Jaydev Vihar", "Fire Station", 5);
			g.addEdge("AG Square", "Acharya Vihar", 5);
			g.addEdge("AG Square", "OUAT Square", 3);
			g.addEdge("AG Square", "Biju Pattnaik Airport", 2);
			g.addEdge("Rajmahal Square", "Lingraj Station", 5);
			g.addEdge("Biju Pattnaik Airport", "Lingraj Station", 4);
			g.addEdge("OUAT Square", "Biju Pattnaik Airport", 3);
			g.addEdge("OUAT Square", "Jagamara", 4);
			g.addEdge("Jagamara", "ITER", 1);
			g.addEdge("ITER", "Lingraj Station", 5);
			g.addEdge("Fire Station", "AG Square", 5);
			g.addEdge("OUAT Square", "Fire Station", 3);
			g.addEdge("Fire Station", "Baramunda", 2);
			g.addEdge("Fire Station", "SUM Hospital", 4);
			g.addEdge("SUM Hospital", "IIIT", 4);
			g.addEdge("Baramunda", "Khandagiri", 2);
			g.addEdge("Khandagiri", "AMRI Hospital", 2);
			g.addEdge("AMRI Hospital", "Collage of Engineering & Technology", 3);
			g.addEdge("Khandagiri", "AIIMS", 5);
			g.addEdge("Khandagiri", "Tomando", 7);
			g.addEdge("Tomando", "Gohiria Square", 3);
			g.addEdge("Gohiria Square", "GITA", 2);
			g.addEdge("Gohiria Square", "C.V. Raman Global University", 1);
			g.addEdge("Gohiria Square", "IIT", 18);
			g.addEdge("Gohiria Square", "CUTM", 14);


		}
		
		public static String[] printCodelist()
		{
			System.out.println("List of places along with their codes:\n");
			ArrayList<String> keys = new ArrayList<>(vtces.keySet());
			int i=1,j=0,m=1;
			StringTokenizer stname;
			String temp="";
			String codes[] = new String[keys.size()];
			char c;
			for(String key : keys) 
			{
				stname = new StringTokenizer(key);
				codes[i-1] = "";
				j=0;
				while (stname.hasMoreTokens())
				{
				        temp = stname.nextToken();
				        c = temp.charAt(0);
				        while (c>47 && c<58)
				        {
				                codes[i-1]+= c;
				                j++;
				                c = temp.charAt(j);
				        }
				        if ((c<48 || c>57) && c<123)
				                codes[i-1]+= c;
				}
				if (codes[i-1].length() < 2)
					codes[i-1]+= Character.toUpperCase(temp.charAt(1));
				            
				System.out.print(i + ". " + key + "\t");
				if (key.length()<(22-m))
                    			System.out.print("\t");
				if (key.length()<(14-m))
                    			System.out.print("\t");
                    		if (key.length()<(6-m))
                    			System.out.print("\t");
                    		System.out.println(codes[i-1]);
				i++;
				if (i == (int)Math.pow(10,m))
				        m++;
			}
			return codes;
		}

        public static void main(String[] args) throws IOException
		{
			Graph g = new Graph();
			Create_Map(g);
			
			System.out.println("\n\t\t\t****WELCOME TO THE  BHUBANESWAR MAP*****");
			BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
				System.out.println("1. LIST ALL THE POINTS IN THE BHUBANESWAR MAP");
				System.out.println("2. SHOW THE VERTEX AND ITS NEIGHBOURS");
				System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' VERTEX TO 'DESTINATION' VERTEX");
				System.out.println("4. EXIT THE MENU");
				System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 4) : ");
				int choice = -1;
				try {
					choice = Integer.parseInt(inp.readLine());
				} catch(Exception e) {
					// default will handle
				}
				System.out.print("\n***********************************************************\n");
				if(choice == 4)
				{
					System.exit(0);
				}
				switch(choice)
				{
				case 1:
                    System.out.println("-------ALL THE PLACES IN BHUBANESWAR ARE SHOWN BELOW-------");
					g.display_Stations();
					break;
			
				case 2:
					g.display_Map();
					break;
				
				case 3:
					ArrayList<String> keys = new ArrayList<>(vtces.keySet());
					String codes[] = printCodelist();
					System.out.println("\n1. TO ENTER SERIAL NO. OF VERTICES\n2. TO ENTER CODE OF VERTICES\n3. TO ENTER NAME OF VERTICES\n");
					System.out.println("ENTER YOUR CHOICE:");
				        int ch = Integer.parseInt(inp.readLine());
					int j;
						
					String st1 = "", st2 = "";
					System.out.println("ENTER THE SOURCE AND DESTINATION VERTICES");
					if (ch == 1)
					{
					    st1 = keys.get(Integer.parseInt(inp.readLine())-1);
					    st2 = keys.get(Integer.parseInt(inp.readLine())-1);
					}
					else if (ch == 2)
					{
					    String a,b;
					    a = (inp.readLine()).toUpperCase();
					    for (j=0;j<keys.size();j++)
					       if (a.equals(codes[j]))
					           break;
					    st1 = keys.get(j);
					    b = (inp.readLine()).toUpperCase();
					    for (j=0;j<keys.size();j++)
					       if (b.equals(codes[j]))
					           break;
					    st2 = keys.get(j);
					}
					else if (ch == 3)
					{
					    st1 = inp.readLine();
					    st2 = inp.readLine();
					}
					else
					{
					    System.out.println("Invalid choice");
					    System.exit(0);
					}
				
					HashMap<String, Boolean> processed = new HashMap<>();
					if(!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
						System.out.println("THE INPUTS ARE INVALID");
					else
					System.out.println("SHORTEST DISTANCE FROM "+st1+" TO "+st2+" IS "+g.dijkstra(st1, st2, false)+"KM\n");
					break;
				
               	         default:  //If switch expression does not match with any case, 
                	        	//default statements are executed by the program.
                            	//No break is needed in the default case
                    	        System.out.println("Please enter a valid option! ");
                        	    System.out.println("The options you can choose are from 1 to 4. ");
                            
				}
			}
        }
}