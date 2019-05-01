package solvers;

import graph.*;

public class Helper
{
	public static double isCrossing (Line l1, Line l2)
	{
		return isCrossing (l1.p1(), l1.p2(), l2.p1(), l2.p2());
	}
	
	public static double isCrossing (Point p11, Point p12, Point p21, Point p22)
	{
		return isCrossing (p11.x(), p11.y(), p12.x(), p12.y(), p21.x(), p21.y(), p22.x(), p22.y());
	}
	
	private static double fnct1(double x)
	{
		return x<0||x>1? 0 : 4*x*(1-x) + .1;
	}
	
	private static double fnct1(double x, double y)
	{
		return x<0||x>1 || y<0||y>1? 0 : 16*x*(1-x)*y*(1-y) + .1;
	}
	
	private static double fnct2(double x, double y)
	{
		return (x<=1 || y<=1) && (y>=0 || x>=0)? 0 : Math.min(1, Math.max(x,y)) - Math.max(0, Math.min(x,y));
	}
	
	public static double isCrossing (double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22)
	{
		double dx1 = x12-x11;
		double dx2 = x22-x21;
		double dy1 = y12-y11;
		double dy2 = y22-y21;
		
		// test singularité
		if (dx1==0 && dx2==0 && dy1==0 && dy2==0)
		{
			return (x11==x21 && y11==y21)? 1 : 0;
		}
		else if (dx1==0 && dy1==0)
		{
			double a = (x21-x11)*dy2 - (y21-y11)*dx2;
			
			if (a==0)
			{
				double alpha = dx2==0 ? (y11-y21)/dy2 : (x11-x21)/dx2;
				return fnct1(alpha);
			}
			else
				return 0;
		}
		else if (dx2==0 && dy2==0)
		{
			double b = (x21-x11)*dy1 - (y21-y11)*dx1;
			if (b==0)
			{
				double alpha = dx1==0 ? (y21-y11)/dy1 : (x21-x11)/dx1;
				return fnct1(alpha);
			}
			else
				return 0;
		}
		
		
		double det = dx1*dy2 - dx2*dy1;
		double a = (x21-x11)*dy2 - (y21-y11)*dx2;
		double b = (x21-x11)*dy1 - (y21-y11)*dx1;
		
		if (det == 0)
		{
			if (a==0 && b==0)
			{
				double alpha = dx1==0 ? (y21-y11)/dy1 : (x21-x11)/dx1;
				double gamma = dx1==0 ? dy2/dy1 : dx2/dx1;
				
				return fnct2(alpha, gamma+alpha);//(alpha<=1 || gamma+alpha<=1) && (gamma+alpha>=0 || alpha>=0);
			}
			else
			{
				return 0;
			}
		}
		
		double t1 = a/det;
		double t2 = b/det;
		
		return fnct1(t1,t2);
	}
	
	public static double angle(Line l1, Line l2)
	{
		double x1 = l1.p2().x() - l1.p1().x(),
				y1 = l1.p2().y() - l1.p1().y(),
				x2 = l2.p2().x() - l2.p1().x(),
				y2 = l2.p2().y() - l2.p1().y();
		
		return Math.atan2(y2, x2) - Math.atan2(y1, x1);
	}
	
	public static double angle (Point p11, Point p12, Point p21, Point p22)
	{
		double x1 = p12.x() - p11.x(),
				y1 = p12.y() - p11.y(),
				x2 = p22.x() - p21.x(),
				y2 = p22.y() - p21.y();
		
		return Math.atan2(y2, x2) - Math.atan2(y1, x1);
	}
	
	public static double normalizeAngle (double angle)
	{
		double res = angle - 2*Math.PI * Math.floor(angle/Math.PI);
		
		return res<-Math.PI? res+2*Math.PI : res>Math.PI? res-2*Math.PI : res;
	}
	
	
	public static class Vec2
	{
		public double x,y;
		
		public Vec2(double x, double y) {this.x = x; this.y = y;}
		public Vec2(Point p) {x=p.x(); y=p.y();}
		public Vec2 add(Vec2 v) {x+=v.x; y+= v.y; return this;}
		public Vec2 sub(Vec2 v) {x-=v.x; y-= v.y; return this;}
		public Vec2 mult(double a) {x*=a; y*=a; return this;}
		public Vec2 inv() {x=-x; y=-y; return this;}
		public Vec2 normalize() {double l = Math.sqrt(x*x+y*y); x/=l; y/=l; return this;}
		public double dot(Vec2 v) {return x*v.x + y*v.y;}
		public double len() {return Math.sqrt(x*x+y*y);}
		public Vec2 normal() {return new Vec2(-y, x);}
	}
	
	public static double penetration(Vec2 n, Rectangle r, Vec2 pt)
	{
		double dmin = new Vec2(r.p1()).sub(pt).dot(n);
		double d = new Vec2(r.p2()).sub(pt).dot(n);
		if (d<dmin) dmin=d;
		d = new Vec2(r.p3()).sub(pt).dot(n);
		if (d<dmin) dmin=d;
		d = new Vec2(r.p4()).sub(pt).dot(n);
		if (d<dmin) dmin=d;
		
		return dmin;
	}
	
	public static Vec2 interpenetration(Rectangle r1, Rectangle r2)
	{
		Vec2 n1 = new Vec2(r1.p2()).sub(new Vec2(r1.p1())).normal().normalize();
		Vec2 n2 = new Vec2(r1.p4()).sub(new Vec2(r1.p2())).normal().normalize();
		Vec2 n3 = new Vec2(r1.p3()).sub(new Vec2(r1.p4())).normal().normalize();
		Vec2 n4 = new Vec2(r1.p1()).sub(new Vec2(r1.p3())).normal().normalize();
		
		Vec2 dir = new Vec2(r2.center()).sub(new Vec2(r1.center()));

		double dmax = n1.dot(dir);
		Vec2 nmax1 = n1;
		Point pmax1 = r1.p1();
		if (n2.dot(dir)>dmax)
		{ nmax1 = n2; dmax = n2.dot(dir); pmax1 = r1.p2(); }
		if (n3.dot(dir)>dmax)
		{ nmax1 = n3; dmax = n3.dot(dir); pmax1 = r1.p4(); }
		if (n4.dot(dir)>dmax)
		{ nmax1 = n4; dmax = n4.dot(dir); pmax1 = r1.p3(); }
		
		
		n1 = new Vec2(r2.p2()).sub(new Vec2(r2.p1())).normal().normalize();
		n2 = new Vec2(r2.p4()).sub(new Vec2(r2.p2())).normal().normalize();
		n3 = new Vec2(r2.p3()).sub(new Vec2(r2.p4())).normal().normalize();
		n4 = new Vec2(r2.p1()).sub(new Vec2(r2.p3())).normal().normalize();
		
		dir.inv();
		
		dmax = n1.dot(dir);
		Vec2 nmax2 = n1;
		Point pmax2 = r1.p1();
		if (n2.dot(dir)>dmax)
		{ nmax2 = n2; dmax = n2.dot(dir); pmax2 = r1.p2(); }
		if (n3.dot(dir)>dmax)
		{ nmax2 = n3; dmax = n3.dot(dir); pmax2 = r1.p4(); }
		if (n4.dot(dir)>dmax)
		{ nmax2 = n4; dmax = n4.dot(dir); pmax2 = r1.p3(); }
		
		
		if (penetration(nmax1,r2,new Vec2(pmax1)) > penetration(nmax2,r1,new Vec2(pmax2)))
		{
			return nmax1.mult(Math.min(0, penetration(nmax1,r2,new Vec2(pmax1))));
		}
		else
		{
			return nmax2.mult(-Math.min(0, penetration(nmax2,r1,new Vec2(pmax2))));
		}
	}
}
