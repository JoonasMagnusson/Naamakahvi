from PyQt4 import uic
from Balance import BalanceWindow


FormClass, BaseClass = uic.loadUiType("authentication.ui")


class AuthenticationWindow(BaseClass, FormClass):
    def __init__(self, parent=None):
        super(BaseClass, self).__init__(parent)
         
        self.setupUi(self)
        self.show()

    def backToStart(self):
        self.close()

    def showBalance(self):
        self.balance = BalanceWindow()
        self.close()