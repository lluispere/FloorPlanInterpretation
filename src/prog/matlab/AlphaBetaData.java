package prog.matlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlphaBetaData {

	private Integer[][] matriu;
	private Set<Set<Integer>> alphaSet, betaSet;
	private List<Integer[]> alphaList, betaList;

	public AlphaBetaData()
	{
		alphaSet = new HashSet<Set<Integer>>();
		betaSet = new HashSet<Set<Integer>>();
	}

	public void setMatriu(Integer[][] matriu) {
		this.matriu = new Integer[matriu.length][matriu[0].length];
		this.matriu = matriu;
	}

	public Integer[][] getMatriu() {
		return matriu;
	}

	public void setAlphaSet(Set<Set<Integer>> alphaSet) {
		this.alphaSet = alphaSet;
	}

	public Set<Set<Integer>> getalphaSet() {
		return alphaSet;
	}

	public void setBetaSet(Set<Set<Integer>> betaSet) {
		this.betaSet = betaSet;
	}

	public Set<Set<Integer>> getbetaSet() {
		return betaSet;
	}
	
	public List<Integer[]> getAlphaList() {
		return alphaList;
	}
	
	public List<Integer[]> getBetaList() {
		return betaList;
	}
	
	public void calculateMatriu()
	{
		// create the lists
		alphaList = new ArrayList<Integer[]>();
		betaList = new ArrayList<Integer[]>();
		
		int i = 0;
		int j = 0;
		
		// matriu is betaXalpha+beta
		matriu = new Integer[betaSet.size()][alphaSet.size()+betaSet.size()];
		
		// for every beta check the intersection with the alphas and the betas
		for (Set<Integer> betaS : betaSet)
		{
			// introduce beta in the list with the specified order
			Integer[] hInt = new Integer[betaS.size()];
			int cont = 0;
			for (int h : betaS)
			{
				hInt[cont] = h;
				cont++;
			}
			betaList.add(hInt);
			// calculate the intersection with the alphas
			for (Set<Integer> alphaS : alphaSet)
			{
				// if it is the first iteration save the alphas into the list
				if(i==0)
				{
					Integer[] kInt = new Integer[alphaS.size()];
					int cont2 = 0;
					for (int k : alphaS)
					{
						kInt[cont2] = k;
						cont2++;
					}
					alphaList.add(kInt);
				}
				if(alphaS.containsAll(betaS))
				{
					matriu[i][j] = 1;
				}
				else
				{
					matriu[i][j] = 0;
				}
				j++;
			}
			// calculate the intersection with the betas
			for (Set<Integer> betasS2 : betaSet)
			{
				if(betasS2.containsAll(betaS))
				{
					matriu[i][j] = 1;
				}
				else
				{
					matriu[i][j] = 0;
				}
				j++;
			}
			j=0;
			i++;
		}
	}
}
