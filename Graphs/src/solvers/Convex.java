package solvers;

import java.util.ArrayList;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;

import graph.Graph;
import graph.Line;
import graph.Point;
import graph.Rectangle;
import javafx.beans.property.SimpleDoubleProperty;

public class Convex {

	public static class Rect
	{
		public double x, y;
		public double w, h;
		public double wp, hp;
		public Rectangle r;
		public ArrayList<Rect> neighbours;
		private static int nid = 0;
		public final int id;

		public Rect(double width, double height)
		{
			id = nid;
			nid ++;

			w = width;
			h = height;
			x = 0;
			y = 0;
			r = null;
			neighbours = new ArrayList<>();
		}
	}


	public static double calculC (ArrayList<Rect> rects, double delta)
	{
		double lminw = Double.POSITIVE_INFINITY;
		double lminh = Double.POSITIVE_INFINITY;

		double lmaxw = 0;
		double lmaxh = 0;

		for(Rect p : rects)
		{
			if (lminw>p.w)
				lminw=p.w;
			if (lminh>p.h)
				lminh=p.h;
			if(lmaxw<p.w)
				lmaxw=p.w;

			lmaxh = Math.max(lmaxh, p.h);
		};


		double lmin = Math.min(lminw, lminh) + delta;
		double lmax = Math.max(lmaxh, lmaxw) + delta;

		double c=0;
		if(lmax<3*lmin)
			c=lmax;
		else if((3*lmin<=lmax)&&(lmax<15*lmin))
			c=3*lmin/2;
		else
			c=lmax/30;

		return c;
	}

	static int select(int[] list, int lo, int hi, int k) {
		int n = hi - lo;
		if (n < 2)
			return list[lo];

		int pivot = list[lo + (k * 7919) % n]; // Pick a random pivot

		// Triage list to [<pivot][=pivot][>pivot]
		int nLess = 0, nSame = 0, nMore = 0;
		int lo3 = lo;
		int hi3 = hi;
		while (lo3 < hi3) {
			int e = list[lo3];
			int cmp = compare(e, pivot);
			if (cmp < 0) {
				nLess++;
				lo3++;
			} else if (cmp > 0) {
				swap(list, lo3, --hi3);
				if (nSame > 0)
					swap(list, hi3, hi3 + nSame);
				nMore++;
			} else {
				nSame++;
				swap(list, lo3, --hi3);
			}
		}
		assert (nSame > 0);
		assert (nLess + nSame + nMore == n);
		assert (list[lo + nLess] == pivot);
		assert (list[hi - nMore - 1] == pivot);
		if (k >= n - nMore)
			return select(list, hi - nMore, hi, k - nLess - nSame);
		else if (k < nLess)
			return select(list, lo, lo + nLess, k);
		return lo + k;
	}

	private static void swap(int[] list, int i, int j) {  //echanger les deux nombres dans la liste
		int tmp = list[i];
		list[i] = list[j];
		list[j] = tmp;
	}

	private static int compare(int e, int pivot)
	{
		if (e>pivot) return -1;
		if (e<pivot) return 1;
		return 0;
	}
	public static void calculWpHp(ArrayList<Rect> rects, double delta,double c) {

		for (Rect p : rects)
		{
			p.hp=Math.ceil((p.h+delta)/c);
			p.wp=Math.ceil((p.w+delta)/c);
		}
	}

	public static void compact(boolean horDirection, double gamma, boolean expand, ArrayList<Rect> rects, int[][] grid)
	{ //The parameter expand is true if boxes need to be expanded to it¡¯s real sizes, otherwise it is false
		// direction is changed after every compaction

		final Variable[] zi = new Variable[rects.size()];
		for(int i=0;i<rects.size();i++)
		{
			zi[i]=Variable.make("Z"+Integer.toString(i)).lower(0).upper(grid.length-1).weight(1);
		}
		final ExpressionsBasedModel retVal= new ExpressionsBasedModel(zi);
		final Expression variance = retVal.addExpression("Variance");

		if(!expand) //wi ou hi ==1
		{
			if(horDirection)  //horizontal
			{
				for(Rect v : rects)
				{
					Rect vj = null;
					for(int i=(int)v.x+1;i<grid.length;i++)
					{
						if(grid[i][(int)v.y]!=-1) //y a un rectangle dans le grid
						{
							vj = rects.get(grid[i][(int)v.y]); 
							final Expression expr = retVal.addExpression("Comp"+rects.indexOf(v));
							expr.set(zi[rects.indexOf(v)],-1);
							expr.set(zi[rects.indexOf(vj)],1);
							expr.lower(gamma);
							break;
						}
					}
					variance.set(zi[rects.indexOf(v)], zi[rects.indexOf(v)], v.neighbours.size());
					variance.set(zi[rects.indexOf(v)], v.neighbours.size());
					for (Rect vjj : v.neighbours)
					{
						variance.set(zi[rects.indexOf(vjj)], zi[rects.indexOf(v)],-1);
						variance.set(zi[rects.indexOf(vjj)],-0.5);
						variance.set(zi[rects.indexOf(vjj)],-0.5);
					}
				}



			}
			else //verticalement
			{


				for(Rect v : rects)
				{
					Rect vj = null;
					for(int i=(int)v.y+1;i<grid.length;i++)
					{
						if(grid[(int)v.x][i]!=-1) //y a un rectangle dans le grid
						{
							vj = rects.get(grid[(int)v.x][i]); 
							final Expression expr = retVal.addExpression("Comp"+rects.indexOf(v));
							expr.set(zi[rects.indexOf(v)],-1);
							expr.set(zi[rects.indexOf(vj)],1);
							expr.lower(gamma);
							break;
						}
					}
					variance.set(zi[rects.indexOf(v)], zi[rects.indexOf(v)], v.neighbours.size());
					variance.set(zi[rects.indexOf(v)], v.neighbours.size());


					for (Rect vjj : v.neighbours)
					{
						variance.set(zi[rects.indexOf(vjj)], zi[rects.indexOf(v)],-1);
						variance.set(zi[rects.indexOf(vjj)],-0.5);
						variance.set(zi[rects.indexOf(v)],-0.5);
					}
				}
			}
		}
		else  //hi et wi != 1
		{
			int [][]grid_p = new int [grid.length][grid[0].length]; //y corspond a la longeur de premier element
			for(int i=0; i<grid_p.length;i++)
			{
				for (int j=0;j<grid_p[0].length;j++)
				{
					grid_p[i][j]=-1;
				}
			}

			for(Rect v : rects)
			{

				for(int i=(int)v.x; i<=(int)v.x+v.wp;i++)
				{
					for (int j=(int)v.y;j<(int)v.y+v.hp;j++)
					{
						if(i<grid_p.length && j<grid_p[0].length)
							grid_p[i][j]= rects.indexOf(v);
					}
				}
			}


			if(horDirection)  //horizontal
			{
				for(Rect v : rects)
				{
					Rect vj = null;
					for(int j = (int)v.y;j<=(int)v.y+v.hp;j++) 
					{
						for(int i=(int)(v.x+v.wp+1);i<grid.length;i++) 
						{
							if(grid_p[i][(int)v.y]!=-1) //y a un rectangle dans le grid
							{
								vj = rects.get(grid_p[i][(int)v.y]); 
								final Expression expr = retVal.addExpression("Comp"+rects.indexOf(v));
								expr.set(zi[rects.indexOf(v)],-1);
								expr.set(zi[rects.indexOf(vj)],1);
								expr.lower(gamma*v.wp);
								break;
							}
						}
						variance.set(zi[rects.indexOf(v)], zi[rects.indexOf(v)], v.neighbours.size());
						variance.set(zi[rects.indexOf(v)], new Double(v.wp*v.neighbours.size()));


						for (Rect vjj : v.neighbours)
						{
							variance.set(zi[rects.indexOf(vjj)], zi[rects.indexOf(v)],-1);
							variance.set(zi[rects.indexOf(v)], new Double(-v.wp*.5));
							variance.set(zi[rects.indexOf(v)], new Double(-vjj.wp*.5));
						}

					}		
				}
			}
			else //verticalement
			{

				for(Rect v : rects)
				{
					Rect vj = null;
					for(int j = (int)v.x;j<=(int)v.x+v.wp;j++) 
					{
						for(int i=(int)(v.y+v.hp+1);i<grid_p.length;i++) 
						{
							if(grid_p[(int) v.x][i]!=-1) //y a un rectangle dans la grid
							{
								vj = rects.get(grid_p[(int) v.x][i]); 
								final Expression expr = retVal.addExpression("Comp"+rects.indexOf(v));
								expr.set(zi[rects.indexOf(v)],-1);
								expr.set(zi[rects.indexOf(vj)],1);
								expr.lower(gamma*v.hp);
								break;
							}
						}	
						variance.set(zi[rects.indexOf(v)], zi[rects.indexOf(v)], v.neighbours.size());
						variance.set(zi[rects.indexOf(v)], new Double(v.wp*v.neighbours.size()));


						for (Rect vjj : v.neighbours)
						{
							variance.set(zi[rects.indexOf(vjj)], zi[rects.indexOf(v)],-1);
							variance.set(zi[rects.indexOf(v)], new Double(-v.wp*.5));
							variance.set(zi[rects.indexOf(v)], new Double(-vjj.wp*.5));
						}


					}	
				}
			}
		}

		retVal.minimise();
		for(Rect v : rects)
		{
			if (horDirection)
			{
				v.x = zi[rects.indexOf(v)].getValue().intValue();
			}
			else
			{
				v.y = zi[rects.indexOf(v)].getValue().intValue();
			}
		}
	}




	public static int neighboursMedianX(Rect v)
	{
		int[] voisinX=new int[v.neighbours.size()];
		int i=0;
		for(Rect voisin : v.neighbours)
		{
			voisinX[i]= (int)voisin.x;
			i++;
		}
		if(voisinX.length==0) return (int)v.x;
		int median = select(voisinX,0,i,(i+1)/2);
		return median;
	}

	public static int neighboursMedianY(Rect v)
	{
		int[] voisinY=new int[v.neighbours.size()];
		int i=0;
		for(Rect voisin : v.neighbours)
		{
			voisinY[i]= (int)voisin.y;
			i++;
		}
		if(voisinY.length==0) return (int)v.y;
		int median = select(voisinY,0,i,(i+1)/2);
		return median;
	}


	public static int[] findSpace(int x,int y, int[][] grid)
	{ //trouver si autour d'un rectangle il y a des rectangle,si oui, renvoie la position de ce rectangle
		int d = 0;
		while(true)
		{
			for(int i=0;i<=2*d;i++)
			{
				if (x-d+i>=0 && x-d+i<grid.length && y-d>=0 && y-d<grid[0].length && grid[x-d+i][y-d]==-1)
					return new int[] {x-d+i,y-d};
				if (x+d>=0&&x+d<grid.length&& y-d+i>=0&& y-d+i<grid[0].length&&grid[x+d][y-d+i]==-1)
					return new int[] {x+d,y-d+i};
				if (x+d-i>=0&&x+d-i<grid.length&& y-d>=0&& y-d<grid[0].length&&grid[x+d-i][y-d]==-1)
					return new int[] {x+d-i,y-d};
				if (x-d>=0&&x-d<grid.length&& y+d-i>=0&& y+d-i<grid[0].length&&grid[x-d][y+d-i]==-1)
					return new int[] {x-d,y+d-i};

			}


			d++;
		} 
	}


	public static Graph mainAlgo(ArrayList<Rect> rects, double delta)
	{
		double c = calculC(rects, delta);
		calculWpHp(rects, delta, c);

		boolean compactionDir; //direction in which compaction is be performed, true for horizontal direction false for vertical
		int cgrille = 5*(int)Math.ceil(Math.sqrt(rects.size()));
		int[][] grid = new int[cgrille][cgrille];
		for(int i=0; i<cgrille; i++)
			for(int j=0; j<cgrille; j++)
				grid[i][j] = -1;

		for(Rect p : rects)
		{
			do
			{
				p.x = Math.floor(Math.random()*cgrille);
				p.y = Math.floor(Math.random()*cgrille);
			}while(p.x-p.wp<0 || p.y-p.hp<0 || grid[(int)(p.x-p.wp)][(int)(p.y-p.hp)]!=-1); //si c'est vrai, il y deja un rectangle, on cherche une nouvelle position


			grid[(int)(p.x-p.wp)][(int)(p.y-p.hp)] = rects.indexOf(p);  //une fois on mets le rectangle dedans, le grid est true
		}
		compactionDir=true;
		int iterationCount=90*(int)Math.ceil(Math.sqrt(rects.size()))+1;
		double T = (2*Math.sqrt(rects.size()));
		double k = Math.pow((0.2/T), (1/iterationCount));



		for(int i=0; i<=(iterationCount-1)/2;i++) {

			for(Rect v : rects) 
			{
				int x= (int)(neighboursMedianX(v)+(Math.random()*(2*T))-T); 
				int y=(int)( neighboursMedianY(v)+(Math.random()*(2*T))-T); 
				int[] pos = findSpace(x,y,grid);
				if (pos[0]==v.x && pos[1]==v.y)
				{
					double x2 =  x-v.x;  
					double y2=    y-v.y;
					double m = Math.max(Math.abs(y2), Math.abs(x2));
					double a=x2/m;
					double b=y2/m;
					int a1 =(int)Math.round(a);
					int b1 =(int)Math.round(b);


					if(a1>=0 && b1>=0 && (int)v.x+a1<grid.length && (int)v.y+b1<grid[0].length)
					{
						int tmp = grid[(int)v.x][(int)v.y] ;  //changer la position
						grid[(int)v.x][(int)v.y] = grid[(int)v.x+a1][(int)v.y+b1];
						grid[(int)v.x+a1][(int)v.y+b1] = tmp;
						v.x = v.x+a1;
						v.y = v.y+b1;
					}
				}
				else
				{
					if (v.x<grid.length && v.y<grid[0].length)
						grid[(int)v.x][(int)v.y]=-1;
					v.x=pos[0]; v.y=pos[1];
					grid[(int)v.x][(int)v.y] = rects.indexOf(v);
				}
				//////////////////////	
			}
			if (i%9==0)
			{
				compact(compactionDir,3,false, rects, grid);
				compactionDir=!compactionDir;

				for(int l=0; l<cgrille; l++)
					for(int j=0; j<cgrille; j++)
						grid[l][j] = -1;

				for(Rect p : rects)
				{
					grid[(int)p.x][(int)p.y] = rects.indexOf(p);  //une fois on mets le rectangle dedans, le grid est true
				}
			}
			T=T*k;
		}
		compact(true,3,true, rects, grid);
		compact(false,3,true, rects, grid);

		for(int i=(iterationCount-1)/2+1; i<iterationCount;i++) {

			for(Rect v : rects) 
			{
				int x= (int)(neighboursMedianX(v)+v.wp*(Math.random()*(2*T))-T); 
				int y=(int)( neighboursMedianY(v)+v.hp*(Math.random()*(2*T))-T); 
				int[] pos = findSpace(x,y,grid);
				if (pos[0]==v.x && pos[1]==v.y)
				{
					double x2 =  x-v.x;  
					double y2=    y-v.y;
					double m = Math.max(Math.abs(y2) ,Math.abs(x2) );
					double a=x2/m;
					double b=y2/m;
					int a1 =(int)Math.round(a);
					int b1 =(int)Math.round(b);


					if(a1>=0 && b1>=0 && (int)v.x+a1<grid.length && (int)v.y+b1<grid[0].length)
					{
						int tmp = grid[(int)v.x][(int)v.y] ;  //changer la position
						grid[(int)v.x][(int)v.y] = grid[(int)v.x+a1][(int)v.y+b1];
						grid[(int)v.x+a1][(int)v.y+b1] = tmp;
						v.x = v.x+a1;
						v.y = v.y+b1;
					}
				}
				else
				{
					if (v.x<grid.length && v.y<grid[0].length)
						grid[(int)v.x][(int)v.y]=-1;
					v.x=pos[0]; v.y=pos[1];
					grid[(int)v.x][(int)v.y] = rects.indexOf(v);
				}
				//////////////////////	
			}
			if (i%9==0)
			{
				compact(compactionDir,Math.max(1, 1+4.*(iterationCount-i-30)/iterationCount),false, rects, grid);

				compactionDir=!compactionDir;

				for(int l=0; l<cgrille; l++)
					for(int j=0; j<cgrille; j++)
						grid[l][j] = -1;

				for(Rect p : rects)
				{
					grid[(int)p.x][(int)p.y] = rects.indexOf(p);  //une fois on mets le rectangle dedans, le grid est true
				}
			}
			T=T*k;
		}
		
		compact(true,1,true, rects, grid);
		compact(false,1,true, rects, grid);
		
		return toGraph(rects, delta, c);
	}

	public static Graph toGraph(ArrayList<Rect> rects, double delta, double c)
	{
		Graph graph = new Graph();

		System.out.println("Graph");
		for (Rect v : rects)
			System.out.println(v.id + " : " +v.x + ", " + v.y + " ( "+v.wp+", "+v.hp+" ) <= ["+v.w/c+", "+v.h/c+" ]");

		for (Rect p : rects)
		{
			p.x=p.x*c + p.w/2;
			p.y=p.y*c + p.h/2;

			Point po= new Point(p.x,p.y);
			p.r = new Rectangle(po,new SimpleDoubleProperty(p.w),new SimpleDoubleProperty(p.h));

			graph.add(p.r);
		}

		for (Rect v : rects)
			System.out.println(v.id + " : " +(v.x-v.w/2) + ", " + (v.y-v.h/2));

		for (Convex.Rect p : rects)
		{
			for (Convex.Rect n : p.neighbours)
			{
				if (n.id>p.id)
				{
					// line from p to n
					Point p1 = new Point(0,0);
					p.r.addAnchorPoint(p1, n.r.center());

					Point p2= new Point (0,0);
					n.r.addAnchorPoint(p2,p.r.center());

					Line l1=new Line(p1,p2);
					graph.add(l1);

				}
			}
		}

		return graph;
	}
}