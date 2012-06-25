import sqlite3

class dbmodule:

	def login(user):
		con = sqlite3.connect('test.db') 
		cur = con.cursor()
             
		cur.execute('SELECT COUNT(ud_username) FROM userdata WHERE ud_username=:user', {"user" : user})	


		if cur.fetchone() == 1:
			return True
		else: 
			return False
	
		cur.close()
		
	def register(user, given, family)
	
		con = sqlite3.connect('test.db')
		cur = con.cursor()
		
		reg = 1
		admin = False
		lang = 'eng'
		
		cur.execute('INSERT INTO userdata VALUES (ud_username,ud_givenname,ud_familyname,ud_regularportion,ud_language,ud_admin) values (?,?,?,?,?,?)', user, given, family, reg, lang, admin)
		
		con.commit()
		cur.close()