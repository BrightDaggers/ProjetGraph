package solvers;

public abstract class Constraint
{
	public abstract double calc_grads();
	public abstract void apply_grads(double coef);
}
