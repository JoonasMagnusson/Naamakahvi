import sqlite3

conn = sqlite3.connect('test.db')

cur = conn.cursor()

cur.execute("CREATE TABLE USERDATA (ud_username varchar(8) NOT NULL,ud_givenname varchar(32) NOT NULL,ud_familyname varchar(64)  NOT NULL,ud_regularportion int NOT NULL,ud_language varchar(3) NOT NULL,ud_admin boolean,PRIMARY KEY (ud_username))")

con.commit()
cur.close()