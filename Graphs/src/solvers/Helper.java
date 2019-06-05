package solvers;

import graph.*;

public class Helper
{	
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
		public double vect(Vec2 v) {return x*v.y - y*v.x;}
		public double len() {return Math.sqrt(x*x+y*y);}
		public Vec2 normal() {return new Vec2(y, -x);}
		public boolean isnull() {return x==0 && y==0;}
	}
	
	
	public static Vec2 isCrossing (Line l1, Line l2)
	{
		return isCrossing (l1.p1(), l1.p2(), l2.p1(), l2.p2());
	}
	
	public static Vec2 isCrossing (Point p11, Point p12, Point p21, Point p22)
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
	
	public static Vec2 isCrossing (double x11, double y11, double x12, double y12, double x21, double y21, double x22, double y22)
	{
		double dx1 = x12-x11;
		double dx2 = x22-x21;
		double dy1 = y12-y11;
		double dy2 = y22-y21;
		
		// test singularité
		if (dx1==0 && dx2==0 && dy1==0 && dy2==0)
		{
			return (x11==x21 && y11==y21)? new Vec2(1,0) : new Vec2(0,0);
		}
		else if (dx1==0 && dy1==0)
		{
			double a = (x21-x11)*dy2 - (y21-y11)*dx2;
			
			if (a==0)
			{
				double alpha = dx2==0 ? (y11-y21)/dy2 : (x11-x21)/dx2;
				return new Vec2(dx2, dy2).normal().normalize().mult(fnct1(alpha));
			}
			else
				return new Vec2(0,0);
		}
		else if (dx2==0 && dy2==0)
		{
			double b = (x21-x11)*dy1 - (y21-y11)*dx1;
			if (b==0)
			{
				double alpha = dx1==0 ? (y21-y11)/dy1 : (x21-x11)/dx1;
				return new Vec2(dx1, dy1).normal().normalize().mult(fnct1(alpha));
			}
			else
				return new Vec2(0,0);
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
				
				return new Vec2(dx1,dy1).normal().normalize().mult(fnct2(alpha, gamma+alpha));//fnct2(alpha, gamma+alpha);//(alpha<=1 || gamma+alpha<=1) && (gamma+alpha>=0 || alpha>=0);
			}
			else
			{
				return new Vec2(0,0);
			}
		}
		
		double t1 = a/det;
		double t2 = b/det;
		
		return new Vec2(y11-x11, y21-x21).normal().normalize().mult(fnct1(t1,t2));//fnct1(t1,t2);
	}
	
	public static double angle(Line l1, Line l2)
	{
		double	x1 = l1.p2().x() - l1.p1().x(),
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
		// res in ]-PI; PI]
		double res = angle - 2*Math.PI * Math.floor(angle/Math.PI);
		
		return res<-Math.PI? res+2*Math.PI : res>Math.PI? res-2*Math.PI : res;
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
	
	public static boolean crossingRL(Rectangle r, Line l)
	{
		return crossingRL(r.x(), r.y(), r.w(), r.h(), r.theta(), l.p1().x(), l.p1().y(), l.p2().x()-l.p1().x(), l.p2().y()-l.p1().y());
	}
	
	public static boolean isIn (double t, double t1, double t2)
	{
		return t>=t1 && t<=t2;
	}
	
	public static boolean intersect (double t1, double t2, double t1p, double t2p)
	{
		return t2>=t1p && t2p>=t1;
	}
	
	public static boolean crossingRL (double x, double y, double w, double h, double theta, double xl, double yl, double wl, double hl)
	{
		Vec2 p = new Vec2(xl, yl).sub(new Vec2 (x, y));
		Vec2 v = new Vec2(wl, hl);
		Vec2 v1 = new Vec2(w*Math.cos(theta), -w*Math.sin(theta));
		Vec2 v2 = new Vec2(h*Math.sin(theta), h*Math.cos(theta));
		
		if (v.isnull())
			return p.x<v1.x/2+v2.x/2 && p.x>-v1.x/2-v2.x/2 && p.y>-v1.y/2-v2.y/2 && p.y<v1.y/2+v2.y/2;
		
		if (v1.isnull())
			return !isCrossing(x, y, v2.x, v2.y, xl, yl, wl, hl).isnull();
		if (v2.isnull())
			return !isCrossing(x, y, v1.x, v1.y, xl, yl, wl, hl).isnull();
		
		if (v.vect(v1)==0)
		{
			double t2 = p.vect(v) / v2.vect(v);
			if (!isIn(t2,0,1))
				return false;
			
			double l1 = v1.vect(v2);
			double l2 = v2.vect(v);
			
			if (l1>=0)
			{
				if (l2>=0)
					return isIn(p.vect(v2), 0, l2+l1);
				else
					return isIn(p.vect(v2), l2, l1);
			}
			else
			{
				if (l2>=0)
					return isIn(p.vect(v2), l1, l2);
				else
					return isIn(p.vect(v2), l2+l1, 0);
			}
		}
		
		if (v.vect(v2)==0)
		{
			double t2 = p.vect(v) / v1.vect(v);
			if (!isIn(t2,0,1))
				return false;
			
			double l1 = v2.vect(v1);
			double l2 = v1.vect(v);
			
			if (l1>=0)
			{
				if (l2>=0)
					return isIn(p.vect(v1), 0, l2+l1);
				else
					return isIn(p.vect(v1), l2, l1);
			}
			else
			{
				if (l2>=0)
					return isIn(p.vect(v1), l1, l2);
				else
					return isIn(p.vect(v1), l2+l1, 0);
			}
		}
		
		double det = v1.vect(v2) * (-v.vect(v1)*v2.dot(v) + v.vect(v2)*v1.dot(v) - v1.vect(v2)*v.dot(v));
		
		double t = (v1.vect(v2)*v2.dot(v)*p.dot(v1) - v1.vect(v2)*v1.dot(v)*p.vect(v2) + v1.vect(v2)*v1.vect(v2)*p.dot(v)) / det;
		double t1 = (v.vect(v2)*v2.dot(v)*p.vect(v1) - (v.vect(v1)*v2.dot(v) + v1.vect(v2)*v.dot(v))*p.vect(v2) + v1.vect(v2)*v.vect(v2)*p.dot(v)) / det;
		double t2 = ((-v.vect(v2)*v1.dot(v)+v.dot(v)*v1.vect(v2))*p.vect(v1) + v.vect(v1)*v1.dot(v)*p.vect(v2) - v.vect(v1)*v1.vect(v2)*p.dot(v)) / det;
		
		return isIn(t,0,1) && isIn(t1,0,1) && isIn(t2,0,1);
	}
	
	public static boolean crossingCL (double x, double y, double r2, double xl, double yl, double w, double h)
	{
		double delta = r2*(w*w+h*h) - (w*(xl-x)+h*(yl-y))*(w*(xl-x)+h*(yl-y));
		
		if (delta < 0)
			return false;
		
		double x1 = -(w*(xl-x)+h*(yl-y))/(w*w+h*h) - Math.sqrt(delta) / (w*w+h*h);
		double x2 = -(w*(xl-x)+h*(yl-y))/(w*w+h*h) + Math.sqrt(delta) / (w*w+h*h);
		
		return intersect(x1,x2,0,1);
	}
}