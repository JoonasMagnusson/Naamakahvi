import unittest
import logging
import psqldb
import sys


#Tests for psqldb -class.
#Assumes that test database has several buyable products and raw products with
#IDs of 1 and 2
class Testpsqldb(unittest.TestCase):
    
    
    
    def setUp(self):
        
        self.log = logging.getLogger( "Testpsqldb.TestConnect" )
        self.db = psqldb.psqldb('naamakanta','sam',None,None,None)
        
        self.db.dbconnect()
                
    def TestUserCreation(self):
        
        #Create normal user
        r = self.db.register("demo","Testi","Test")
        self.assertTrue(r,"Registering must succeed with complete user data")
        
        
        #Try to create user with insufficent data
        r = self.db.register("ABBA","","")
        self.assertFalse(r,"Registering must fail with incomplete user data")
        
                
        #Log in the created user
        r = self.db.login("demo")
        self.assertTrue(r,"Login must succeed with created user")
        
        #Try to log in 
        r = self.db.login('NotActuallyUser')
        self.assertFalse(r,"User is deleted")
        
        #Delete user
        r = self.db.deleteUser("demo")
        self.assertTrue(r,"User deletion must succeed")
        
    def TestUserBalance(self):
        
        #
        #Items with id 1 and 2 MUST exist!
        #
        
        
        #Create normal user
        r = self.db.register("demo2","Testi","Test")
        self.assertTrue(r)
        
        #Lets buy some stuff
        
        r = self.db.getUserBalanceById(1,"demo2")
        self.assertTrue(r == [],"Saldo must be empty")
        
        r = self.db.buy("1","1","demo2")
        self.assertTrue(r)
        
        r = self.db.getUserBalanceById(1,"demo2")
        z = r[0][0]
        self.assertEqual(z, -1,"Saldo must be -1")
        
        r = self.db.buy("1","1","demo2")
        self.assertTrue(r)
        
        r = self.db.getUserBalanceById(1,"demo2")
        z = r[0][0]
        self.assertEqual(z, -2,"Saldo must be -2")
        
        #Lets buy some other stuff
        
        r = self.db.getUserBalanceById(2,"demo2")
        self.assertTrue(r == [],"Saldo must be empty")
        
        r = self.db.buy("2","1","demo2")
        self.assertTrue(r)
        
        r = self.db.getUserBalanceById(2,"demo2")
        z = r[0][0]
        self.assertEqual(z, -1,"Saldo must be -1")
        
        r = self.db.buy("2","1","demo2")
        self.assertTrue(r)
        
        r = self.db.getUserBalanceById(2,"demo2")
        z = r[0][0]
        self.assertEqual(z, -2,"Saldo must be -2")
        
        
        
        
        
        #delete balances
        r = self.db.deleteUserBalances("demo2")
        self.assertTrue(r, "Balances must be deleted")
        
        r = self.db.getUserBalanceById(1,"demo2")
        print r
        self.assertTrue(r == [],"Saldo must be empty again")
        
        
        #Delete user
        r = self.db.deleteUser("demo2")
        self.assertTrue(r,"User deletion must succeed")

if __name__ == '__main__':
    logging.basicConfig( stream=sys.stderr )
    logging.getLogger( "Testpsqldb.TestConnect" ).setLevel( logging.DEBUG )
    unittest.main()

