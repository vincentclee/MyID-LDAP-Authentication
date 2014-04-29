EITS LDAP Authentication
========================

A set of Java classes that can provide UGA student MyID authentication via UGA's LDAP server


###LDAP Authentication

To use the MyID system for LDAP authentication, you will need these parameters:

Field | Value
----- | -----
Search Base | ou=users,o=uga
Host | eds.uga.edu
Port | 636 (only)
Unique ID | cn or UID

 
####Secure LDAP Access

We only support SSL encrypted LDAP for authentication to the MyID system.

All applications that utilize 'eds.uga.edu' for MyID secure LDAP access must use the GeoTrust Root Certificates as trusted root Certificate Authorities (CAs).

[GeoTrust Root Certificates 1 ‒ 8 can be obtained here](https://www.geotrust.com/resources/root-certificates)

If an application already contains the GeoTrust Root Certificates, no action is required on your part for your applications to continue to function properly.

If an application does not contain the GeoTrust Root Certificates, then you will need to obtain and add the GeoTrust Root Certificates (Certificate Authority Certificates 1 through 8) to the application.

####Distinguished Name Format

To form the full DN (distinguished name) of a MyID, use this format: **cn=MyID,ou=users,o=uga** (where *MyID* is replaced by the user's MyID).

In LDAP, a NULL password or username is considered an anonymous bind attempt (bind is the LDAP word for authentication) and will always succeed. Your application should either filter out NULL password strings or validate the successful bind attempt. To validate a bind attempt, have your application attempt to read the attribute ugaAuthCheck. The attribute should have the value of '**y**' (the letter y without the quotes).

Please [contact EITS](http://wiki.eits.uga.edu/help/index.php/Main_Page) if you need help configuring your application to work with MyID via LDAP.

####To Exclude a User in a Specific OU

If an application is using the User Principal Name (myid@uga.edu) to authorize users, programming changes to that application’s log in page may be needed.


- If users are authenticated against Active Directory (AD) using the format objectName@domain, then application should verify "distinguishedName" in AD to check if user belongs to correct OU (Organizational Unit) i.e. applicant/msmyid.


If an application is using the MyID to authenticate users, minimal or no changes will be needed.

* If users are being authenticated against LDAP using myid, authorization can be controlled using following two methods: <br> 1. Search user in required OU by using following format to connect:
Search user in required OU by using following format to connect: <br> `cn=<myid>, ou=<desired_ou>, DC=msmyid, DC=uga, DC=edu` <br><br> An Applicant will be in the following format: <br> `CN=<my_id>,OU=Applicant,DC=msmyid,DC=uga,DC=edu` <br><br> A MyID will be in the following format: <br> `CN=<my_id>,OU=MyID,DC=msmyid,DC=uga,DC=edu` <br><br> 2. Look for user’s DN and extract OU to check if it is the desired OU.
