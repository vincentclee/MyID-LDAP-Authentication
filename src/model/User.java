package model;

/**
 * Model class that defines a User
 * @author Will Henry
 * @author Vincent Lee
 * @version 2.0
 * @since April 23, 2014
 */

public class User {
	/** User's first, middle, and last name */
	private String fullName;
	/** user's 810 number */
	private String identifier;
	/** user's major */
	private String major;
	
	/**
	 * Constructor for a user
	 * @param fullName
	 * @param identifier
	 * @param major
	 */
	public User(String fullName, String identifier, String major) {
		this.fullName = fullName;
		this.identifier = identifier;
		this.major = major;
	}

	/** @return the fullName */
	public String getFullName() {return fullName;}

	/** @return the identifier */
	public String getIdentifier() {return identifier;}

	/** @return the major */
	public String getMajor() {return major;}
}
