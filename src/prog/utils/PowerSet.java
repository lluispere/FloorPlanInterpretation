package prog.utils;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This clas is mainly constructed as a generic way to create powersets.
 * Given a generic set of elements, it creates all possible combinations of sets:
 * originalSet = {1,2,3}
 * result:
 * {1}
 * {1},{2}
 * {1},{2},{3}
 * {1},{3}
 * {2},{3}
 * {2}
 * {3}
 * @author lpheras
 *
 */
public class PowerSet
{

	// default constructor
	public PowerSet()
	{

	}
	
	// function for sets
	public static <T> Set<Set<Set<T>>> powerSetSets(Set<Set<T>> actualSet) 
	{
		Set<Set<Set<T>>> finalSet = new HashSet<Set<Set<T>>>();
		if(actualSet.isEmpty())
		{
			finalSet.add(new HashSet<Set<T>>());
			return finalSet;
		}
		
		List<Set<T>> list = new ArrayList<Set<T>>(actualSet);
	    Set<T> head = list.get(0);
	    Set<Set<T>> rest = new HashSet<Set<T>>(list.subList(1, list.size())); 
	    //rest = removeAlreadyContainedIntegers(head, rest);
	    for (Set<Set<T>> set : powerSetSets(rest)) {
	    	Set<Set<T>> newSet = new HashSet<Set<T>>();
	    	newSet.add(head);
	    	newSet.addAll(removeAlreadyContainedIntegers(head, set));
	    	finalSet.add(newSet);
	    	finalSet.add(set);
	    }
	    return finalSet;
	}
	
	public static <T> Set<Set<T>> removeAlreadyContainedIntegers(Set<T> head, Set<Set<T>> rest)
	{
		Set<Set<T>> newSet = new HashSet<Set<T>>();
		for(Set<T> set : rest)
		{	
			boolean flag = false;
			search: for(T t : head)
			{
				if(set.contains(t))
				{
					flag = true;
					break search;
				}
			}
			if (!flag)
				newSet.add(set);
		}
		return newSet;
		
	}
	
	// main function
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) 
	{
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }
	    return sets;
	}
	
	public static <T> Set<Set<T>> powerSet(int n, Set<T> originalSet) 
	{
		Set<Set<T>> set = powerSet(originalSet);
		Set<Set<T>> returnSet = new HashSet<Set<T>>();
		for (Set<T> s : set)
		{
			if (s.size()>=n)
				returnSet.add(s);
		}
		return returnSet;
	}
	
	public static <T> Set<Set<T>> powerExact(int n, Set<T> originalSet) 
	{
		Set<Set<T>> set = powerSet(originalSet);
		Set<Set<T>> returnSet = new HashSet<Set<T>>();
		for (Set<T> s : set)
		{
			if (s.size()==n)
				returnSet.add(s);
		}
		return returnSet;
	}
	
	public static <T> Set<Set<T>> powerSetEq(int n, Set<T> originalSet) 
	{
		Set<Set<T>> set = powerSet(originalSet);
		Set<Set<T>> returnSet = new HashSet<Set<T>>();
		for (Set<T> s : set)
		{
			if (s.size()==n)
				returnSet.add(s);
		}
		return returnSet;
	}
		
		
		
		
		
}