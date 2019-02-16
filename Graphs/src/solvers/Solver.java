package solvers;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Solver implements Runnable
{
	private AtomicBoolean run = new AtomicBoolean(false);
	
	private long lastTime;
	private int deltaT = 33;
	protected boolean end = false;
	
	@Override
	public void run()
	{
		lastTime = System.currentTimeMillis();
		
		while (!end)
		{
			try
			{
				long t = deltaT + lastTime - System.currentTimeMillis();
				if (t>0)
					Thread.sleep(t);
			} catch (InterruptedException e)
				{ e.printStackTrace(); }
			
			if (run.get())
			{
				update((System.currentTimeMillis() - lastTime) / 1000.);
			}
			
			lastTime = System.currentTimeMillis();
		}
	}
	
	protected abstract void update(double dt);
	
	public void start ()
	{
		run.set(true);
	}
	
	public void stop ()
	{
		run.set(false);
	}
	
	public boolean isRunning ()
	{
		return run.get();
	}
	
	public void setUpdFreq (int milli)
	{
		deltaT = milli;
	}
	
	public void end()
	{
		end = true;
	}
}
