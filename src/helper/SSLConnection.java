package helper;

import com.novell.ldap.*;

import java.security.Security;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.novell.ldap.util.Base64;

import java.util.Enumeration;
import java.util.Iterator;

import model.*;

/**
 * Creates an SSL connection to an LDAP server through which encrypted communication can happen
 * @author Will Henry
 * @author Vincent Lee
 * @version 2.0
 * @since April 23, 2014
 */
public class SSLConnection {
	private static final boolean DEBUG = false;
	/** LDAP Version - we use V3 at UGA */
	private static final int LDAP_VERSION = LDAPConnection.LDAP_V3;
	/** LDAP Host server */
	private static final String LDAP_HOST = "eds.uga.edu";
	/** LDAP Port - 636 (only) */
	private static final int LDAP_PORT = LDAPConnection.DEFAULT_SSL_PORT;
	/**
	 * Path to Java security CA certificates
	 * windows - /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/lib/security/cacerts
	 * linux - /usr/lib/jvm/java/jre/lib/security/cacerts
	 */
	private final Path JAVA_CACERTS = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
	/** LDAP Search Base */
	private static final String LDAP_SEARCH_BASE = "ou=users,o=uga";
	/** LDAP Unique ID - cn or UID */
	private static final String LDAP_UNIQUE_ID = "cn=";
	
	
	/** UGA MyID */
	private String username;
	/** the corresponding password to the MyID */
	private String password;
	/**
	 * Distinguished Name Format
	 * 
	 * To form the full DN (distinguished name) of a MyID, use this format: cn=MyID,ou=users,o=uga
	 * (where MyID is replaced by the user's MyID)
	 */
	private String distinguishedName;
	/** LDAP connection to the SSL server */
	private LDAPConnection conn;
	
	/**
	 * Default Constructor
	 */
	public SSLConnection() {}
	
	/**
	 * Constructor
	 * @param username
	 * @param password
	 */
	public SSLConnection(String username, String password) {
		this.username = username;
		this.password = password;
		this.distinguishedName = generateDistinguishedName(username);
	}
	
	/**
	 * Creates a connection the the LDAP server
	 * @return a connection or null if unsuccessful
	 */
	public LDAPConnection connect() {
		LDAPSocketFactory ssf;
		LDAPConnection lc = null;
		
		try {
			// Dynamically set JSSE as a security provider
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			// Dynamically set the property that JSSE uses to identify
			// the keystore that holds trusted root certificates
			System.setProperty("javax.net.ssl.trustStore", JAVA_CACERTS.toString());
			ssf = new LDAPJSSESecureSocketFactory();
			// Set the socket factory as the default for all future connections
			LDAPConnection.setSocketFactory(ssf);
			// Note: the socket factory can also be passed in as a parameter
			// to the constructor to set it for this connection only.
			lc = new LDAPConnection();
			// connect to the server
			lc.connect(LDAP_HOST, LDAP_PORT);
			// authenticate to the server
			lc.bind( LDAP_VERSION, distinguishedName, password.getBytes("UTF8"));
			return lc;
		}catch(LDAPException e) {
			System.out.println("Error: " + e.toString());
			return lc;
		}catch(UnsupportedEncodingException e) {
			System.out.println("Error: " + e.toString());
			return lc;
		}
	}
	
	public User getUser() {
		int searchScope = LDAPConnection.SCOPE_ONE;
		String searchBase = "ou=users,o=uga";
		String searchFilter = "(cn=" + username + ")";
		String id = "";
		String name = "";
		String major = "";
		
		try {
			LDAPSearchResults searchResults = this.conn.search(searchBase, searchScope, searchFilter,null,false);
			while (searchResults.hasMore()) {
				//Processes an entry of the search result
				LDAPEntry nextEntry = null;
				try {
					nextEntry = searchResults.next();
				} catch(LDAPException e) {
					System.out.println("Error: " + e.toString());
					// Exception is thrown, go for next entry
					if(e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR)
						break;
					else
						continue;
				}
				
				LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
				
				//Get all Attributes
				if (DEBUG) {
					Iterator<?> iterator = attributeSet.iterator();
					while(iterator.hasNext())
						System.out.println(iterator.next());
				}
				
				
				Iterator<?> allAttributes = attributeSet.iterator();
				
				while(allAttributes.hasNext()) {
					//processes an attribute of the current search result
					LDAPAttribute attribute = (LDAPAttribute)allAttributes.next();
					String attributeName = attribute.getName();
					boolean idPresent = false;
					boolean namePresent = false;
					boolean majorPresent = false;
					if(attributeName.equals("ugaIDNumber")){
						idPresent = true;
					} else if(attributeName.equals("fullname")){
						namePresent = true;
					} else if(attributeName.equals("ugaDegreeMajorDesc")){
						majorPresent = true;
					}
					
					Enumeration<?> allValues = attribute.getStringValues();
					if(allValues != null) {
						while(allValues.hasMoreElements()) {
							//processes a value for the current attribute
							String value = (String) allValues.nextElement();
							if (!Base64.isLDIFSafe(value)) {
								value = Base64.encode(value.getBytes());
							}
							if(idPresent) {
								id = value;
							} else if(namePresent) {
								name = value;
							} else if(majorPresent) {
								major = value;
							}
						}//while
					}//if
				}//while
			}//while
			User user = new User(name, id, major);
			conn.disconnect();
			
			return user;
		}catch(LDAPException e) {
			System.out.println("Error: " + e.toString());
			return null;
		}
	}
	
	/**
	 * getter for the connection
	 * @return the connection
	 */
	public LDAPConnection getConn() {
		return conn;
	}
	/**
	 * setter for the connection
	 * @param conn
	 */
	public void setConn(LDAPConnection conn) {
		this.conn = conn;
	}
	
	/**
	 * Distinguished Name Format Generator
	 * @param username
	 * @return String in Distinguished Name Format
	 */
	public String generateDistinguishedName(String username) {
		return LDAP_UNIQUE_ID + username + "," + LDAP_SEARCH_BASE;
	}
}
