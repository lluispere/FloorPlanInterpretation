package prog.matlab;

public class StructuralElementTriplets {
	
	private int st1, st2, st3;
	
	public StructuralElementTriplets(int st1, int st2, int st3)
	{
		this.st1 = st1;
		this.st2 = st2;
		this.st3 = st3;
	}

	public void setSt1(int st1) {
		this.st1 = st1;
	}

	public int getSt1() {
		return st1;
	}

	public void setSt2(int st2) {
		this.st2 = st2;
	}

	public int getSt2() {
		return st2;
	}

	public void setSt3(int st3) {
		this.st3 = st3;
	}

	public int getSt3() {
		return st3;
	}
	
	@Override
	public String toString()
	{
		return st1 + " " + st2 + " " + " " + st3; 
	}

}
