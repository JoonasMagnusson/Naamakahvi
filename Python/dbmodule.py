import sqlite3



class dbmodule:
	

	def __init__(self,db):
		self.dbname = db 

	def login(self,user):
		con = sqlite3.connect(self.dbname) 
		cur = con.cursor()
		             
		try:
			cur.execute('SELECT COUNT(ud_username) FROM userdata WHERE ud_username=:user', {"user" : user})	
		except  Exception ,e:
			print e

		if cur.fetchone()[0] == 1:
			return True
		else: 
			return False
	
		cur.close()
		
	def register(self,user, given, family):
	
		con = sqlite3.connect(self.dbname)
		cur = con.cursor()
		
		reg = 1
		admin = False
		lang = 'eng'

		cur.execute('INSERT INTO userdata  (ud_username,ud_givenname,ud_familyname,ud_regularportion,ud_language,ud_admin) values (?,?,?,?,?,?)', (user, given, family, reg, lang, admin))
		
		con.commit()
		cur.close()
