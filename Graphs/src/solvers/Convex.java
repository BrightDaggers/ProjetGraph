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

public class Convex extends Solver {

	public static class Rect
	{
		public double x, y;
		public double w, h;
		public int xp, yp, wp, hp;
		public Rectangle r;
		public ArrayList<Rect> neighbours;
		public int id;

		public Rect(double width, double height)
		{
			w = width;
			h = height;
			x = 0;
			y = 0;
			xp = 0;
			yp = 0;
			wp = 0;
			yp = 0;
			id = 0;
			r = null;
			neighbours = new ArrayList<>();
		}
	}
	
	
	private ArrayList<Rect> rects;
	private double delta;
	
	public Convex (ArrayList<Rect> rs, double margin)
	{
		rects = rs;
		delta = margin;
	}
	

	public double calculC ()
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

	public void compact(boolean horDirection, double gamma, boolean expand, int[][] grid)
	{ //The parameter expand is true if boxes need to be expanded to it¡¯s real sizes, otherwise it is false
		// direction is changed after every compaction

		final Variable[] zi = new Variable[rects.size()];
		for(int i=0;i<rects.size();i++)
		{
			zi[i] = Variable.make("Z"+Integer.toString(i)).lower(0).weight(1);
			
			if (expand)
			{
				if (horDirection)
					zi[i].upper(grid.length-1-rects.get(i).wp);
				else
					zi[i].upper(grid.length-1-rects.get(i).hp);
			}
			else
			{
				zi[i].upper(grid.length-1);
			}
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
					for(int i=v.xp+1;i<grid.length;i++)
					{
						if(grid[i][v.yp]!=-1) //y a un rectangle dans le grid
						{
							vj = rects.get(grid[i][v.yp]); 
							final Expression expr = retVal.addExpression("Comp"+v.id);
							expr.set(zi[v.id],-1);
							expr.set(zi[vj.id],1);
							expr.lower(gamma);
							break;
						}
					}
					variance.set(zi[v.id], zi[v.id], v.neighbours.size());
					variance.set(zi[v.id], v.neighbours.size());
					
					for (Rect vjj : v.neighbours)
					{
						variance.set(zi[vjj.id], zi[v.id],-1);
						variance.set(zi[vjj.id],-0.5);
						variance.set(zi[vjj.id],-0.5);
					}
				}
			}
			else //verticalement
			{
				for(Rect v : rects)
				{
					Rect vj = null;
					for(int i=v.yp+1;i<grid.length;i++)
					{
						if(grid[v.xp][i]!=-1) //y a un rectangle dans le grid
						{
							vj = rects.get(grid[v.xp][i]); 
							final Expression expr = retVal.addExpression("Comp"+v.id);
							expr.set(zi[v.id],-1);
							expr.set(zi[vj.id],1);
							expr.lower(gamma);
							break;
						}
					}
					variance.set(zi[v.id], zi[v.id], v.neighbours.size());
					variance.set(zi[v.id], v.neighbours.size());


					for (Rect vjj : v.neighbours)
					{
						variance.set(zi[vjj.id], zi[v.id],-1);
						variance.set(zi[vjj.id],-0.5);
						variance.set(zi[v.id],-0.5);
					}
				}
			}
		}
		else  //hi et wi != 1
		{
			class Ivec2
			{
				public int x=-1, y=0;
				public Ivec2() {}
			}

			if(horDirection)  //horizontal
			{
				boolean vs[] = new boolean[rects.size()];
				for(Rect v : rects)
				{
					Ivec2 projs[] = new Ivec2[v.hp];
					
					for (int i=0; i<v.hp; i++)
						projs[i] = new Ivec2();
					
					for (int i=0;i<rects.size(); i++)
						vs[i] = false;
					
					for (Rect vj : rects)
					{
						if (!vj.equals(v) && (vj.xp-v.xp>0 || (vj.xp-v.xp==0 && vj.id>v.id)))
						{
							for (int i=Integer.max(v.yp, vj.yp); i<Integer.min(v.yp+v.hp, vj.yp+vj.hp); i++)
							{
								if (projs[i-v.yp].x == -1 || projs[i-v.yp].y > vj.xp-v.xp)
								{
									projs[i-v.yp].x = vj.id;
									projs[i-v.yp].y = vj.xp-v.xp;
								}
							}
						}
					}
					
					for (int i=0; i<projs.length; i++)
					{
						if (projs[i].x!=-1 && !vs[projs[i].x])
						{
							Rect vj = rects.get(projs[i].x);
							final Expression expr = retVal.addExpression("Comp"+v.id+";"+vj.id);
							expr.set(zi[v.id],-1);
							expr.set(zi[vj.id],1);
							expr.lower(gamma*v.wp);
						}
					}
					
					
					variance.set(zi[v.id], zi[v.id], v.neighbours.size());
					variance.set(zi[v.id], v.wp*v.neighbours.size());

					for (Rect vj : v.neighbours)
					{
						variance.set(zi[vj.id], zi[v.id],-1);
						variance.set(zi[vj.id], -v.wp*.5);
						variance.set(zi[v.id], -vj.wp*.5);
					}
				}
			}
			else //verticalement
			{
				boolean vs[] = new boolean[rects.size()];
				for(Rect v : rects)
				{
					Ivec2 projs[] = new Ivec2[v.wp];

					for (int i=0; i<v.wp; i++)
						projs[i] = new Ivec2();
					
					for (int i=0;i<rects.size(); i++)
						vs[i] = false;
					
					for (Rect vj : rects)
					{
						if (!vj.equals(v) && (vj.yp-v.yp>0 || (vj.yp-v.yp==0 && vj.id>v.id)))
						{
							for (int i=Integer.max(v.xp, vj.xp); i<Integer.min(v.xp+v.wp, vj.xp+vj.wp); i++)
							{
								if (projs[i-v.xp].x == -1 || projs[i-v.xp].y > vj.yp-v.yp)
								{
									projs[i-v.xp].x = vj.id;
									projs[i-v.xp].y = vj.yp-v.yp;
								}
							}
						}
					}
					
					for (int i=0; i<projs.length; i++)
					{
						if (projs[i].x!=-1 && !vs[projs[i].x])
						{
							Rect vj = rects.get(projs[i].x);
							final Expression expr = retVal.addExpression("Comp"+v.id+";"+vj.id);
							expr.set(zi[v.id],-1);
							expr.set(zi[vj.id],1);
							expr.lower(gamma*v.hp);
						}
					}
					
					
					variance.set(zi[v.id], zi[v.id], v.neighbours.size());
					variance.set(zi[v.id], v.hp*v.neighbours.size());

					for (Rect vj : v.neighbours)
					{
						variance.set(zi[vj.id], zi[v.id],-1);
						variance.set(zi[vj.id], -v.hp*.5);
						variance.set(zi[v.id], -vj.hp*.5);
					}
				}
			}
		}

		retVal.minimise();
		for(Rect v : rects)
		{
			if (horDirection)
				v.xp = zi[v.id].getValue().intValue();
			else
				v.yp = zi[v.id].getValue().intValue();
		}
	}




	public static int neighboursMedianX(Rect v)
	{
		int[] voisinX = new int[v.neighbours.size()];
		int i = 0;
		
		for(Rect voisin : v.neighbours)
		{
			voisinX[i]= voisin.xp;
			i++;
		}
		
		
		if(voisinX.length==0) return v.xp;
		
		return select(voisinX, 0, i, (i+1)/2);
	}

	public static int neighboursMedianY(Rect v)
	{
		int[] voisinY = new int[v.neighbours.size()];
		int i=0;
		
		for(Rect voisin : v.neighbours)
		{
			voisinY[i]= voisin.yp;
			i++;
		}
		
		
		if(voisinY.length==0) return v.yp;
		
		return select(voisinY, 0, i, (i+1)/2);
	}


	public static int[] findSpace(int x, int y, int id, int[][] grid)
	{ //trouver si autour d'un rectangle il y a des rectangle,si oui, renvoie la position de ce rectangle
		int d = 0;
		while(true)
		{
			for(int i=0;i<=2*d;i++)
			{
				if (x-d+i>=0 && x-d+i<grid.length && y-d>=0 && y-d<grid[0].length && (grid[x-d+i][y-d]==-1 || grid[x-d+i][y-d]==id))
					return new int[] {x-d+i,y-d};
				
				if (x+d>=0 && x+d<grid.length && y-d+i>=0 && y-d+i<grid[0].length && (grid[x+d][y-d+i]==-1 || grid[x+d][y-d+i]==id))
					return new int[] {x+d,y-d+i};
				
				if (x+d-i>=0 && x+d-i<grid.length && y+d>=0 && y+d<grid[0].length && (grid[x+d-i][y+d]==-1 || grid[x+d-i][y+d]==id))
					return new int[] {x+d-i,y+d};
				
				if (x-d>=0 && x-d<grid.length && y+d-i>=0 && y+d-i<grid[0].length && (grid[x-d][y+d-i]==-1 || grid[x-d][y+d-i]==id))
					return new int[] {x-d,y+d-i};

			}

			d++;
		} 
	}


	public Graph mainAlgo()
	{
		for (Rect r : rects)
			r.id = rects.indexOf(r);
		
		
		double c = calculC();
		for (Rect p : rects)
		{
			p.hp = (int)Math.ceil((p.h+delta)/c);
			p.wp = (int)Math.ceil((p.w+delta)/c);
		}

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
				p.xp = (int)Math.floor(Math.random()*cgrille);
				p.yp = (int)Math.floor(Math.random()*cgrille);
			}while(p.xp-p.wp<0 || p.yp-p.hp<0 || grid[p.xp-p.wp][p.yp-p.hp]!=-1); //si c'est vrai, il y deja un rectangle, on cherche une nouvelle position


			grid[p.xp-p.wp][p.yp-p.hp] = p.id;  //une fois on mets le rectangle dedans, le grid est true
		}
		
		
		compactionDir = true;
		int iterationCount = 90*(int)Math.ceil(Math.sqrt(rects.size()))+1;
		double T = (2*Math.sqrt(rects.size()));
		double k = Math.pow((0.2/T), (1/iterationCount));


		for(int i=0; i<=(iterationCount-1)/2;i++) {

			for(Rect v : rects) 
			{
				int x = (int)(neighboursMedianX(v)+(Math.random()*(2*T))-T); 
				int y = (int)(neighboursMedianY(v)+(Math.random()*(2*T))-T);
				
				int[] pos = findSpace(x, y, v.id, grid);
				
				if (pos[0]==v.xp && pos[1]==v.yp)
				{
					double m = Math.max(Math.abs(x-v.xp), Math.abs(y-v.yp));
					int a = (int)Math.round((x-v.xp)/m);
					int b = (int)Math.round((y-v.yp)/m);


					if(a>=0 && b>=0 && v.xp+a<grid.length && v.yp+b<grid[0].length)
					{
						int tmp = grid[v.xp][v.yp] ;  //changer la position
						grid[v.xp][v.yp] = grid[v.xp+a][v.yp+b];
						grid[v.xp+a][v.yp+b] = tmp;
						v.xp += a;
						v.yp += b;
					}
				}
				else
				{
					grid[v.xp][v.yp]=-1;
					v.xp = pos[0];
					v.yp = pos[1];
					grid[v.xp][v.yp] = v.id;
				}
			}
			
			if (i%9==0)
			{
				compact(compactionDir, 3, false, grid);
				compactionDir = !compactionDir;

				for(int l=0; l<cgrille; l++)
					for(int j=0; j<cgrille; j++)
						grid[l][j] = -1;

				for(Rect p : rects)
					grid[p.xp][p.yp] = p.id;
				
			}
			
			T=T*k;
		}
		
		
		compact(true, 3, true, grid);
		compact(false, 3, true, grid);


		for(int i=(iterationCount-1)/2+1; i<iterationCount;i++)
		{
			for(Rect v : rects) 
			{
				int x = (int)(neighboursMedianX(v) + v.wp * (Math.random()*(2*T)-T)); 
				int y = (int)(neighboursMedianY(v) + v.hp * (Math.random()*(2*T)-T));
				int[] pos = findSpace(x, y, v.id, grid);
				
				if (pos[0]==v.xp && pos[1]==v.yp)
				{
					double m = Math.max(Math.abs(x-v.xp), Math.abs(y-v.yp));
					int a = (int)Math.round((x-v.xp)/m);
					int b = (int)Math.round((y-v.yp)/m);


					if(a>=0 && b>=0 && v.xp+a<grid.length && v.yp+b<grid[0].length)
					{
						int tmp = grid[v.xp][v.yp];  //changer la position
						grid[v.xp][v.yp] = grid[v.xp+a][v.yp+b];
						grid[v.xp+a][v.yp+b] = tmp;
						v.xp += a;
						v.yp += b;
					}
				}
				else
				{
					grid[v.xp][v.yp]=-1;
					v.xp=pos[0];
					v.yp=pos[1];
					grid[v.xp][v.yp] = v.id;
				}
				//////////////////////	
			}
			if (i%9==0)
			{
				compact(compactionDir, Math.max(1, 1+4.*(iterationCount-i-30)/iterationCount), false, grid);

				compactionDir=!compactionDir;

				for(int l=0; l<cgrille; l++)
					for(int j=0; j<cgrille; j++)
						grid[l][j] = -1;

				for(Rect p : rects)
					grid[p.xp][p.yp] = p.id;
			}
			T=T*k;
		}
		

		compact(true,1,true, grid);
		compact(false,1,true, grid);
		
		return toGraph(c);
	}

	public Graph toGraph(double c)
	{
		Graph graph = new Graph();
				
		int xmin=Integer.MAX_VALUE, xmax=Integer.MIN_VALUE, ymin=Integer.MAX_VALUE, ymax=Integer.MIN_VALUE;

		for (Rect v : rects)
		{
			if (v.xp<xmin) xmin = v.xp;
			if (v.yp<ymin) ymin = v.yp;
			if (v.xp+v.wp>xmax) xmax = v.xp+v.wp;
			if (v.yp+v.hp>ymax) ymax = v.yp+v.hp;
		}
		
		double xm = (xmax+xmin)/2.;
		double dx = xmax-xm;
		double ym = (ymax+ymin)/2.;
		double dy = ymax-ym;

		for (Rect p : rects)
		{
			p.x = (p.xp-xm)/dx * 300 + p.w/2 + 400;
			p.y = (p.yp-ym)/dy * 200 + p.h/2 + 300;

			Point po = new Point(p.x,p.y);
			p.r = new Rectangle(po,new SimpleDoubleProperty(p.w),new SimpleDoubleProperty(p.h));

			graph.add(p.r);
		}

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
	
	
	@Override
	protected void update(double dt)
	{
		return;
	}
}