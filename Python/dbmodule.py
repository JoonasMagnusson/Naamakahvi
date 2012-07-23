import sqlite3
#Should be standard stuff in most python installations
from xml.dom.minidom import parseString


class dbmodule:
	
	#Constructor
	#db: 	Database to be used
	#xml: 	XML-file containig SQL queries
	def __init__(self,db,xml):
		self.dbname = db
		self.xmldb =  xml
		self.parseXML(xml)
		print "DB Initialized."

	def parseXML(self,xmlfile):
		file = open(xmlfile,'r')
		data = file.read()
		file.close()

		#parse the data
		self.pxml = parseString(data)

	def getQuery(self,qname):
		dom  =	self.pxml
		for subelem in dom.getElementsByTagName('entry'):
			if (subelem.getAttribute("key")== qname):
				return subelem.firstChild.nodeValue


	#Checks if the user has been created in the database (has registered).
	def login(self,user):
		con = sqlite3.connect(self.dbname) 
		cur = con.cursor()
		
		q = self.getQuery("isUserRegistered")
		print q
		             
		try:
			cur.execute(q, {"user" : user})		
		except  Exception ,e:
			print e
		
		if cur.fetchone()[0] == 'true':
			return True
		else: 
			return False
	
		cur.close()
		
	#Creates new user
	def register(self,user, given, family):
	
		con = sqlite3.connect(self.dbname)
		cur = con.cursor()
		
		q = self.getQuery("insertUserdata")
		print q
		reg = 1
		admin = False
		lang = 'eng'
		try:
			cur.execute(q, (user, given, family, reg, lang, admin))
		except  Exception ,e:
			print e
		con.commit()
		cur.close()
		
	def buyCoffee(self, user, coffee, amount):
		
		con = sqlite3.connect(self.dbname)
		cur = con.cursor()
		
		oldBalance = cur.execute("SELECT ub_balance FROM userbalance WHERE ub_username = ? AND ub_groupid = ?", user, coffee)
		newBalance = oldBalance - amount
		
		q = self.getQuery("updateUserBalances")
		
		try:
			cur.execute(q, (newBalance, coffee, user))
		except  Exception ,e:
			print e
		
		con.commit()
		cur.close()
		
		
	def getProducts(self)
	
		con = sqlite3.connect(self.dbname)
		cur = con.cursor()
		
		
		
		con.commit()
		cur.close()
	
		
