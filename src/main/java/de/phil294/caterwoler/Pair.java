package de.phil294.caterwoler;


class Pair<T, U> {
	private final T	m_first;
	private final U	m_second;
	private int		flag	= 0;

	public Pair(T first, U second) {
		this.m_first = first;
		this.m_second = second;
	}

	public Pair(T first, U second, int flag) {
		this.m_first = first;
		this.m_second = second;
		this.flag = flag;
	}

	public T first() {
		return this.m_first;
	}

	public U second() {
		return this.m_second;
	}

	public int flag() {
		return this.flag;
	}

}