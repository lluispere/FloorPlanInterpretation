package prog.planegraph;

import java.util.ArrayList;
import java.util.List;


public class PWedge {
	
	private PNode vk, vi, vj;
	private boolean status;
	private int hashCode = 0;
	
	public PWedge(PNode vk, PNode vi, PNode vj)
	{
		this.vk = vk;
		this.vi = vi;
		this.vj = vj;
		this.status = false;
	}
	
	public List<PNode> getWedge()
	{
		List<PNode> vList = new ArrayList<PNode>(3);
		
		vList.add(vk);
		vList.add(vi);
		vList.add(vj);
		
		return vList;
	}
	
	public PNode getPNodeComponent(int i)
	{
		switch(i)
		{
			case 0: return vk;
			case 1: return vi;
			default: return vj;
		}
	}
	
	public boolean getStatus()
	{
		return this.status;
	}
	
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	
	public boolean contigous(PWedge pw)
	{
		if( (pw.getPNodeComponent(0).getId() == vi.getId()) && (pw.getPNodeComponent(1).getId() == vj.getId()) ) 
			return true;
		else return false;
	}
	
	public void printPWedgeConsole()
	{
		System.out.println("( " + vk.getId() + " : " + vi.getId() + " : " + vj.getId() + " )");
	}
	
	public static int genericHash(int... fieldHashes) {
		int result = 17;
		for (int hash : fieldHashes) {
			result = 37 * result + hash;
		}
		return result;
	}
	
	@Override
	public boolean equals(final Object object) {

		if (this == object) return true;
		if (!(object instanceof PWedge)) return false;
		final PWedge point = (PWedge) object;
		return ( ((this.vk.getId() == point.vk.getId()) || (this.vk.getId() == point.vj.getId()))  
				&& (this.vi.getId() == point.vi.getId()) && ((this.vj.getId() == point.vk.getId()) || (this.vj.getId() == point.vj.getId())) );
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = genericHash(Integer.toString(this.vi.getId()).hashCode(),
					Integer.toString(this.vj.getId()+this.vk.getId()).hashCode());
		}
		return this.hashCode;
	}
	
	@Override
	public String toString()
	{
		return this.vk.getId() + ":" + this.vi.getId() + ":" + this.vj.getId();
	}

}
