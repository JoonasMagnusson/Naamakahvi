import psycopg2
#Should be standard stuff in most python installations
from xml.dom.minidom import parseString


class psqldb:
	
	#Constructor
	#db: 	Database to be used
	#xml: 	XML-file containig SQL queries
	def __init__(self,db,user,xml):
		self.dbname = db
		self.user = user
		self.connect = "dbname=" + db + " user=" + user
		self.xmldb =  xml
		self.parseXML(xml)
		print "DB Initialized."

	def parseXML(self,xmlfile):
		file = open(xmlfile,'r')
		data = file.read()
		file.close()

		#parse the data
		self.pxml = parseString(data)
	
	def dbconnect(self):
		self.con = psycopg2.connect(self.connect) 
		self.cur = self.con.cursor()
		
	def dbclose(self):
		self.cur.close()
		

	def getQuery(self,qname):
		dom  =	self.pxml
		for subelem in dom.getElementsByTagName('entry'):
			if (subelem.getAttribute("key")== qname):
				return subelem.firstChild.nodeValue


	#Checks if the user has been created in the database (has registered).
	def login(self,user):
		
		q = self.getQuery("isUserRegistered")

		try:
			self.cur.execute(q,(user,))		
		except  Exception ,e:
			print e
		
		if self.cur.fetchone()[0] == 'true':
			return True
		else: 
			return False
	
		
	#Creates new user
	def register(self,user, given, family,reg):
			
		q = self.getQuery("insertUserdata")
		print q
		admin = False
		lang = 'eng'
		try:
			self.cur.execute(q, (user, given, family, reg, lang, admin))
		except Exception, exc:
			return exc
		else:
			self.con.commit()
			return True
	
	
	#New importable product
	def insertImportProduct(self,groupid,value,sizeunit,finname,engname,svename):
	
		q = self.getQuery("insertImportProduct")
		reg = 1
		admin = False
		lang = 'eng'
		try:
			self.cur.execute(q, (groupid, value,sizeunit, finname, engname, svename))
		except  Exception ,e:
			return e
		self.con.commit()
		return True


	#
	def insertProductGroup(self,finname, engname, svename):
			
		q = self.getQuery("insertProductGroup")

		try:
			self.cur.execute(q, (finname,engname,svename))
		except  Exception ,e:
			return e
		self.con.commit()
		return True	
	
	def insertExportProduct(self,groupid, value,finname, engname, swename):
			
		q = self.getQuery("insertExportProduct")

		try:
			self.cur.execute(q, (groupid, value,finname, engname, swename))
		except  Exception ,e:
			return e
		self.con.commit()
		return True
	
	def selectFinProductNames(self):
		
		q = self.getQuery("selectEProductNames")		

		print q
			
		try:
			self.cur.execute(q)		
		except  Exception ,e:
			print e
			
		result = self.cur.fetchall()
		return result


	def nukeTable(self,table):
		
		q = "TRUNCATE TABLE " + table +" CASCADE;"
		
		try:
			self.cur.execute(q)
		except  Exception ,e:
			return e
		self.con.commit()
		return True	
		
		
