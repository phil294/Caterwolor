/**
 * .___.				<br>
 * {o,o}				<br>
 * /)__)				<br>
 * -"-"--				<br>
 * 2015_01
 * phil
 * eochls@web.de
 */
public class Option {

	public String	section;
	public String	key;
	public Object	defaul;
	// String name is saved in the TreeMap<String - value

	public Object	value;

	public Option(String sec, String ke, Object def) {
		this.section = sec;
		this.key = ke;
		this.defaul = def;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.defaul == null) ? 0 : this.defaul.hashCode());
		result = (prime * result) + ((this.key == null) ? 0 : this.key.hashCode());
		result = (prime * result) + ((this.section == null) ? 0 : this.section.hashCode());
		result = (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Option other = (Option) obj;
		if (this.defaul == null) {
			if (other.defaul != null)
				return false;
		} else if (!this.defaul.equals(other.defaul))
			return false;
		if (this.key == null) {
			if (other.key != null)
				return false;
		} else if (!this.key.equals(other.key))
			return false;
		if (this.section == null) {
			if (other.section != null)
				return false;
		} else if (!this.section.equals(other.section))
			return false;
		if (this.value == null) {
			if (other.value != null)
				return false;
		} else if (!this.value.equals(other.value))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Option [section=" + this.section + ", key=" + this.key + ", defaul=" + this.defaul + ", value=" + this.value + "]";
	}
}
