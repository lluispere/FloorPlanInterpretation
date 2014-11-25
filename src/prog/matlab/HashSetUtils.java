package prog.matlab;

import java.util.HashSet;

public class HashSetUtils<E> {
	
	public HashSet<E> set;
	
	public HashSetUtils()
	{
		set = new HashSet<E>();
	}
	
	public void addAll(E[] array)
	{
		for (E i : array)
		{
			set.add(i);
		}
	}
	
	public void addAll(E i)
	{
		set.add(i);
	}
	
	@Override
	// the string is the id
	public String toString() {
		return set.toString();
	}


	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if (!(object instanceof HashSetUtils<?>)) return false;
		final HashSetUtils<?> hset = (HashSetUtils<?>) object;
		return this.set == hset.set;
	}
	

}
